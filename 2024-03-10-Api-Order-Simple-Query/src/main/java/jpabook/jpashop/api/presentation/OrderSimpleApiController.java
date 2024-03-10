package jpabook.jpashop.api.presentation;


import jpabook.jpashop.api.application.dto.OrderSimpleQueryDto;
import jpabook.jpashop.api.domain.Address;
import jpabook.jpashop.api.domain.Order;
import jpabook.jpashop.api.domain.OrderStatus;
import jpabook.jpashop.api.infrastructure.persistence.OrderRepository;
import jpabook.jpashop.api.infrastructure.persistence.OrderSearch;
import jpabook.jpashop.api.infrastructure.persistence.OrderSimpleQueryRepository;
import lombok.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

import static java.util.stream.Collectors.toList;

/**
 * 주문 + 배송 정보 + 회원을 조회하는 API
 * 지연 로딩 때문에 발생하는 성능 문제를 단계적으로 해결해보자
 * 간단한 주문 조회 V1 : 엔티티를 직접 노출
 *
 * 엔티티를 직접 노출하는 것은 좋지 않다.
 * order -> member 와 order -> address 는 지연 로딩이다. 따라서 실제 엔티티 대신에
 * 프록시가 존재한다.
 *
 * jackson 라이브러리는 기본적으로 이 프록시 객체를 json으로 어떻게 생성해야 하는지 모른다. 따라서 예외가 발생한다.
 * Hibernate5Module을 스프링 빈으로 등록하면 해결된다.(스프링 부트 사용중)
 */

/**
 * xToOne(ManyToOne, OneToOne) 관계 최적화
 * Order
 * Order -> Member
 * Order -> Delivery
 */
@RestController
@RequiredArgsConstructor
public class OrderSimpleApiController {

    private final OrderRepository orderRepository;

    private final OrderSimpleQueryRepository orderSimpleQueryRepository;

    /**
     * V1. 엔티티 직접 노출
     * - Hibernate5Module 모듈 등록, LAZY = null 처리
     * - 양방향 관계 문제 발생 -> @JsonIgnore
     */
    @GetMapping("/api/v1/simple-orders")
    public List<Order> ordersV1(){
        List<Order> all = orderRepository.findAll();
        for(Order order: all){
            order.getMember().getName(); // Lazy 강제 초기화
            order.getDelivery().getAddress(); // Lazy 강제 초기화
        }
        return all;
    }


    /**
     * implementation "com.fasterxml.jackson.datatype: jackson-datatype-hibernate5"
     *
     * @Bean
     * Hibernate5Module hibernate5Module(){
     *     return new Hibernate5Module();
     * }
     *
     * hibernate5Module 설정을 통해 강제로 지연 로딩도 사용 가능하다.
     * 알아만두고 실무에서는 DTO로 변환해서 반환하는것이 더 좋은 방법이다.
     */

    /**
     * 지연 로딩을 피하기 위해 즉시 로딩을 설정하면 안된다! 즉시 로딩 때문에
     * 연관관계가 필요 없는 경우에도 데이터를 항상 조회해서 성능 문제가 발생할 수 있다.
     * 즉시 로딩으로 설정하면 성능 튜닝이 매우 어려워 진다.
     * 항상 지연 로딩을 기본으로 하고, 성능 최적화가 필요한 경우에는 페치 조인(fetch join)을 사용해라!
     *
     */

    /**
     * V2. 엔티티를 조회해서 DTO로 변환 (fetch join 사용 X)
     * - 단점: 지연로딩으로 쿼리 N번 호출
     */

    /**
     * 엔티티를 DTO로 변환하는 일반적인 방법이다.
     * 쿼리가 총 1+N+N 번 실행된다 (v1과 쿼리수는 같다.)
     * order 조회 1번 (order 조회 결과 수가 N이 된다.)
     * order -> member 지연 로딩 조회 N번
     * order -> delivery 지연 로딩 조회 N번
     * 예) order의 결과가 4개면 최악의 경우 1 + 4 + 4번 실행된다.
     * 지연로딩은 영속성 컨텍스트에서 조회하므로 , 이미 조회된 경우 쿼리를 생략한다.
     */
    @GetMapping("/api/v2/simple-orders")
    public List<SimpleOrderDto> ordersV2(){

//        List<Order> orders = orderRepository.findAllByString(new OrderSearch());
        List<Order> orders = orderRepository.findAll();
        List<SimpleOrderDto> result = orders.stream()
                .map(o -> new SimpleOrderDto(o))
                .collect(toList());

        return result;

    }


    @Data
    static class SimpleOrderDto{
        private Long orderId;
        private String name;
        private LocalDateTime orderDate;
        private OrderStatus orderStatus;
        private Address address;

        public SimpleOrderDto(Order order){
            orderId = order.getId();
            name = order.getMember().getName(); // LAZY 초기화
            orderDate = order.getOrderDate();
            orderStatus = order.getStatus();
            address = order.getDelivery().getAddress(); // LAZY 초기화
        }
    }

    /**
     * V3. 엔티티를 조회해서 DTO로 변환(fetch join 사용 O)
     * - fetch join 으로 쿼리 1번 호출
     * 참고 : fetch join에 대한 자세한 내용은 JPA 기본편 참고(정말 중요함)
     */
    @GetMapping("/api/v3/simple-orders")
    public List<SimpleOrderDto> ordersV3(){
        List<Order> orders = orderRepository.findAllWithMemberDelivery();
        List<SimpleOrderDto> result = orders.stream()
                .map(o -> new SimpleOrderDto((o)))
                .collect(toList());

        return result;
    }

    /**
     * ==>
     * 엔티티를 페치 조인(fetch join)을 사용해서 쿼리 1번에 조회
     * 페치 조인으로 order -> member, order -> delivery는 이미 조회 된 상태 이므로 지연로딩 X
     */


    /**
     * V4. JPA에서 DTO로 바로 조회
     * - 쿼리 1번 호출
     * - select 절에서 원하는 데이터만 선택해서 조회
     */
    @GetMapping("/api/v4/simple-orders")
    public List<OrderSimpleQueryDto> ordersV4(){
        return orderSimpleQueryRepository.findOrderDtos();
    }


    /**
     * 일반적인 SQL을 사용할때 처럼 원하는 값을 선택해서 조회
     * new 명령어를 사용해서 JPQL의 결과를 DTO로 즉시 변환
     * SELECT 절에서 원하는 데이터를 직접 선택하므로 DB -> 애플리케이션 네트웍
     * 용량 최적화(생각보다 미비함..)
     * 리포지토리 재사용성 떨어짐, API 스펙에 맞춘 코드가 리포지토리에 들어가는 단점이 있다.
     *
     */


    /**
     * 정리
     * 엔티티를 DTO로 변환하거나 , DTO로 바로 조회하는 두가지 방법은 각각 장단점이 있다. 둘중 상황에
     * 따라서 더 나은 방법을 선택하면 된다. 엔티티로 조회하면 리포지토리 재사용성도 좋고, 개발도 단순해진다.
     * 따라서 권장하는 방법은 다음과 같다.
     *
     * 쿼리 방식 선택 권장 순서
     * 1. 우선 엔티티를 DTO로 변환하는 방법을 선택한다.
     * 2. 필요하면 페치 조인으로 성능을 최적화 한다. -> 대부분의 성능 이슈가 해결된다.
     * 3. 그래도 안되면 DTO로 직접 조회하는 방법을 사용한다.
     * 4. 최후의 방법은 JPA가 제공하는 네이티브 SQL이나 스프링 JDBC Template를 사용해서 SQL을 직접 사용한다.
     */



}

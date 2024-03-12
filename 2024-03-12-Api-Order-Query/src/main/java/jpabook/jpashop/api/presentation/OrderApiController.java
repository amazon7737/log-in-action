package jpabook.jpashop.api.presentation;

import jpabook.jpashop.api.domain.Address;
import jpabook.jpashop.api.domain.Order;
import jpabook.jpashop.api.domain.OrderItem;
import jpabook.jpashop.api.domain.OrderStatus;
import jpabook.jpashop.api.infrastructure.persistence.OrderRepository;
import lombok.*;
import org.aspectj.weaver.ast.Or;
import org.springframework.data.domain.jaxb.SpringDataJaxb;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * V1. 엔티티 직접 노출
 * - 엔티티가 변하면 API 스펙이 변한다.
 * - 트랜잭션 안에서 지연 로딩 필요
 * - 양방향 연관관계 문제
 *
 * V2. 엔티티를 조회해서 DTO로 변환 (fetch join 사용 X)
 * - 트랜잭션 안에서 지연 로딩 필요
 *
 * V3. 엔티티를 조회해서 DTO로 변환 (fetch join 사용 O)
 * - 페이징 시에는 N 부분을 포기해야함 (대신에 batch fetch size ?
 * 옵션 주면 N -> 1 쿼리로 변경 가능)
 *
 * V4. JPA에서 DTO로 바로 조회, 컬렉션 N 조회 (1 + N Query)
 * - 페이징 가능
 *
 * V5. JPA에서 DTO로 바로 조회, 컬렉션 1 조회 최적화 버전 (1 + 1 Query)
 * - 페이징 가능
 *
 * V6. JPA에서 DTO로 바로 조회, 플랫 데이터(1Query) (1 Query)
 * - 페이징 불가능...
 *
 */
@RestController
@RequiredArgsConstructor
public class OrderApiController {

    private final OrderRepository orderRepository;

    /**
     * V1. 엔티티 직접 노출
     * - Hibernate5Module 모듈 등록, LAZY = null 처리
     * - 양방향 관계 문제 발생 -> @JsonIgnore
     */
    @GetMapping("/api/v1/orders")
    public List<Order> ordersv1(){
        List<Order> all = orderRepository.findAll();
        for(Order order : all){
            order.getMember().getName(); // Lazy 강제 초기화
            order.getDelivery().getAddress(); // Lazy 강제 초기화
            List<OrderItem> orderItems = order.getOrderItems();
            orderItems.stream().forEach(o -> o.getItem().getName()); // Lazy 강제 초기화

        }
        return all;
    }

    /**
     * =>
     * orderItem , item 관계를 직접 초기화하면 Hibernate5Module 설정에 의해 엔티티를 JSON으로 생성한다.
     * 양방향 연관관계면 무한 루프에 걸리지 않게 한곳에 @JsonIgnore를 추가해야 한다.
     * 엔티티를 직접 노출하므로 좋은 방법은 아니다.
     */


    /**
     * V2. 주문조회 : 엔티티를 DTO로 변환
     */
    @GetMapping("/api/v2/orders")
    public List<OrderDto> ordersV2(){
        List<Order> orders = orderRepository.findAll();
        List<OrderDto> result = orders.stream()
                .map(o -> new OrderDto(o))
                .collect(Collectors.toList());

        return result;
    }


    @Data
    static class OrderDto{
        private Long orderId;
        private String name;
        private LocalDateTime orderDate;
        private OrderStatus orderStatus;
        private Address address;
        private List<OrderItemDto> orderItems;

        public OrderDto(Order order){
            orderId = order.getId();
            name = order.getMember().getName();
            orderDate = order.getOrderDate();
            orderStatus = order.getStatus();
            address = order.getDelivery().getAddress();;
            orderItems = order.getOrderItems().stream()
                    .map(orderItem -> new OrderItemDto(orderItem))
                    .collect(Collectors.toList());
        }

    }

    @Data
    static class OrderItemDto{
        private String itemName;
        private int orderPrice;
        private int count;

        public OrderItemDto(OrderItem orderItem){
            itemName = orderItem.getItem().getName();
            orderPrice = orderItem.getOrderPrice();
            count = orderItem.getCount();
        }

    }

    /**
     * =>
     * 지연 로딩으로 너무 많은 SQL을 실행한다.
     * SQL 실행 수
     * order 1번
     * member , address N번 (order 조회 수 만큼)
     * orderItem N번 (order 조회 수 만큼)
     * item N번 (orderItem 조회 수 만큼)
     * 참고 : 지연 로딩은 영속성 컨텍스트에 있으면 영속성 컨텍스트에 있는 엔티티를 사용하고 없으면 SQL을 실행한다.
     * 따라서 같은 영속성 컨텍스트에서 이미 로딩한 회원 엔티티를 추가로 조회하면 SQL을 실행하지 않는다.
     *
     */

    @GetMapping("/api/v3/orders")
    public List<OrderDto> ordersV3(){

        List<Order> orders = orderRepository.findAllWithItem();
        List<OrderDto> result = orders.stream()
                .map(o -> new OrderDto(o))
                .collect(Collectors.toList());

        return result;
    }

    /**
     * 페치 조인으로 SQL이 1번만 실행됨
     * distinct를 사용한 이유는 1대다 조인이 있으므로 데이터베이스 row가 증가한다. 그 결과 같은
     * order 엔티티의 조회 수도 증가하게 된다. JPA의 distinct는 SQL에 distinct를 추가하고, 더해서 같은
     * 엔티티가 조회되면, 애플리케이션에서 중복을 걸러준다. 이 예에서 order가 컬렛션 페치 조인 때문에 중복 조회 되는
     * 것을 막아준다.
     *
     * 단점
     * - 페이징 불가능
     *
     * 참고 : 컬렉션 페치 조인을 사용하면 페이징이 불가능하다. 하이버네이트는 경고 로그를 남기면서 모든
     * 데이터를 DB에서 읽어오고, 메모리에서 페이징 해버린다.
     * 참고 : 컬렉션 페치 조인은 1개만 사용할 수 있다. 컬렉션 둘 이상에 페치 조인을 사용하면 안된다. 데이터가 부정합하게 조회될 수 있다.
     */

    /**
     * 주문조회 V3.1 : 엔티티를 DTO로 변환 - 페이징과 한계 돌파
     * 페이징과 한계 돌파
     * 컬렉션을 페치 조인하면 페이징이 불가능하다.
     *  컬렉션을 페치 조인하면 일대다 조인이 발생하므로 데이터가 예측할 수 없이 증가한다.
     *  일대다에서 일(1)을 기준으로 페이징을 하는 것이 목적이다. 그런데 데이터는 다(N)를 기준으로 row가 생성된다.
     *  Order를 기준으로 페이징 하고 싶은데, 다(N)인 OrderItem을 조인하면 OrderItem이 기준이 되어버린다.
     *  하이버네이트는 경고 로그를 남기고 모든 DB 데이터를 읽어서 메모리에서 페이징을 시도한다. 최악의 경우 장애 발생
     *  ( 참고 : ORM 표준 JPA 프로그래밍 - 폐치 조인 한계 참조)
     *
     *  한계 돌파
     *  페이징 + 컬렉션 엔티티를 함께 조회하려면 어떻게 해야할까?
     *
     *  ToOne(OneToOne, ManyToOne) 관계를 모두 페치 조인한다. ToOne 관계는 row수를 증가시키지 않으므로 페이징 쿼리에 영향을 주지 않는다.
     *  컬렉션은 지연 로딩으로 조회한다. 그리고 지연 로딩 성능 최적화를 위해 hibernate.default_batch_fetch_size, @BatchSize를 적용한다.
     *  hibernate.default_batch_fetch_size : 글로벌 설정
     * @BatchSize : 개별 최적화
     *  컬렉션이나 프록시 객체를 한꺼번에 설정한 size 만큼 IN 쿼리로 조회한다.
     */

    @GetMapping("/api/v3.1/orders")
    public List<OrderDto> ordersV3_page(@RequestParam(value = "offset" defaultValue = "0") int offset, @RequestParam(value = "limit" , defaultValue = "100") int limit){
        List<Order> orders = orderRepository.findAllWithMemberDelivery(offset, limit);
        List<OrderDto> result = orders.stream()
                .map(o -> new OrderDto(o))
                .collect(Collectors.toList());

        return result;
    }

    /**
     * =>
     * 개별로 설정하려면 @BatchSize 를 적용하면 된다. (컬렉션은 컬렉션 필드로 그리고 엔티티는 엔티티 클래스에 적용한다)
     *
     * 장점
     * 쿼리 호출수가 1+N -> 1+1로 최적화 된다.
     * 조인보다 데이터 전송량이 최적화 된다. (Order 와 OrderItem을 조인하면 Order 가 OrderItem 만큼 중복해서 조회되는데, 각각 조회하므로 전송해야
     * 할 중복데이터가 없다.
     * 페치 조인 방식과 비교해서 쿼리 호출 수가 약간 증가하지만, DB 데이터 전송량이 감소한다.
     * 컬렉션 페치 조인은 페이징이 불가능 하지만 이 방법은 페이징이 가능하다.
     *
     * 결론
     * ToOne 관계는 페치 조인해도 페이징에 영향을 주지 않는다. 따라서 ToOne 관계는 페치조인으로 쿼리 수를 줄이고 해결하고, 나머지는 hibernate.default_batch_fetch_size 로 최적화하면 된다.
     *
     * 참고 : default_batch_fetct_size 크기는 적당한 사이즈를 골라야 하는데, 100 ~ 1000 사이를 선택하는 것을 권장한다.
     * SQL IN 절을 사용하는데, 데이터베이스에 따라서 IN 절 파라미터를 1000으로 제한하기도 한다. 1000으로 잡으면 한번에 1000개를 DB에서 애플리케이션에 불러오므로
     * DB에 순간 부하가 증가할 수 있다. 하지만 100이든 1000이든 결국 전체 데이터를 로딩해야하므로 메모리 사용량은 같다. 1000으로 설정하는 것이 성능상 좋지만
     * 결국 디비든 애플리케이션이든 순간 부하를 어디까지 견딜 수 있는지로 결정하면 된다.
     */






}

package jpabook.jpashop.api.infrastructure.persistence;

import jakarta.persistence.EntityManager;
import jpabook.jpashop.api.application.dto.OrderItemQueryDto;
import jpabook.jpashop.api.application.dto.OrderQueryDto;
import lombok.*;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class OrderQueryRepository {

    private final EntityManager em;

    /**
     * 컬렉션은 별도로 조회
     * Query: 루트 1번, 컬렉션 N 번
     * 단건 조회에서 많이 사용하는 방식
     */
    public List<OrderQueryDto> findOrderQueryDtos(){
        // 루트 조회(toOne 코드를 모두 한번에 조회)
        List<OrderQueryDto> result = findOrders();

        // 루프를 돌면서 컬렉션 추가(추가 쿼리 실행)
        result.forEach(o -> {
            List<OrderItemQueryDto> orderItems = findOrderItems(o.getOrderId());
            o.setOrderItems(orderItems);
        });
        return result;
    }

    /**
     * 1:N 관계(컬렉션)을 제외한 나머지를 한번에 조회
     */
    public List<OrderQueryDto> findOrders(){
        return em.createQuery(
                "select new jpabook.jpashop.api.application.dto.OrderQueryDto(o.id, m.name, o.orderDate, o.status, d.address)" +
                        " from Order o" +
                        " join o.member m" +
                        " join o.delivery d", OrderQueryDto.class)
                .getResultList();

    }

    /**
     * 1:N 관계인 orderItems 조회
     */
    public List<OrderItemQueryDto> findOrderItems(Long orderId){
        return em.createQuery(
                "select new jpabook.jpashop.api.application.dto.OrderItemQueryDto(oi.order.id , i.name , oi.orderPrice, oi.count)" +
                        " from OrderItem oi" +
                        " join oi.item i" +
                        " where oi.order.id = :orderId",
                OrderItemQueryDto.class)
                .setParameter("orderId", orderId)
                .getResultList();
    }
    /**
     *  =>
     *  Query: 루트 1번 , 컬렉션 N번 실행
     *  ToOne(N:1, 1:1) 관계들을 먼저 조회하고, ToMany(1:N) 관계는 각각 별도로 처리한다.
     *    ToOne 관계는 조인해도 데이터 row 수가 증가하지 않는다.
     *    ToMany(1:N) 관계는 조인하면 row 수가 증가한다.
     *  row 수가 증가하지 않는 ToOne 관계는 조인으로 최적화하기 쉬우므로 한번에 조회하고 ToMany 관계는 최적화하기 어려우므로
     *  findOrderItems() 같은 별도의 메서드로 조회한다.
     *
     */


}

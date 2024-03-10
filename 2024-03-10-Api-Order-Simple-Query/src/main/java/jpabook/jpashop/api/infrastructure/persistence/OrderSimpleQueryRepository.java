package jpabook.jpashop.api.infrastructure.persistence;

import jakarta.persistence.EntityManager;
import jpabook.jpashop.api.application.dto.OrderSimpleQueryDto;
import jpabook.jpashop.api.presentation.OrderSimpleApiController;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class OrderSimpleQueryRepository {

    private final EntityManager em;

    public List<OrderSimpleQueryDto> findOrderDtos(){
        return em.createQuery(
                "select new jpabook.jpashop.api.application.dto.OrderSimpleQueryDto(o.id, m.name, o.orderDate, o.status, d.address)"+
                        " from Order o" +
                        " join o.member m"+
                        " join o.delivery d" , OrderSimpleQueryDto.class)
                .getResultList();
    }
}

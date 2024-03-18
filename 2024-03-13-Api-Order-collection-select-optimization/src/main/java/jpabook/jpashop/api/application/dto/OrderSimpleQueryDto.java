package jpabook.jpashop.api.application.dto;

import jpabook.jpashop.api.domain.Address;
import jpabook.jpashop.api.domain.OrderStatus;
import lombok.Data;

import java.time.LocalDateTime;



@Data
public class OrderSimpleQueryDto{
    private Long orderId;
    private String name;
    private LocalDateTime orderDate; // 주문 시간
    private OrderStatus orderStatus;
    private Address address;

    public OrderSimpleQueryDto(Long orderId, String name , LocalDateTime orderDate, OrderStatus orderStatus, Address address){
        this.orderId = orderId;
        this.name = name;
        this.orderDate = orderDate;
        this.orderStatus = orderStatus;
        this.address = address;
    }

}

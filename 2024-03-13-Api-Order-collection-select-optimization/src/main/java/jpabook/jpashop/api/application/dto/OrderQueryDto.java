package jpabook.jpashop.api.application.dto;

import jpabook.jpashop.api.domain.Address;
import jpabook.jpashop.api.domain.OrderItem;
import jpabook.jpashop.api.domain.OrderStatus;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Data
@EqualsAndHashCode(of = "orderId")
public class OrderQueryDto {

    private Long orderId;
    private String name;
    private LocalDateTime orderDate; // 주문시간
    private OrderStatus orderStatus;
    private Address address;
    private List<OrderItemQueryDto> orderItems;

    public OrderQueryDto(Long orderId, String name, LocalDateTime orderDate, OrderStatus orderStatus, Address address){
        this.orderId = orderId;
        this.name = name;
        this.orderDate = orderDate;
        this.orderStatus =orderStatus;
        this.address =address;
    }
}

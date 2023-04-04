package com.teemo.shopping.Order.dto;

import com.teemo.shopping.Order.domain.Order;
import com.teemo.shopping.Order.domain.enums.OrderStatus;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class OrderDTO {
    private Long id;
    private int totalPrice;
    private OrderStatus status;
    public static OrderDTO from(Order order) {
        return OrderDTO.builder()
            .id(order.getId())
            .totalPrice(order.getTotalPrice())
            .status(order.getStatus())
            .build();
    }
}

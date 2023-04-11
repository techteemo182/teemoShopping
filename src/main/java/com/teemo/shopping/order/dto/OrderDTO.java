package com.teemo.shopping.order.dto;

import com.teemo.shopping.order.domain.Order;
import com.teemo.shopping.order.enums.OrderStatus;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class OrderDTO {
    private Long id;
    private int totalPrice;
    private OrderStatus status;
    private Long accountId;
    public static OrderDTO from(Order order) {
        return OrderDTO.builder()
            .id(order.getId())
            .totalPrice(order.getTotalPrice())
            .status(order.getStatus())
            .accountId(order.getAccount().getId())
            .build();
    }
}

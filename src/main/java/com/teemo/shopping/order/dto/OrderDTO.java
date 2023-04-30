package com.teemo.shopping.order.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.teemo.shopping.order.domain.Order;
import com.teemo.shopping.order.enums.OrderStates;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class OrderDTO {
    private Long id;
    private int totalPrice;
    private OrderStates state;
    private Long accountId;
    public static OrderDTO from(Order order) {
        return OrderDTO.builder()
            .id(order.getId())
            .totalPrice(order.getTotalPrice())
            .state(order.getState())
            .accountId(order.getAccount().getId())
            .build();
    }
}

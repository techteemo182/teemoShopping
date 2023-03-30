package com.teemo.shopping.Order.domain.enums;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public enum OrdersGamesStatus {
    MAINTAIN("MAINTAIN"),
    REFUND("REFUND");
    private String status;
}
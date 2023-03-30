package com.teemo.shopping.Order.domain;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public enum OrderStatus {
    CANCEL("CANCEL"),
    PENDING("PENDING"),
    SUCCESS("SUCCESS");
    private String status;
}

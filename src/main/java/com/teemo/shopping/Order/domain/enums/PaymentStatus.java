package com.teemo.shopping.Order.domain.enums;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public enum PaymentStatus {
    CANCEL("CANCEL"),
    PENDING("PENDING"),
    SUCCESS("SUCCESS");
    private String status;
}
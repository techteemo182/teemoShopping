package com.teemo.shopping.order.enums;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public enum PaymentStatus {
    CANCEL("CANCEL"),
    PENDING("PENDING"),
    SUCCESS("SUCCESS"),
    REFUNDED("REFUNDED"),
    PARTIAL_REFUNDED("PARTIAL_REFUNDED");
    protected String status;
}

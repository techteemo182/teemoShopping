package com.teemo.shopping.Order.domain.enums;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public enum PaymentStatus {
    CANCEL("CANCEL"),
    PENDING("PENDING"),
    SUCCESS("SUCCESS");
    protected String status;
}

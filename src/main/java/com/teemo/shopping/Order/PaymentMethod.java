package com.teemo.shopping.Order;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum PaymentMethod {
    CARD("CARD"),
    KAKAOPAY("KAKAOPAY"), // 예시로 하나만
    COUPON("COUPON"),
    DISCOUNT("DISCOUNT");
    private String method;
}

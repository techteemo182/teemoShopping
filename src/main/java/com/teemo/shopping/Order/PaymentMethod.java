package com.teemo.shopping.Order;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum PaymentMethod {
    CARD("CARD"),
    KAKAOPAY("KAKAOPAY"), // 예시로 하나만
    COUPON("COUPON"),  // 쿠폰 할인
    DISCOUNT("DISCOUNT"),   // 상시 할인
    POINT("POINT");
    private String method;
}

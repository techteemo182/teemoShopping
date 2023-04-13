package com.teemo.shopping.order.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

public enum PaymentMethod {
    CARD(Values.CARD),
    KAKAOPAY(Values.KAKAOPAY), // 예시로 하나만
    COUPON(Values.COUPON),  // 쿠폰 할인
    POINT(Values.POINT),
    DISCOUNT(Values.DISCOUNT);
    PaymentMethod(String method) {
        this.method = method;
    }
    @JsonValue
    public String getMethod() {
        return this.method;
    }
    private String method;
    public class Values {
        public static final String CARD = "CARD";
        public static final String KAKAOPAY = "KAKAOPAY";
        public static final String COUPON = "COUPON";
        public static final String POINT = "POINT";
        public static final String DISCOUNT = "DISCOUNT";
    }
}

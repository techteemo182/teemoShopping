package com.teemo.shopping.order.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import com.teemo.shopping.order.domain.CouponPayment;
import com.teemo.shopping.order.domain.DiscountPayment;
import com.teemo.shopping.order.domain.KakaopayPayment;
import com.teemo.shopping.order.domain.Payment;
import com.teemo.shopping.order.domain.PointPayment;

public enum PaymentMethod {
    KAKAOPAY(Values.KAKAOPAY, KakaopayPayment.class),
    COUPON(Values.COUPON, CouponPayment.class),
    POINT(Values.POINT, PointPayment.class),
    DISCOUNT(Values.DISCOUNT, DiscountPayment.class);
    PaymentMethod(String method, Class<? extends Payment> paymentClass) {
        this.method = method;
        this.paymentClass = paymentClass;
    }
    @JsonValue
    public String getMethod() {
        return this.method;
    }

    public Class<? extends Payment> getPaymentClass() {
        return this.paymentClass;
    }
    private String method;
    private Class<? extends Payment> paymentClass;
    public class Values {
        public static final String KAKAOPAY = "KAKAOPAY";
        public static final String COUPON = "COUPON";
        public static final String POINT = "POINT";
        public static final String DISCOUNT = "DISCOUNT";
    }
}

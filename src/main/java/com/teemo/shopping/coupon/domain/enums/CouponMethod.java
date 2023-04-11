package com.teemo.shopping.coupon.domain.enums;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum CouponMethod {
    PERCENT("PERCENT"),
    STATIC("STATIC");
    private String method;
}

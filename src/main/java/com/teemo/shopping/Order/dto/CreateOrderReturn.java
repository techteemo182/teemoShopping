package com.teemo.shopping.Order.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CreateOrderReturn {
    private OrderDTO order;    // 주문
    private int pointPrice;  // 포인트 사용량
    private int discountPrice;    // 할인된 가격
    private int couponPrice;    // 쿠폰 가격
    private int kakaopayPrice; // 카카오페이 가격
    private int totalPrice; // 총 가격

    // 카카오 페이 결제 URL
    private String nextRedirectAppUrl;
    private String nextRedirectMobileUrl;
    private String nextRedirectPcUrl;
}

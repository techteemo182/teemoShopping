package com.teemo.shopping.Order.domain.enums;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public enum OrdersGamesStatus {
    PENDING("PENDING"), // 상태 기다리는 중
    PURCHASE("PURCHASE"), // 구매 상태
    REFUND("REFUND"); // 환불 상태
    private String status;
}

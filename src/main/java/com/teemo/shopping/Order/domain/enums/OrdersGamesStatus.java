package com.teemo.shopping.Order.domain.enums;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public enum OrdersGamesStatus {
    PENDING("PENDING"), // 상태 기다리는 중
    PURCHASE("PURCHASE"), // 게임 구매
    REFUND("REFUND"), // 게임 환불
    CANCEL("CANCEL") // 게임 주문 취소
    private String status;
}

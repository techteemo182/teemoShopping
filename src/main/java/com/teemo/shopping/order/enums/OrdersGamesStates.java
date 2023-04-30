package com.teemo.shopping.order.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum OrdersGamesStates {
    PENDING("PENDING"), // 상태 기다리는 중
    PURCHASE("PURCHASE"), // 게임 구매
    PENDING_REFUND("PENDING_REFUND"),   // 환불 대기중
    REFUND("REFUND"), // 게임 환불
    CANCEL("CANCEL"); // 게임 주문 취소
    @JsonValue
    public String getState() {
        return this.state;
    }
    private String state;
}

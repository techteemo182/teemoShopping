package com.teemo.shopping.payment.domain.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor(access = AccessLevel.PROTECTED)
public enum PaymentStates {
    INIT("INIT"),
    CANCEL("CANCEL"),
    PENDING("PENDING"),
    SUCCESS("SUCCESS"),
    REFUNDED("REFUNDED"),
    PARTIAL_REFUNDED("PARTIAL_REFUNDED"),
    PENDING_REFUND("PENDING_REFUND");
    @JsonValue
    public String getState() {
        return this.state;
    }
    protected String state;
}

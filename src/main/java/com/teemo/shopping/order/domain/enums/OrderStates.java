package com.teemo.shopping.order.domain.enums;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

public enum OrderStates {
    CANCEL,
    PENDING,
    SUCCESS,
    PENDING_REFUND,
    PARTIAL_REFUNDED,
    REFUNDED
}

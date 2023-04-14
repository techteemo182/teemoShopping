package com.teemo.shopping.order.controller;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.teemo.shopping.order.enums.PaymentMethod;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class Pizza {
    @JsonCreator
    public Pizza(List<PaymentMethod> payments) {
        this.payments = payments;
    }

    private final List<PaymentMethod> payments;
}

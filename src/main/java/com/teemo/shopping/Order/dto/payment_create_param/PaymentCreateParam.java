package com.teemo.shopping.Order.dto.payment_create_param;

import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class PaymentCreateParam {
    @NotNull
    private final Integer amount;
    @NotNull
    private final Long orderId;
}

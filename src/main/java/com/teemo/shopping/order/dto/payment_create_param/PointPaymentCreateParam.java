package com.teemo.shopping.order.dto.payment_create_param;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

@Getter
public class PointPaymentCreateParam extends PaymentCreateParam {
    @NotNull
    private final Long accountId;
    @NotNull
    private final Integer availablePoint;

    @Builder
    public PointPaymentCreateParam(@NotNull Integer amount,
        @NotNull Long orderId, @NotNull Long accountId, @NotNull Integer availablePoint) {
        super(amount, orderId);
        this.accountId = accountId;
        this.availablePoint = availablePoint;
    }
}

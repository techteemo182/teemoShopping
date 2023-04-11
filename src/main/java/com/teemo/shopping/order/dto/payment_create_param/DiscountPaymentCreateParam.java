package com.teemo.shopping.order.dto.payment_create_param;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

@Getter
public class DiscountPaymentCreateParam extends PaymentCreateParam {

    @Builder
    public DiscountPaymentCreateParam(@NotNull Integer amount, @NotNull Long orderId, @NotNull Long gameId) {
        super(amount, orderId);
        this.gameId = gameId;
    }

    @NotNull
    private final Long gameId;
}

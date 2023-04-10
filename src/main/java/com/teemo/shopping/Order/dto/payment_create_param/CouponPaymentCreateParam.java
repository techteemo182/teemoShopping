package com.teemo.shopping.Order.dto.payment_create_param;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

@Getter
public class CouponPaymentCreateParam extends PaymentCreateParam{

    @Builder
    public CouponPaymentCreateParam(@NotNull Integer amount,
        @NotNull Long orderId, @NotNull Long gameId, @NotNull Long couponId, @NotNull Long accountId) {
        super(amount, orderId);
        this.gameId = gameId;
        this.couponId = couponId;
        this.accountId = accountId;
    }

    @NotNull
    private final Long gameId;
    @NotNull
    private final Long couponId;

    @NotNull
    private final Long accountId;

}

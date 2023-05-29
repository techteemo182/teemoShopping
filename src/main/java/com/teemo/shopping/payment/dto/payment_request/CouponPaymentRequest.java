package com.teemo.shopping.payment.dto.payment_request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.teemo.shopping.payment.domain.enums.PaymentMethods;
import lombok.Getter;

@Getter
public class CouponPaymentRequest extends PaymentRequest {
    @JsonCreator
    public CouponPaymentRequest(PaymentMethods paymentMethods, Long couponId, Long gameId) {
        super(paymentMethods);
        this.couponId = couponId;
        this.gameId = gameId;
    }

    private final Long couponId;
    private final Long gameId;
}

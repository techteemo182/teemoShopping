package com.teemo.shopping.payment.dto.payment_request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.teemo.shopping.payment.domain.enums.PaymentMethods;
import lombok.Getter;

@Getter
public class DiscountPaymentRequest extends PaymentRequest {
    @JsonCreator
    public DiscountPaymentRequest(PaymentMethods paymentMethods, Long gameId) {
        super(paymentMethods);
        this.gameId = gameId;
    }

    private final Long gameId;
}

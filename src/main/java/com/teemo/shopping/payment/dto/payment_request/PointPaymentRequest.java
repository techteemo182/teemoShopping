package com.teemo.shopping.payment.dto.payment_request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.teemo.shopping.payment.domain.enums.PaymentMethods;
import lombok.Getter;

@Getter
public class PointPaymentRequest extends PaymentRequest {
    @JsonCreator
    public PointPaymentRequest(PaymentMethods paymentMethods, Long amount) {
        super(paymentMethods);
        this.amount = amount;
    }

    private final Long amount;
}

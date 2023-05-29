package com.teemo.shopping.payment.dto.payment_request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.teemo.shopping.payment.domain.enums.PaymentMethods;
import lombok.Getter;

@Getter
public class KakaopayPaymentRequest extends PaymentRequest {
    @JsonCreator
    public KakaopayPaymentRequest(PaymentMethods paymentMethods, Long amount, String nextRedirect) {
        super(paymentMethods);
        this.amount = amount;
        this.nextRedirect = nextRedirect;
    }

    private final Long amount;
    private final String nextRedirect;
}

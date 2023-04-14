package com.teemo.shopping.order.dto.payment;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;
import lombok.experimental.SuperBuilder;
@Getter
@SuperBuilder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class KakaoPaymentDTO extends PaymentDTO {

    public static KakaoPaymentDTO from(KakaoPaymentDTO payment) {
        return KakaoPaymentDTO.builder()
            .id(payment.getId())
            .amount(payment.getAmount())
            .refundableAmount(payment.getRefundableAmount())
            .refundedAmount(payment.getRefundedAmount())
            .method(payment.getMethod())
            .status(payment.getStatus())
            .build();
    }
}


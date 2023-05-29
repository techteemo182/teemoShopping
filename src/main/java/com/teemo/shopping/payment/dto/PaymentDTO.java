package com.teemo.shopping.payment.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.teemo.shopping.payment.domain.Payment;
import com.teemo.shopping.payment.domain.enums.PaymentMethods;
import com.teemo.shopping.payment.domain.enums.PaymentStates;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class PaymentDTO {

    private Long id;
    private Integer amount;
    private Integer refundableAmount;
    private Integer refundedAmount;
    private PaymentStates status;
    private Long orderId;

    public static PaymentDTO from(Payment payment) {
        return PaymentDTO.builder().id(payment.getId()).amount(payment.getAmount())
            .refundableAmount(payment.getRefundableAmount())
            .refundedAmount(payment.getRefundedAmount())
            .status(payment.getState()).build();
    }
}
package com.teemo.shopping.order.dto.payment;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.teemo.shopping.order.domain.Payment;
import com.teemo.shopping.order.enums.PaymentMethods;
import com.teemo.shopping.order.enums.PaymentStates;
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
    private PaymentMethods method;
    private PaymentStates status;
    private Long orderId;

    public static PaymentDTO from(Payment payment) {
        return PaymentDTO.builder().id(payment.getId()).amount(payment.getAmount())
            .refundableAmount(payment.getRefundableAmount())
            .refundedAmount(payment.getRefundedAmount()).method(payment.getMethod())
            .status(payment.getState()).build();
    }
}
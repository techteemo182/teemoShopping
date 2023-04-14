package com.teemo.shopping.order.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.teemo.shopping.order.domain.Payment;
import com.teemo.shopping.order.enums.PaymentMethod;
import com.teemo.shopping.order.enums.PaymentStatus;
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

@Getter
@SuperBuilder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
class PaymentDTO {
    @JsonCreator
    protected PaymentDTO(Long id, int amount, int refundableAmount, int refundedAmount, PaymentMethod method,
        PaymentStatus status, Long orderId) {
        this.id = id;
        this.amount = amount;
        this.refundableAmount = refundableAmount;
        this.refundedAmount = refundedAmount;
        this.method = method;
        this.status = status;
        this.orderId = orderId;
    }
    private final Long id;
    private final int amount;
    private final int refundableAmount;
    private final int refundedAmount;
    private final PaymentMethod method;
    private final PaymentStatus status;
    private final Long orderId;

    public static PaymentDTO from(Payment payment) {
        return PaymentDTO.builder()
            .id(payment.getId())
            .amount(payment.getAmount())
            .refundableAmount(payment.getRefundableAmount())
            .refundedAmount(payment.getRefundedAmount())
            .method(payment.getMethod())
            .status(payment.getStatus())
            .build();
    }
}
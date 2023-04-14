package com.teemo.shopping.order.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.teemo.shopping.order.domain.DiscountPayment;
import com.teemo.shopping.order.domain.PointPayment;
import com.teemo.shopping.order.enums.PaymentMethod;
import com.teemo.shopping.order.enums.PaymentStatus;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class PointPaymentDTO extends PaymentDTO{
    @JsonCreator
    public PointPaymentDTO(Long id, int amount, int refundableAmount, int refundedAmount,
        PaymentMethod method, PaymentStatus status, Long orderId) {
        super(id, amount, refundableAmount, refundedAmount, method, status, orderId);
    }
    public static PointPaymentDTO from(PointPayment payment) {
        return PointPaymentDTO.builder()
            .id(payment.getId())
            .amount(payment.getAmount())
            .refundableAmount(payment.getRefundableAmount())
            .refundedAmount(payment.getRefundedAmount())
            .method(payment.getMethod())
            .status(payment.getStatus())
            .build();
    }
}

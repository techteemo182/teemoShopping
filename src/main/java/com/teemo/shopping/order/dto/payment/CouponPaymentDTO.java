package com.teemo.shopping.order.dto.payment;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.teemo.shopping.order.domain.CouponPayment;
import com.teemo.shopping.order.enums.PaymentMethod;
import com.teemo.shopping.order.enums.PaymentStatus;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class CouponPaymentDTO extends PaymentDTO {
    @JsonCreator
    protected CouponPaymentDTO(Long id, int amount, int refundableAmount, int refundedAmount,
        PaymentMethod method, PaymentStatus status, Long orderId, Long couponId) {
        super(id, amount, refundableAmount, refundedAmount, method, status, orderId);
        this.couponId = couponId;
    }
    private final Long couponId;
    public static CouponPaymentDTO from(CouponPayment payment) {
        return CouponPaymentDTO.builder()
            .id(payment.getId())
            .amount(payment.getAmount())
            .refundableAmount(payment.getRefundableAmount())
            .refundedAmount(payment.getRefundedAmount())
            .method(payment.getMethod())
            .status(payment.getStatus())
            .couponId(payment.getCoupon().getId())
            .build();
    }
}
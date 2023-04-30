package com.teemo.shopping.order.dto.payment;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.teemo.shopping.order.domain.CouponPayment;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class CouponPaymentDTO extends PaymentDTO {

    private Long couponId;

    public static CouponPaymentDTO from(CouponPayment payment) {
        return CouponPaymentDTO.builder().id(payment.getId()).amount(payment.getAmount())
            .refundableAmount(payment.getRefundableAmount())
            .refundedAmount(payment.getRefundedAmount()).method(payment.getMethod())
            .status(payment.getState()).couponId(payment.getCoupon().getId()).build();
    }
}
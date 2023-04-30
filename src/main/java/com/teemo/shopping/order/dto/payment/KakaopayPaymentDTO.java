package com.teemo.shopping.order.dto.payment;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.teemo.shopping.order.domain.KakaopayPayment;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class KakaopayPaymentDTO extends PaymentDTO {

    private String tid;
    private String cid;
    private String redirect;

    public static KakaopayPaymentDTO from(KakaopayPayment payment) {
        return KakaopayPaymentDTO.builder().id(payment.getId()).amount(payment.getAmount())
            .refundableAmount(payment.getRefundableAmount())
            .refundedAmount(payment.getRefundedAmount()).method(payment.getMethod())
            .status(payment.getState()).tid(payment.getTid()).cid(payment.getCid())
            .redirect(payment.getRedirect()).build();
    }
}


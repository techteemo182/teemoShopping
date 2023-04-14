package com.teemo.shopping.order.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.teemo.shopping.order.domain.KakaopayPayment;
import com.teemo.shopping.order.enums.PaymentMethod;
import com.teemo.shopping.order.enums.PaymentStatus;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class KakaopayPaymentDTO extends PaymentDTO {

    private final String tid;
    private final String cid;
    private final String nextRedirectAppUrl;
    private final String nextRedirectMobileUrl;
    private final String nextRedirectPcUrl;
    private final String androidAppScheme;
    private final String iosAppScheme;
    @JsonCreator
    public KakaopayPaymentDTO(Long id, int amount, int refundableAmount, int refundedAmount,
        PaymentMethod method, PaymentStatus status, Long orderId, String tid, String cid,
        String nextRedirectAppUrl, String nextRedirectMobileUrl, String nextRedirectPcUrl,
        String androidAppScheme, String iosAppScheme) {
        super(id, amount, refundableAmount, refundedAmount, method, status, orderId);
        this.tid = tid;
        this.cid = cid;
        this.nextRedirectAppUrl = nextRedirectAppUrl;
        this.nextRedirectMobileUrl = nextRedirectMobileUrl;
        this.nextRedirectPcUrl = nextRedirectPcUrl;
        this.androidAppScheme = androidAppScheme;
        this.iosAppScheme = iosAppScheme;
    }

    public static KakaopayPaymentDTO from(KakaopayPayment payment) {
        return KakaopayPaymentDTO.builder()
            .id(payment.getId())
            .amount(payment.getAmount())
            .refundableAmount(payment.getRefundableAmount())
            .refundedAmount(payment.getRefundedAmount())
            .method(payment.getMethod())
            .status(payment.getStatus())
            .tid(payment.getTid())
            .cid(payment.getCid())
            .nextRedirectPcUrl(payment.getNextRedirectPcUrl())
            .nextRedirectAppUrl(payment.getNextRedirectAppUrl())
            .nextRedirectMobileUrl(payment.getNextRedirectMobileUrl())
            .androidAppScheme(payment.getAndroidAppScheme())
            .iosAppScheme(payment.getIosAppScheme())
            .build();
    }
}


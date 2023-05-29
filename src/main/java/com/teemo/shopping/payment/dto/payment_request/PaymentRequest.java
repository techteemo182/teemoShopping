package com.teemo.shopping.payment.dto.payment_request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.teemo.shopping.payment.domain.enums.PaymentMethods;
import lombok.Getter;

@Getter
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "paymentMethod"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = CouponPaymentRequest.class, name = PaymentMethods.Values.COUPON),
        @JsonSubTypes.Type(value = KakaopayPaymentRequest.class, name = PaymentMethods.Values.KAKAOPAY),
        @JsonSubTypes.Type(value = DiscountPaymentRequest.class, name = PaymentMethods.Values.DISCOUNT),
        @JsonSubTypes.Type(value = PointPaymentRequest.class, name = PaymentMethods.Values.POINT)
})
public class PaymentRequest {
    @JsonCreator
    protected PaymentRequest(PaymentMethods paymentMethods) {
        this.paymentMethod = paymentMethods;
    }

    protected final PaymentMethods paymentMethod;
}


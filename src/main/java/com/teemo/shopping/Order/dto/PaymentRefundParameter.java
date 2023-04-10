package com.teemo.shopping.Order.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PaymentRefundParameter {
    Long paymentId;
    int refundPrice;
}

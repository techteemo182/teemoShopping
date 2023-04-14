package com.teemo.shopping.order.service.parameter;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PaymentRefundParameter {
    Long paymentId;
    int refundPrice;
}

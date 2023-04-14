package com.teemo.shopping.order.service.parameter;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class KakaopayRedirectParameter {
    public enum KakaopayRedirectType {
        SUCCESS,
        FAIL,
        CANCEL
    }
    private KakaopayRedirectType type;
    private String pgToken;
    private String partnerOrderId;
    private String partnerUserId;
}

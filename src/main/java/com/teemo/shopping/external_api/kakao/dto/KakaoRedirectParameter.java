package com.teemo.shopping.external_api.kakao.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;

@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class KakaoRedirectParameter {
    private final String pg_token;
    private final String partner_order_id;
    private final String partner_user_id;

    public String getPgToken() {
        return pg_token;
    }

    public String getPartnerOrderId() {
        return partner_order_id;
    }

    public String getPartnerUserId() {
        return partner_user_id;
    }

}

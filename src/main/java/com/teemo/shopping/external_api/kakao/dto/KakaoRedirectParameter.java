package com.teemo.shopping.external_api.kakao.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;

@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class KakaoRedirectParameter {
    @NotNull
    private final String pg_token;
    @NotNull
    private final Long payment_id;
    @NotNull
    private final String redirect;
    @NotNull
    private final String redirect_secret;

    public String getPgToken() {
        return pg_token;
    }

    public String getRedirectSecret() {
        return redirect_secret;
    }

    public Long getPaymentId() {
        return payment_id;
    }
    public String getRedirect() {
        return redirect;
    }
}

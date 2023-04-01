package com.teemo.shopping.external_api.kakao.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class KakaopayApproveRequest {

    @NotNull
    public String tid;
    @NotNull
    public String partnerOrderId;
    @NotNull
    public String partnerUserId;
    @NotNull
    public String pgToken;
}

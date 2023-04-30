package com.teemo.shopping.external_api.kakao.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class KakaopayApproveParameter {

    @NotNull
    private final String tid;
    @NotNull
    private final String partnerUserId;
    @NotNull
    private final String cid;
    @NotNull
    private final String partnerOrderId;
    @NotNull
    private final String pgToken;
}

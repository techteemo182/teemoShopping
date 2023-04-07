package com.teemo.shopping.external_api.kakao.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class KakaopayCancelParameter {
    @NotNull
    private final String tid;

    @NotNull
    private final Integer price;
}

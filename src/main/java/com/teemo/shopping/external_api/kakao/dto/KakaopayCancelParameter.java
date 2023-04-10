package com.teemo.shopping.external_api.kakao.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import org.hibernate.validator.constraints.Range;

@Getter
@Builder
public class KakaopayCancelParameter {
    @NotNull
    private final String tid;

    @NotNull
    @Range(min = 1)
    private final Integer amount;
}

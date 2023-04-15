package com.teemo.shopping.external_api.kakao.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.hibernate.validator.constraints.Range;

@Getter
@Builder
@AllArgsConstructor
public class KakaopayReadyParameter {
    @NotNull
    private String itemName;
    @NotNull
    private String partnerOrderId;
    @NotNull
    @Range(min = 1)
    private Integer amount;
    @NotNull
    private String redirect;
}

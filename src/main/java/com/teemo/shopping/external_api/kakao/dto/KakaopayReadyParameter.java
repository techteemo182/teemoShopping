package com.teemo.shopping.external_api.kakao.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class KakaopayReadyParameter {
    private String itemName;
    private String partnerOrderId;
    private Integer price;
}

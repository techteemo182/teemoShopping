package com.teemo.shopping.external_api.kakao.dto;

import jakarta.validation.constraints.NotNull;
import java.util.Map;
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
    @Range(min = 1)
    private Integer amount;
    @NotNull
    private Integer quantity;
    @NotNull
    private Map<String, String> additionalQuery;
    @NotNull
    private String cid;
    @NotNull
    private String partnerOrderId;
    @NotNull
    private String partnerUserId;
    @NotNull
    private String approvalURL;
    @NotNull
    private String cancelURL;
    @NotNull
    private String failURL;
}

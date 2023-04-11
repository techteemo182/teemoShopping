package com.teemo.shopping.external_api.kakao.dto;

import com.teemo.shopping.util.ConverterToMultiValueMap;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@AllArgsConstructor
public class KakaopayAPIApproveRequest {
    @NotNull
    public String cid;
    public String cidSecret;
    @NotNull
    public String tid;
    @NotNull
    public String partnerOrderId;
    @NotNull
    public String partnerUserId;
    @NotNull
    public String pgToken;
    public String payload;
    public Integer totalAmount;

    public MultiValueMap<String, String> toFormData() {
        return ConverterToMultiValueMap.convertToFormData(this);
    }
}

package com.teemo.shopping.external_api.kakao.dto;

import com.teemo.shopping.util.ConverterToMultiValueMap;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import org.springframework.util.MultiValueMap;

@Getter
@Builder
public class KakaopayAPICancelRequest {
    @NotNull
    private final String cid;
    private final String cidSecret;
    @NotNull
    private final String tid;
    @NotNull
    private final Integer cancelAmount;
    @NotNull
    private final Integer cancelTaxFreeAmount;
    private final Integer cancelVatAmount;
    private final Integer cancelAvailableAmount;
    private final String payload;
    public MultiValueMap<String, String> toFormData() {
        return ConverterToMultiValueMap.convertToFormData(this);
    }
}

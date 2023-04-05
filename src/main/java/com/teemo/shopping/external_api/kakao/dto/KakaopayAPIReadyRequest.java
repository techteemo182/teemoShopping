package com.teemo.shopping.external_api.kakao.dto;

import com.teemo.shopping.util.ConverterToMultiValueMap;
import com.teemo.shopping.util.CustomConverter;
import jakarta.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.MultiValueMap;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@AllArgsConstructor
public class KakaopayAPIReadyRequest {

    @NotNull
    private String cid;
    private String cidSecret;
    @NotNull
    private String partnerOrderId;
    @NotNull
    private String partnerUserId;
    @NotNull
    private String itemName;
    private String itemCode;
    @NotNull
    private Integer quantity;
    @NotNull
    private Integer totalAmount;
    @NotNull
    private Integer taxFreeAmount;
    private Integer vatAmount;
    private Integer greenDeposit;
    @NotNull
    private String approvalUrl;
    @NotNull
    private String cancelUrl;
    @NotNull
    private String failUrl;
    private List<String> availableCards;
    private String paymentMethodType;
    private Integer installMonth;

    public MultiValueMap<String, String> toFormData() {
        Map<String, CustomConverter> customConveterMap = new HashMap<>();
        customConveterMap.put("availableCards", (obj) -> {
            List<String> strs = (List<String>) obj;
            return "[" + String.join(",", strs) + "]";
        });
        return ConverterToMultiValueMap.convertToFormData(this, customConveterMap);
    }
}

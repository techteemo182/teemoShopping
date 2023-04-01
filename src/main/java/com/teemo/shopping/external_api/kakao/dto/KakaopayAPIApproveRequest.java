package com.teemo.shopping.external_api.kakao.dto;

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
        MultiValueMap<String, String> ret = new LinkedMultiValueMap<>();
        ret.add("cid", cid);
        if(cidSecret != null) {
            ret.add("cid_secret", cidSecret);
        }
        ret.add("tid", tid);
        ret.add("partner_order_id", partnerOrderId);
        ret.add("partner_user_id", partnerUserId);
        if(pgToken != null) {
            ret.add("pg_token", pgToken);
        }
        if(payload != null) {
            ret.add("payload", payload);
        }
        ret.add("total_amount", totalAmount.toString());

        return ret;
    }
}

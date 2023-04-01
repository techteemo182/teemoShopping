package com.teemo.shopping.external_api.kakao.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@JsonDeserialize
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class KakaopayAPIApprovalResponse {
    private String aid;
    private String tid;
    private String cid;
    private String sid;
    private String partnerOrderId;
    private String partnerUserId;
    private String paymentMethodType;
    private KakaopayAPIApprovalAmount amount;
    private KakaopayAPIApprovalCardInfo cardInfo;
    private String itemName;
    private String itemCode;
    private Integer quantity;
    private LocalDateTime createdAt;
    private LocalDateTime approvedAt;
    private String payload;
}


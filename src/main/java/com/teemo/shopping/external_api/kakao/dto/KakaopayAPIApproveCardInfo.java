package com.teemo.shopping.external_api.kakao.dto;

import com.fasterxml.jackson.annotation.JsonRootName;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@JsonRootName(value = "card_info")
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class KakaopayAPIApproveCardInfo {

    private String purchaseCorp;
    private String purchaseCorpCode;
    private String issuerCorp;
    private String issuerCorpCode;
    private String kakaopayPurchaseCorp;
    private String kakaopayPurchaseCorpCode;
    private String kakaopayIssuerCorp;
    private String kakaopayIssuerCorpCode;
    private String bin;
    private String cardType;
    private String installMonth;
    private String approvedId;
    private String cardMid;
    private String interestFreeInstall;
    private String cardItemCode;
}

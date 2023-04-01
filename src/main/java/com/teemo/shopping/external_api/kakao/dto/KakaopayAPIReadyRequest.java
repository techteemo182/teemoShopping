package com.teemo.shopping.external_api.kakao.dto;

import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

/**
 * @implNote 리팩토링 반드시 필요
 */
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
        MultiValueMap<String, String> multiValueMap = new LinkedMultiValueMap();
        multiValueMap.add("cid", cid);
        if (cidSecret != null) {
            multiValueMap.add("cid_secret", cidSecret);
        }
        multiValueMap.add("partner_order_id", partnerOrderId);
        multiValueMap.add("partner_user_id", partnerUserId);
        multiValueMap.add("item_name", itemName);
        if (itemCode != null) {
            multiValueMap.add("item_code", itemCode);
        }
        multiValueMap.add("quantity", quantity.toString());
        multiValueMap.add("total_amount", totalAmount.toString());
        multiValueMap.add("tax_free_amount", taxFreeAmount.toString());
        if (vatAmount != null) {
            multiValueMap.add("vat_amount", vatAmount.toString());
        }
        if (greenDeposit != null) {
            multiValueMap.add("green_deposit", greenDeposit.toString());
        }
        multiValueMap.add("approval_url", approvalUrl);
        multiValueMap.add("cancel_url", cancelUrl);
        multiValueMap.add("fail_url", failUrl);
        /**
         * 예시
         * ["HANA", "BC"]
         */

        if (availableCards != null && !availableCards.isEmpty()) {
            String availableCardsString =
                "[" + String.join(", ", availableCards) + "]";
            multiValueMap.add("available_cards", availableCardsString);
        }

        if (paymentMethodType != null) {
            multiValueMap.add("payment_method_type", paymentMethodType);
        }
        if (installMonth != null) {
            multiValueMap.add("install_month", installMonth.toString());
        }
        return multiValueMap;
    }
}

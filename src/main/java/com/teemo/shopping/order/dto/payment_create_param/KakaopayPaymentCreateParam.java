package com.teemo.shopping.order.dto.payment_create_param;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

@Getter
public class KakaopayPaymentCreateParam extends PaymentCreateParam {

    @Builder
    public KakaopayPaymentCreateParam(@NotNull Integer amount,
        @NotNull Long orderId, @NotNull String itemName) {
        super(amount, orderId);
        this.itemName = itemName;
    }

    @NotNull
    private final String itemName;
}

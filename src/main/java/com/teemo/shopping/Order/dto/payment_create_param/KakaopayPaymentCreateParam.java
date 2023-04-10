package com.teemo.shopping.Order.dto.payment_create_param;

import com.teemo.shopping.Order.dto.OrderCreateContext;
import com.teemo.shopping.game.domain.Game;
import jakarta.validation.constraints.NotNull;
import java.util.List;
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

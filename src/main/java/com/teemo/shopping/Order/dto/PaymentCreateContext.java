package com.teemo.shopping.Order.dto;

import com.teemo.shopping.Order.domain.Order;
import com.teemo.shopping.account.domain.Account;
import com.teemo.shopping.coupon.domain.Coupon;
import com.teemo.shopping.game.domain.Game;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PaymentCreateContext {
    private final Game game;
    private final List<Game> games;
    private final Optional<Coupon> coupon;
    @NotNull
    private final Order order;
    @NotNull
    private final Account account;
    private final int point;
    private int remainPrice;
    @NotNull
    private final CreateOrderReturn.CreateOrderReturnBuilder createOrderReturnBuilder;
    public void setRemainPrice(int remainPrice) {
        this.remainPrice = remainPrice;
    }
}


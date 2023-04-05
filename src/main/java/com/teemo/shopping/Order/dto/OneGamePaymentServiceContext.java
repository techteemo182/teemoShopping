package com.teemo.shopping.Order.dto;

import com.teemo.shopping.Order.domain.Order;
import com.teemo.shopping.account.domain.Account;
import com.teemo.shopping.coupon.domain.Coupon;
import com.teemo.shopping.game.domain.Game;
import jakarta.validation.constraints.NotNull;
import java.util.Optional;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class OneGamePaymentServiceContext {        // context for All PaymentFactory
    @NotNull
    private Game game;
    private Optional<Coupon> coupon;
    @NotNull
    private Account account;
    private int remainPrice;
    @NotNull
    private CreateOrderReturn.CreateOrderReturnBuilder createOrderReturnBuilder;
    @NotNull
    private Order order;
    public void setRemainPrice(int remainPrice) {
        this.remainPrice = remainPrice;
    }
}

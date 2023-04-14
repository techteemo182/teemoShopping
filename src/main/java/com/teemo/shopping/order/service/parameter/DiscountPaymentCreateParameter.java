package com.teemo.shopping.order.service.parameter;

import com.teemo.shopping.account.domain.Account;
import com.teemo.shopping.game.domain.Game;
import com.teemo.shopping.order.domain.Order;
import lombok.Builder;
import lombok.Getter;
import org.springframework.stereotype.Component;

@Getter
public class DiscountPaymentCreateParameter extends PaymentCreateParameter {

    @Builder
    protected DiscountPaymentCreateParameter(Account account, Order order, Integer amount,
        Game game) {
        super(account, order, amount);
        this.game = game;
    }

    private final Game game;
}

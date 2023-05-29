package com.teemo.shopping.payment.service.factory;

import com.teemo.shopping.game.domain.Game;
import com.teemo.shopping.payment.domain.DiscountPayment;
import com.teemo.shopping.payment.domain.Payment;

import javax.naming.Context;

public class DiscountPaymentFactory implements PaymentFactory<OrderContext> {
    @Override
    public Payment create(OrderContext context) {

        Game game = context.getGame().get();
        int amount = calculateAmount(game);
        return DiscountPayment.builder().game(game).amount(amount).build();
    }
    protected int calculateAmount(Game game) {
        int amount = game.getPrice();
        amount -= (int)(amount * game.getDiscountPercent() / 100d);
        return amount;
    }
}

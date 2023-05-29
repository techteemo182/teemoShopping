package com.teemo.shopping.payment.service.factory;

import com.teemo.shopping.game.domain.Game;
import com.teemo.shopping.payment.domain.KakaopayPayment;
import com.teemo.shopping.payment.domain.Payment;

import java.util.List;

public class KakaopayPaymentFactory implements PaymentFactory<OrderContext>{
    @Override
    public Payment create(OrderContext orderContext) {
        int amount = orderContext.getRemainAmount();
        List<Game> games = orderContext.getGames().get();
        String itemName = String.join(",", games.stream().map(game -> game.getName()).toList());
        return KakaopayPayment.builder()
                .amount(amount).itemName(itemName)
                .build();
    }
}

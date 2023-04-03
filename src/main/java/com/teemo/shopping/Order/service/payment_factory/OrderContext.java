package com.teemo.shopping.Order.service.payment_factory;

import com.teemo.shopping.Order.domain.Order;
import com.teemo.shopping.Order.domain.enums.PaymentMethod;
import com.teemo.shopping.account.domain.Account;
import com.teemo.shopping.coupon.domain.Coupon;
import com.teemo.shopping.game.domain.Game;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class OrderContext {

    private List<PaymentMethod> paymentMethods;
    private Order order;
    private List<Game> games;
    private Account account;
    private Map<Game, Optional<Coupon>> gameCouponMap;
    private int point;
}

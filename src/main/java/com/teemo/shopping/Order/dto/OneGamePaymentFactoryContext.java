package com.teemo.shopping.Order.dto;

import com.teemo.shopping.Order.domain.enums.PaymentMethod;
import com.teemo.shopping.account.domain.Account;
import com.teemo.shopping.coupon.domain.Coupon;
import com.teemo.shopping.game.domain.Game;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

@Getter
@Builder
public class OneGamePaymentFactoryContext {        // context for All PaymentFactory
    private Game game;
    private Optional<Coupon> coupon;
    private Account account;
    private int remainPrice;
    public void setRemainPrice(int remainPrice) {
        this.remainPrice = remainPrice;
    }
}


package com.teemo.shopping.Order.service.payment_factory;

import com.teemo.shopping.account.domain.Account;
import com.teemo.shopping.coupon.domain.Coupon;
import com.teemo.shopping.game.domain.Game;
import java.util.Optional;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SingleGameProductContext {

    private Game game;
    private Account account;
    private Optional<Coupon> coupon;
    private int remainPrice;
}

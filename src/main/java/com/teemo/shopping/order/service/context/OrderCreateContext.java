package com.teemo.shopping.order.service.context;

import com.teemo.shopping.order.domain.Order;
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
public class OrderCreateContext {
    @NotNull
    private final Order order;
    @NotNull
    private final Account account;
    private final Game game;
    private final List<Game> games;
    private final Optional<Coupon> coupon;
    private final Integer point;
    private final Integer amount;
    private final String itemName;

}


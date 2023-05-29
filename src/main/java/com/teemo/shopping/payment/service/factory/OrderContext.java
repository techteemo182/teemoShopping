package com.teemo.shopping.payment.service.factory;

import com.teemo.shopping.account.domain.Account;
import com.teemo.shopping.coupon.domain.Coupon;
import com.teemo.shopping.game.domain.Game;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Getter
@Setter
@Builder
public class OrderContext {
    private Optional<Game> game;
    private Optional<Account>account;
    private Optional<List<Game>> games;
    private Optional<Coupon> coupon;
    private Integer remainAmount;
    private Optional<Integer> amount;
}

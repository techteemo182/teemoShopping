package com.teemo.shopping.Order.service.payment_factory;

import com.teemo.shopping.account.domain.Account;
import com.teemo.shopping.game.domain.Game;
import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AllGameProductContext {

    private List<Game> games;
    private Account account;
    private int point;
    private int totalRemainPrice;
}

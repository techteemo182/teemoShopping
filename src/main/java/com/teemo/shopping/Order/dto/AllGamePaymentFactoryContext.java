package com.teemo.shopping.Order.dto;

import com.teemo.shopping.Order.domain.enums.PaymentMethod;
import com.teemo.shopping.account.domain.Account;
import com.teemo.shopping.coupon.domain.Coupon;
import com.teemo.shopping.game.domain.Game;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.swing.text.html.Option;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Builder
public class AllGamePaymentFactoryContext {        // context for All PaymentFactory
    private List<Game> games;
    private Integer point;
    private Account account;
    private int remainPrice;

    public void setRemainPrice(int remainPrice) {
        this.remainPrice = remainPrice;
    }
}


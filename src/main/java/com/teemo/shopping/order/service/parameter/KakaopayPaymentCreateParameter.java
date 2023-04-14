package com.teemo.shopping.order.service.parameter;

import com.teemo.shopping.account.domain.Account;
import com.teemo.shopping.order.domain.Order;
import lombok.Builder;
import lombok.Getter;
import org.springframework.stereotype.Component;

@Getter
public class KakaopayPaymentCreateParameter extends PaymentCreateParameter {

    @Builder
    protected KakaopayPaymentCreateParameter(Account account, Order order, Integer amount,
        String itemName) {
        super(account, order, amount);
        this.itemName = itemName;
    }

    private final String itemName;
}

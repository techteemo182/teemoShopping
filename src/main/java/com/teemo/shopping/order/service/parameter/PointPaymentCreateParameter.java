package com.teemo.shopping.order.service.parameter;

import com.teemo.shopping.account.domain.Account;
import com.teemo.shopping.order.domain.Order;
import lombok.Builder;
import lombok.Getter;

@Getter
public class PointPaymentCreateParameter extends PaymentCreateParameter {

    @Builder
    protected PointPaymentCreateParameter(Account account, Order order, Integer amount,
        Integer availablePoint) {
        super(account, order, amount);
        this.availablePoint = availablePoint;
    }

    private final Integer availablePoint;

}

package com.teemo.shopping.order.service.parameter;

import com.teemo.shopping.account.domain.Account;
import com.teemo.shopping.order.domain.Order;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class PaymentCreateParameter {
    private final Account account;
    private final Order order;
    private final Integer amount;
}

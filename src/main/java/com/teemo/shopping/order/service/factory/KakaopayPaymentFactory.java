package com.teemo.shopping.order.service.factory;

import com.teemo.shopping.order.domain.KakaopayPayment;
import com.teemo.shopping.order.domain.Payment;
import com.teemo.shopping.order.service.KakaopayPaymentService;
import com.teemo.shopping.order.service.context.OrderCreateContext;
import com.teemo.shopping.order.service.parameter.KakaopayPaymentCreateParameter;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class KakaopayPaymentFactory implements PaymentFactory {

    @Autowired
    private KakaopayPaymentService kakaopayPaymentService;

    @Override
    public Optional<Payment> create(OrderCreateContext context) {
        return kakaopayPaymentService.add(
            KakaopayPaymentCreateParameter.builder().account(context.getAccount())
                .order(context.getOrder()).amount(context.getAmount())
                .redirect(context.getRedirect())
                .itemName(context.getItemName()).build());
    }

    @Override
    public Class<? extends Payment> getPaymentClass() {
        return KakaopayPayment.class;
    }
}

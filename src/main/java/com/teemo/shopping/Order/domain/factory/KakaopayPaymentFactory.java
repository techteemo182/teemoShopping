package com.teemo.shopping.Order.domain.factory;

import com.teemo.shopping.Order.domain.KakaopayPayment;
import com.teemo.shopping.Order.domain.Payment;
import com.teemo.shopping.Order.domain.enums.PaymentMethod;
import com.teemo.shopping.Order.domain.enums.PaymentStatus;
import com.teemo.shopping.Order.dto.AllGamePaymentFactoryContext;
import com.teemo.shopping.Order.repository.PaymentRepository;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(200)
public class KakaopayPaymentFactory implements AllGamePaymentFactory {    //전략 패턴

    @Autowired
    private PaymentRepository<KakaopayPayment> KakaopayPaymentRepository;

    public PaymentMethod getTargetPaymentMethod() {
        return PaymentMethod.KAKAOPAY;
    }

    @Override
    public Optional<Payment> create(AllGamePaymentFactoryContext context) {
        int remainPrice = context.getRemainPrice();
        KakaopayPayment kakaopayPayment = KakaopayPayment.builder().status(PaymentStatus.PENDING)
            .price(remainPrice).build();
        KakaopayPaymentRepository.save(kakaopayPayment);
        context.setRemainPrice(0);
        return Optional.of(kakaopayPayment);
    }
}

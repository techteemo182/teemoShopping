package com.teemo.shopping.Order.service.payment_factory;

import com.teemo.shopping.Order.domain.KakaopayPayment;
import com.teemo.shopping.Order.domain.Payment;
import com.teemo.shopping.Order.domain.enums.PaymentMethod;
import com.teemo.shopping.Order.domain.enums.PaymentStatus;
import com.teemo.shopping.Order.repository.PaymentRepository;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@org.springframework.core.annotation.Order(200)
public class KakaopayPaymentFactory implements AllProductPaymentFactory {    //전략 패턴

    @Autowired
    private PaymentRepository<KakaopayPayment> KakaopayPaymentRepository;

    public PaymentMethod getTargetPaymentMethod() {
        return PaymentMethod.KAKAOPAY;
    }

    @Override
    public Optional<Payment> create(AllGameProductContext context) {
        int totalRemainPrice = context.getTotalRemainPrice();
        KakaopayPayment kakaopayPayment = KakaopayPayment.builder().status(PaymentStatus.PENDING)
            .price(totalRemainPrice).build();
        KakaopayPaymentRepository.save(kakaopayPayment);
        context.setTotalRemainPrice(0);
        return Optional.of(kakaopayPayment);
    }
}

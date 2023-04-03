package com.teemo.shopping.Order.service.payment_factory;

import com.teemo.shopping.Order.domain.DiscountPayment;
import com.teemo.shopping.Order.domain.Payment;
import com.teemo.shopping.Order.domain.enums.PaymentMethod;
import com.teemo.shopping.Order.domain.enums.PaymentStatus;
import com.teemo.shopping.Order.repository.PaymentRepository;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@org.springframework.core.annotation.Order(100)
public class DiscountPaymentFactory implements SingleProductPaymentFactory {    //전략 패턴

    public PaymentMethod getTargetPaymentMethod() {
        return PaymentMethod.DISCOUNT;
    }

    @Autowired
    private PaymentRepository<DiscountPayment> discountPaymentRepository;

    @Override
    public Optional<Payment> create(SingleGameProductContext context) {
        int discountPrice = (int) (
            context.getRemainPrice() * (1 - context.getGame().getDiscount()));
        DiscountPayment discountPayment = DiscountPayment.builder().game(context.getGame())
            .price(discountPrice).status(PaymentStatus.SUCCESS).build();
        discountPaymentRepository.save(discountPayment);
        context.setRemainPrice(context.getRemainPrice() - discountPrice);
        return Optional.of(discountPayment);
    }
}

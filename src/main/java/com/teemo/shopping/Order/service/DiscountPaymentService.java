package com.teemo.shopping.Order.service;

import com.teemo.shopping.Order.domain.DiscountPayment;
import com.teemo.shopping.Order.domain.Payment;
import com.teemo.shopping.Order.domain.enums.PaymentMethod;
import com.teemo.shopping.Order.domain.enums.PaymentStatus;
import com.teemo.shopping.Order.dto.OneGamePaymentServiceContext;
import com.teemo.shopping.Order.repository.PaymentRepository;
import com.teemo.shopping.game.domain.Game;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

@Service
@Order(100)
public class DiscountPaymentService extends OneGamePaymentService {    //전략 패턴

    public PaymentMethod getTargetPaymentMethod() {
        return PaymentMethod.DISCOUNT;
    }

    @Autowired
    private PaymentRepository<DiscountPayment> discountPaymentRepository;

    @Override
    public Optional<Payment> create(OneGamePaymentServiceContext context) {
        Game game = context.getGame();
        com.teemo.shopping.Order.domain.Order order = context.getOrder();
        int discountPrice = (int) (
            context.getRemainPrice() * (1 - game.getDiscount()));
        DiscountPayment discountPayment = DiscountPayment.builder().game(game)
            .price(discountPrice).status(PaymentStatus.SUCCESS).build();
        discountPaymentRepository.save(discountPayment);
        context.setRemainPrice(context.getRemainPrice() - discountPrice);
        return Optional.of(discountPayment);
    }
}
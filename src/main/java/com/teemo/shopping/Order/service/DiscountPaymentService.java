package com.teemo.shopping.Order.service;

import com.teemo.shopping.Order.domain.CouponPayment;
import com.teemo.shopping.Order.domain.DiscountPayment;
import com.teemo.shopping.Order.domain.Order;
import com.teemo.shopping.Order.domain.Payment;
import com.teemo.shopping.Order.domain.enums.PaymentMethod;
import com.teemo.shopping.Order.domain.enums.PaymentStatus;
import com.teemo.shopping.Order.dto.PaymentCreateContext;
import com.teemo.shopping.Order.dto.PaymentRefundParameter;
import com.teemo.shopping.Order.repository.PaymentRepository;
import com.teemo.shopping.game.domain.Game;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
@Qualifier("forOneProduct")
@org.springframework.core.annotation.Order(100)
public class DiscountPaymentService extends PaymentService {    //전략 패턴


    @Autowired
    private PaymentRepository<DiscountPayment> discountPaymentRepository;

    @Override
    public Optional<Payment> create(PaymentCreateContext context) {
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

    @Override
    public void refund(PaymentRefundParameter parameter) {    // 부분 취소 불가능
        DiscountPayment payment = discountPaymentRepository.findById(parameter.getPaymentId())
            .get();
        if (payment.getStatus() != PaymentStatus.SUCCESS) {
            throw new RuntimeException();
        }
        payment.updateRefundedPoint(payment.getPrice());
        payment.updateStatus(PaymentStatus.REFUNDED);
    }

    @Override
    public PaymentMethod getPaymentMethod() {
        return PaymentMethod.DISCOUNT;
    }
}

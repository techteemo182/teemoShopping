package com.teemo.shopping.order.service;

import com.teemo.shopping.game.domain.Game;
import com.teemo.shopping.order.domain.DiscountPayment;
import com.teemo.shopping.order.domain.Order;
import com.teemo.shopping.order.domain.Payment;
import com.teemo.shopping.order.enums.PaymentStatus;
import com.teemo.shopping.order.repository.PaymentRepository;
import com.teemo.shopping.order.service.parameter.DiscountPaymentCreateParameter;
import com.teemo.shopping.order.service.parameter.PaymentRefundParameter;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DiscountPaymentService extends PaymentService<DiscountPaymentCreateParameter> {    //전략 패턴


    @Autowired
    private PaymentRepository<DiscountPayment> discountPaymentRepository;

    @Override
    public Optional<Payment> create(DiscountPaymentCreateParameter parameter) {
        Game game = parameter.getGame();
        Order order = parameter.getOrder();
        double discountPercent = game.getDiscountPercent();
        double discountDecimalPercent = discountPercent / 100d;
        int amount = parameter.getAmount();
        int discountAmount = (int) (amount * discountDecimalPercent);
        DiscountPayment discountPayment = DiscountPayment.builder().game(game).order(order)
            .amount(discountAmount).status(PaymentStatus.SUCCESS).build();
        discountPayment = discountPaymentRepository.save(discountPayment);
        return Optional.of(discountPayment);
    }

    @Override
    public void refund(PaymentRefundParameter parameter) {    // 부분 취소 불가능
        DiscountPayment payment = discountPaymentRepository.findById(parameter.getPaymentId())
            .get();
        if (payment.getStatus() != PaymentStatus.SUCCESS) {
            throw new RuntimeException();
        }
        payment.updateRefundedPoint(payment.getAmount());
        payment.updateStatus(PaymentStatus.REFUNDED);
    }

    @Override
    public Class<DiscountPayment> getPaymentClass() {
        return DiscountPayment.class;
    }
}

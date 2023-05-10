package com.teemo.shopping.order.service.payment;

import com.teemo.shopping.game.domain.Game;
import com.teemo.shopping.order.domain.DiscountPayment;
import com.teemo.shopping.order.domain.Payment;
import com.teemo.shopping.order.enums.PaymentStates;
import com.teemo.shopping.order.repository.PaymentRepository;
import com.teemo.shopping.order.service.context.OrderCreateContext;
import com.teemo.shopping.order.service.observer.PaymentStateUpdatePublisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DiscountPaymentService extends PaymentService {    //전략 패턴


    @Autowired
    private PaymentRepository<DiscountPayment> discountPaymentRepository;
    @Autowired
    private PaymentStateUpdatePublisher paymentStateUpdatePublisher;

    @Override
    public Payment create(OrderCreateContext context) {
        Game game = context.getGame().get();
        double discountPercent = game.getDiscountPercent();
        if (discountPercent == 0) {
            throw new IllegalStateException("할인 가능 금액이 없습니다.");
        }
        int amount = (int) (context.getAmount() * game.getDiscountPercent() / 100d);
        DiscountPayment discountPayment = DiscountPayment.builder().game(game).amount(amount)
            .build();
        return discountPayment;
    }

    @Override
    public void refund(Long paymentId, int amount) {    // 부분 취소 불가능
        DiscountPayment payment = discountPaymentRepository.findById(paymentId).get();
        if (payment.getState().equals(PaymentStates.SUCCESS)) {
            throw new IllegalStateException("환불 가능한 상태가 아님.");
        }
        payment.setRefundedAmount(payment.getAmount());
        payment.setState(PaymentStates.REFUNDED);
        paymentStateUpdatePublisher.publish(payment.getPaymentStateUpdateSubscriberTypes(), payment.getId());
    }

    @Override
    public void pay(Long paymentId) {
        DiscountPayment payment = discountPaymentRepository.findById(paymentId).get();
        if (!payment.getState().equals(PaymentStates.PENDING)) {
            throw new IllegalStateException("결제 가능한 상태가 아님.");
        }
        payment.setState(PaymentStates.SUCCESS);
        paymentStateUpdatePublisher.publish(payment.getPaymentStateUpdateSubscriberTypes(), payment.getId());
    }

    @Override
    public Class<DiscountPayment> getPaymentClass() {
        return DiscountPayment.class;
    }
}

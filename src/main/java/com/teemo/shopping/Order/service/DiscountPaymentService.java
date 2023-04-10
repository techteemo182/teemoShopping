package com.teemo.shopping.Order.service;

import com.teemo.shopping.Order.domain.DiscountPayment;
import com.teemo.shopping.Order.domain.Order;
import com.teemo.shopping.Order.domain.enums.PaymentStatus;
import com.teemo.shopping.Order.dto.PaymentRefundParameter;
import com.teemo.shopping.Order.dto.payment_create_param.DiscountPaymentCreateParam;
import com.teemo.shopping.Order.repository.OrderRepository;
import com.teemo.shopping.Order.repository.PaymentRepository;
import com.teemo.shopping.account.repository.AccountRepository;
import com.teemo.shopping.game.domain.Game;
import com.teemo.shopping.game.repository.GameRepository;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DiscountPaymentService extends GameProductPaymentService<DiscountPaymentCreateParam> {    //전략 패턴


    @Autowired
    private PaymentRepository<DiscountPayment> discountPaymentRepository;

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private AccountRepository accountRepository;

    @Override
    public Optional<Long> create(DiscountPaymentCreateParam param) {
        Game game = gameRepository.findById(param.getGameId()).get();
        Order order = orderRepository.findById(param.getOrderId()).get();
        double discountPercent = game.getDiscountPercent();
        double discountDecimalPercent = discountPercent / 100d;
        int amount = param.getAmount();
        int discountAmount = (int) (amount * discountDecimalPercent);
        DiscountPayment discountPayment = DiscountPayment.builder().game(game).order(order)
            .amount(discountAmount).status(PaymentStatus.SUCCESS).build();
        discountPaymentRepository.save(discountPayment);
        return Optional.of(discountPayment.getId());
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
    public Class<DiscountPayment> getTargetPaymentClass() {
        return DiscountPayment.class;
    }
}

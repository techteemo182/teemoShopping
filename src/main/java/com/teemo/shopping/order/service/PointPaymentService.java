package com.teemo.shopping.order.service;

import com.teemo.shopping.account.domain.Account;
import com.teemo.shopping.account.repository.AccountRepository;
import com.teemo.shopping.order.domain.Order;
import com.teemo.shopping.order.domain.Payment;
import com.teemo.shopping.order.domain.PointPayment;
import com.teemo.shopping.order.enums.PaymentMethod;
import com.teemo.shopping.order.enums.PaymentStatus;
import com.teemo.shopping.order.repository.OrderRepository;
import com.teemo.shopping.order.repository.PaymentRepository;
import com.teemo.shopping.order.service.parameter.PaymentRefundParameter;
import com.teemo.shopping.order.service.parameter.PointPaymentCreateParameter;
import jakarta.transaction.Transactional;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PointPaymentService extends
    PaymentService<PointPaymentCreateParameter> {

    @Autowired
    private PaymentRepository<PointPayment> pointPaymentRepository;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private AccountRepository accountRepository;

    public PaymentMethod getPaymentMethod() {
        return PaymentMethod.POINT;
    }

    @Override
    @Transactional
    public Optional<Payment> add(PointPaymentCreateParameter parameter) {
        int amount = parameter.getAmount();
        Order order = parameter.getOrder();
        Account account = parameter.getAccount();
        int availablePoint = parameter.getAvailablePoint();
        if (account.getPoint() < availablePoint) {
            throw new IllegalStateException("계정의 포인트가 사용 할 포인트 보다 적음");
        }
        int toUsePoint = Math.min(availablePoint, amount);
        PointPayment payment = PointPayment.builder().status(PaymentStatus.SUCCESS)
            .amount(toUsePoint).order(order).build();
        payment = pointPaymentRepository.save(payment);

        account.updatePoint(account.getPoint() - toUsePoint);
        return Optional.of(payment);
    }

    @Override
    @Transactional
    public void refund(PaymentRefundParameter parameter) {    // 부분 취소 가능
        PointPayment payment = pointPaymentRepository.findById(parameter.getPaymentId()).get();
        int refundPrice = parameter.getRefundPrice();
        if(payment.getRefundableAmount() < refundPrice) { // 환불 가능한 금액이 환불할 금액보다 큼
            throw new RuntimeException();
        }
        payment.updateRefundedPoint(payment.getRefundedAmount() + refundPrice);
        payment.updateStatus(payment.getAmount() == payment.getRefundedAmount() ? PaymentStatus.REFUNDED : PaymentStatus.PARTIAL_REFUNDED);   // 환불 상태 결정
    }
    @Override
    public Class<PointPayment> getPaymentClass() {
        return PointPayment.class;
    }
}

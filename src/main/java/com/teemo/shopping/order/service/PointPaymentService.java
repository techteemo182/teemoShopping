package com.teemo.shopping.order.service;

import com.teemo.shopping.order.domain.Order;
import com.teemo.shopping.order.domain.PointPayment;
import com.teemo.shopping.order.enums.PaymentMethod;
import com.teemo.shopping.order.enums.PaymentStatus;
import com.teemo.shopping.order.dto.payment_create_param.PointPaymentCreateParam;
import com.teemo.shopping.order.repository.OrderRepository;
import com.teemo.shopping.order.repository.PaymentRepository;
import com.teemo.shopping.account.domain.Account;
import com.teemo.shopping.account.repository.AccountRepository;
import jakarta.transaction.Transactional;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PointPaymentService extends AllProductPaymentService<PointPaymentCreateParam> {

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
    public Optional<Long> create(PointPaymentCreateParam param) {
        int amount = param.getAmount();
        Order order = orderRepository.findById(param.getOrderId()).get();
        Account account = accountRepository.findById(param.getAccountId()).get();
        int availablePoint = param.getAvailablePoint();
        if (account.getPoint() < availablePoint) {
            throw new IllegalStateException("계정의 포인트가 사용 할 포인트 보다 적음");
        }
        int toUsePoint = Math.min(availablePoint, amount);
        PointPayment payment = PointPayment.builder().status(PaymentStatus.SUCCESS)
            .amount(toUsePoint).order(order).build();
        payment = pointPaymentRepository.save(payment);

        account.updatePoint(account.getPoint() - toUsePoint);
        return Optional.of(payment.getId());
    }

    @Override
    @Transactional
    void refund(Long paymentId, int refundAmount) {    // 부분 취소 가능
        PointPayment payment = pointPaymentRepository.findById(paymentId).get();
        if(payment.getRefundableAmount() < refundAmount) { // 환불 가능한 금액이 환불할 금액보다 큼
            throw new IllegalStateException("환불할 금액이 환불 가능한 금액보다 큼");
        }
        payment.updateRefundedPoint(payment.getRefundedAmount() + refundAmount);
        payment.updateStatus(payment.getAmount() == payment.getRefundedAmount() ? PaymentStatus.REFUNDED : PaymentStatus.PARTIAL_REFUNDED);   // 환불 상태 결정
    }
    @Override
    public Class<PointPayment> getTargetPaymentClass() {
        return PointPayment.class;
    }
}

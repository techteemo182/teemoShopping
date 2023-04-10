package com.teemo.shopping.Order.service;

import com.teemo.shopping.Order.domain.Order;
import com.teemo.shopping.Order.domain.PointPayment;
import com.teemo.shopping.Order.domain.enums.PaymentMethod;
import com.teemo.shopping.Order.domain.enums.PaymentStatus;
import com.teemo.shopping.Order.dto.PaymentRefundParameter;
import com.teemo.shopping.Order.dto.payment_create_param.PointPaymentCreateParam;
import com.teemo.shopping.Order.repository.OrderRepository;
import com.teemo.shopping.Order.repository.PaymentRepository;
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
    void refund(PaymentRefundParameter parameter) {    // 부분 취소 가능
        PointPayment payment = pointPaymentRepository.findById(parameter.getPaymentId()).get();
        int refundPrice = parameter.getRefundPrice();
        if(payment.getRefundableAmount() < refundPrice) { // 환불 가능한 금액이 환불할 금액보다 큼
            throw new RuntimeException();
        }
        payment.updateRefundedPoint(payment.getRefundedAmount() + refundPrice);
        payment.updateStatus(payment.getAmount() == payment.getRefundedAmount() ? PaymentStatus.REFUNDED : PaymentStatus.PARTIAL_REFUNDED);   // 환불 상태 결정
    }
    @Override
    public Class<PointPayment> getTargetPaymentClass() {
        return PointPayment.class;
    }
}

package com.teemo.shopping.order.service.paymentSubscriber;

import com.teemo.shopping.order.domain.Payment;
import com.teemo.shopping.order.repository.PaymentRepository;
import com.teemo.shopping.order.service.OrderStateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class OrderStateChangeableSubscriber implements PaymentStateSubscriber {

    @Autowired
    private OrderStateService orderStateService;
    @Autowired
    private PaymentRepository<Payment> paymentRepository;

    @Override
    public void subscribe(Long paymentId) {
        orderStateService.orderStateTransition(paymentId);
    }
}

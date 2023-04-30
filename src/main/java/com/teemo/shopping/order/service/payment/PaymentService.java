package com.teemo.shopping.order.service.payment;

import com.teemo.shopping.core.layer.ServiceLayer;
import com.teemo.shopping.core.observer.Observer;
import com.teemo.shopping.order.domain.Payment;
import com.teemo.shopping.order.repository.PaymentRepository;
import com.teemo.shopping.order.service.context.OrderCreateContext;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class PaymentService implements ServiceLayer {

    @Autowired
    protected PaymentRepository<Payment> paymentRepository;

    public abstract Payment create(OrderCreateContext context) throws Exception;
    public abstract void refund(Long paymentId, int amount);

    /**
     * Throw 하면 CANCEL 로 상태전이됨
     *
     */
    public abstract void pay(Long paymentId);
    public abstract Class<? extends Payment> getPaymentClass();
}


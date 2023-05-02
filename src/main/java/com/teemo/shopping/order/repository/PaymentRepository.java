package com.teemo.shopping.order.repository;

import com.teemo.shopping.order.domain.Order;
import com.teemo.shopping.order.domain.Payment;
import com.teemo.shopping.order.enums.PaymentStates;
import jakarta.persistence.LockModeType;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentRepository<T extends Payment> extends JpaRepository<T, Long> {
    List<T> findAllByOrder(Order order);
    List<T> findAllByOrderAndState(Order order, PaymentStates paymentStates);
}

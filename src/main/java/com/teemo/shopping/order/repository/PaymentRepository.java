package com.teemo.shopping.order.repository;

import com.teemo.shopping.order.domain.Order;
import com.teemo.shopping.order.domain.Payment;
import jakarta.transaction.Transactional;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentRepository<T extends Payment> extends JpaRepository<T, Long> {
    @Transactional
    List<Payment> findAllByOrder(Order order);
}

package com.teemo.shopping.order.repository;

import com.teemo.shopping.order.domain.KakaopayPayment;
import com.teemo.shopping.order.domain.Order;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public interface KakaopayPaymentRepository extends PaymentRepository<KakaopayPayment> {
    Optional<KakaopayPayment> findByTid(String tid);
    Optional<KakaopayPayment> findByOrder(Order order);
}

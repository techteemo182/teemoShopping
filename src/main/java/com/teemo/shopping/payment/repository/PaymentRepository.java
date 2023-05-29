package com.teemo.shopping.payment.repository;

import com.teemo.shopping.payment.domain.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentRepository<T extends Payment> extends JpaRepository<T, Long> {
}

package com.teemo.shopping.Order.repository;

import com.teemo.shopping.Order.domain.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

}

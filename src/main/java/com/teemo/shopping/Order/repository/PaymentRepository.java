package com.teemo.shopping.Order.repository;

import com.teemo.shopping.Order.domain.Payment;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentRepository<T extends Payment> extends JpaRepository<T, Long> {

}

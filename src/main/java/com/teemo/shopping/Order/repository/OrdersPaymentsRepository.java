package com.teemo.shopping.Order.repository;

import com.teemo.shopping.Order.domain.OrdersPayments;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrdersPaymentsRepository extends JpaRepository<OrdersPayments, Long> {

}

package com.teemo.shopping.order.repository;

import com.teemo.shopping.order.domain.Order;
import com.teemo.shopping.account.domain.Account;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findAllByAccount(Account account);
}

package com.teemo.shopping.Order.repository;

import com.teemo.shopping.Order.domain.Order;
import com.teemo.shopping.account.domain.Account;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findAllByAccount(Account account);
}

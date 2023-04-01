package com.teemo.shopping.Order.repository;

import com.teemo.shopping.Order.domain.OrdersGames;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrdersGamesRepository extends JpaRepository<OrdersGames, Long> {

}

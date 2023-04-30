package com.teemo.shopping.order.repository;

import com.teemo.shopping.order.domain.Order;
import com.teemo.shopping.order.domain.OrdersGames;
import com.teemo.shopping.game.domain.Game;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface OrdersGamesRepository extends JpaRepository<OrdersGames, Long> {
    Optional<OrdersGames> findByOrderAndGame(Order order, Game game);
    List<OrdersGames> findAllByOrder(Order order);

    @Query(value = "select case when count(og) > 0 then true else false end from OrdersGames og join Order o where og.game.id in (:gameIds) and o.account.id = :accountId and o.state <> 'REFUNDED'")
    boolean isPurchasable(Long accountId, List<Long> gameIds);
}

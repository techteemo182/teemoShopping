package com.teemo.shopping.order.domain.trigger;

import com.teemo.shopping.order.domain.OrdersGames;
import com.teemo.shopping.order.repository.OrdersGamesRepository;
import jakarta.persistence.PostPersist;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

public class OrdersGamesTrigger {

    @Autowired
    @Lazy
    private OrdersGamesRepository repository;

    @PostPersist
    @Transactional(readOnly = true, propagation = Propagation.REQUIRES_NEW, isolation = Isolation.READ_UNCOMMITTED)
    public void afterCheck(OrdersGames ordersGames) {
        if (repository.isPurchasableWithSelf(ordersGames.getAccount().getId(),
            ordersGames.getGame().getId())) {
            throw new IllegalStateException("생성 불가능");
        }
    }
}

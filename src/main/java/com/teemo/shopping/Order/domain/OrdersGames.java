package com.teemo.shopping.Order.domain;


import com.teemo.shopping.Order.domain.enums.OrdersGamesStatus;
import com.teemo.shopping.core.entity.BaseEntity;
import com.teemo.shopping.game.domain.Game;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
public class OrdersGames extends BaseEntity {

    @Builder
    protected OrdersGames(Order order, Game game, OrdersGamesStatus status) {
        this.order = order;
        this.game = game;
        this.status = status;
    }

    @ManyToOne
    @JoinColumn(name = "orders_id")
    private Order order;

    @ManyToOne
    @JoinColumn(name = "games_id")
    private Game game;

    @Enumerated(EnumType.STRING)
    @NotNull
    private OrdersGamesStatus status;

    public void updateStatus(OrdersGamesStatus status) {
        this.status = status;
    }
}


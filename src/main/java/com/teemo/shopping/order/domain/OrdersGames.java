package com.teemo.shopping.order.domain;


import com.teemo.shopping.order.enums.OrdersGamesStatus;
import com.teemo.shopping.core.entity.BaseEntity;
import com.teemo.shopping.game.domain.Game;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
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
@AttributeOverride(name = "id", column = @Column(name = "orders_games_id"))
public class OrdersGames extends BaseEntity {

    @Builder
    protected OrdersGames(Order order, Game game, OrdersGamesStatus status) {
        this.order = order;
        this.game = game;
        this.status = status;
    }
    @Override
    public boolean equals(Object obj) {
        OrdersGames target = (OrdersGames)obj;
        return target.getGame().getId().equals(getGame().getId()) &&
            target.getOrder().getId().equals(getOrder().getId());
    }
    @ManyToOne
    @JoinColumn(name = "orders_id")
    @NotNull
    private Order order;

    @ManyToOne
    @JoinColumn(name = "games_id")
    @NotNull
    private Game game;

    @Enumerated(EnumType.STRING)
    @NotNull
    private OrdersGamesStatus status;

    public void updateStatus(OrdersGamesStatus status) {
        this.status = status;
    }
}


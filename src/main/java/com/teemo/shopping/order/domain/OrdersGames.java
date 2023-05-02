package com.teemo.shopping.order.domain;


import com.teemo.shopping.account.domain.Account;
import com.teemo.shopping.order.enums.OrdersGamesStates;
import com.teemo.shopping.core.entity.BaseEntity;
import com.teemo.shopping.game.domain.Game;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.web.bind.annotation.InitBinder;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
@AttributeOverride(name = "id", column = @Column(name = "orders_games_id"))
@Table(
    uniqueConstraints = {
        @UniqueConstraint(
            columnNames = {
                "accounts_id",
                "games_id"
            }
        )
    }
)
public class OrdersGames extends BaseEntity {

    @Builder
    public OrdersGames(Order order, Game game, Account account, OrdersGamesStates state,
        Integer price) {
        this.order = order;
        this.game = game;
        this.account = account;
        this.state = state;
        this.price = price;
    }

    @Override
    public boolean equals(Object obj) {
        OrdersGames target = (OrdersGames)obj;
        return target.getGame().getId().equals(getGame().getId()) &&
            target.getAccount().getId().equals(getAccount().getId());
    }
    @ManyToOne
    @JoinColumn(name = "orders_id")
    @NotNull
    private Order order;

    @ManyToOne
    @JoinColumn(name = "games_id")
    @NotNull
    private Game game;

    @ManyToOne
    @JoinColumn(name = "accounts_id")
    @NotNull
    private Account account;

    @Enumerated(EnumType.STRING)
    @NotNull
    private OrdersGamesStates state;

    @Column
    private Integer price;

    public void updateState(OrdersGamesStates state) {
        this.state = state;
    }
}


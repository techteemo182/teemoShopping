package com.teemo.shopping.Order;


import com.teemo.shopping.core.BaseEntity;
import com.teemo.shopping.game.Game;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
public class OrderHasGame extends BaseEntity {
    @ManyToOne

    @JoinColumn(name = "orders_id")
    private Order order;

    @ManyToOne
    @JoinColumn(name = "games_id")
    private Game game;

    @Column
    private double price;
}

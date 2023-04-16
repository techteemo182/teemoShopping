package com.teemo.shopping.order.domain;

import com.teemo.shopping.order.enums.PaymentMethod;
import com.teemo.shopping.order.enums.PaymentStatus;
import com.teemo.shopping.game.domain.Game;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MappedSuperclass;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;


@NoArgsConstructor(access = AccessLevel.PROTECTED)
@MappedSuperclass
public class GameProductPayment extends Payment {

    protected GameProductPayment(int amount, PaymentStatus status,
        Order order, PaymentMethod method, Game game) {
        super(amount, status, order, method);
        this.game = game;
    }

    @ManyToOne
    @JoinColumn(name = "games_id")
    @NotNull
    private Game game;

}

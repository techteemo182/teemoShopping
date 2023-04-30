package com.teemo.shopping.order.domain;

import com.teemo.shopping.order.enums.PaymentMethods;
import com.teemo.shopping.order.enums.PaymentMethods.Values;
import com.teemo.shopping.order.enums.PaymentStates;
import com.teemo.shopping.game.domain.Game;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(
    name = "discount_payments"
)
@DiscriminatorValue(Values.DISCOUNT)
public class DiscountPayment extends Payment {

    @Builder
    protected DiscountPayment(Integer amount, PaymentStates status, Order order,
        Game game) {
        super(amount, status, order, PaymentMethods.DISCOUNT);
        this.game = game;
    }


    @ManyToOne
    @JoinColumn(name = "games_id")
    @NotNull
    private Game game;

}

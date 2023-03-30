package com.teemo.shopping.Order;

import com.teemo.shopping.Order.PaymentMethod.Values;
import com.teemo.shopping.game.Game;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
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
public class DiscountPayment extends Payment{
    @Builder
    protected DiscountPayment(PaymentMethod method, double price, String info, Game game) {
        super(method, price, info);
        this.game = game;
    }

    @ManyToOne
    private Game game;
}

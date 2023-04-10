package com.teemo.shopping.Order.domain;

import com.teemo.shopping.Order.domain.enums.PaymentMethod;
import com.teemo.shopping.Order.domain.enums.PaymentMethod.Values;
import com.teemo.shopping.Order.domain.enums.PaymentStatus;
import com.teemo.shopping.game.domain.Game;
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
public class DiscountPayment extends GameProductPayment {

    @Builder
    protected DiscountPayment(int amount, PaymentStatus status, Order order, Game game) {
        super(amount, status, order, PaymentMethod.DISCOUNT, game);
    }
}

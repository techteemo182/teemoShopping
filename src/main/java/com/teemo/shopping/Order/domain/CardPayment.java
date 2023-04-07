package com.teemo.shopping.Order.domain;


import com.teemo.shopping.Order.domain.enums.PaymentMethod;
import com.teemo.shopping.Order.domain.enums.PaymentMethod.Values;
import com.teemo.shopping.Order.domain.enums.PaymentStatus;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(
    name = "card_payments"
)
@DiscriminatorValue(Values.KAKAOPAY)
public class CardPayment extends Payment {
    @Builder
    public CardPayment(int price, PaymentStatus status, Order order) {
        super(price, status, order, PaymentMethod.CARD);
    }
}
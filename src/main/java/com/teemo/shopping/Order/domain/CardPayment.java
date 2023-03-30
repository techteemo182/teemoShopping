package com.teemo.shopping.Order.domain;


import com.teemo.shopping.Order.domain.PaymentMethod.Values;
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
    public CardPayment(double price, PaymentStatus status) {
        super(price, status);
    }
}
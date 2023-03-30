package com.teemo.shopping.Order;


import com.teemo.shopping.Order.PaymentMethod.Values;
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
public class CardPayment extends Payment{
    @Builder
    public CardPayment(double price, PaymentStatus status) {
        super(price, status);
    }
}
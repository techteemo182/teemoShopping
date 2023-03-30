package com.teemo.shopping.Order.domain;

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
    name = "kakaopay_payments"
)
@DiscriminatorValue(Values.KAKAOPAY)
public class KakaoPayPayment extends Payment {
    @Builder
    public KakaoPayPayment(double price, PaymentStatus status) {
        super(price, status);
    }
}

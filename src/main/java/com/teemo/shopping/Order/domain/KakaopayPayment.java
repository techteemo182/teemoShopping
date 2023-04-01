package com.teemo.shopping.Order.domain;

import com.teemo.shopping.Order.domain.enums.PaymentMethod.Values;
import com.teemo.shopping.Order.domain.enums.PaymentStatus;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(
    name = "kakaopay_payments"
)
@DiscriminatorValue(Values.KAKAOPAY)
public class KakaopayPayment extends Payment {
    @Builder
    public KakaopayPayment(double price, PaymentStatus status) {
        super(price, status);
    }
    @Column
    @Length(min = 20, max = 20)
    private String tid;

    @Column
    private String pgToken;

    @Column
    private String cid;

    @Column
    private String partnerOrderId;

    @Column
    private String partnerUserId;
}

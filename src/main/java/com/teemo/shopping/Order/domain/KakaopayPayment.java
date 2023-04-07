package com.teemo.shopping.Order.domain;

import com.teemo.shopping.Order.domain.enums.PaymentMethod;
import com.teemo.shopping.Order.domain.enums.PaymentMethod.Values;
import com.teemo.shopping.Order.domain.enums.PaymentStatus;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Table;
import java.util.Optional;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.NaturalId;
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
    public KakaopayPayment(int price, PaymentStatus status, Order order,
        String tid, String cid, String partnerUserIdm) {
        super(price, status, order, PaymentMethod.KAKAOPAY);
        this.tid = tid;
        this.cid = cid;
        this.partnerUserId = partnerUserId;
    }

    @Length(min = 20, max = 20)
    private String tid;

    @Column
    private String cid;
    @Column
    private String partnerUserId; //현재는 1개지만 확장 될 경우 Partner(가명) 엔티티의 id 를 사용

    public void updateTid(String tid) {
        this.tid = tid;
    }
}

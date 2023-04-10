package com.teemo.shopping.Order.domain;

import com.teemo.shopping.Order.domain.enums.PaymentMethod;
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
public class KakaopayPayment extends AllProductPayment {

    @Builder
    public KakaopayPayment(int amount, PaymentStatus status, Order order,
        String tid, String cid) {
        super(amount, status, order, PaymentMethod.KAKAOPAY);
        this.tid = tid;
        this.cid = cid;
    }

    @Length(min = 20, max = 20)
    private String tid;

    @Column
    private String cid;
    @Column
    private String nextRedirectAppUrl;
    @Column
    private String nextRedirectMobileUrl;
    @Column
    private String nextRedirectPcUrl;
    @Column
    private String androidAppScheme;
    @Column
    private String iosAppScheme;
    public void updateRedirect(String nextRedirectAppUrl, String nextRedirectMobileUrl, String nextRedirectPcUrl, String androidAppScheme, String iosAppScheme) {
        this.nextRedirectAppUrl = nextRedirectAppUrl;
        this.nextRedirectMobileUrl = nextRedirectMobileUrl;
        this.nextRedirectPcUrl = nextRedirectPcUrl;
        this.androidAppScheme = androidAppScheme;
        this.iosAppScheme = iosAppScheme;
    }

    public void updateTid(String tid) {
        this.tid = tid;
    }
}

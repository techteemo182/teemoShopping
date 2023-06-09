package com.teemo.shopping.payment.domain;

import com.teemo.shopping.payment.domain.enums.KakaopayAPIStates;
import com.teemo.shopping.payment.domain.enums.PaymentMethods;
import com.teemo.shopping.payment.domain.enums.PaymentMethods.Values;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "kakaopay_payments")
@DiscriminatorValue(Values.KAKAOPAY)
public class KakaopayPayment extends Payment {

    @Column
    private String itemName;
    @Column
    private String pgToken;
    @Column
    private String redirectSecret;
    @Column
    private String nextRedirect;
    @Length(min = 20, max = 20)
    private String tid;
    @Enumerated(EnumType.STRING)
    private KakaopayAPIStates kakaopayAPIStates;
    @Column
    private String partnerUserId;
    @Column
    private String partnerOrderId;
    @Column
    private String redirect;
    @Column
    private String cid;

    @Builder
    protected KakaopayPayment(Integer amount, String redirect, String itemName,
        String pgToken, String redirectSecret, String nextRedirect, String tid, String partnerUserId, String partnerOrderId,
        String cid) {
        super(amount);
        this.itemName = itemName;
        this.pgToken = pgToken;
        this.redirectSecret = redirectSecret;
        this.nextRedirect = nextRedirect;
        this.tid = tid;
        this.kakaopayAPIStates = KakaopayAPIStates.INIT;
        this.partnerUserId = partnerUserId;
        this.partnerOrderId = partnerOrderId;
        this.cid = cid;
        this.redirect = redirect;
    }

    public void updateCid(String cid) {
        this.cid = cid;
    }

    public void updatePartnerUserId(String partnerUserId) {
        this.partnerUserId = partnerUserId;
    }

    public void updatePartnerOrderId(String partnerOrderId) {
        this.partnerOrderId = partnerOrderId;
    }

    public void updateRedirectSecret(String redirectSecret) {
        this.redirectSecret = redirectSecret;
    }

    public void updatePgToken(String pgToken) {
        this.pgToken = pgToken;
    }

    public void updateKakaopayAPIState(KakaopayAPIStates kakaopayAPIStates) {
        this.kakaopayAPIStates = kakaopayAPIStates;
    }

    public void updateRedirect(String redirect) {
        this.redirect = redirect;
    }

    public void updateTid(String tid) {
        this.tid = tid;
    }
}


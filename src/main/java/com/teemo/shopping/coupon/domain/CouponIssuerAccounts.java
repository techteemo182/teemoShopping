package com.teemo.shopping.coupon.domain;

import com.teemo.shopping.account.domain.Account;
import com.teemo.shopping.core.entity.BaseEntity;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access =  AccessLevel.PROTECTED)
@Getter
@Entity
@AttributeOverride(name = "id", column = @Column(name = "couponIssuePoliciesAccounts_id"))
@Table(
    name = "CouponIssuePoliciesAccounts"
)
public class CouponIssuerAccounts extends BaseEntity {

    @Builder
    public CouponIssuerAccounts(CouponIssuer couponIssuer, Account account) {
        this.couponIssuer = couponIssuer;
        this.account = account;
    }

    @ManyToOne
    private CouponIssuer couponIssuer;
    @ManyToOne
    private Account account;
}

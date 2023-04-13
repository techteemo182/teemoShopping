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
public class CouponIssuePoliciesAccounts extends BaseEntity {

    @Builder
    public CouponIssuePoliciesAccounts(CouponIssuePolicy couponIssuePolicy, Account account) {
        this.couponIssuePolicy = couponIssuePolicy;
        this.account = account;
    }

    @ManyToOne
    private CouponIssuePolicy couponIssuePolicy;
    @ManyToOne
    private Account account;
}

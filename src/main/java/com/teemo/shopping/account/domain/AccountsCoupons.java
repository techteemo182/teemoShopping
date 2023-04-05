package com.teemo.shopping.account.domain;

import com.teemo.shopping.core.entity.BaseEntity;
import com.teemo.shopping.coupon.domain.Coupon;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
@AttributeOverride(name = "id", column = @Column(name = "accounts_coupons_id"))
public class AccountsCoupons extends BaseEntity {
    @Builder
    public AccountsCoupons(Account account, Coupon coupon, int amount) {
        this.account = account;
        this.coupon = coupon;
        this.amount = amount;
    }

    @ManyToOne
    @JoinColumn(name = "accounts_id")
    private Account account;

    @ManyToOne
    @JoinColumn(name = "coupons_id")
    private Coupon coupon;

    @Column
    private int amount;

    public void updateAmount(int amount) {
        this.amount = amount;
    }
}

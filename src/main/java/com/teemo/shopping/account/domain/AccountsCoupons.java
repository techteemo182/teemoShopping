package com.teemo.shopping.account.domain;

import com.teemo.shopping.core.entity.BaseEntity;
import com.teemo.shopping.coupon.domain.Coupon;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
@Entity
public class AccountsCoupons extends BaseEntity {
    @Builder
    public AccountsCoupons(Account account, Coupon coupon, int amount) {
        this.account = account;
        this.coupon = coupon;
        this.amount = amount;
    }

    @ManyToOne
    @JoinColumn(name = "account_id")
    private Account account;

    @ManyToOne
    @JoinColumn(name = "coupon_id")
    private Coupon coupon;

    @Column
    private int amount;

    public void updateAmount(int amount) {
        this.amount = amount;
    }
}

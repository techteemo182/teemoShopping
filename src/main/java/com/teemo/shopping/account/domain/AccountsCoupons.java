package com.teemo.shopping.account.domain;

import com.teemo.shopping.core.BaseEntity;
import com.teemo.shopping.coupon.domain.Coupon;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
@Entity
public class AccountsCoupons extends BaseEntity {
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

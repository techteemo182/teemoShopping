package com.teemo.shopping.coupon.domain;

import com.teemo.shopping.core.entity.BaseEntity;
import com.teemo.shopping.game.domain.GameCategory;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
@Table(
    indexes = {
        @Index(columnList = "coupons_id, game_categories_id")
    }
)
@AttributeOverride(name = "id", column = @Column(name = "coupons_categories_id"))
public class CouponsGameCategories extends BaseEntity {

    @Builder
    public CouponsGameCategories(Coupon coupon, GameCategory gameCategory) {
        this.coupon = coupon;
        this.gameCategory = gameCategory;
    }

    @Override
    public boolean equals(Object obj) {
        CouponsGameCategories target = (CouponsGameCategories)obj;
        return target.getCoupon().getId().equals(getCoupon().getId())
            && target.getGameCategory().getId().equals(getGameCategory().getId());
    }
    @ManyToOne
    @JoinColumn(name = "coupons_id")
    @NotNull
    private Coupon coupon;

    @ManyToOne
    @JoinColumn(name = "game_categories_id")
    @NotNull
    private GameCategory gameCategory;
}

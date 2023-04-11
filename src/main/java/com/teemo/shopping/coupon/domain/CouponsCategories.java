package com.teemo.shopping.coupon.domain;

import com.teemo.shopping.core.entity.BaseEntity;
import com.teemo.shopping.game.domain.GameCategoriesGames;
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
import lombok.Getter;
import lombok.NoArgsConstructor;


@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(
    indexes = {
        @Index(columnList = "coupons_id, game_categories_id")
    }
)
@AttributeOverride(name = "id", column = @Column(name = "coupons_categories_id"))
@Entity
public class CouponsCategories extends BaseEntity {
    @Override
    public boolean equals(Object obj) {
        CouponsCategories target = (CouponsCategories)obj;
        return target.getCoupon().getId().equals(getCoupon().getId())
            && target.getGameCategory().getId().equals(getGameCategory().getId());
    }
    @ManyToOne
    @JoinColumn(name = "coupon_id")
    @NotNull
    private Coupon coupon;

    @ManyToOne
    @JoinColumn(name = "game_category_id")
    @NotNull
    private GameCategory gameCategory;
}

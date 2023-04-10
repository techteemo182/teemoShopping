package com.teemo.shopping.coupon.domain;

import com.teemo.shopping.core.entity.BaseEntity;
import com.teemo.shopping.game.domain.GameCategory;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

@Table(
    indexes = {
        @Index(columnList = "coupon_id, game_category_id")
    }
)
@AttributeOverride(name = "id", column = @Column(name = "coupons_categories_id"))
public class CouponsCategories extends BaseEntity {
    @ManyToOne
    @JoinColumn(name = "coupon_id")
    @NotNull
    private Coupon coupon;

    @ManyToOne
    @JoinColumn(name = "game_category_id")
    @NotNull
    private GameCategory gameCategory;
}

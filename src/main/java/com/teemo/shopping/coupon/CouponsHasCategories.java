package com.teemo.shopping.coupon;

import com.teemo.shopping.core.BaseEntity;
import com.teemo.shopping.game.GameCategory;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Table(
    indexes = {
        @Index(columnList = "coupon_id, game_category_id")
    }
)
public class CouponsHasCategories extends BaseEntity {
    @ManyToOne
    @JoinColumn(name = "coupon_id")
    Coupon coupon;

    @ManyToOne
    @JoinColumn(name = "game_category_id")
    GameCategory gameCategory;
}

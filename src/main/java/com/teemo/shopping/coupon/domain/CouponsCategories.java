package com.teemo.shopping.coupon.domain;

import com.teemo.shopping.core.entity.BaseEntity;
import com.teemo.shopping.game.domain.GameCategory;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Table(
    indexes = {
        @Index(columnList = "coupon_id, game_category_id")
    }
)
public class CouponsCategories extends BaseEntity {
    @ManyToOne
    @JoinColumn(name = "coupon_id")
    Coupon coupon;

    @ManyToOne
    @JoinColumn(name = "game_category_id")
    GameCategory gameCategory;
}

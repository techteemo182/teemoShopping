package com.teemo.shopping.coupon.domain;

import com.teemo.shopping.core.entity.BaseEntity;
import com.teemo.shopping.game.domain.Game;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

@Table(
    indexes = {
        @Index(columnList = "coupon_id, game_id")
    }
)
@AttributeOverride(name = "id", column = @Column(name = "coupons_games_id"))
public class CouponsGames extends BaseEntity {
    @ManyToOne
    @JoinColumn(name = "coupons_id")
    @NotNull
    private Coupon coupon;

    @ManyToOne
    @JoinColumn(name = "games_id")
    @NotNull
    private Game game;
}

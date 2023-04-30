package com.teemo.shopping.coupon.domain;

import com.teemo.shopping.core.entity.BaseEntity;
import com.teemo.shopping.game.domain.Game;
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
@AttributeOverride(name = "id", column = @Column(name = "coupons_games_id"))
@Entity
public class CouponsGames extends BaseEntity {

    @Builder
    public CouponsGames(Coupon coupon, Game game) {
        this.coupon = coupon;
        this.game = game;
    }

    @Override
    public boolean equals(Object obj) {
        CouponsGames target = (CouponsGames)obj;
        return target.getCoupon().getId().equals(getCoupon().getId())
            && target.getGame().getId().equals(getGame().getId());
    }
    @ManyToOne
    @JoinColumn(name = "coupons_id")
    @NotNull
    private Coupon coupon;

    @ManyToOne
    @JoinColumn(name = "games_id")
    @NotNull
    private Game game;
}

package com.teemo.shopping.account.domain;

import com.teemo.shopping.core.entity.BaseEntity;
import com.teemo.shopping.coupon.domain.Coupon;
import com.teemo.shopping.game.domain.Game;
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
@AttributeOverride(name = "id", column = @Column(name = "accounts_games_id"))
public class AccountsGames extends BaseEntity {
    @Builder
    public AccountsGames(Account account, Game game) {
        this.account = account;
        this.game = game;
    }

    @ManyToOne
    @JoinColumn(name = "accounts_id")
    private Account account;

    @ManyToOne
    @JoinColumn(name = "games_id")
    private Game game;

}

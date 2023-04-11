package com.teemo.shopping.account.domain;

import com.teemo.shopping.core.entity.BaseEntity;
import com.teemo.shopping.game.domain.Game;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
@AttributeOverride(name = "id", column = @Column(name = "accounts_has_games_id"))
public class AccountsOwnGames extends BaseEntity {
    @Builder
    public AccountsOwnGames(Account account, Game game) {
        this.account = account;
        this.game = game;
    }
    @Override
    public boolean equals(Object obj) {
        AccountsOwnGames target = (AccountsOwnGames)obj;
        return target.getAccount().getId().equals(getAccount().getId())
            && target.getGame().getId().equals(getGame().getId());
    }
    @ManyToOne
    @JoinColumn(name = "accounts_id")
    @NotNull
    private Account account;

    @ManyToOne
    @JoinColumn(name = "games_id")
    @NotNull
    private Game game;

}

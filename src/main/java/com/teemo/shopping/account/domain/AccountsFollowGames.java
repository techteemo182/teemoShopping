package com.teemo.shopping.account.domain;

import com.teemo.shopping.core.entity.BaseEntity;
import com.teemo.shopping.game.domain.Game;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@AttributeOverride(name = "id", column = @Column(name = "accounts_follow_games_id"))
@Table(
    name = "accounts_follow_games"
)
public class AccountsFollowGames extends BaseEntity {

    @Builder
    public AccountsFollowGames(Account account, Game game) {
        this.account = account;
        this.game = game;
    }
    @Override
    public boolean equals(Object obj) {
        AccountsFollowGames target = (AccountsFollowGames)obj;
        return target.getAccount().getId().equals(getAccount().getId())
            && target.getGame().getId().equals(getGame().getId());
    }
    /**
     * 게정
     */
    @ManyToOne
    @JoinColumn(name = "account_id")
    @NotNull
    private Account account;
    /**
     *  게임
     */
    @ManyToOne
    @JoinColumn(name = "game_id")
    @NotNull
    private Game game;
}

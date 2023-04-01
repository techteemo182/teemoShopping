package com.teemo.shopping.account.domain;

import com.teemo.shopping.account.domain.Account;
import com.teemo.shopping.core.BaseEntity;
import com.teemo.shopping.game.domain.Game;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
@AttributeOverride(name = "id", column = @Column(name = "cart_id"))
@Table(
    name = "cart"
)
public class Cart extends BaseEntity {

    /**
     * 게정
     */
    @ManyToOne
    @JoinColumn(name = "account_id")
    private Account account;
    /**
     *  게임
     */
    @ManyToOne
    @JoinColumn(name = "game_id")
    private Game game;
}

package com.teemo.shopping.Order;

import com.teemo.shopping.account.Account;
import com.teemo.shopping.core.BaseEntity;
import com.teemo.shopping.game.Game;
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
@AttributeOverride(name = "id", column = @Column(name = "carts_id"))
@Table(
    name = "carts"
)
public class Cart extends BaseEntity {
    @ManyToOne
    @JoinColumn(name = "accounts_id")
    private Account account;

    @ManyToOne
    @JoinColumn(name = "games_id")
    private Game game;
}

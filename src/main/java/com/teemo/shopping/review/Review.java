package com.teemo.shopping.review;

import com.teemo.shopping.account.Account;
import com.teemo.shopping.core.BaseEntity;
import com.teemo.shopping.game.Game;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Range;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@AttributeOverride(name = "id", column = @Column(name = "reviews_id"))
@Table(
    name = "reviews"
)
public class Review extends BaseEntity {

    @Builder
    protected Review(Account account, Game game, String content, double rating) {
        this.account = account;
        this.game = game;
        this.content = content;
        this.rating = rating;
    }

    @ManyToOne
    private Account account;

    @ManyToOne
    private Game game;

    @Column
    @NotBlank
    @NotNull
    private String content;

    @Column
    @Range(min = 0, max = 5)
    @NotNull
    private double rating;
}

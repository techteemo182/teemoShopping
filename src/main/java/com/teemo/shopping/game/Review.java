package com.teemo.shopping.game;

import com.teemo.shopping.account.Account;
import com.teemo.shopping.core.BaseEntity;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Range;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(
    name = "reviews"
)
@AttributeOverride(name = "id", column = @Column(name = "reviews_id"))
public class Review extends BaseEntity {

    @Builder
    public Review(String content, double rating, Account account, Game game) {
        this.content = content;
        this.rating = rating;
        this.account = account;
        this.game = game;
    }

    @Column
    private String content;

    @Column
    @Range(min = 0, max = 5)
    private double rating;

    @ManyToOne
    private Account account;

    @ManyToOne
    private Game game;
}

package com.teemo.shopping.game;

import com.teemo.shopping.core.BaseEntity;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.NaturalId;
import org.hibernate.validator.constraints.Range;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity()
@AttributeOverride(name = "id", column = @Column(name = "games_id"))
@Table(
    name = "games",
    indexes = {
        @Index(columnList = "rating")
    }
)
public class Game extends BaseEntity {

    @Builder
    protected Game(String name, double rating, int ratingCount) {
        this.name = name;
        this.rating = rating;
        this.ratingCount = ratingCount;
    }

    @Column
    @NaturalId
    @NotNull
    private String name;

    @Column
    @Range(min = 0, max = 5)
    private double rating;

    @Column
    private int ratingCount;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "game")
    List<GameCategoryGame> gameCategoryGames = new ArrayList<>();
}

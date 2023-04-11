package com.teemo.shopping.game.domain;

import com.teemo.shopping.core.entity.BaseEntity;
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
@Entity
@Table (
    indexes = {
        @Index(columnList = "game_categories_id, games_id")
    }
)
@AttributeOverride(name = "id", column = @Column(name = "game_categories_games_id"))
public class GameCategoriesGames extends BaseEntity {

    @Builder
    protected GameCategoriesGames(GameCategory gameCategory, Game game) {
        this.gameCategory = gameCategory;
        this.game = game;
    }
    @Override
    public boolean equals(Object obj) {
        GameCategoriesGames target = (GameCategoriesGames)obj;
        return target.getGameCategory().getId().equals(gameCategory.getId())
            && target.getGame().getId().equals(game.getId());
    }

    @ManyToOne
    @JoinColumn(name = "game_categories_id")
    @NotNull
    private GameCategory gameCategory;

    @ManyToOne
    @JoinColumn(name = "games_id")
    @NotNull
    private Game game;
}

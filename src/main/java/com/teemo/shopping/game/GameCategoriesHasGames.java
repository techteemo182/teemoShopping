package com.teemo.shopping.game;

import com.teemo.shopping.core.BaseEntity;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
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
@Table (
    indexes = {
        @Index(columnList = "game_categories_id, games_id")
    }
)
@AttributeOverride(name = "id", column = @Column(name = "game_categories_has_games_id"))
public class GameCategoriesHasGames extends BaseEntity {

    @Builder
    protected GameCategoriesHasGames(GameCategory gameCategory, Game game) {
        this.gameCategory = gameCategory;
        this.game = game;
    }


    @ManyToOne
    @JoinColumn(name = "game_categories_id")
    GameCategory gameCategory;

    @ManyToOne
    @JoinColumn(name = "games_id")
    Game game;
}

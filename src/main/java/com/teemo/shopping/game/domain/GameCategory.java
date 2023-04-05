package com.teemo.shopping.game.domain;

import com.teemo.shopping.core.entity.BaseEntity;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.NaturalId;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@AttributeOverride(name = "id", column = @Column(name = "game_categories_id"))
@Table(
    name = "game_categories"
)
public class GameCategory extends BaseEntity {

    @Builder
    protected GameCategory(String name) {
        this.name = name;
    }

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "gameCategory")
    private List<GameCategoriesGames> gameCategoryGames = new ArrayList<>();

    @Column
    @NaturalId
    private String name;

    public void updateName(String name) {
        this.name = name;
    }
}

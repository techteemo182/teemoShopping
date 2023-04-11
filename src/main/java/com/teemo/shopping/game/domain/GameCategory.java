package com.teemo.shopping.game.domain;

import com.teemo.shopping.core.entity.BaseEntity;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
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
    @Override
    public boolean equals(Object obj) {
        GameCategoriesGames target = (GameCategoriesGames)obj;
        return target.getId().equals(this.getId());
    }
    @Column
    @NotNull
    private String name;

    public void updateName(String name) {
        this.name = name;
    }
}

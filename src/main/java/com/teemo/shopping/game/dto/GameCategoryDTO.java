package com.teemo.shopping.game.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.teemo.shopping.game.domain.GameCategory;
import lombok.Builder;
import lombok.Getter;

@Getter
public class GameCategoryDTO {
    @JsonCreator
    @Builder
    protected GameCategoryDTO(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    private final Long id;
    private final String name;

    public static GameCategoryDTO from(GameCategory gameCategory) {
        return GameCategoryDTO.builder()
            .id(gameCategory.getId())
            .name(gameCategory.getName())
            .build();
    }
    public GameCategory to() {
        return GameCategory.builder()
            .name(name)
            .build();
    }
}

package com.teemo.shopping.game.dto;

import com.teemo.shopping.game.domain.GameCategory;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GameCategoryDTO {
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

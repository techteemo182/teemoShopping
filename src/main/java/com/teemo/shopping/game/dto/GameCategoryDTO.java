package com.teemo.shopping.game.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.teemo.shopping.game.domain.GameCategory;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class GameCategoryDTO {

    private Long id;
    private String name;

    public static GameCategoryDTO from(GameCategory gameCategory) {
        return GameCategoryDTO.builder().id(gameCategory.getId()).name(gameCategory.getName())
            .build();
    }

    public GameCategory to() {
        return GameCategory.builder().name(name).build();
    }
}

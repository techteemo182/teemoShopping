package com.teemo.shopping.game.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class GameCategoryModifyName {

    private final Long gameCategoryId;
    private final String name;
}

package com.teemo.shopping.game.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class GameCategoryRemoveById {

    private final Long gameCategoryId;
}

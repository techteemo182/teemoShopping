package com.teemo.shopping.game.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class GameCategoryAdd {
    private final String name;
}

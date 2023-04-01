package com.teemo.shopping.game.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class GameAdd {
    private final int price;
    private final String name;
    private final String description;
}

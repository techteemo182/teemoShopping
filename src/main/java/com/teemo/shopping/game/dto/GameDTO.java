package com.teemo.shopping.game.dto;

import com.teemo.shopping.game.domain.Game;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class GameDTO {
    private final Long id;
    private final String name;
    private final String description;
    private final double ratingAvg;
    private final int ratingCount;
    private final int price;
    private final double discountPercent;

    public static GameDTO from(Game game) {
        return GameDTO.builder()
            .id(game.getId())
            .name(game.getName())
            .description(game.getDescription())
            .ratingAvg(game.getRatingAvg())
            .ratingCount(game.getRatingCount())
            .price(game.getPrice())
            .discountPercent(game.getDiscountPercent())
            .build();
    }
    public Game to() {
        return Game.builder()
            .name(name)
            .description(description)
            .ratingAvg(ratingAvg)
            .ratingCount(ratingCount)
            .price(price)
            .discountPercent(discountPercent)
            .build();
    }
}

package com.teemo.shopping.game.dto;

import com.teemo.shopping.game.domain.Game;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class GameDTO {
    private final Long gameId;
    private final String name;
    private final String description;
    private final double ratingAvg;
    private final int ratingCount;
    private final double price;
    private final double discount;

    public static GameDTO from(Game game) {
        return GameDTO.builder()
            .gameId(game.getId())
            .name(game.getName())
            .description(game.getDescription())
            .ratingAvg(game.getRatingAvg())
            .ratingCount(game.getRatingCount())
            .price(game.getPrice())
            .discount(game.getDiscountPercent())
            .build();
    }
}

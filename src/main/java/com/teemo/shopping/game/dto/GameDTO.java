package com.teemo.shopping.game.dto;

import com.teemo.shopping.game.domain.Game;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class GameDTO {

    private Long gameId;
    private String name;
    private String description;
    private double ratingAvg;
    private int ratingCount;
    private double price;
    private double discount;

    public static GameDTO from(Game game) {
        return GameDTO.builder()
            .gameId(game.getId())
            .name(game.getName())
            .description(game.getDescription())
            .ratingAvg(game.getRatingAvg())
            .ratingCount(game.getRatingCount())
            .price(game.getPrice())
            .discount(game.getDiscount())
            .build();
    }
}

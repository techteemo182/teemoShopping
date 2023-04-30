package com.teemo.shopping.game.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.teemo.shopping.game.domain.Game;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class GameDTO {

    private Long id;
    private String name;
    private String description;
    private Double ratingAvg;
    private Integer ratingCount;
    private Integer price;
    private Double discountPercent;

    public static GameDTO from(Game game) {
        return GameDTO.builder().id(game.getId()).name(game.getName())
            .description(game.getDescription()).ratingAvg(game.getRatingAvg())
            .ratingCount(game.getRatingCount()).price(game.getPrice())
            .discountPercent(game.getDiscountPercent()).build();
    }

    public Game to() {
        return Game.builder().name(name).description(description).ratingAvg(ratingAvg)
            .ratingCount(ratingCount).price(price).discountPercent(discountPercent).build();
    }
}

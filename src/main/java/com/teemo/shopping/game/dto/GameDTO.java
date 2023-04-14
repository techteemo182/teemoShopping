package com.teemo.shopping.game.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.teemo.shopping.game.domain.Game;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
public class GameDTO {
    @JsonCreator
    @Builder
    protected GameDTO(Long id, String name, String description, Double ratingAvg, Integer ratingCount,
        Integer price, Double discountPercent) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.ratingAvg = ratingAvg;
        this.ratingCount = ratingCount;
        this.price = price;
        this.discountPercent = discountPercent;
    }

    private final Long id;
    private final String name;
    private final String description;
    private final Double ratingAvg;
    private final Integer ratingCount;
    private final Integer price;
    private final Double discountPercent;

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

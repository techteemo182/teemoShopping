package com.teemo.shopping.game.dto;

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
}

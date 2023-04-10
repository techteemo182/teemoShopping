package com.teemo.shopping.game.domain;

import com.teemo.shopping.core.entity.BaseEntity;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Range;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity()
@AttributeOverride(name = "id", column = @Column(name = "games_id"))
@Table(
    name = "games",
    indexes = {
        @Index(columnList = "ratingAvg")
    }
)
public class Game extends BaseEntity {

    @Builder
    protected Game(String name, String description, double ratingAvg, int ratingCount, int price, double discountPercent) {
        this.price = price;
        this.name = name;
        this.description = description;
        this.ratingAvg = ratingAvg;
        this.ratingCount = ratingCount;
        this.discountPercent = discountPercent;
    }

    @Override
    public boolean equals(Object obj) {
        Game target = (Game)obj;
        return target.getId() == this.getId();
    }
    @Column
    @NotNull
    /**
     * 게임 이름
     */
    private String name; // 게임 이름

    @Column
    /**
     * 게임 설명
     */
    private String description; // 게임 설명

    @Column
    @Range(min = 0, max = 5)
    /**
     * 게임 평균 평점
     */
    private double ratingAvg; // 평균 평점

    @Column
    @Range(min = 0)
    /**
     * 게임 평점 개수
     */
    private int ratingCount;
    /**
     *  게임 base 가격 단위(원)
     */
    @Column
    @Range(min = 0)
    private int price;

    /**
     *  할인율 단위 (%)
     */
    @Range(min = 0, max = 100)
    @Column
    private double discountPercent;
}

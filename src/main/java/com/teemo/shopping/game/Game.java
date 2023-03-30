package com.teemo.shopping.game;

import com.teemo.shopping.core.BaseEntity;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.NaturalId;
import org.hibernate.validator.constraints.Range;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity()
@AttributeOverride(name = "id", column = @Column(name = "games_id"))
@Table(
    name = "games",
    indexes = {
        @Index(columnList = "rating")
    }
)
public class Game extends BaseEntity {

    @Builder
    protected Game(String name, String description, double ratingAvg, int ratingCount) {
        this.name = name;
        this.description = description;
        this.ratingAvg = ratingAvg;
        this.ratingCount = ratingCount;
    }

    @Column
    @NaturalId
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
    /**
     * 게임 평점 개수
     */
    private int ratingCount;
    /**
     *  게임 base 가격 단위(원)
     */
    private double price;

    /**
     *  할인율 단위 (%)
     */
    private double discount;
    /**
     * 게임이 속한 카테고리
     */
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "game")
    List<GameCategoriesHasGames> gameCategoryGames = new ArrayList<>();
}

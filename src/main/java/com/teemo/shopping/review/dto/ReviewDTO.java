package com.teemo.shopping.review.dto;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.teemo.shopping.review.domain.Review;
import lombok.Builder;
import lombok.Getter;
import org.hibernate.validator.constraints.Range;

@Getter
public class ReviewDTO {
    @JsonCreator
    @Builder
    protected ReviewDTO(String content, double rating, Long accountId, Long gameId) {
        this.content = content;
        this.rating = rating;
        this.accountId = accountId;
        this.gameId = gameId;
    }

    private final String content;
    @Range(min = 0, max = 5)
    private final double rating;
    private final Long accountId;
    private final Long gameId;

    public static ReviewDTO from(Review review) {
        return ReviewDTO.builder()
            .content(review.getContent())
            .rating(review.getRating())
            .accountId(review.getAccount().getId())
            .gameId(review.getGame().getId())
            .build();
    }
}

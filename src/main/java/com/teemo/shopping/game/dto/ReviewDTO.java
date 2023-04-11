package com.teemo.shopping.game.dto;
import com.teemo.shopping.game.domain.Review;
import jakarta.persistence.Column;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import org.hibernate.validator.constraints.Range;

@Getter
@Builder
public class ReviewDTO {
    @Column
    private final String content;
    @Column
    @Range(min = 0, max = 5)
    private final double rating;
    @ManyToOne
    @NotNull
    private final Long accountId;

    @ManyToOne
    @NotNull
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

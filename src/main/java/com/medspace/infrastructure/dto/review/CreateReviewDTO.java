package com.medspace.infrastructure.dto.review;

import com.medspace.domain.model.Review;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreateReviewDTO {

    @NotNull
    @Min(1)
    @Max(5)
    private Integer rating;

    @NotBlank
    private String comment;

    @NotNull
    private Long rentRequestId;

    public Review toReview(Review.Type type) {
        Review review = new Review();
        review.setRating(rating);
        review.setComment(comment);
        review.setType(type);
        return review;
    }
}


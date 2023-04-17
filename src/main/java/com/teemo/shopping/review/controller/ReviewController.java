package com.teemo.shopping.review.controller;

import com.teemo.shopping.review.service.ReviewService;
import com.teemo.shopping.review.dto.ReviewDTO;
import com.teemo.shopping.security.PermissionChecker;
import io.swagger.v3.oas.annotations.Operation;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/reviews")
public class ReviewController {

    @Autowired
    private ReviewService reviewService;
    @Autowired
    private PermissionChecker permissionChecker;

    @Operation(operationId = "리뷰 조회", summary = "리뷰 조회", tags = {"리뷰"})
    @GetMapping(path = "/{reviewId}")
    public ReviewDTO get(@PathVariable("reviewId") Long reviewId) throws Exception {
        return reviewService.get(reviewId);
    }

    @Operation(operationId = "리뷰 삭제", summary = "리뷰 삭제", tags = {"리뷰"})
    @DeleteMapping(path = "/{reviewId}")
    public void remove(@PathVariable("reviewId") Long reviewId) throws Exception {
        permissionChecker.checkAuthenticatedAndThrow();
        ReviewDTO reviewDTO = reviewService.get(reviewId);
        if (!permissionChecker.checkAdmin() && !permissionChecker.checkResourceOwner(reviewDTO.getAccountId())) {
            throw new SecurityException("접근 권한 없음");
        }
        reviewService.remove(reviewId);
    }

    @Operation(operationId = "리뷰 리스트", summary = "리뷰 리스트", tags = {"리뷰"})
    @GetMapping(path = "/")
    public List<ReviewDTO> list(@PathVariable("gameId") Long gameId) throws Exception {
        return reviewService.list(gameId);
    }
}

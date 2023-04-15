package com.teemo.shopping.review.controller;

import com.teemo.shopping.review.service.ReviewService;
import com.teemo.shopping.review.dto.ReviewDTO;
import com.teemo.shopping.security.PermissionChecker;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/review")
public class ReviewController {

    @Autowired
    private ReviewService reviewService;
    @Autowired
    private PermissionChecker permissionChecker;

    @GetMapping(path = "/{reviewId}")
    public ReviewDTO get(@PathVariable("reviewId") Long reviewId) throws Exception {
        return reviewService.get(reviewId);
    }

    @DeleteMapping(path = "/{reviewId}")
    public void remove(@PathVariable("reviewId") Long reviewId) throws Exception {
        if(!permissionChecker.checkAuthenticated()) {
            throw new SecurityException("접근 권한 없음");
        }
        ReviewDTO reviewDTO = reviewService.get(reviewId);
        if (!permissionChecker.checkAdmin() && !permissionChecker.checkResourceOwner(reviewDTO.getAccountId())) {
            throw new SecurityException("접근 권한 없음");
        }
        reviewService.remove(reviewId);
    }

    @GetMapping(path = "/")
    public List<ReviewDTO> list(@PathVariable("gameId") Long gameId) {
        return reviewService.list(gameId);
    }
}

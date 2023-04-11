package com.teemo.shopping.game.controller;

import com.teemo.shopping.game.dto.ReviewDTO;
import com.teemo.shopping.game.repository.ReviewRepository;
import com.teemo.shopping.game.service.ReviewService;
import com.teemo.shopping.security.PermissionChecker;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/games/{gameId}/reviews")
public class ReviewController {

    @Autowired
    private ReviewService reviewService;
    @Autowired
    private PermissionChecker permissionChecker;

    @GetMapping(path = "/{reviewId}")
    public ReviewDTO get(@PathVariable("reviewId") Long reviewId) throws Exception {
        return reviewService.get(reviewId);
    }

    @PostMapping(path = "/")
    public Long add(ReviewDTO reviewDTO) throws Exception {
        if (!permissionChecker.checkAuthenticated() || (!permissionChecker.checkAdmin()
            && !permissionChecker.checkResourceOwner(reviewDTO.getAccountId()))) {
            throw new SecurityException("접근 권한 없음");
        }
        return reviewService.add(reviewDTO);
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

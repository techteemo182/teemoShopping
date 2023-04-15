package com.teemo.shopping.review.service;

import com.teemo.shopping.account.domain.Account;
import com.teemo.shopping.account.repository.AccountRepository;
import com.teemo.shopping.account.repository.AccountsOwnGamesRepository;
import com.teemo.shopping.game.domain.Game;
import com.teemo.shopping.review.domain.Review;
import com.teemo.shopping.game.repository.GameRepository;
import com.teemo.shopping.review.dto.ReviewDTO;
import com.teemo.shopping.review.repository.ReviewRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ReviewService {

    @Autowired
    private ReviewRepository reviewRepository;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private GameRepository gameRepository;
    @Autowired
    private AccountsOwnGamesRepository accountsOwnGamesRepository;
    @Transactional
    public Long add(ReviewDTO reviewDTO) throws Exception {
        Account account = accountRepository.findById(reviewDTO.getAccountId()).get();
        Game game = gameRepository.findById(reviewDTO.getGameId()).get();
        if(accountsOwnGamesRepository.findByAccountAndGame(account, game).isEmpty()) {
            throw new IllegalStateException("게정이 게임을 소유하고 있지 않음");
        }
        if(reviewRepository.findByAccountAndGame(account, game).isPresent()) {
            throw new IllegalStateException("이미 리뷰가 존재함.");
        }
        Review review = Review
            .builder()
            .account(account)
            .game(game)
            .content(reviewDTO.getContent())
            .rating(reviewDTO.getRating())
            .build();
        review = reviewRepository.save(review);
        game.addRating(review.getRating());

        return review.getId();
    }

    @Transactional
    public void remove(Long reviewId) throws Exception {
        Review review = reviewRepository.findById(reviewId).get();
        Game game = review.getGame();
        game.removeRating(review.getRating());
        reviewRepository.deleteById(reviewId);
    }

    @Transactional(readOnly = true)
    public ReviewDTO get(Long reviewId) throws Exception {
        return ReviewDTO.from(reviewRepository.findById(reviewId).get());
    }

    @Transactional(readOnly = true)
    public List<ReviewDTO> list() {
        return reviewRepository.findAll().stream()
            .map(review -> ReviewDTO.from(review)).toList();
    }
    @Transactional(readOnly = true)
    public List<ReviewDTO> list(Long gameId) {
        Game game = gameRepository.findById(gameId).get();
        return reviewRepository.findAllByGame(game).stream()
            .map(review -> ReviewDTO.from(review)).toList();
    }
}

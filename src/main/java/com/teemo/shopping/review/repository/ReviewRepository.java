package com.teemo.shopping.review.repository;

import com.teemo.shopping.account.domain.Account;
import com.teemo.shopping.game.domain.Game;
import com.teemo.shopping.review.domain.Review;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    Optional<Review> findByAccountAndGame(Account account, Game game);
    List<Review> findAllByAccount(Account account);
    List<Review> findAllByGame(Game game);
}

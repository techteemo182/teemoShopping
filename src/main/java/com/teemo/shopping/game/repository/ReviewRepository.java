package com.teemo.shopping.game.repository;

import com.teemo.shopping.account.domain.Account;
import com.teemo.shopping.game.domain.Game;
import com.teemo.shopping.game.domain.Review;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    Optional<Review> findByAccountAndGame(Account account, Game game);
    List<Review> findByGame(Game game);
}

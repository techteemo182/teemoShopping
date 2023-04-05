package com.teemo.shopping.account.repository;

import com.teemo.shopping.account.domain.Account;
import com.teemo.shopping.account.domain.AccountsCoupons;
import com.teemo.shopping.account.domain.AccountsGames;
import com.teemo.shopping.coupon.domain.Coupon;
import com.teemo.shopping.game.domain.Game;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountsGamesRepository extends JpaRepository<AccountsGames, Long> {
    Optional<AccountsGames> findByAccountAndGame(Account account, Game game);
    Optional<AccountsGames> findByGame(Game game);
}

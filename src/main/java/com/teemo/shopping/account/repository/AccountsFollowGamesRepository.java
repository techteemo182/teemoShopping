package com.teemo.shopping.account.repository;

import com.teemo.shopping.account.domain.Account;
import com.teemo.shopping.account.domain.AccountsFollowGames;
import com.teemo.shopping.game.domain.Game;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountsFollowGamesRepository extends JpaRepository<AccountsFollowGames, Long> {
    List<AccountsFollowGames> findAllByAccount(Account account);
    Optional<AccountsFollowGames> findByAccountAndGame(Account account, Game game);
}

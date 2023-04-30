package com.teemo.shopping.account.repository;

import com.teemo.shopping.account.domain.Account;
import com.teemo.shopping.account.domain.AccountsOwnGames;
import com.teemo.shopping.game.domain.Game;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountsOwnGamesRepository extends JpaRepository<AccountsOwnGames, Long> {
    Optional<AccountsOwnGames> findByAccountAndGame(Account account, Game game);
    List<AccountsOwnGames> findAllByAccount(Account account);
    void deleteByAccountAndGame(Account account, Game game);
}


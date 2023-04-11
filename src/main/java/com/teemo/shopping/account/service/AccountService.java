package com.teemo.shopping.account.service;

import com.teemo.shopping.account.domain.Account;
import com.teemo.shopping.account.domain.AccountsFollowGames;
import com.teemo.shopping.account.domain.AccountsOwnGames;
import com.teemo.shopping.account.dto.AccountDTO;
import com.teemo.shopping.account.repository.AccountRepository;
import com.teemo.shopping.account.repository.AccountsFollowGamesRepository;
import com.teemo.shopping.account.repository.AccountsOwnGamesRepository;
import com.teemo.shopping.game.domain.Game;
import com.teemo.shopping.game.repository.GameRepository;
import java.util.NoSuchElementException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AccountService {

    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private GameRepository gameRepository;
    @Autowired
    private AccountsOwnGamesRepository accountsOwnGamesRepository;
    @Autowired
    private AccountsFollowGamesRepository accountsFollowGamesRepository;

    @Transactional
    public void discardGame(Long gameId, Long accountId) throws RuntimeException {
        Game game = gameRepository.findById(gameId).get();
        Account account = accountRepository.findById(accountId).get();
        AccountsOwnGames accountsOwnGames = accountsOwnGamesRepository.findByAccountAndGame(account,
            game).get();
        try {
            accountsOwnGames = accountsOwnGamesRepository.findByAccountAndGame(account, game).get();
        } catch (NoSuchElementException e) {
            throw new IllegalStateException("게정이 게임을 소유 하고 있지 않음");
        }
        accountsOwnGamesRepository.delete(accountsOwnGames);
    }

    @Transactional
    public void ownGame(Long accountId, Long gameId) {
        Game game = gameRepository.findById(gameId).get();
        Account account = accountRepository.findById(accountId).get();
        if (accountsOwnGamesRepository.findByAccountAndGame(account, game)
            .isPresent()) {   // 게임 소유 중복
            throw new IllegalStateException("게정이 이미 게임을 소유하고 있음");
        }
        AccountsOwnGames accountsOwnGames = AccountsOwnGames.builder().account(account).game(game)
            .build();
        accountsOwnGamesRepository.save(accountsOwnGames);
    }

    @Transactional
    public void followGame(Long accountId, Long gameId) {
        Game game = gameRepository.findById(gameId).get();
        Account account = accountRepository.findById(accountId).get();
        if (accountsFollowGamesRepository.findByAccountAndGame(account, game).isPresent()) {
            throw new IllegalStateException("게정이 이미 게임을 팔로우하고 있음");
        }
        AccountsFollowGames accountsFollowGames = AccountsFollowGames.builder().account(account)
            .game(game).build();
        accountsFollowGamesRepository.save(accountsFollowGames);
    }

    @Transactional
    public void unfollowGame(Long accountId, Long gameId) {
        Game game = gameRepository.findById(gameId).get();
        Account account = accountRepository.findById(accountId).get();
        AccountsFollowGames accountsFollowGames;
        try {
            accountsFollowGames = accountsFollowGamesRepository.findByAccountAndGame(account, game)
                .get();
        } catch (NoSuchElementException e) {
            throw new IllegalStateException("게정이 게임을 팔로우 하고 있지 않음");
        }
        accountsFollowGamesRepository.save(accountsFollowGames);
    }

    @Transactional(readOnly = true)
    public AccountDTO get(String username) {
        return AccountDTO.from(accountRepository.findByUsername(username).get());
    }

    @Transactional(readOnly = true)
    public AccountDTO get(Long accountId) {
        return AccountDTO.from(accountRepository.findById(accountId).get());
    }
}

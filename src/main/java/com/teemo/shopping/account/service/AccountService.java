package com.teemo.shopping.account.service;

import com.teemo.shopping.account.domain.Account;
import com.teemo.shopping.account.domain.AccountsCoupons;
import com.teemo.shopping.account.domain.AccountsFollowGames;
import com.teemo.shopping.account.domain.AccountsOwnGames;
import com.teemo.shopping.account.dto.AccountDTO;
import com.teemo.shopping.account.repository.AccountRepository;
import com.teemo.shopping.account.repository.AccountsCouponsRepository;
import com.teemo.shopping.account.repository.AccountsFollowGamesRepository;
import com.teemo.shopping.account.repository.AccountsOwnGamesRepository;
import com.teemo.shopping.coupon.domain.Coupon;
import com.teemo.shopping.coupon.dto.CouponDTO;
import com.teemo.shopping.coupon.repository.CouponRepository;
import com.teemo.shopping.game.domain.Game;
import com.teemo.shopping.game.repository.GameRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
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

    @Autowired
    private CouponRepository couponRepository;
    @Autowired
    private AccountsCouponsRepository accountsCouponsRepository;

    @Transactional
    public void addCoupon(Long accountId, Long couponId, int amount) throws RuntimeException {
        Coupon coupon = couponRepository.findById(couponId).get();
        Account account = accountRepository.findById(accountId).get();
        AccountsCoupons accountsCoupons = accountsCouponsRepository.findByAccountAndCoupon(account,
                coupon)
            .orElse(AccountsCoupons.builder().account(account).coupon(coupon).amount(0).build());
        accountsCoupons.updateAmount(accountsCoupons.getAmount() + amount);
        accountsCouponsRepository.save(accountsCoupons);
    }

    @Transactional
    public void removeCoupon(Long accountId, Long couponId, int amount) {
        Coupon coupon = couponRepository.findById(couponId).get();
        Account account = accountRepository.findById(accountId).get();
        Optional<AccountsCoupons> accountsCouponsOptional = accountsCouponsRepository.findByAccountAndCoupon(account, coupon);
        if (!accountsCouponsOptional.isEmpty()) {
            throw new IllegalStateException("쿠폰이 없음");
        }
        AccountsCoupons accountsCoupons = accountsCouponsOptional.get();
        if (accountsCoupons.getAmount() < amount) {
            throw new IllegalStateException("쿠폰이 가지고있는 수가 제거 할 수보다 적음");
        }
        accountsCoupons.updateAmount(accountsCoupons.getAmount() - amount);
    }

    @Transactional
    public void discardGame(Long accountId, Long gameId) throws RuntimeException {
        Game game = gameRepository.findById(gameId).get();
        Account account = accountRepository.findById(accountId).get();
        AccountsOwnGames accountsOwnGames;
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
    @Transactional(readOnly = true)
    public List<CouponDTO> getCoupons(Long accountId) {
        Account account = accountRepository.findById(accountId).get();
        List<AccountsCoupons> accountsCoupons = accountsCouponsRepository.findByAccount(account);
        List<CouponDTO> couponDTOs = new ArrayList<>();
        for(var accountsCouponsEntry : accountsCoupons) {
            int amount = accountsCouponsEntry.getAmount();
            for(int i = 0; i < amount; i++) {
                couponDTOs.add(CouponDTO.from(accountsCouponsEntry.getCoupon()));
            }
        }
        return couponDTOs;
    }
}

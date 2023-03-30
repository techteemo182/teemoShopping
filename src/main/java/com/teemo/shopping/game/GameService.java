package com.teemo.shopping.game;

import com.teemo.shopping.Order.Payment;
import com.teemo.shopping.account.Account;
import com.teemo.shopping.account.AccountRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GameService {
    @Autowired
    AccountRepository accountRepository;

    @Autowired
    GameRepository gameRepository;

    @Transactional
    public void create(@Valid Game game) throws Exception {
        gameRepository.save(game);
    }
    @Transactional
    public void delete(@Valid Game game) throws Exception {
        gameRepository.delete(game);
    }
    @Transactional
    public void modify(@Valid Game game) throws Exception {
        gameRepository.save(game);
    }

    @Transactional
    public void purchaseGame(Account account, Game game, Payment payment) {
        //간단한 알고리즘
        //게임을 구매하려면 구입 방법등이 있어야한다.
        // 게임을 고른다 구입을 한다
        // 구입을 한다는것은 돈을 내고 게임을 받는다.

        // 돈을 내는 방법은 카드, 카카오 등 여러가지 방법등있다
        //
    }
}

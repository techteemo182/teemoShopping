package com.teemo.shopping.game.service;

import com.teemo.shopping.account.repository.AccountRepository;
import com.teemo.shopping.game.repository.GameRepository;
import com.teemo.shopping.game.domain.Game;
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

}

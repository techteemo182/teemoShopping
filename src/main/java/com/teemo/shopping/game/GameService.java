package com.teemo.shopping.game;

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
}

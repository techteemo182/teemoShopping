package com.teemo.shopping.account.service;

import com.teemo.shopping.account.domain.AccountsGames;
import com.teemo.shopping.account.dto.DeleteGameFromAccountParameter;
import com.teemo.shopping.account.dto.OwnGameParameter;
import com.teemo.shopping.account.exception.AccountAlreadyExist;
import com.teemo.shopping.account.exception.AccountNotFound;
import com.teemo.shopping.account.domain.Account;
import com.teemo.shopping.account.repository.AccountRepository;
import com.teemo.shopping.account.repository.AccountsGamesRepository;
import com.teemo.shopping.game.domain.Game;
import com.teemo.shopping.game.repository.GameRepository;
import jakarta.transaction.Transactional;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Service
@Validated
public class AccountService {

    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private GameRepository gameRepository;
    @Autowired
    private AccountsGamesRepository accountsGamesRepository;

    @Qualifier("loginFilter")
    private PasswordEncoder passwordEncoder;

    @Transactional
    public boolean validate(String username, String password) throws AccountNotFound {
        Optional<Account> accountOptional = accountRepository.findByUsername(username);
        if (accountOptional.isEmpty()) {
            throw new AccountNotFound();
        }
        Account account = accountOptional.get();

        return passwordEncoder.matches(account.getPassword(), password);
    }

    public boolean checkUsername(String username) {
        return !accountRepository.findByUsername(username).isEmpty();
    }

    @Transactional
    public void register(String username, String password) throws AccountAlreadyExist {
        if (accountRepository.findByUsername(username).isPresent()) {
            throw new AccountAlreadyExist();
        }
        accountRepository.save(
            Account.builder().username(username).password(passwordEncoder.encode(password))
                .build());
    }

    @Transactional
    public void ownGame(OwnGameParameter parameter) throws RuntimeException{
        Game game = gameRepository.findById(parameter.getGameId()).get();   // throw RuntimeException
        Account account = accountRepository.findById(parameter.getAccountId()).get();   // throw RuntimeException
        if(accountsGamesRepository.findByAccountAndGame(account, game).isPresent()) {   // 게임 소유 중복
            throw new RuntimeException();
        }
        accountsGamesRepository.save(AccountsGames.builder().game(game).account(account).build());
    }

    @Transactional
    public void deleteGameFromAccount(DeleteGameFromAccountParameter parameter) throws  RuntimeException {
        Game game = gameRepository.findById(parameter.getGameId()).get();   // throw RuntimeException
        Account account = accountRepository.findById(parameter.getAccountId()).get();   // throw RuntimeException
        AccountsGames accountsGames = accountsGamesRepository.findByAccountAndGame(account, game).get(); // throw RuntimeException
        accountsGamesRepository.delete(accountsGames);
    }
}

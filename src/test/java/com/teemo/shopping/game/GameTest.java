package com.teemo.shopping.game;

import com.teemo.shopping.Main;
import com.teemo.shopping.account.repository.AccountRepository;
import com.teemo.shopping.account.service.AccountAuthenticationService;
import com.teemo.shopping.account.service.AccountService;
import com.teemo.shopping.game.service.GameService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.reactive.function.client.WebClient;

@SpringBootTest(classes = Main.class)
public class GameTest {
    @Autowired
    AccountAuthenticationService accountAuthenticationService;
    @Autowired
    GameService gameService;
    @Autowired
    GameService reviewService;
    @Test
    public void createReview() {
        String username = "dsfjsl;a";
        String password = "ij1d12";
        Long accountId = accountAuthenticationService.register(username, password);

    }
}

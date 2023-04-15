package com.teemo.shopping;

import com.teemo.shopping.account.service.AccountAuthenticationService;
import com.teemo.shopping.account.service.AccountService;
import com.teemo.shopping.game.dto.GameDTO;
import com.teemo.shopping.game.service.GameService;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Component;

@SpringBootApplication
@EnableJpaAuditing
@EnableJpaRepositories
public class Main {

    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }
}

@Component
class TestModule {

    @Autowired
    private AccountService accountService;
    @Autowired
    private AccountAuthenticationService accountAuthenticationService;
    @Autowired
    private GameService gameService;

    TestModule() {

    }

    @PostConstruct
    void inject() throws Exception {
        String username = "root";
        String password = "root";
        Long accountId = accountAuthenticationService.register(username, password);
        Long teemoId = accountAuthenticationService.register("teemo", "teemo");
        accountAuthenticationService.updateAdmin(accountId, true);
        accountService.addPoint(teemoId, 100000);
        gameService.add(GameDTO.builder()
            .name("티모어드벤쳐")
            .description("티모와 게임을")
            .price(10300)
            .build()
        );
        gameService.add(GameDTO.builder()
            .name("리그오브치킨")
            .description("최고의 치킨이 되자")
            .price(20000)
            .build()
        );
    }
}
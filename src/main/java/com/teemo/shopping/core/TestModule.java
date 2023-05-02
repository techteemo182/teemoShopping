package com.teemo.shopping.core;

import com.teemo.shopping.account.service.AccountAuthenticationService;
import com.teemo.shopping.account.service.AccountService;
import com.teemo.shopping.game.dto.GameDTO;
import com.teemo.shopping.game.service.GameService;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class TestModule {

    @Autowired
    private AccountService accountService;
    @Autowired
    private AccountAuthenticationService accountAuthenticationService;
    @Autowired
    private GameService gameService;

    @Value("${test.admin.username}")
    String adminUsername;
    @Value("${test.admin.password}")
    String adminPassword;
    @Value("${test.user.username}")
    String username;
    @Value("${test.user.password}")
    String password;
    @PostConstruct
    void inject() throws Exception {
        Long accountId = accountAuthenticationService.register(adminUsername, adminPassword);
        Long teemoId = accountAuthenticationService.register(username, password);
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

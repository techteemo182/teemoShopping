package com.teemo.shopping;

import com.teemo.shopping.account.service.AccountAuthenticationService;
import com.teemo.shopping.account.service.AccountService;
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
	TestModule() {

	}
	@PostConstruct
	void inject() {
		String username = "root";
		String password = "root";
		Long accountId = accountAuthenticationService.register(username, password);
		accountAuthenticationService.updateAdmin(accountId, true);
	}
}
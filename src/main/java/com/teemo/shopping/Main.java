package com.teemo.shopping;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@SpringBootApplication
@EnableJpaAuditing
@EnableJpaRepositories
public class Main {
	public static void main(String[] args) {
		SpringApplication.run(Main.class, args);
	}
}

@Controller
@RequestMapping(path  = "/")
class TestContorller {
	@ResponseBody
	@GetMapping(path = "/")
	String main() {
		return "hello";
	}
}
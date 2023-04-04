package com.teemo.shopping;

import com.teemo.shopping.external_api.kakao.KakaopayService;
import com.teemo.shopping.external_api.kakao.dto.KakaopayReadyRequest;
import com.teemo.shopping.game.dto.GameDTO;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.view.RedirectView;

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
	@Value("${key.kakao.cid}")
	private String cid;
	@Value("${key.kakao.partner-user-id}")
	private String partnerUserId;
	@Autowired
	private KakaopayService kakaopayService;
	@ResponseBody
	@GetMapping(path = "/")
	RedirectView main() {
		return new RedirectView(kakaopayService.readyKakaopay(KakaopayReadyRequest.builder()
				.gameDTOs(List.of(GameDTO.builder().name("100만원 기부해주세요").build()).iterator())
				.price(1000000)
				.partnerUserId("123")
				.partnerOrderId("123")
				.cid(cid)
			.build()).block().getNextRedirectPcUrl());
	}
}
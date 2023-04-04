package com.teemo.shopping.external_api.kakao.dto;

import jakarta.validation.constraints.NotNull;
import java.net.http.HttpResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatusCode;
import reactor.netty.http.HttpProtocol;

@Getter
@Builder
public class KakaopayApproveResponse {
    private KakaopayAPIApproveResponse kakaopayAPIApproveResponse;
}

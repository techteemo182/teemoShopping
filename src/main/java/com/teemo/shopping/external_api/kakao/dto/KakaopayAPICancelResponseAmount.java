package com.teemo.shopping.external_api.kakao.dto;

import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class KakaopayAPICancelResponseAmount {
    private final Integer total;
    private final Integer taxFree;
    private final Integer vat;
    private final Integer point;
    private final Integer discount;
    private final Integer greenDeposit;
}

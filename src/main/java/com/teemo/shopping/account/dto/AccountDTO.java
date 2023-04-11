package com.teemo.shopping.account.dto;

import com.teemo.shopping.account.domain.Account;
import jakarta.persistence.Column;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AccountDTO {
    @Column
    @NotNull
    private Long id;

    @Column
    @NotNull
    private String username;

    @Column
    @NotNull
    private int point;

    public static AccountDTO from(Account account) {
        return AccountDTO.builder()
            .id(account.getId())
            .point(account.getPoint())
            .username(account.getUsername())
            .build();
    }
}

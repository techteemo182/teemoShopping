package com.teemo.shopping.account.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.teemo.shopping.account.domain.Account;
import jakarta.persistence.Column;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class AccountDTO {
    @Column
    @NotNull
    private Long id;

    @Column
    @NotNull
    private String username;

    @Column
    private String password;
    @Column
    private boolean isAdmin;
    @Column
    private int point;

    public static AccountDTO from(Account account) {
        return AccountDTO.builder()
            .id(account.getId())
            .point(account.getPoint())
            .username(account.getUsername())
            .isAdmin(account.isAdmin())
            .build();
    }
}

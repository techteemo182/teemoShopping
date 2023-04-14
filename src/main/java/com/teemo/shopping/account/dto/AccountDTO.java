package com.teemo.shopping.account.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
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
    @JsonCreator
    @Builder
    protected AccountDTO(Long id, String username, String password, boolean isAdmin, int point) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.isAdmin = isAdmin;
        this.point = point;
    }


    private Long id;
    private String username;
    private String password;
    private boolean isAdmin;
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

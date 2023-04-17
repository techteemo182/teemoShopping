package com.teemo.shopping.account.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class LoginRequest {
    @NotNull
    private final String username;
    @NotNull
    private final String password;
}

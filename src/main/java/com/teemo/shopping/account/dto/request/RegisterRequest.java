package com.teemo.shopping.account.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.hibernate.validator.constraints.Length;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class RegisterRequest {
    @NotNull
    @Length(min = 4, max = 20)
    private final String username;
    @NotNull
    @Length(min = 8, max = 50)
    private final String password;
}

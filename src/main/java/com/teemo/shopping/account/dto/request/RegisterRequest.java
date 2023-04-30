package com.teemo.shopping.account.dto.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.hibernate.validator.constraints.Length;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@AllArgsConstructor(onConstructor_ = {@JsonCreator})
@Getter
public class RegisterRequest {
    @NotNull
    @Length(min = 4, max = 20)
    private String username;
    @NotNull
    @Length(min = 8, max = 50)
    private String password;
}

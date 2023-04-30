package com.teemo.shopping.account.controller;

import com.teemo.shopping.account.dto.request.LoginRequest;
import com.teemo.shopping.account.dto.request.RegisterRequest;
import com.teemo.shopping.account.service.AccountAuthenticationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/accounts")
public class AccountAuthenticationController {

    @Autowired
    private AccountAuthenticationService accountAuthenticationService;

    @PostMapping(path = "/login")
    @ResponseBody
    @Operation(operationId = "로그인", summary = "로그인 하고 엑세스 토큰 반환", tags = {"인증"}, parameters = {
        @Parameter(in = ParameterIn.PATH, name = "id", description = "Employee Id", schema = @Schema(example = "\"username\": teemo\n\"password\":password"))}, responses = {
        @ApiResponse(responseCode = "200", description = "성공", content = @Content(schema = @Schema(example = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJ0ZWVtb19zaG9wcGluZyIsImV4cCI6MTY4MTY4MTYyNSwiYWNjb3VudF9pZCI6MSwicm9sZSI6IlJPTEVfQURNSU4ifQ.XH7o3QTMmW1NXltGbSzOSilgcWOwDZnhF2YhFu2tMs1hCyhfA6QVjvkhExbCCdLXOot8eeuae76HM0GliEcQJVFkvPPRtTFQPfaAibIuNFujb72WYiuKxSUSPyJwMQ-fP7RjjAaL2BXgQxUtvgYH0Cvc5zAuo3J0ney9CMGoatSND5bsm7gESEocv8H8njxCM-wOYJ3u3RMfsbdSbWBJ6OLmEoP2oMvKp67rh96y3Y_QRlRatesfB8tj85rwznrkRbVTBBownTmQ9BT1UZALyKy1DEY3gaxhiqMRnbG_UfYlNq9_vXmNYRyqQT79Tu-2RBD70VEa6qlMdqKINYWgcA"))),
        @ApiResponse(responseCode = "400", description = "로그인 실패", content = @Content(schema = @Schema(example = "실패 사유")))})
    public String login(@RequestBody LoginRequest loginRequest) {
        return accountAuthenticationService.login(loginRequest.getUsername(),
            loginRequest.getPassword());
    }

    @PostMapping(path = "/register")    //improve: check robot
    @ResponseBody
    @Operation(operationId = "회원가입", summary = "회원가입", tags = {"인증"}, parameters = {
        @Parameter(in = ParameterIn.PATH, name = "id", description = "Employee Id")}, responses = {
        @ApiResponse(responseCode = "200", description = "성공", content = @Content(schema = @Schema(example = "success"))),
        @ApiResponse(responseCode = "400", description = "회원가입 실패", content = @Content(schema = @Schema(example = "실패 사유")))})
    public String register(@RequestBody RegisterRequest registerRequest) {
        accountAuthenticationService.register(registerRequest.getUsername(),
            registerRequest.getPassword());
        return "success";
    }
}

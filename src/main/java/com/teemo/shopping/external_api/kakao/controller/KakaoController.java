package com.teemo.shopping.external_api.kakao.controller;

import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/kakao")
public class KakaoController {
    @GetMapping("/success")
    public void success(@RequestParam("pg_token") String pgToken) {

    }
    @GetMapping("/cancel")
    public void cancel(@RequestParam Map<String, String> params) {

    }
    @GetMapping("/fail")
    public void fail(@RequestParam Map<String, String> params) {

    }
}

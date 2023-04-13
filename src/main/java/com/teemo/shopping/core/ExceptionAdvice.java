package com.teemo.shopping.core;

import jakarta.servlet.http.HttpServletResponse;
import java.util.NoSuchElementException;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.InitBinder;

@ControllerAdvice
public class ExceptionAdvice {
    @ExceptionHandler
    public ResponseEntity<String> exception(Exception e) {
        String message = "";
        if(e instanceof NoSuchElementException) {
            message = "올바른 parameter 인지 다시 확인해 주세요.";
        } else message = e.getMessage();
        return ResponseEntity.status(400).body(message);
    }
    @InitBinder
    public void initBinder(WebDataBinder webDataBinder) {

    }
}

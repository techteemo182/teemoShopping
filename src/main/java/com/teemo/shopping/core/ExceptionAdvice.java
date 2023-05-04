package com.teemo.shopping.core;

import com.teemo.shopping.core.exception.ServiceException;
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

        if(e instanceof NoSuchElementException) {
            return ResponseEntity.status(400).body("유효 하지 않은 Parameter 입니다.");
        } else if(e instanceof SecurityException) {
            return ResponseEntity.status(401).body("접근 가능한 권한이 없습니다.");
        } else if(e instanceof ServiceException){
            return ResponseEntity.status(400).body(e.getMessage());
        } else {
            return ResponseEntity.status(400).body("API 에러입니다.");
        }
    }
}

package com.example.shoppingmall.controller;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/api")
public class AuthController {

//    @PostMapping("/logout")
////    @GetMapping("/logout")
//    public ResponseEntity<String> logout(HttpServletRequest request, HttpServletResponse response) {
//        // Spring Security가 자동으로 로그아웃 처리
//    	System.out.println("===================================================>log out");
//    	
//        return ResponseEntity.ok("로그아웃 성공");
//    }
}
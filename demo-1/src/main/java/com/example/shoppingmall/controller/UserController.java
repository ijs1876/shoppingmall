package com.example.shoppingmall.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.shoppingmall.entity.User;
import com.example.shoppingmall.repository.UserRepository;

@RestController
@RequestMapping("/api")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/mypage")
    public User getMyPage(Authentication auth) { // 사용자 정보 조회
        return (User) auth.getPrincipal();
    }

    @PatchMapping("/mypage")
    public ResponseEntity<User> updateUser(
            @RequestBody Map<String, String> updates,
            Authentication auth) {
        User user = (User) auth.getPrincipal();
        if (updates.containsKey("email")) {
            user.setEmail(updates.get("email"));
        }
        // username은 보통 수정 불가로 유지
        User updatedUser = userRepository.save(user);
        return ResponseEntity.ok(updatedUser);
    }

    @GetMapping("/admin/users")
    @PreAuthorize("hasRole('ADMIN')")
    public List<User> getAllUsers() { // 관리자 사용자 목록 조회
        return userRepository.findAll();
    }
}
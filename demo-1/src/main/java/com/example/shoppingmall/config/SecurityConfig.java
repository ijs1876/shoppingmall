package com.example.shoppingmall.config;

import java.util.Arrays;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.example.shoppingmall.service.CustomUserDetailsService;

// Spring 설정 클래스임을 나타냄
@Configuration
// Spring Security를 활성화
@EnableWebSecurity
// WebSecurityConfigurerAdapter를 상속하여 Spring Security 설정 커스터마이징
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    // 사용자 세부 정보를 처리하는 서비스 주입
    @Autowired
    private CustomUserDetailsService userDetailsService;

    // 인증 관리자 설정: 사용자 세부 정보 서비스와 비밀번호 인코더 설정
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService) // CustomUserDetailsService를 통해 사용자 정보 로드
            .passwordEncoder(passwordEncoder()); // 비밀번호 암호화에 BCrypt 사용
    }

    // HTTP 보안 설정: 요청 권한, 로그인, 로그아웃 등 정의
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .cors() // CORS 설정 활성화 (아래 corsConfigurationSource 빈 참조)
            .and()
            .csrf().disable() // CSRF 비활성화 (REST API 환경에서 주로 사용)
            .authorizeRequests() // 요청에 대한 권한 설정 시작
                .antMatchers("/api/products/**", "/api/logout", "/images/**").permitAll() // 인증 없이 접근 허용
                .antMatchers("/api/cart/**", "/api/orders/**").authenticated() // 인증된 사용자만 접근 가능
                .antMatchers("/api/admin/**").hasRole("ADMIN") // ADMIN 역할이 있는 사용자만 접근 가능
                .anyRequest().authenticated() // 그 외 모든 요청은 인증 필요
            .and()
            .formLogin() // 폼 기반 로그인 설정
                .loginProcessingUrl("/api/login") // 로그인 요청 처리 URL
                .defaultSuccessUrl("/api/products") // 로그인 성공 시 이동할 기본 URL
                .permitAll() // 로그인 페이지는 누구나 접근 가능
            .and()
            .logout() // 로그아웃 설정
                .logoutUrl("/api/logout") // 로그아웃 요청 URL
                .logoutSuccessUrl("/api/products") // 로그아웃 성공 시 이동할 URL ("/"로 변경 가능)
                .invalidateHttpSession(true) // 세션 무효화
                .deleteCookies("JSESSIONID") // JSESSIONID 쿠키 삭제
                .permitAll() // 로그아웃은 누구나 요청 가능
                .logoutSuccessHandler((req, res, auth) -> { // 로그아웃 성공 시 커스텀 응답 (선택적)
                    res.setStatus(HttpServletResponse.SC_OK); // HTTP 상태 200 설정
                    res.setContentType("application/json; charset=UTF-8"); // 응답 타입과 인코딩 설정
                    res.getWriter().write("{\"message\": \"로그아웃 성공\"}"); // JSON 응답 작성
                });
    }

    // CORS 설정을 위한 빈 정의
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://127.0.0.1:3000")); // 허용된 출처 (프론트엔드 URL)
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PATCH", "PUT", "DELETE")); // 허용된 HTTP 메서드
        configuration.setAllowCredentials(true); // 쿠키/인증 정보 포함 허용
        configuration.setAllowedHeaders(Arrays.asList("*")); // 모든 헤더 허용
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration); // 모든 경로에 CORS 설정 적용
        return source;
    }

    // 비밀번호 암호화를 위한 BCryptPasswordEncoder 빈 정의
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // BCrypt 알고리즘을 사용한 비밀번호 인코더 반환
    }
}
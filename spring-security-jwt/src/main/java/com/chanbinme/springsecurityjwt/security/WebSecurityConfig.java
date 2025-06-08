package com.chanbinme.springsecurityjwt.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Spring Security 설정 클래스
 * - HTTP 보안 정책 정의
 * - 필터 체인 구성
 * - 인증/인가 규칙 설정
 */
@Configuration
@EnableWebSecurity  // Spring Security 활성화
@EnableMethodSecurity   // 메소드 보안을 활성화하여 @PreAuthorize, @PostAuthorize 등의 어노테이션 사용 가능
@RequiredArgsConstructor
public class WebSecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter; // JWT 인증 필터
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint; // 인증 실패 시 처리할 엔트리 포인트

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // CSRF 보호 비활성화 (API 서버에서는 일반적으로 CSRF 보호가 필요 없음)
            .csrf(AbstractHttpConfigurer::disable)
            // 세션 관리 정책: STATELESS로 설정 (JWT를 사용하므로 세션을 사용하지 않음)
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            // 인증 실패 시 처리할 Entry Point 설정
            .exceptionHandling(exception -> exception.authenticationEntryPoint(jwtAuthenticationEntryPoint))
            // URL 별 접근 권한 설정
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/**").permitAll() // 인증 없이 접근 가능한 URL
                .requestMatchers("/api/public/**").permitAll() // 공개 API URL
                .requestMatchers(HttpMethod.GET, "/api/posts/**").permitAll() // 게시글 조회는 인증 없이 가능
                .anyRequest().authenticated() // 나머지 요청은 인증 필요
            )
            // JWT 인증 필터를 UsernamePasswordAuthenticationFilter 이전에 추가
            // 이렇게 하면 JWT 토큰 검증이 기본 인증보다 먼저 실행됨
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build(); // SecurityFilterChain 빌드 및 반환
    }

    /**
     * AuthenticationManager Bean 등록
     * - Spring Security의 인증 매니저를 제공하여 인증 관련 작업을 처리
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    /**
     * PasswordEncoder Bean 등록
     * - 비밀번호 암호화를 위한 BCryptPasswordEncoder 사용
     * - Spring Security에서 비밀번호 비교 시 사용됨
     */
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}

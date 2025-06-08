package com.chanbinme.springsecurityjwt.controller;

import com.chanbinme.springsecurityjwt.dto.LoginRequest;
import com.chanbinme.springsecurityjwt.security.CustomUserDetailService;
import com.chanbinme.springsecurityjwt.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final CustomUserDetailService userDetailService;

    @PostMapping("/login")
    public ResponseEntity<JwtAuthResponse> authenticateUser(
        @RequestBody LoginRequest loginRequest) {
        // 1. 사용자 인증 요청 처리
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(loginRequest.username(),
                loginRequest.password()));

        // 2. 인증 성공 후 사용자 정보 추출
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        // 3. JWT 토큰 생성
        String accessToken = jwtTokenProvider.generateAccessToken(userDetails);
        String refreshToken = jwtTokenProvider.generateRefreshToken(userDetails);

        return ResponseEntity.ok()
            .body(new JwtAuthResponse(accessToken, refreshToken));
    }

    @PostMapping("/refresh")
    public ResponseEntity<JwtAuthResponse> refreshToken(
        @RequestBody String refreshToken) {
        // 1. 리프레시 토큰 검증
        if (!jwtTokenProvider.validateToken(refreshToken)) {
            throw new IllegalArgumentException("유효하지 않은 리프레시 토큰입니다.");
        }

        // 2. 리프레시 토큰에서 사용자 이름 추출
        String username = jwtTokenProvider.getUsernameFromToken(refreshToken);

        // 3. 사용자 정보 조회
        UserDetails userDetails = userDetailService.loadUserByUsername(username);

        // 4. 새로운 액세스 토큰 생성
        String newAccessToken = jwtTokenProvider.generateAccessToken(userDetails);

        return ResponseEntity.ok()
            .body(new JwtAuthResponse(newAccessToken, refreshToken));
    }
}

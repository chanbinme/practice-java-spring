package com.chanbinme.springsecurityjwt.security;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.util.Date;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

/**
 * JWT 토큰의 생성, 검증, 파싱을 담당하는 핵심 유틸리티 클래스
 * - Access Token과 Refresh Token 생성
 * - JWT 토큰의 유효성 검사
 * - JWT 토큰에서 사용자 정보 추출
 */
@Component
public class JwtTokenProvider {

    // HMAC SHA 알고리즘을 위한 비밀키(application.yml에서 주입)
    private final SecretKey secretKey;
    // JWT 토큰의 만료 시간 설정 (24시간)
    private final long jwtExpiration;
    // JWT 토큰의 리프레시 시간 설정 (7일)
    private final long jwtRefreshExpiration;

    /**
     * Jwt 생성을 위한 SecretKey 초기화
     * @param secretKey application.yml에서 설정한 비밀키
     */
    public JwtTokenProvider(@Value("${jwt.secret-key}") String secretKey,
                            @Value("${jwt.access-token-expiration-millis}") long jwtExpiration,
                            @Value("${jwt.refresh-token-expiration-millis}") long jwtRefreshExpiration) {
        this.secretKey = Keys.hmacShaKeyFor(secretKey.getBytes());
        this.jwtExpiration = jwtExpiration;
        this.jwtRefreshExpiration = jwtRefreshExpiration;
    }

    /**
     * 사용자 인증 정보를 기반으로 Access Token 생성
     *
     * @param userDetails Spring Security의 사용자 상세 정보
     * @return 생성된 Access Token 문자열
     */
    public String generateAccessToken(UserDetails userDetails) {
        return createToken(userDetails.getUsername(), jwtExpiration);
    }

    /**
     * JWT 토큰을 생성하는 내부 메소드
     *
     * @param userDetails Spring Security의 사용자 상세 정보
     * @return 생성된 JWT 토큰 문자열
     */
    public String generateRefreshToken(UserDetails userDetails) {
        return createToken(userDetails.getUsername(), jwtRefreshExpiration);
    }

    /**
     * JWT 토큰을 생성하는 내부 메소드
     * @param username 사용자 이름
     * @param expiration 토큰의 만료 시간 (밀리초 단위)
     * @return 생성된 JWT 토큰 문자열
     */
    private String createToken(String username, long expiration) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration);
        return Jwts.builder()
            .subject(username)  // 토큰의 주제(subject)로 사용자 이름을 설정
            .issuedAt(now)  // 토큰 발급 시간 설정
            .expiration(expiryDate) // 토큰 만료 시간 설정
            .signWith(secretKey)  // 비밀키를 사용하여 서명
            .compact();
    }

    /**
     * JWT 토큰에서 사용자명 추출
     *
     * @param token 검증할 JWT 토큰 문자열
     * @return 토큰에서 추출한 사용자 이름
     */
    public String getUsernameFromToken(String token) {
        return Jwts.parser()
            .verifyWith(secretKey)  // 비밀키로 토큰 검증
            .build()
            .parseSignedClaims(token)   // 서명된 토큰 파싱
            .getPayload()   // 토큰을 파싱하여 Claims 객체를 생성
            .getSubject();  // 토큰에서 사용자 이름(subject) 추출
    }

    /**
     * JWT 토큰의 유효성을 검사
     *
     * @param token 검증할 JWT 토큰 문자열
     * @return 토큰이 유효하면 true, 그렇지 않으면 false
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                .verifyWith(secretKey)  // 비밀키로 토큰 검증
                .build()
                .parseSignedClaims(token);  // 서명된 토큰 파싱
            return true;  // 토큰이 유효한 경우 true 반환
        } catch (JwtException | IllegalArgumentException e) {
            return false;  // 예외 발생 시 토큰이 유효하지 않음
        }
    }
}

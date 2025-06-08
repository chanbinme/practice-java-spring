package com.chanbinme.springsecurityjwt.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * JWT 인증 필터 클래스
 * - HTTP 요청에서 JWT 토큰을 추출하고 검증하여 인증 정보를 설정
 * - 인증 정보가 유효한 경우 SecurityContext에 사용자 정보를 저장
 * - Spring Security의 OncePerRequestFilter를 상속받아 매 요청당 한 번만 실행됨
 * - Spring Security의 필터 체인에서 UsernamePasswordAuthenticationFilter 이전에 실행
 */
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;    // JWT 토큰 생성 및 검증을 위한 유틸리티 클래스
    private final UserDetailsService userDetailsService;    // 사용자 정보를 조회하기 위한 서비스

    /**
     * 필터의 핵심 로직: 요청에서 JWT 토큰을 추출하고 검증하여 인증 정보 설정
     *
     * @param request     HTTP 요청 객체
     * @param response    HTTP 응답 객체
     * @param filterChain 다음 필터로 요청을 전달하기 위한 체인
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        // 1. 요청에서 JWT 토큰 추출
        String token = getTokenFromRequest(request);

        // 2. 토큰이 존재하고 유효한 경우
        if (StringUtils.hasText(token) && jwtTokenProvider.validateToken(token)) {
            // 3. 토큰에서 사용자 이름 추출
            String username = jwtTokenProvider.getUsernameFromToken(token);

            // 4. 사용자 정보를 UserDetailsService를 통해 조회
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            // 5. Spring Security 인증 객체 생성
            // principal(사용자 정보), credentials(이미 인증되었기 때문에 null로 설정), authorities(권한 정보)를 포함하는 인증 객체 생성
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

            // 6. SecurityContext에 인증 정보 설정
            // 이후 Controller에서 @AuthenticationPrincipal 어노테이션을 통해 인증된 사용자 정보에 접근 가능
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        // 7. 다음 필터로 요청 전달
        filterChain.doFilter(request, response);
    }

    /**
     * 요청에서 JWT 토큰을 추출하는 메소드
     * @param request HTTP 요청 객체
     * @return 추출된 JWT 토큰 문자열, 없으면 null
     */
    private String getTokenFromRequest(HttpServletRequest request) {
        // Authorization 헤더에서 Bearer 토큰 형식으로 JWT 토큰 추출
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7); // "Bearer " 이후의 토큰 부분 반환
        }
        return null; // 토큰이 없으면 null 반환
    }
}

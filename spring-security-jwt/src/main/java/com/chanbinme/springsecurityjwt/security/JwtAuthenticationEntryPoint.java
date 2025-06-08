package com.chanbinme.springsecurityjwt.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

/**
 * JWT 인증 실패 시 처리할 엔트리 포인트 클래스
 * - 인증되지 않은 사용자가 보호된 리소스에 접근하려고 할 때 호출됨
 * - HTTP 응답 상태 코드와 메시지를 설정하여 클라이언트에게 인증 실패를 알림
 */
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    /**
     * 인증 실패 시 호출되는 메소드
     * - HTTP 401 Unauthorized 상태 코드를 설정하고, 클라이언트에게 인증 실패 메시지를 전송
     *
     * @param request       HTTP 요청 객체
     * @param response      HTTP 응답 객체
     * @param authException 인증 예외 객체
     */
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        // 응답 헤더 설정
        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401 Unauthorized 상태 코드 설정

        // 에러 Response Body 설정
        HashMap<String, Object> body = new HashMap<>();
        body.put("status", HttpServletResponse.SC_UNAUTHORIZED);
        body.put("error", "Unauthorized");
        body.put("message", "인증이 필요합니다. 유효한 JWT 토큰을 제공해주세요.");
        body.put("path", request.getServletPath());

        // JSON 형태로 응답 본문 작성
        response.getWriter().write(body.toString());
    }
}

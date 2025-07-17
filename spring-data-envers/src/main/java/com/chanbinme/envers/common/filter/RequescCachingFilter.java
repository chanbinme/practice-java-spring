package com.chanbinme.envers.common.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;

/**
 * 요청을 ContentCachingRequestWrapper로 래핑하여 요청 본문을 캐싱하는 필터.
 * 이 필터는 요청 본문을 읽을 수 있도록 하여, 이후의 필터나 컨트롤러에서 요청 본문에 접근할 수 있게 한다.
 */
@Component
@Order(1)
public class RequescCachingFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        ContentCachingRequestWrapper wrappedRequest = new ContentCachingRequestWrapper(request);
        filterChain.doFilter(wrappedRequest, response);
    }
}

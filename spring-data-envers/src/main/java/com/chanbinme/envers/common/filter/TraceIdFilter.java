package com.chanbinme.envers.common.filter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.apache.logging.log4j.ThreadContext;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;

/**
 * 요청 본문에서 traceId를 추출하는 필터.
 * 이 필터는 ContentCachingRequestWrapper를 사용하여 요청 본문을 읽고, traceId를 추출하여 로그에 출력한다.
 * 하지만 request 본문이 아직 읽히지 않은 상태에서 이 필터가 호출되기 때문에, getContentAsByteArray()를 호출해도 빈 배열을 반환한다.
 */
@Component
public class TraceIdFilter extends OncePerRequestFilter {

    private static final String TRACE_ID_FIELD = "traceId";

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (request instanceof ContentCachingRequestWrapper cachingRequest) {
            // 요청의 ContentCachingRequestWrapper를 사용하여 요청 본문을 읽을 수 있다.
            String requestBody = new String(cachingRequest.getContentAsByteArray(), cachingRequest.getCharacterEncoding());

            // 요청 본문에서 traceId를 추출한다.
            String traceId = extractTraceId(requestBody);

            // traceId를 로그에 출력하거나 다른 용도로 사용할 수 있습니다.
            System.out.println("############### Trace ID Filter Start ###############");
            System.out.println("Trace ID: " + traceId);
            System.out.println("############### Trace ID Filter End ###############");
        }

        filterChain.doFilter(request, response);
    }

    private String extractTraceId(String requestBody) throws JsonProcessingException {
        JsonNode node = objectMapper.readTree(requestBody);
        if (node.has(TRACE_ID_FIELD)) {
            return node.get(TRACE_ID_FIELD).asText();
        }
        return null;
    }
}

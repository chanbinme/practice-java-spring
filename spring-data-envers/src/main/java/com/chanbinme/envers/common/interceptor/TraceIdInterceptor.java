package com.chanbinme.envers.common.interceptor;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.util.ContentCachingRequestWrapper;

/**
 * 요청에서 traceId를 추출하는 인터셉터.
 * ContentCachingRequestWrapper로 감싼다고 해서 body를 읽을 수 있는 것은 아니다.
 * body가 한 번도 읽히지 않았다면, getContentAsByteArray()는 빈 배열을 반환한다.
 * 일반적으로 body가 읽히는 시점은 @RequestBody가 있는 컨트롤러 메소드가 호출될 때이다.
 */
@Component
@RequiredArgsConstructor
public class TraceIdInterceptor implements HandlerInterceptor {

    private static final String TRACE_ID_FIELD = "traceId";

    private final ObjectMapper objectMapper;

    /**
     * 요청이 컨트롤러에 도달하기 전에 호출되는 메소드.
     * 아직 @RequestBoyd를 호출하지 않았기 때문에 ContentCachingRequestWrapper를 사용하여 요청 본문을 읽을 수 없다.
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (request instanceof ContentCachingRequestWrapper cachingRequest) {
            // 요청의 ContentCachingRequestWrapper를 사용하여 요청 본문을 읽을 수 있다.
            String requestBody = new String(cachingRequest.getContentAsByteArray(), cachingRequest.getCharacterEncoding());

            // 요청 본문에서 traceId를 추출한다.
            String traceId = extractTraceId(requestBody);

            // traceId를 로그에 출력하거나 다른 용도로 사용할 수 있습니다.
            System.out.println("############### Trace ID Interceptor.preHandle Start ###############");
            System.out.println("Trace ID: " + traceId);
            System.out.println("############### Trace ID Interceptor.preHandle End ###############");
        }

        return true;
    }

    /**
     * 컨트롤러 메소드가 호출된 후에 호출되는 메소드.
     * 이 시점에서는 @RequestBody가 호출되어 ContentCachingRequestWrapper가 요청 본문을 캐싱했기 때문에, 본문을 읽을 수 있다.
     * 하지만 예외가 발생하면 이 메소드는 호출되지 않는다.
     */
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        if (request instanceof ContentCachingRequestWrapper cachingRequest) {
            // 요청의 ContentCachingRequestWrapper를 사용하여 요청 본문을 읽을 수 있다.
            String requestBody = new String(cachingRequest.getContentAsByteArray(), cachingRequest.getCharacterEncoding());

            // 요청 본문에서 traceId를 추출한다.
            String traceId = extractTraceId(requestBody);

            // traceId를 로그에 출력하거나 다른 용도로 사용할 수 있습니다.
            System.out.println("############### Trace ID Interceptor.postHandle Start ###############");
            System.out.println("Trace ID: " + traceId);
            System.out.println("############### Trace ID Interceptor.postHandle End ###############");
        }
    }

    /**
     * 컨트롤러 메소드가 호출된 후에 호출되는 메소드.
     * 이 시점에서는 @RequestBody가 호출되어 ContentCachingRequestWrapper가 요청 본문을 캐싱했기 때문에, 본문을 읽을 수 있다.
     * afterCompletion 메소드는 요청 처리 후에 항상 호출되며, 예외가 발생하더라도 호출된다.
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        if (request instanceof ContentCachingRequestWrapper cachingRequest) {
            // 요청의 ContentCachingRequestWrapper를 사용하여 요청 본문을 읽을 수 있다.
            String requestBody = new String(cachingRequest.getContentAsByteArray(), cachingRequest.getCharacterEncoding());

            // 요청 본문에서 traceId를 추출한다.
            String traceId = extractTraceId(requestBody);

            // traceId를 로그에 출력하거나 다른 용도로 사용할 수 있습니다.
            System.out.println("############### Trace ID Interceptor.afterCompletion Start ###############");
            System.out.println("Trace ID: " + traceId);
            System.out.println("############### Trace ID Interceptor.afterCompletion End ###############");
        }
    }

    private String extractTraceId(String requestBody) throws JsonProcessingException {
        JsonNode node = objectMapper.readTree(requestBody);
        if (node.has(TRACE_ID_FIELD)) {
            return node.get(TRACE_ID_FIELD).asText();
        }
        return null;
    }
}

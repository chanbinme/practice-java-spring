package com.chanbinme.envers.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.CommonsRequestLoggingFilter;

@Configuration
public class RequestLoggingConfig {

    @Bean
    public CommonsRequestLoggingFilter requestLoggingFilter() {
        CommonsRequestLoggingFilter filter = new CommonsRequestLoggingFilter();
        filter.setIncludeClientInfo(false);  // IP 주소 및 사용자 에이전트 정보 포함
        filter.setIncludeQueryString(false); // 쿼리 문자열 포함
        filter.setIncludePayload(true); // 요청 페이로드 포함
        filter.setMaxPayloadLength(10000); // 최대 페이로드 길이 설정
        filter.setIncludeHeaders(false); // 헤더 정보 포함
        filter.setAfterMessagePrefix("Request Data: "); // 로그 메시지 접두사 설정

        return filter;
    }
}

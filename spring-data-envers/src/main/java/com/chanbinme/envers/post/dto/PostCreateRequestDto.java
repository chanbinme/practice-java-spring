package com.chanbinme.envers.post.dto;

public record PostCreateRequestDto(
    String traceId,
    String title,
    String content
) { }
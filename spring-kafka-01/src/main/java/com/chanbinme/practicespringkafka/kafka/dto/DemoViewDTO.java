package com.chanbinme.practicespringkafka.kafka.dto;

import lombok.Builder;

@Builder
public record DemoViewDTO(String name, int age, String id) {
}
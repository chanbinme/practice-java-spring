package com.chanbinme.practice02.kafka.dto;

import lombok.Builder;

@Builder
public record DemoViewDTO2(String name, int age, String id) {
}
package com.chanbinme.springbatch.domain.transaction;

import lombok.Getter;

@Getter
public enum TransactionStatus {
    SUCCESS("성공"),
    FAILURE("실패"),
    PENDING("대기중"),
    CANCELLED("취소됨");

    private final String description;

    TransactionStatus(String description) {
        this.description = description;
    }
}

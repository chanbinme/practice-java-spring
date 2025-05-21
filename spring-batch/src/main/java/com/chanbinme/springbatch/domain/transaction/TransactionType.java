package com.chanbinme.springbatch.domain.transaction;

import lombok.Getter;

@Getter
public enum TransactionType {
    DEPOSIT("입금"),
    WITHDRAWAL("출금"),
    TRANSFER("이체"),
    PAYMENT("결제"),
    REFUND("환불");

    private final String description;

    TransactionType(String description) {
        this.description = description;
    }
}

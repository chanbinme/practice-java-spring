package com.chanbinme.springbatch.domain.aggregation;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class DailyTransactionSummary {

    @EmbeddedId
    private DailyTransactionSummaryId id;
    private Long transactionAmount;
    private Long transactionCount;
}

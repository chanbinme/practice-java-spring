package com.chanbinme.springbatch.domain.aggregation;

import static jakarta.persistence.GenerationType.IDENTITY;

import com.chanbinme.springbatch.domain.transaction.TransactionStatus;
import com.chanbinme.springbatch.domain.transaction.TransactionType;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class DailyTransactionSummary {

    @EmbeddedId
    private DailyTransactionSummaryId id;
    private Long transactionAmount;
    private Long transactionCount;
    private Long transactionSuccessCount;
    private Long transactionFailureCount;
    private Long transactionPendingCount;
    private Long transactionCancelledCount;
    private Long transactionSuccessAmount;
    private Long transactionFailureAmount;
    private Long transactionPendingAmount;
    private Long transactionCancelledAmount;
}

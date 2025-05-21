package com.chanbinme.springbatch.domain.aggregation;

import org.springframework.data.jpa.repository.JpaRepository;

public interface DailyTransactionSummaryRepository extends JpaRepository<DailyTransactionSummary, DailyTransactionSummaryId> {
}

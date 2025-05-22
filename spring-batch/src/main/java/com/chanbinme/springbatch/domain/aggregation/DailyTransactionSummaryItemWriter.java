package com.chanbinme.springbatch.domain.aggregation;

import com.chanbinme.springbatch.domain.transaction.Transaction;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JdbcBatchItemWriter;

@RequiredArgsConstructor
public class DailyTransactionSummaryItemWriter implements ItemWriter<Transaction> {

    private final JdbcBatchItemWriter<DailyTransactionSummary> jdbcBatchItemWriter;

    @Override
    public void write(Chunk<? extends Transaction> chunk) throws Exception {
        // stream() 메서드를 사용해 저장할 transactionAmount, transactionCount를 집계해서 DailyTransactionSummaryList를 생성한다.
        List<DailyTransactionSummary> dailyTransactionSummaryList = chunk.getItems().stream()
            .collect(Collectors.groupingBy(
                transaction -> DailyTransactionSummaryId.builder()
                    .transactionDate(LocalDate.now().minusDays(1))
                    .transactionType(transaction.getTransactionType().name())
                    .build(),
                Collectors.summarizingLong(Transaction::getTransactionAmount)
            ))
            .entrySet()
            .stream()
            .map(entry -> DailyTransactionSummary.builder()
                .id(entry.getKey())
                .transactionAmount(entry.getValue().getSum())
                .transactionCount(entry.getValue().getCount())
                .build())
            .collect(Collectors.toList());

        // jdbcBatchItemWriter를 사용해 DailyTransactionSummaryList를 DB에 저장한다.
        jdbcBatchItemWriter.write(new Chunk<>(dailyTransactionSummaryList));
    }
}

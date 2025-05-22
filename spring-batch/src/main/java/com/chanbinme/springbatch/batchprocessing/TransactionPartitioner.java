package com.chanbinme.springbatch.batchprocessing;

import com.chanbinme.springbatch.domain.transaction.TransactionRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.item.ExecutionContext;

@RequiredArgsConstructor
public class TransactionPartitioner implements Partitioner {

    private final TransactionRepository transactionRepository;

    @Override
    public Map<String, ExecutionContext> partition(int gridSize) {
        LocalDateTime startDate = LocalDate.now().minusDays(2).atStartOfDay();
        LocalDateTime endDate = LocalDate.now().atStartOfDay();

        Long min = transactionRepository.findIdByTransactionDateGreaterThanEqualAndTransactionDateLessThanOrderByIdAsc(startDate, endDate);
        Long max = transactionRepository.findIdByTransactionDateGreaterThanEqualAndTransactionDateLessThanOrderByIdDesc(startDate, endDate);

        if (min == null || max == null) {
            throw new IllegalStateException("Transaction IDs not found for the given date range.");
        }

        long targetSize = (max - min) / gridSize + 1;

        Map<String, ExecutionContext> result = new HashMap<>();
        long number = 0;
        long start = min;
        long end = start + targetSize - 1;

        while (start <= max) {
            ExecutionContext context = new ExecutionContext();
            result.put("partition" + number, context);

            if (end >= max) {
                end = max;
            }

            context.putLong("minId", start);
            context.putLong("maxId", end);
            start = end + 1;
            end = Math.min(end + targetSize, max);
            number++;
        }

        return result;
    }
}

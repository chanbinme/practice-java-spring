package com.chanbinme.springbatch.batchprocessing;

import com.chanbinme.springbatch.domain.aggregation.DailyTransactionSummary;
import com.chanbinme.springbatch.domain.aggregation.DailyTransactionSummaryItemWriter;
import com.chanbinme.springbatch.domain.transaction.Transaction;
import jakarta.persistence.EntityManagerFactory;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;
import javax.sql.DataSource;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

/**
 * Spring Batch의 배치 작업을 설정하는 클래스이다.
 * Spring Batch는 대량의 데이터를 처리하기 위한 프레임워크이다.
 * 배치 작업은 주기적으로 실행되는 작업으로, 대량의 데이터를 처리하는 데 사용된다.
 * 이 클래스에서는 JpaPagingItemReader, ItemProcessor, ItemWriter를 설정한다.
 */
@Configuration
@RequiredArgsConstructor
public class BatchConfiguration {

    private final EntityManagerFactory entityManagerFactory;
    private static final int CHUNK_SIZE = 100;

    /**
     * 전 날의 Transaction을 읽어오는 JpaPagingItemReader를 생성하는 메서드
     */
    @Bean
    public JpaPagingItemReader<Transaction> reader() {
        LocalDate today = LocalDate.now().minusDays(1);
        LocalDateTime start = today.atStartOfDay();
        LocalDateTime end = start.plusDays(1);
        Map<String, Object> parameterValues = Map.of("start", start, "end", end);

        return new JpaPagingItemReaderBuilder<Transaction>()
            .name("transactionItemReader")
            .entityManagerFactory(entityManagerFactory)
            .pageSize(CHUNK_SIZE)
            .parameterValues(parameterValues)
            .queryString("SELECT t FROM Transaction t WHERE t.transactionDate >= :start AND t.transactionDate < :end")
            .build();
    }

    public ItemProcessor<Transaction, Transaction> processor() {
        return null;
    }

    /**
     * ItemWriter는 배치 작업에서 데이터를 처리하는 역할을 한다.
     * ItemWriter는 Chunk 단위로 데이터를 처리한다.
     */
    @Bean(value = "dailyTransactionSummaryItemWriter")
    public ItemWriter<Transaction> dailyTransactionSummaryItemWriter(JdbcBatchItemWriter<DailyTransactionSummary> jdbcBatchItemWriter) {
        return new DailyTransactionSummaryItemWriter(jdbcBatchItemWriter);
    }

    /**
     * JdbcBatchItemWriter는 JDBC를 사용하여 데이터베이스에 데이터를 쓰는 ItemWriter이다.
     * JdbcBatchItemWriterBuilder를 사용하여 JdbcBatchItemWriter를 생성한다.
     */
    @Bean(value = "jdbcBatchItemWriter")
    public JdbcBatchItemWriter<DailyTransactionSummary> jdbcBatchItemWriter(DataSource dataSource) {
        return new JdbcBatchItemWriterBuilder<DailyTransactionSummary>()
            .dataSource(dataSource)
            .sql("""
                INSERT INTO daily_transaction_summary (transaction_date, transaction_type, transaction_amount, transaction_count)
                VALUES (:id.transactionDate, :id.transactionType, :transactionAmount, :transactionCount)
                ON DUPLICATE KEY UPDATE
                    transaction_amount = transaction_amount + VALUES(transaction_amount),
                    transaction_count = transaction_count + VALUES(transaction_count)
                """)
            .beanMapped()
            .build();
    }

    /**
     * JobRepository를 사용하여 Job을 생성하는 메서드
     * JobRepository는 배치 작업을 관리하는 인터페이스이다.
     * JobRepositoryFactoryBean을 사용하여 JobRepository를 생성한다.
     */
    @Bean
    public Job job(JobRepository jobRepository, Step step1, JdbcCompletionNotificationListener listener) {
        return new JobBuilder("importUserJob", jobRepository)
            .listener(listener)
            .start(step1)
            .build();
    }

    /**
     * JobRepository를 사용하여 Step을 생성하는 메서드
     * Step은 배치 작업의 단위이다.
     */
    @Bean
    public Step step1(JobRepository jobRepository, PlatformTransactionManager transactionManager,
        JpaPagingItemReader<Transaction> reader,
        ItemWriter<Transaction> writer) {
        return new StepBuilder("step1", jobRepository)
            .<Transaction, Transaction>chunk(CHUNK_SIZE, transactionManager)
            .reader(reader)
            .writer(writer)
            .build();
    }
}

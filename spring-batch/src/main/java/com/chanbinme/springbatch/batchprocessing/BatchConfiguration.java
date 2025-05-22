package com.chanbinme.springbatch.batchprocessing;

import com.chanbinme.springbatch.domain.aggregation.DailyTransactionSummary;
import com.chanbinme.springbatch.domain.aggregation.DailyTransactionSummaryItemWriter;
import com.chanbinme.springbatch.domain.transaction.Transaction;
import com.chanbinme.springbatch.domain.transaction.TransactionRepository;
import jakarta.persistence.EntityManagerFactory;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;
import javax.sql.DataSource;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.core.partition.support.TaskExecutorPartitionHandler;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
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

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final EntityManagerFactory entityManagerFactory;
    private final TransactionRepository transactionRepository;
    private static final int CHUNK_SIZE = 100;
    private static final int POOL_SIZE = 10;

    /**
     * ThreadPoolTaskExecutor는 멀티 스레드로 배치 작업을 처리하기 위한 Executor이다.
     * ThreadPoolTaskExecutor는 ThreadPoolTaskScheduler를 사용하여 스레드를 관리한다.
     */
    @Bean
    public TaskExecutor executor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(POOL_SIZE);
        executor.setMaxPoolSize(POOL_SIZE);
        executor.setQueueCapacity(POOL_SIZE);
        executor.setThreadNamePrefix("batch-thread-");
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.initialize();

        return executor;
    }

    @Bean
    public TaskExecutorPartitionHandler partitionHandler(Step workerStep) {
        TaskExecutorPartitionHandler partitionHandler = new TaskExecutorPartitionHandler();
        partitionHandler.setGridSize(POOL_SIZE);
        partitionHandler.setTaskExecutor(executor());
        partitionHandler.setStep(workerStep);
        return partitionHandler;
    }

    /**
     * Partitioner는 배치 작업을 여러 개의 파트로 나누는 역할을 한다.
     * Partitioner는 PartitionHandler와 함께 사용된다.
     * PartitionHandler는 Partitioner가 나눈 파트를 실행하는 역할을 한다.
     */
    @Bean
    public TransactionPartitioner partitioner() {
        return new TransactionPartitioner(transactionRepository);
    }

    /**
     * JobRepository는 배치 작업을 관리하는 인터페이스이다.
     * JobRepositoryFactoryBean을 사용하여 JobRepository를 생성한다.
     */
    @Bean(name = "managerStep")
    public Step managerStep(JobRepository jobRepository, Step workerStep) {
        return new StepBuilder("managerStep", jobRepository)
            .partitioner("workerStep", partitioner())
            .step(workerStep)
            .gridSize(POOL_SIZE)
            .taskExecutor(executor())
            .build();
    }

    /**
     * 전 날의 Transaction을 읽어오는 JpaPagingItemReader를 생성하는 메서드
     */
    @Bean
    @StepScope
    public JpaPagingItemReader<Transaction> reader(
        @Value("#{stepExecutionContext['minId']}") Long minId,
        @Value("#{stepExecutionContext['maxId']}") Long maxId
    ) {
        Map<String, Object> parameterValues = Map.of("minId", minId, "maxId", maxId);

        return new JpaPagingItemReaderBuilder<Transaction>()
            .name("transactionItemReader")
            .entityManagerFactory(entityManagerFactory)
            .pageSize(CHUNK_SIZE)
            .parameterValues(parameterValues)
            .queryString("SELECT t FROM Transaction t WHERE t.id BETWEEN :minId AND :maxId")
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
    @StepScope
    public ItemWriter<Transaction> dailyTransactionSummaryItemWriter(JdbcBatchItemWriter<DailyTransactionSummary> jdbcBatchItemWriter) {
        return new DailyTransactionSummaryItemWriter(jdbcBatchItemWriter);
    }

    /**
     * JdbcBatchItemWriter는 JDBC를 사용하여 데이터베이스에 데이터를 쓰는 ItemWriter이다.
     * JdbcBatchItemWriterBuilder를 사용하여 JdbcBatchItemWriter를 생성한다.
     */
    @Bean(value = "jdbcBatchItemWriter")
    @StepScope
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
    public Job job(JobRepository jobRepository, Step managerStep, JdbcCompletionNotificationListener listener) {
        return new JobBuilder("importUserJob", jobRepository)
            .listener(listener)
            .start(managerStep)
            .build();
    }

    /**
     * JobRepository를 사용하여 Step을 생성하는 메서드
     * Step은 배치 작업의 단위이다.
     */
    @Bean(name = "workerStep")
    public Step workerStep(
        JpaPagingItemReader<Transaction> reader,
        ItemWriter<Transaction> dailyTransactionSummaryItemWriter) {
        return new StepBuilder("workerStep", jobRepository)
            .<Transaction, Transaction>chunk(CHUNK_SIZE, transactionManager)
            .reader(reader)
            .writer(dailyTransactionSummaryItemWriter)
            .build();
    }
}

package com.chanbinme.springbatch.batchprocessing;

import javax.sql.DataSource;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

@Configuration
public class BatchConfiguration {

    /**
     * FlatFileItemReader를 사용하여 CSV 파일을 읽어오는 메서드
     * FlatFileItemReader는 CSV 파일을 읽어오는 ItemReader 인터페이스를 구현한 클래스이다.
     * FlatFileItemReaderBuilder를 사용하여 FlatFileItemReader를 생성한다.
     * FlatFileItemReaderBuilder는 Builder 패턴을 사용하여 FlatFileItemReader를 생성하는 클래스이다.
     * @return FlatFileItemReader<Person>
     */
    @Bean
    public FlatFileItemReader<Person> reader() {
        return new FlatFileItemReaderBuilder<Person>()
            .name("personItemReader")
            .resource(new ClassPathResource("sample-data.csv"))
            .delimited()
            .names("firstName", "lastName")
            .targetType(Person.class)
            .build();
    }

    @Bean
    public PersonItemProcessor processor() {
        return new PersonItemProcessor();
    }

    /**
     * JdbcBatchItemWriter를 사용하여 DB에 데이터를 저장하는 메서드
     * JdbcBatchItemWriter는 DB에 데이터를 저장하는 ItemWriter 인터페이스를 구현한 클래스이다.
     * JdbcBatchItemWriterBuilder를 사용하여 JdbcBatchItemWriter를 생성한다.
     * JdbcBatchItemWriterBuilder는 Builder 패턴을 사용하여 JdbcBatchItemWriter를 생성하는 클래스이다.
     * @param dataSource DataSource
     * @return JdbcBatchItemWriter<Person>
     */
    @Bean
    public JdbcBatchItemWriter<Person> writer(DataSource dataSource) {
        return new JdbcBatchItemWriterBuilder<Person>()
            .sql("INSERT INTO people (first_name, last_name) VALUES (:firstName, :lastName)")
            .dataSource(dataSource)
            .beanMapped()
            .build();
    }

    /**
     * JobRepository를 사용하여 Job을 생성하는 메서드
     * JobRepository는 배치 작업을 관리하는 인터페이스이다.
     * JobRepositoryFactoryBean을 사용하여 JobRepository를 생성한다.
     * @param jobRepository JobRepository
     * @param step1 Step
     * @param listener JdbcCompletionNotificationListener
     * @return Job
     */
    @Bean
    public Job importUserJob(JobRepository jobRepository, Step step1, JdbcCompletionNotificationListener listener) {
        return new JobBuilder("importUserJob", jobRepository)
            .listener(listener)
            .start(step1)
            .build();
    }

    /**
     * Step을 생성하는 메서드
     * Step은 배치 작업의 단위이다.
     * StepBuilder를 사용하여 Step을 생성한다.
     * @param jobRepository JobRepository
     * @param transactionManager DataSourceTransactionManager
     * @param reader FlatFileItemReader<Person>
     * @param processor PersonItemProcessor
     * @param writer JdbcBatchItemWriter<Person>
     * @return Step
     */
    @Bean
    public Step step1(JobRepository jobRepository, DataSourceTransactionManager transactionManager,
        FlatFileItemReader<Person> reader,
        PersonItemProcessor processor, JdbcBatchItemWriter<Person> writer) {
        return new StepBuilder("step1", jobRepository)
            .<Person, Person>chunk(3, transactionManager)
            .reader(reader)
            .processor(processor)
            .writer(writer)
            .build();
    }
}

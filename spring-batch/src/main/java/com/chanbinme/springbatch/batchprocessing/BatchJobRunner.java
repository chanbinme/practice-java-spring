package com.chanbinme.springbatch.batchprocessing;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * CommandLineRunner 인터페이스를 구현한 클래스
 * Spring Boot 애플리케이션이 시작될 때 실행되는 코드를 정의할 수 있다.
 * run() 메서드에서 배치 작업을 실행한다.
 */
@Component
@RequiredArgsConstructor
public class BatchJobRunner implements CommandLineRunner {

    private final JobLauncher jobLauncher;
    private final Job importUserJob;

    @Override
    public void run(String... args) throws Exception {
        JobParameters jobParameters = new JobParametersBuilder()
            .addLong("run.id", System.currentTimeMillis())
            .toJobParameters();
        jobLauncher.run(importUserJob, jobParameters);
    }
}
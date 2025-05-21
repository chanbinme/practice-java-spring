package com.chanbinme.springbatch.batchprocessing;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

/**
 * JobExecutionListener 인터페이스를 구현한 클래스
 * JobExecutionListener는 배치 작업의 시작과 끝에 대한 이벤트를 처리하는 역할을 한다.
 * 배치 작업이 시작되기 전에 수행할 작업과 배치 작업이 끝난 후에 수행할 작업을 정의할 수 있다.
 * 배치 작업이 끝난 후에 수행할 작업은 afterJob() 메서드에서 정의한다.
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class JdbcCompletionNotificationListener implements JobExecutionListener {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void afterJob(JobExecution jobExecution) {
        if (jobExecution.getStatus() == BatchStatus.COMPLETED) {
            log.info("!!! JOB FINISHED! Time to verify the results");
        }
    }

}

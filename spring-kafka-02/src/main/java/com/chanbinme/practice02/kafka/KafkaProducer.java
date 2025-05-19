package com.chanbinme.practice02.kafka;

import com.chanbinme.practice02.kafka.dto.DemoViewDTO1;
import com.chanbinme.practice02.kafka.dto.DemoViewDTO2;
import java.util.concurrent.CompletableFuture;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@Data
public class KafkaProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void sendPayload(String topic, Object payload) {
        CompletableFuture<SendResult<String, Object>> future = kafkaTemplate.send(topic, payload);
        log.info("Sending message: {}", payload);
        future.whenComplete((result, ex) -> {
            if (ex != null) {
                log.info("Error sending message: {}", ex.getMessage());
            } else {
                log.info("Message sent successfully: {}", result.getRecordMetadata().offset());
            }
        });
    }
}

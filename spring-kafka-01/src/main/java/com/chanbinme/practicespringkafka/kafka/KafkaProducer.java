package com.chanbinme.practicespringkafka.kafka;

import com.chanbinme.practicespringkafka.kafka.dto.DemoViewDTO;
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

    private final KafkaTemplate<String, DemoViewDTO> kafkaTemplate;

    public void sendMessage(String message, DemoViewDTO payload) {
        CompletableFuture<SendResult<String, DemoViewDTO>> future = kafkaTemplate.send(message, payload);
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

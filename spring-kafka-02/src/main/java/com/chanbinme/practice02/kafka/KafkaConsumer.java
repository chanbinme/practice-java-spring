package com.chanbinme.practice02.kafka;

import com.chanbinme.practice02.kafka.dto.DemoViewDTO1;
import com.chanbinme.practice02.kafka.dto.DemoViewDTO2;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Getter
@Setter
@KafkaListener(groupId = "chanbinme-groupId", topics = "chanbinme-topic")
public class KafkaConsumer {

    private CountDownLatch latch = new CountDownLatch(10);
    private List<Object> payloads = new ArrayList<>();

    @KafkaHandler
    public void handleDTO1(DemoViewDTO1 dto, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
        log.info("Received message from topic {}: {}", topic, dto);
        payloads.add(dto);
        latch.countDown();
    }

    @KafkaHandler
    public void handleDTO2(DemoViewDTO2 dto, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
        log.info("Received message from topic {}: {}", topic, dto);
        payloads.add(dto);
        latch.countDown();
    }

    @KafkaHandler(isDefault = true)
    public void handleDefault(Object dto) {
        log.info("Received unknown type: {}", dto);
        payloads.add(dto);
        latch.countDown();
    }
}

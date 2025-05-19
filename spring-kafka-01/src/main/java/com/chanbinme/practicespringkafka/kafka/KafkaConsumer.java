package com.chanbinme.practicespringkafka.kafka;

import com.chanbinme.practicespringkafka.kafka.dto.DemoViewDTO;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@Data
public class KafkaConsumer {

    private CountDownLatch latch = new CountDownLatch(10);
    private List<DemoViewDTO> payloads = new ArrayList<>();
    private DemoViewDTO payload;

    // record를 수신하기 위한 consumer 설정
    @KafkaListener(topics = "chanbinme-topic", containerFactory = "filteredKafkaListenerContainerFactory")
    public void receive(ConsumerRecord<String, DemoViewDTO> consumerRecord) {
        payload = consumerRecord.value();
        log.info("Received message: {}", payload);
        payloads.add(payload);
        latch.countDown();
    }

    public void resetLatch() {
        latch = new CountDownLatch(1);
    }
}

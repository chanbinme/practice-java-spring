package com.chanbinme.practicespringkafka.kafka;

import static org.junit.jupiter.api.Assertions.*;

import com.chanbinme.practicespringkafka.kafka.dto.DemoViewDTO;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.context.EmbeddedKafka;

@SpringBootTest
@EmbeddedKafka(partitions = 1,
    brokerProperties = {"listeners=PLAINTEXT://localhost:9092"},
    ports = 9092)
class KafkaConsumerTest {

    @Autowired
    private KafkaConsumer kafkaConsumer;

    @Autowired
    private KafkaProducer kafkaProducer;

    @Test
    void giveEmbeddedKafkaBroker_whenSendingWithSimpleProducer_thenMessageReceived() throws Exception {
        String topic = "chanbinme-topic";
        DemoViewDTO payload = DemoViewDTO.builder()
            .id("temporary_id_001")
            .name("김찬빈")
            .age(32)
            .build();
        DemoViewDTO payload2 = DemoViewDTO.builder()
            .id("temporary_id_001")
            .name("김현경")
            .age(30)
            .build();

        int testCount = 0;
        for (int i = 0; i < 10; i++) {
            if (testCount % 2 == 0) {
                kafkaProducer.sendMessage(topic, payload);
            } else {
                kafkaProducer.sendMessage(topic, payload2);
            }
            testCount++;
        }

        // 모든 메시지를 수신할 때까지 기다림
        kafkaConsumer.getLatch().await(10, TimeUnit.SECONDS);

        System.out.println("========================================================");
        System.out.println(kafkaConsumer.getPayloads().size());
        System.out.println(kafkaConsumer.getPayloads());
    }
}
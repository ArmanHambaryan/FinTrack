package service.impl;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaProducerService {

    private final KafkaTemplate<String, String> kafkaTemplate;

    // Spring-ը ավտոմատ ներարկում (inject) է KafkaTemplate-ը
    public KafkaProducerService(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendMessage(String topic, String message) {
        System.out.println("-> Ուղարկվում է նամակ Kafka-ին: " + message);
        kafkaTemplate.send(topic, message);
    }
}
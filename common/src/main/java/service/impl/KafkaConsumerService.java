package service.impl;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaConsumerService {

    // Այս annotation-ը ստիպում է մեթոդին անդադար լսել "test-topic" անունով թեման
    @KafkaListener(topics = "test-topic", groupId = "fintrack-group")
    public void consume(String message) {
        System.out.println("<- Կարդացվեց նոր նամակ Kafka-ից: " + message);
    }
}
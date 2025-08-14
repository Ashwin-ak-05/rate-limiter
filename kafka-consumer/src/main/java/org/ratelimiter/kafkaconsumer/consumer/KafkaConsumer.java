package org.ratelimiter.kafkaconsumer.consumer;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaConsumer {
    @KafkaListener(topics = "ratelimiter-logs", groupId = "ratelimiter-group")
    public void consume(ConsumerRecord<String, String> record) {
        System.out.println("Received throttled log: " + record.value());
    }
}

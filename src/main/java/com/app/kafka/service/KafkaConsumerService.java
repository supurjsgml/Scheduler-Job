package com.app.kafka.service;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class KafkaConsumerService {

//    @KafkaListener(topics = "QuartzJob", groupId = "${spring.kafka.consumer.group-id}")
    public void consume(String message) {
        log.info("kafka message : {}", message);
    }
}
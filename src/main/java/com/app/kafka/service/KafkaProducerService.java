package com.app.kafka.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaProducerService {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public <T> void sendMessage(String topic, T message) {
    	log.info("Sending message : {}, topic : {}", message, topic);
        this.kafkaTemplate.send(topic, message);
    }
}
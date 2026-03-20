package com.app.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.support.converter.JsonMessageConverter;
import org.springframework.kafka.support.converter.RecordMessageConverter;

@Configuration
public class KafkaConfig {
	
    @Bean
    public RecordMessageConverter converter() {
        return new JsonMessageConverter(); //파라미터 타입을 보고 알잘딱으로 변환
    }
}
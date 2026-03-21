package com.app.config;

import org.apache.kafka.common.TopicPartition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.converter.JsonMessageConverter;
import org.springframework.kafka.support.converter.RecordMessageConverter;
import org.springframework.util.backoff.FixedBackOff;

@Configuration
public class KafkaConfig {
	
    @Bean
    public RecordMessageConverter converter() {
        return new JsonMessageConverter(); //파라미터 타입을 보고 알잘딱으로 변환
    }
    
    @Bean
    public DefaultErrorHandler errorHandler(KafkaTemplate<String, Object> template) {
        //재시도 및 DLT 전송 설정
        DeadLetterPublishingRecoverer recoverer = new DeadLetterPublishingRecoverer(template, (failMsgInfo, ex) -> new TopicPartition(failMsgInfo.topic().concat(".DLT"), failMsgInfo.partition()));

        //재시도 횟수 설정
        FixedBackOff backOff = new FixedBackOff(5000L, 3L);

        return new DefaultErrorHandler(recoverer, backOff);
    }
}
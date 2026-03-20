package com.app.kafka.service;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.app.job.jobKorea.dto.req.MemberReqDTO;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class KafkaConsumerService {

//	@KafkaListener(topics = "restApi")
	public void restApiConsume(MemberReqDTO memberReqDTO) {
		log.info("restApi kafka message : {}", memberReqDTO);
	}

//    @KafkaListener(topics = "QuartzJob")
    public void quartzJobConsume(String message) {
        log.info("QuartzJob kafka message : {}", message);
    }
}
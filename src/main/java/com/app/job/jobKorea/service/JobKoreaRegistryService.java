package com.app.job.jobKorea.service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;

import com.app.job.jobKorea.dto.req.MemberReqDTO;

@Service
public class JobKoreaRegistryService {
	
	//로컬 메모리좀 빌리마
    private static final Map<String, MemberReqDTO> userStore = new ConcurrentHashMap<>();
    
    public void registerUser(MemberReqDTO dto) {
        userStore.put(dto.getId(), dto);
    }

    public MemberReqDTO getUser(String userId) {
        return userStore.get(userId);
    }
}
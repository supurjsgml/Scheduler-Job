package com.app.grafana;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;  
import java.lang.management.*;  
import java.util.*;  

@RestController  
public class ThreadInfoController {  

    @GetMapping(value = "/actuator/threads", produces = MediaType.APPLICATION_JSON_VALUE)  
    public List<Map<String, Object>> threads() {  
        return Arrays.stream(  
            ManagementFactory.getThreadMXBean().dumpAllThreads(false, false))
            .map(t -> {
            	Map<String, Object> map = new HashMap<>();  
                map.put("name", t.getThreadName());  
                map.put("state", t.getThreadState().name());  
                map.put("daemon", t.isDaemon());  
                return map; 
            })
            .toList();
    }  
}  
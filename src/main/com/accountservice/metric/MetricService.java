package com.accountservice.metric;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class MetricService {	
	private AtomicInteger requestCount;
    private ConcurrentMap<Integer, Integer> statusMetric;
 
    public MetricService() {
    	requestCount = new AtomicInteger(0);    	
        statusMetric = new ConcurrentHashMap<Integer, Integer>();
    }
     
    public void increaseCount(String request, int status) {
    	requestCount.addAndGet(1);
        Integer statusCount = statusMetric.get(status);
        if (statusCount == null) {
            statusMetric.put(status, 1);
        } else {
            statusMetric.put(status, statusCount + 1);
        }
    }
 
    public StatusInfo getOverview() {
    	return new StatusInfo(statusMetric, requestCount.get());
    }
    
    @Scheduled(fixedDelay = 1000)
    public void update() {
    	requestCount.set(0);		        
    }
}

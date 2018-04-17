package com.accountservice.metric;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class StatusInfo {
	public Map<Integer, Integer> RequestStatus;
	public int RequestPerSec;
	
	public StatusInfo(Map<Integer, Integer> requestStatus, int requestPerSec) {
		RequestStatus = new ConcurrentHashMap<Integer, Integer>(requestStatus);
		RequestPerSec = requestPerSec;
	}
}

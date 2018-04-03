package com.accountservice.response;

import java.util.HashMap;
import java.util.Map;

public class ApiResult<T> extends RequestResult<T>{
	public Map<String, Object> source = new HashMap<String, Object>();
	
	public ApiResult<T> addSourceParam(String name, Object value){
		source.put(name, value);
		return this;
	}
}

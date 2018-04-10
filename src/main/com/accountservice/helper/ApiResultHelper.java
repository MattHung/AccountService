package com.accountservice.helper;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.accountservice.response.ApiResult;

public class ApiResultHelper {
	public static <T> void assigenParam(HttpServletRequest request, ApiResult<T> result) {
		Map<String, String[]> parameters = request.getParameterMap();
		for(String key : parameters.keySet()) 
			result.addSourceParam(key, parameters.get(key).length>0 ? parameters.get(key)[0]:"");		
	}
}

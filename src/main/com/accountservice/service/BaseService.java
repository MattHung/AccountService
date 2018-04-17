package com.accountservice.service;

import javax.servlet.http.HttpServletRequest;

import org.springframework.transaction.annotation.Transactional;
import com.accountservice.dao.BaseDAO;

public abstract class BaseService {
	protected abstract BaseDAO getDAO();
	
	@Transactional
	public void updateDao() {
		getDAO().update();
		getDAO().flushLogs();
	}
	
	@Transactional
	public void logRequest(HttpServletRequest request, Object result) {
		String source_ip = request.getRemoteAddr();
		String action = request.getRequestURI().substring(request.getContextPath().length());
		
		getDAO().Log(action, source_ip, result);
	}
}

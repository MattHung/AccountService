package com.accountservice.service;

import javax.servlet.http.HttpServletRequest;

import org.springframework.transaction.annotation.Transactional;
import com.accountservice.dao.BaseDAO;

public class BaseService {	
	protected BaseDAO baseDao;	
	
	public void setDao(BaseDAO dao) {
		baseDao = dao;
	}
	
	@Transactional
	public void updateDao() {
		baseDao.update();
		baseDao.flushLogs();
	}
	
	@Transactional
	public void logRequest(HttpServletRequest request, Object result) {
		String source_ip = request.getRemoteAddr();
		String action = request.getRequestURI().substring(request.getContextPath().length());
		
		baseDao.Log(action, source_ip, result);
	}
}

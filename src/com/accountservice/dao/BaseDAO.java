package com.accountservice.dao;

import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.accountservice.helper.ExceptionHelper;
import com.fasterxml.jackson.databind.ObjectMapper;


public abstract class BaseDAO {
	private Logger logger = LoggerFactory.getLogger(BaseDAO.class);
			
	@Autowired
	@Qualifier(value = "hibernate4AnnotatedSessionFactory")
	protected SessionFactory sessionFactory;
	
	protected abstract void onFlushRequestLogs(String action, String details, String source_ip, Object result);
	
	public void Log(String action, String source_ip, Object result){
		try {
			ObjectMapper mapper = new ObjectMapper();
			String details = mapper.writeValueAsString(result);
			
			onFlushRequestLogs(action, details, source_ip, result);
		}catch (Exception e) {
			String msg = ExceptionHelper.getDetails(e);			
			logger.debug(msg);	
		}		
	}
}

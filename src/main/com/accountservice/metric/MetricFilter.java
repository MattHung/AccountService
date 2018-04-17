package com.accountservice.metric;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MetricFilter implements Filter {
	@Autowired 
	MetricController metricController;
	 
	@Autowired
    private MetricService metricService;	
 
    @Override
    public void init(FilterConfig config) throws ServletException {
    }
 
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) 
      throws IOException, ServletException {    	
        HttpServletRequest httpRequest = ((HttpServletRequest) request);
        
        String req = httpRequest.getMethod() + " " + httpRequest.getRequestURI();
 
        chain.doFilter(request, response);
 
        int status = ((HttpServletResponse) response).getStatus();
        
        if(!metricController.containsPath(httpRequest.getServletPath()))        	
        	metricService.increaseCount(req, status);
    }

	@Override
	public void destroy() {
		// TODO Auto-generated method stub
		
	}
}

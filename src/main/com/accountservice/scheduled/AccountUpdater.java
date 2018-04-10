package com.accountservice.scheduled;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.accountservice.service.AccountService;

@Component
@EnableScheduling
public class AccountUpdater {
	@Autowired
	@Qualifier(value = "AccountService")
	private AccountService accountService;
	
	@Scheduled(fixedDelay = 1000)
    public void updateSession() {
		accountService.updateSession();        
    }
}

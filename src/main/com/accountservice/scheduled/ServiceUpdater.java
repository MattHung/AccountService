package com.accountservice.scheduled;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.accountservice.service.AccountService;
import com.accountservice.service.CreditService;
import com.accountservice.service.DealerService;

@Component
@EnableScheduling
public class ServiceUpdater {
	@Autowired
	private AccountService accountService;
	
	@Autowired
	private CreditService creditService;
	
	@Autowired
	private DealerService dealerService;
	
	@Scheduled(fixedDelay = 300)
    public void update() {
		accountService.updateDao();
		creditService.updateDao();
		dealerService.updateDao();
    }
}

package com.accountservice.service;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.accountservice.dao.BaseDAO;
import com.accountservice.dao.CreditDAO;
import com.accountservice.model.credit.CreditInfo;
import com.accountservice.model.credit.Transaction;
import com.accountservice.response.QueryInsertResult;

@Service(value = "CreditService")
public class CreditService extends BaseService{
	
	@Autowired
	private CreditDAO creditDAO;
	
	@Transactional
	public QueryInsertResult<CreditInfo> getCredit(int dealer_id, int user_id) {
		QueryInsertResult<CreditInfo> result = new QueryInsertResult<CreditInfo>();
		CreditInfo creditInfo = null;
		
		try {
			creditInfo = creditDAO.getCredit(dealer_id, user_id);
		}catch (Exception e) {
			return result.setData(false, creditInfo, e.getMessage());
		}
		
		if(creditInfo==null)
			return result.setData(false, creditInfo, "cannot find this user");
		
		return	result.setData(true, creditInfo, "looks good!");
	}
	
	@Transactional
	public QueryInsertResult<Transaction> addCredit(int dealer_id, int user_id, double amount) {
		QueryInsertResult<Transaction> result = new QueryInsertResult<Transaction>();
		Transaction transaction = null;
		
		try {
			transaction = creditDAO.addCredit(dealer_id, user_id, amount);
		}catch (Exception e) {
			return result.setData(false, transaction, e.getMessage());
		}		
		
		if(transaction==null)
			return result.setData(false, transaction, "cannot find this user");
		
		return	result.setData(true, transaction, "looks good!");
	}
	
	@Transactional
	public QueryInsertResult<CreditInfo> addNewUser(int dealer_id, int user_id, String currency) {
		QueryInsertResult<CreditInfo> result = new QueryInsertResult<CreditInfo>();
		CreditInfo creditInfo = null;
		
		try {
			creditInfo = creditDAO.addNewUser(dealer_id, user_id, currency);
		}catch (Exception e) {
			return result.setData(false, creditInfo, e.getMessage());
		}
		
		if(creditInfo==null)
			return result.setData(false, creditInfo, "cannot find this user");
		
		return	result.setData(true, creditInfo, "looks good!");
	}
	
	@Override
	protected BaseDAO getDAO() {
		return creditDAO; 
	}
}

package com.accountservice.dao;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import com.accountservice.helper.ExceptionHelper;
import com.accountservice.model.credit.CreditInfo;
import com.accountservice.model.credit.CreditLog;
import com.accountservice.model.credit.Transaction;
import com.accountservice.service.AccountService;

@Repository(value = "CreditDAO")
public class CreditDAO extends BaseDAO{
	public final String TABLENAME = "credit";
	
	@Autowired
	@Qualifier(value = "hibernate4AnnotatedSessionFactory")
	private SessionFactory sessionFactory;
	
	@Autowired
	private AccountService accountService;
	
	public CreditInfo getCredit(int dealer_id, int user_id) throws Exception {
		String query = String.format("select * from %s where "
				+ "dealer_id=%d and user_id=%d", TABLENAME, dealer_id, user_id);  
		CreditInfo result = null;
		
		try {		
			result = (CreditInfo)sessionFactory.getCurrentSession().
								createSQLQuery(query).addEntity(CreditInfo.class).uniqueResult();
		}catch (Exception e) {
			sessionFactory.getCurrentSession().clear();
			throw new Exception(ExceptionHelper.getDetails(e));
		}
		
		return result;
	}	
	
	public CreditInfo addNewUser(int dealer_id, int user_id, String currency) throws Exception{		
		if(accountService.getAccount(dealer_id, user_id, "") == null)
			return null;
		
		CreditInfo result = null;
		
		try {
		
			CreditInfo creditInfo = new CreditInfo(dealer_id, user_id, currency);
			long id = (long)sessionFactory.getCurrentSession().save(creditInfo);
			
			String query = String.format("select * from %s where id=%d", TABLENAME, id);  
					
			result = (CreditInfo)sessionFactory.getCurrentSession().
								createSQLQuery(query).addEntity(CreditInfo.class).uniqueResult();
		}catch (Exception e) {
			throw new Exception(ExceptionHelper.getDetails(e));
		}
		return result;
	}
	
	public Transaction addCredit(int dealer_id, int user_id, double amount) throws Exception{		
		if(accountService.getAccount(dealer_id, user_id, "") == null)
			return null;
		
		Transaction result = null;
		
		try {
		
			String query = String.format("update %s set balance=balance+(%.6f) "
										+ "where dealer_id=%d and user_id=%d", 
										TABLENAME, amount, dealer_id, user_id);  
					
			CreditInfo beforeTrans = getCredit(dealer_id, user_id);
			
			if(beforeTrans.getBalance() + amount > Integer.MAX_VALUE)
				throw new Exception("out of the range of credit ");
			
			if(beforeTrans.getBalance() + amount < 0)
				throw new Exception("out of the range of credit ");
			
			sessionFactory.getCurrentSession().createSQLQuery(query).executeUpdate();
			sessionFactory.getCurrentSession().clear();
			CreditInfo afterTrans = getCredit(dealer_id, user_id);
			
			result = new Transaction(afterTrans);
			result.beforeTrans = beforeTrans.getBalance();
			result.afterTrans = afterTrans.getBalance();
		}catch (Exception e) {
			throw new Exception(ExceptionHelper.getDetails(e));
		}
		
		return result;
	}

	@Override
	protected void onFlushRequestLogs(String action, String details, String source_ip, Object result) {
		CreditLog logs = new CreditLog(action, details, source_ip);
		sessionFactory.getCurrentSession().save(logs);		
	}
}
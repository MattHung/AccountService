package com.accountservice.dao;

import java.math.BigInteger;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import com.accountservice.helper.ExceptionHelper;
import com.accountservice.helper.SHAHelper;
import com.accountservice.model.account.AccountInfo;
import com.accountservice.model.account.UsersLog;

@Repository(value = "AccountDAO")
public class AccountDAO extends BaseDAO{
	public final String TABLENAME = "users";
	public final int SESSION_EXPIRE_MINUTES = 30;
	private final Logger logger = LoggerFactory.getLogger(AccountDAO.class);
	
	@SuppressWarnings("unchecked")
	public ArrayList<AccountInfo> getAccount(int dealer_id, int[] users_id, String[] users_name) throws Exception{
		ArrayList<AccountInfo> result = new ArrayList<AccountInfo>();
		
		try {
			String query = "";
			
			if(users_id.length>0) {
				query = String.format("select * from %s where dealer_id=%d and user_id in (", TABLENAME, dealer_id);
				for(int i=0; i<users_id.length; i++) {
					query += String.valueOf(users_id[i]);
					if(i != users_id.length-1)
						query += ",";
				}
				query += ")";
				result = (ArrayList<AccountInfo>)sessionFactory.getCurrentSession().
						createSQLQuery(query).addEntity(AccountInfo.class).list();
			}
				
			if(users_name.length>0 && result.size()==0) {
				query = String.format("select * from %s where dealer_id=%d and user_name in (", TABLENAME, dealer_id);
				for(int i=0; i<users_name.length; i++) {
					query += String.format("'%s'", users_name[i]);
					if(i != users_name.length-1)
						query += ",";
				}
				
				query += ")";
				result = (ArrayList<AccountInfo>)sessionFactory.getCurrentSession().
						createSQLQuery(query).addEntity(AccountInfo.class).list();
			}
		}catch (Exception e) {
			sessionFactory.getCurrentSession().clear();
			throw new Exception(ExceptionHelper.getDetails(e));
		}		
		
		return result;
	}
	
	public AccountInfo addAccount(int dealer_id, String user_name, String user_password) throws Exception {
 		AccountInfo accountInfo = null;
		try {			
			String query = String.format("insert into %s(user_id,dealer_id, user_name, user_password)"
					+ "select coalesce(max(user_id), 0)+1, %d, '%s', '%s' from %s where dealer_id=%d", 
					TABLENAME, dealer_id, user_name, user_password, TABLENAME, dealer_id);

 			sessionFactory.getCurrentSession().createSQLQuery(query).executeUpdate();
 			
 			query = "select LAST_INSERT_ID()";
 			long row_id = ((BigInteger)sessionFactory.getCurrentSession().createSQLQuery(query).uniqueResult()).longValue();
 			
 			query = String.format("select * from %s where id=%d", TABLENAME, row_id);
 			accountInfo = (AccountInfo)sessionFactory.getCurrentSession().createSQLQuery(query).addEntity(AccountInfo.class).list().get(0);
		}
		catch (Exception e) {
			sessionFactory.getCurrentSession().clear();			
			
			String msg = ExceptionHelper.getDetails(e);
			if(msg.contains("Duplicate entry"))
				msg = "duplicate user";
			
			throw new Exception(msg);
		}
		
		return accountInfo;
	}
	
	public AccountInfo userLogout(int dealer_id, String user_name) throws Exception {
		AccountInfo accountInfo = null;
		
		try {
			String query = String.format("select * from %s where dealer_id=%d and "
										+ "user_name='%s'", TABLENAME, dealer_id, user_name);
										
 			accountInfo = (AccountInfo)sessionFactory.getCurrentSession().createSQLQuery(query).addEntity(AccountInfo.class).uniqueResult();
 			
 			if(accountInfo == null)
 				return null;
 			
 			accountInfo.setSession_id("");
 			sessionFactory.getCurrentSession().update(accountInfo); 			
		}
		catch (Exception e) {
 			sessionFactory.getCurrentSession().clear();						
			String msg = ExceptionHelper.getDetails(e);			
			throw new Exception(msg);
		}
		
		return accountInfo;
	}
	
	public AccountInfo userLogin(int dealer_id, String user_name, String user_password) throws Exception {
		AccountInfo accountInfo = null;
		
		try {
			String query = String.format("select * from %s where dealer_id=%d and "
										+ "user_name='%s' and user_password='%s'",
										TABLENAME, dealer_id, user_name, user_password);
										
 			accountInfo = (AccountInfo)sessionFactory.getCurrentSession().createSQLQuery(query).addEntity(AccountInfo.class).uniqueResult();
 			
 			if(accountInfo == null)
 				return null;
 			
 			String key = String.format("%d_%s_%s_%d", 
 					dealer_id, user_name, 
 					new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date()), 
 					System.currentTimeMillis());
 			
 			if(accountInfo.getSession_id() == null || accountInfo.getSession_id().isEmpty()) {
	 			key = SHAHelper.getSha256(key);	 			
	 			accountInfo.setSession_id(key);
	 			sessionFactory.getCurrentSession().update(accountInfo);
 			}
		}
		catch (Exception e) {
 			sessionFactory.getCurrentSession().clear();						
			String msg = ExceptionHelper.getDetails(e);			
			throw new Exception(msg);
		}
		
		return accountInfo;
	}
	
	public AccountInfo checkUserLoggedIn(int dealer_id, String user_name, String session_id) throws Exception {
		AccountInfo accountInfo = null;
		
		try {
			String query = String.format("select * from %s where dealer_id=%d and user_name='%s'", 
										TABLENAME, dealer_id, user_name);
										
 			accountInfo = (AccountInfo)sessionFactory.getCurrentSession().createSQLQuery(query).addEntity(AccountInfo.class).uniqueResult();
 			
 			if(accountInfo == null)
 				return null;
 			
 			if(!accountInfo.getSession_id().equals(session_id))
 				return accountInfo;
 			
 			Timestamp timestamp = new Timestamp(System.currentTimeMillis());			
			accountInfo.setSession_id_updated_at(timestamp);			
			updateAccount(accountInfo);
		}
		catch (Exception e) {
 			sessionFactory.getCurrentSession().clear();						
			String msg = ExceptionHelper.getDetails(e);			
			throw new Exception(msg);
		}
		
		return accountInfo;		
	}
	
	public AccountInfo updateAccount(AccountInfo account) throws Exception{
		int user_id = account.getUser_id();
		String user_name = account.getUser_name();
		if(getAccount(account.getDealer_id(), new int[] {user_id}, new String[] {user_name}).size()<=0)
			return null;
			
		try {
			sessionFactory.getCurrentSession().update(account); 			
		}
		catch (Exception e) {
 			sessionFactory.getCurrentSession().clear();						
			String msg = ExceptionHelper.getDetails(e);			
			throw new Exception(msg);
		}
		
		return account;		
	}
	
	@SuppressWarnings("unchecked")
	public void updateSession() {
		String query = String.format("select * from users where session_id != '' and "
				+ "date_add(session_id_updated_at, "
				+ "interval %d minute) < now() limit 300", SESSION_EXPIRE_MINUTES);
		
		try {
			ArrayList<AccountInfo> result = (ArrayList<AccountInfo>)sessionFactory.getCurrentSession().
					createSQLQuery(query).addEntity(AccountInfo.class).list();
			
			for(int i=0; i<result.size(); i++) {
				AccountInfo accountInfo = result.get(i);
				accountInfo.setSession_id("");							
				updateAccount(accountInfo);
			}
		}
		catch (Exception e) {
 			sessionFactory.getCurrentSession().clear();						
			String msg = ExceptionHelper.getDetails(e);
			logger.debug(msg);			
		}
	}

	@Override
	protected void onFlushRequestLogs(String action, String details, String source_ip, Object result) {
		UsersLog logs = new UsersLog(action, details, source_ip);
		sessionFactory.getCurrentSession().save(logs);
	}
}

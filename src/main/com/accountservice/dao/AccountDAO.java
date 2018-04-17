package com.accountservice.dao;

import java.math.BigInteger;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import com.accountservice.helper.ExceptionHelper;
import com.accountservice.helper.SHAHelper;
import com.accountservice.model.account.AccountInfo;
import com.accountservice.types.FieldSet;

@Repository(value = "AccountDAO")
public class AccountDAO extends BaseDAO{
	public final int SESSION_EXPIRE_MINUTES = 10;
	public final int MAX_FLUSH_UPDATE_SIZE = 300;
	private final Logger logger = LoggerFactory.getLogger(AccountDAO.class);
	
	private ReentrantLock query_update_locker;
	private Queue<AccountInfo> query_update;
	
	@PostConstruct
	public void init(){
		TABLENAME = "users";
		query_update = new ConcurrentLinkedQueue<>();
		query_update_locker = new ReentrantLock();
	}
	
	@Override
	public void update() {
		// TODO Auto-generated method stub
		super.update();
		flushQueuedQuery();
	}
	
	private void flushQueuedQuery() {
		List<AccountInfo> accounts = new LinkedList<>();
		
		query_update_locker.lock();
		
		while(query_update.size() > 0) {
			accounts.add(query_update.poll());
			if(accounts.size() == MAX_FLUSH_UPDATE_SIZE)
				break;
		}
		
		query_update_locker.unlock();
		
		if(accounts.size() == 0)
			return;		

		String query = String.format("insert into %s(dealer_id,user_id,session_id,session_id_updated_at) values ", TABLENAME);
		
		for(int i=0; i<accounts.size(); i++) {
			AccountInfo account = accounts.get(i);
			query += String.format("(%d,%d,'%s','%s')", 
					account.getDealer_id(), account.getUser_id(),
					account.getSession_id(), account.getSession_id_updated_at());
			
			if(i!=accounts.size()-1)
				query += ",";
		}
		
		query += " on duplicate key update dealer_id=values(dealer_id)," + 
										"user_id=values(user_id)," +
										"session_id=values(session_id)," +
										"session_id_updated_at=values(session_id_updated_at)";
		
		sessionFactory.getCurrentSession().createSQLQuery(query).executeUpdate();
	}
	
	public AccountInfo getAccount(int dealer_id, int user_id, String user_name) throws Exception{
		ArrayList<AccountInfo> accounts = getAccount(dealer_id, 
								user_id > 0 ? new int[]{user_id} : new int[0], 
								new String[]{user_name});
		if(accounts.size() > 0)
			return accounts.get(0);
		
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public ArrayList<AccountInfo> getAccount(int dealer_id, int[] users_id, String[] users_name) throws Exception{
		ArrayList<AccountInfo> result = new ArrayList<AccountInfo>();
		ArrayList<AccountInfo> SQLResult = new ArrayList<>();
		
		List<Integer> list_users_id =  Arrays.stream(users_id).boxed().collect(Collectors.toList());
		List<String> list_users_name = new ArrayList<String>(Arrays.asList(users_name));
		
		String key = "";
		AccountInfo accountInfo = null;
		
		for(int i= list_users_id.size()-1; i>=0; i--) {
			int user_id = (int)list_users_id.get(i);
			
			key = genRedisKeyByArgs(new FieldSet("dealer_id", dealer_id),
			  						new FieldSet("user_id", user_id));

			accountInfo = getRedisCache(key, AccountInfo.class);
			
			if(accountInfo != null) {
				list_users_id.remove(i);
				result.add(accountInfo);
			}
		}		
		users_id = new int[list_users_id.size()];
		for(int i=0; i<list_users_id.size(); i++)
			users_id[i] = list_users_id.get(i);		
		
		for(int i= list_users_name.size()-1; i>=0; i--) {
			String user_name = (String)list_users_name.get(i);
			
			key = genRedisKeyByArgs(new FieldSet("dealer_id", dealer_id),
			  						new FieldSet("user_name", user_name));

			accountInfo = getRedisCache(key, AccountInfo.class);
			
			if(accountInfo != null) {
				list_users_name.remove(i);
				result.add(accountInfo);
			}
		}
		users_name = (String[]) list_users_name.toArray(new String[0]);
		
		if(list_users_id.size() == 0)
		if(list_users_name.size() == 0)
			return result;
		
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
				SQLResult = (ArrayList<AccountInfo>)sessionFactory.getCurrentSession().
						createSQLQuery(query).addEntity(AccountInfo.class).list();
			}
				
			if(users_name.length>0 && SQLResult.size()==0) {
				query = String.format("select * from %s where dealer_id=%d and user_name in (", TABLENAME, dealer_id);
				for(int i=0; i<users_name.length; i++) {
					query += String.format("'%s'", users_name[i]);
					if(i != users_name.length-1)
						query += ",";
				}
				
				query += ")";
				SQLResult = (ArrayList<AccountInfo>)sessionFactory.getCurrentSession().
						createSQLQuery(query).addEntity(AccountInfo.class).list();
			}
		}catch (Exception e) {
			sessionFactory.getCurrentSession().clear();
			throw new Exception(ExceptionHelper.getDetails(e));
		}		
		
		result.addAll(SQLResult);
		updateRedisCache(result);		
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
		
		updateUniqueRedisCache(accountInfo);
		return accountInfo;
	}
	
	public AccountInfo userLogout(int dealer_id, String user_name) throws Exception {
		AccountInfo accountInfo = null;
		
		try {			
 			accountInfo = getAccount(dealer_id, 0, user_name);
 			
 			if(accountInfo == null)
 				return null;
 			
 			accountInfo.setSession_id("");
 			updateAccount(accountInfo); 			
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
		

		updateUniqueRedisCache(accountInfo);
		return accountInfo;
	}
	
	public AccountInfo checkUserLoggedIn(int dealer_id, String user_name, String session_id) throws Exception {
		AccountInfo accountInfo = null;
		
		try {
 			accountInfo = getAccount(dealer_id, 0, user_name);
 			
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
			query_update_locker.lock();
			query_update.offer(account);
			query_update_locker.unlock();
			
//			sessionFactory.getCurrentSession().update(account);
			updateUniqueRedisCache(account);
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
				userLogout(accountInfo.getDealer_id(), accountInfo.getUser_name());
			}
		}
		catch (Exception e) {
 			sessionFactory.getCurrentSession().clear();						
			String msg = ExceptionHelper.getDetails(e);
			logger.debug(msg);			
		}
	}
}

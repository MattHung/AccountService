package com.accountservice.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.accountservice.dao.AccountDAO;
import com.accountservice.dao.BaseDAO;
import com.accountservice.helper.SHAHelper;
import com.accountservice.helper.StringHelper;
import com.accountservice.model.account.AccountInfo;
import com.accountservice.response.QueryInsertResult;
import com.accountservice.types.UserSessionCheck;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;


@Service(value = "AccountService")
public class AccountService extends BaseService{	
	@Autowired
	private AccountDAO accountDAO;
	
	@Autowired
	private CreditService creditService;
	
	@Autowired
	private DealerService dealerService;
	
	private ObjectMapper objectMapper;
	
	@PostConstruct
	private void init() {
		objectMapper = new ObjectMapper();
	}	
	
	public AccountInfo getAccount(int dealer_id, int users_id, String users_name){
		QueryInsertResult<ArrayList<AccountInfo>> getResult = getAccount(dealer_id, 
				new int[] {users_id}, new String[] {users_name});
		
		if(getResult.succeed)
			return getResult.data.get(0);
		
		return null;
	}
	
	@Transactional
	public QueryInsertResult<ArrayList<AccountInfo>> getAccount(int dealer_id, int[] users_id, String[] users_name) {
		QueryInsertResult<ArrayList<AccountInfo>> result = new QueryInsertResult<ArrayList<AccountInfo>>();
		ArrayList<AccountInfo> accountInfo = null;
		
		try {
			accountInfo = accountDAO.getAccount(dealer_id, users_id, users_name);;
		}catch (Exception e) {
			return result.setData(false, accountInfo, e.getMessage());
		}
		
		if(accountInfo.size()==0)
			return result.setData(false, accountInfo, "cannot find any users!");
		
		return result.setData(true, accountInfo, "looks good!"); 
	}
	
	@Transactional
	public QueryInsertResult<AccountInfo> addAccount(int dealer_id, String user_name, 
													String user_password, String currency){		
		QueryInsertResult<AccountInfo> result = new QueryInsertResult<AccountInfo>();
		user_name = StringHelper.clearInvalidChar(user_name);
		user_password = StringHelper.clearInvalidChar(user_password);
		currency = StringHelper.clearInvalidChar(currency);
		
		if(dealerService.getDealer(dealer_id, "")==null)
			return result.setNote("dealer_id not exists");
		
		if(dealer_id==0)
			return result.setNote("invalid dealer_id");		
		
 		if(user_name==null || user_name.isEmpty())
			return result.setNote("invalid user_name");
 		
 		if(user_password.length()<8)
 			return result.setNote("required min 8 characters");
 		
 		AccountInfo accountInfo = null;
 		
 		try {
 			user_password = SHAHelper.getSha256(user_password);
 			accountInfo = accountDAO.addAccount(dealer_id, user_name, user_password);
			if(accountInfo==null)
				return result.setData(false, accountInfo, "cannot find this user!");
			
			creditService.addNewUser(dealer_id, accountInfo.getUser_id(), currency);
 		}catch (Exception e) {
			return result.setData(false, accountInfo, e.getMessage());
		}
		
		return result.setData(true, accountInfo, "looks good!");
	}
	
	@Transactional
	public QueryInsertResult<AccountInfo> userLogin(int dealer_id, String user_name, String user_password){		
		QueryInsertResult<AccountInfo> result = new QueryInsertResult<AccountInfo>();
		user_name = StringHelper.clearInvalidChar(user_name);
		user_password =StringHelper.clearInvalidChar(user_password);
		
		if(dealerService.getDealer(dealer_id, "")==null)
			return result.setNote("dealer_id not exists");
		
		if(dealer_id==0)
			return result.setNote("invalid dealer_id");		
		
 		if(user_name==null || user_name.isEmpty())
			return result.setNote("invalid user_name");
		
		if(user_password.length()<8)
 			return result.setNote("required min 8 characters");
		
		AccountInfo accountInfo = null;
		
		try {
 			user_password = SHAHelper.getSha256(user_password);
 			accountInfo = accountDAO.userLogin(dealer_id, user_name, user_password);
			if(accountInfo==null)
				return result.setData(false, accountInfo, "invalid user_name/user_password!");
 		}catch (Exception e) {
			return result.setData(false, accountInfo, e.getMessage());
		}
		
		return result.setData(true, accountInfo, "looks good!");
	}
		
	@Transactional
	public QueryInsertResult<AccountInfo> userLogout(int dealer_id, String user_name){
		QueryInsertResult<AccountInfo> result = new QueryInsertResult<AccountInfo>();
		user_name = StringHelper.clearInvalidChar(user_name);
		
		if(dealerService.getDealer(dealer_id, "")==null)
			return result.setNote("dealer_id not exists");
		
		if(dealer_id==0)
			return result.setNote("invalid dealer_id");		
		
 		if(user_name==null || user_name.isEmpty())
			return result.setNote("invalid user_name");
		
		AccountInfo accountInfo = null;
		
		try { 			
 			accountInfo = accountDAO.userLogout(dealer_id, user_name);
			if(accountInfo==null)
				return result.setData(false, accountInfo, "cannot find this user!");
 		}catch (Exception e) {
			return result.setData(false, accountInfo, e.getMessage());
		}
		
		return result.setData(true, accountInfo, "looks good!");
	}
	
	@Transactional
	public QueryInsertResult<List<UserSessionCheck>> checkUserLoggedIn(String users_json) {
		QueryInsertResult<List<UserSessionCheck>> result = new QueryInsertResult<List<UserSessionCheck>>();
		List<UserSessionCheck> users = null;
		
		try {		
			users = objectMapper.readValue(users_json, new TypeReference<List<UserSessionCheck>>() {});			
		}catch (Exception e) {
			 return result.setNote("invalid json format");
		}
		
		users.sort(new Comparator<UserSessionCheck>() {
			@Override
			public int compare(UserSessionCheck o1, UserSessionCheck o2) {return o1.getDealer_id() - o2.getDealer_id();}
		});
		
		List<UserSessionCheck> checkResults = new LinkedList<>();
		
		for(UserSessionCheck userSessoin : users) {
			UserSessionCheck checkResult = new UserSessionCheck();
			checkResult.setDealer_id(userSessoin.getDealer_id());
			checkResult.setUser_name(userSessoin.getUser_name());
			checkResult.setSession_id(userSessoin.getSession_id());
			
			AccountInfo accountInfo = null;
			
			if(dealerService.getDealer(checkResult.getDealer_id(), "")==null)
				checkResult.setDetails("dealer_id not exists");			
			else if(checkResult.getDealer_id()==0)
				checkResult.setDetails("invalid dealer_id");			
			else if(checkResult.getUser_name()==null || checkResult.getUser_name().isEmpty())
	 			checkResult.setDetails("invalid user_name");			
			else if(checkResult.getSession_id()==null || checkResult.getSession_id().isEmpty())
				checkResult.setDetails("invalid session_id");
			else {			
				try { 			
		 			accountInfo = accountDAO.checkUserLoggedIn(
		 					checkResult.getDealer_id(), 
		 					checkResult.getUser_name(), 
		 					checkResult.getSession_id());
		 			
					if(accountInfo == null)
						checkResult.setDetails("cannot find this user!");
					else if(!accountInfo.getSession_id().equals(checkResult.getSession_id()))
						checkResult.setDetails("Invalid session_id!");
					else
						checkResult.setSession_online(true);
		 		}catch (Exception e) {
		 			checkResult.setDetails(e.getMessage());
				}
			}
			
			checkResults.add(checkResult);
		}
		
		return result.setData(true, checkResults, "looks good!");
	}
	
	@Transactional
	public void updateSession() {
		try {
			accountDAO.updateSession();			
 		}catch (Exception e) {
			
		}
	}

	@Override
	protected BaseDAO getDAO() {
		return accountDAO; 
	}
}

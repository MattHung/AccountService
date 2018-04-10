package com.accountservice.service;

import java.sql.Timestamp;
import java.sql.Date;
import java.util.ArrayList;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.accountservice.dao.AccountDAO;
import com.accountservice.helper.SHAHelper;
import com.accountservice.helper.StringHelper;
import com.accountservice.model.account.AccountInfo;
import com.accountservice.response.QueryInsertResult;


@Service(value = "AccountService")
public class AccountService extends BaseService{	
	@Autowired
	private AccountDAO accountDAO;
	
	@Autowired
	private CreditService creditService;
	
	@Autowired
	private DealerService dealerService;
	
	@PostConstruct
	private void init() {
		setDao(accountDAO);
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
	public QueryInsertResult<AccountInfo> checkUserLoggedIn(int dealer_id, String user_name, String session_id) {
		QueryInsertResult<AccountInfo> result = new QueryInsertResult<AccountInfo>();
		user_name = StringHelper.clearInvalidChar(user_name);
		session_id = StringHelper.clearInvalidChar(session_id);
		
		AccountInfo accountInfo = null;
		
		if(dealerService.getDealer(dealer_id, "")==null)
			return result.setNote("dealer_id not exists");
		
		if(dealer_id==0)
			return result.setNote("invalid dealer_id");		
		
 		if(user_name==null || user_name.isEmpty())
			return result.setNote("invalid user_name");
		
		if(session_id==null || session_id.isEmpty())
 			return result.setData(false, accountInfo, "Invalid session_id!");
		
		try { 			
 			accountInfo = accountDAO.checkUserLoggedIn(dealer_id, user_name, session_id);
			if(accountInfo == null)
				return result.setData(false, accountInfo, "cannot find this user!");
			
			if(!accountInfo.getSession_id().equals(session_id))
				return result.setData(false, accountInfo, "Invalid session_id!");
 		}catch (Exception e) {
			return result.setData(false, accountInfo, e.getMessage());
		}
		
		return result.setData(true, accountInfo, "looks good!");
	}
	
	@Transactional
	public void updateSession() {
		try {
			accountDAO.updateSession();
 		}catch (Exception e) {
			
		}
	}
}

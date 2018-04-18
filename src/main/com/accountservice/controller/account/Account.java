package com.accountservice.controller.account;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.accountservice.helper.ApiResultHelper;
import com.accountservice.model.account.AccountInfo;
import com.accountservice.response.ApiResult;
import com.accountservice.service.AccountService;
import com.accountservice.types.UserSessionCheck;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@Controller
public class Account {
	@Autowired
	@Qualifier(value = "AccountService")
	private AccountService accountService;
	
	@ResponseBody
	@RequestMapping(value = "/account/getAccount", method = RequestMethod.GET)	
	public ApiResult<ArrayList<AccountInfo>> getAccount(Model model, HttpServletRequest request,
			@RequestParam("dealer_id") int dealer_id,
			@RequestParam(value = "user_id", required = false, defaultValue="")  int[] users_id,
			@RequestParam(value = "user_name", required = false, defaultValue="")  String[] users_name) {
				
		ApiResult<ArrayList<AccountInfo>> result =  accountService.getAccount(dealer_id, users_id, users_name);		
		ApiResultHelper.assigenParam(request, result);
		
		accountService.logRequest(request, result);
		return result;
	}
	
	@ResponseBody
	@RequestMapping(value = "/account/addAccount", method = RequestMethod.GET)	
	public ApiResult<AccountInfo> addAccount(Model model, HttpServletRequest request, 
			@RequestParam("dealer_id") int dealer_id,
			@RequestParam("user_name") String user_name,
			@RequestParam("user_password") String user_password,
			@RequestParam("currency")  String currency) {		
		
		ApiResult<AccountInfo> result = accountService.addAccount(dealer_id, user_name, user_password, currency);		
		ApiResultHelper.assigenParam(request, result);
		
		accountService.logRequest(request, result);
		return result;
}
	
	@ResponseBody
	@RequestMapping(value = "/account/user_login", method = RequestMethod.GET)
	public ApiResult<AccountInfo> userLogin(Model model, HttpServletRequest request,
			@RequestParam("dealer_id") int dealer_id,
			@RequestParam("user_name")  String user_name,
			@RequestParam("user_password")  String user_password){
			
		ApiResult<AccountInfo> result = accountService.userLogin(dealer_id, user_name, user_password);		
		ApiResultHelper.assigenParam(request, result);
		
		accountService.logRequest(request, result);
		return result;		
	}
	
	@ResponseBody
	@RequestMapping(value = "/account/user_logout", method = RequestMethod.GET)
	public ApiResult<AccountInfo> userLogout(Model model, HttpServletRequest request,
			@RequestParam("dealer_id") int dealer_id,
			@RequestParam("user_name")  String user_name){
			
		ApiResult<AccountInfo> result = accountService.userLogout(dealer_id, user_name);		
		ApiResultHelper.assigenParam(request, result);
		
		accountService.logRequest(request, result);
		return result;		
	}
	
	@ResponseBody
	@RequestMapping(value = "/account/checkUserLoggedIn", method = RequestMethod.GET)
	public ApiResult<List<UserSessionCheck>> checkUserLoggedIn(Model model, HttpServletRequest request,
			@RequestParam("users") String users_json){
		
		ApiResult<List<UserSessionCheck>> result = accountService.checkUserLoggedIn(users_json);		
		ApiResultHelper.assigenParam(request, result);
		return result;
	}
}

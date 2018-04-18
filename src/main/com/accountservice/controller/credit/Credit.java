package com.accountservice.controller.credit;

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
import com.accountservice.model.credit.CreditInfo;
import com.accountservice.model.credit.Transaction;
import com.accountservice.response.ApiResult;
import com.accountservice.service.CreditService;
import com.accountservice.service.MachineService;
import com.accountservice.types.ServerRole;

@Controller
public class Credit {
	public final String MESSAGE_NOT_ALLOW = "not allowed to access credit!";
	
	@Autowired
	@Qualifier(value= "CreditService")
	private CreditService creditService;
	
	@Autowired
	@Qualifier(value = "MachineService")
	private MachineService machineService;
	
	private boolean checkAllowAccessCredit(ServerRole role, String server_ip, String api_key) {
		if(server_ip.equals("0:0:0:0:0:0:0:1"))
			return true;

		return machineService.checkExist(role, server_ip, api_key);
	}
		
	@ResponseBody
	@RequestMapping(value = "/credit/getCredit", method = RequestMethod.GET)	
	public ApiResult<CreditInfo> getCredit(Model model, HttpServletRequest request,
			@RequestParam("dealer_id") int dealer_id,
			@RequestParam("user_id") int user_id) {
		
		if(!checkAllowAccessCredit(ServerRole.Platfrom, request.getRemoteAddr(), ""))
		if(!checkAllowAccessCredit(ServerRole.GameServer, request.getRemoteAddr(), ""))
			return (ApiResult<CreditInfo>)(new ApiResult<CreditInfo>().setNote(MESSAGE_NOT_ALLOW));
		
		ApiResult<CreditInfo> result = creditService.getCredit(dealer_id, user_id);
		ApiResultHelper.assigenParam(request, result);
		
		creditService.logRequest(request, result);
		return result;
	}
	
	@ResponseBody
	@RequestMapping(value = "/credit/addCredit", method = RequestMethod.GET)
	public ApiResult<Transaction> addCredit(Model model, HttpServletRequest request,
			@RequestParam("dealer_id") int dealer_id,
			@RequestParam("user_id") int user_id,
			@RequestParam("amount") double amount,
			@RequestParam("api_key") String api_key,
			@RequestParam("op_trace") String op_trace) {
		
		if(!checkAllowAccessCredit(ServerRole.Platfrom, request.getRemoteAddr(), api_key))
			return (ApiResult<Transaction>)(new ApiResult<Transaction>().setNote(MESSAGE_NOT_ALLOW));
		
		ApiResult<Transaction> result = creditService.addCredit(dealer_id, user_id, amount);
		result.getData().api_key = api_key;
		result.getData().op_trace = op_trace;
		
		ApiResultHelper.assigenParam(request, result);
		creditService.logRequest(request, result);
		return result;
	}
}

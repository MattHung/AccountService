package com.accountservice.controller.dealer;

import java.util.ArrayList;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.accountservice.helper.ApiResultHelper;
import com.accountservice.model.dealer.DealerInfo;
import com.accountservice.response.ApiResult;
import com.accountservice.service.DealerService;

@Controller
public class Dealer {
	
	@Autowired
	private DealerService dealerService;
	
//	http://localhost:8080/AccountService/dealer/getDealer/?dealer_id=1, 2
//	http://localhost:8080/AccountService/dealer/getDealer/?&dealer_name='傲勝','dealerA'
	@ResponseBody
	@RequestMapping(value = "/dealer/getDealer", method = RequestMethod.GET)	
	public ApiResult<ArrayList<DealerInfo>> getDealer(Model model, HttpServletRequest request,
			@RequestParam(value = "dealer_id", required=false, defaultValue="") int[] dealers_id,
			@RequestParam(value = "dealer_name", required=false, defaultValue="") String[] dealers_name) {
		
		ApiResult<ArrayList<DealerInfo>> result = dealerService.getDealer(dealers_id, dealers_name);
		ApiResultHelper.assigenParam(request, result);
		
		dealerService.logRequest(request, result);   
		return result;
	}
	
	@ResponseBody
	@RequestMapping(value = "/dealer/addDealer", method = RequestMethod.GET)
	public ApiResult<DealerInfo> addDealer(Model model, HttpServletRequest request,
			@RequestParam("dealer_name") String dealer_name){
		
		ApiResult<DealerInfo> result = dealerService.addDealer(dealer_name);
		ApiResultHelper.assigenParam(request, result);		
		
		dealerService.logRequest(request, result);
		return result;
	}
	
	@ResponseBody
	@RequestMapping(value = "/dealer/delDealer", method = RequestMethod.GET)
	public ApiResult<Boolean> delDealer(Model model, HttpServletRequest request,
			@RequestParam("dealer_id") int dealer_id){
		
		ApiResult<Boolean> result = dealerService.deleteDealer(dealer_id);
		ApiResultHelper.assigenParam(request, result);		
		
		dealerService.logRequest(request, result);
		return result;
	}
}

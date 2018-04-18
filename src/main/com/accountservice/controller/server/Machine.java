package com.accountservice.controller.server;

import java.util.List;

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
import com.accountservice.model.game.GameInfo;
import com.accountservice.model.machine.ServerInfo;
import com.accountservice.response.ApiResult;
import com.accountservice.service.MachineService;

@Controller
public class Machine {
	@Autowired
	@Qualifier(value = "MachineService")
	private MachineService machineService;
	
	@ResponseBody
	@RequestMapping(value = "/machine/updateServer", method = RequestMethod.GET)	
	public ApiResult<ServerInfo> updateServer(Model model, HttpServletRequest request,
			@RequestParam(value = "role") int role,
			@RequestParam(value = "name") String name,
			@RequestParam(value = "ip_address") String ip_address) {
				
		ApiResult<ServerInfo> result =  machineService.updateServer(role, name, ip_address);		
		ApiResultHelper.assigenParam(request, result);
		
		machineService.logRequest(request, result);
		return result;
	}
	
	@ResponseBody
	@RequestMapping(value = "/machine/getServers", method = RequestMethod.GET)	
	public ApiResult<List<ServerInfo>> getServers(Model model, HttpServletRequest request,
			@RequestParam(value = "role", required = false, defaultValue = "0") int role,
			@RequestParam(value = "id", required = false, defaultValue = "0") int id) {
				
		ApiResult<List<ServerInfo>> result =  machineService.getServers(role, id);		
		ApiResultHelper.assigenParam(request, result);
		
		machineService.logRequest(request, result);
		return result;
	}
}
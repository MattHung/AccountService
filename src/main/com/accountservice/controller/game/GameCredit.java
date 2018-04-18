package com.accountservice.controller.game;

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
import com.accountservice.response.ApiResult;
import com.accountservice.service.GameService;

@Controller
public class GameCredit {
	@Autowired
	@Qualifier(value = "GameService")
	private GameService gameService;
	
	@ResponseBody
	@RequestMapping(value = "/game/credit/exchange", method = RequestMethod.GET)
	public ApiResult<GameInfo> exchange(Model model, HttpServletRequest request,
			@RequestParam(value = "game_id")  int game_id,
			@RequestParam(value = "dealer_id") int dealer_id,			
			@RequestParam(value = "user_id")  int user_id,			
			@RequestParam(value = "amount")  double amount) {
				
		return null;
	}
	
	@ResponseBody
	@RequestMapping(value = "/game/credit/cashOut", method = RequestMethod.GET)
	public ApiResult<GameInfo> cashOut(Model model, HttpServletRequest request,
			@RequestParam(value = "type") int type,
			@RequestParam(value = "enabled", required = false, defaultValue = "1") int enabled,
			@RequestParam(value = "game_id")  int game_id,
			@RequestParam(value = "name")  String name) {
				
		return null;
	}
}

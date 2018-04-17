package com.accountservice.controller;

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
import com.accountservice.response.ApiResult;
import com.accountservice.service.GameService;

@Controller
public class Game {
	@Autowired
	@Qualifier(value = "GameService")
	private GameService gameService;
	
	@ResponseBody
	@RequestMapping(value = "/game/updateGame", method = RequestMethod.GET)	
	public ApiResult<GameInfo> updateGame(Model model, HttpServletRequest request,
			@RequestParam(value = "type") int type,
			@RequestParam(value = "enabled", required = false, defaultValue = "1") int enabled,
			@RequestParam(value = "game_id")  int game_id,
			@RequestParam(value = "name")  String name) {
				
		ApiResult<GameInfo> result =  gameService.updateGame(type, enabled, game_id, name);		
		ApiResultHelper.assigenParam(request, result);
		
		gameService.logRequest(request, result);
		return result;
	}
	
	@ResponseBody
	@RequestMapping(value = "/game/getGames", method = RequestMethod.GET)	
	public ApiResult<List<GameInfo>> getGames(Model model, HttpServletRequest request,
			@RequestParam(value = "type", required = false, defaultValue = "0") int type,
			@RequestParam(value = "game_id", required = false, defaultValue = "")  int[] game_id) {
				
		ApiResult<List<GameInfo>> result =  gameService.getGames(type, game_id);		
		ApiResultHelper.assigenParam(request, result);
		
		gameService.logRequest(request, result);
		return result;
	}
}

package com.accountservice.service;

import java.util.LinkedList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestParam;

import com.accountservice.dao.BaseDAO;
import com.accountservice.dao.GameDAO;
import com.accountservice.model.game.GameInfo;
import com.accountservice.response.ApiResult;
import com.accountservice.response.QueryInsertResult;

@Service(value = "GameService")
public class GameService extends BaseService{
	@Autowired
	private GameDAO gameDAO;
	
	@Transactional
	public QueryInsertResult<GameInfo> updateGame(int type, int enabled, int game_id, String name){
		QueryInsertResult<GameInfo> result = new QueryInsertResult<GameInfo>();
		GameInfo gameInfo = null;
		
		try {
			gameInfo = gameDAO.updateGame(type, enabled, game_id, name);;
		}catch (Exception e) {
			return result.setData(false, gameInfo, e.getMessage());
		}
		
		if(gameInfo == null)
			return result.setData(false, gameInfo, "add game failed!");
		
		return result.setData(true, gameInfo, "looks good!"); 
	}
	
	@Transactional
	public QueryInsertResult<List<GameInfo>> getGames(int type, int[] game_id){
		QueryInsertResult<List<GameInfo>> result = new QueryInsertResult<List<GameInfo>>();
		List<GameInfo> gamesInfo = new LinkedList<>();
		
		try {
			gamesInfo = gameDAO.getGames(type, game_id);;
		}catch (Exception e) {
			return result.setData(false, gamesInfo, e.getMessage());
		}
		
		if(gamesInfo.size() == 0)
			return result.setData(false, gamesInfo, "no any games found!");
		
		return result.setData(true, gamesInfo, "looks good!");
	}
	
	@Override
	protected BaseDAO getDAO() {
		return gameDAO; 
	}
}

package com.accountservice.dao;

import java.util.LinkedList;
import java.util.List;

import javax.persistence.Table;

import org.springframework.stereotype.Repository;

import com.accountservice.helper.ExceptionHelper;
import com.accountservice.model.game.GameInfo;


@Repository(value = "GameDAO")
@Table(name = "games")
public class GameDAO extends BaseDAO{
	
	public GameInfo updateGame(int type, int enabled, int game_id, String name) throws Exception{
		GameInfo result = new GameInfo(type, game_id, name);
		
		result.setEnabled(enabled);
		
		try {
			sessionFactory.getCurrentSession().saveOrUpdate(result);
		}catch (Exception e) {
			sessionFactory.getCurrentSession().clear();
			throw new Exception(ExceptionHelper.getDetails(e));
		}
		
		return result;
	}
	
	@SuppressWarnings("unchecked")
	public List<GameInfo> getGames(int type, int[] game_id) throws Exception{
		List<GameInfo> result = new LinkedList<>();
		
		try {
			String query = String.format("select * from %s", getTableName());
			
			if(game_id.length > 0) {
				query += " where game_id in (";
				for(int i=0; i<game_id.length; i++) { 
					query += String.format("%d", game_id[i]);
					if(i!=game_id.length-1)
						query += ",";
				}
				
				query += ")";
			}
			
			if(type > 0) 
				query = String.format("select result.* from (%s) result where type = %d", query, type);				
			
			result = sessionFactory.getCurrentSession().createSQLQuery(query).addEntity(GameInfo.class).list();
		}catch (Exception e) {
			sessionFactory.getCurrentSession().clear();
			throw new Exception(ExceptionHelper.getDetails(e));
		}
		
		return result;
	}
}

package com.accountservice.dao;

import java.util.LinkedList;
import java.util.List;

import javax.persistence.Table;

import org.springframework.stereotype.Repository;

import com.accountservice.helper.ExceptionHelper;
import com.accountservice.model.machine.ServerInfo;

@Repository(value = "MachineDAO")
@Table(name = "servers")
public class MachineDAO extends BaseDAO {

	public ServerInfo updateServer(ServerInfo serverInfo) throws Exception{
		try {			
			sessionFactory.getCurrentSession().saveOrUpdate(serverInfo);
		}
		catch (Exception e) {
			sessionFactory.getCurrentSession().clear();						
			String msg = ExceptionHelper.getDetails(e);
			throw new Exception(msg);
		}
		
		return serverInfo;
	}
	
	@SuppressWarnings("unchecked")
	public List<ServerInfo> getServers(int role, int id) throws Exception{
		List<ServerInfo> serversInfo = new LinkedList<>();
		try {			
			String query = String.format("select * from %s", getTableName());
			
			if(role > 0) 
				query = String.format("select result.* from (%s) result where role=%d", query, role);
			
			if(id > 0) 
				query = String.format("select id_result.* from (%s) id_result where id=%d", query, id);
			
			serversInfo = sessionFactory.getCurrentSession().createSQLQuery(query).addEntity(ServerInfo.class).list();
		}
		catch (Exception e) {
			sessionFactory.getCurrentSession().clear();						
			String msg = ExceptionHelper.getDetails(e);
			throw new Exception(msg);
		}
		
		return serversInfo;
	}
}

package com.accountservice.service;

import java.util.LinkedList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.accountservice.dao.BaseDAO;
import com.accountservice.dao.MachineDAO;
import com.accountservice.helper.SHAHelper;
import com.accountservice.model.machine.ServerInfo;
import com.accountservice.response.QueryInsertResult;
import com.accountservice.types.ServerRole;

@Service(value = "MachineService")
public class MachineService extends BaseService{
	@Autowired
	private MachineDAO machineDAO;
	
	private List<ServerInfo> cacheList;
	
	@Autowired
	private MachineService instance;
	
	@PostConstruct
	private void init() {		
		cacheList = instance.getServers(0, 0).data;
	}
	
	public boolean checkExist(ServerRole role, String ip_address) {
		for(ServerInfo server : cacheList)
		if(role.getVal() == server.getRole() && ip_address.equals(server.getIp_address()))
			return true;
		
		return false;
	}
	
	@Transactional
	public QueryInsertResult<ServerInfo> updateServer(int role, String name, String ip_address){
		QueryInsertResult<ServerInfo> result = new QueryInsertResult<ServerInfo>();
		ServerInfo serverInfo = new ServerInfo(role, name, ip_address);
		
		String api_key = SHAHelper.getSha256(String.format("%d%s%s", 
				serverInfo.getRole(),
				serverInfo.getName(), 
				serverInfo.getIp_address()));
		
		serverInfo.setApi_key(api_key);
		
		try {
			serverInfo = machineDAO.updateServer(serverInfo);
		}catch (Exception e) {
			return result.setData(false, serverInfo, e.getMessage());
		}
		
		return result.setData(true, serverInfo, "looks good!"); 
	}
	
	@Transactional
	public QueryInsertResult<List<ServerInfo>> getServers(int role, int id){
		QueryInsertResult<List<ServerInfo>> result = new QueryInsertResult<List<ServerInfo>>();
		List<ServerInfo> serversInfo = new LinkedList<>(); 
			
		try {
			serversInfo = machineDAO.getServers(role, id);
		}catch (Exception e) {
			return result.setData(false, serversInfo, e.getMessage());
		}
		
		return result.setData(true, serversInfo, "looks good!"); 
	}	

	@Override
	protected BaseDAO getDAO() {
		return machineDAO;
	}
}

package com.accountservice.service;

import java.util.ArrayList;
import java.util.LinkedList;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.accountservice.dao.BaseDAO;
import com.accountservice.dao.DealerDAO;
import com.accountservice.model.dealer.DealerInfo;
import com.accountservice.response.QueryInsertResult;

@Service(value = "DealerService")
public class DealerService extends BaseService{
	
	@Autowired
	private DealerDAO dealerDAO;
	
	public DealerInfo getDealer(int dealer_id, String dealer_name) {
		QueryInsertResult<ArrayList<DealerInfo>> dealers = getDealer(new int[] {dealer_id}, new String[] {dealer_name});		
		if(dealers.succeed)
			return dealers.getData().get(0);
		
		return null;
	}
	
	@Transactional
	public QueryInsertResult<ArrayList<DealerInfo>> getDealer(int[] dealers_id, String[] dealers_name) {
		QueryInsertResult<ArrayList<DealerInfo>> result = new QueryInsertResult<ArrayList<DealerInfo>>();
		ArrayList<DealerInfo> dealerInfo = null;
		
		try {
			dealerInfo = dealerDAO.getDealer(dealers_id, dealers_name);
		}catch (Exception e) {
			return result.setData(false, dealerInfo, e.getMessage());
		}
		
		if(dealerInfo.size()==0)
			return result.setData(false, dealerInfo, "not any dealer found！");
		
		return result.setData(true, dealerInfo, "looks good!");
	}
	
	@Transactional
	public QueryInsertResult<DealerInfo> addDealer(String dealer_name) {
		QueryInsertResult<DealerInfo> result = new QueryInsertResult<DealerInfo>();
		DealerInfo dealerInfo = null;
		
		try {
			dealerInfo = dealerDAO.addDealer(dealer_name);
		}catch (Exception e) {
			return result.setData(false, dealerInfo, e.getMessage());
		}
		
		if(dealerInfo==null)
			return result.setData(false, dealerInfo, "cannot find this dealer");
		
		return result.setData(true, dealerInfo, "looks good!");
	}
	
	@Transactional
	public QueryInsertResult<Boolean> deleteDealer(int dealer_id){
		QueryInsertResult<Boolean> result = new QueryInsertResult<Boolean>();
		boolean deleteResult = false;
		
		try {
			deleteResult = dealerDAO.deleteDealer(dealer_id);
		}catch (Exception e) {
			return result.setData(false, deleteResult, e.getMessage());
		}
		
		return result.setData(true, deleteResult, "looks good!");
	}
	
	@Override
	protected BaseDAO getDAO() {
		return dealerDAO; 
	}
}

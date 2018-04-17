package com.accountservice.dao;

import java.util.ArrayList;

import javax.annotation.PostConstruct;
import javax.persistence.Table;

import org.springframework.stereotype.Repository;

import com.accountservice.helper.ExceptionHelper;
import com.accountservice.model.dealer.DealerInfo;

@Repository(value = "DealerDAO")
@Table(name = "dealers")
public class DealerDAO extends BaseDAO{
	@SuppressWarnings("unchecked")
	public ArrayList<DealerInfo> getDealer(int[] dealers_id, String[] dealers_name) throws Exception{
		ArrayList<DealerInfo> result = new ArrayList<DealerInfo>();
		
		try {
			String query = "";
			
			if(dealers_id!=null && dealers_id.length > 0) {			
				query = String.format("select * from %s where dealer_id in (", getTableName());
				
				for(int i=0; i<dealers_id.length; i++) {
					query += String.valueOf(dealers_id[i]);
					if(i != dealers_id.length-1)
						query += ",";
				}
				
				query += ")";
				result = (ArrayList<DealerInfo>)sessionFactory.getCurrentSession().createSQLQuery(query).addEntity(DealerInfo.class).list();
			}
			
			if((result==null || result.size()==0) && dealers_name!=null && dealers_name.length > 0) {			
				query = String.format("select * from %s where dealer_name in (", getTableName());
				
				for(int i=0; i<dealers_name.length; i++) {
					String d_name = dealers_name[i]; 
					d_name = d_name.replaceAll("\"", "").replaceAll("\'", "");					
					query += String.format("'%s'", d_name);
					if(i != dealers_name.length-1)
						query += ",";
				}
				
				query += ")";
				result = (ArrayList<DealerInfo>)sessionFactory.getCurrentSession().createSQLQuery(query).addEntity(DealerInfo.class).list();
			}
		}catch (Exception e) {
			sessionFactory.getCurrentSession().clear();
			throw new Exception(ExceptionHelper.getDetails(e));
		}
		
		return result;
	}
	
	public DealerInfo addDealer(String dealer_name) throws Exception{
		DealerInfo result = null;
		
		try {
			String query = String.format("insert into %s(dealer_id, dealer_name)"
					+ "select coalesce(max(dealer_id), 0)+1, %s from %s", 
					getTableName(), dealer_name, getTableName());
			
			sessionFactory.getCurrentSession().createSQLQuery(query).executeUpdate();
			
			query = String.format("select * from %s where "
					+ "dealer_name=%s", getTableName(), dealer_name);
			
			result = (DealerInfo)sessionFactory.getCurrentSession().createSQLQuery(query).addEntity(DealerInfo.class).uniqueResult();			
		}catch (Exception e) {
			sessionFactory.getCurrentSession().clear();
			throw new Exception(ExceptionHelper.getDetails(e));
		}
		
		return result;
	}
	
	public boolean deleteDealer(int dealer_id) throws Exception{		
		try {
			String query = String.format("delete from %s where dealer_id=%d", 
					getTableName(), dealer_id);
			
			sessionFactory.getCurrentSession().createSQLQuery(query).executeUpdate();			
		}catch (Exception e) {
			sessionFactory.getCurrentSession().clear();
			throw new Exception(ExceptionHelper.getDetails(e));
		}
		
		return true;
	}	
}

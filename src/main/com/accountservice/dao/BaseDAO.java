package com.accountservice.dao;

import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PostConstruct;

import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.hibernate.type.StandardBasicTypes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.transaction.annotation.Transactional;

import com.accountservice.helper.ExceptionHelper;
import com.accountservice.types.FieldIndex;
import com.accountservice.types.FieldSet;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import redis.clients.jedis.JedisCluster;

public abstract class BaseDAO {
	class LogObject{
		public String action;
		public String details; 
		public String source_ip;
		
		public LogObject(String _action, String _details, String _source_ip) {
			action = _action;
			details = _details;
			source_ip = _source_ip;
		}
	}
	
	public static final int BATCH_FLUSH_COUNT = 200;
	
	private Logger logger = LoggerFactory.getLogger(BaseDAO.class);	
	private Queue<LogObject> logs;
	private Map<String, FieldIndex> indexes;
	private ObjectMapper objectMapper;
	
	public String TABLENAME = "";
	
	@Autowired
    private JedisCluster jedisCluster;
			
	@Autowired
	@Qualifier(value = "hibernate4AnnotatedSessionFactory")
	protected SessionFactory sessionFactory;
	
	@PostConstruct
	private void init() {
		logs = new LinkedList<LogObject>();
		indexes = new ConcurrentHashMap<>();
		objectMapper = new ObjectMapper();
	}
	
	public void update() {}
	
	@SuppressWarnings("unchecked")
	@Transactional
	private void setupIndexes() {
		if(indexes.size() != 0)
			return;
		
		String query = String.format("show index from %s", "users");
		
		SQLQuery sqlQuery = sessionFactory.getCurrentSession().createSQLQuery(query);
		sqlQuery.addScalar("Table", StandardBasicTypes.STRING);
		sqlQuery.addScalar("Key_name", StandardBasicTypes.STRING);
		sqlQuery.addScalar("Column_name", StandardBasicTypes.STRING);
		List<Object[]> indexList = sqlQuery.list();
		
		for(Object[] fileds : indexList) {
			if(fileds[1].toString().toLowerCase().contains("index")) {
				String index_name = fileds[1].toString();
				if(!indexes.containsKey(index_name))
					indexes.put(index_name, new FieldIndex());
				
				String index_value = indexes.get(index_name).getIndex_value();
				
				if(!index_value.equals(""))
					index_value += String.format("#%s", fileds[2]);
				else
					index_value += String.format("%s", fileds[2]);
					
				indexes.get(index_name).setIndex_value(index_value);
			}			
		}
	}
	
	protected String getLogTableName() {return "";}
	
	protected void onFlushRequestLogs(String action, String details, String source_ip, Object result) {		
		logs.offer(new LogObject(action, details, source_ip));flushLogs();
	}	
	
	protected String genRedisKeyByArgs(FieldSet ...fileds) {
		String result = String.format("%s", TABLENAME);
		
		for(FieldSet filed : fileds)
			result += String.format("_%s:%s", filed.getFieldName(), filed.getFieldValue());
		
		return result;
	}
	
	protected String genRedisKeyByFields(FieldSet[] fileds) {
		String result = String.format("%s", TABLENAME);
		
		for(FieldSet filed : fileds)
			result += String.format("_%s:%s", filed.getFieldName(), filed.getFieldValue());
		
		return result;
	}
	
	protected String getRedisCache(String redisKey) {
		return jedisCluster.get(redisKey);
	}
	
	protected <T> T getRedisCache(String redisKey, Class<T> type) {
		String jsonResult =  jedisCluster.get(redisKey);		
		
		try {
			if(jsonResult.equals(null))
				return null;
			
			return objectMapper.readValue(jsonResult, type);
		}catch (Exception e) {
			return null;
		}
	}
	
	protected String setRedisCache(String fileds_key, String value) {
		jedisCluster.set(String.format("%s_%s", TABLENAME, fileds_key), value);
		return value;
	}
	
	private <T> List<Field> getObjectFields(FieldIndex fieldIndex, T obj){
		if(fieldIndex.getObject_fields().size() == 0) {
			String[] field_keys = fieldIndex.getIndex_value().split("#");
			
			List<FieldSet> fieldSets = new LinkedList<>();
			
			for(String field_key : field_keys) {
				try {
					Field field = obj.getClass().getDeclaredField(field_key);
					fieldIndex.getObject_fields().add(field);
				}catch (Exception e) {
				}
			}
		}
		
		return fieldIndex.getObject_fields();
	}
	
	private <T> String getKeyByIndexValue(FieldIndex fieldIndex, T obj) {
		List<FieldSet> fieldSets = new LinkedList<>();		
		List<Field> fields = getObjectFields(fieldIndex, obj);
		
		for(Field field : fields) {
			try {			
				field.setAccessible(true);
				fieldSets.add(new FieldSet(field.getName(), field.get(obj)));
			}catch (Exception e) {		
				e.printStackTrace();
			}
		}
		
		return genRedisKeyByFields(fieldSets.toArray(new FieldSet[0]));
	}
		
	@SuppressWarnings("serial")
	protected <T> void updateUniqueRedisCache(T value) {
		updateRedisCache(new LinkedList<T>() {{add(value);}});
	}
	
	protected <T> void updateRedisCache(List<T> values) {
		setupIndexes();		
		
		if(values==null)
			return;
		if(values.size()==0)
			return;
		
		for(FieldIndex fieldIndex : indexes.values()) 
		for(T value : values){
			String key = getKeyByIndexValue(fieldIndex, value);			
			try {
				jedisCluster.set(key, objectMapper.writeValueAsString(value));
			} catch (JsonProcessingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}		
	}
		
	public void flushLogs() {
		int count = 0;
		
		if(logs.size()==0)
			return;
		
		try {		
			String query = String.format("insert into %s(action, details, source_ip) values", "_logs_" + TABLENAME);
			
			while(logs.size() > 0) {
				LogObject log = logs.poll();
				query += String.format("('%s','%s','%s'),", log.action, log.details, log.source_ip);
				
				if(count==BATCH_FLUSH_COUNT)
					break;
				
				count++;
			}
			
			query = query.substring(0, query.length()-1);
					
			sessionFactory.getCurrentSession().createSQLQuery(query).executeUpdate();
		}catch(Exception e) {
			sessionFactory.getCurrentSession().clear();
		}
	}
	
	public void Log(String action, String source_ip, Object result){
		try {
			ObjectMapper mapper = new ObjectMapper();
			String details = mapper.writeValueAsString(result);
			
			onFlushRequestLogs(action, details, source_ip, result);
		}catch (Exception e) {
			String msg = ExceptionHelper.getDetails(e);			
			logger.debug(msg);	
		}		
	}
}

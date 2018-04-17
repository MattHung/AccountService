package com.accountservice.metric;

import java.util.HashSet;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import redis.clients.jedis.JedisCluster;

@Controller
public class MetricController {		
	private HashSet<String> paths;
	
	@Autowired
	private MetricService metricService;
	
	@Autowired
    private JedisCluster jedisCluster;
	
	@PostConstruct
	private void Init() {
		paths = new HashSet<String>();
		paths.add("/metric/status");
	}
	
	public boolean containsPath(String path) {
		return paths.contains(path);
	}
	
	@RequestMapping(value = "/metric/status", method = RequestMethod.GET)
	@ResponseBody
	public StatusInfo getStatusMetric() {	
		jedisCluster.set("zx_hello", "hi  welocme login!");
	    String value=jedisCluster.get("zx_hello");
	    
	    value = jedisCluster.get("fefe");
	    
	    return metricService.getOverview();
	}
}

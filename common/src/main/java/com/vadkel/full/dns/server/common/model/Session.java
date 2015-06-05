package com.vadkel.full.dns.server.common.model;

import java.util.HashMap;
import java.util.Map;

import com.vadkel.full.dns.server.common.interfaces.ISession;

public class Session implements ISession {
		
	private Map<String, Object> attributes;
	
	private Integer timeout = 1800;
	
	
	public Session(){
		super();
		attributes = new HashMap<String, Object>();
	}
	
	public Session(Integer timeout) {
		attributes = new HashMap<String, Object>();
		this.timeout = timeout;
	}

	@Override
	public void setAttribute(String key, Object value) {
		attributes.put(key, value);
	}

	@Override
	public Object getAttribute(String key) {
		return attributes.get(key);
	}
	
	@Override
	public Map<String, Object> getAttributes(){
		return attributes;
	}

	public Integer getTimeout() {
		return timeout;
	}

	public void setTimeout(Integer timeout) {
		this.timeout = timeout;
	}
	
	

}

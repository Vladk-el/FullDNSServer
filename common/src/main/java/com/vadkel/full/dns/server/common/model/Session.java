package com.vadkel.full.dns.server.common.model;

import java.util.HashMap;
import java.util.Map;

import com.vadkel.full.dns.server.common.interfaces.ISession;
import com.vadkel.full.dns.server.common.utils.config.Config;

public class Session implements ISession {
	
	private Map<String, String> attributes;
	
	private Integer timeout = 1800;
	
	
	public Session(){
		super();
		attributes = new HashMap<String, String>();
	}
	
	public Session(Integer timeout) {
		attributes = new HashMap<String, String>();
		this.timeout = timeout;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		
		sb.append(Config.SESSION + " : \n");
		for(String key : attributes.keySet()) {
			sb.append("\t");
			sb.append(key);
			sb.append("=");
			sb.append(attributes.get(key));
			sb.append("\n");
		}
		
		return sb.toString();
	}

	@Override
	public void setAttribute(String key, String value) {
		attributes.put(key, value);
	}

	@Override
	public String getAttribute(String key) {
		return attributes.get(key);
	}
	
	@Override
	public Map<String, String> getAttributes(){
		return attributes;
	}

	@Override
	public Integer getTimeout() {
		return timeout;
	}

	@Override
	public void setTimeout(Integer timeout) {
		this.timeout = timeout;
	}
	
	

}

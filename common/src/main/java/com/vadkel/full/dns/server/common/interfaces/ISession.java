package com.vadkel.full.dns.server.common.interfaces;

import java.util.Map;

public interface ISession {

	public void setAttribute(String key, Object value);
	
	public Object getAttribute(String key);
	
	public Map<String, Object> getAttributes();
	
	public Integer getTimeout();

	public void setTimeout(Integer timeout);
	
}

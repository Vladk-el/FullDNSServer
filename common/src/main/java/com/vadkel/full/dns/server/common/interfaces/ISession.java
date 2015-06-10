package com.vadkel.full.dns.server.common.interfaces;

import java.util.Map;

public interface ISession {

	public void setAttribute(String key, String value);
	
	public String getAttribute(String key);
	
	public Map<String, String> getAttributes();
	
	public Integer getTimeout();

	public void setTimeout(Integer timeout);
	
}

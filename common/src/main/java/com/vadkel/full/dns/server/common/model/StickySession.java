package com.vadkel.full.dns.server.common.model;

public class StickySession {

	private String id;
	
	private Integer serverId;
	
	private Integer timeout = 1800;
	
	public StickySession() {
		super();
	}
	
	public StickySession(String id, Integer serverId) {
		this.id = id;
		this.serverId = serverId;
	}
	
	public void resetTimeout() {
		
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Integer getServerId() {
		return serverId;
	}

	public void setServerId(Integer serverId) {
		this.serverId = serverId;
	}

	public Integer getTimeout() {
		return timeout;
	}

	public void setTimeout(Integer timeout) {
		this.timeout = timeout;
	}
	
	
}

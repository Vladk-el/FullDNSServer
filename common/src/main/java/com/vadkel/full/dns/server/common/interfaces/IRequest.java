package com.vadkel.full.dns.server.common.interfaces;

public interface IRequest {
	
	boolean init();
	
	void parse(String [] lines);
	
}

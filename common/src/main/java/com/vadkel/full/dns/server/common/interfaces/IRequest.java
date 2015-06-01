package com.vadkel.full.dns.server.common.interfaces;

public interface IRequest {
	
	void init();
	
	void parse(String [] lines);
	
}

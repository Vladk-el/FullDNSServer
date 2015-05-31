package com.vadkel.full.dns.server.common.interfaces;

import java.net.Socket;

import com.vadkel.full.dns.server.common.model.Request;

/**
 * 
 * @author Eliott
 *
 */
public interface IServer {
	
	static final String CONFIG_PATH = "conf/config.ini";

	void init();
		
	void run();
	
	void handle(Socket client);
	
	void manageSession(Request request);
	
	void execute(Request request);
	
}

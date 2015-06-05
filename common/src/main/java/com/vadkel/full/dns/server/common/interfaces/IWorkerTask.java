package com.vadkel.full.dns.server.common.interfaces;

import com.vadkel.full.dns.server.common.model.Request;

public interface IWorkerTask extends Runnable {

	void run();
	
	void handle();
	
	void manageSession(Request request);
	
	void execute(Request request);
	
}

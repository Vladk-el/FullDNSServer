package com.vadkel.full.dns.server.threadpool.interfaces;

public interface IPool {
	
	void addJob(Runnable job);
	
	Runnable nextJob();
	
}

package com.vadkel.full.dns.server.threadpool.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vadkel.full.dns.server.threadpool.interfaces.IPool;

public class Worker extends Thread {
	
	private static final Logger logger = LoggerFactory.getLogger(Worker.class);

	private Integer id;
	
	private IPool pool;
	
	public Worker(IPool pool, Integer id) {
		this.pool = pool;
		this.id = id;
	}
	
	@Override
	public void run() {
		while(true) {
			logger.info(id + "starts job");
			pool.nextJob().run();
			logger.info(id + "ends job");
		}
	}
}

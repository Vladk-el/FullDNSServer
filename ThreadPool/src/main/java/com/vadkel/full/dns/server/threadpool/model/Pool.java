package com.vadkel.full.dns.server.threadpool.model;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vadkel.full.dns.server.threadpool.interfaces.IPool;

public class Pool implements IPool {
	
	private static final Logger logger = LoggerFactory.getLogger(Pool.class);

	private Object monitor;
	
	private Integer workersNumber = Runtime.getRuntime().availableProcessors();
	
	private List<Worker> workers = new ArrayList<Worker>();
	
	private CircularStack stack = new CircularStack();
	
	public Pool() {
		init();
	}
	
	@Deprecated
	public Pool(Integer number) {
		workersNumber = number;
		init();
	}
	
	private void init() {
		monitor = new Object();
		for(int i = 0; i < workersNumber; i++) {
			workers.add(new Worker(this, i));
			workers.get(i).start();
		}
	}
	
	@Override
	public void addJob(Runnable job) {
		synchronized (monitor) {
			this.stack.add(new Node(job));
			monitor.notify();
		}
	}

	@Override
	public Runnable nextJob() {
		synchronized (monitor) {
			
			try {
				if(stack.getSize() == 0) {
					monitor.wait();
				}
			} catch (InterruptedException e) {
				logger.error("{}", e);
				return null;
			}
			
			return this.stack.remove().getJob();
		}
	}

	public Object getMonitor() {
		return monitor;
	}

	public void setMonitor(Object monitor) {
		this.monitor = monitor;
	}

	public Integer getWorkersNumber() {
		return workersNumber;
	}

	public void setWorkersNumber(Integer workersNumber) {
		this.workersNumber = workersNumber;
	}

	public List<Worker> getWorkers() {
		return workers;
	}

	public void setWorkers(List<Worker> workers) {
		this.workers = workers;
	}

	public CircularStack getStack() {
		return stack;
	}

	public void setStack(CircularStack stack) {
		this.stack = stack;
	}

}

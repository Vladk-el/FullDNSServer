package com.vadkel.full.dns.server.threadpool.model;

public class Node {

	private Runnable job;
	
	private Node next;
	
	
	public Node(Runnable job) {
		this.job = job;
	}
	
	public Node(Runnable job, Node next) {
		this.job = job;
		this.next = next;
	}

	public Runnable getJob() {
		return job;
	}

	public void setJob(Runnable job) {
		this.job = job;
	}

	public Node getNext() {
		return next;
	}

	public void setNext(Node next) {
		this.next = next;
	}
	
}

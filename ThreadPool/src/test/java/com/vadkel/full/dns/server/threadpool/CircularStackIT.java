package com.vadkel.full.dns.server.threadpool;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.vadkel.full.dns.server.threadpool.model.CircularStack;
import com.vadkel.full.dns.server.threadpool.model.Node;

public class CircularStackIT {
	
	private static final Logger logger = LoggerFactory.getLogger(CircularStackIT.class);
	
	CircularStack stack;
	Node node1 = new Node(null);
	Node node2 = new Node(null);
	Node node3 = new Node(null);
	Node node4 = new Node(null);
	Node node5 = new Node(null);

	@BeforeTest
	public void configure() {
		
		logger.info("Launch CircularStackIT Tests");
		
		stack = new CircularStack();
		
	}
	
	@Test(enabled=true)
	public void init() {
		assert stack.getLast() == null;
		assert stack.getSize() == 0;
		assert stack.remove() == null;
	}
	
	
	@Test(enabled = true, dependsOnMethods = {"init"})
	public void add() {
		
		stack.add(node1);
		assert stack.getLast() == node1;
		assert stack.getLast().getNext() == node1;
		assert stack.getSize() == 1;
		
		stack.add(node2);
		assert stack.getLast() == node1;
		assert stack.getLast().getNext() == node2;
		assert stack.getSize() == 2;
		
		stack.add(node3);
		assert stack.getLast() == node1;
		assert stack.getLast().getNext() == node3;
		assert stack.getSize() == 3;
		
		stack.add(node4);
		assert stack.getLast() == node1;
		assert stack.getLast().getNext() == node4;
		assert stack.getSize() == 4;
		
		stack.add(node5);
		assert stack.getLast() == node1;
		assert stack.getLast().getNext() == node5;
		assert stack.getSize() == 5;
			
	}
	
	@Test(enabled = true, dependsOnMethods = {"add"})
	public void toStringShouldBeNotNull() {
		assert stack.toString() != null;
		stack.print();	
	}
	
	@Test(enabled = true, dependsOnMethods = {"toStringShouldBeNotNull"})
	public void remove() {
		Node node = null;
		
		node = stack.remove();
		assert node == node1;
		assert stack.getLast() == node2;
		assert stack.getLast().getNext() == node5;
		assert stack.getSize() == 4;
		
		node = stack.remove();
		assert node == node2;
		assert stack.getLast() == node3;
		assert stack.getLast().getNext() == node5;
		assert stack.getSize() == 3;
		
		node = stack.remove();
		assert node == node3;
		assert stack.getLast() == node4;
		assert stack.getLast().getNext() == node5;
		assert stack.getSize() == 2;
		
		node = stack.remove();
		assert node == node4;
		assert stack.getLast() == node5;
		assert stack.getLast().getNext() == node5;
		assert stack.getSize() == 1;
		
		node = stack.remove();
		assert node == node5;
		assert stack.getLast() == null;
		assert stack.getSize() == 0;
	}
	
	@Test(enabled = true, dependsOnMethods = {"remove"}, expectedExceptions = NullPointerException.class)
	public void getNullOnNull() {
//		Should return a NullPointerException
		assert stack.getLast().getNext() == node5;
	}
	
	
	
}

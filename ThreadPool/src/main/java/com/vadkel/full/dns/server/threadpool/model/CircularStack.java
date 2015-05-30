package com.vadkel.full.dns.server.threadpool.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CircularStack {
	
	private static final Logger logger = LoggerFactory.getLogger(CircularStack.class);

	private Node last;

	private Integer size;

	public CircularStack() {
		last = null;
		size = 0;
	}

	/**
	 * add a node as first node if size == 0 add node as last and last.next else
	 * add last.next as node.next and last.next as node
	 * 
	 * @param node
	 */
	public void add(Node node) {
		if (size == 0) {
			last = node;
			last.setNext(node);
		} else {
			node.setNext(last.getNext());
			last.setNext(node);
		}
		size++;
	}

	/**
	 * remove last node
	 * 
	 * @return
	 */
	public Node remove() {
		Node toReturn = null;
		
		if(size > 0){
			if (size > 1) {
				Node current = last.getNext();
				while (current.getNext() != last) {
					current = current.getNext();
				}
				current.setNext(last.getNext());
				toReturn = last;
				last = current;
			} else if(size == 1) {
				toReturn = last;
				last = null;
			}
			
			size--;
		}
		
		return toReturn;
	}

	/**
	 * print statement of the stack
	 */
	public void print() {
		if(size > 0) {
			logger.info("Stack size : " + size);
			Node current = last.getNext();
			for(int i = 0; i < size; i++) {
				logger.info("\t" + i + " : " + current.toString());
				current = current.getNext();
			}
		} else {
			logger.info("Stack is empty !");
		}
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		
		if(size > 0) {
			builder.append("Stack size : " + size + "\n");
			Node current = last.getNext();
			for(int i = 0; i < size; i++) {
				builder.append("\t" + i + " : " + current.toString() + "\n");
				current = current.getNext();
			}
		} else {
			builder.append("Stack is empty !" + "\n");
		}
		
		return builder.toString();
	}

	public Node getLast() {
		return last;
	}

	public void setLast(Node last) {
		this.last = last;
	}

	public Integer getSize() {
		return size;
	}

	public void setSize(Integer size) {
		this.size = size;
	}

}

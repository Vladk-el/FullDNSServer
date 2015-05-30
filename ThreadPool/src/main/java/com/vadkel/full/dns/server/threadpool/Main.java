package com.vadkel.full.dns.server.threadpool;

import java.util.Random;
import java.util.Scanner;

import com.vadkel.full.dns.server.threadpool.model.Pool;
import com.vadkel.full.dns.server.threadpool.model.Worker;

/**
 * Main class
 *
 */
public class Main {
	
	
    public static void main( String[] args ) {
        System.out.println( "Hello ThreadPool!" );
        
        Pool pool = new Pool();
        
        
        addRunnable(pool);
		addRunnable(pool);
		addRunnable(pool);
		addRunnable(pool);
		addRunnable(pool);
		addRunnable(pool);
		addRunnable(pool);
		addRunnable(pool);
		addRunnable(pool);
		addRunnable(pool);
		addRunnable(pool);
		addRunnable(pool);
		
		controlPool(pool);
		
		addDynamicRunnable(pool);
    }
    
    public static void addRunnable(Pool pool){
		pool.addJob(new Runnable() {

			@Override
			public void run() {
				try {
					Random random = new Random();
					Thread.sleep(random.nextInt(5000));
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		});
	}
    
    public static void controlPool(Pool pool){
		System.out.println(pool.getStack().getSize() + " : ");
		pool.getStack().print();
	}
    
    public static void addDynamicRunnable(Pool pool){
		Scanner sc = new Scanner(System.in);
		
		while(true){
			sc.nextLine();
			addRunnable(pool);
			addRunnable(pool);
			addRunnable(pool);
			addRunnable(pool);
			addRunnable(pool);
			System.out.println("add a job ==> stack size : " + pool.getStack().getSize());
			controlWorkersState(pool);
		}
	}
	
	public static void controlWorkersState(Pool pool){
		for(Worker worker : pool.getWorkers()){
			System.out.println(worker.getName() + " is " + worker.getState());
		}
	}
    
}

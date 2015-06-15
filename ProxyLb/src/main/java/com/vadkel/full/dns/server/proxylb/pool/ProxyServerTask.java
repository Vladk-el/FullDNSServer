package com.vadkel.full.dns.server.proxylb.pool;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vadkel.full.dns.server.common.interfaces.IWorkerTask;
import com.vadkel.full.dns.server.common.model.Request;
import com.vadkel.full.dns.server.common.utils.config.Config;
import com.vadkel.full.dns.server.common.utils.socket.SocketUtils;
import com.vadkel.full.dns.server.proxylb.server.ProxyServer;

public class ProxyServerTask implements IWorkerTask {

	private static final Logger logger = LoggerFactory.getLogger(ProxyServerTask.class);
	
	private ProxyServer server;
	
	private Socket socket;
		
	private Map<String, String> loadBalancer;
	
	private Integer workerToBalance;
	
	
	public ProxyServerTask(ProxyServer server, Socket socket) {
		super();
		this.server = server;
		this.socket = socket;
		loadBalancer = new HashMap<>();
	}
	
	@Override
	public void run() {
		handle();
	}

	@Override
	public void handle() {
		Request request = new Request(socket);
		if(request.init()) {
			request.show();
			manageSession(request);
			execute(request);
			
			if(socket != null && !socket.isClosed()){
				try {
					socket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	// TODO DOING
	@Override
	public void manageSession(Request request) {
		/**
		 * find the host by the request (my.webstite ....
		 * ==> find the good load balancer
		 * 
		 * look for load balancer strategy
		 * 	if round_robin
		 * 		get the next server and balance to this one
		 * 	if sticky_session
		 * 		check if session exists
		 * 			yes : balance to the good server
		 * 			no : balance like round_robin and save session_key
		 * 
		 *  set currentLb && strategy
		 */
		
		/**
		 * Find and set the load balancer
		 */
		
		System.out.println(Config.LOAD_BALANCER + " : " + Config.DOMAIN + " : " + request.getHost());
		Integer currentLb = server.getConf().getNumberByTypeKeyAndLikeValue(
				Config.LOAD_BALANCER, 
				Config.DOMAIN, 
				request.getHost()
		);
		
		setLoadBalancer(server.getConf().getAsMap(Config.LOAD_BALANCER, currentLb));
		
		List<Integer> workers = new ArrayList<Integer>();
		
		for(String worker : getLoadBalancer().get(Config.WORKERS).split(",")) {
			workers.add(Integer.parseInt(worker));
		}
		
		switch (getLoadBalancer().get(Config.STRATEGY)) {
			case Config.STRATEGY_RR:
				if(workers.size() > 0) {
					Integer lastServer = server.getLastRRServer();
					if(lastServer != null) {
						lastServer = workers.get(0);
					}
					
					Iterator<Integer> it = workers.iterator();
					Integer cw = null;
					while(it.hasNext()) {
						cw = it.next();
						if(cw == lastServer) {
							break;
						}
					}
					setWorkerToBalance(it.hasNext() ? it.next() : workers.get(0));
					logger.info("worker to balance on : " + getWorkerToBalance());
				}
				else {
					logger.error("NO worker available !!!");
				}
				break;
				
			case Config.STRATEGY_SS:
				logger.error("NOT IMPLEMENTED YET");
				
				/**
				 * if sticky_session
					read data
					check if SS_KEY
						yes : 
							get the good server by a map of SS or map of map<Strint, String>
						no : 
							create a SS_KEY
							round_robin to find the good server
							create and put a SS in the SS map
							rajouter un cookie dans la réponse

				 */
				
				
				break;
	
			default:
				break;
		}
				
	}

	/**
	 * balance request to the good worker
	 */
	@Override
	public void execute(Request request) {
		
		Socket balancedSocket = null;
		StringBuilder sb = new StringBuilder();
		
		/**
		 * Connect to the balanced server
		 */
		
		try {
			
			balancedSocket = new Socket(
					server.getConf().get(Config.WORKER, getWorkerToBalance(), Config.IP),
					Integer.parseInt(server.getConf().get(Config.WORKER, getWorkerToBalance(), Config.PORT))
				);
			
		} catch (Exception e) {
			logger.error("Error on connect to the balanced server : ", e);
		}
		
		/**
		 * Write to the balanced server
		 */
		
		for(String str : request.getDatas()) {
			sb.append(str + "\r\n");
		}
		sb.append("\r\n");
		
		System.out.println("Datas send to balanced server : " + sb.toString());
		
		try {
			SocketUtils.writeDatasIntoSocket(balancedSocket, sb.toString());
		} catch (IOException e) {
			logger.error("Error on sending datas to the balanced server : ", e);
		}
		
		
		/**
		 * Read from the balanced server
		 */
		
		String[] lines = null;
		try {
			lines = SocketUtils.getDatasToStringTab(balancedSocket);
		} catch (Exception e) {
			logger.error("Error on reading response from the balanced server : ", e);
		}
		
		
		/**
		 * Write response to the client socket
		 */
		
		sb.setLength(0);
		
		for(String line : lines) {
			sb.append(line + "\r\n");
			if(line.contains(Config.CONTENT_TYPE)) {
				sb.append("\r\n");
			}
		}
		
		System.out.println("Response from the balanced server : " + sb.toString());
		
		try {
			SocketUtils.writeDatasIntoRequest(request, sb.toString());
		} catch (IOException e) {
			logger.error("Error on retrieving datas to the client server : ", e);
		}
	}

	public ProxyServer getServer() {
		return server;
	}

	public void setServer(ProxyServer server) {
		this.server = server;
	}

	public Socket getSocket() {
		return socket;
	}

	public void setSocket(Socket socket) {
		this.socket = socket;
	}

	public Map<String, String> getLoadBalancer() {
		return loadBalancer;
	}

	public void setLoadBalancer(Map<String, String> loadBalancer) {
		this.loadBalancer = loadBalancer;
	}

	public Integer getWorkerToBalance() {
		return workerToBalance;
	}

	public void setWorkerToBalance(Integer workerToBalance) {
		this.workerToBalance = workerToBalance;
	}

}

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
import com.vadkel.full.dns.server.common.model.Cookie;
import com.vadkel.full.dns.server.common.model.Request;
import com.vadkel.full.dns.server.common.model.StickySession;
import com.vadkel.full.dns.server.common.utils.config.Config;
import com.vadkel.full.dns.server.common.utils.session.SessionUtils;
import com.vadkel.full.dns.server.common.utils.socket.SocketUtils;
import com.vadkel.full.dns.server.proxylb.server.ProxyServer;

public class ProxyServerTask implements IWorkerTask {

	private static final Logger logger = LoggerFactory.getLogger(ProxyServerTask.class);
	
	private ProxyServer server;
	
	private Socket socket;
		
	private Map<String, String> loadBalancer;
	
	private Integer workerToBalance;
	
	private StickySession stickySession;
	
	private Map<String, StickySession> stickySessions;
	
	private Cookie stickySessionCookie;
	
	
	public ProxyServerTask(ProxyServer server, Socket socket) {
		super();
		this.server = server;
		this.socket = socket;
		loadBalancer = new HashMap<>();
		workerToBalance = null;
		stickySession = null;
		stickySessions = new HashMap<>();
		stickySessionCookie = null;
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

	/**
	 * Manage the sticky session and determine the good server to execute on
	 */
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
		Integer lastServer = null;
		
		for(String worker : getLoadBalancer().get(Config.WORKERS).split(",")) {
			workers.add(Integer.parseInt(worker));
		}
		
		switch (getLoadBalancer().get(Config.STRATEGY)) {
			/**
			 * Round robin case
			 */
			case Config.STRATEGY_RR:
				
				lastServer = server.getLastRRServer();
				
				setWorkerToBalance(getRoundRobinServer(workers, lastServer));
				
				server.setLastRRServer(workerToBalance);
				
				break;
			
			/**
			 * Sticky session case
			 */
			case Config.STRATEGY_SS:				

				String stickySessionId = null;
				for(Cookie cookie : request.getCookies()) {
					if(cookie.getAttribute(Config.STICKY_SESSION_ID) != null) {
						stickySessionId = cookie.getAttribute(Config.STICKY_SESSION_ID);
						setStickySessionCookie(cookie);
						break;
					}
				}
				
				if(stickySessionId != null && stickySessions.get(stickySessionId) != null) {
					setStickySession(stickySessions.get(stickySessionId));
					setWorkerToBalance(getStickySession().getServerId());
				} else {
					lastServer = server.getLastRRServer();
					setWorkerToBalance(getRoundRobinServer(workers, lastServer));
					stickySessionId = SessionUtils.generateSessionKey(server.getConf().get(Config.PROXY, Config.NAME));
					server.setLastSSServer(workerToBalance);
					
					Map<String, String> cookieProperties = new HashMap<>();
					cookieProperties.put(Config.STICKY_SESSION_ID, stickySessionId);
					
					setStickySessionCookie(new Cookie(cookieProperties, true));
					
					setStickySession(new StickySession(stickySessionId, workerToBalance));
					stickySessions.put(stickySessionId, stickySession);
				}
				
				break;
	
			default:
				logger.error("UNRECOGNIZED STRATEGY, PLEASE CONTACT YOUR ADMIN SYSTEM");
				break;
		}
		
		logger.info("worker to balance on : " + getWorkerToBalance());
				
	}

	/**
	 * Balance request to the good worker
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
				if(stickySessionCookie.isNeedToBeSent()) {
					sb.append(stickySessionCookie.getReadyToUse(
							SessionUtils.getDateForCookie(server.getConf().get(Config.PROXY, Config.TIMEOUT))
						));
				}
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
	
	/**
	 * Get the next server with round_robin way
	 */
	private Integer getRoundRobinServer(List<Integer> workers, Integer lastServer) {
		if(workers.size() > 0) {

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
			
			return it.hasNext() ? it.next() : workers.get(0);
		}
		else {
			logger.error("NO worker available !!!");
			return null;
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

	public StickySession getStickySession() {
		return stickySession;
	}

	public void setStickySession(StickySession stickySession) {
		this.stickySession = stickySession;
	}

	public Map<String, StickySession> getStickySessions() {
		return stickySessions;
	}

	public void setStickySessions(Map<String, StickySession> stickySessions) {
		this.stickySessions = stickySessions;
	}

	public Cookie getStickySessionCookie() {
		return stickySessionCookie;
	}

	public void setStickySessionCookie(Cookie stickySessionCookie) {
		this.stickySessionCookie = stickySessionCookie;
	}

}

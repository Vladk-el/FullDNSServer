package com.vadkel.full.dns.server.sessionserver.pool;

import java.io.IOException;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vadkel.full.dns.server.common.interfaces.ISession;
import com.vadkel.full.dns.server.common.interfaces.IWorkerTask;
import com.vadkel.full.dns.server.common.model.Request;
import com.vadkel.full.dns.server.common.model.Session;
import com.vadkel.full.dns.server.common.utils.config.Config;
import com.vadkel.full.dns.server.common.utils.socket.SocketUtils;
import com.vadkel.full.dns.server.sessionserver.server.SessionServer;

public class SessionServerTask implements IWorkerTask {

	private static final Logger logger = LoggerFactory.getLogger(SessionServerTask.class);
	
	private SessionServer server;
	
	private Socket socket;
	
	private ISession session;
	
	public SessionServerTask(SessionServer server, Socket socket) {
		super();
		this.server = server;
		this.socket = socket;
	}
	
	@Override
	public void run() {
		handle();
	}

	@Override
	public void handle() {
		Request request = new Request(socket);
		if(request.init()) {
			//request.show();
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

	@Override
	public void manageSession(Request request) {
		
		for(String str : request.getDatas()) {
			if(str.contains(Config.SESSION_COOKIE_ID)) {
				String key = str.replaceAll(Config.SESSION_COOKIE_ID + "=", "");
				request.setSessionId(key);
				if(server.getSessions().get(key) == null) {
					server.getSessions().put(key, new Session());
				}
				session = server.getSessions().get(key);
			}
			
			else if(str.contains(Config.SESSION_COOKIE_SET_PROPERTY)) {
				String [] tab = str.replaceAll(
									Config.SESSION_COOKIE_SET_PROPERTY, 
									"").split("=");
				if(tab.length == 2) {
					session.setAttribute(tab[0], tab[1]);
					logger.info("Setted property : " + tab[0] + "=" + tab[1]);
				}
			}
			
			else if(str.contains(Config.SESSION_COOKIE_GET_PROPERTY)) {
				request.getWantedProperties().put(str.replaceAll(Config.SESSION_COOKIE_GET_PROPERTY, ""), null);
			}
		}	
		
		logger.info("Requested session : " + request.getSessionId());
	}

	@Override
	public void execute(Request request) {

		//logger.info(server.getSessions().get(request.getSessionId()).toString());
		
		StringBuilder sb = new StringBuilder();
		
		sb.append(Config.SESSION_COOKIE_ID + "=" + request.getSessionId() + "\r\n");
		
		for(String key : request.getWantedProperties().keySet()) {
			if(session.getAttribute(key) != null) {
				sb.append(Config.SESSION_COOKIE_GET_PROPERTY + key + "=" + 
						   session.getAttribute(key) + "\r\n");
			}
		}
		
		//System.out.println("Response : " + sb.toString());
		
		/**
		 * Write response
		 */
		
		try {
			
			SocketUtils.writeDatasIntoRequest(request, sb.toString());

		} catch(Exception e) {
			logger.error("", e);
		}
	}

}

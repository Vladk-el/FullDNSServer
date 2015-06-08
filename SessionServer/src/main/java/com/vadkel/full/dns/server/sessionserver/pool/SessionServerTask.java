package com.vadkel.full.dns.server.sessionserver.pool;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vadkel.full.dns.server.common.interfaces.ISession;
import com.vadkel.full.dns.server.common.interfaces.IWorkerTask;
import com.vadkel.full.dns.server.common.model.Cookie;
import com.vadkel.full.dns.server.common.model.Request;
import com.vadkel.full.dns.server.common.model.Session;
import com.vadkel.full.dns.server.common.model.SessionRequest;
import com.vadkel.full.dns.server.common.utils.config.Config;
import com.vadkel.full.dns.server.common.utils.session.SessionUtils;
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
		SessionRequest request = new SessionRequest(socket);
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

	@Override
	public void manageSession(Request request) {
		
		for(String str : request.getDatas()) {
			if(str.contains(Config.SESSION_COOKIE_ID)) {
				String key = str.replaceAll(Config.SESSION_COOKIE_ID, "");
				request.setSessionId(key);
				if(server.getSessions().get(key) != null) {
					session = server.getSessions().get(key);
				} else {
					server.getSessions().put(key, new Session());
					session = server.getSessions().get(key);
				}
			}
			
			else if(str.contains(Config.SESSION_COOKIE_SET_PROPERTY)) {
				String [] tab = str.replaceAll(
									Config.SESSION_COOKIE_SET_PROPERTY, 
									"").split("=");
				if(tab.length == 2) {
					session.setAttribute(tab[0], tab[1]);
				}
			}
			
			else if(str.contains(Config.SESSION_COOKIE_GET_PROPERTY)) {
				((SessionRequest)request).getWantedProperties().add(str.replaceAll(Config.SESSION_COOKIE_GET_PROPERTY, ""));
			}
		}
		
		execute(request);
	}

	@Override
	public void execute(Request request) {

		try {
			
			request.getWriter().writeBytes(Config.SESSION_COOKIE_ID + "=" + request.getSessionId() + "\r\n");
			
			for(String key : ((SessionRequest)request).getWantedProperties()) {
				request.getWriter().writeBytes(Config.SESSION_COOKIE_GET_PROPERTY + 
											   key + "=" + 
											   server.getSessions().get(request.getSessionId()).getAttribute(key) + 
											   "\r\n");
			}
			
			request.getWriter().flush();
		} catch(Exception e) {
			logger.error("", e);
		} finally {
			try {
				request.getWriter().close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}

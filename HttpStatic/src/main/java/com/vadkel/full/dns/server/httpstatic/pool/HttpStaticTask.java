package com.vadkel.full.dns.server.httpstatic.pool;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vadkel.full.dns.server.common.interfaces.ISession;
import com.vadkel.full.dns.server.common.interfaces.IWorkerTask;
import com.vadkel.full.dns.server.common.model.Cookie;
import com.vadkel.full.dns.server.common.model.Request;
import com.vadkel.full.dns.server.common.model.Session;
import com.vadkel.full.dns.server.common.utils.config.Config;
import com.vadkel.full.dns.server.common.utils.session.SessionUtils;
import com.vadkel.full.dns.server.common.utils.socket.SocketUtils;
import com.vadkel.full.dns.server.httpstatic.server.HttpServer;

public class HttpStaticTask implements IWorkerTask {
	
	private static final Logger logger = LoggerFactory.getLogger(HttpStaticTask.class);
	
	private HttpServer server;
	
	private Socket socket;

	public HttpStaticTask(HttpServer server, Socket socket) {
		super();
		this.server = server;
		this.socket = socket;
	}
	
	@Override
	public void run() {
		handle();
	}
	
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

	// TODO
	public void manageSession(Request request) {
		
		/**
		 * check local / remote
		 * if local
		 * 	use sessions
		 * else remote
		 * 	new connexion to SessionServer
		 * 	write request.wantedProperties
		 * 
		 */
		
		boolean needSessionCookie = (request.getSessionId() == null) ? true : false;
				
		if(needSessionCookie) {
			request.setSessionId(SessionUtils.generateSessionKey(server.getConf().get(Config.WORKER, Config.NAME)));
		}
		
		ISession session = null;
		
		switch (server.getConf().get(Config.SESSION, Config.MODE)) {
		
			case Config.LOCAL:
				
				if((session = server.getSessions().get(request.getSessionId())) == null) {
					server.getSessions().put(request.getSessionId(), new Session());
					session = server.getSessions().get(request.getSessionId());
				}
				
				for(String str : request.getPropertiesToSet().keySet()) {
					session.setAttribute(str, request.getPropertiesToSet().get(str));
				}
				
				for(String str : request.getWantedProperties().keySet()) {
					request.getWantedProperties().put(str, session.getAttribute(str));
				}
				
				break;
				
			case Config.REMOTE:
				
				Socket sessionSocket = null;
				
				StringBuilder sb = new StringBuilder();
				
				/**
				 * Jeu de test
				 */
				
				
				request.getPropertiesToSet().put("TEST", "HAHAHAHA");
				request.getPropertiesToSet().put("TEST2", "HAHAHAHAHA2");
				request.getPropertiesToSet().put("TEST3", "HAHAHAHAHAHA3");
				request.getPropertiesToSet().put("TEST4", "HAHAHAHAHAHAHA4");
				
				request.getWantedProperties().put("TEST", null);
				request.getWantedProperties().put("TEST3", null);
				
				
				/**
				 * Connect to SessionServer
				 */
				
				try {
					sessionSocket = new Socket(
							server.getConf().get(Config.SESSION, Config.IP),
							Integer.parseInt(server.getConf().get(Config.SESSION, Config.PORT))
						);
					
//					sessionSocket = new Socket();
//					socket.connect(
//							new InetSocketAddress(server.getConf().get(Config.SESSION, Config.IP),
//									Integer.parseInt(server.getConf().get(Config.SESSION, Config.PORT))), 
//							1000);
				} catch (Exception e) {
					logger.error("Error on connect to the Session server : ", e);
				}
				
				
				/**
				 * Write
				 */
				
				sb.append(Config.SESSION_COOKIE_ID + "=" + request.getSessionId() + "\r\n");
				
				for(String key : request.getPropertiesToSet().keySet()) {
					if(request.getPropertiesToSet().get(key) != null) {
						sb.append(Config.SESSION_COOKIE_SET_PROPERTY + key + "=" + 
								   request.getPropertiesToSet().get(key) + "\r\n");
					}
				}
				
				for(String key : request.getWantedProperties().keySet()) {
					sb.append(Config.SESSION_COOKIE_GET_PROPERTY + key +  "\r\n");
				}
				
				System.out.println("Datas send to Session server : " + sb.toString());
				
				try {
					SocketUtils.writeDatasIntoSocket(sessionSocket, sb.toString());
				} catch (IOException e) {
					logger.error("Error on sending datas to the Session server : ", e);
				}
				
				
				/**
				 * Read
				 */
				
				String[] lines = null;
				try {
					lines = SocketUtils.getDatasToStringTab(sessionSocket);
				} catch (Exception e) {
					logger.error("Error on reading response from the Session server : ", e);
				}
				
				System.out.println("Response from the Session server : ");
				
				for(String line : lines) {
					if(line.contains(Config.SESSION_COOKIE_GET_PROPERTY)) {
						System.out.println("\t" + line);
						String [] tab = line.replaceAll(Config.SESSION_COOKIE_GET_PROPERTY, "")
											.split("=");
						if(tab.length == 2) {
							request.getWantedProperties().put(tab[0], tab[1]);
						}
					}
				}
				
				break;
	
			default:
				logger.error("No session strategy defined for this server.");
				break;
		}
		
		if(needSessionCookie) {
			Cookie cookie = new Cookie(null, true);
			cookie.getAttributes().put(Config.SESSION_COOKIE_ID, request.getSessionId());
			request.getCookies().add(cookie);
		}
		
	}

	public void execute(Request request) {

		File file = new File(
				server.getConf().get(
						Config.DOMAIN,
						server.getConf().getNumberByTypeKeyValue(
							Config.DOMAIN, Config.NAME, request.getHost()
						),
						Config.DOCUMENT_ROOT
					) + 
					request.getPath()
				);
				
		if(file.exists()) {
			try {
				// Header 
				request.getWriter().writeBytes("HTTP/1.1 200 OK\r\n");
								
				// add cookies
				for(Cookie cookie : request.getCookies()) {
					if(cookie.isNeedToBeSent()) {
						request.getWriter().writeBytes(
								cookie.getReadyToUse(SessionUtils.getDateForCookie(
									server.getConf().get(Config.SESSION, Config.TIMEOUT))
								)
							);
					}
				}
				
				if(file.isDirectory()) {
					showDirectory(file, request);
				} else if(file.isFile()) {
					downloadFile(file, request);
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
		} else {
			logger.error("ressource inexistante : " + file.getPath());
			try {
				request.getWriter().writeBytes("ressource inexistante");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void showDirectory(File file, Request request) throws IOException {
		
		// Header
		request.getWriter().writeBytes("Content-Type: text/html\r\n\r\n");
	
		// Content
		File[] files = file.listFiles();
		String present = "<h1>Index of " + file.getName() + "</h1>";
		request.getWriter().write(present.getBytes());

		for(File f : files){
			StringBuilder sb = new StringBuilder();
			sb.append("<p><a href='");
			sb.append(request.getPath());
			sb.append((request.getPath().endsWith("/") ? "" : "/"));
			sb.append(f.getName());
			sb.append("'>");
			sb.append(f.getName());
			sb.append("</a></p>");

			request.getWriter().write(sb.toString().getBytes());
		}
	}

	public void downloadFile(File file, Request request) throws IOException {
		
		// Header
		request.getWriter().writeBytes("Content-Type: octet/stream\r\n\r\n");
		
		FileInputStream in = new FileInputStream(file);
		byte[] buffer = new byte[4096];

		while (in.read(buffer) > 0) {
			request.getWriter().write(buffer);
		}
		in.close();
	}
	
	

}

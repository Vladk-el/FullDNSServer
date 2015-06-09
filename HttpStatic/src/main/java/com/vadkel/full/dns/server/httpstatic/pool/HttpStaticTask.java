package com.vadkel.full.dns.server.httpstatic.pool;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vadkel.full.dns.server.common.interfaces.IWorkerTask;
import com.vadkel.full.dns.server.common.model.Cookie;
import com.vadkel.full.dns.server.common.model.Request;
import com.vadkel.full.dns.server.common.utils.config.Config;
import com.vadkel.full.dns.server.common.utils.session.SessionUtils;
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
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
	}

	// TODO
	public void manageSession(Request request) {
		
		if(request.getSessionId() == null) {
			request.setSessionId(SessionUtils.generateSessionKey(server.getConf().get(Config.WORKER, Config.NAME)));
		}
		
		/**
		 * check local / remote
		 * if local
		 * 	use sessions
		 * else remote
		 * 	new connexion to SessionServer
		 * 	write request.wantedProperties
		 * 
		 */
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
					request.getWriter().writeBytes(
							cookie.getReadyToUse(SessionUtils.getDateForCookie(
										server.getConf().get(Config.SESSION, Config.TIMEOUT)
									)
								)
						);
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

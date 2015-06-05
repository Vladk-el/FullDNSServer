package com.vadkel.full.dns.server.common.model;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vadkel.full.dns.server.common.interfaces.IRequest;
import com.vadkel.full.dns.server.common.utils.config.Config;

public class Request implements IRequest {

	private static final Logger logger = LoggerFactory.getLogger(Request.class);

	private Socket socket;
	
	private String sessionId;
	
	private String host;
	
	private String userAgent;
	
	private String path;
	
	private List<String> datas;
	
	private Map<String, String> cookies;
	
	private DataOutputStream writer;
	

	public Request(Socket socket) {
		setSocket(socket);
		datas = new ArrayList<>();
		cookies = new HashMap<>();
	}

	@Override
	public boolean init() {
		byte[] messageByte = new byte[1000];
		int bytesRead;
		StringBuilder sb = new StringBuilder();
		String stringRead;

		try {
			DataInputStream in = new DataInputStream(socket.getInputStream());

			while (true) {
				bytesRead = in.read(messageByte);
				stringRead = new String(messageByte, 0, bytesRead);
				sb.append(stringRead);
				if (stringRead.length() != 100) {
					break;
				}
			}
			
			String [] lines = sb.toString().split("\r\n");
			
			parse(lines);
			
			setWriter(new DataOutputStream(socket.getOutputStream()));
			
			return true;
		} catch (Exception e) {
			logger.error("Error on init() Request", e);
			return false;
		}
	}
	
	@Override
	public void parse(String [] lines) {
		
		for(String s : lines) {
			if(!s.isEmpty()) {
				//logger.info(s);

				if(s.startsWith(Config.HOST)) {
					setHost(s.replace(Config.HOST, ""));
				}
				else if(s.startsWith(Config.USER_AGENT)) {
					setUserAgent(s.replace(Config.USER_AGENT, ""));
				}
				else if(s.endsWith(Config.HTTP)) {
					String [] header = s.split(" ");
					if(header.length == 3) {
						setPath(header[1]);
					} else {
						setPath(
								s.replaceAll(Config.GET, "").
								  replaceAll(Config.POST, "").
								  replaceAll(Config.HTTP, "").
								  trim()
								);
					}
					
				} // only take cookies like key=value
				else if(s.startsWith(Config.COOKIE)) { 
					String [] cookie = s.replace(Config.COOKIE, "").split("=");
					if(cookie.length == 2){
						getCookies().put(cookie[0], cookie[1]);
					}
				}
			}
			getDatas().add(s);
		}
	}
	
	public void show() {
		StringBuilder sb = new StringBuilder();
		sb.append("Request : " + "\n");
		
		sb.append("\t" + "SESSION ID :" + "\n");
			sb.append("\t\t" + getSessionId() + "\n");
		
		sb.append("\t" + Config.HOST + "\n");
			sb.append("\t\t" + getHost() + "\n");
		
		sb.append("\t" + Config.USER_AGENT + "\n");
			sb.append("\t\t" + getUserAgent() + "\n");
			
		sb.append("\t" + Config.PATH + "\n");
			sb.append("\t\t" + getPath() + "\n");
		
		sb.append("\t" + Config.COOKIE + "\n");
		for(String cookie : getCookies().keySet()) {
			sb.append("\t\t" + cookie + " : " + getCookies().get(cookie) + "\n");
		}
		
		sb.append("\tAll request :" + "\n");
		for(String data : getDatas()) {
			sb.append("\t\t" + data + "\n");
		}
		
		logger.info(sb.toString());
	}

	public Socket getSocket() {
		return socket;
	}

	public void setSocket(Socket socket) {
		this.socket = socket;
	}

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getUserAgent() {
		return userAgent;
	}

	public void setUserAgent(String userAgent) {
		this.userAgent = userAgent;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public static Logger getLogger() {
		return logger;
	}

	public List<String> getDatas() {
		return datas;
	}

	public void setDatas(List<String> datas) {
		this.datas = datas;
	}

	public Map<String, String> getCookies() {
		return cookies;
	}

	public void setCookies(Map<String, String> cookies) {
		this.cookies = cookies;
	}

	public DataOutputStream getWriter() {
		return writer;
	}

	public void setWriter(DataOutputStream writer) {
		this.writer = writer;
	}

}

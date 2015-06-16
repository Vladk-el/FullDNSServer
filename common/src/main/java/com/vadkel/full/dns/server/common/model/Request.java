package com.vadkel.full.dns.server.common.model;

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
import com.vadkel.full.dns.server.common.utils.socket.SocketUtils;

public class Request implements IRequest {

	private static final Logger logger = LoggerFactory.getLogger(Request.class);

	private Socket socket;
	
	private String sessionId;
	
	private String host;
	
	private String userAgent;
	
	private String path;
	
	private List<String> datas;
	
	private List<Cookie> cookies;
	
	private DataOutputStream writer;
	
	private Map<String, String> wantedProperties;
	
	private Map<String, String> propertiesToSet;
	

	public Request(Socket socket) {
		setSocket(socket);
		datas = new ArrayList<>();
		cookies = new ArrayList<>();
		wantedProperties = new HashMap<>();
		propertiesToSet = new HashMap<>();
	}

	@Override
	public boolean init() {

		try {
			
			String [] lines = SocketUtils.getDatasToStringTab(socket);
			
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
					String host = s.replace(Config.HOST, "");
					if(host.contains(":")){
						host = host.substring(0, host.indexOf(":"));
					}
					setHost(host);
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
					
				}
				else if(s.startsWith(Config.COOKIE)) { 
					Cookie cookie = new Cookie(s);
					if(cookie.getAttribute(Config.SESSION_COOKIE_ID) != null) {
						this.sessionId = cookie.getAttribute(Config.SESSION_COOKIE_ID);
					}
					getCookies().add(cookie);
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
		for(Cookie cookie : getCookies()) {
			sb.append("\t\t" + cookie + "\n");
		}
		
		sb.append("\t" + "wantedProperties" + "\n");
		for(String wp : getWantedProperties().keySet()) {
			sb.append("\t\t" + wp + " : " + getWantedProperties().get(wp) + "\n");
		}
		
		sb.append("\t" + "propertiesToSet" + "\n");
		for(String wp : getPropertiesToSet().keySet()) {
			sb.append("\t\t" + wp + " : " + getWantedProperties().get(wp) + "\n");
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

	public List<Cookie> getCookies() {
		return cookies;
	}

	public void setCookies(List<Cookie> cookies) {
		this.cookies = cookies;
	}

	public DataOutputStream getWriter() {
		return writer;
	}

	public void setWriter(DataOutputStream writer) {
		this.writer = writer;
	}

	public Map<String, String> getWantedProperties() {
		return wantedProperties;
	}

	public void setWantedProperties(Map<String, String> wantedProperties) {
		this.wantedProperties = wantedProperties;
	}

	public Map<String, String> getPropertiesToSet() {
		return propertiesToSet;
	}

	public void setPropertiesToSet(Map<String, String> propertiesToSet) {
		this.propertiesToSet = propertiesToSet;
	}
	

}

package com.vadkel.full.dns.server.common.model;

import java.io.DataInputStream;
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
	
	private String host;
	
	private String userAgent;
	
	private List<String> datas;
	
	private Map<String, String> cookies;
	

	public Request(Socket socket) {
		setSocket(socket);
		datas = new ArrayList<>();
		cookies = new HashMap<>();
		init();
	}

	@Override
	public void init() {
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
			
		} catch (Exception e) {
			logger.error("Error on init() Request", e);
		} finally {

		}
	}
	
	@Override
	public void parse(String [] lines) {
		
		for(String s : lines) {
			if(!s.isEmpty()) {
				//logger.info(s);

				if(s.startsWith(Config.HOST)) {
					setHost(s);
				}
				else if(s.startsWith(Config.USER_AGENT)) {
					setUserAgent(s);
				}
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
		
		sb.append("\t" + Config.HOST + "\n");
			sb.append("\t\t" + getHost() + "\n");
		
		sb.append("\t" + Config.USER_AGENT + "\n");
			sb.append("\t\t" + getUserAgent() + "\n");
		
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

}

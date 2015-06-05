package com.vadkel.full.dns.server.common.model;

import java.util.Map;

import com.vadkel.full.dns.server.common.utils.config.Config;
import com.vadkel.full.dns.server.common.utils.session.SessionUtils;

public class Cookie {
	
	private String name;

	private String path;
	
	private String domain;
	
	private String expires;
	
	private Map<String, String> attributes;
	
	// TODO finir cookies
	public Cookie(String cookie) {
		/*
		 * Config.SET_COOKIE + 
									"nom=" + 
									request.getSessionId() + 
									" expires=" + 
									SessionUtils.getDateForCookie(
										Integer.parseInt(
											server.getConf().get(Config.SESSION, Config.TIMEOUT)
										)
									) + 
									" path=" + 
									this.getClass().getCanonicalName()	
		 */
		
		String [] tab = cookie.split(";"); 
		
		for(String str : tab) {
			str = str.trim();
			String [] elements = str.split("=");
			if(elements.length == 2){
				if(elements[0].equalsIgnoreCase("name")) {
					setName(elements[1]);
				} else if(elements[0].equalsIgnoreCase("path")) {
					setName(elements[1]);
				} else if(elements[0].equalsIgnoreCase("domain")) {
					setName(elements[1]);
				} else if(elements[0].equalsIgnoreCase("expires")) {
					setName(elements[1]);
				} else {
					attributes.put(elements[0], elements[1]);
				}
			}
			
		}
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public String getExpires() {
		return expires;
	}

	public void setExpires(String expires) {
		this.expires = expires;
	}

	public Map<String, String> getAttributes() {
		return attributes;
	}

	public void setAttributes(Map<String, String> attributes) {
		this.attributes = attributes;
	}
	
	
}

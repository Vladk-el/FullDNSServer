package com.vadkel.full.dns.server.common.model;

import java.util.Map;

import com.vadkel.full.dns.server.common.utils.config.Config;

public class Cookie {
	
	private String path;
		
	private String expires;
	
	private Map<String, String> attributes;
	
	public Cookie(String expires, Map<String, String> attributes) {
		setPath(Config.DEFAULT_COOKIE_PATH);
		setExpires(expires);
		setAttributes(attributes);
	}
	
		
	// TODO finir cookies
	public Cookie(String cookie) {
		
		String [] tab = cookie.split(";"); 
		
		for(String str : tab) {
			str = str.trim();
			String [] elements = str.split("=");
			if(elements.length == 2){
				if(elements[0].equalsIgnoreCase("path")) {
					setPath(elements[1]);
				} else if(elements[0].equalsIgnoreCase("expires")) {
					setExpires(elements[1]);
				} else {
					attributes.put(elements[0], elements[1]);
				}
			}
			
		}
	}
	
	@Override
	public String toString(){
		StringBuilder sb = new StringBuilder();
		 
		 if(getPath() != null) {
			 sb.append("path=" + getPath());
			 sb.append("; ");
		 }
		 
		 if(getExpires() != null) {
			 sb.append("expires=" + getExpires());
			 sb.append("; ");
		 }
		 
		 for(String attr : attributes.keySet()) {
			 sb.append(attr + "=" + attributes.get(attr));
			 sb.append("; ");
		 }
		
		return sb.toString();
	}
	
	public String getReadyToUse(String expires) {
		
		String toReturn = Config.SET_COOKIE + toString();
		
		if(getPath() == Config.DEFAULT_COOKIE_PATH) {
			if(getExpires() != null) {
				return toReturn.replaceAll(getExpires(), expires);
			} else {
				return toReturn + "expires=" + expires + ";";
			}
		}
		return toReturn;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
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

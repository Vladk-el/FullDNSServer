package com.vadkel.full.dns.server.common.model;

import java.util.HashMap;
import java.util.Map;

import com.vadkel.full.dns.server.common.utils.config.Config;

public class Cookie {
	
	private Map<String, String> attributes;
	
	private boolean needToBeSent = false;
	
	public Cookie(Map<String, String> attributes, boolean needToBeSent) {
		setAttributes(attributes == null ? new HashMap<>() : attributes);
		this.needToBeSent = needToBeSent;
	}
		
	public Cookie(String cookie) {
		attributes = new HashMap<>();
		String [] tab = cookie.replaceAll(Config.COOKIE, "").split(";"); 
		
		for(String str : tab) {
			str = str.trim();
			String [] elements = str.split("=");
			if(elements.length == 2) {				
				attributes.put(elements[0], elements[1]);
			}
		}
	}
	
	@Override
	public String toString(){
		StringBuilder sb = new StringBuilder();
		 
		 for(String attr : attributes.keySet()) {
			 sb.append(attr + "=" + attributes.get(attr));
			 sb.append("; ");
		 }
		
		return sb.toString();
	}
	
	public String getReadyToUse(String expires) {
		return Config.SET_COOKIE + toString() + "expires=" + expires + ";\r\n";
	}
	
	public String getAttribute(String key) {
		return attributes.get(key);
	}
	
	public void setAttribute(String key, String value) {
		attributes.put(key, value);
	}

	public Map<String, String> getAttributes() {
		return attributes;
	}

	public void setAttributes(Map<String, String> attributes) {
		this.attributes = attributes;
	}
	
	public boolean isNeedToBeSent() {
		return needToBeSent;
	}

	public void setNeedToBeSent(boolean needToBeSent) {
		this.needToBeSent = needToBeSent;
	}
	
}

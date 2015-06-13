package com.vadkel.full.dns.server.common.utils.config;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author Eliott
 *
 */
public class Config {

	private static final Logger logger = LoggerFactory.getLogger(Config.class);

	public static final String DEFAULT_CONFIG_PATH = "conf/config.ini";
	
	public static final String SESSION_COOKIE_ID = "VladkelHttpStaticKey";
	
	public static final String SESSION_COOKIE_SET_PROPERTY = "VLADKELHttpStaticSetProperty: ";
	
	public static final String SESSION_COOKIE_GET_PROPERTY = "VLADKELHttpStaticGetProperty: ";
	
	public static final String DEFAULT_COOKIE_PATH = "FullDnsServer/com/vladkel/full/dns/server";
	
	public static final String TIMEOUT = "timeout";
	
	public static final String PORT = "port";
	
	public static final String IP = "ip";
	
	public static final String NAME = "name";
	
	public static final String DOCUMENT_ROOT = "document_root";
	
	public static final String USER_AGENT = "User-Agent: ";
	
	public static final String HOST = "Host: ";
	
	public static final String CONTENT_TYPE = "Content-Type: ";
	
	public static final String COOKIE = "Cookie: ";
	
	public static final String SET_COOKIE = "Set-Cookie: ";
	
	public static final String PATH = "Path: ";
	
	public static final String HTTP = "HTTP/1.1";
	
	public static final String GET = "GET";
	
	public static final String POST = "POST";
	

	public static final String DOMAIN = "domain";

	public static final String SESSION = "session";
	
		public static final String MODE = "mode";
		
		public static final String REMOTE = "remote";
		
		public static final String LOCAL = "local";
	
	public static final String PROXY = "proxy";
	
		public static final String LOAD_BALANCER = "lb";
		
		public static final String STRATEGY = "strategy";
		
		public static final String BAN_ON_FAIL = "ban_on_fail";
		
		public static final String STRATEGY_RR = "round_robin";
		
		public static final String STRATEGY_SS = "stiky_session";
		
		public static final String WORKERS = "workers";
				
	public static final String WORKER = "worker";
		
	
	public static enum Type {

		domain {
			public String toString() {
				return DOMAIN;
			}
		},
		session {
			public String toString() {
				return SESSION;
			}
		},
		worker {
			public String toString() {
				return WORKER;
			}
		},
		proxy {
			public String toString() {
				return PROXY;
			}
		},
		load_balancer {
			public String toString() {
				return LOAD_BALANCER;
			}
		}
	}
	
	public static enum Strategies {
		round_robin {
			public String toString() {
				return STRATEGY_RR;
			}
		},
		sticky_session {
			public String toString() {
				return STRATEGY_SS;
			}
		}
	}

	private Map<String, Map<Integer, Map<String, String>>> properties;
	
	
	public Config() {
		properties = new HashMap<>();
	}

	public String get(String type, String key) {
		return get(type, 0, key);
	}

	public String get(String type, String id, String key) {
		return get(type, Integer.parseInt(id), key);
	}

	public String get(String type, Integer id, String key) {
		return properties.get(type).get(id).get(key);
	}
	
	public Integer getNumberByTypeKeyValue(String type, String key, String value) {
		
		for(Integer index : properties.get(type).keySet()) {
			if(properties.get(type).get(index).get(key).equalsIgnoreCase(value)) {
				return index;
			}
		}
		
		return null;
	}
	
	public Integer getNumberByTypeKeyAndLikeValue(String type, String key, String value) {
		
		for(Integer index : properties.get(type).keySet()) {
			if(properties.get(type).get(index).get(key).contains(value)) {
				return index;
			}
		}
		
		return null;
	}
	
	public Map<String, String> getAsMap(String type, String id) {
		return getAsMap(type, id);
	}
	
	public Map<String, String> getAsMap(String type, Integer id) {
		return properties.get(type).get(id);
	}
	

	public void put(String type, String key, String value) {
		put(type, 0, key, value);
	}

	public void put(String type, String id, String key, String value) {
		put(type, Integer.parseInt(id), key, value);
	}

	public void put(String type, Integer id, String key, String value) {
		if (properties.get(type) == null) {
			properties.put(type, new HashMap<>());
		}
		if (properties.get(type).get(id) == null) {
			properties.get(type).put(id, new HashMap<>());
		}
		properties.get(type).get(id).put(key, value);
	}

	public void show() {
		for(String type : properties.keySet()) {
			for(Integer id : properties.get(type).keySet()) {
				for(String key : properties.get(type).get(id).keySet()) {
					StringBuilder sb = new StringBuilder();
					sb.append(type);
					sb.append(".");
					sb.append(id);
					sb.append(".");
					sb.append(key);
					sb.append("=");
					sb.append(properties.get(type).get(id).get(key));
					logger.info(sb.toString());
				}
			}
		}
	}

}

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
	
	public static final String PORT = "port";
	
	public static final String NAME = "name";
	

	private static final String DOMAIN = "domain";

	private static final String SESSION = "session";
	
	private static final String WORKER = "worker";
	
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

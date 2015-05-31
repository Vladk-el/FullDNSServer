package com.vadkel.full.dns.server.common.utils.config;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @author Eliott
 *
 */
public class ConfigParser {
	
	private Config conf;
	
	private Map<String, Config.Type> types;

	public ConfigParser(Config config) {
		setConf(config);
		setTypes(new HashMap<>());
		
		for(Config.Type type : Config.Type.values()) {
//			System.out.println(type);
			getTypes().put(type.toString(), type);
		}
	}
	
	public void parse(String line) {
		String [] tab = line.split("=");
		if(tab.length == 2) {
			String [] keys = tab[0].split("\\.");
			if(types.get(keys[0]) != null) {
				if(keys.length > 2) {
					conf.put(keys[0], keys[1], keys[2], tab[1]);
				}
				else if(keys.length > 1) {
					conf.put(keys[0], keys[1], tab[1]);
				}
			}
		}
	}

	public Config getConf() {
		return conf;
	}

	public void setConf(Config conf) {
		this.conf = conf;
	}

	public Map<String, Config.Type> getTypes() {
		return types;
	}

	public void setTypes(Map<String, Config.Type> types) {
		this.types = types;
	}

}

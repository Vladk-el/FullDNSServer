package com.vadkel.full.dns.server.common.utils.config;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * 
 * @author Eliott
 *
 */
public class ConfigReader {

	private Config conf;

	private ConfigParser parser;

	private BufferedReader br;
	
	private String applicationPath;

	public ConfigReader(String applicationPath) {
		conf = new Config();
		parser = new ConfigParser(conf);
		this.applicationPath = applicationPath;
	}

	public Config read() throws IOException {

		br = new BufferedReader(new InputStreamReader(
				new FileInputStream(applicationPath + Config.DEFAULT_CONFIG_PATH)));

		String line = "";

		while ((line = br.readLine()) != null) {
			if(line != null && !line.startsWith("#")) {
				parser.parse(line);
			}
		}

		return conf;
	}
}

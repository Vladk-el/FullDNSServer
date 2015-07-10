/**
 * 
 */
package com.vadkel.full.dns.server.dns.server.server;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vadkel.full.dns.server.common.interfaces.IServer;
import com.vadkel.full.dns.server.common.model.Request;
import com.vadkel.full.dns.server.common.utils.config.Config;
import com.vadkel.full.dns.server.common.utils.config.ConfigReader;
import com.vadkel.full.dns.server.dns.server.pool.DNSServerTask;
import com.vadkel.full.dns.server.threadpool.interfaces.IPool;
import com.vadkel.full.dns.server.threadpool.model.Pool;

/**
 * @author Eliott
 * Created on 7 juil. 2015
 *
 */
public class DNSServer implements IServer {

	private static final Logger logger = LoggerFactory.getLogger(DNSServer.class);

	private Config conf;
	
	private IPool pool;
	
	private Map<String, Map<String, String>> domains;
	
	public DNSServer() {
		if(init()){
			run();
		}
	}
	
	@Override
	public boolean init() {
		setConf(null);
		pool = new Pool();
		domains = new HashMap<String, Map<String,String>>();
		
		try {
			
			ConfigReader cr = new ConfigReader(new File("./").getAbsolutePath());
			setConf(cr.read());
			getConf().show();
			
			fillDomains();
			
			return true;
		} catch (Exception e) {
			logger.error("Error on loading config.ini : ", e);
			return false;
		}
	}

	@Override
	public void run() {
		ServerSocket server = null;
		Socket client = null;

		try {
			server = new ServerSocket(Integer.parseInt(conf.get(
					Config.Type.worker.toString(), Config.PORT)));
			logger.info(
					"DNS server {} is now online and wait for connections . . . ",
					conf.get(Config.Type.worker.toString(), Config.NAME));

			while (true) {
				try {
					client = server.accept();
					logger.info("client " + client + " connected");
					handle(client);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

		} catch (Exception e) {
			logger.error("Error on run() method : ", e);
		} finally {
			if (server != null) {
				try {
					server.close();
					logger.info("server offline");
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	public void handle(Socket client) {
		
		pool.addJob(new DNSServerTask(this, client));
		
	}

	@Override
	public void manageSession(Request request) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void execute(Request request) {
		// TODO Auto-generated method stub
		
	}
	
	private void fillDomains() throws IOException {
		List<String> current = new ArrayList<>();
		File base = new File(Config.DEFAULT_DOMAINS_PATH);
				
		if(base.isDirectory()) {
			fillDomains(current, base);
		}
		
		logger.info("Registered domain names : ");
		for(String key : domains.keySet()) {
			logger.info("\t" + key + " : ");
			for(String str : domains.get(key).keySet()) {
				logger.info("\t\t" + str + " => " + domains.get(key).get(str));
			}
		}

	}
	
	private void fillDomains(List<String> location, File directory) throws IOException {
		for(File file : directory.listFiles()) {
			location.add(file.getName());
			
			if(file.isDirectory()) {
				fillDomains(location, file);
			} else if(file.isFile()) {
				
				Map<String, String> map = new HashMap<>();
				
				StringBuilder sb = new StringBuilder();
				for(int i = location.size() - 1; i > -1; i--) {
					sb.append(location.get(i));
					sb.append(".");
				}
				sb.deleteCharAt(sb.length() - 1);
				
				@SuppressWarnings("resource")
				BufferedReader br = new BufferedReader(new FileReader(file));
				String str = "";
				while((str = br.readLine()) != null) {
					String [] tab = str.split("=");
					map.put(tab[0], tab[1]);
				}
				
				domains.put(sb.toString(), map);
				
				location.remove(location.size() - 1);
			}
		}
	}

	public Config getConf() {
		return conf;
	}

	public void setConf(Config conf) {
		this.conf = conf;
	}

	public IPool getPool() {
		return pool;
	}

	public void setPool(IPool pool) {
		this.pool = pool;
	}

	public Map<String, Map<String, String>> getDomains() {
		return domains;
	}

	public void setDomains(Map<String, Map<String, String>> domains) {
		this.domains = domains;
	}

}

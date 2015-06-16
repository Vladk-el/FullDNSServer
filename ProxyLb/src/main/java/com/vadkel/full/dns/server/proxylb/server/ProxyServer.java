package com.vadkel.full.dns.server.proxylb.server;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vadkel.full.dns.server.common.interfaces.IServer;
import com.vadkel.full.dns.server.common.model.Request;
import com.vadkel.full.dns.server.common.utils.config.Config;
import com.vadkel.full.dns.server.common.utils.config.ConfigReader;
import com.vadkel.full.dns.server.proxylb.pool.ProxyServerTask;
import com.vadkel.full.dns.server.threadpool.interfaces.IPool;
import com.vadkel.full.dns.server.threadpool.model.Pool;

public class ProxyServer implements IServer {

	private static final Logger logger = LoggerFactory.getLogger(ProxyServer.class);
	
	private Config conf;
	
	private IPool pool;
	
	private Integer lastRRServer;
	
	private Integer lastSSServer;
	
	public ProxyServer() {
		if(init()) {
			run();
		}
	}

	@Override
	public boolean init() {
		setConf(null);
		pool = new Pool();
		
		try {
			ConfigReader cr = new ConfigReader(new File("./").getAbsolutePath());
			setConf(cr.read());
			getConf().show();
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
					Config.Type.proxy.toString(), Config.PORT)));
			logger.info(
					"{} is now online and wait for connections . . . ",
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

		pool.addJob(new ProxyServerTask(this, client));
		
	}

	@Override
	public void manageSession(Request request) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void execute(Request request) {
		// TODO Auto-generated method stub
		
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

	public Integer getLastRRServer() {
		return lastRRServer;
	}

	public void setLastRRServer(Integer lastRRServer) {
		this.lastRRServer = lastRRServer;
	}

	public Integer getLastSSServer() {
		return lastSSServer;
	}

	public void setLastSSServer(Integer lastSSServer) {
		this.lastSSServer = lastSSServer;
	}
}

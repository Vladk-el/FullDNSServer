package com.vadkel.full.dns.server.httpstatic.server;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vadkel.full.dns.server.common.interfaces.IServer;
import com.vadkel.full.dns.server.common.interfaces.ISession;
import com.vadkel.full.dns.server.common.model.Request;
import com.vadkel.full.dns.server.common.utils.config.Config;
import com.vadkel.full.dns.server.common.utils.config.ConfigReader;
import com.vadkel.full.dns.server.httpstatic.pool.HttpStaticTask;
import com.vadkel.full.dns.server.threadpool.interfaces.IPool;
import com.vadkel.full.dns.server.threadpool.model.Pool;

/**
 * 
 * @author Eliott
 *
 */
public class HttpServer implements IServer {

	private static final Logger logger = LoggerFactory.getLogger(HttpServer.class);

	private Config conf;
	
	private IPool pool;
	
	private Map<String, ISession> sessions;

	public HttpServer() {
		if(init()){
			run();
		}
	}

	@Override
	public boolean init() {
		setConf(null);
		pool = new Pool();
		sessions = new HashMap<>();
		
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
					Config.Type.worker.toString(), Config.PORT)));
			logger.info(
					"worker {} is now online and wait for connections . . . ",
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
		
		pool.addJob(new HttpStaticTask(this, client));

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

	public Map<String, ISession> getSessions() {
		return sessions;
	}

	public void setSessions(Map<String, ISession> sessions) {
		this.sessions = sessions;
	}

}

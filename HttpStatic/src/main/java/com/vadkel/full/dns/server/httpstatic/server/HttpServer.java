package com.vadkel.full.dns.server.httpstatic.server;

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

/**
 * 
 * @author Eliott
 *
 */
public class HttpServer implements IServer {

	private static final Logger logger = LoggerFactory.getLogger(HttpServer.class);

	private Config conf;

	public HttpServer() {
		init();
		run();
	}

	@Override
	public void init() {
		setConf(null);
		try {
			ConfigReader cr = new ConfigReader(new File("./").getAbsolutePath());
			setConf(cr.read());
			getConf().show();
		} catch (Exception e) {
			logger.error("Error on loading config.ini : ", e);
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
					System.out.println("client " + client + " connected");
					handle(client);
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					System.out.println("client " + client + " disconnected");
					client.close();
				}
			}

		} catch (Exception e) {
			logger.error("Error on run() method : ", e);
		} finally {
			if (server != null) {
				try {
					server.close();
					System.out.println("server offline");
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	public void handle(Socket client) {
		Request request = new Request(client);
		manageSession(request);
		execute(request);
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

}

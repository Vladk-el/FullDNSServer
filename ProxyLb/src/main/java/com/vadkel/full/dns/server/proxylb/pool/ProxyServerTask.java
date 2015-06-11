package com.vadkel.full.dns.server.proxylb.pool;

import java.io.IOException;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vadkel.full.dns.server.common.interfaces.IWorkerTask;
import com.vadkel.full.dns.server.common.model.Request;
import com.vadkel.full.dns.server.proxylb.server.ProxyServer;

public class ProxyServerTask implements IWorkerTask {

	private static final Logger logger = LoggerFactory.getLogger(ProxyServerTask.class);
	
	private ProxyServer server;
	
	private Socket socket;
	
	public ProxyServerTask(ProxyServer server, Socket socket) {
		super();
		this.server = server;
		this.socket = socket;
	}
	
	@Override
	public void run() {
		handle();
	}

	@Override
	public void handle() {
		Request request = new Request(socket);
		if(request.init()) {
			request.show();
			manageSession(request);
			execute(request);
			
			if(socket != null && !socket.isClosed()){
				try {
					socket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	public void manageSession(Request request) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void execute(Request request) {
		// TODO Auto-generated method stub
		
	}

	public ProxyServer getServer() {
		return server;
	}

	public void setServer(ProxyServer server) {
		this.server = server;
	}

	public Socket getSocket() {
		return socket;
	}

	public void setSocket(Socket socket) {
		this.socket = socket;
	}

}
package com.vadkel.full.dns.server.httpstatic.pool;

import java.io.IOException;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vadkel.full.dns.server.common.interfaces.IWorkerTask;
import com.vadkel.full.dns.server.common.model.Request;
import com.vadkel.full.dns.server.httpstatic.server.HttpServer;

public class HttpStaticTask implements IWorkerTask {
	
	private static final Logger logger = LoggerFactory.getLogger(HttpStaticTask.class);
	
	private HttpServer server;
	
	private Socket socket;

	public HttpStaticTask(HttpServer server, Socket socket) {
		super();
		this.server = server;
		this.socket = socket;
	}
	
	@Override
	public void run() {
		handle();
	}
	
	public void handle() {
		Request request = new Request(socket);
		request.show();
		manageSession(request);
		execute(request);
		
		if(socket != null && !socket.isClosed()){
			try {
				socket.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public void manageSession(Request request) {
		// TODO Auto-generated method stub

	}

	public void execute(Request request) {
		// TODO Auto-generated method stub

	}

}

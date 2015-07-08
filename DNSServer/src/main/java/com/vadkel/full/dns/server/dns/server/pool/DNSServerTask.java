/**
 * 
 */
package com.vadkel.full.dns.server.dns.server.pool;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vadkel.full.dns.server.common.interfaces.IWorkerTask;
import com.vadkel.full.dns.server.common.model.DNSRequest;
import com.vadkel.full.dns.server.common.model.Request;
import com.vadkel.full.dns.server.common.utils.config.Config;
import com.vadkel.full.dns.server.dns.server.model.DNSAnswerRecord;
import com.vadkel.full.dns.server.dns.server.model.DNSIpAddress;
import com.vadkel.full.dns.server.dns.server.model.DNSQuestion;
import com.vadkel.full.dns.server.dns.server.model.DNSResponse;
import com.vadkel.full.dns.server.dns.server.server.DNSServer;

/**
 * @author Eliott
 * Created on 7 juil. 2015
 *
 */
public class DNSServerTask implements IWorkerTask {

	private static final Logger logger = LoggerFactory.getLogger(DNSServerTask.class);
	
	private DNSServer server;
	
	private Socket socket;
	
	public DNSServerTask(DNSServer server, Socket socket) {
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
		
		DNSRequest request = new DNSRequest(socket);
		
		if(request.init()) { 
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
	
	/**
	 * Read dns request
	 *  => extract the wanted domain name
	 *  	=> search for it in server.domains
	 *  	   if( present )
	 *  		=> construct a response with founded domain name
	 *  	   else
	 *  		=> ask google dns and directly transfer the response
	 */
	public void execute(DNSRequest request) {

		DNSResponse response = null;
		DNSQuestion question = null;
		String domain = null;
		
		try {
			
			response = new DNSResponse(request.getDatas(), request.getDataSize());
			
			if(response.getQuestions().size() > 0) {
				question = response.getQuestions().get(0);
			}
			
			logger.info("Question found : ");
			logger.info("\t" + question.toString());
			
			domain = question.getDomain();
			Map<String, String> ips = server.getDomains().get(domain);
			
			if(ips != null) {
				// construct a response with founded domain name
				
				DNSAnswerRecord record = new DNSAnswerRecord(domain);
				
				for(String key : ips.keySet()) {
					//System.out.println(key + " => " + ips.get(key));
					record.addIp(new DNSIpAddress(ips.get(key)));
				}
				
				/*
				 * creates ips ok, now update 
				 */
				
				byte[] localDataresponse = response.createResponseFromLocalRecord(record, response.getId());
				
				request.getSocket().getOutputStream().write(localDataresponse, 0, localDataresponse.length);
				request.getSocket().getOutputStream().flush();
				//System.out.println("sending response to client ok");
				
			} else {
				// ask google dns and directly transfer the response
				//System.out.println("Asking google");
				
				DatagramSocket socket = new DatagramSocket();
				DatagramPacket packet = new DatagramPacket(
						request.getDatas(), 
						request.getDatas().length, 
						InetAddress.getByName(Config.DEFAULT_GOOGLE_DNS_IP), 
						Config.DEFAULT_GOOGLE_DNS_PORT
				);
				socket.send(packet);
//				System.out.println("Connected to google");
//				System.out.println("Sending datas ok");
				
				
				byte[] buf = new byte[1000];
				DatagramPacket responseDatas = new DatagramPacket(buf, buf.length);
				socket.receive(responseDatas);
				byte [] googleDataResponse = responseDatas.getData();
				
				
//				Socket socket = new Socket(Config.DEFAULT_GOOGLE_DNS_IP, Config.DEFAULT_GOOGLE_DNS_PORT);
//				System.out.println("Connected to google");
//				
//				socket.getOutputStream().write(request.getDatas());
//				socket.getOutputStream().flush();
//				System.out.println("Sending datas ok");
//				
//				byte [] googleDataResponse = SocketUtils.readBytesIntoSocket(socket);
//				System.out.println("Reading response : size = " + responseDatas.getLength());
				
				request.getSocket().getOutputStream().write(googleDataResponse, 0, responseDatas.getLength());
				request.getSocket().getOutputStream().flush();
				//System.out.println("sending response to client ok");
				
			}
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	

	public DNSServer getServer() {
		return server;
	}

	public void setServer(DNSServer server) {
		this.server = server;
	}

	public Socket getSocket() {
		return socket;
	}

	public void setSocket(Socket socket) {
		this.socket = socket;
	}

}

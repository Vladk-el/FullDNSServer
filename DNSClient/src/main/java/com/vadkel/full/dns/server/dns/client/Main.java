package com.vadkel.full.dns.server.dns.client;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;

import javax.print.attribute.ResolutionSyntax;

import com.vadkel.full.dns.server.common.utils.dns.DNS;
import com.vadkel.full.dns.server.common.utils.socket.SocketUtils;
import com.vadkel.full.dns.server.dns.server.model.DNSQuery;
import com.vadkel.full.dns.server.dns.server.model.DNSResponse;

/**
 * Hello world!
 *
 */
public class Main {
	
    public static void main( String[] args ) {
        System.out.println( "Hello DSNClient!" );
        
        try {
			// QueryDNS ==> 
			DNSQuery query = new DNSQuery("google.fr", DNS.QTYPE_ANY, DNS.CLASS_IN);
			
			/*
			 * test
			 */
			
			Socket socket = new Socket("localhost", 8888);
			//Socket socket = new Socket("8.8.8.8", 53);
			byte[] datas = query.buildQuery();
			System.out.println("Length : " + datas.length);
			
			socket.getOutputStream().write(datas);
			socket.getOutputStream().flush();
			//socket.getOutputStream().close();
			
			System.out.println("writing done");
			
			//Thread.sleep(10000);
			
			byte [] googleDataResponse = SocketUtils.readBytesIntoSocket(socket);
			
			DNSResponse response = new DNSResponse(googleDataResponse, googleDataResponse.length);
			
			
//			DatagramSocket socket = new DatagramSocket();			
//			socket.setSoTimeout(5000);
//
//			byte[] datas = query.buildQuery();
//
//			System.out.println("Length : " + datas.length);
//			DatagramPacket packet = new DatagramPacket(datas, datas.length, InetAddress.getByName("8.8.8.8"), 53);
//			socket.send(packet);
//			
//			
//			byte[] buf = new byte[1000];
//			DatagramPacket responseDatas = new DatagramPacket(buf, buf.length);
//			socket.receive(responseDatas);
//			byte[] responseData = responseDatas.getData();
//			
//			System.out.println("*** Response ***");
//			System.out.println("\t length : " + responseDatas.getLength());
//			System.out.println("\t port : " + responseDatas.getPort());
//			System.out.println("\t address : " + responseDatas.getAddress());
//			System.out.println("\t byte array lenght : " + responseData.length);
//			
//			System.out.println("*** DNSResponse ***");
//			DNSResponse response = new DNSResponse(responseData, responseDatas.getLength());
//	

			System.out.println("END");
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
    
}

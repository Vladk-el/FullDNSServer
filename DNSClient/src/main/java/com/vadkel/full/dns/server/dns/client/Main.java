package com.vadkel.full.dns.server.dns.client;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import com.vadkel.full.dns.server.common.utils.dns.DNS;
import com.vadkel.full.dns.server.dns.client.model.DNSQuery;

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
			
			
			DatagramSocket socket = new DatagramSocket();			
			socket.setSoTimeout(5000);

			byte[] data = query.buildQuery();

			System.out.println("Length : " + data.length);
			DatagramPacket packet = new DatagramPacket(data, data.length, InetAddress.getByName("8.8.8.8"), 53);
			socket.send(packet);

			System.out.println("END");
		} catch (Exception E) {

		}
    }
    
}

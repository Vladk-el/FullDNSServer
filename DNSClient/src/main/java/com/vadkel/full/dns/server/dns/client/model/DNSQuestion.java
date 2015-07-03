package com.vadkel.full.dns.server.dns.client.model;

import java.io.DataInputStream;
import java.io.IOException;

import com.vadkel.full.dns.server.common.utils.dns.DNSUtils;

/**
 * @author Eliott
 * Created on 2 juil. 2015
 *
 */
public class DNSQuestion {
	
	private String domain;
	private short queryType;
	private short queryClass;
	
	public DNSQuestion(DataInputStream dis) throws IOException {
		// Extract domain name
		byte firstByte = dis.readByte();
		
		System.out.println("Question firstByte : " + firstByte);
		
		domain = DNSUtils.extractDomainName(dis, firstByte);

		// Extract TYPE
		queryType = dis.readShort();
		
		// Extract QCLASS
		queryClass = dis.readShort();
	}
	
	public String toString() {
		return domain + " ### type : " + queryType + " ### class : " + queryClass; 
	}

}

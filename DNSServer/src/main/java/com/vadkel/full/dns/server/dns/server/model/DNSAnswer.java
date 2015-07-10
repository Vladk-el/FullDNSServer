package com.vadkel.full.dns.server.dns.server.model;

import java.io.DataInputStream;
import java.io.IOException;

import com.vadkel.full.dns.server.common.utils.dns.DNS;

/**
 * @author Eliott
 * Created on 2 juil. 2015
 *
 */
@SuppressWarnings("unused")
public class DNSAnswer {

	private int pointerPos = 0;
	private String domainName;
	private int queryType = DNS.TYPE_A;
	private int queryClass = DNS.CLASS_IN;
	private int ttl = 0;
	private short rrDataLength = 0; // ip address size
	private byte rrData[] = null; // ip address
	
	public DNSAnswer(DataInputStream dis) throws IOException {

//		byte firstByte = dis.readByte();
//		
//		System.out.println("\tAnswer firstByte : " + firstByte);
//		
//		firstByte = dis.readByte();
//		
//		System.out.println("\tAnswer firstByte : " + firstByte);
//		
//		firstByte = dis.readByte();
//		
//		System.out.println("\tAnswer firstByte : " + firstByte);
//		
//		System.out.println("\tAnswer firstByte & DNS.TYPE_PTR : " + (firstByte & DNS.TYPE_PTR));
//		
//		// Check if we have a domain name pointer
//		if (0 != (firstByte & DNS.TYPE_PTR)) {
//			System.out.println("Domain name pointer");
//			// Domain name pointer
//			pointerPos = firstByte & DNS.LIMIT_SIZE_LABEL;
//			// read an ending 0
//			byte ending = dis.readByte();
//			pointerPos = (pointerPos << 8) | ending; 
//		} else {
//			// this is a normal name
//			domainName = DNSUtils.extractDomainName(dis, firstByte);
//		}
		
		// read byte pointer
		dis.readByte();
		
		// read byte pointer location
		dis.readByte();
		
		// Extract TYPE
		queryType = dis.readShort();
//		System.out.println("queryType : " + queryType);

		// Extract QCLASS
		queryClass = dis.readShort();
//		System.out.println("queryClass : " + queryClass);
		
		// Extract TTL
		ttl = dis.readInt();
//		System.out.println("ttl : " + ttl);
		
		// Extract IP ADDRESS
		rrDataLength = dis.readShort();
//		System.out.println("SHOULD BE 4 : " + rrDataLength);
		if(rrDataLength > 4) {
			rrData = new byte[rrDataLength];
			dis.read(rrData, 0, rrData.length - 4);
			rrDataLength = 4;
		}
		if (rrDataLength > 0) {
			rrData = new byte[rrDataLength];
			dis.read(rrData, 0, rrData.length);
		}

	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		// domain name
		if (isPointer()) {
			sb.append("Ptr ");
			sb.append(pointerPos);
		} else {
			sb.append(domainName);
		}
		sb.append(':');
		
		// ip address
		for(int i = 0; i < rrData.length; i++) {
			sb.append((short)rrData[i] & 0xFF);
			sb.append('.');
		}

		sb.deleteCharAt(sb.length() - 1);
//		sb.append((int)rrData[0]&0xFF);sb.append('.');
//		sb.append((int)rrData[1]&0xFF);sb.append('.');
//		sb.append((int)rrData[2]&0xFF);sb.append('.');
//		sb.append((int)rrData[3]&0xFF);
		
		return sb.toString();
	}
	
	public boolean isPointer() {
		return domainName == null;
	}
	
}

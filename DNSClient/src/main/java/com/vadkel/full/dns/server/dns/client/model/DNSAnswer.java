/**
 * 
 */
package com.vadkel.full.dns.server.dns.client.model;

import java.io.DataInputStream;
import java.io.IOException;

import com.vadkel.full.dns.server.common.utils.dns.DNS;
import com.vadkel.full.dns.server.common.utils.dns.DNSUtils;

/**
 * @author Eliott
 * Created on 2 juil. 2015
 *
 */
public class DNSAnswer {

	private int pointerPos = 0;
	private String domainName;
	private int queryType = DNS.TYPE_A;
	private int queryClass = DNS.CLASS_IN;
	private int ttl = 0;
	private short rrDataLength = 0; // ip address size
	private byte rrData[] = null; // ip address
	
	public DNSAnswer(DataInputStream dis) throws IOException {

		byte firstByte = dis.readByte();
		
		// Check if we have a domain name pointer
		if (0 != (firstByte & DNS.TYPE_PTR)) {
			// Domain name pointer
			pointerPos = firstByte & DNS.LIMIT_SIZE_LABEL;
			// read an ending 0
			byte ending = dis.readByte();
			pointerPos = (pointerPos << 8) | ending; 
		} else {
			// this is a normal name
			domainName = DNSUtils.extractDomainName(dis, firstByte);
		}
		
		// Extract TYPE
		queryType = dis.readShort();

		// Extract QCLASS
		queryClass = dis.readShort();
		
		// Extract TTL
		ttl = dis.readInt();
		
		// Extract IP ADDRESS
		rrDataLength = dis.readShort();
		if (rrDataLength > 0) {
			rrData = new byte[rrDataLength];
			dis.read(rrData, 0, rrData.length);
		}

	}
	
	public String toString() {
		StringBuffer sb = new StringBuffer();
		// domain name
		if (isPointer()) {
			sb.append("Ptr ");
			sb.append(pointerPos);
		} else {
			sb.append(domainName);
		}
		sb.append(':');
		// ip address
		sb.append((int)rrData[0]&0xFF);sb.append('.');
		sb.append((int)rrData[1]&0xFF);sb.append('.');
		sb.append((int)rrData[2]&0xFF);sb.append('.');
		sb.append((int)rrData[3]&0xFF);
		
		return sb.toString();
	}
	
	public boolean isPointer() {
		return domainName == null;
	}
	
}
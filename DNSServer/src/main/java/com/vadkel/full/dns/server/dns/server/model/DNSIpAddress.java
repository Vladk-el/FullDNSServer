package com.vadkel.full.dns.server.dns.server.model;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;

/**
 * @author Eliott
 * Created on 8 juil. 2015
 *
 */
public class DNSIpAddress {
	
	private byte [] ip;
	
	private final int IP_SIZE = 4;

	public DNSIpAddress(byte [] ip) throws Exception {
		if(ip.length == 4) {
			this.ip = ip;
		} else {
			ip = null;
			throw new Exception (ip + " is not a valid ip address !");
		}
	}
	
	public DNSIpAddress(String strip) throws Exception {
		String [] tab = strip.split("\\.");
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(out);
		
		if(tab.length == IP_SIZE) {
			for(int i = 0; i < IP_SIZE; i++) {
				short c = Short.parseShort(tab[i]);
				dos.writeShort(c);
			}
			ip = out.toByteArray();
		} else {
			ip = null;
			throw new Exception (ip + " is not a valid ip address !");
		}
	}
	
	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append((int)ip[0]&0xFF); sb.append('.');
		sb.append((int)ip[1]&0xFF); sb.append('.');
		sb.append((int)ip[2]&0xFF); sb.append('.');
		sb.append((int)ip[3]&0xFF);
		return sb.toString();
	}

	public byte [] getIp() {
		return ip;
	}

	public void setIp(byte [] ip) {
		this.ip = ip;
	}
	
}

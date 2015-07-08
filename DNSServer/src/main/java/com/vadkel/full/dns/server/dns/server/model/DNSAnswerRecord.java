/**
 * 
 */
package com.vadkel.full.dns.server.dns.server.model;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Eliott
 * Created on 7 juil. 2015
 *
 */
public class DNSAnswerRecord {
	
	private String domain;
	
	private List<DNSIpAddress> ips;
	

	public DNSAnswerRecord() {
		super();
		init();
	}
	
	public DNSAnswerRecord(String domain) {
		this.domain = domain;
		init();
	}
	
	public void init() {
		ips = new ArrayList<DNSIpAddress>();
	}
	
	public void addIp(DNSIpAddress ip) {
		getIps().add(ip);
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public List<DNSIpAddress> getIps() {
		return ips;
	}

	public void setIps(List<DNSIpAddress> ips) {
		this.ips = ips;
	}
	
	
}

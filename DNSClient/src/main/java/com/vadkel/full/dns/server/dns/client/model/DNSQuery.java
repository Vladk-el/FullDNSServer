package com.vadkel.full.dns.server.dns.client.model;

public class DNSQuery {

	private static int id;
	
	private String queryHost;
	private int queryType;
	private int queryClass;
	private int queryID;	
	
	private boolean authoritative;
	private boolean truncated; 
	private boolean recursive;
	
	public DNSQuery() {
		super();
	}
	
	public DNSQuery(String host, int type, int clazz) {
		
	}

	public static int getId() {
		return id;
	}

	public static void setId(int id) {
		DNSQuery.id = id;
	}

	public String getQueryHost() {
		return queryHost;
	}

	public void setQueryHost(String queryHost) {
		this.queryHost = queryHost;
	}

	public int getQueryType() {
		return queryType;
	}

	public void setQueryType(int queryType) {
		this.queryType = queryType;
	}

	public int getQueryClass() {
		return queryClass;
	}

	public void setQueryClass(int queryClass) {
		this.queryClass = queryClass;
	}

	public int getQueryID() {
		return queryID;
	}

	public void setQueryID(int queryID) {
		this.queryID = queryID;
	}

	public boolean isAuthoritative() {
		return authoritative;
	}

	public void setAuthoritative(boolean authoritative) {
		this.authoritative = authoritative;
	}

	public boolean isTruncated() {
		return truncated;
	}

	public void setTruncated(boolean truncated) {
		this.truncated = truncated;
	}

	public boolean isRecursive() {
		return recursive;
	}

	public void setRecursive(boolean recursive) {
		this.recursive = recursive;
	}
	
}

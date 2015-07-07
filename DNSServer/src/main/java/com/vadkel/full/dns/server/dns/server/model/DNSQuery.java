package com.vadkel.full.dns.server.dns.server.model;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.StringTokenizer;

import com.vadkel.full.dns.server.common.utils.dns.DNS;

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
		StringTokenizer labels = new StringTokenizer(host, ".");
		
		while (labels.hasMoreTokens())
			if (labels.nextToken().length() > DNS.LIMIT_SIZE_LABEL)
				throw new IllegalArgumentException("Invalid hostname: " + host);
		queryHost = host;
		queryType = type;
		queryClass = clazz;
		
		synchronized (getClass()) {
			queryID = (++id) % 65536;
		}
	}


	/**
	 * Allows to create a query like : 
	 *                 +---------------------------------------------------+
	 *	    Header     | OPCODE=SQUERY                                     |
	 *	               +---------------------------------------------------+
	 *	    Question   | QNAME=SRI-NIC.ARPA., QCLASS=IN, QTYPE=A           |
	 *	               +---------------------------------------------------+
	 *	    Answer     | <empty>                                           |
	 *	               +---------------------------------------------------+
	 *	    Authority  | <empty>                                           |
	 *	               +---------------------------------------------------+
	 *	    Additional | <empty>                                           |
	 *	               +---------------------------------------------------+
	 *
	 */
	public byte[] buildQuery() {
		ByteArrayOutputStream byteArrayOut = new ByteArrayOutputStream();
		DataOutputStream dataOut = new DataOutputStream(byteArrayOut);
		try {
			
			// ID
			dataOut.writeShort(queryID);
			
//			System.out.println("(0 << DNS.SHIFT_QUERY)" + DNS.SHIFT_QUERY + " : " + (0 << DNS.SHIFT_QUERY));
//			
//			System.out.println("(DNS.OPCODE_QUERY << DNS.SHIFT_OPCODE)" + DNS.OPCODE_QUERY + " ET " + DNS.SHIFT_OPCODE + " : " + (DNS.OPCODE_QUERY << DNS.SHIFT_OPCODE));
//			
//			System.out.println("(1 << DNS.SHIFT_RECURSE_PLEASE)" + DNS.SHIFT_RECURSE_PLEASE + " : " + (1 << DNS.SHIFT_RECURSE_PLEASE));
//			
			
			// Flags
			dataOut.writeShort((0 << DNS.SHIFT_QUERY) // 0 décallé de 15 car REQUETE ==> [0]000 0000 0000 0000
					| (DNS.OPCODE_QUERY << DNS.SHIFT_OPCODE) // 0 décallé de 11 == > 0000 [0]000 0000 0000
					| (1 << DNS.SHIFT_RECURSE_PLEASE)); // 1 décallé de 8 ==> 0000 000[1] 0000 0000
			
			/**
			 * ==> [0]000 0000 0000 0000 | 0000 [0]000 0000 0000 | 0000 000[1] 0000 0000 ==> 0000 0001 0000 0000 (256)
			 */
			
			// Questions number
			dataOut.writeShort(1);
			
			// Responses number
			dataOut.writeShort(0);
			
			// Authority RR number
			dataOut.writeShort(0);
			
			// Additionnal RR number
			dataOut.writeShort(0);

			StringTokenizer labels = new StringTokenizer(queryHost, ".");
			while (labels.hasMoreTokens()) {
				String label = labels.nextToken();
				System.out.println("Label : " + label);
				dataOut.writeByte(label.length());
				dataOut.writeBytes(label);
			}
			
			dataOut.writeByte(0);
			dataOut.writeShort(queryType);
			dataOut.writeShort(queryClass);
		} catch (IOException ignored) {
		}
		return byteArrayOut.toByteArray();
	}

	
	/**
	 * Getters ands setters
	 */

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

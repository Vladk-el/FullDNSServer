package com.vadkel.full.dns.server.dns.server.model;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.vadkel.full.dns.server.common.utils.dns.DNS;

/**
 * @author Eliott
 * Created on 2 juil. 2015
 *
 */
public class DNSResponse {
	
	private short id;
	private short flags;
	private short numQuestions;
	private short numRR;
	private short numAuthRR;
	private short numAuxRR;
	
	private List<DNSQuestion> questions;
	private List<DNSAnswer> answers;

	public DNSResponse(byte [] datas, int dataSize) throws Exception {
		questions = new ArrayList<>();
		answers = new ArrayList<>();
		createResponseFromByteArray(datas, dataSize);
	}
	
	/**
	 * Allows to create a DNSQuery from a dns server response
	 *                 +---------------------------------------------------+
	 *	    Header     | OPCODE=SQUERY, RESPONSE, AA                       |
	 *	               +---------------------------------------------------+
	 *	    Question   | QNAME=SRI-NIC.ARPA., QCLASS=IN, QTYPE=A           |
	 *	               +---------------------------------------------------+
	 *	    Answer     | SRI-NIC.ARPA. 86400 IN A 26.0.0.73                |
	 *	               |               86400 IN A 10.0.0.51                |
	 *	               +---------------------------------------------------+
	 *	    Authority  | <empty>                                           |
	 *	               +---------------------------------------------------+
	 *	    Additional | <empty>                                           |
	 *	               +---------------------------------------------------+
	 *
	 */
	public byte [] createResponseFromLocalRecord(DNSAnswerRecord record, int id) throws Exception {
		
		// First, get the response length
		int headerLength = 12;
		int questionLength = 5;
		
		String [] tab = record.getDomain().split("\\.");
		for(String str : tab)  {
			questionLength += (str.length() + 1); // domain fragment size + "."
		}
		
		int answerLength = 
				(
						2 + // 2 bytes for domain name pointer
						8 + // 8 bytes for type, class and ttl
						6 	// 6 bytes for ip address
				)
				* record.getIps().size();
		
		int total = headerLength + questionLength + answerLength;
		
		// Now, create the new byte array which will contains the response
		ByteArrayOutputStream out = new ByteArrayOutputStream(total);
		DataOutputStream dos = new DataOutputStream(out);
		
		// Start by header
			// id
				dos.writeShort(id);
			
			// flags
				short flags = (short) 0x8180;
				if (record.getIps().size() == 0)
					flags |= 0x03;
				dos.writeShort(flags);
		
			// number of questions
				dos.writeShort(1);
				
			// number of answers
				dos.writeShort(record.getIps().size());
			
			// authority RRs
				dos.writeShort(0);
			
			// additional RRs
				dos.writeShort(0);		
				
		// Write questions
			// Start with domain
				for (String str : tab) {
					dos.writeByte(str.length());
					for (char c: str.toCharArray()) {
						dos.writeByte((byte)c);
					}
				}
			
			// End domain by a 0
				dos.writeByte(0);
			
			// QUERY TYPE
				dos.writeShort(DNS.TYPE_A);
			
			// QUERY CLASS
				dos.writeShort(DNS.CLASS_IN);
		
		// Write answers
			for (DNSIpAddress ip : record.getIps()) {
				// domain name is a pointer
				dos.writeByte(0xC0);
				// domain pointer location
				dos.writeByte(0x0C);
				// QUERY TYPE
				dos.writeShort(DNS.TYPE_A);
				// QUERY CLASS
				dos.writeShort(DNS.CLASS_IN);
				// Time To Live ==> 6 minutes
				dos.writeInt(360);
				// RR data length (ip size)
				dos.writeShort(4);
				// IP address
				dos.write(ip.getIp(), 0, 4);
			}
		
		dos.close();
		
		return out.toByteArray();
	}
	
	
	
	
	/**
	 * 
	 * Allows to read a DNSQuery from a dns server response
	 *
	 */
	private void createResponseFromByteArray(byte[] datas, int dataSize) {
		
		ByteArrayInputStream is = new ByteArrayInputStream(datas, 0, dataSize);
		DataInputStream dis = new DataInputStream(is);
		
		try {
			parseHeader(dis);
			parseQuestions(dis);
			parseAnswers(dis);
			dis.close();
		} catch(IOException e) {
			e.printStackTrace();
		}

	}
	
	public void parseHeader(DataInputStream dis) throws IOException {
		//System.out.println("*** parseHeader ***");
		id = dis.readShort();
		//System.out.println("\t txnId : " + id);
		flags = dis.readShort();
		//System.out.println("\t flags :" + flags);
		numQuestions = dis.readShort();
		//System.out.println("\t numQuestions :" + numQuestions);
		numRR = dis.readShort();
		//System.out.println("\t numRR : " + numRR);
		numAuthRR = dis.readShort();
		//System.out.println("\t numAuthRR : " + numAuthRR);
		numAuxRR = dis.readShort();
		//System.out.println("\t numAuxRR : " + numAuxRR);
	}
	
	public void parseQuestions(DataInputStream dis) throws IOException {
		//System.out.println("*** parseQuestions ***");
		for (int i = 0; i < numQuestions; i++) {
			//System.out.println("\t*** read one question ***");
			DNSQuestion question = new DNSQuestion(dis);
			//System.out.println("\t\t" + question.toString());
			questions.add(question);
		}
	}
	
	public void parseAnswers(DataInputStream dis) throws IOException {
		//System.out.println("*** parseAnswers ***");
		for (int i = 0; i < numRR; i++) {
			//System.out.println("\t*** read one answer ***");
			DNSAnswer answer = new DNSAnswer(dis);
			System.out.println("\t\t" + answer.toString());
			answers.add(answer);
		}
	}
	
	public short getId() {
		return id;
	}

	public void setId(short id) {
		this.id = id;
	}

	public List<DNSQuestion> getQuestions() {
		return questions;
	}

	public void setQuestions(List<DNSQuestion> questions) {
		this.questions = questions;
	}

	public List<DNSAnswer> getAnswers() {
		return answers;
	}

	public void setAnswers(List<DNSAnswer> answers) {
		this.answers = answers;
	}
	
}

package com.vadkel.full.dns.server.dns.server.model;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Eliott
 * Created on 2 juil. 2015
 *
 */
public class DNSResponse {
	
	private short txnId;
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
		System.out.println("*** parseHeader ***");
		txnId = dis.readShort();
		System.out.println("\t txnId : " + txnId);
		flags = dis.readShort();
		System.out.println("\t flags :" + flags);
		numQuestions = dis.readShort();
		System.out.println("\t numQuestions :" + numQuestions);
		numRR = dis.readShort();
		System.out.println("\t numRR : " + numRR);
		numAuthRR = dis.readShort();
		System.out.println("\t numAuthRR : " + numAuthRR);
		numAuxRR = dis.readShort();
		System.out.println("\t numAuxRR : " + numAuxRR);
	}
	
	public void parseQuestions(DataInputStream dis) throws IOException {
		System.out.println("*** parseQuestions ***");
		for (int i = 0; i < numQuestions; i++) {
			System.out.println("\t*** read one question ***");
			DNSQuestion question = new DNSQuestion(dis);
			System.out.println("\t\t" + question.toString());
			questions.add(question);
		}
	}
	
	public void parseAnswers(DataInputStream dis) throws IOException {
		System.out.println("*** parseAnswers ***");
		for (int i = 0; i < numRR; i++) {
			System.out.println("\t*** read one answer ***");
			DNSAnswer answer = new DNSAnswer(dis);
			System.out.println("\t\t" + answer.toString());
			answers.add(answer);
		}
	}

	public List<DNSQuestion> getQuestions() {
		return questions;
	}

	public void setQuestions(List<DNSQuestion> questions) {
		this.questions = questions;
	}
	
}

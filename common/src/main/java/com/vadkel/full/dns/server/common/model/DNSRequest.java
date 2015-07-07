/**
 * 
 */
package com.vadkel.full.dns.server.common.model;

import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vadkel.full.dns.server.common.utils.socket.SocketUtils;

/**
 * @author Eliott
 * Created on 7 juil. 2015
 *
 */
public class DNSRequest {
	
	private static final Logger logger = LoggerFactory.getLogger(DNSRequest.class);

	private Socket socket;
	
	private byte[] datas;
	
	private int dataSize;
	
	public DNSRequest(Socket socket) {
		setSocket(socket);
	}
	
	public boolean init() {
		
		try {
			
			datas = SocketUtils.readBytesIntoSocket(socket);
			dataSize = datas.length;
			
			return true;
		} catch (Exception e) {
			logger.error("Error on parsing DSNRequest : ", e);
		}

		return false;
	}

	public Socket getSocket() {
		return socket;
	}

	public void setSocket(Socket socket) {
		this.socket = socket;
	}

	public byte[] getDatas() {
		return datas;
	}

	public void setDatas(byte[] datas) {
		this.datas = datas;
	}

	public int getDataSize() {
		return dataSize;
	}

	public void setDataSize(int dataSize) {
		this.dataSize = dataSize;
	}
	
	
}

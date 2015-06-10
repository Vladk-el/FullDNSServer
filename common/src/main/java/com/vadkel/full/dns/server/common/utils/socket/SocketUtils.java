package com.vadkel.full.dns.server.common.utils.socket;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import com.vadkel.full.dns.server.common.model.Request;

public class SocketUtils {

	public static String [] getDatasToStringTab(Socket socket) throws IOException, InterruptedException {
		byte[] messageByte = new byte[10];
		int bytesRead;
		StringBuilder sb = new StringBuilder();
		String stringRead;
		
		DataInputStream in = new DataInputStream(socket.getInputStream());
		
		while(in.available() == 0) {
			Thread.sleep(1);
		}

		while ((bytesRead = in.read(messageByte)) > 0) {
			System.out.println(bytesRead);
			stringRead = new String(messageByte, 0, bytesRead);
			sb.append(stringRead);
			if(in.available() == 0)
				break;
		}
				
		String [] lines = sb.toString().split("\r\n");
		
		return lines;
	}
	
	public static void writeDatasIntoSocket(Socket socket, String datas) throws IOException {
		DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
		dos.writeBytes(datas);
		dos.flush();
	}
	
	public static void writeDatasIntoRequest(Request request, String datas) throws IOException {
		request.getWriter().writeBytes(datas);
		request.getWriter().flush();
	}
}

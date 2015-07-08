package com.vadkel.full.dns.server.common.utils.socket;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.Socket;

import com.vadkel.full.dns.server.common.model.Request;

public class SocketUtils {

	private static final int DEFAULT_BUFFER_SIZE = 10;

	public static String[] getDatasToStringTab(Socket socket)
			throws IOException, InterruptedException {
		byte[] messageByte = new byte[DEFAULT_BUFFER_SIZE];
		int bytesRead;
		StringBuilder sb = new StringBuilder();
		String stringRead;

		DataInputStream in = new DataInputStream(socket.getInputStream());

		while (in.available() == 0) {
			Thread.sleep(1);
		}

		while ((bytesRead = in.read(messageByte)) > 0) {
			// System.out.println(bytesRead);
			stringRead = new String(messageByte, 0, bytesRead);
			sb.append(stringRead);
			if (in.available() == 0) {
				if (sb.toString().endsWith("\r\n\r\n")) {
					break;
				}
			}
		}

		String[] lines = sb.toString().split("\r\n");

		return lines;
	}

	public static byte[] readBytesIntoSocket(Socket socket) throws IOException, InterruptedException {

		DataInputStream dis = new DataInputStream(socket.getInputStream());
		ByteArrayOutputStream out = new ByteArrayOutputStream();

		int count = 0;
		byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
		
		while (dis.available() == 0) {
			Thread.sleep(1);
		}
		
		while ((count = dis.read(buffer)) > 0) {
			out.write(buffer, 0, count);
			if(count < DEFAULT_BUFFER_SIZE)
				break;
		}

		return out.toByteArray();
	}

	public static void writeDatasIntoSocket(Socket socket, String datas)
			throws IOException {
		DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
		dos.writeBytes(datas);
		dos.flush();
	}

	public static void writeDatasIntoRequest(Request request, String datas)
			throws IOException {
		request.getWriter().writeBytes(datas);
		request.getWriter().flush();
	}

	public static void writeBytesIntoRequest(Request request, String headers,
			File file) throws IOException {
		request.getWriter().writeBytes(headers);

		FileInputStream in = new FileInputStream(file);
		byte[] buffer = new byte[4096];

		while (in.read(buffer) > 0) {
			request.getWriter().write(buffer);
		}
		in.close();

		request.getWriter().flush();
	}
}

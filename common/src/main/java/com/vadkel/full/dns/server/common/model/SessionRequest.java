package com.vadkel.full.dns.server.common.model;

import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class SessionRequest extends Request {

	private List<String> wantedProperties;
	
	
	public SessionRequest(Socket socket) {
		super(socket);
		wantedProperties = new ArrayList<>();
	}


	public List<String> getWantedProperties() {
		return wantedProperties;
	}


	public void setWantedProperties(List<String> wantedProperties) {
		this.wantedProperties = wantedProperties;
	}

}

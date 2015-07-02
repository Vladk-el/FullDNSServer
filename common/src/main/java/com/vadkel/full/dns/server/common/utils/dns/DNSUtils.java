package com.vadkel.full.dns.server.common.utils.dns;

import java.io.DataInputStream;
import java.io.IOException;

/**
 * @author Eliott
 * Created on 2 juil. 2015
 *
 */
public class DNSUtils {

	public static String extractDomainName(DataInputStream dis, byte firstByte) throws IOException {
		StringBuilder sb = new StringBuilder();
		
		/**
		 * Read domain
		 */
		
		while (true) {
			
			byte count = 0;
			
			// For first iteration
			if (firstByte != 0) {
				count = firstByte;
				firstByte = 0;
			} else {
				count = dis.readByte();
			}
				
			if (count == 0) {
				break;
			}
			
			if (sb.length() > 0) {
				sb.append('.');
			}
			
			while (count-- > 0) {
				sb.append(
					(char)dis.readByte()
				);
			}
		}
		
		return sb.toString();
	}
	
}

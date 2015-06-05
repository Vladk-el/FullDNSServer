package com.vadkel.full.dns.server.common.utils.session;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Random;
import java.util.Date;
import java.util.TimeZone;

public class SessionUtils {
	
	private final static Integer RANGE = 100000;

	public static String generateSessionKey(String workerName) {
		
		Random random = new Random();
		
		return workerName + (new Timestamp(new Date().getTime())).getTime() + "." + random.nextInt(RANGE);
	}
	
	public static String getDateForCookie(Integer delay) {
		
		DateFormat df = new SimpleDateFormat("dd MMM yyyy kk:mm:ss z");
		df.setTimeZone(TimeZone.getTimeZone("GMT"));
		Date date = new Date(new Date().getTime() + delay);
		
		return df.format(date);
	}
}

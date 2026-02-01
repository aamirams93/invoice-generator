package com.invoice.utils;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;

@Component
public class InfoUtil {
	public String getClientIP(HttpServletRequest request) {
	    String ip = request.getHeader("X-Forwarded-For");
	    if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
	        ip = request.getHeader("Proxy-Client-IP");
	    }
	    if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
	        ip = request.getHeader("WL-Proxy-Client-IP");
	    }
	    if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
	        ip = request.getRemoteAddr();
	    }
	    return ip;
	}
	
    private static final ZoneId IST_ZONE = ZoneId.of("Asia/Kolkata");
    
    private static final DateTimeFormatter IST_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss zzz");
    public String getCurrentIstFormattedDateModern() {
       
        ZonedDateTime nowIst = ZonedDateTime.now(IST_ZONE);
        return nowIst.format(IST_FORMATTER);
    }

}

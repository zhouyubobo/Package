package com.xiangGo.common.service;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ServiceUtil {
	
	private static HashMap<String,String> _urlPatternMapping = new HashMap<String,String>();
	
	public static void registerMapping(String pattern,String host){
		_urlPatternMapping.put(pattern, host);
	}
	
	public static String getServerAddress(String url){
		String host = null;
		
		String[] ps = _urlPatternMapping.keySet().toArray(new String[0]);
		for(String k : ps){
			Pattern r = Pattern.compile(k);
			Matcher m = r.matcher(url);
			if(m.find()){
				host = m.group(0);
				break;
			}
		}
		
		if(null == host){
			host = "127.0.0.1";
		}
		return host;
	}
}

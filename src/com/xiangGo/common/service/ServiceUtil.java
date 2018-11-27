package com.xiangGo.common.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ServiceUtil {
	
	private static HashMap<String,List<String>> _urlPatternMapping = new HashMap<String,List<String>>();
	
	public static HashMap<String,List<String>> getUrlPatternMapping(){
		return _urlPatternMapping;
	}
	
	public static void registerMapping(String pattern,String host){
		synchronized (_urlPatternMapping) {
			if(!_urlPatternMapping.containsKey(pattern)){
				_urlPatternMapping.put(pattern, new ArrayList<String>());
			}
			if(!_urlPatternMapping.get(pattern).contains(host)){
				_urlPatternMapping.get(pattern).add(host);
			}
		}
	}
	
	public static void unRegisterMapping(String pattern, String host){
		synchronized (_urlPatternMapping) {
			if(!_urlPatternMapping.containsKey(pattern)){
				_urlPatternMapping.put(pattern, new ArrayList<String>());
			}
			if(_urlPatternMapping.get(pattern).contains(host)){
				_urlPatternMapping.get(pattern).remove(host);
			}
		}
	}

	private static Random _r = new Random();
	public static String getServerAddress(String url){
		String host = null;
		
		String[] ps = null;
		synchronized (_urlPatternMapping) {
			ps = _urlPatternMapping.keySet().toArray(new String[0]);
		}
		for(String k : ps){
			Pattern r = Pattern.compile(k);
			Matcher m = r.matcher(url);
			if(m.find()){
				synchronized (_urlPatternMapping) {
					List<String> l = _urlPatternMapping.get(k);
					if(null != l && l.size()>0){
						host = l.get(_r.nextInt(l.size()));
					}
				}
				break;
			}
		}
		
		if(null == host){
			host = "127.0.0.1:80";
		}
		return host;
	}
}

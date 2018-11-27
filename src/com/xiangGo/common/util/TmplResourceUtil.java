package com.xiangGo.common.util;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TmplResourceUtil {

	public static final Pattern INCLUDE_PATTERN = Pattern.compile("\\<%@[ ]{0,}include[ ]{1,}file[ ]{0,}=[ ]{0,}\\\"([^\\\"]{0,})\\\"[ ]{0,}%\\>");
	
	/**
	 * @param clz
	 * @param path classpath:开头绝对路径, 其他为当前clz相对路径
	 * @return
	 */
	public static String getTmplResource(Class clz, String path){
		String tmpl = null;
		
		String folderPath = clz.getResource("").getPath();
		
		File f = null;
		if(path.startsWith("classpath:")){
			path = path.substring("classpath:".length());
			folderPath = clz.getResource("/").getPath();
		}
		f = new File(folderPath+ path);
		if(!f.exists()){
			System.out.println("TmplResourceUtil.getTmplResource using file not found:"+f.getPath());
			return tmpl;
		}
		tmpl = FileIOUtil.readFileAsString(f.getPath());
		
		Matcher m = INCLUDE_PATTERN.matcher(tmpl);
     	while (m.find()){
     		String includePath = m.group(1);
     		tmpl = tmpl.replaceFirst(m.group(), getTmplResource(clz, includePath));
     	}
		
		return tmpl;
	}
}

package com.xiangGo.common.util;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.xiangGo.common.bean.JSDataObject;

public class JsonUtil {

	private static void setCommonConfs(ObjectMapper mapper){

	    mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
	    mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
	}
	
	public static Map<?, ?> Json2Map(String json) {
	    ObjectMapper mapper = new ObjectMapper();
	    setCommonConfs(mapper);
	    try {
	    	return mapper.readValue(json, Map.class);
	    } catch (Exception e) {
			//e.printStackTrace();
	    }
	    return new HashMap();
	}

	/**
	 * @param obj
	 * @return
	 */
	public static String toJsonString(Object obj) {
	    ObjectMapper mapper = new ObjectMapper();
		try {
		    setCommonConfs(mapper);
			return mapper.writeValueAsString(obj);
		} catch (Exception e) {
			//e.printStackTrace();
		}
		return null;
	}

	/**
	 * 
	 * @param <T>
	 * @param str
	 * @param cla
	 * @return
	 */
	public static <T> T toObject(String str, Class<T> cla) {
		return toObject(str, cla, null);
	}
	

	/**
	 * @param <T>
	 * @param str
	 * @param cla
	 * @return
	 */
	public static <T> T toObject(String str, Class<T> cla, HashMap<Class,Class> jsonClassMapping) {
		if (str == null || "".equals(str)) {
			return null;
		}

	    ObjectMapper mapper = getMapper(jsonClassMapping);
	    
		try {
			return mapper.readValue(str, cla);
		} catch (Exception e) {
			//e.printStackTrace();
			System.out.println("JsonUtil.toObject error, type["+cla+"], json:"+str);
		}
		return null;
	}

	private static ObjectMapper getMapper(HashMap<Class,Class> jsonClassMapping){
	    ObjectMapper mapper = new ObjectMapper();
	    if(null != jsonClassMapping){
		    SimpleModule module = new SimpleModule();
			for(Class c :jsonClassMapping.keySet()){
				module.addAbstractTypeMapping(c, jsonClassMapping.get(c));
			}
		    mapper.registerModule(module);
	    }
	    setCommonConfs(mapper);
    	
	    return mapper;
	}


	
	/**
	 * @param json
	 * @param function function(ob){return ob;}
	 */
	public static <T> T getJsonValueByFunc(String json, String function, Class<T> cls){
		return new JSDataObject(json).getJsonValueByFunc(function, cls);
	}
	
	/**
	 * @param json
	 * @param express 
	 */
	public static <T> T getJsonValue(String json, String express, Class<T> cls){
		return new JSDataObject(json).getExpData(express, cls);
	}

	
}

package com.xiangGo.common.bean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import sun.org.mozilla.javascript.internal.NativeArray;
import sun.org.mozilla.javascript.internal.NativeObject;

public class JSDataObject {
	
	private String json = null;
	private ScriptEngine se = null;
	
	public JSDataObject(String json){
		setJson(json);
	}
	
	public void setJson(String json){
		this.json = json;
		setScriptEngine();
	}
	
	private void setScriptEngine(){
		se = new ScriptEngineManager().getEngineByName("javascript");
		try {
			se.eval("var obj = "+json+";");
		} catch (ScriptException e) {
			//e.printStackTrace();
		}
	}
	
	
	
	public <T> T getExpData(String exp, Class<T> clz) {
		T t = getJsonValueByFunc("function(ob){return ob"+exp+";}",clz);
		return t;
	}

	public <T> T getJsonValueByFunc(String function, Class<T> clz) {
		T t = null;
		try {
			se.eval("var func = "+function+";");
			se.eval("var t = func(obj);");
			
			String type = (String)se.eval("typeof t");
			
			if(!"undefined".equals(type)){
				t = (T) se.eval("t");
			}
			
		} catch (ScriptException e) {
			System.out.println("JSDataObject get error, function:"+function);
			//e.printStackTrace();
		}
		return t;
	}
	
	
	public static Object getNativeObject(Object obj){
		
		if(obj instanceof NativeArray){
			List list = new ArrayList();
			
			NativeArray arr = (NativeArray)obj;
			for(int i=0;i<arr.getLength();i++){
				list.add(getNativeObject(arr.get(i, null)));
			}
			return list;
		}
		
		if(obj instanceof NativeObject){
			String str = obj.toString();
			if("[object Object]".equals(str)){
				HashMap hash = new HashMap();
				NativeObject no = (NativeObject)obj;
				
				Object[] keys = no.getAllIds();
				for(Object k : keys){
					Object v = null;
					if(k instanceof Integer){
						v = no.get((Integer)k, null);
					}else{
						v = no.get(k.toString(), null);
					}
					
					hash.put(k.toString(), getNativeObject(v));
				}
				
				return hash;
			}else{
				return obj;
			}
		}
		if(obj instanceof Double){
			//优先long输出, 不要科学计数法
			double d = Double.parseDouble(obj.toString());
			if(d == (long)d){
				return (long)d;
			}
			return d;
		}
		
		return obj;
	}
}

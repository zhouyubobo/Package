package com.xiangGo.common.script;

import java.io.InputStream;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import com.xiangGo.common.util.FileIOUtil;

public class ExtScriptEngineManager {

	private static String js_funcs = "";
	static {
		
		String[] funcs = new String[]{
				"functions/array.indexOf.js",
				"functions/array.lastIndexOf.js"
		};
		
		js_funcs = "";
		for(String k : funcs){
			InputStream is = ExtScriptEngineManager.class.getResourceAsStream(k);
			js_funcs += FileIOUtil.toString(is);
			js_funcs += "\n";
		}
	}
	
	public static ScriptEngine createEngine() throws ScriptException{
		ScriptEngine engine =  new ScriptEngineManager().getEngineByName("js");
		engine.eval(js_funcs);
		return engine;
	}

	
	public static void main(String[] args) throws ScriptException {
	}
}

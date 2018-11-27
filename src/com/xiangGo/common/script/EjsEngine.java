package com.xiangGo.common.script;

import java.io.InputStream;
import java.util.HashMap;

import javax.script.ScriptEngine;
import javax.script.ScriptException;

import com.xiangGo.common.util.FileIOUtil;
import com.xiangGo.common.util.JsonUtil;

public class EjsEngine {
	
	private static String js_tmpl = "";
	static {
		InputStream is = EjsEngine.class.getResourceAsStream("simpleTemplate.js");
		js_tmpl =  FileIOUtil.toString(is);
	}
	
	
	private String tmpl = "";
	public EjsEngine setTmpl(String tmpl) throws ScriptException{
		this.tmpl = tmpl;
		initEngine();
		return this;
	}
	
	ScriptEngine engine = null;
	private void initEngine() throws ScriptException{
		engine = ExtScriptEngineManager.createEngine();
		
		engine.eval(js_tmpl);
		engine.eval("var tmpl = '';");
		String[] ContentLines = tmpl.split("\n");
		for(String line : ContentLines){
			engine.eval("tmpl += \"" + line.replace("\"", "\\\"") + "\";");
			engine.eval("tmpl += '\\n';");
		}
	}
	
	public String render(HashMap hash){
		
		String content = null;
		
		try {
			engine.eval("var hash="+JsonUtil.toJsonString(hash)+";");
			engine.eval("var txt=Tmpl(tmpl,hash);");
			
			content = engine.get("txt").toString();
		} catch (ScriptException e) {
			//e.printStackTrace();
		}
		
		return content;
	}
}

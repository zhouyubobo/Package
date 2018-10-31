package com.xiangGo.common.bean;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.script.ScriptEngine;
import javax.script.ScriptException;

import com.xiangGo.common.script.ExtScriptEngineManager;
import com.xiangGo.common.service.ServiceUtil;
import com.xiangGo.common.util.HttpUtil;
import com.xiangGo.common.util.JsonUtil;

public class PackageDataObj {

	private Object rt = null;
	
	public PackageDataObj(){
	}

	public PackageDataObj deal(ParamHash pHash, boolean useCache) throws ScriptException, UnsupportedEncodingException{
		this.rt = null;
		
		PackageDealer dealer = new PackageDealer();
		dealer.useCache = useCache;
		this.rt = dealer.setParamHash(pHash).deal();
		return this;
	}
	
	public Object getResult(){
		return rt;
	}
	
	private static class PackageDealer{
		
		private String currentNodeExp = null;
		private String currentScriptInfo = null;
		
		private void engineEval(String script) throws ScriptException{
			currentScriptInfo = script;
			try{
				engine.eval(script);
			}catch(ScriptException ex){
				throw ex;
			}
		}
		
		ScriptEngine engine = null;
		PackageDealer() throws ScriptException{
			engine =  ExtScriptEngineManager.createEngine();
			
			engine.eval("var $root = {};");
			engine.eval("var $parent = {};");
			engine.eval("var v = null;");
		}
		
		public boolean useCache = true;
		
		//HashMap<url,responseText>
		private HashMap<String,String> _c = new HashMap<String, String>();

		public ParamHash pHash = null;
		
		public PackageDealer setParamHash(ParamHash pHash){
			this.pHash = pHash;
			return this;
		}
		
		private void reset() throws ScriptException{
			engine.eval("$root = {};");
			engine.eval("$parent = {};");
			engine.eval("v = null;");
			currentNodeExp = null;
		}
		
		public Object deal() throws ScriptException, UnsupportedEncodingException{
			
			Object obj = null;

			try{
				this.reset();
				currentNodeExp = "$root";
				
				String str = cacheGetUrl(pHash.getUrl());
				dealJsonRt("$root", str, pHash.returnType, pHash.dealFunc);
				
				dealProps(pHash,"");

	     		engine.eval("v = $root;");
	     		
	     		obj = JSDataObject.getNativeObject(engine.get("v"));
			}catch(ScriptException ex){
				throw new ScriptException("PackageDealer error, currentExp:"+currentNodeExp+", currentScriptInfo:"+currentScriptInfo+"\n"+ex.getMessage());
			}
			
     		return obj;
		}
		
		private String cacheGetUrl(String url){
			if(null == url)
				return null;
			
			if(useCache && _c.containsKey(url)){
				return _c.get(url);
			}
			
			long st = System.currentTimeMillis();
			String rt = HttpUtil.sendGet(url);
			long ct = System.currentTimeMillis() - st;
			if(ct>200){
				System.out.println("PackageDealer.cacheGetUrl["+url+"] cost "+ct+"ms");
			}
			
			_c.put(url, rt);
			return _c.get(url);
		}
		
		private boolean checkCondition(ParamHash pHash, String parentExp) throws ScriptException{
			
			boolean go = true;
			
			Pattern r = Pattern.compile("\\$\\{([^\\}]{1,})\\}");

     		engine.eval("$parent = $root"+ parentExp +";");

     		String condition = pHash.condition;
     		if(null != condition && !"".equals(condition)){
     			Matcher m = r.matcher(condition);
    	     	while (m.find()){
    	     		engineEval("v = '' + " + m.group(1) + ";");
    	     		String v = engine.get("v").toString();
    	     		condition = condition.replace(m.group(), v);
    	     	}
    	     	engineEval("v = ((" + condition + ")?true:false);");
    	     	go = Boolean.parseBoolean(engine.get("v").toString());
     		}

     		engine.eval("$parent = {};");
     		
     		return go;
		}
		
		private String dealGet(ParamHash pHash, String parentExp) throws ScriptException{
			Pattern r = Pattern.compile("\\$\\{([^\\}]{1,})\\}");

     		engine.eval("$parent = $root"+ parentExp +";");

			String propUrl = pHash.getUrl();
     		if(true){
    	     	Matcher m = r.matcher(propUrl);
    	     	while (m.find()){
    	     		engineEval("v = '' + " + m.group(1) + ";");
    	     		String v = engine.get("v").toString();
    	     		propUrl = propUrl.replace(m.group(), v);
    	     	}
     		}
     		engine.eval("$parent = {};");
	     	
	     	return cacheGetUrl(propUrl);
		}
		
		private void dealProps(ParamHash pHash, String selfFullExp) throws ScriptException, UnsupportedEncodingException{
			for(ParamHash pph : pHash.props){
				
				String rootFullExp = selfFullExp + pph.parentExp;
				
				if(null == pph.parentType){
					
					String currentExpr = "$root" + rootFullExp + "."+ pph.alias;
					currentNodeExp = currentExpr;
					
			     	boolean go = checkCondition(pph, rootFullExp);
			     	if(!go){
			     		continue;
			     	}
			     	
			     	String rt = null;
					if(null != pph.url && !"".equals(pph.url)){
						rt = dealGet(pph, rootFullExp);
					}
					dealJsonRt("$root" + rootFullExp + "."+ pph.alias, rt, pph.returnType, pph.dealFunc);
					
		     		dealProps(pph, rootFullExp + "."+ pph.alias);
					
				}else if("list".equals(pph.parentType)){
		     		engine.eval("v = ((typeof $root" + rootFullExp + ")? $root" + rootFullExp + ".length : 0);");
		     		int v = (int)Double.parseDouble(engine.get("v").toString());
		     		for(int i=0;i<v;i++){
				     	
		     			ParamHash npph = new ParamHash();
				     	npph.parentExp = pph.parentExp+"["+i+"]";
				     	npph.parentType = pph.parentType;
				     	npph.returnType = pph.returnType;
				     	npph.condition = pph.condition;
				     	npph.url = pph.url;
				     	npph.props = pph.props;

						String currentExpr = "$root"+ selfFullExp + npph.parentExp + "."+ pph.alias;
						currentNodeExp = currentExpr;
						
				     	boolean go = checkCondition(npph, selfFullExp + npph.parentExp);
				     	if(!go){
				     		continue;
				     	}

				     	if((null == npph.url || "".equals(npph.url)) || "".equals(pph.alias)){
				     		dealProps(npph, selfFullExp + npph.parentExp);
				     		continue;
				     	}
				     	
						String rt = dealGet(npph, selfFullExp + npph.parentExp);
						dealJsonRt("$root"+ selfFullExp + npph.parentExp + "."+ pph.alias, rt, pph.returnType, pph.dealFunc);
				     	
			     		dealProps(npph, selfFullExp + npph.parentExp + "."+ pph.alias);
		     		}
				}
			}
		}
		
		private void dealJsonRt(String paramExpr, String rt, String returnType, String dealFunc) throws ScriptException, UnsupportedEncodingException{

            if ("text".equals(returnType))
            {
                String content = "";
                if (null != rt)
                {
                    content = rt;
                }
                if (null != content)
                {
                    content = URLEncoder.encode(content, "utf-8").replace("+", "%20");
                }

                engineEval(paramExpr + " = '" + content + "';");
                engineEval(paramExpr + "= decodeURIComponent(" + paramExpr + ");");
            }
            else if(null == returnType || "".equals(returnType) || "json".equals(returnType))
            {
                engineEval(paramExpr + " = " + (null == rt || "".equals(rt) ? "null" : rt) + ";");
            }
            else
            {
                engineEval(paramExpr + " = null;");
            }
	     	if(null != dealFunc){
	     		engineEval("_$dealFunc = "+dealFunc);
	     		engineEval(paramExpr + " = _$dealFunc("+paramExpr+");");
	     	}
		}
		
	}
	
	public static class ParamHash{
		
		public String url;
		public String alias = null;//别名
		public String parentExp = "";//$parent对象相对于上一层parent的表达式
		public String parentType = null;//$parent对象类型

        public String returnType = null;//http返回对象类型, [json, txt], null默认为json
		public String condition = null;
		public String dealFunc = null;
		
		public List<ParamHash> props = new ArrayList<ParamHash>();
		
		public static ParamHash parse(String json){
			ParamHash phash = JsonUtil.toObject(json, ParamHash.class);
			
			return phash;
		}
		
		public String getUrl(){
			if(null == url){
				return url;
			}

			if(null != url && (url.startsWith("http://") || url.startsWith("https://"))){
				return url;
			}

			String host = ServiceUtil.getServerAddress(url);
			String path = "http://" + host + "" + url;
			
			return path;
		}
	}
	
}

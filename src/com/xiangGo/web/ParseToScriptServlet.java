package com.xiangGo.web;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;

import javax.script.ScriptException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.xiangGo.common.bean.PackageDataObj.ParamHash;
import com.xiangGo.common.script.EjsEngine;
import com.xiangGo.common.util.JsonUtil;
import com.xiangGo.common.util.TmplResourceUtil;

public class ParseToScriptServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

    public ParseToScriptServlet() {
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String paramHashJsonStr = request.getParameter("paramHashJsonStr");
		int hostParse = "1".equals(request.getParameter("hostParse"))?1:0;
		
		boolean parseHost = (hostParse != 0);

		HashMap hash = new HashMap();
		
		if(parseHost){
			ParamHash phash = ParamHash.parse(paramHashJsonStr);
			hash.put("paramHash", phash);
		}else{
			hash.put("paramHash", JsonUtil.toObject(paramHashJsonStr, HashMap.class));
		}

		String scriptTmpl = TmplResourceUtil.getTmplResource(ParseToScriptServlet.class, "classpath:com/xiangGo/common/tmpl/script_package.js.tmpl");

		EjsEngine engine = new EjsEngine();
		String script = "";
		try {
			script = engine.setTmpl(scriptTmpl).render(hash);
		} catch (ScriptException e) {
			//e.printStackTrace();
		}
		
		response.setHeader("Content-type", "application/javascript;charset=UTF-8");
		response.setCharacterEncoding("UTF-8");
        OutputStream ps = response.getOutputStream();
        ps.write(script.getBytes("UTF-8"));  
	}
}

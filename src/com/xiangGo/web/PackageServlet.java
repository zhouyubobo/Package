package com.xiangGo.web;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;

import javax.script.ScriptException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.xiangGo.common.bean.PackageDataObj;
import com.xiangGo.common.bean.PackageDataObj.ParamHash;
import com.xiangGo.common.util.JsonUtil;

public class PackageServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

    public PackageServlet() {
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String paramHashJsonStr = request.getParameter("paramHashJsonStr");
		int noCache = "1".equals(request.getParameter("noCache"))?1:0;

		Object rt = null;
		
		ParamHash phash = ParamHash.parse(paramHashJsonStr);
		
		PackageDataObj dealer = new PackageDataObj();
		boolean useCache = (noCache == 0);
		try {
			rt = dealer.deal(phash, useCache).getResult();
		} catch (Exception e) {
			//e.printStackTrace();
			HashMap hash = new HashMap();
			hash.put("error", e.toString());
			rt = hash;
		}
		
		response.setHeader("Content-type", "application/json;charset=UTF-8");
		response.setCharacterEncoding("UTF-8");
        OutputStream ps = response.getOutputStream();
        ps.write(JsonUtil.toJsonString(rt).getBytes("UTF-8"));  
	}

}

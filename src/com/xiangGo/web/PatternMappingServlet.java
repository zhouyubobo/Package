package com.xiangGo.web;

import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.xiangGo.common.service.ServiceUtil;
import com.xiangGo.common.util.JsonUtil;

public class PatternMappingServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

    public PatternMappingServlet() {
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String op = request.getParameter("op");

		String pattern = request.getParameter("pattern");
		String host = request.getParameter("host");
		
		Object rt = null;
		
		if(null != op){
			 if("register".equals(op)){
				ServiceUtil.registerMapping(pattern, host);
			 }
			 if("unRegister".equals(op)){
				ServiceUtil.unRegisterMapping(pattern, host);
			 }
		}
		rt = ServiceUtil.getUrlPatternMapping();
		
		response.setHeader("Content-type", "application/json;charset=UTF-8");
		response.setCharacterEncoding("UTF-8");
        OutputStream ps = response.getOutputStream();
        ps.write(JsonUtil.toJsonString(rt).getBytes("UTF-8"));  
	}
}

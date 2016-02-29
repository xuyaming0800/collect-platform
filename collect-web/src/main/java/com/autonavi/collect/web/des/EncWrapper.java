package com.autonavi.collect.web.des;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import com.autonavi.collect.des.BaseEnc;

public class EncWrapper extends HttpServletRequestWrapper {

	public EncWrapper(HttpServletRequest request) {
		super(request);
	}
	@Override
	public String getParameter(String name) {
		String value = super.getParameter(name);
		if(value!=null&&!name.equals("charset")){
			if(super.getParameter("charset")!=null&&!super.getParameter("charset").equals("")){
				value=BaseEnc.disp_decode(value,super.getParameter("charset"));
			}else{
				value=BaseEnc.disp_decode(value,"utf-8");
			}
			
		}
		
		return value;
	}

}

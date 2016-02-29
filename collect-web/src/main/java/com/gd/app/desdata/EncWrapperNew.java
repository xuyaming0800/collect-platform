package com.gd.app.desdata;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

public class EncWrapperNew extends HttpServletRequestWrapper {

	public EncWrapperNew(HttpServletRequest request) {
		super(request);
	}
	@Override
	public String getParameter(String name) {
		String value = super.getParameter(name);
		if(value!=null)
		value=BaseEncNew.disp_decode(value,"utf-8");
		return value;
	}

}

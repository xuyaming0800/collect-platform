package com.autonavi.collect.web.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;

/**
 * web参数工具类
 * @author wenfeng.gao
 *
 */
public class WebParamsUtils {

	private static final String YYMMDDHHMMSS="yyyy-MM-dd HH:mm:ss";


	/**
	 * 获取日期参数
	 * @param request
	 * @param paramName
	 * @return
	 * @throws Exception
	 */
	public static Date getDateParam(HttpServletRequest request,String paramName) throws Exception{
		String startTimeStr = request.getParameter(paramName);
		if(StringUtils.isBlank(startTimeStr)){
			return null;
		}
		DateFormat df = new SimpleDateFormat(YYMMDDHHMMSS); 
		return df.parse(startTimeStr);
	}
	
	public static String getStringParam(HttpServletRequest request,String paramName,String defaultValue) throws Exception{
		String paramValue = request.getParameter(paramName);
		if(StringUtils.isBlank(paramValue)){
			return defaultValue;
		}
        return paramValue;
	}

}

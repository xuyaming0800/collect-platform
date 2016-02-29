package com.autonavi.collect.web.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.autonavi.collect.web.constant.WebConstant;

import autonavi.online.framework.property.PropertiesConfig;
import autonavi.online.framework.property.PropertiesConfigUtil;

public class ImgReloadFilter implements Filter {
	private static final Logger logger = Logger.getLogger(ImgReloadFilter.class);
    private String filterPath;
    private String redirectIp;
    private String rootPath;
    private String imgRootPath;
    private PropertiesConfig pc = null;
	@Override
	public void destroy() {
		// TODO Auto-generated method stub

	}
	public ImgReloadFilter()throws Exception{
		if (pc == null)
			pc = PropertiesConfigUtil.getPropertiesConfigInstance();
	}

	@Override
	public void doFilter(ServletRequest arg0, ServletResponse arg1,
			FilterChain arg2) throws IOException, ServletException {
		// TODO Auto-generated method stub
		HttpServletRequest request = (HttpServletRequest) arg0;
		String path=request.getRequestURL().toString();
		String[] fp=filterPath.split(",");
		boolean b=true;
		for(String s:fp){
			s="/"+s+"/";
			if(path.indexOf(s)!=-1){
				b=false;
				if(logger.isDebugEnabled()){
					logger.debug(path+ "will redirect!");
				}
				break;
			}
		}
		if(b){
			arg2.doFilter(arg0, arg1);
		}else{
			HttpServletResponse response = (HttpServletResponse) arg1;
			path=path.substring(path.indexOf(rootPath));
			path=path.replace(rootPath+"/", imgRootPath);
			response.sendRedirect(redirectIp+"/"+path);
			
		}
		

	}

	@Override
	public void init(FilterConfig arg0) throws ServletException {
		// TODO Auto-generated method stub
		filterPath=pc.getProperty(WebConstant.IMG_FILTER_FILEPATHS).toString();
		redirectIp=pc.getProperty(WebConstant.IMG_FILTER_REDIRECT_ADDRESS).toString();
		rootPath=pc.getProperty(WebConstant.IMG_FILTER_APP_ROOT_PATH).toString();
		imgRootPath=pc.getProperty(WebConstant.IMG_FILTER_IMG_ROOT_PATH).toString();
		if(imgRootPath==null)imgRootPath="";
		if(!imgRootPath.equals("")){
			imgRootPath=imgRootPath+"/";
		}

	}

}

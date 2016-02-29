package cn.dataup.mgr.web.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import cn.dataup.mgr.web.component.PropertityUtilComponent;
import cn.dataup.mgr.web.constant.WebConstant;

import com.autonavi.collect.mgr.entity.SimpleUserInfo;
import com.autonavi.collect.service.CollectMgrUtilService;

public class SimpleCheckUserFilter implements Filter {
	private static final Logger logger = Logger.getLogger(SimpleCheckUserFilter.class);
	@Override
	public void destroy() {
		// TODO Auto-generated method stub

	}

	@Override
	public void doFilter(ServletRequest arg0, ServletResponse arg1,
			FilterChain arg2) throws IOException, ServletException {
		// TODO Auto-generated method stub
		HttpServletRequest request = (HttpServletRequest) arg0;
		HttpServletResponse response = (HttpServletResponse) arg1;
		ServletContext context = request.getSession().getServletContext();  
		ApplicationContext ctx = WebApplicationContextUtils.getWebApplicationContext(context);
		PropertityUtilComponent prop=ctx.getBean(PropertityUtilComponent.class);
		CollectMgrUtilService util=ctx.getBean(CollectMgrUtilService.class);
		try {
			SimpleUserInfo info=util.getUserInfoFromSession(prop.getBizProperty(WebConstant.COLLECT_MGR_USER_SESSION)+request.getSession().getId(), 
					SimpleUserInfo.class, 
					Integer.valueOf(prop.getBizProperty(WebConstant.SESSION_TIME_OUT)));
			if(info!=null){
				arg2.doFilter(arg0, arg1);
			}else{
				request.getSession().invalidate();
				request.getRequestDispatcher("/session_timeout.jsp").forward(request, response);
			}
		} catch (Exception e) {
			logger.error(e.getCause(), e);
			request.getSession().invalidate();
			request.getRequestDispatcher("/session_timeout.jsp").forward(request, response);
		}
	}

	@Override
	public void init(FilterConfig arg0) throws ServletException {

	}

}

package com.gd.app.web;

import java.io.IOException;
import java.io.PrintWriter;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.gd.app.entity.ResultEntity;
import com.gd.app.entity.ServletInfoBean;
import com.gd.app.exception.AppException;
import com.gd.app.service.TaskUtilService;
@Controller
public class CommonController extends BaseController{
	private Logger logger = LogManager.getLogger(this.getClass());
	@Resource
	TaskUtilService taskUtilService;
	@RequestMapping("/common")
	public void systemTime(HttpServletRequest req, HttpServletResponse resp){
		try {
			resp.setContentType("text/html;charset=utf-8");
			String key = req.getParameter("key");
			if(key != null && !key.trim().equals("")){
				PrintWriter writer = resp.getWriter();
				
				//  统一获取系统当前时间
				if(key.equals("systemTime")) {
					try{
						String currentTime = taskUtilService.querySystemDateTime();
						writer.write("{\"success\": true, \"msg\": \"获取当前时间成功!\", \"currentTime\": \""+ currentTime +"\"}");
						
					} catch(Exception e) {
						writer.write("{\"success\": false, \"msg\": \"获取当前时间失败!\"}");
					} finally {
						writer.close();
					}
					return;
					
					// 用户登录接口
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			logger.error(e.getMessage(), e);
		}
	}
	/**
	 * 提交错误日志
	 * @param req
	 * @param resp
	 */
	@RequestMapping("/ereport")
	public void ereport(HttpServletRequest req, HttpServletResponse resp){
		ResultEntity entity = new ResultEntity();
        try{
			// 添加修改任务名称
        	logger.info("==error report start...");
			String userName = req.getParameter("userName");
			String type = req.getParameter("type"); //0 代表安卓 1代表Ios
			String version = req.getParameter("version");
			String erCode = req.getParameter("erCode");//错误编号
			String erInfo = req.getParameter("erInfo");//错误信息
			String deviceInfo = req.getParameter("deviceInfo"); //设备信息
			
			if(userName==null)userName="";
			if(type==null)type="";
			if(version==null)version="";
			if(erCode==null)erCode="";
			if(erInfo==null)erInfo="";
			if(deviceInfo==null)deviceInfo="";
			
			logger.info("==error report parm : userName="+userName+" type="+type+" version="+version+" erCode="+erCode+" erInfo="+erInfo+" deviceInfo="+deviceInfo);
			taskUtilService.saveErrorInfo(userName, type, version,deviceInfo, erCode, erInfo);
			entity.setCode("0");
		    entity.setDesc(SUCCESS);
        }catch(AppException e){
        	entity.setCode(e.getSqlExpEnum().getCode());
			entity.setDesc(e.getSqlExpEnum().getMessage());
			logger.error("==ErrorReportServlet save error ====",e);
        }
        
		// 输出结果
		this.writeResult(entity, req, resp);
	}

	@Override
	protected ServletInfoBean vaildJsonData(String content, ServletInfoBean obj) {
		// TODO Auto-generated method stub
		return null;
	}

}

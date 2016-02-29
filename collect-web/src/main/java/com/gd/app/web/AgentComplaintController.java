package com.gd.app.web;

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
import com.gd.app.exception.AppExceptionEnum;
import com.gd.app.service.AgentTaskService;
import com.gd.app.util.FilterUtil;
@Controller
public class AgentComplaintController extends BaseController {
	private Logger logger = LogManager.getLogger(this.getClass());
	@Resource
	AgentTaskService agentTaskService;
	@RequestMapping("/agentComplaint")
	private void saveAgentComplaint(HttpServletRequest req, HttpServletResponse resp){
		ResultEntity entity = new ResultEntity();
		logger.info("用户意见接口请求:"
						+ req.getQueryString());
		if (!valdateParam(req)) {
			processError(req, resp, entity);
			return;
		}
		String phone = req.getParameter("phone");
		String content =req.getParameter("content");
		String userName = req.getParameter("userName");
		String deviceSys = null;
		if(req.getParameter("deviceSys")!=null){
			deviceSys=req.getParameter("deviceSys");
		}
		String deviceModel = null;
		if(req.getParameter("deviceModel")!=null){
			deviceModel=req.getParameter("deviceModel");
		}
		entity.setCode("0");
		try {
			//service.saveAgentComplaint(phone, userName, content);
			agentTaskService.saveAgentComplaint(phone, userName, content, deviceSys, deviceModel);
			entity.setDesc(SUCCESS);
		} catch (AppException e) {
			entity.setCode(e.getSqlExpEnum().getCode());
			entity.setDesc(e.getSqlExpEnum().getMessage());
		}
		// 输出结果
		writeResult(entity, req, resp);
	}
	/**
	 * 验证请求参数
	 * 
	 * @param req
	 * @return
	 */
	private boolean valdateParam(HttpServletRequest req) {

		String phone = req.getParameter("phone");
		String content = req.getParameter("content");
		String userName = req.getParameter("userName");
		if (userName == null || "".equals(userName) || phone == null
				|| "".equals(phone) || content == null || "".equals(content)) {
			logger.error("请求参数phone,content为空");
			return false;
		}
		boolean isMobile = FilterUtil.isMobileNO(phone);
		boolean isEmail = FilterUtil.isEmail(phone);

		if (!(isMobile || isEmail)) {
			logger.warn("联系方式格式不对!" + phone);
			return false;
		}
		return true;
	}
	private void processError(HttpServletRequest req, HttpServletResponse resp,
			ResultEntity entity) {
		AppException ex = new AppException(AppExceptionEnum.PARAM_FORMAT_EXP);
		writeError(entity, req, resp, ex);
	}

	@Override
	protected ServletInfoBean vaildJsonData(String content, ServletInfoBean obj) {
		// TODO Auto-generated method stub
		return null;
	}

}

package com.gd.app.web;

import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.gd.app.entity.AgentTask;
import com.gd.app.entity.ResultEntity;
import com.gd.app.entity.ServletInfoBean;
import com.gd.app.exception.AppException;
import com.gd.app.exception.AppExceptionEnum;
import com.gd.app.service.AgentTaskService;
@Controller
public class AgentTaskCountController extends BaseController {
	private Logger logger = LogManager.getLogger(this.getClass());
	@Resource
	AgentTaskService agentTaskService;
	@RequestMapping("/queryAgentTaskCount")
	public void getTaskCount(HttpServletRequest req, HttpServletResponse resp){
		ResultEntity entity = new ResultEntity();
		logger.info("统计用户上传任务状态量接口请求:"
				+ req.getQueryString());
		// 验证
		if (!validateParam(req)) {
			entity.setCode(AppExceptionEnum.PARAM_FORMAT_EXP.getCode());
			entity.setDesc(AppExceptionEnum.PARAM_FORMAT_EXP.getMessage());
			writeResult(entity, req, resp);
			return;
		}

		String userName = req.getParameter("userName");
		entity.setCode("0");
		entity.setDesc(SUCCESS);
		try {
			List<AgentTask> list = agentTaskService.queryTotalGroupByStatus(userName);
			entity.setResultData(list);
		} catch (AppException e) {
			entity.setCode(e.getSqlExpEnum().getCode());
			entity.setDesc(e.getSqlExpEnum().getMessage());
		}
		// 输出结果
		writeResult(entity, req, resp);
	}
	
	private boolean validateParam(HttpServletRequest req) {
		String userName = req.getParameter("userName");
		if (userName == null || "".equals(userName)) {
			return false;
		}
		return true;
	}
	@Override
	protected ServletInfoBean vaildJsonData(String content, ServletInfoBean obj) {
		// TODO Auto-generated method stub
		return null;
	}

}

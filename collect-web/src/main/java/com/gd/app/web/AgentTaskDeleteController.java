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
import com.gd.app.service.TaskHandlerService;
import com.gd.app.util.StringUtil;
@Controller
public class AgentTaskDeleteController extends BaseController {
	private Logger logger = LogManager.getLogger(this.getClass());
	@Resource
	private TaskHandlerService taskHandlerService;
	@RequestMapping("/deleteTask")
	public void deleteTask(HttpServletRequest req, HttpServletResponse resp){
		ResultEntity entity = new ResultEntity();
		logger.info("用户删除任务请求：" + req.getQueryString(), "UTF-8");
		String taskId = req.getParameter("taskId");
		taskId = (taskId == null) ? "" : taskId;
		String userName = req.getParameter("userName");
		if (userName == null || ("".equals(userName))) {
			this.sendMsg(resp, "{\"success\": false, \"code\": 201, \"desc\": \"请求参数 【userName】为空!\"}");
			return;
		}
		String dataName = req.getParameter("dataName");
		if (dataName == null || ("".equals(dataName))) {
			this.sendMsg(resp, "{\"success\": false, \"code\": 201, \"desc\": \"请求参数 【dataName】为空!\"}");
			return;
		}
		dataName = StringUtil.formatStr(dataName);
		try {
			taskHandlerService.updateTaskByUnLock(taskId, userName, dataName);
			// service.deleteTask(taskId, userName, dataName);
			entity.setCode("0");
			entity.setDesc(BaseController.SUCCESS);
		} catch (AppException e) {
			entity.setCode(e.getSqlExpEnum().getCode());
			entity.setDesc(e.getSqlExpEnum().getMessage());
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

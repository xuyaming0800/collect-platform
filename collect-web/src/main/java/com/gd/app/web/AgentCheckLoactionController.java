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
import com.gd.app.service.TaskUtilService;
@Controller
public class AgentCheckLoactionController extends BaseController {
	private Logger logger = LogManager.getLogger(this.getClass());
	@Resource
	private TaskUtilService taskUtilService;
	@RequestMapping("/checkLocation")
	public void checkLocation(HttpServletRequest req, HttpServletResponse resp){
		logger.info("检测地理位置合法性" + req.getQueryString());
		ResultEntity entity = new ResultEntity();
		String x = req.getParameter("X");
		String y = req.getParameter("Y");
		String isGPS = req.getParameter("isGPS");

		if (!this.valieateXY(x, y,logger)) {
			AppException ex = new AppException(AppExceptionEnum.COORDINATE_ERROR);
			this.writeError(entity, req, resp, ex);
			return;
		}
		try {
			if (!"0".equals(isGPS)) {
				String[] offsetList = taskUtilService.offsetXY(x, y);
				if (offsetList.length == 2) {
					x = offsetList[0];
					y = offsetList[1];
				}
			}
			double corrX = Double.parseDouble(x);
			double corrY = Double.parseDouble(y);
			boolean flag=taskUtilService.checkLocationVaild(corrX, corrY);
			if(flag){
				entity.setCode("0");
				entity.setDesc(BaseController.SUCCESS);
			}else{
				AppException ex = new AppException(AppExceptionEnum.TASK_VALIDATE_ERROR);
				this.writeError(entity, req, resp, ex);
				return;
			}
		} catch (AppException e) {
			AppException ex = new AppException(AppExceptionEnum.TASK_VALIDATE_ERROR);
			this.writeError(entity, req, resp, ex);
			return;
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

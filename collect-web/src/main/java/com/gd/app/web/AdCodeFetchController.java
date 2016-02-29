package com.gd.app.web;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

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
import com.gd.app.service.TaskUtilService;
/**
 * AdCode获取controller
 * @author yaming.xu
 *
 */
@Controller
public class AdCodeFetchController extends BaseController {
	
	private Logger logger = LogManager.getLogger(this.getClass());
	@Resource
	private TaskUtilService taskUtilService;
	
	@Resource
	AgentTaskService agentTaskService;
	
	//直辖市相关的城区/郊县
	private static ConcurrentHashMap<String, String> municipalitiesMap = new ConcurrentHashMap<String, String>();

	static {
		AdCodeFetchController.municipalitiesMap.put("31", "上海");
		AdCodeFetchController.municipalitiesMap.put("11", "北京");
		AdCodeFetchController.municipalitiesMap.put("50", "重庆");
		AdCodeFetchController.municipalitiesMap.put("12", "天津");
	}
	@RequestMapping("/fetchAdCode")
	public void fetchAdCode(HttpServletRequest req, HttpServletResponse resp){
		logger.info("获取adCode请求:" + req.getQueryString());
		ResultEntity entity = new ResultEntity();
		String x = req.getParameter("X");
		String y = req.getParameter("Y");
		String key = req.getParameter("flag");
		String isGPS = req.getParameter("isGPS");

		if (!this.valieateXY(x, y,logger)) {
			AppException ex = new AppException(AppExceptionEnum.COORDINATE_ERROR);
			this.writeError(entity, req, resp, ex);
			return;
		}
		Map<String, Object> adCodeMap = new HashMap<String, Object>();
		entity.setCode("0");
		entity.setDesc(BaseController.SUCCESS);
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
			String adCode = taskUtilService.fetchAdcode(corrX, corrY);
			if (adCode == null || "".equals(adCode) || (adCode.length() != 6)) {
				AppException ex = new AppException(AppExceptionEnum.FETCH_ADCODE_ERROR);
				this.writeError(entity, req, resp, ex);
				return;
			}
			logger.info("获取区县级adCode=" + adCode);
			if ("1".equals(key)) {
				String beginAdCode = adCode.substring(0, 2);
				if (AdCodeFetchController.municipalitiesMap.containsKey(beginAdCode)) {
					adCode = beginAdCode + "0000";
				} else {
					adCode = adCode.substring(0, 4) + "00";
				}
			}
			adCodeMap.put("adCode", adCode);
			entity.setResultData(adCodeMap);
		} catch (AppException e) {
			AppException ex = new AppException(AppExceptionEnum.FETCH_ADCODE_ERROR);
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

package com.gd.app.web;

import java.util.List;
import java.util.Map;

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
import com.gd.app.service.AgentTaskService;
@Controller
public class AutoTaskQueryController extends BaseController {
	private Logger logger = LogManager.getLogger(this.getClass());
	private static final int PAGE_COUNT = 200;
	private final String     LBS_FLAG   = "0";
	@Resource
	AgentTaskService agentTaskService;
	/**
	 * 获取附近已有门址
	 */
	@RequestMapping("autoTaskQuery")
	public void getAutoTask(HttpServletRequest req, HttpServletResponse resp){
		logger.info("主动门址搜索接口: " + req.getQueryString());
        ResultEntity entity = new ResultEntity();

        // 获取参数值

        String x = req.getParameter("X");
        String y = req.getParameter("Y");
        //String radius = req.getParameter("radius");
        String radius = "200";

        String maxRowCount = req.getParameter("maxRowCount");
        //String isZip = req.getParameter("isZip");
        int rowCount = getPageCount(maxRowCount);
        String gpsFlag = req.getParameter("isGPS");
        boolean isGPS = true;
        if (gpsFlag != null && LBS_FLAG.equals(gpsFlag)) {
            isGPS = false;
        }

        // 设置分页
        entity.setCode("0");
        entity.setDesc(SUCCESS);
        try {
            List<Map<String, String>> list = agentTaskService.queryAutoTask(x, y, radius, rowCount, 1, isGPS);
            entity.setResultData(list);
            int size = (list == null) ? 0 : list.size();
            entity.setTotalCount(String.valueOf(size));
        } catch (AppException e) {
            entity.setCode(e.getSqlExpEnum().getCode());
            entity.setDesc(e.getSqlExpEnum().getMessage());
        }
        // 输出结果
        writeResult(entity, req, resp);
    }
	
	private int getPageCount(String maxRowCount) {
        int pageCount = PAGE_COUNT;
        try {
            pageCount = Integer.parseInt(maxRowCount);
        } catch (Exception e) {
        }
        return pageCount;
    }

	@Override
	protected ServletInfoBean vaildJsonData(String content, ServletInfoBean obj) {
		// TODO Auto-generated method stub
		return null;
	}

}

package cn.dataup.mgr.web.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import cn.dataup.mgr.web.bean.ResultEntity;
import cn.dataup.mgr.web.component.PropertityUtilComponent;

import com.autonavi.collect.exception.BusinessRunException;
import com.autonavi.collect.service.CollectTaskClazzMgrService;
@Controller
public class CollectClazzMgrController extends BaseController{
	private Logger logger = LogManager.getLogger(this.getClass());
	@Autowired
	private CollectTaskClazzMgrService collectTaskClazzMgrService;
	@Autowired
	private PropertityUtilComponent propertityUtilComponent;
	@RequestMapping("/manager/refreshCollectClazz")
	public void refreshClazzCache(HttpServletRequest request,
			HttpServletResponse response){
		logger.info("刷新采集品类缓存");
		ResultEntity entity = new ResultEntity();
		entity.setMsg("刷新成功");
		entity.setCode("0");
		try {
			collectTaskClazzMgrService.refreshCollectTaskClazzCache();
		} catch (BusinessRunException e) {
			this.writeErrorResult(entity, e.getSqlExpEnum().getCode(), e.getSqlExpEnum().getMessage(), response, request);
			return;
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			this.writeErrorResult(entity, "-1", e.getMessage(), response, request);
			return;
		}
		this.writeResult(entity, request, response);
	}

}

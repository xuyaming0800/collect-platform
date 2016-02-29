package com.gd.app.web;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.gd.app.entity.AgentTask;
import com.gd.app.entity.DistrictQueryEntity;
import com.gd.app.entity.ResultEntity;
import com.gd.app.entity.ScoreLevel;
import com.gd.app.entity.ServletInfoBean;
import com.gd.app.entity.TaskAppEntity;
import com.gd.app.exception.AppException;
import com.gd.app.exception.AppExceptionEnum;
import com.gd.app.service.AgentTaskService;
import com.gd.app.util.PageBean;
import com.gd.app.util.StringUtil;
import com.gd.app.util.SysProps;

@Controller
public class AgentTaskQueryController extends BaseController {
	private Logger logger = LogManager.getLogger(this.getClass());
	private final String  LBS_FLAG = "0";
//  private static String TASK_NOT_FIND="1";
    private static String TASK_NAME_ERROR="2";
//  private static String TASK_REPEAT="3";
    private static String TASK_OTHER_ERROR="4";
	@Resource
	AgentTaskService agentTaskService;

	/**
	 * 获取代理商任务接口(全部任务，待审核任务，生效任务，未生效任务)
	 * 
	 * @param req
	 * @param resp
	 */
	@RequestMapping("queryAgentTask")
	public void queryAgentTask(HttpServletRequest req, HttpServletResponse resp) {
		ResultEntity entity = new ResultEntity();
		List<ScoreLevel> l1 = ScoreLevelInit.scoreLevels;
		logger.info("查询用户任务接口请求:" + req.getQueryString());
		if (!validateParam(req)) {
			entity.setCode(AppExceptionEnum.PARAM_FORMAT_EXP.getCode());
			entity.setDesc(AppExceptionEnum.PARAM_FORMAT_EXP.getMessage());
			writeResult(entity, req, resp);
			return;
		}
		String version = req.getParameter("version");
		String userName = req.getParameter("userName");
		int taskStatus = Integer.parseInt(req.getParameter("taskstatus"));

		PageBean pageBean = new PageBean();
		compousePageBean(pageBean, req);
		int rowCount = 0;
		try {
			rowCount = agentTaskService.queryAgentCount(userName, taskStatus);
		} catch (AppException e) {
			entity.setCode(e.getSqlExpEnum().getCode());
			entity.setDesc(e.getSqlExpEnum().getMessage());
			return;
		}
		pageBean.setRowCount(rowCount);
		int endResult = pageBean.getMaxResults() * pageBean.getPageNo();
		int beginResult = pageBean.getFirstResult();

		entity.setCode("0");
		entity.setDesc(SUCCESS);
		try {
			List<AgentTask> list = agentTaskService.queryAgentTask(userName,
					taskStatus, endResult, beginResult, l1, version);
			entity.setResultData(list);
			entity.setTotalCount(String.valueOf(rowCount));
		} catch (AppException e) {
			entity.setCode(e.getSqlExpEnum().getCode());
			entity.setDesc(e.getSqlExpEnum().getMessage());
		}
		// 输出结果
		writeResult(entity, req, resp);
	}

	/**
	 * 获取区域下任务接口
	 * 
	 * @param req
	 * @param resp
	 */
	@RequestMapping("/queryTaskByDistrict")
	public void queryTaskByDistrict(HttpServletRequest req,
			HttpServletResponse resp) {
		logger.info("区县下任务接口请求:" + req.getQueryString());
		ResultEntity entity = new ResultEntity();
		// 验证参数否为空
		if (!validateParamDistrict(req)) {
			entity.setCode(AppExceptionEnum.PARAM_IS_NULL.getCode());
			entity.setDesc(AppExceptionEnum.PARAM_IS_NULL.getMessage());
			writeResult(entity, req, resp);
			return;
		}

		String userName = req.getParameter("username");
		String districtId = req.getParameter("districtid");
		String dataName = req.getParameter("dataName");
		// 除去空白和全角字符
		dataName = StringUtil.formatStr(dataName);
		PageBean pageBean = new PageBean();
		compousePageBeanDistrict(pageBean, req);
		// 不是关键字搜索必须传入rowcount
		if ("".equals(dataName) && (pageBean.getRowCount() == 0)) {
			logger.warn("非关键字搜索,必须传入rowCount");
			entity.setCode(AppExceptionEnum.PARAM_IS_NULL.getCode());
			entity.setDesc(AppExceptionEnum.PARAM_IS_NULL.getMessage());
			writeResult(entity, req, resp);
			return;
		}
		// 关键字搜索
		int rowCount = 0;
		if (!"".equals(dataName)) {
			try {
				rowCount = agentTaskService.queryDistrictCountByKeyWord(districtId,
						dataName);
				pageBean.setRowCount(rowCount);
			} catch (AppException e) {
				entity.setCode(e.getSqlExpEnum().getCode());
				entity.setDesc(e.getSqlExpEnum().getMessage());
				writeResult(entity, req, resp);
				return;
			}
		}
		int endResult = pageBean.getMaxResults() * pageBean.getPageNo();
		int beginResult = pageBean.getFirstResult();

		entity.setCode("0");
		entity.setDesc(SUCCESS);
		try {
			List<TaskAppEntity> list = agentTaskService.queryTaskPageByDistrictId(
					districtId, userName, dataName, endResult, beginResult);
			generateTaskAppEntity(list);
			entity.setResultData(list);
			entity.setTotalCount(String.valueOf(pageBean.getRowCount()));
		} catch (AppException e) {
			entity.setCode(e.getSqlExpEnum().getCode());
			entity.setDesc(e.getSqlExpEnum().getMessage());
		}

		// 输出结果
		writeResult(entity, req, resp);
	}
	/**
	 * 附近任务获取
	 * @param req
	 * @param resp
	 */
	@RequestMapping("/queryTaskByRd")
	public void queryTaskByRd(HttpServletRequest req, HttpServletResponse resp){
		logger.info("附近任务查询接口请求: " + req.getQueryString());
        ResultEntity entity = new ResultEntity();

        // 获取参数值
        String isRoad=req.getParameter("isRoad");
        if(isRoad==null||isRoad.equals("")){
        	isRoad="0";
        }
        String userName = req.getParameter("username");
        String type = req.getParameter("tasktype");
        int taskType = (type == null) ? 0 : Integer.parseInt(type);
        String x = req.getParameter("X");
        String y = req.getParameter("Y");

        String radius = req.getParameter("radius");
        String dataName = req.getParameter("dataName");
        String gpsFlag = req.getParameter("isGPS");
        boolean isGPS = true;
        if (gpsFlag != null && LBS_FLAG.equals(gpsFlag)) {
            isGPS = false;
        }
        //除去空格及全角
        dataName = StringUtil.formatStr(dataName);

        boolean byRoad = isRoad.equals("1");
        		
        // 设置分页
        PageBean pageBean = new PageBean();
        compousePageBean(pageBean, req);
        int rowCount = 0;
        try {
            //rowCount = service.queryTaskByRadiusCountNew(taskType, x, y, radius, dataName, isGPS,byRoad);
        	if(byRoad){
            rowCount =  agentTaskService.queryTaskByRadiusCount(taskType, x, y, radius, dataName, isGPS);
        	}
        } catch (AppException e) {
            entity.setCode(e.getSqlExpEnum().getCode());
            entity.setDesc(e.getSqlExpEnum().getMessage());
            writeResult(entity, req, resp);
            return;
        }
        pageBean.setRowCount(rowCount);

        entity.setCode("0");
        entity.setDesc(SUCCESS);
        try {
        	List<TaskAppEntity> list=null;
        	int endResult = pageBean.getMaxResults() * pageBean.getPageNo();
            int beginResult = pageBean.getFirstResult();
        	if(!byRoad){
        		
        		/**在搜索引擎中查询 附近任务*/
        		 list = agentTaskService.queryTaskByRadiusInSearch(taskType, x, y, radius, userName,
        	                dataName, pageBean.getPageNo(), pageBean.getMaxResults(), isGPS);
        		 if(list.size()>0){
        			 entity.setTotalCount(String.valueOf(list.get(0).getTotal()));
        		 }else{
        			 entity.setTotalCount("0");
        		 }
        		 //list = service.queryTaskByRadius(taskType, x, y, radius, userName, dataName, endResult, beginResult, isGPS);
        		 
        	}else{
                
        		list = agentTaskService.queryTaskByRadiusRd(taskType, x, y, radius, userName,
    	                dataName, endResult, beginResult, isGPS);
        		 entity.setTotalCount(String.valueOf(rowCount));
        	}
        	generateTaskAppEntity(list);

            entity.setResultData(list);
           
        } catch (AppException e) {
            entity.setCode(e.getSqlExpEnum().getCode());
            entity.setDesc(e.getSqlExpEnum().getMessage());
        }
        // 输出结果
        writeResult(entity, req, resp);
	}
	@RequestMapping("/queryTaskByUser")
	public void queryTaskByUser(HttpServletRequest req, HttpServletResponse resp){
		logger.info("用户定向任务接口请求:" + req.getQueryString());
        ResultEntity entity = new ResultEntity();
        // 验证参数否为空
        if (!validateParamUser(req)) {
            entity.setCode(AppExceptionEnum.PARAM_IS_NULL.getCode());
            entity.setDesc(AppExceptionEnum.PARAM_IS_NULL.getMessage());
            writeResult(entity, req, resp);
            return;
        }

        String userName = req.getParameter("username");
      //  String adCode = req.getParameter("adCode");
        String dataName = req.getParameter("dataName");
        //除去空白和全角字符
        String isRoad=req.getParameter("isRoad");
        if(isRoad==null||isRoad.equals(""))isRoad="0";
        dataName = StringUtil.formatStr(dataName);
        PageBean pageBean = new PageBean();
        compousePageBean(pageBean, req);
        // 不是关键字搜索必须传入rowcount
//        if ("".equals(dataName) && (pageBean.getRowCount() == 0)) {
//            log.warn("非关键字搜索,必须传入rowCount");
//            entity.setCode(AppExceptionEnum.PARAM_IS_NULL.getCode());
//            entity.setDesc(AppExceptionEnum.PARAM_IS_NULL.getMessage());
//            writeResult(entity, req, resp);
//            return;
//        }
        //关键字搜索
        int rowCount = 0;
        try {
            rowCount = agentTaskService.queryUserCountByKeyWord(userName,dataName);
            pageBean.setRowCount(rowCount);
        } catch (AppException e) {
            entity.setCode(e.getSqlExpEnum().getCode());
            entity.setDesc(e.getSqlExpEnum().getMessage());
            writeResult(entity, req, resp);
            return;
        }
        int endResult = pageBean.getMaxResults() * pageBean.getPageNo();
        int beginResult = pageBean.getFirstResult();

        entity.setCode("0");
        entity.setDesc(SUCCESS);
        try {
        	if(!isRoad.equals("1")){
        		 List<TaskAppEntity> list = agentTaskService.queryTaskPageByUser(userName, dataName,
        	                endResult, beginResult);
        		 generateTaskAppEntity(list);
        	            entity.setResultData(list);
        	            entity.setTotalCount(String.valueOf(pageBean.getRowCount()));
        	}else{
        		 List<TaskAppEntity> list = agentTaskService.queryTaskPageByUserRd(userName, dataName,
     	                endResult, beginResult);
        		 generateTaskAppEntity(list);
     	         entity.setResultData(list);
     	         entity.setTotalCount(String.valueOf(pageBean.getRowCount()));
        	}
           
        } catch (AppException e) {
            entity.setCode(e.getSqlExpEnum().getCode());
            entity.setDesc(e.getSqlExpEnum().getMessage());
        }

        // 输出结果
        writeResult(entity, req, resp);
	}
	/**
	 * 获取城市下任务量接口(分区县)
	 * @param req
	 * @param resp
	 */
	@RequestMapping("/queryCountByAd")
	public void queryCountByAd(HttpServletRequest req, HttpServletResponse resp){
		logger.info("区县任务量统计接口请求:" + req.getQueryString());

        String adCode = req.getParameter("adCode");
        if ((adCode == null || "".equals(adCode)) || adCode.length() != 6) {
            sendMsg(resp, "{\"success\": false, \"code\": 201, \"desc\": \"请求参数 【adCode】为空 或 不符合规范!\"}");
            return;
        }
        
        String userName = req.getParameter("userName");

        ResultEntity entity = new ResultEntity();
        entity.setCode("0");
        entity.setDesc(SUCCESS);
        try {
        	List<DistrictQueryEntity> list = agentTaskService.queryTaskCountByAdCode(adCode);//普通查询
        	
    		
        	if (null != userName && !"".equals(userName)) {
        		//定向任务查询
        		int total = agentTaskService.queryUserCountByKeyWord(userName,"");
        		
        		DistrictQueryEntity dqe = new DistrictQueryEntity();
        		dqe.setDistrictid("-1");
        		dqe.setDistrictname("推荐任务");
        		dqe.setTotal(total);
        		
        		List<DistrictQueryEntity> tempList = new ArrayList<DistrictQueryEntity>();
        		tempList.add(dqe);
        		
        		for (DistrictQueryEntity d: list) {
        			tempList.add(d);
        		}
        		
        		list = tempList;
        	}
        	
        	entity.setResultData(list);
        } catch (AppException e) {
            entity.setCode(e.getSqlExpEnum().getCode());
            entity.setDesc(e.getSqlExpEnum().getMessage());
        }

        // 输出结果
        writeResult(entity, req, resp);
	}
	/**
	 * 我征服的城市
	 * @param req
	 * @param resp
	 */
	@RequestMapping("/myConquerCity")
	public void myConquerCity(HttpServletRequest req, HttpServletResponse resp){
		    logger.info("用户征服城市接口请求:" + req.getQueryString());
	        ResultEntity entity = new ResultEntity();
	        // 验证参数否为空
	        if (!validateParamConqer(req)) {
	            entity.setCode(AppExceptionEnum.PARAM_IS_NULL.getCode());
	            entity.setDesc(AppExceptionEnum.PARAM_IS_NULL.getMessage());
	            writeResult(entity, req, resp);
	            return;
	        }

	        String userName = req.getParameter("userName");
	        try {
	        	 Map<String,Integer> m=agentTaskService.queryMyConquer(userName);
	        	 entity.setCode("0");
	             entity.setDesc(SUCCESS);
	             entity.setResultData(m);
	           
	        } catch (AppException e) {
	            entity.setCode(e.getSqlExpEnum().getCode());
	            entity.setDesc(e.getSqlExpEnum().getMessage());
	        }

	        // 输出结果
	        writeResult(entity, req, resp);
	}
	@RequestMapping("/passiveTaskBack")
	public void passiveTaskBack(HttpServletRequest req, HttpServletResponse resp){
		logger.info("被动任务纠错接口开始" +req.getQueryString());
        ResultEntity entity = new ResultEntity();
        // 验证参数否为空
        if (!validateParamBack(req)) {
            entity.setCode(AppExceptionEnum.PARAM_IS_NULL.getCode());
            entity.setDesc(AppExceptionEnum.PARAM_IS_NULL.getMessage());
            writeResult(entity, req, resp);
            return;
        }

        String userName = req.getParameter("userName");
        String dataName = req.getParameter("dataName");
        String taskId = req.getParameter("taskId");
        String errorCode = req.getParameter("errorCode");
        String comment= req.getParameter("comment");
        String deviceInfo=req.getParameter("deviceInfo");
        try {
        	 agentTaskService.addPassiveTaskBack(userName, taskId, dataName, errorCode, comment, deviceInfo);
        	 entity.setCode("0");
             entity.setDesc(SUCCESS);
           
        } catch (AppException e) {
            entity.setCode(e.getSqlExpEnum().getCode());
            entity.setDesc(e.getSqlExpEnum().getMessage());
        }

        // 输出结果
        writeResult(entity, req, resp);
	}

	private void compousePageBean(PageBean pageBean, HttpServletRequest req) {
		String pageNo = req.getParameter("page");
		String pageSizePar = req.getParameter("pageSize");

		String pageSize = "";
		int page = 1;
		if (pageNo == null || ("".equals(pageNo))) {
			page = 1;
		} else {
			try {
				page = Integer.parseInt(pageNo);
				pageSize = getBizProperty(SysProps.PROP_ANDROID_PAGE_SIZE);
			} catch (Exception e) {
				logger.warn("page 参数值不为[int]");
			}
		}
		if (pageSizePar != null && (!"".equals(pageSizePar))) {
			pageSize = pageSizePar;
		}

		pageBean.setPageNo(page);
		pageBean.setMaxResults(Integer.parseInt(pageSize));
	}
	private void compousePageBeanDistrict(PageBean pageBean, HttpServletRequest req) {
        String pageNo = req.getParameter("page");
        String pageSizePar = req.getParameter("pageSize");
        String totalCount = req.getParameter("totalCount");
        String pageSize = "";
        int page = 1;
        int rowCount = 0;
        if (pageNo == null || ("".equals(pageNo))) {
            page = 1;
        } else {
            try {
                page = Integer.parseInt(pageNo);
                rowCount = Integer.parseInt(totalCount);
                pageSize =getBizProperty(SysProps.PROP_ANDROID_PAGE_SIZE);
            } catch (Exception e) {
                logger.warn("page 参数值不为[int]");
            }
        }
        if (pageSizePar != null && (!"".equals(pageSizePar))) {
            pageSize = pageSizePar;
        }
        pageBean.setPageNo(page);
        pageBean.setMaxResults(Integer.parseInt(pageSize));
        pageBean.setRowCount(rowCount);

    }

	private boolean validateParam(HttpServletRequest req) {
		String userName = req.getParameter("userName");
		int taskStatus = -1;
		if (userName == null || "".equals(userName)) {
			return false;
		}
		try {
			taskStatus = Integer.parseInt(req.getParameter("taskstatus"));
		} catch (Exception e) {
			logger.warn("[taskStatus]参数格式错误=" + taskStatus);
			return false;
		}
		return true;
	}

	private boolean validateParamDistrict(HttpServletRequest req) {
		String userName = req.getParameter("username");
		String districtId = req.getParameter("districtid");
		if (userName == null || "".equals(userName)) {
			logger.warn("参数[username]为空!");
			return false;
		}
		if (districtId == null || "".equals(districtId)) {
			logger.warn("参数[districtid]为空!");
			return false;
		}

		return true;
	}
	private boolean validateParamUser(HttpServletRequest req) {
        String userName = req.getParameter("username");
       // String adCode = req.getParameter("adCode");
        if (userName == null || "".equals(userName)) {
            logger.warn("参数[username]为空!");
            return false;
        }
//        if (adCode == null || "".equals(adCode)) {
//            log.warn("参数[adCode]为空!");
//            return false;
//        }

        return true;
    }
	private boolean validateParamConqer(HttpServletRequest req) {
        String userName = req.getParameter("userName");
       // String adCode = req.getParameter("adCode");
        if (userName == null || "".equals(userName)) {
            logger.warn("参数[username]为空!");
            return false;
        }
//        if (adCode == null || "".equals(adCode)) {
//            log.warn("参数[adCode]为空!");
//            return false;
//        }

        return true;
    }
	 private boolean validateParamBack(HttpServletRequest req) {
	        String userName = req.getParameter("userName");
	        String taskId = req.getParameter("taskId");
	        String dataName = req.getParameter("dataName");
	        String errorCode = req.getParameter("errorCode");
	        String comment=req.getParameter("comment");
	       // String adCode = req.getParameter("adCode");
	        if (userName == null || "".equals(userName)) {
	            logger.warn("参数[userName]为空!");
	            return false;
	        }
	        if (taskId == null || "".equals(taskId)) {
	        	logger.warn("参数[taskId]为空!");
	            return false;
	        }
	        if (dataName == null || "".equals(dataName)) {
	        	logger.warn("参数[dataName]为空!");
	            return false;
	        }
	        if (errorCode == null || "".equals(errorCode)) {
	        	logger.warn("参数[errorCode]为空!");
	            return false;
	        }
	        if ((errorCode.equals(TASK_OTHER_ERROR)||errorCode.equals(TASK_NAME_ERROR))&&(comment == null || "".equals(comment))) {
	        	logger.warn("参数[comment]为空!");
	            return false;
	        }
//	        if (adCode == null || "".equals(adCode)) {
//	            log.warn("参数[adCode]为空!");
//	            return false;
//	        }

	        return true;
	    }

	@Override
	protected ServletInfoBean vaildJsonData(String content, ServletInfoBean obj) {
		// TODO Auto-generated method stub
		return null;
	}

}

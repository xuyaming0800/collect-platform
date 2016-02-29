package com.gd.app.web;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.gd.app.entity.Appeal;
import com.gd.app.entity.ResultEntity;
import com.gd.app.entity.ServletInfoBean;
import com.gd.app.exception.AppException;
import com.gd.app.service.AppealService;
import com.gd.app.util.DateUtil;


/**
 * 用户申诉controller
 * @author wenfeng.gao
 *
 */


@Controller
@RequestMapping("appeal")
public class AgentAppealController extends BaseController {

	private Logger logger = LogManager.getLogger(AgentAppealController.class);

	private final static String SUCCESS_CODE = "0";
	private final static String SERVICE_EXPETION_CODE = "1";
	private final static String SERVICE_EXPETION_STR = "服务异常";

	@Resource
	private AppealService appealService;



	/**
	 * 用户申诉提交
	 * @param req
	 * @param resp
	 */
	@RequestMapping("/save")
	public void save(HttpServletRequest req, HttpServletResponse resp){
		ResultEntity entity = new ResultEntity();
		logger.info("统计用户申诉提交接口请求:"
				+ req.getQueryString());

		entity.setCode(SUCCESS_CODE);
		entity.setDesc(SUCCESS);
		try {
			Appeal appeal = new Appeal();
			String dataName = req.getParameter("dataName");
			String content = req.getParameter("content");
			String type = req.getParameter("type");
			String reason = req.getParameter("reason");
			String userName = req.getParameter("userName");
			String baseId = req.getParameter("baseId");
			String url = req.getParameter("url");
			
			if (StringUtils.isNotEmpty(dataName)){
				appeal.setName(dataName);
			}	
			if (StringUtils.isNotEmpty(content)){
				appeal.setContent(content);
			}	
			if (StringUtils.isNotEmpty(type)){
				appeal.setType(type);
			}	
			else {
				throw new AppException(400, "缺少申诉类型");
			}	
			if (StringUtils.isNotEmpty(baseId)){
				appeal.setBaseId(Long.parseLong(baseId));
			}	
			if (StringUtils.isNotEmpty(userName)){
				appeal.setUserName(userName);
			}	
			else{
				throw new AppException(400, "缺少用户名");
			}
			if (StringUtils.isNotEmpty(reason)){
				appeal.setReason(reason);
			}	
			else {
				throw new AppException(400, "缺少申诉原因");
			}	
			if (StringUtils.isNotEmpty(url)){
				appeal.setUrl(url);
			}
			appeal.setSubmitTime(DateUtil.getCurrentDateTime());
			appeal.setStatus(0);
			logger.info("提交申诉:" + appeal.toString());

			int count = 0;

			if (appeal.getBaseId() != null) {
				Appeal appealObject = appealService.queryAppealByBaseID(appeal.getBaseId(),userName);
				if (appealObject != null)
					throw new AppException(901, "该任务已经申诉过，不能重复申诉");
			}
			count = appealService.saveAppeal(appeal);
			
			entity.setResultData(count);


		} catch (AppException e) {
			setEntityException(entity,e.getSqlExpEnum().getCode(),e.getSqlExpEnum().getMessage());
		} catch (Exception e) {
			setEntityException(entity,SERVICE_EXPETION_CODE,SERVICE_EXPETION_STR);
			writeResult(entity, req, resp);
		}
		// 输出结果
		writeResult(entity, req, resp);
	}


	/**
	 * 用户任务申诉修改
	 * @param req
	 * @param resp
	 */
	@RequestMapping("/update")
	public void update(HttpServletRequest req, HttpServletResponse resp){
		ResultEntity entity = new ResultEntity();
		logger.info("统计用户任务申诉修改接口请求:"
				+ req.getQueryString());

		entity.setCode(SUCCESS_CODE);
		entity.setDesc(SUCCESS);
		try {
			Appeal appeal = new Appeal();
			
			String userName = req.getParameter("userName");
			
			String status = req.getParameter("status");
			
			String remark = req.getParameter("remark");
			
			String id = req.getParameter("id");
			
			if (StringUtils.isNotEmpty(id) && id.matches("\\d*")){
				appeal.setId(Long.parseLong(id));
			}	
			if (StringUtils.isNotEmpty(status)){
				appeal.setStatus(Integer.parseInt(status));
			}	
			if (StringUtils.isNotEmpty(remark)){
				appeal.setRemark(remark);
			}	
			if (StringUtils.isNotEmpty(userName)){
				appeal.setUserName(userName);
			}
			logger.info("修改申诉:" + appeal.toString());

			int count = appealService.updateAppeal(appeal);

			entity.setResultData(count);


		} catch (AppException e) {
			setEntityException(entity,e.getSqlExpEnum().getCode(),e.getSqlExpEnum().getMessage());
		} catch (Exception e) {
			setEntityException(entity,SERVICE_EXPETION_CODE,SERVICE_EXPETION_STR);
			writeResult(entity, req, resp);
		}
		// 输出结果
		writeResult(entity, req, resp);
	}


	/**
	 * 用户任务申诉查询
	 * @param req
	 * @param resp
	 */
	@RequestMapping("/query")
	public void query(HttpServletRequest req, HttpServletResponse resp){
		ResultEntity entity = new ResultEntity();
		logger.info("统计用户任务申诉查询接口请求:"
				+ req.getQueryString());

		entity.setCode(SUCCESS_CODE);
		entity.setDesc(SUCCESS);
		try {
			Appeal appeal = new Appeal();

			String type = req.getParameter("type");

			String userName = req.getParameter("userName");
		
			String submitTime = req.getParameter("submitTime");

			String beginTime = req.getParameter("beginTime");
			String endTime = req.getParameter("endTime");
			if (StringUtils.isNotEmpty(userName)){
				appeal.setUserName(userName);
			}	
			if (StringUtils.isNotEmpty(submitTime)){
				appeal.setSubmitTime(submitTime);
			}	
			if (StringUtils.isNotEmpty(beginTime)){
				appeal.setBeginTime(beginTime);
			}	
			if (StringUtils.isNotEmpty(endTime)){
				appeal.setEndTime(endTime);
			}	
			if (StringUtils.isNotEmpty(type)){
				appeal.setType(type);
			}	
			logger.info("查询申诉:" + appeal.toString());

			int pageIndex = Integer.parseInt(req.getParameter("pageIndex") == null ? "1" : req.getParameter("pageIndex"));
			int pageSize = Integer.parseInt(req.getParameter("pageSize") == null ? "20" : req.getParameter("pageSize"));

			int count = appealService.queryAppealsCount(appeal);
			List<Appeal> appealList = appealService.queryAppeals(appeal, pageIndex, pageSize);

			entity.setResultData(appealList);
			entity.setTotalCount(String.valueOf(count));

		} catch (AppException e) {
			setEntityException(entity,e.getSqlExpEnum().getCode(),e.getSqlExpEnum().getMessage());
		} catch (Exception e) {
			setEntityException(entity,SERVICE_EXPETION_CODE,SERVICE_EXPETION_STR);
			writeResult(entity, req, resp);
		}
		// 输出结果
		writeResult(entity, req, resp);
	}
	
	/**
	 * 用户任务申诉数量查询
	 * @param req
	 * @param resp
	 */
	@RequestMapping("/count")
	public void count(HttpServletRequest req, HttpServletResponse resp){
		ResultEntity entity = new ResultEntity();
		logger.info("统计用户任务申诉数量接口请求:"
				+ req.getQueryString());

		entity.setCode(SUCCESS_CODE);
		entity.setDesc(SUCCESS);
		try {
			
			Appeal appeal = new Appeal();

			String type = req.getParameter("type");

			String userName = req.getParameter("userName");
			
			String status = req.getParameter("status");
			String submitTime = req.getParameter("submitTime");

			if (StringUtils.isNotEmpty(userName)){
				appeal.setUserName(userName);
			}	
			if (StringUtils.isNotEmpty(submitTime)){
				appeal.setSubmitTime(submitTime);
			}	
			if (StringUtils.isNotEmpty(type)){
				appeal.setType(type);
			}	
			if (StringUtils.isNotEmpty(status)){
				appeal.setStatus(Integer.parseInt(status));
			}	
			logger.info("查询数量:" + appeal.toString());

			int count = appealService.queryAppealsCount(appeal);
			
			entity.setResultData(count);

		} catch (AppException e) {
			setEntityException(entity,e.getSqlExpEnum().getCode(),e.getSqlExpEnum().getMessage());
		} catch (Exception e) {
			setEntityException(entity,SERVICE_EXPETION_CODE,SERVICE_EXPETION_STR);
			writeResult(entity, req, resp);
		}
		// 输出结果
		writeResult(entity, req, resp);
	}
	

	/**
	 * 任务申诉单条查询
	 * @param req
	 * @param resp
	 */
	@RequestMapping("/queryone")
	public void queryone(HttpServletRequest req, HttpServletResponse resp){
		ResultEntity entity = new ResultEntity();
		logger.info("统计任务申诉单条查询接口请求:"
				+ req.getQueryString());

		entity.setCode(SUCCESS_CODE);
		entity.setDesc(SUCCESS);
		try {
			
			Appeal appeal = new Appeal();

			String id = req.getParameter("id");
			
			String userName = req.getParameter("userName");
			
			if (id != null && id.matches("\\d*")) {
				appeal.setId(new Long(id));
			} else {
				throw new AppException(400, "缺少申诉id");
			}
			
			if (StringUtils.isNotEmpty(userName)){
				appeal.setUserName(userName);
			}	
//			else{
//				throw new AppException(400, "缺少用户名");
//			}
			
			logger.info("查询单条申诉:" + appeal.toString());

			Appeal appealObject = appealService.queryAppeal(appeal.getId(),userName);

			entity.setResultData(appealObject);

		} catch (AppException e) {
			setEntityException(entity,e.getSqlExpEnum().getCode(),e.getSqlExpEnum().getMessage());
		} catch (Exception e) {
			setEntityException(entity,SERVICE_EXPETION_CODE,SERVICE_EXPETION_STR);
			writeResult(entity, req, resp);
		}
		// 输出结果
		writeResult(entity, req, resp);
	}
	

	/**
	 * 无效任务详情查看
	 * @param req
	 * @param resp
	 */
	@RequestMapping("/task")
	public void task(HttpServletRequest req, HttpServletResponse resp){
		ResultEntity entity = new ResultEntity();
		logger.info("统计无效任务详情查看接口请求:"
				+ req.getQueryString());

		entity.setCode(SUCCESS_CODE);
		entity.setDesc(SUCCESS);
		try {
			
			Appeal appeal = new Appeal();

			String baseId = req.getParameter("baseId");
			
			String userName = req.getParameter("userName");
		
			if (StringUtils.isNotEmpty(baseId)){
				appeal.setBaseId(Long.parseLong(baseId));
			}	
			else{
				throw new AppException(400, "缺少baseId");
			}	
			
			if (StringUtils.isNotEmpty(userName)){
				appeal.setUserName(userName);
			}	
//			else{
//				throw new AppException(400, "缺少用户名");
//			}
			
			logger.info("申诉任务详情:" + appeal.toString());

			Map<String, String> map = appealService.queryAppealTask(appeal.getBaseId(),userName);
			
			entity.setResultData(map);

		} catch (AppException e) {
			setEntityException(entity,e.getSqlExpEnum().getCode(),e.getSqlExpEnum().getMessage());
		} catch (Exception e) {
			setEntityException(entity,SERVICE_EXPETION_CODE,SERVICE_EXPETION_STR);
			writeResult(entity, req, resp);
		}
		// 输出结果
		writeResult(entity, req, resp);
	}
	
	
	/**
	 * 设置EntityException
	 * @param entity
	 * @param code
	 * @param desc
	 */
	private void setEntityException(ResultEntity entity,String code,String desc){
		entity.setCode(code);
		entity.setDesc(desc);
	}
	


	@Override
	protected ServletInfoBean vaildJsonData(String content, ServletInfoBean obj) {
		// TODO Auto-generated method stub
		return null;
	}

}

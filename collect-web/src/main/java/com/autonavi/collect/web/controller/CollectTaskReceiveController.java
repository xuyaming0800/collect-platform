package com.autonavi.collect.web.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.autonavi.collect.bean.CollectBasePackage;
import com.autonavi.collect.bean.CollectTaskBase;
import com.autonavi.collect.entity.CollectTaskReceiveReturnEntity;
import com.autonavi.collect.exception.BusinessException;
import com.autonavi.collect.exception.BusinessExceptionEnum;
import com.autonavi.collect.exception.BusinessRunException;
import com.autonavi.collect.service.PassiveTaskService;
import com.autonavi.collect.service.TaskCollectUtilService;
import com.autonavi.collect.web.bean.CheckDataInfoBean;
import com.autonavi.collect.web.bean.ResultDesc;
import com.autonavi.collect.web.bean.ResultEntity;
import com.autonavi.collect.web.bean.TaskReceiveReqEntity;
import com.autonavi.collect.web.bean.TaskReceiveRespEntity;

@Controller
public class CollectTaskReceiveController extends BaseController<TaskReceiveReqEntity> {
	public CollectTaskReceiveController() throws Exception {
		super();
		// TODO Auto-generated constructor stub
	}

	private Logger logger = LogManager.getLogger(this.getClass());
	@Autowired
	private TaskCollectUtilService taskCollectUtilService;
	@Autowired
	private PassiveTaskService passiveTaskService;
	
	@RequestMapping("/receiveTask")
	public void receiveTask(HttpServletRequest request,
			HttpServletResponse response) {
		ResultEntity entity = new ResultEntity();
		
		TaskReceiveReqEntity receive=new TaskReceiveReqEntity();
		try {
			int check = this.checkCryptorVersion(request, response, receive, logger,
					null);
			if (check > 0) {
				if (check == 3) {
					this.writeErrorResult(entity, "401", "系统内部异常", response,
							request);
					return;
				}
				if (check == 1) {
					this.writeErrorResult(entity, "201", "参数无法解析", response,
							request);
					return;
				}
				if (check == 2) {
					this.writeErrorResult(entity, "201", "参数无法解析", response, request);
					return;
				}
			} else if (check == -2) {
				this.writeErrorResult(entity, "201", "参数无法解析", response, request);
				return;
			} else {
				//准备提交
				CollectBasePackage pack=new CollectBasePackage();
				pack.setId(receive.getBasePackageId());
				pack.setPassivePackageId(receive.getTaskPackageId());
				pack.setAllotUserId(receive.getUserId());
				pack.setOwnerId(receive.getOwnerId());
				//提交
				CollectTaskReceiveReturnEntity _entity=passiveTaskService.taskPackageReceive(pack);
				//组织查询结果
				List<CollectTaskBase> list=_entity.getCollectTaskBaseList();
				List<TaskReceiveRespEntity> result=new ArrayList<TaskReceiveRespEntity>();
				for(CollectTaskBase base:list){
					TaskReceiveRespEntity resp=new TaskReceiveRespEntity();
					resp.setBaseId(base.getId().toString());
					resp.setDataName(base.getDataName());
					resp.setTaskId(base.getPassiveId().toString());
					resp.setEndTime(DateFormatUtils.format(new Date(base.getAllotEndTime()*1000), "yyyy-MM-dd HH:mm:ss"));
					resp.setPackageName(_entity.getPackageName());
					resp.setBasePackageId(base.getTaskPackageId().toString());
					result.add(resp);
				}
				ResultDesc desc=new ResultDesc();
				desc.setCode("0");
				desc.setMsg(BaseController.SUCCESS);
				entity.setStatus(desc);
				entity.setResult(result);
			}
		} catch (BusinessRunException e) {
			this.writeErrorResult(entity, e.getSqlExpEnum().getCode(), e
					.getSqlExpEnum().getMessage(), response, request);
			return;
		} catch (BusinessException e) {
			this.writeErrorResult(entity, e.getSqlExpEnum().getCode(), e
					.getSqlExpEnum().getMessage(), response, request);
			return;
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			this.writeErrorResult(entity,
					BusinessExceptionEnum.TASK_RECEIVE_ERROR.getCode(),
					BusinessExceptionEnum.TASK_RECEIVE_ERROR.getMessage(), response,
					request);
		}
		
		this.writeResult(entity, request, response);
	}
	
	protected  CheckDataInfoBean vaildJsonData(String content, TaskReceiveReqEntity receive) {
		CheckDataInfoBean bean=new CheckDataInfoBean();
		try {
			JSONObject obj = JSONObject.fromObject(content);
			logger.info("获取的入参为" + obj.toString());
			// 防止参数重复错误
			String regex = "\\[\".*\",\".*\"\\]";
			Pattern pattren = Pattern.compile(regex);
			Matcher mat = pattren.matcher(obj.toString());
			if (mat.find()) {
				bean.setErrorMessage("JSON格式解析错误");
				return bean;
			}
			if (obj.get("userName") != null
					&& !obj.get("userName").toString().equals("")
					&& !obj.get("userName").toString().equalsIgnoreCase("null")) {
				String user=obj.get("userName").toString();
				receive.setUserId(taskCollectUtilService.getUserIdCache(user));
			} else {
				bean.setErrorMessage("userName参数为空");
				return bean;
			}
			if (obj.get("basePackageId") != null && !obj.get("basePackageId").toString().equals("")
					&& !obj.get("basePackageId").toString().equalsIgnoreCase("null")) {
				receive.setBasePackageId(new Long(obj.get("basePackageId").toString()));
			} 
			if (obj.get("taskPackageId") != null && !obj.get("taskPackageId").toString().equals("")
					&& !obj.get("taskPackageId").toString().equalsIgnoreCase("null")) {
				receive.setTaskPackageId(new Long(obj.get("taskPackageId").toString()));
			} else {
				bean.setErrorMessage("taskPackageId参数为空");
				return bean;
			}
			//业主-品类
			if (obj.get("ownerId") != null && !obj.get("ownerId").toString().equals("")
					&& !obj.get("ownerId").toString().equalsIgnoreCase("null")) {
				receive.setOwnerId(Long.valueOf(obj.get("ownerId").toString()));
			}else{
				logger.warn("暂时兼容广告拍拍被动任务! 设置为2");
				receive.setOwnerId(2L);
			}
			
			
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			// TODO Auto-generated catch block
			bean.setErrorMessage("JSON数据格式错误");
		}
		return bean;
	}

}

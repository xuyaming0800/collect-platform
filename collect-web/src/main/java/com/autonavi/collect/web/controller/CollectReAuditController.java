package com.autonavi.collect.web.controller;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.autonavi.collect.entity.CollectTaskReAuditEntity;
import com.autonavi.collect.exception.BusinessExceptionEnum;
import com.autonavi.collect.exception.BusinessRunException;
import com.autonavi.collect.service.CollectTaskReAuditService;
import com.autonavi.collect.service.TaskCollectUtilService;
import com.autonavi.collect.web.bean.CheckDataInfoBean;
import com.autonavi.collect.web.bean.MyTaskQueryEntity;
import com.autonavi.collect.web.bean.ResultDesc;
import com.autonavi.collect.web.bean.ResultEntity;

@Controller
public class CollectReAuditController extends BaseController<MyTaskQueryEntity> {
	private Logger logger = LogManager.getLogger(this.getClass());
	@Autowired
	private TaskCollectUtilService taskCollectUtilService;
	@Autowired
	private CollectTaskReAuditService collectTaskReAuditService;
	public CollectReAuditController() throws Exception {
		super();
	}
	
	@RequestMapping("/reAuditApply")
	public void reAuditApply(HttpServletRequest request, HttpServletResponse response) {
		ResultEntity entity = new ResultEntity();
		MyTaskQueryEntity query=new MyTaskQueryEntity();
		logger.info("用户申诉自己的任务");
		try {
			int check = this.checkCryptorVersion(request, response, query, logger,
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
				CollectTaskReAuditEntity base=new CollectTaskReAuditEntity();
				base.setUserId(query.getUserId());
				base.setUserName(query.getUserName());
				base.setBasePackageId(query.getBasePackageId());
				base.setOwnerId(query.getOwnerId());
				//申诉任务
				collectTaskReAuditService.reAuditTaskPackage(base);
				query.setUserId(null);
				query.setQueryType(null);
				query.setIsPassive(null);
				query.setBasePackageIdList(null);
				ResultDesc desc=new ResultDesc();
				desc.setCode("0");
				desc.setMsg(BaseController.SUCCESS);
				entity.setStatus(desc);
				entity.setResult(query);
				
			}
		} catch (BusinessRunException e) {
			this.writeErrorResult(entity, e.getSqlExpEnum().getCode(), e
					.getSqlExpEnum().getMessage(), response, request);
			return;
		}  catch (Exception e) {
			logger.error(e.getMessage(),e);
			this.writeErrorResult(entity,
					BusinessExceptionEnum.TASK_REAUDIT_FAIL.getCode(),
					BusinessExceptionEnum.TASK_REAUDIT_FAIL.getMessage(), response,
					request);
		}
		
		this.writeResult(entity, request, response);
		
		
	}
	protected  CheckDataInfoBean vaildJsonData(String content, MyTaskQueryEntity query) {
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
				query.setUserId(taskCollectUtilService.getUserIdCache(user));
				query.setUserName(user);
			} else {
				bean.setErrorMessage("userName参数为空");
				return bean;
			}
			if (obj.get("basePackageId") != null
					&& !obj.get("basePackageId").toString().equals("")
					&& !obj.get("basePackageId").toString().equalsIgnoreCase("null")) {
				query.setBasePackageId(new Long(obj.get("basePackageId").toString()));
			} else {
				bean.setErrorMessage("basePackageId参数为空");
				return bean;
			}
			if (obj.get("ownerId") != null && !obj.get("ownerId").toString().equals("")
					&& !obj.get("ownerId").toString().equalsIgnoreCase("null")) {
				query.setOwnerId(Long.valueOf(obj.get("ownerId").toString()));
			}else{
				logger.warn("暂时兼容广告拍拍主动任务! 设置为1");
				query.setOwnerId(1L);
			}
			
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			// TODO Auto-generated catch block
			bean.setErrorMessage("JSON数据格式错误");
		}
		return bean;
	}

}

package com.autonavi.collect.web.controller;

import java.util.List;
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

import autonavi.online.framework.util.json.JsonBinder;

import com.autonavi.collect.entity.TaskClazzMenuEntity;
import com.autonavi.collect.exception.BusinessException;
import com.autonavi.collect.exception.BusinessExceptionEnum;
import com.autonavi.collect.exception.BusinessRunException;
import com.autonavi.collect.service.CollectTaskClazzService;
import com.autonavi.collect.web.bean.CheckDataInfoBean;
import com.autonavi.collect.web.bean.ResultDesc;
import com.autonavi.collect.web.bean.ResultEntity;
@Controller
public class CollectTaskClassController extends BaseController<TaskClazzMenuEntity> {
    public CollectTaskClassController() throws Exception {
		super();
		// TODO Auto-generated constructor stub
	}
	private Logger logger = LogManager.getLogger(this.getClass());
	
	@Autowired
	private CollectTaskClazzService collectTaskClazzService;
	@RequestMapping("/queryCollectClass")
	public void queryCollectClass(HttpServletRequest request,
			HttpServletResponse response) {
		logger.info("获取主动任务类型");
		ResultEntity entity = new ResultEntity();
		TaskClazzMenuEntity clazz=new TaskClazzMenuEntity();
		try {
			int check = this.checkCryptorVersion(request, response, clazz, logger,
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
				String tree=collectTaskClazzService.getCollectTaskInitiativeClazzTree(clazz.getOwnerId().toString());
				ResultDesc desc=new ResultDesc();
				desc.setCode("0");
				desc.setMsg(BaseController.SUCCESS);
				entity.setStatus(desc);
				JsonBinder binder=JsonBinder.buildNormalBinder(false);
				entity.setResult(binder.fromJson(tree, List.class, binder.getCollectionType(List.class, TaskClazzMenuEntity.class)));
			}
			
		} catch (BusinessRunException e) {
			this.writeErrorResult(entity, e.getSqlExpEnum().getCode(), e.getSqlExpEnum().getMessage(), response, request);
			return;
		} catch (BusinessException e) {
			this.writeErrorResult(entity, e.getSqlExpEnum().getCode(), e.getSqlExpEnum().getMessage(), response, request);
			return;
		} catch (Exception e) {
			this.writeErrorResult(entity,
					BusinessExceptionEnum.QUERY_TASK_CLAZZ_ERROR.getCode(),
					BusinessExceptionEnum.QUERY_TASK_CLAZZ_ERROR.getMessage(), response,
					request);
			return;
		}
		this.writeResult(entity, request, response);
	}
	protected  CheckDataInfoBean vaildJsonData(String content, TaskClazzMenuEntity clazz) {
		CheckDataInfoBean bean=new CheckDataInfoBean();
		try {
			JSONObject obj = JSONObject.fromObject(content);
			logger.info("获取的入参为" + obj.toString());
			String regex = "\\[\".*\",\".*\"\\]";
			Pattern pattren = Pattern.compile(regex);
			Matcher mat = pattren.matcher(obj.toString());
			if (mat.find()) {
				bean.setErrorMessage("JSON格式解析错误");
				return bean;
			}
			//业主-品类
			if (obj.get("ownerId") != null && !obj.get("ownerId").toString().equals("")
					&& !obj.get("ownerId").toString().equalsIgnoreCase("null")) {
				clazz.setOwnerId(Long.valueOf(obj.get("ownerId").toString()));
			}else{
				logger.warn("暂时兼容广告拍拍主动任务! 设置为1");
				clazz.setOwnerId(1L);
			}
			
		}catch(Exception e){
			logger.error(e.getMessage(),e);
			// TODO Auto-generated catch block
			bean.setErrorMessage("JSON数据格式错误");
		}
		return bean;
	}

}

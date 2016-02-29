package com.autonavi.collect.web.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import cn.dataup.collect.tools.service.CollectShapeService;

import com.autonavi.collect.constant.CommonConstant;
import com.autonavi.collect.exception.BusinessException;
import com.autonavi.collect.exception.BusinessRunException;
import com.autonavi.collect.web.bean.CheckDataInfoBean;
import com.autonavi.collect.web.bean.ResultDesc;
import com.autonavi.collect.web.bean.ResultEntity;
import com.autonavi.collect.web.bean.ShapeInfoEntity;
@Controller
public class CollectShapeController extends BaseController<ShapeInfoEntity> {

	public CollectShapeController() throws Exception {
		super();
		// TODO Auto-generated constructor stub
	}

	private Logger logger = LogManager.getLogger(this.getClass());

	@Autowired
	private CollectShapeService collectShapeService;
	
	@RequestMapping("/checkLocation")
	public void checkLocation(HttpServletRequest request,
			HttpServletResponse response){
		logger.info("检测坐标是否位于地址围栏");
		ResultEntity entity = new ResultEntity();
		ShapeInfoEntity be=new ShapeInfoEntity();
		try{
			int check = this.checkCryptorVersion(request, response, be, logger,
					null);
			if(check>0){
				if(check==3){
					this.writeErrorResult(entity, "401", "系统内部异常", response, request);
					return;
				}
				if(check==1){
					this.writeErrorResult(entity, "201", "无法解析全部参数", response, request);
					return;
				}
				if(check==2){
					this.writeErrorResult(entity, "405", "任务非法", response, request);
					return;
				}
				return;
			}else if(check==-2){
				logger.info("用户疑似作弊IP=["+this.getIpAddr(request)+"]");
				this.writeErrorResult(entity, "201", "无法解析全部参数", response, request);
				return;
			}else{
				Boolean flag=collectShapeService.checkLocationsVaild(be.getX(), be.getY(), be.getTaskClazzId(), CommonConstant.GPS_SYSTEM.BAIDU.getCode());
				ShapeInfoEntity resultEntity=new ShapeInfoEntity();
				resultEntity.setIsValid(flag);
				ResultDesc desc=new ResultDesc();
				desc.setCode("0");
				desc.setMsg(BaseController.SUCCESS);
				entity.setStatus(desc);
				entity.setResult(resultEntity);
			}
		} catch (BusinessRunException e) {
			this.writeErrorResult(entity, e.getSqlExpEnum().getCode(), e.getSqlExpEnum().getMessage(), response, request);
			return;
		} catch (BusinessException e) {
			this.writeErrorResult(entity, e.getSqlExpEnum().getCode(), e.getSqlExpEnum().getMessage(), response, request);
			return;
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			this.writeErrorResult(entity, "-1", e.getMessage(), response, request);
			return;
		}
		this.writeResult(entity, request, response);
	}
	protected CheckDataInfoBean vaildJsonData(String content, ShapeInfoEntity entity) {
		CheckDataInfoBean obj=new CheckDataInfoBean();
		try {
			JSONObject json = JSONObject.fromObject(content);
			logger.info("获取的入参为"+json.toString());
			if(json.get("userName")!=null&&!json.get("userName").toString().equals("")&&!json.get("userName").toString().equalsIgnoreCase("null")){
//				entity.setUserId(taskCollectUtilService.getUserIdCache(json.get("userName").toString()));
			}else{
				obj.setErrorMessage("userName");
				return obj;
			}
			if(json.get("collectClassId")!=null&&!json.get("collectClassId").toString().equals("")&&!json.get("collectClassId").toString().equalsIgnoreCase("null")){
				entity.setTaskClazzId(Long.valueOf(json.get("collectClassId").toString()));
			}else{
				obj.setErrorMessage("collectClassId");
				return obj;
			}
			if(json.get("x")!=null&&!json.get("x").toString().equals("")&&!json.get("x").toString().equalsIgnoreCase("null")){
				entity.setX(Double.valueOf(json.get("x").toString()));
			}else{
				obj.setErrorMessage("x");
				return obj;
			}
			if(json.get("y")!=null&&!json.get("y").toString().equals("")&&!json.get("y").toString().equalsIgnoreCase("null")){
				entity.setY(Double.valueOf(json.get("y").toString()));
			}else{
				obj.setErrorMessage("y");
				return obj;
			}
            if(json.get("ownerId")!=null&&!json.get("ownerId").toString().equals("")&&!json.get("ownerId").toString().equalsIgnoreCase("null")){
            	entity.setOwnerId(Long.valueOf(json.get("ownerId").toString()));
			}else{
				obj.setErrorMessage("ownerId");
				return obj;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			if(e instanceof BusinessException){
				throw new BusinessRunException((BusinessException)e);
			}
			obj.setErrorMessage("JSON数据格式错误");
		}
		return obj;
	}

}

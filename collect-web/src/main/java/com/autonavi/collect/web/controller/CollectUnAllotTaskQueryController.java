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

import com.autonavi.collect.constant.CommonConstant.TASK_STATUS;
import com.autonavi.collect.entity.SearchPassiveTaskEntity;
import com.autonavi.collect.entity.SearchPassiveTaskFacetResult;
import com.autonavi.collect.entity.SearchPassiveTaskResultEntity;
import com.autonavi.collect.entity.SearchTaskQueryEntity;
import com.autonavi.collect.exception.BusinessExceptionEnum;
import com.autonavi.collect.exception.BusinessRunException;
import com.autonavi.collect.service.TaskCollectUtilService;
import com.autonavi.collect.service.TaskSearchService;
import com.autonavi.collect.web.bean.CheckDataInfoBean;
import com.autonavi.collect.web.bean.ResultDesc;
import com.autonavi.collect.web.bean.ResultEntity;
import com.autonavi.collect.web.bean.UnAllotTaskQueryEntity;
import com.autonavi.collect.web.bean.UnAllotTaskResultEntity;
import com.autonavi.collect.web.constant.WebConstant;
import com.autonavi.collect.web.constant.WebConstant.ALLOT_STATUS;
@Controller
public class CollectUnAllotTaskQueryController extends BaseController<UnAllotTaskQueryEntity> {
	public CollectUnAllotTaskQueryController() throws Exception {
		super();
		// TODO Auto-generated constructor stub
	}
	private Logger logger = LogManager.getLogger(this.getClass());
	private static final int MAX_SIZE=20;
	private static final int MAX_RADIS=3000;
	@Autowired
	private TaskCollectUtilService taskCollectUtilService;
	@Autowired
	private TaskSearchService taskSearchService;
	
	/**
	 * 获取区域下任务数量接口
	 * 
	 * @param req
	 * @param resp
	 */
	@RequestMapping("/queryTaskCountByDistrict")
	public void queryTaskCountByDistrict(HttpServletRequest request,
			HttpServletResponse response) {
		ResultEntity entity = new ResultEntity();
		
		UnAllotTaskQueryEntity query=new UnAllotTaskQueryEntity();
		query.setQueryType(WebConstant.UNALLOT_QUERY_TYPE.District_Count.getCode());
		logger.info("用户周边任务总量");
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
				//获取adcode
				if (!this.valieateXY(query.getX().toString(), query.getY().toString(),logger)) {
					throw new BusinessRunException(BusinessExceptionEnum.COORDINATE_ERROR);
				}
				if (!"0".equals(query.getIsGPS())) {
					String[] offsetList = taskCollectUtilService.offsetXY(query.getX().toString(), query.getY().toString());
					if (offsetList.length == 2) {
						query.setX(new Double(offsetList[0]));
						query.setY(new Double(offsetList[1]));
					}
				}

				String adCode = taskCollectUtilService.fetchAdcode(query.getX(), query.getY());
				if(adCode==null||adCode.length()!=6){
					throw new BusinessRunException(BusinessExceptionEnum.COORDINATE_ERROR);
				}
				adCode=adCode.substring(0,4);
				//准备提交
				SearchTaskQueryEntity _entity=new SearchTaskQueryEntity();
				_entity.setAdcode(adCode);
				_entity.setUserId(query.getUserId());
				_entity.setOwnerId(query.getOwnerId());
				_entity.setCollectCLazzId(query.getCollectClassId());
//				_entity.setPage(query.getPage());
//				_entity.setNumber(query.getSize());
				//提交查询
				List<SearchPassiveTaskFacetResult> queryResult=null;
				if(query.getAllotStatus().equals(ALLOT_STATUS.UNALLOT.getCode())){
					_entity.setTaskStatus(TASK_STATUS.UNALLOT.getCode());
					queryResult=taskSearchService.districtFacetNoUserTask(_entity);
				}else{
					_entity.setTaskStatus(TASK_STATUS.ALLOT.getCode());
					queryResult=taskSearchService.districtFacetTask(_entity);
				}
				List<UnAllotTaskResultEntity> resultList=new ArrayList<UnAllotTaskResultEntity>();
				//组织查询结果
				for(SearchPassiveTaskFacetResult _r:queryResult){
					UnAllotTaskResultEntity result=new UnAllotTaskResultEntity();
					result.setAdcode(_r.getAdcode());
					result.setAdCount(_r.getCount().toString());
					result.setAdName(taskCollectUtilService.fetchAdcodeToName(_r.getAdcode()));
					resultList.add(result);
				}
				ResultDesc desc=new ResultDesc();
				desc.setCode("0");
				desc.setMsg(BaseController.SUCCESS);
				entity.setStatus(desc);
				entity.setResult(resultList);
				//entity.setTotalCount(queryResult.getCount().toString());
				
			}
		} catch (BusinessRunException e) {
			this.writeErrorResult(entity, e.getSqlExpEnum().getCode(), e
					.getSqlExpEnum().getMessage(), response, request);
			return;
//		} catch (BusinessException e) {
//			this.writeErrorResult(entity, e.getSqlExpEnum().getCode(), e
//					.getSqlExpEnum().getMessage(), response, request);
//			return;
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			this.writeErrorResult(entity,
					BusinessExceptionEnum.TASK_QUERY_ERROR.getCode(),
					BusinessExceptionEnum.TASK_QUERY_ERROR.getMessage(), response,
					request);
		}
		
		this.writeResult(entity, request, response);
		
		
	}
	/**
	 * 获取区域下任务接口
	 * 
	 * @param req
	 * @param resp
	 */
	@RequestMapping("/queryTaskByDistrict")
	public void queryTaskByDistrict(HttpServletRequest request,
			HttpServletResponse response) {
		ResultEntity entity = new ResultEntity();
		
		UnAllotTaskQueryEntity query=new UnAllotTaskQueryEntity();
		query.setQueryType(WebConstant.UNALLOT_QUERY_TYPE.District.getCode());
		logger.info("用户周边任务-区域");
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
				SearchTaskQueryEntity _entity=new SearchTaskQueryEntity();
				_entity.setAdcode(query.getAdcode().toString());
				_entity.setUserId(query.getUserId());
				_entity.setPage(query.getPage());
				_entity.setNumber(query.getSize());
				_entity.setOwnerId(query.getOwnerId());
				_entity.setCollectCLazzId(query.getCollectClassId());
				//提交查询
				SearchPassiveTaskResultEntity queryResult=null;
				if(query.getAllotStatus().equals(ALLOT_STATUS.UNALLOT.getCode())){
					_entity.setTaskStatus(TASK_STATUS.UNALLOT.getCode());
					queryResult=taskSearchService.districtNoUserTask(_entity);
				}else{
					_entity.setTaskStatus(TASK_STATUS.ALLOT.getCode());
					queryResult=taskSearchService.districtTask(_entity);
				}
				List<UnAllotTaskResultEntity> resultList=new ArrayList<UnAllotTaskResultEntity>();
				//组织查询结果
				for(SearchPassiveTaskEntity task:queryResult.getCollectPassiveTaskEntity()){
					UnAllotTaskResultEntity result=new UnAllotTaskResultEntity();
					if(query.getAllotStatus().equals(ALLOT_STATUS.UNALLOT.getCode())){
						result.setTaskPackageId(task.getCollectBasePackage().getId().toString());
						result.setBasePackageId("0");
					}else{
						result.setBasePackageId(task.getCollectBasePackage().getId().toString());
						result.setTaskPackageId(String.valueOf(task.getCollectBasePackage().getPassivePackageId()));
					}
					
					result.setPackageName(task.getCollectBasePackage().getTaskPackageName());
					result.setPackageDesc(task.getCollectBasePackage().getTaskPackageDesc());
					result.setPay(task.getCollectBasePackage().getTaskPackagePay().toString());
					result.setTaskCount(task.getCollectBasePackage().getTaskPackageCount().toString());
					result.setPackageType(task.getCollectBasePackage().getTaskPackageType().toString());
					if(task.getCollectBasePackage().getAllotEndTime()!=null){
						result.setEndTime(DateFormatUtils.format(new Date(task.getCollectBasePackage().getAllotEndTime()*1000), "yyyy-MM-dd HH:mm:ss"));
					}
					result.setX(task.getCollectOriginalCoordinate().getOriginalX().toString());
					result.setY(task.getCollectOriginalCoordinate().getOriginalY().toString());
					result.setImgUrls(this.getOrginImgUrls(task.getCollectOriginalCoordinate().getTaskSampleImg()));
					result.setPackageCate(task.getCollectBasePackage().getTaskPackageCate());
					if(task.getCollectBasePackage().getTaskClazzId()!=null)
						result.setCollectClassId(task.getCollectBasePackage().getTaskClazzId().toString());
					resultList.add(result);
				}
				ResultDesc desc=new ResultDesc();
				desc.setCode("0");
				desc.setMsg(BaseController.SUCCESS);
				entity.setStatus(desc);
				entity.setResult(resultList);
				entity.setTotalCount(queryResult.getCount().toString());
				
			}
		} catch (BusinessRunException e) {
			this.writeErrorResult(entity, e.getSqlExpEnum().getCode(), e
					.getSqlExpEnum().getMessage(), response, request);
			return;
//		} catch (BusinessException e) {
//			this.writeErrorResult(entity, e.getSqlExpEnum().getCode(), e
//					.getSqlExpEnum().getMessage(), response, request);
//			return;
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			this.writeErrorResult(entity,
					BusinessExceptionEnum.TASK_QUERY_ERROR.getCode(),
					BusinessExceptionEnum.TASK_QUERY_ERROR.getMessage(), response,
					request);
		}
		
		this.writeResult(entity, request, response);
		
		
	}
	
	/**
	 * 获取附近任务
	 * 
	 * @param req
	 * @param resp
	 */
	@RequestMapping("/queryTaskByRd")
	public void queryTaskByRd(HttpServletRequest request,
			HttpServletResponse response) {
		ResultEntity entity = new ResultEntity();
		
		UnAllotTaskQueryEntity query=new UnAllotTaskQueryEntity();
		query.setQueryType(WebConstant.UNALLOT_QUERY_TYPE.Arround.getCode());
		logger.info("用户周边任务-坐标");
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
				//获取adcode
				if (!this.valieateXY(query.getX().toString(), query.getY().toString(),logger)) {
					throw new BusinessRunException(BusinessExceptionEnum.COORDINATE_ERROR);
				}
				if (!"0".equals(query.getIsGPS())) {
					String[] offsetList = taskCollectUtilService.offsetXY(query.getX().toString(), query.getY().toString());
					if (offsetList.length == 2) {
						query.setX(new Double(offsetList[0]));
						query.setY(new Double(offsetList[1]));
					}
				}
				//准备提交
				SearchTaskQueryEntity _entity=new SearchTaskQueryEntity();
				//坐标偏
				_entity.setX(query.getX());
				_entity.setY(query.getY());
				_entity.setUserId(query.getUserId());
				_entity.setPage(query.getPage());
				_entity.setNumber(query.getSize());
				_entity.setRange(query.getRadis());
				_entity.setOwnerId(query.getOwnerId());
				_entity.setCollectCLazzId(query.getCollectClassId());
				//提交查询
				SearchPassiveTaskResultEntity queryResult=null;
				if(query.getAllotStatus().equals(ALLOT_STATUS.UNALLOT.getCode())){
					_entity.setTaskStatus(TASK_STATUS.UNALLOT.getCode());
					queryResult=taskSearchService.aroundNoUserTask(_entity);
				}else{
					_entity.setTaskStatus(TASK_STATUS.ALLOT.getCode());
					queryResult=taskSearchService.aroundTask(_entity);
				}
				List<UnAllotTaskResultEntity> resultList=new ArrayList<UnAllotTaskResultEntity>();
				//组织查询结果
				for(SearchPassiveTaskEntity task:queryResult.getCollectPassiveTaskEntity()){
					UnAllotTaskResultEntity result=new UnAllotTaskResultEntity();
					if(query.getAllotStatus().equals(ALLOT_STATUS.UNALLOT.getCode())){
						result.setTaskPackageId(task.getCollectBasePackage().getId().toString());
						result.setBasePackageId("0");
					}else{
						result.setBasePackageId(task.getCollectBasePackage().getId().toString());
						result.setTaskPackageId(task.getCollectBasePackage().getPassivePackageId().toString());
					}
					result.setPackageName(task.getCollectBasePackage().getTaskPackageName());
					result.setPackageDesc(task.getCollectBasePackage().getTaskPackageDesc());
					result.setPay(task.getCollectBasePackage().getTaskPackagePay().toString());
					result.setTaskCount(task.getCollectBasePackage().getTaskPackageCount().toString());
					result.setPackageType(task.getCollectBasePackage().getTaskPackageType().toString());
					if(task.getCollectBasePackage().getAllotEndTime()!=null){
						result.setEndTime(DateFormatUtils.format(new Date(task.getCollectBasePackage().getAllotEndTime()*1000), "yyyy-MM-dd HH:mm:ss"));
					}
					result.setX(task.getCollectOriginalCoordinate().getOriginalX().toString());
					result.setY(task.getCollectOriginalCoordinate().getOriginalY().toString());
					result.setImgUrls(this.getOrginImgUrls(task.getCollectOriginalCoordinate().getTaskSampleImg()));
					result.setDistance(task.getDistance().toString());
					result.setPackageCate(task.getCollectBasePackage().getTaskPackageCate());
					if(task.getCollectBasePackage().getTaskClazzId()!=null)
					result.setCollectClassId(task.getCollectBasePackage().getTaskClazzId().toString());
					resultList.add(result);
				}
				ResultDesc desc=new ResultDesc();
				desc.setCode("0");
				desc.setMsg(BaseController.SUCCESS);
				entity.setStatus(desc);
				entity.setResult(resultList);
				entity.setTotalCount(queryResult.getCount().toString());
				
			}
		} catch (BusinessRunException e) {
			this.writeErrorResult(entity, e.getSqlExpEnum().getCode(), e
					.getSqlExpEnum().getMessage(), response, request);
			return;
//		} catch (BusinessException e) {
//			this.writeErrorResult(entity, e.getSqlExpEnum().getCode(), e
//					.getSqlExpEnum().getMessage(), response, request);
//			return;
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			this.writeErrorResult(entity,
					BusinessExceptionEnum.TASK_QUERY_ERROR.getCode(),
					BusinessExceptionEnum.TASK_QUERY_ERROR.getMessage(), response,
					request);
		}
		
		this.writeResult(entity, request, response);
		
		
	}
	
	@RequestMapping("/queryAllTaskByRd")
	public void queryAllTaskByRd(HttpServletRequest request,
			HttpServletResponse response) {
		ResultEntity entity = new ResultEntity();
		
		UnAllotTaskQueryEntity query=new UnAllotTaskQueryEntity();
		query.setQueryType(WebConstant.UNALLOT_QUERY_TYPE.Arround_All.getCode());
		logger.info("用户周边任务-坐标-显示所有状态");
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
				//获取adcode
				if (!this.valieateXY(query.getX().toString(), query.getY().toString(),logger)) {
					throw new BusinessRunException(BusinessExceptionEnum.COORDINATE_ERROR);
				}
				if (!"0".equals(query.getIsGPS())) {
					String[] offsetList = taskCollectUtilService.offsetXY(query.getX().toString(), query.getY().toString());
					if (offsetList.length == 2) {
						query.setX(new Double(offsetList[0]));
						query.setY(new Double(offsetList[1]));
					}
				}
				//准备提交
				SearchTaskQueryEntity _entity=new SearchTaskQueryEntity();
				//坐标偏
				_entity.setX(query.getX());
				_entity.setY(query.getY());
				_entity.setUserId(query.getUserId());
				_entity.setPage(query.getPage());
				_entity.setNumber(query.getSize());
				_entity.setRange(query.getRadis());
				_entity.setOwnerId(query.getOwnerId());
				_entity.setCollectCLazzId(query.getCollectClassId());
				//提交查询
				SearchPassiveTaskResultEntity queryResult=null;
				if(query.getAllotStatus().equals(ALLOT_STATUS.UNALLOT.getCode())){
					_entity.setTaskStatus(TASK_STATUS.UNALLOT.getCode());
					queryResult=taskSearchService.aroundNoUserTask(_entity);
				}else{
					_entity.setTaskStatus(TASK_STATUS.ALLOT.getCode());
					queryResult=taskSearchService.aroundTask(_entity);
				}
				List<UnAllotTaskResultEntity> resultList=new ArrayList<UnAllotTaskResultEntity>();
				//组织查询结果
				for(SearchPassiveTaskEntity task:queryResult.getCollectPassiveTaskEntity()){
					UnAllotTaskResultEntity result=new UnAllotTaskResultEntity();
					if(query.getAllotStatus().equals(ALLOT_STATUS.UNALLOT.getCode())){
						result.setTaskPackageId(task.getCollectBasePackage().getId().toString());
						result.setBasePackageId("0");
					}else{
						result.setBasePackageId(task.getCollectBasePackage().getId().toString());
						result.setTaskPackageId(task.getCollectBasePackage().getPassivePackageId().toString());
					}
					result.setPackageName(task.getCollectBasePackage().getTaskPackageName());
					result.setPackageDesc(task.getCollectBasePackage().getTaskPackageDesc());
					result.setPay(task.getCollectBasePackage().getTaskPackagePay().toString());
					result.setTaskCount(task.getCollectBasePackage().getTaskPackageCount().toString());
					result.setPackageType(task.getCollectBasePackage().getTaskPackageType().toString());
					if(task.getCollectBasePackage().getAllotEndTime()!=null){
						result.setEndTime(DateFormatUtils.format(new Date(task.getCollectBasePackage().getAllotEndTime()*1000), "yyyy-MM-dd HH:mm:ss"));
					}
					result.setX(task.getCollectOriginalCoordinate().getOriginalX().toString());
					result.setY(task.getCollectOriginalCoordinate().getOriginalY().toString());
					result.setImgUrls(this.getOrginImgUrls(task.getCollectOriginalCoordinate().getTaskSampleImg()));
					result.setDistance(task.getDistance().toString());
					result.setPackageCate(task.getCollectBasePackage().getTaskPackageCate());
					if(task.getCollectBasePackage().getTaskPackageStatus().equals(TASK_STATUS.UNALLOT.getCode())
							||task.getCollectBasePackage().getTaskPackageStatus().equals(TASK_STATUS.ALLOT.getCode())){
						result.setStatus("0");
					}
					else{
						result.setStatus("1");
						
					}
					resultList.add(result);
					
				}
				ResultDesc desc=new ResultDesc();
				desc.setCode("0");
				desc.setMsg(BaseController.SUCCESS);
				entity.setStatus(desc);
				entity.setResult(resultList);
				entity.setTotalCount(queryResult.getCount().toString());
				
			}
		} catch (BusinessRunException e) {
			this.writeErrorResult(entity, e.getSqlExpEnum().getCode(), e
					.getSqlExpEnum().getMessage(), response, request);
			return;
//		} catch (BusinessException e) {
//			this.writeErrorResult(entity, e.getSqlExpEnum().getCode(), e
//					.getSqlExpEnum().getMessage(), response, request);
//			return;
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			this.writeErrorResult(entity,
					BusinessExceptionEnum.TASK_QUERY_ERROR.getCode(),
					BusinessExceptionEnum.TASK_QUERY_ERROR.getMessage(), response,
					request);
		}
		
		this.writeResult(entity, request, response);
		
		
	}
	protected  CheckDataInfoBean vaildJsonData(String content, UnAllotTaskQueryEntity query) {
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
			//默认查询为查询未分配的自由任务
			query.setAllotStatus(WebConstant.ALLOT_STATUS.UNALLOT.getCode());
			if (obj.get("ownerId") != null
					&& !obj.get("ownerId").toString().equals("")
					&& !obj.get("ownerId").toString().equalsIgnoreCase("null")) {
				query.setOwnerId(Long.valueOf(obj.get("ownerId").toString()));
			}else{
				logger.warn("兼容被动任务设置ownerId为2L");
				query.setOwnerId(2L);
			}
			query.setAllotStatus(WebConstant.ALLOT_STATUS.UNALLOT.getCode());
			if (obj.get("collectClassId") != null
					&& !obj.get("collectClassId").toString().equals("")
					&& !obj.get("collectClassId").toString().equalsIgnoreCase("null")) {
				query.setCollectClassId(Long.valueOf(obj.get("collectClassId").toString()));
			}
			if (obj.get("allotStatus") != null
					&& !obj.get("allotStatus").toString().equals("")
					&& !obj.get("allotStatus").toString().equalsIgnoreCase("null")) {
				query.setAllotStatus(obj.get("allotStatus").toString());
				if(!query.getAllotStatus().equals(ALLOT_STATUS.UNALLOT.getCode())
						&&!query.getAllotStatus().equals(ALLOT_STATUS.ALLOT.getCode())){
					query.setAllotStatus(WebConstant.ALLOT_STATUS.UNALLOT.getCode());
				}
			}
			if(query.getQueryType().equals(WebConstant.UNALLOT_QUERY_TYPE.District.getCode())){//省市区检索入参校验
				if (obj.get("userName") != null
						&& !obj.get("userName").toString().equals("")
						&& !obj.get("userName").toString().equalsIgnoreCase("null")) {
					String user=obj.get("userName").toString();
					query.setUserId(taskCollectUtilService.getUserIdCache(user));
				} else {
					bean.setErrorMessage("userName参数为空");
					return bean;
				}
				if (obj.get("adcode") != null && !obj.get("adcode").toString().equals("")
						&& !obj.get("adcode").toString().equalsIgnoreCase("null")) {
					query.setAdcode(new Integer(obj.get("adcode").toString()));
					
				} else {
					bean.setErrorMessage("adcode参数为空");
					return bean;
				}
				if (obj.get("page") != null && !obj.get("page").toString().equals("")
						&& !obj.get("page").toString().equalsIgnoreCase("null")) {
					query.setPage(new Integer(obj.get("page").toString()));
				} else {
					bean.setErrorMessage("page参数为空");
					return bean;
				}
				query.setSize(CollectUnAllotTaskQueryController.MAX_SIZE);
				if (obj.get("size") != null && !obj.get("size").toString().equals("")
						&& !obj.get("size").toString().equalsIgnoreCase("null")) {
					query.setSize(new Integer(obj.get("size").toString()));
				}
				if(query.getSize()>CollectUnAllotTaskQueryController.MAX_SIZE){
					throw new BusinessRunException(BusinessExceptionEnum.TASK_QUERY_MAX_SIZE_OVER);
				}
			}else if(query.getQueryType().equals(WebConstant.UNALLOT_QUERY_TYPE.Arround.getCode())){//省市区检索入参校验
				if (obj.get("userName") != null
						&& !obj.get("userName").toString().equals("")
						&& !obj.get("userName").toString().equalsIgnoreCase("null")) {
					String user=obj.get("userName").toString();
					query.setUserId(taskCollectUtilService.getUserIdCache(user));
				} else {
					bean.setErrorMessage("userName参数为空");
					return bean;
				}
				if (obj.get("x") != null && !obj.get("x").toString().equals("")
						&& !obj.get("x").toString().equalsIgnoreCase("null")) {
					query.setX(new Double(obj.get("x").toString()));
					
				} else {
					bean.setErrorMessage("x参数为空");
					return bean;
				}
				if (obj.get("y") != null && !obj.get("y").toString().equals("")
						&& !obj.get("y").toString().equalsIgnoreCase("null")) {
					query.setY(new Double(obj.get("y").toString()));
					
				} else {
					bean.setErrorMessage("y参数为空");
					return bean;
				}
				if (obj.get("page") != null && !obj.get("page").toString().equals("")
						&& !obj.get("page").toString().equalsIgnoreCase("null")) {
					query.setPage(new Integer(obj.get("page").toString()));
				} else {
					bean.setErrorMessage("page参数为空");
					return bean;
				}
				query.setSize(CollectUnAllotTaskQueryController.MAX_SIZE);
				if (obj.get("size") != null && !obj.get("size").toString().equals("")
						&& !obj.get("size").toString().equalsIgnoreCase("null")) {
					query.setSize(new Integer(obj.get("size").toString()));
				}
				if(query.getSize()>CollectUnAllotTaskQueryController.MAX_SIZE){
					throw new BusinessRunException(BusinessExceptionEnum.TASK_QUERY_MAX_SIZE_OVER);
				}
				query.setRadis(CollectUnAllotTaskQueryController.MAX_RADIS);
				if (obj.get("radis") != null && !obj.get("radis").toString().equals("")
						&& !obj.get("radis").toString().equalsIgnoreCase("null")) {
					query.setRadis(new Integer(obj.get("radis").toString()));
				}
				if(query.getRadis()>CollectUnAllotTaskQueryController.MAX_RADIS){
					throw new BusinessRunException(BusinessExceptionEnum.TASK_QUERY_MAX_RADIS_OVER);
				}
				if (obj.get("isGPS") != null && !obj.get("isGPS").toString().equals("")
						&& !obj.get("isGPS").toString().equalsIgnoreCase("null")) {
					query.setIsGPS(obj.get("isGPS").toString());
				} 
			}else if(query.getQueryType().equals(WebConstant.UNALLOT_QUERY_TYPE.Arround_All.getCode())){//省市区检索入参校验
				if (obj.get("userName") != null
						&& !obj.get("userName").toString().equals("")
						&& !obj.get("userName").toString().equalsIgnoreCase("null")) {
					String user=obj.get("userName").toString();
					query.setUserId(taskCollectUtilService.getUserIdCache(user));
				} else {
					bean.setErrorMessage("userName参数为空");
					return bean;
				}
				if (obj.get("x") != null && !obj.get("x").toString().equals("")
						&& !obj.get("x").toString().equalsIgnoreCase("null")) {
					query.setX(new Double(obj.get("x").toString()));
					
				} else {
					bean.setErrorMessage("x参数为空");
					return bean;
				}
				if (obj.get("y") != null && !obj.get("y").toString().equals("")
						&& !obj.get("y").toString().equalsIgnoreCase("null")) {
					query.setY(new Double(obj.get("y").toString()));
					
				} else {
					bean.setErrorMessage("y参数为空");
					return bean;
				}
				if (obj.get("page") != null && !obj.get("page").toString().equals("")
						&& !obj.get("page").toString().equalsIgnoreCase("null")) {
					query.setPage(new Integer(obj.get("page").toString()));
				} else {
					query.setPage(1);
				}
//				query.setSize(CollectUnAllotTaskQueryController.MAX_SIZE);
				if (obj.get("size") != null && !obj.get("size").toString().equals("")
						&& !obj.get("size").toString().equalsIgnoreCase("null")) {
					query.setSize(new Integer(obj.get("size").toString()));
				}else{
					query.setSize(Integer.valueOf(this.getBizProperty(WebConstant.COLLECT_MAX_PASSIVE_TASK_SIZE)));
				}
//				if(query.getSize()>CollectUnAllotTaskQueryController.MAX_SIZE){
//					throw new BusinessRunException(BusinessExceptionEnum.TASK_QUERY_MAX_SIZE_OVER);
//				}
				query.setRadis(CollectUnAllotTaskQueryController.MAX_RADIS);
				if (obj.get("radis") != null && !obj.get("radis").toString().equals("")
						&& !obj.get("radis").toString().equalsIgnoreCase("null")) {
					query.setRadis(new Integer(obj.get("radis").toString()));
				}
				if(query.getRadis()>CollectUnAllotTaskQueryController.MAX_RADIS){
					throw new BusinessRunException(BusinessExceptionEnum.TASK_QUERY_MAX_RADIS_OVER);
				}
				if (obj.get("isGPS") != null && !obj.get("isGPS").toString().equals("")
						&& !obj.get("isGPS").toString().equalsIgnoreCase("null")) {
					query.setIsGPS(obj.get("isGPS").toString());
				} 
			}else if(query.getQueryType().equals(WebConstant.UNALLOT_QUERY_TYPE.District_Count.getCode())){//省市区检索入参校验
				if (obj.get("userName") != null
						&& !obj.get("userName").toString().equals("")
						&& !obj.get("userName").toString().equalsIgnoreCase("null")) {
					String user=obj.get("userName").toString();
					query.setUserId(taskCollectUtilService.getUserIdCache(user));
				} else {
					bean.setErrorMessage("userName参数为空");
					return bean;
				}
				if (obj.get("x") != null && !obj.get("x").toString().equals("")
						&& !obj.get("x").toString().equalsIgnoreCase("null")) {
					query.setX(new Double(obj.get("x").toString()));
				} else {
					bean.setErrorMessage("x参数为空");
					return bean;
				}
				if (obj.get("y") != null && !obj.get("y").toString().equals("")
						&& !obj.get("y").toString().equalsIgnoreCase("null")) {
					query.setY(new Double(obj.get("y").toString()));
				} else {
					bean.setErrorMessage("y参数为空");
					return bean;
				}
				if (obj.get("isGPS") != null && !obj.get("isGPS").toString().equals("")
						&& !obj.get("isGPS").toString().equalsIgnoreCase("null")) {
					query.setIsGPS(obj.get("isGPS").toString());
				} 
				
			}
			
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			// TODO Auto-generated catch block
			bean.setErrorMessage("JSON数据格式错误");
		}
		return bean;
	}
	

}

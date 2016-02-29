package com.autonavi.collect.web.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.autonavi.collect.bean.CollectBasePackage;
import com.autonavi.collect.bean.CollectTaskBase;
import com.autonavi.collect.bean.CollectTaskClazz;
import com.autonavi.collect.bean.CollectTaskImg;
import com.autonavi.collect.constant.CommonConstant;
import com.autonavi.collect.constant.CommonConstant.TASK_STATUS;
import com.autonavi.collect.constant.CommonConstant.TASK_VERIFY_STATUS;
import com.autonavi.collect.entity.CollectBasePackageEntity;
import com.autonavi.collect.entity.CollectTaskBaseEntity;
import com.autonavi.collect.entity.CollectUserTaskQueryEntity;
import com.autonavi.collect.exception.BusinessException;
import com.autonavi.collect.exception.BusinessExceptionEnum;
import com.autonavi.collect.exception.BusinessRunException;
import com.autonavi.collect.service.InitiativeTaskService;
import com.autonavi.collect.service.MyTaskService;
import com.autonavi.collect.service.PassiveTaskService;
import com.autonavi.collect.service.TaskCollectUtilService;
import com.autonavi.collect.web.bean.CheckDataInfoBean;
import com.autonavi.collect.web.bean.DeletePackageResult;
import com.autonavi.collect.web.bean.MyTaskQueryEntity;
import com.autonavi.collect.web.bean.MyTaskResultEntity;
import com.autonavi.collect.web.bean.ResultDesc;
import com.autonavi.collect.web.bean.ResultEntity;
import com.autonavi.collect.web.constant.WebConstant;

@Controller
public class CollectUserTaskQueryController extends BaseController<MyTaskQueryEntity> {
	public CollectUserTaskQueryController() throws Exception {
		super();
		// TODO Auto-generated constructor stub
	}

	private Logger logger = LogManager.getLogger(this.getClass());
	private static final int MAX_SIZE=20;
	@Autowired
	private TaskCollectUtilService taskCollectUtilService;
	
	@Autowired
	private MyTaskService myTaskService;
	@Autowired
	private InitiativeTaskService initiativeTaskService;
	
	@Autowired
	private PassiveTaskService passiveTaskService;
	
	/**
	 * 获取代理商任务包接口(全部任务，待审核任务，生效任务，未生效任务)
	 * 
	 * @param req
	 * @param resp
	 */
	@RequestMapping("/queryTaskPackage")
	public void queryTaskPackage(HttpServletRequest request, HttpServletResponse response) {
		ResultEntity entity = new ResultEntity();
		MyTaskQueryEntity query=new MyTaskQueryEntity();
		query.setQueryType(WebConstant.MYTASK_QUERY_TYPE.PACKAGES.getCode());
		logger.info("用户查询自己的任务");
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
				CollectBasePackage base=new CollectBasePackage();
				base.setCollectUserId(query.getUserId());
				base.setTaskPackageStatus(query.getTaskStatus());
				base.setOwnerId(query.getOwnerId());
//				CollectTaskBase base=new CollectTaskBase();
//				base.setCollectUserId(query.getUserId());
//				base.setTaskStatus(query.getTaskStatus());
				CollectUserTaskQueryEntity _entity=new CollectUserTaskQueryEntity();
//				_entity.setCollectBasePackage(base);
				_entity.setCollectBasePackage(base);
				_entity.setStart((query.getPage()-1)*query.getSize());
				_entity.setLimit(query.getSize());
				_entity.setIsPassive(query.getIsPassive());
				if(_entity.getCollectBasePackage().getTaskPackageStatus().equals(TASK_STATUS.FINISH.getCode())){
					_entity.getCollectBasePackage().setTaskPackageVerifyStatus(TASK_VERIFY_STATUS.FAIL.getCode());
				}
				if(_entity.getCollectBasePackage().getTaskPackageStatus().equals(TASK_STATUS.FIRST_AUDIT.getCode())){
					_entity.getCollectBasePackage().setTaskPackageVerifyStatus(TASK_VERIFY_STATUS.FIRST_FAIL.getCode());
				}
				//查询结果
				CollectUserTaskQueryEntity result= myTaskService.getTaskPackage(_entity);
				//获取转换
				if(result.getCollectBasePackageList()==null)result.setCollectBasePackageList(new ArrayList<CollectBasePackageEntity>());
				List<Long> clazzId=new ArrayList<Long>();
				for(CollectBasePackageEntity _pentity:result.getCollectBasePackageList()){
					if(_pentity.getCollectClassId()!=null){
						clazzId.add(Long.valueOf(_pentity.getCollectClassId()));
					}
				}
				if(clazzId.size()>0){
					Map<Long,String> clazzCache=myTaskService.getTaskClazzCacheJson(clazzId);
					//转换任务品类 
					result=myTaskService.transferClazzNamefromCache(result,clazzCache);
				}
				//结果封装
				
				List<MyTaskResultEntity> list=new ArrayList<MyTaskResultEntity>();
				for(CollectBasePackageEntity _b:result.getCollectBasePackageList()){
					MyTaskResultEntity re=new MyTaskResultEntity();
					re.setBasePackageId(_b.getId().toString());
					re.setTaskPackageId(_b.getPassivePackageId()==null?"":_b.getPassivePackageId().toString());
					re.setDataName(_b.getTaskPackageName());
					re.setDesc(_b.getTaskPackageDesc());
					re.setTaskStatus(_b.getTaskPackageStatus().toString());
					re.setTaskCount(_b.getTaskPackageCount().toString());
					re.setBasePackageCate(_b.getTaskPackageCate());
					re.setBasePackageName(_b.getTaskPackageName());
					if(_b.getCollectClassPayType()!=null){
						re.setCollectClassPayType(_b.getCollectClassPayType().toString());
					}
					if(_b.getTaskPackageType()!=null){
						re.setBasePackageType(_b.getTaskPackageType().toString());
					}
					if(_b.getCollectClassId()!=null){
						re.setCollectClassId(_b.getCollectClassId().toString());
					}
					re.setImgUrls(this.getOrginImgUrls(_b.getImgUrl()));
					if(_b.getX()!=null)
					re.setX(_b.getX().toString());
					if(_b.getY()!=null)
					re.setY(_b.getY().toString());
					re.setCollectClassName(_b.getCollectClassName());
					if(_b.getTaskPackagePay()!=null)
					re.setPay(_b.getTaskPackagePay().toString());
					//已领取 显示有效时间
					if(_b.getTaskPackageStatus().equals(CommonConstant.TASK_STATUS.RECEIVE.getCode())){
						re.setEndTime(DateFormatUtils.format(new Date(_b.getAllotEndTime()*1000), "yyyy-MM-dd HH:mm:ss"));
					}
					//保存时候显示有效时间
					else if(_b.getTaskPackageStatus().equals(CommonConstant.TASK_STATUS.SAVE.getCode())){
						re.setSubmitTime(DateFormatUtils.format(new Date(_b.getUpdateTime()*1000), "yyyy-MM-dd HH:mm:ss"));
						re.setEndTime(DateFormatUtils.format(new Date(_b.getAllotEndTime()*1000), "yyyy-MM-dd HH:mm:ss"));
					}else{
						if(_b.getTaskPackageStatus().equals(CommonConstant.TASK_STATUS.FINISH.getCode())){
							if(_b.getTaskPackageVerifyStatus().equals(CommonConstant.TASK_VERIFY_STATUS.PASS.getCode())){
								re.setAuditStatus("0");
							}else{
								re.setAuditStatus("1");
							}
							
						}
						if(_b.getTaskPackageStatus().equals(CommonConstant.TASK_STATUS.FIRST_AUDIT.getCode())
								||_b.getTaskPackageStatus().equals(CommonConstant.TASK_STATUS.RE_AUDIT.getCode())){
							if(_b.getTaskPackageVerifyStatus().equals(CommonConstant.TASK_VERIFY_STATUS.FIRST_PASS.getCode())){
								re.setAuditStatus("0");
							}else{
								re.setAuditStatus("1");
							}
							
						}
						if(_b.getSubmitTime()==null)_b.setSubmitTime(_b.getUpdateTime());
						re.setSubmitTime(DateFormatUtils.format(new Date(_b.getSubmitTime()*1000), "yyyy-MM-dd HH:mm:ss"));
					}
					list.add(re);
				}
				ResultDesc desc=new ResultDesc();
				desc.setCode("0");
				desc.setMsg(BaseController.SUCCESS);
				entity.setStatus(desc);
				entity.setResult(list);
				entity.setTotalCount(result.getTotal().toString());
				if(result.getVerifyFailCount()!=null){
					entity.setVerifyFailCount(result.getVerifyFailCount().toString());
				}
				if(result.getVerifyPassCount()!=null){
					entity.setVerifyPassCount(result.getVerifyPassCount().toString());
				}
				
				
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
					BusinessExceptionEnum.TASK_QUERY_ERROR.getCode(),
					BusinessExceptionEnum.TASK_QUERY_ERROR.getMessage(), response,
					request);
		}
		
		this.writeResult(entity, request, response);
		
		
	}
	@RequestMapping("/queryCollectClassCount")
	public void queryCollectClassCount(HttpServletRequest request, HttpServletResponse response) {
		ResultEntity entity = new ResultEntity();
		MyTaskQueryEntity query=new MyTaskQueryEntity();
		query.setQueryType(WebConstant.MYTASK_QUERY_TYPE.COUNT.getCode());
		logger.info("用户查询分类步骤计数");
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
				CollectTaskBase base=new CollectTaskBase();
				base.setCollectUserId(query.getUserId());
				base.setId(query.getBaseId());
				base.setOwnerId(query.getOwnerId());
				CollectUserTaskQueryEntity _entity=new CollectUserTaskQueryEntity();
				_entity.setCollectTaskBase(base);
				_entity.setLevel(query.getLevel());
				_entity.setCollectClassId(query.getCollectClassId().toString());
				//查询结果
				CollectUserTaskQueryEntity result= myTaskService.getTaskClazzCount(_entity);
				
				
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
					BusinessExceptionEnum.TASK_QUERY_ERROR.getCode(),
					BusinessExceptionEnum.TASK_QUERY_ERROR.getMessage(), response,
					request);
		}
		
		this.writeResult(entity, request, response);
	}
	@RequestMapping("/queryCollectClassCounts")
	public void queryCollectClassCounts(HttpServletRequest request, HttpServletResponse response) {
		ResultEntity entity = new ResultEntity();
		MyTaskQueryEntity query=new MyTaskQueryEntity();
		query.setQueryType(WebConstant.MYTASK_QUERY_TYPE.COUNTS.getCode());
		logger.info("用户查询总计数");
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
				CollectTaskBase base=new CollectTaskBase();
				base.setCollectUserId(query.getUserId());
				base.setId(query.getBaseId());
				base.setOwnerId(query.getOwnerId());
				CollectUserTaskQueryEntity _entity=new CollectUserTaskQueryEntity();
				_entity.setCollectTaskBase(base);
				_entity.setLevel(query.getLevel());
				//查询结果
				List<CollectUserTaskQueryEntity> result= myTaskService.getTaskClazzCounts(_entity);
				
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
					BusinessExceptionEnum.TASK_QUERY_ERROR.getCode(),
					BusinessExceptionEnum.TASK_QUERY_ERROR.getMessage(), response,
					request);
		}
		
		this.writeResult(entity, request, response);
	}
	@RequestMapping("/queryPackageTasks")
	@SuppressWarnings("unchecked")
	public void queryPackageTasks(HttpServletRequest request, HttpServletResponse response) {
		ResultEntity entity = new ResultEntity();
		MyTaskQueryEntity query=new MyTaskQueryEntity();
		query.setQueryType(WebConstant.MYTASK_QUERY_TYPE.TASKS.getCode());
		logger.info("用户查询自己的任务概要信息");
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
				CollectTaskBase base=new CollectTaskBase();
				base.setCollectUserId(query.getUserId());
				base.setTaskPackageId(query.getBasePackageId());
				base.setTaskStatus(query.getTaskStatus());
				base.setOwnerId(query.getOwnerId());
				CollectBasePackage pack=new CollectBasePackage();
				pack.setId(query.getBasePackageId());
				pack.setCollectUserId(query.getUserId());
				pack.setAllotUserId(query.getUserId());
				pack.setOwnerId(query.getOwnerId());
				CollectUserTaskQueryEntity _entity=new CollectUserTaskQueryEntity();
				_entity.setCollectTaskBase(base);
				_entity.setCollectBasePackage(pack);
				_entity.setStart((query.getPage()-1)*query.getSize());
				_entity.setLimit(query.getSize());
				_entity.setLevel(query.getLevel());
				//查询结果
				CollectUserTaskQueryEntity result= myTaskService.getTasksByPackage(_entity);
				//获取转换
				if(result.getCollectTaskBaseList()==null)result.setCollectTaskBaseList(new ArrayList<CollectTaskBaseEntity>());
				List<Long> clazzId=new ArrayList<Long>();
				for(CollectTaskBaseEntity _base:result.getCollectTaskBaseList()){
					if(_base.getTaskClazzId()!=null){
						clazzId.add(_base.getTaskClazzId());
					}
				}
				if(clazzId.size()>0){
					Map<Long,String> clazzCache=myTaskService.getTaskClazzCacheJson(clazzId);
					//转换任务品类 
					result=myTaskService.transferClazzNamefromCache(result,clazzCache);
				}
				
				//结果封装
				
				List<MyTaskResultEntity> list=new ArrayList<MyTaskResultEntity>();
				Map<Long,CollectTaskClazz> clazzMap=result.getCollectTaskClazzMap();
				for(CollectTaskBaseEntity _b:result.getCollectTaskBaseList()){
					MyTaskResultEntity re=new MyTaskResultEntity();
					if(_b.getExtras()!=null&& _b.getExtras() instanceof List){
						List<Object> _l=(List<Object>)_b.getExtras();
						for(Object _obj:_l){
							if(_obj instanceof Map){
								Map<Object,Object> _map=(Map<Object,Object>)_obj;
								_map.put("collectClassId", _map.get("collectClazzId"));
							}
						}
						
					}
					re.setExtraList(_b.getExtras());
					if(_b.getPassiveId()!=null)
					re.setTaskId(_b.getPassiveId().toString());
					re.setBaseId(_b.getId().toString());
					re.setBasePackageName(result.getCollectBasePackage().getTaskPackageName());
					re.setDataName(_b.getDataName());
					if(re.getDataName()==null){
						re.setDataName(_b.getCollectDataName());
					}
					re.setTaskStatus(_b.getTaskStatus().toString());
					
					if(_b.getTaskClazzId()!=null){
						CollectTaskClazz clazz=clazzMap.get(_b.getTaskClazzId());
						if(clazz!=null){
							re.setCollectClassId(clazz.getId().toString());
							re.setCollectClassCount(clazz.getClazzImgCount()==null?"":clazz.getClazzImgCount().toString());
							re.setCollectClassfarCount(clazz.getClazzFarImgCount()==null?"":clazz.getClazzFarImgCount().toString());
							re.setCollectClassNearCount(clazz.getClazzNearImgCount()==null?"":clazz.getClazzNearImgCount().toString());
							re.setCollectClassName(clazz.getClazzName());
							re.setPay(clazz.getClazzPay()==null?"":clazz.getClazzPay().toString());
							re.setCollectClassPayType(clazz.getClazzPayType()==null?"":clazz.getClazzPayType().toString());
						}
					}
					
					
					//已领取 显示有效时间
					if(_b.getTaskStatus().equals(CommonConstant.TASK_STATUS.RECEIVE.getCode())){
						re.setEndTime(DateFormatUtils.format(new Date(_b.getAllotEndTime()*1000), "yyyy-MM-dd HH:mm:ss"));
					}
					//保存时候显示有效时间
					else if(_b.getTaskStatus().equals(CommonConstant.TASK_STATUS.SAVE.getCode())){
						re.setSubmitTime(DateFormatUtils.format(new Date(_b.getTaskSaveTime()*1000), "yyyy-MM-dd HH:mm:ss"));
						re.setEndTime(DateFormatUtils.format(new Date(_b.getAllotEndTime()*1000), "yyyy-MM-dd HH:mm:ss"));
					}else{
						if(_b.getTaskStatus().equals(CommonConstant.TASK_STATUS.FINISH.getCode())){
							if(_b.getVerifyStatus().equals(CommonConstant.TASK_VERIFY_STATUS.PASS.getCode())){
								re.setAuditStatus("0");
							}else{
								re.setAuditStatus("1");
							}
						}
						re.setSubmitTime(DateFormatUtils.format(new Date(_b.getTaskSubmitTime()*1000), "yyyy-MM-dd HH:mm:ss"));
					}
					list.add(re);
				}
				ResultDesc desc=new ResultDesc();
				desc.setCode("0");
				desc.setMsg(BaseController.SUCCESS);
				entity.setStatus(desc);
				entity.setResult(list);
				entity.setTotalCount(result.getTotal().toString());
				
				
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
					BusinessExceptionEnum.TASK_QUERY_ERROR.getCode(),
					BusinessExceptionEnum.TASK_QUERY_ERROR.getMessage(), response,
					request);
		}
		
		this.writeResult(entity, request, response);
		
		
	}
	@RequestMapping("/deletePackage")
	public void deletePackage(HttpServletRequest request, HttpServletResponse response) {
		ResultEntity entity = new ResultEntity();
		MyTaskQueryEntity query=new MyTaskQueryEntity();
		query.setQueryType(WebConstant.MYTASK_QUERY_TYPE.DELETE.getCode());
		logger.info("用户删除自己的任务");
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
				DeletePackageResult deletePackageResult=new DeletePackageResult();
				//准备提交
				for(Long pkgId:query.getBasePackageIdMap().keySet()){
					CollectBasePackage base=new CollectBasePackage();
					base.setCollectUserId(query.getUserId());
					base.setAllotUserId(query.getUserId());
					base.setId(pkgId);
					base.setOwnerId(query.getOwnerId());
					base.setPassivePackageId(query.getBasePackageIdMap().get(pkgId));
					int count=0;
					try {
						if(base.getPassivePackageId()==null){
							//主动任务删除
							count = initiativeTaskService.taskDelete(base);
						}else{
							count = passiveTaskService.taskDelete(base);
						}
						
					} catch (Exception e) {
						logger.error(e.getMessage(),e);
					}
					if(count==1){
						deletePackageResult.getSuccessId().add(pkgId);
					}else{
						deletePackageResult.getFailedId().add(pkgId);
					}
				}
				
				
				ResultDesc desc=new ResultDesc();
				desc.setCode("0");
				desc.setMsg(BaseController.SUCCESS);
				entity.setStatus(desc);
				entity.setResult(deletePackageResult);
			}
		} catch (BusinessRunException e) {
			this.writeErrorResult(entity, e.getSqlExpEnum().getCode(), e
					.getSqlExpEnum().getMessage(), response, request);
			return;
		}  catch (Exception e) {
			logger.error(e.getMessage(),e);
			this.writeErrorResult(entity,
					BusinessExceptionEnum.TASK_DELETE_ERROR.getCode(),
					BusinessExceptionEnum.TASK_DELETE_ERROR.getMessage(), response,
					request);
		}
		
		this.writeResult(entity, request, response);
		
		
	}
	@RequestMapping("/queryTaskDetails")
	public void queryTaskDetails(HttpServletRequest request, HttpServletResponse response) {
		ResultEntity entity = new ResultEntity();
		MyTaskQueryEntity query=new MyTaskQueryEntity();
		query.setQueryType(WebConstant.MYTASK_QUERY_TYPE.IMGS.getCode());
		logger.info("用户查询自己的任务的采集图片或详细步骤信息");
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
				CollectTaskBase base=new CollectTaskBase();
				base.setCollectUserId(query.getUserId());
				base.setId(query.getBaseId());
				base.setOwnerId(query.getOwnerId());
				base.setAllotUserId(query.getUserId());
				CollectUserTaskQueryEntity _entity=new CollectUserTaskQueryEntity();
				_entity.setCollectTaskBase(base);
				_entity.setLevel(query.getLevel());
				_entity.setBatchId(query.getBatchId());
				
				//查询结果
				CollectUserTaskQueryEntity result= myTaskService.getTaskImage(_entity);
				//结果封装
				
				CollectTaskBase _b=result.getCollectTaskBase();
				MyTaskResultEntity re=new MyTaskResultEntity();
				if(_b.getPassiveId()!=null){
					re.setTaskId(_b.getPassiveId().toString());
				}
				if(result.getCollectTaskBaseEntity()!=null&&result.getCollectTaskBaseEntity().getExtras()!=null){
					re.setExtra(result.getCollectTaskBaseEntity().getExtras());
				}
				re.setBaseId(_b.getId().toString());
				re.setBasePackageName(result.getCollectBasePackage().getTaskPackageName());
				re.setDataName(_b.getDataName());
				re.setTaskStatus(_b.getTaskStatus().toString());
				//已领取 显示有效时间
				if(_b.getTaskStatus().equals(CommonConstant.TASK_STATUS.RECEIVE.getCode())){
					re.setEndTime(DateFormatUtils.format(new Date(_b.getAllotEndTime()*1000), "yyyy-MM-dd HH:mm:ss"));
				}
				//保存时候显示有效时间
				else if(_b.getTaskStatus().equals(CommonConstant.TASK_STATUS.SAVE.getCode())){
					re.setSubmitTime(DateFormatUtils.format(new Date(_b.getTaskSaveTime()*1000), "yyyy-MM-dd HH:mm:ss"));
					re.setEndTime(DateFormatUtils.format(new Date(_b.getAllotEndTime()*1000), "yyyy-MM-dd HH:mm:ss"));
				}else{
					if(_b.getTaskStatus().equals(CommonConstant.TASK_STATUS.FINISH.getCode())){
						if(_b.getVerifyStatus().equals(CommonConstant.TASK_VERIFY_STATUS.PASS.getCode())){
							re.setAuditStatus("0");
						}else{
							re.setAuditStatus("1");
						}
					}
					if(_b.getTaskStatus().equals(CommonConstant.TASK_STATUS.SUBMIT.getCode())){
						re.setSubmitTime(DateFormatUtils.format(new Date(_b.getTaskSubmitTime()*1000), "yyyy-MM-dd HH:mm:ss"));
					}
					
				}
				
				List<String> list=new ArrayList<String>();
				if(result.getCollectTaskImgList()!=null){
					for(CollectTaskImg img:result.getCollectTaskImgList()){
						list.add(getCollectImgUrl(img.getImgName()));
					}
				}
				
				re.setImgUrls(list);
				ResultDesc desc=new ResultDesc();
				desc.setCode("0");
				desc.setMsg(BaseController.SUCCESS);
				entity.setStatus(desc);
				entity.setResult(re);
				entity.setTotalCount(result.getTotal()==null?"0":result.getTotal().toString());
				
				
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
					BusinessExceptionEnum.TASK_QUERY_ERROR.getCode(),
					BusinessExceptionEnum.TASK_QUERY_ERROR.getMessage(), response,
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
			if(!query.getQueryType().equals(WebConstant.MYTASK_QUERY_TYPE.DELETE.getCode())){
				String regex = "\\[\".*\",\".*\"\\]";
				Pattern pattren = Pattern.compile(regex);
				Matcher mat = pattren.matcher(obj.toString());
				if (mat.find()) {
					bean.setErrorMessage("JSON格式解析错误");
					return bean;
				}
			}
			
			
			if (obj.get("userName") != null
					&& !obj.get("userName").toString().equals("")
					&& !obj.get("userName").toString().equalsIgnoreCase("null")) {
				String user=obj.get("userName").toString();
				query.setUserId(taskCollectUtilService.getUserIdCache(user));
			} else {
				bean.setErrorMessage("userName参数为空");
				return bean;
			}
			
			if(query.getQueryType().equals(WebConstant.MYTASK_QUERY_TYPE.COUNTS.getCode())){
				if (obj.get("baseId") != null
						&& !obj.get("baseId").toString().equals("")
						&& !obj.get("baseId").toString().equalsIgnoreCase("null")) {
					query.setBaseId(new Long(obj.get("baseId").toString()));
				}else{
					bean.setErrorMessage("baseId参数为空");
					return bean;
				}
				if (obj.get("level") != null
						&& !obj.get("level").toString().equals("")
						&& !obj.get("level").toString().equalsIgnoreCase("null")) {
					query.setLevel(obj.get("level").toString());
				}else{
					query.setLevel("");
				}
				if (obj.get("ownerId") != null && !obj.get("ownerId").toString().equals("")
						&& !obj.get("ownerId").toString().equalsIgnoreCase("null")) {
					query.setOwnerId(Long.valueOf(obj.get("ownerId").toString()));
				}else{
					logger.warn("暂时兼容广告拍拍主动任务! 设置为1");
					query.setOwnerId(1L);
				}
				
				
			}else if(query.getQueryType().equals(WebConstant.MYTASK_QUERY_TYPE.COUNT.getCode())){
				if (obj.get("baseId") != null
						&& !obj.get("baseId").toString().equals("")
						&& !obj.get("baseId").toString().equalsIgnoreCase("null")) {
					query.setBaseId(new Long(obj.get("baseId").toString()));
				}else{
					bean.setErrorMessage("baseId参数为空");
					return bean;
				}
				if (obj.get("collectClassId") != null
						&& !obj.get("collectClassId").toString().equals("")
						&& !obj.get("collectClassId").toString().equalsIgnoreCase("null")) {
					query.setCollectClassId(new Long(obj.get("collectClassId").toString()));
				}else{
					bean.setErrorMessage("collectClassId参数为空");
					return bean;
				}
				if (obj.get("level") != null
						&& !obj.get("level").toString().equals("")
						&& !obj.get("level").toString().equalsIgnoreCase("null")) {
					query.setLevel(obj.get("level").toString());
				}else{
					query.setLevel("");
				}
				if (obj.get("ownerId") != null && !obj.get("ownerId").toString().equals("")
						&& !obj.get("ownerId").toString().equalsIgnoreCase("null")) {
					query.setOwnerId(Long.valueOf(obj.get("ownerId").toString()));
				}else{
					logger.warn("暂时兼容广告拍拍主动任务! 设置为1");
					query.setOwnerId(1L);
				}
				
				
			}else if(query.getQueryType().equals(WebConstant.MYTASK_QUERY_TYPE.DELETE.getCode())){
				if (obj.get("basePackageId") != null
						&& !obj.get("basePackageId").toString().equals("")
						&& !obj.get("basePackageId").toString().equalsIgnoreCase("null")) {
					logger.warn("兼容广告拍拍删除操作");
					JSONArray jsonArray=JSONArray.fromObject(obj.get("basePackageId").toString());
					if(jsonArray.size()>50){
						bean.setErrorMessage("批量删除的记录过多,最多条数50条，实际传入"+jsonArray.size()+"条");
						return bean;
					}
					for(Object _obj:jsonArray){
						query.getBasePackageIdMap().put(Long.valueOf(_obj.toString()),null);
					}
				}
				if (obj.get("ownerId") != null && !obj.get("ownerId").toString().equals("")
						&& !obj.get("ownerId").toString().equalsIgnoreCase("null")) {
					query.setOwnerId(Long.valueOf(obj.get("ownerId").toString()));
				}else{
					//暂时不做业主校验 保证被动任务兼容
					logger.warn("暂时兼容广告拍拍主动任务! 设置为1");
					query.setOwnerId(1L);
				}
				if (obj.get("deleteInfo") != null
						&& !obj.get("deleteInfo").toString().equals("")
						&& !obj.get("deleteInfo").toString().equalsIgnoreCase("null")) {
					JSONArray jsonArray=JSONArray.fromObject(obj.get("deleteInfo"));
					if(jsonArray.size()>50){
						bean.setErrorMessage("批量删除的记录过多,最多条数50条，实际传入"+jsonArray.size()+"条");
						return bean;
					}
					for(Object _obj:jsonArray){
						JSONObject json=JSONObject.fromObject(_obj);
						Object bId=json.get("basePackageId");
						Object tId=json.get("taskPackageId");
						String basePackageId=bId==null?null:bId.toString();
						String taskPackageId=tId==null?null:tId.toString();
						if(basePackageId==null||basePackageId.equals("")){
							bean.setErrorMessage("deleteInfo.basePackageId不能为空");
							return bean;
						}
						if(taskPackageId==null||taskPackageId.equals("")){
							query.getBasePackageIdMap().put(Long.valueOf(basePackageId),null);
						}else{
							query.getBasePackageIdMap().put(Long.valueOf(basePackageId),Long.valueOf(taskPackageId));
						}
						
					}
				}
			}
			else if(query.getQueryType().equals(WebConstant.MYTASK_QUERY_TYPE.IMGS.getCode())){
				if (obj.get("baseId") != null
						&& !obj.get("baseId").toString().equals("")
						&& !obj.get("baseId").toString().equalsIgnoreCase("null")) {
					query.setBaseId(new Long(obj.get("baseId").toString()));
				}else{
					bean.setErrorMessage("baseId参数为空");
					return bean;
				}
				if (obj.get("ownerId") != null && !obj.get("ownerId").toString().equals("")
						&& !obj.get("ownerId").toString().equalsIgnoreCase("null")) {
					query.setOwnerId(Long.valueOf(obj.get("ownerId").toString()));
				}else{
					logger.warn("暂时兼容广告拍拍主动任务! 设置为1");
					query.setOwnerId(1L);
				}
				if (obj.get("level") != null
						&& !obj.get("level").toString().equals("")
						&& !obj.get("level").toString().equalsIgnoreCase("null")) {
					query.setLevel(obj.get("level").toString());
				}
				if (obj.get("batchId") != null
						&& !obj.get("batchId").toString().equals("")
						&& !obj.get("batchId").toString().equalsIgnoreCase("null")) {
					query.setBatchId(Long.valueOf(obj.get("batchId").toString()));
				}
			}else{
				if (obj.get("taskStatus") != null && !obj.get("taskStatus").toString().equals("")
						&& !obj.get("taskStatus").toString().equalsIgnoreCase("null")) {
					query.setTaskStatus(new Integer(obj.get("taskStatus").toString()));
					if(!query.getTaskStatus().equals(CommonConstant.TASK_STATUS.RECEIVE.getCode())
					  &&!query.getTaskStatus().equals(CommonConstant.TASK_STATUS.SAVE.getCode())
					  &&!query.getTaskStatus().equals(CommonConstant.TASK_STATUS.SUBMIT.getCode())
					  &&!query.getTaskStatus().equals(CommonConstant.TASK_STATUS.FINISH.getCode())
					  &&!query.getTaskStatus().equals(CommonConstant.TASK_STATUS.FIRST_AUDIT.getCode())
					  &&!query.getTaskStatus().equals(CommonConstant.TASK_STATUS.RE_AUDIT.getCode())){
						throw new BusinessRunException(BusinessExceptionEnum.TASK_QUERY_STATUS_ERROR);
					}
				} else {
					bean.setErrorMessage("taskStatus参数为空");
					return bean;
				}
				if(query.getQueryType().equals(WebConstant.MYTASK_QUERY_TYPE.TASKS.getCode())){
					if (obj.get("basePackageId") != null
							&& !obj.get("basePackageId").toString().equals("")
							&& !obj.get("basePackageId").toString().equalsIgnoreCase("null")) {
						query.setBasePackageId(new Long(obj.get("basePackageId").toString()));
					} else {
						bean.setErrorMessage("basePackageId参数为空");
						return bean;
					}
					if (obj.get("level") != null
							&& !obj.get("level").toString().equals("")
							&& !obj.get("level").toString().equalsIgnoreCase("null")) {
						query.setLevel(obj.get("level").toString());
					} 
				}
				
				if (obj.get("page") != null && !obj.get("page").toString().equals("")
						&& !obj.get("page").toString().equalsIgnoreCase("null")) {
					query.setPage(new Integer(obj.get("page").toString()));
				} else {
					bean.setErrorMessage("page参数为空");
					return bean;
				}
				query.setSize(CollectUserTaskQueryController.MAX_SIZE);
				if (obj.get("size") != null && !obj.get("size").toString().equals("")
						&& !obj.get("size").toString().equalsIgnoreCase("null")) {
					query.setSize(new Integer(obj.get("size").toString()));
				}
				if(query.getSize()>CollectUserTaskQueryController.MAX_SIZE){
					throw new BusinessRunException(BusinessExceptionEnum.TASK_QUERY_MAX_SIZE_OVER);
				}
				if (obj.get("isPassive") != null) {
					query.setIsPassive(obj.getBoolean("isPassive"));
				}
				if (obj.get("ownerId") != null && !obj.get("ownerId").toString().equals("")
						&& !obj.get("ownerId").toString().equalsIgnoreCase("null")) {
					query.setOwnerId(Long.valueOf(obj.get("ownerId").toString()));
				}else{
					if (query.getIsPassive()!=null&&query.getIsPassive()&&query.getQueryType().equals(WebConstant.MYTASK_QUERY_TYPE.PACKAGES.getCode())) {
						logger.warn("暂时兼容广告拍拍被动任务! 设置为2");
						query.setOwnerId(2L);
					}else{
						logger.warn("暂时兼容广告拍拍主动任务! 设置为1");
						query.setOwnerId(1L);
					}
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

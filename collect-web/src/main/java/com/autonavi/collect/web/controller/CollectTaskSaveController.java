package com.autonavi.collect.web.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

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

import com.autonavi.collect.bean.CollectTaskBase;
import com.autonavi.collect.bean.CollectTaskImg;
import com.autonavi.collect.bean.CollectTaskToken;
import com.autonavi.collect.constant.CommonConstant;
import com.autonavi.collect.constant.CommonConstant.TASK_IMG_STATUS;
import com.autonavi.collect.entity.CollectTaskSaveEntity;
import com.autonavi.collect.entity.TaskExtraInfoEntity;
import com.autonavi.collect.exception.BusinessException;
import com.autonavi.collect.exception.BusinessRunException;
import com.autonavi.collect.service.InitiativeTaskService;
import com.autonavi.collect.service.PassiveTaskService;
import com.autonavi.collect.service.TaskCollectUtilService;
import com.autonavi.collect.service.TaskTokenService;
import com.autonavi.collect.web.bean.CheckDataInfoBean;
import com.autonavi.collect.web.bean.ResultDesc;
import com.autonavi.collect.web.bean.ResultEntity;
import com.autonavi.collect.web.bean.TaskExtraInfoWebEntity;
import com.autonavi.collect.web.bean.TaskLockResultEntity;
import com.autonavi.collect.web.constant.WebConstant;
@Controller
public class CollectTaskSaveController extends BaseController<CollectTaskSaveEntity> {
	public CollectTaskSaveController() throws Exception {
		super();
		// TODO Auto-generated constructor stub
	}

	private Logger logger = LogManager.getLogger(this.getClass());
	
	@Autowired
	private TaskCollectUtilService taskCollectUtilService;
	
	@Autowired
	private TaskTokenService taskTokenService;
	
	@Autowired
	private PassiveTaskService passiveTaskService;
	
	@Autowired
	private InitiativeTaskService initiativeTaskService;
	
	
	@RequestMapping("/saveTask")
	public void saveTask(HttpServletRequest request,
			HttpServletResponse response){
		logger.info("用户数据保存接口请求开始");
		CollectTaskSaveEntity st=new CollectTaskSaveEntity();
		ResultEntity entity = new ResultEntity();
		try {
			int check = this.checkCryptorVersion(request, response, st, logger,
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
				/* 判断是否 主动任务(如果taskId是空，表示是主动任务) */
				boolean activeTask = st.getCollectTaskBase().getPassiveId() == null;
				TaskLockResultEntity resultEntity=new TaskLockResultEntity();
				if(st.getCollectTaskToken()!=null)
				resultEntity.setTokenId(st.getCollectTaskToken().getToken());
				Long userId=st.getCollectTaskBase().getCollectUserId();
				if(activeTask){
					CollectTaskBase _base=initiativeTaskService.taskSave(st);
					resultEntity.setBaseId(_base.getId().toString());
					resultEntity.setSubmitTime(DateFormatUtils.format(new Date(_base.getTaskSaveTime()*1000), "yyyy-MM-dd HH:mm:ss"));
					resultEntity.setEndTime(DateFormatUtils.format(new Date(_base.getAllotEndTime()*1000), "yyyy-MM-dd HH:mm:ss"));
					resultEntity.setCollectDataName(_base.getCollectDataName());
					resultEntity.setBasePackageId(_base.getTaskPackageId().toString());
					resultEntity.setTaskId(_base.getPassiveId()==null?"":_base.getPassiveId().toString());
				}else{
					//被动任务
					CollectTaskBase _base=passiveTaskService.taskSave(st);
					resultEntity.setBaseId(_base.getId().toString());
					resultEntity.setSubmitTime(DateFormatUtils.format(new Date(_base.getTaskSaveTime()*1000), "yyyy-MM-dd HH:mm:ss"));
					resultEntity.setEndTime(DateFormatUtils.format(new Date(_base.getAllotEndTime()*1000), "yyyy-MM-dd HH:mm:ss"));
					resultEntity.setBasePackageId(_base.getTaskPackageId().toString());
					resultEntity.setCollectDataName(_base.getCollectDataName());
					resultEntity.setTaskId(_base.getPassiveId()==null?"":_base.getPassiveId().toString());
				}
				for(TaskExtraInfoEntity _ent:st.getTaskExtraInfoEntityList()){
					taskCollectUtilService.clearPlatFromBatchId(Long.valueOf(_ent.getBatchId()),userId);
					_ent.setValue(null);
					_ent.setMoneyChange(null);
				}
				resultEntity.setExtras(st.getTaskExtraInfoEntityList());
				ResultDesc desc=new ResultDesc();
				desc.setCode("0");
				desc.setMsg(BaseController.SUCCESS);
				entity.setStatus(desc);
				entity.setResult(resultEntity);
				

//			if (st.getId()!=null) {
//				taskHandlerService.modifyTask(st.getBaseId(), st.getAdCode(), st.getDataName(), st.getUserName(),st.getScoreId(),st.getTokenId());
//				entity.setCode("0");
//				entity.setDesc(BaseController.SUCCESS);
//
//			} else {
//				TaskLockResultEntity resultEntity=null;
//				//判断是否是定向 1是定向
//				if(!st.getRecommend().equals("1")){
//					resultEntity = taskHandlerService.saveLockTask(st.getTaskId(), st.getUserName(), st.getDataName(), st.getCoordinateX(), st.getCoordinateY(), st.getAdCode(),st.getDeviceInfo(),st.getScoreId(),st.getTokenId());
//				}else{
//					resultEntity=taskHandlerService.saveLockUserTask(st.getTaskId(), st.getUserName(), st.getDataName(), st.getCoordinateX(), st.getCoordinateY(), st.getAdCode(),st.getDeviceInfo(),st.getScoreId(),st.getTokenId());
//				}
//				
//				
//				entity.setResultData(resultEntity);
//				resultEntity.setAdCode(st.getAdCode());
//				entity.setCode("0");
//				entity.setDesc(BaseController.SUCCESS);
//			}
			//以下操作为打标记 防止重复保存时出现清除TOKEN情况
//		try {
//			if(st.getTokenId()!=null&&!st.getTokenId().equals("")){
//				taskHandlerService.updateAgentTaskTokenForSave(st.getTokenId(),st.getUserName());
//			}
//			
//		} catch (AppException e) {
//			// TODO Auto-generated catch block
//			this.writeErrorResult(entity, e.getSqlExpEnum().getCode(), e.getSqlExpEnum().getMessage(), response, request);
//			if(st.getTokenId()!=null&&!st.getTokenId().equals("")){
////				service.deleteAgentTaskToken(tokenId);
//				taskHandlerService.deleteAgentTaskTokenForSave(st.getTokenId(),st.getUserName());
//			}
//			return;
//		}

			// 输出结果
			
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

	@Override
	protected CheckDataInfoBean vaildJsonData(String content, CollectTaskSaveEntity entity) {
		CheckDataInfoBean obj=new CheckDataInfoBean();
		CollectTaskBase save=new CollectTaskBase();
		List<CollectTaskImg> imgList =new ArrayList<CollectTaskImg>();
		List<TaskExtraInfoEntity> extraList=new ArrayList<TaskExtraInfoEntity>();
		String tokenId="";
		try {
			boolean isDevMode=new Boolean(this.getBizProperty(WebConstant.DEV_MODE).toString());
			JSONObject json = JSONObject.fromObject(content);
			logger.info("获取的入参为"+json.toString());
			
			
//			//防止参数重复错误
//			String regex="\\[\".*\",\".*\"\\]";
//			Pattern pattren=Pattern.compile(regex);
//			Matcher mat = pattren.matcher(json.toString());
//			if(mat.find()){
//				obj.setErrorMessage("JSON格式解析错误");
//				return  obj;
//			}
			//強制校验tokenId
			if(json.get("tokenId")!=null&&!json.get("tokenId").toString().equals("")&&!json.get("tokenId").toString().equalsIgnoreCase("null")){
				tokenId=json.get("tokenId").toString();
			}
//			else{
//				obj.setErrorMessage("tokenId");
//				return obj;
//			}
		
			//定向任务判断
//			String recommend="0";
//			if(json.get("recommend")!=null&&json.get("recommend").toString().equals("1"))recommend=json.get("recommend").toString();
//			save.setRecommend(recommend);
			//用户校验前置判断
			if(json.get("userName")!=null&&!json.get("userName").toString().equals("")&&!json.get("userName").toString().equalsIgnoreCase("null")){
				save.setCollectUserId(taskCollectUtilService.getUserIdCache(json.get("userName").toString()));
			}else{
				obj.setErrorMessage("userName");
				return obj;
			}
			String baseId = null;
			if(json.get("baseId")!=null&&!json.get("baseId").toString().equals("")&&!json.get("baseId").toString().equalsIgnoreCase("null")){
				baseId = json.get("baseId").toString();
			}
			//主被动任务判断
			String taskId=null;
			
//			String adCode = null;
			if(json.get("taskId")!=null&&!json.get("taskId").toString().equals("")&&!json.get("taskId").toString().equalsIgnoreCase("null")){
				taskId = json.get("taskId").toString();
				save.setPassiveId(new Long(taskId));
			}
			//业主-品类
			if (json.get("ownerId") != null && !json.get("ownerId").toString().equals("")
					&& !json.get("ownerId").toString().equalsIgnoreCase("null")) {
				save.setOwnerId(Long.valueOf(json.get("ownerId").toString()));
			}else{
				boolean activeTask = save.getPassiveId() == null;
				if(activeTask){
					logger.warn("暂时兼容广告拍拍主动任务! 设置为主动1");
					save.setOwnerId(1L);
					
				}else{
					logger.warn("暂时兼容广告拍拍被动任务! 设置为被动2");
					save.setOwnerId(2L);
				}
			}
			//判定是否使用Token校验
			Boolean isUseToken=taskTokenService.checkTokenUseStatus(save.getOwnerId());
			if(isUseToken){
				if(tokenId==null){
					obj.setErrorMessage("tokenId");
					return obj;
				}
				CollectTaskToken token=new CollectTaskToken();
				token.setOwnerId(save.getOwnerId());
				//校验Token信息
				Boolean check=taskTokenService.checkTaskToken(save.getCollectUserId(), tokenId,baseId, null,token.getOwnerId(),false);
				if(!check){
					obj.setErrorMessage("token校验失败");
					return obj;
				}
				token.setToken(tokenId);
				token.setUserId(save.getCollectUserId());
				entity.setCollectTaskToken(token);
			}
			
//			save.setCoordinateX(token.getX());
//			save.setCoordinateY(token.getY());
//			if(!token.getUserName().equals(save.getUserName())){
//				save.setErrorMessage("userName");
//				logger.error("token用户名不一致 校验失败");
//				taskHandlerService.deleteAgentTaskTokenForSave(save.getTokenId(),save.getUserName());
//				return save;
//			}
			

			if(json.get("collectClassId")!=null&&!json.get("collectClassId").toString().equals("")&&!json.get("collectClassId").toString().equalsIgnoreCase("null")){
				save.setTaskClazzId(new Long(json.get("collectClassId").toString()));
			}
//			校验上传坐标组
			if(json.get("coods")!=null&&!json.get("coods").toString().equals("")&&!json.get("coods").toString().equalsIgnoreCase("null")){
				JSONArray jsonArray=JSONArray.fromObject(json.get("coods").toString());
				Set<Integer> existIndex=new HashSet<Integer>();
				Long tempBatchId=taskCollectUtilService.getPlatFromUniqueId();
				for(Iterator<?> it=jsonArray.iterator();it.hasNext();){
					JSONObject cood=(JSONObject)it.next();
					CollectTaskImg img=new CollectTaskImg();
					if (baseId != null) {
						img.setBaseId(new Long(baseId));
					}
					if(cood.get("x")!=null&&!cood.get("x").toString().equals("")&&!cood.get("x").toString().equalsIgnoreCase("null")){
						img.setCollectX(new Double(cood.get("x").toString()));
					}else{
						obj.setErrorMessage("x");
						return obj;
					}
                    if(cood.get("y")!=null&&!cood.get("y").toString().equals("")&&!cood.get("y").toString().equalsIgnoreCase("null")){
                    	img.setCollectY(new Double(cood.get("y").toString()));
					}else{
						obj.setErrorMessage("y");
						return obj;
					}
                    if(cood.get("index")!=null&&!cood.get("index").toString().equals("")&&!cood.get("index").toString().equalsIgnoreCase("null")){
						Integer index=new Integer(cood.get("index").toString());
						if(!existIndex.add(index)){
							obj.setErrorMessage("index重复");
							return obj;
						}
						img.setImageIndex(index);
						img.setImageH5Id("old"+index.toString());
						img.setTempBatchId(tempBatchId);
						//默认批次号为1000 兼容旧模式
						img.setImageBatchId(1000L);
						img.setImageStatus(TASK_IMG_STATUS.USE.getCode());
						
					}else{
						obj.setErrorMessage("index");
						return obj;
					}
                    if(img.getCollectX()!=null&&!img.getCollectX().equals("")&&img.getCollectY()!=null&&!img.getCollectY().equals("")){
						String[] offset=this.getOffsetXY(img.getCollectX().toString(), img.getCollectY().toString());
						img.setCollectOffsetX(new Double(offset[0]));
						img.setCollectOffsetY(new Double(offset[1]));
						img.setCollectAdcode(this.fetchAdcode(new Double(offset[0]), new Double(offset[1])));
						double[] xy=taskCollectUtilService.transferCoordinate(CommonConstant.GPS_SYSTEM.BAIDU.getCode(), CommonConstant.GPS_SYSTEM.DEFAULT.getCode(), img.getCollectOffsetX(), img.getCollectOffsetY());
						img.setCollectX(xy[0]);
						img.setCollectY(xy[1]);
					}
                    imgList.add(img);
				}
			}
			//使用额外信息上传坐标信息
			if(json.get("extras")!=null&&!json.get("extras").toString().equals("")&&!json.get("extras").toString().equalsIgnoreCase("null")){
				TaskExtraInfoWebEntity taskExtraInfoWebEntity=new TaskExtraInfoWebEntity();
				if(baseId!=null){
					taskExtraInfoWebEntity=this.getCollectTaskExtrasInfo(json.get("extras").toString(), new Long(baseId),save.getCollectUserId(),taskCollectUtilService,save.getTaskClazzId());
				}else{
					taskExtraInfoWebEntity=this.getCollectTaskExtrasInfo(json.get("extras").toString(),null,save.getCollectUserId(), taskCollectUtilService,save.getTaskClazzId());
				}
				imgList=taskExtraInfoWebEntity.getCollectTaskImgList();
				extraList=taskExtraInfoWebEntity.getTaskExtraInfoEntityList();
				
			}
			if(taskId==null&&save.getTaskClazzId()==null){
				obj.setErrorMessage("collectClassId不能为空");
				return obj;
			}
			if(taskId==null&&imgList.size()==0){
//				obj.setErrorMessage("未上传坐标组");
//				return obj;
			}
			taskId = (taskId == null) ? "" : taskId;
			
			if(json.get("dataName")!=null&&!json.get("dataName").toString().equals("")&&!json.get("dataName").toString().equalsIgnoreCase("null")){
				save.setCollectDataName(json.get("dataName").toString());
			}else{
				obj.setErrorMessage("dataName");
				if(!isDevMode)
				  taskTokenService.updateTaskTokenStatus(save.getCollectUserId(), tokenId, CommonConstant.TOKEN_STATUS_CHEAT);
				return obj;
			}
			
			
			
			
//			if(json.get("adCode")!=null&&!json.get("adCode").toString().equals("")&&!json.get("adCode").toString().equalsIgnoreCase("null")){
//				adCode = json.get("adCode").toString();
//			}
			if (baseId != null) {
				save.setId(new Long(baseId));
//				save.setAdCode(adCode);
				
			}

//			if (!this.valieateXY(save.getCoordinateX(), save.getCoordinateY(),logger)) {
//			save.setErrorMessage("X,Y坐标错误");
//			taskHandlerService.deleteAgentTaskTokenForSave(save.getTokenId(),save.getUserName());
//			return save;
//		}
//		if (adCode != null && !"".equals(adCode) && adCode.length() != 6) {
//			save.setErrorMessage("adCode");
//			taskHandlerService.deleteAgentTaskTokenForSave(save.getTokenId(),save.getUserName());
//			return save;
//		} else if (adCode == null || "".equals(adCode)) {
//			// 没有ADCODE 自动获取
//			adCode = this.getAdCode(save.getCoordinateX(), save.getCoordinateY(), true);
//		}
		//设备信息为空强制不让保存
		if(json.get("deviceInfo")!=null&&!json.get("deviceInfo").toString().equals("")&&!json.get("deviceInfo").toString().equalsIgnoreCase("null")){
			save.setDeviceInfo(json.get("deviceInfo").toString());
		}else{
			obj.setErrorMessage("deviceInfo");
			if(!isDevMode)
				  taskTokenService.updateTaskTokenStatus(save.getCollectUserId(), tokenId, CommonConstant.TOKEN_STATUS_CHEAT);
			return obj;
		}
		
//		save.setAdCode(adCode);
		entity.setCollectTaskBase(save);
		entity.setCollectTaskImgList(imgList);
		entity.setTaskExtraInfoEntityList(extraList);
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

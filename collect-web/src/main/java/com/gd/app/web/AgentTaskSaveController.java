package com.gd.app.web;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.gd.app.entity.AgentTaskToken;
import com.gd.app.entity.ResultEntity;
import com.gd.app.entity.SaveTaskEntity;
import com.gd.app.entity.ServletInfoBean;
import com.gd.app.entity.TaskLockResultEntity;
import com.gd.app.exception.AppException;
import com.gd.app.service.TaskHandlerService;
@Controller
public class AgentTaskSaveController extends BaseController {
	private Logger logger = LogManager.getLogger(this.getClass());
	@Resource
	private TaskHandlerService taskHandlerService;
	@RequestMapping("/saveTask")
	public void saveTask(HttpServletRequest request,
			HttpServletResponse response){
		SaveTaskEntity st=new SaveTaskEntity();
		ResultEntity entity = new ResultEntity();
		int check = this.checkCryptorVersion(request, response, st, logger,
				null);
		if(check>0){
			if(check==3){
				this.writeErrorResult(entity, "401", "系统内部异常", response, request);
				return;
			}
			if(check==1){
				logger.info("用户 "+st.getUserName()+"疑似作弊IP=["+this.getIpAddr(request)+"]");
				this.writeErrorResult(entity, "201", "无法解析全部参数", response, request);
				return;
			}
			if(check==2){
				String user=request.getParameter("userName");
				logger.info("用户 ["+user+"]疑似作弊 IP=["+this.getIpAddr(request)+"]");
				this.writeErrorResult(entity, "405", "任务非法", response, request);
				return;
			}
			return;
		}else if(check==-2){
			logger.info("用户疑似作弊IP=["+this.getIpAddr(request)+"]");
			this.writeErrorResult(entity, "201", "无法解析全部参数", response, request);
			return;
		}else{
			try{
				/* 判断是否 主动任务(如果taskId是空，表示是主动任务) */
				boolean activeTask = st.getTaskId() == null || st.getTaskId().isEmpty();
				if(activeTask){
					st.setScoreId(null);
				}

				if (st.getBaseId()!=null) {
					taskHandlerService.modifyTask(st.getBaseId(), st.getAdCode(), st.getDataName(), st.getUserName(),st.getScoreId(),st.getTokenId());
					entity.setCode("0");
					entity.setDesc(BaseController.SUCCESS);

				} else {
					TaskLockResultEntity resultEntity=null;
					//判断是否是定向 1是定向
					if(!st.getRecommend().equals("1")){
						resultEntity = taskHandlerService.saveLockTask(st.getTaskId(), st.getUserName(), st.getDataName(), st.getCoordinateX(), st.getCoordinateY(), st.getAdCode(),st.getDeviceInfo(),st.getScoreId(),st.getTokenId());
					}else{
						resultEntity=taskHandlerService.saveLockUserTask(st.getTaskId(), st.getUserName(), st.getDataName(), st.getCoordinateX(), st.getCoordinateY(), st.getAdCode(),st.getDeviceInfo(),st.getScoreId(),st.getTokenId());
					}
					
					
//					TaskHandlerService4Search.l
					
					entity.setResultData(resultEntity);
					resultEntity.setAdCode(st.getAdCode());
					entity.setCode("0");
					entity.setDesc(BaseController.SUCCESS);
				}
			}catch (AppException e) {
			// 修改为成功失败信息JSON格式统一模式
			this.writeErrorResult(entity, e.getSqlExpEnum().getCode(), e.getSqlExpEnum().getMessage(), response, request);
			if(st.getTokenId()!=null&&!st.getTokenId().equals("")
					&&!e.getSqlExpEnum().getCode().equals("500")
					&&!e.getSqlExpEnum().getCode().equals("501")
					&&!e.getSqlExpEnum().getCode().equals("502")
					&&!e.getSqlExpEnum().getCode().equals("503")
					&&!e.getSqlExpEnum().getCode().equals("111")
					&&!e.getSqlExpEnum().getCode().equals("114")){
//				service.deleteAgentTaskToken(tokenId);
				taskHandlerService.deleteAgentTaskTokenForSave(st.getTokenId(),st.getUserName());
			}
			return;
			// entity.setCode(e.getSqlExpEnum().getCode());
			// entity.setDesc(e.getSqlExpEnum().getMessage());
		}
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
		this.writeResult(entity, request, response);
		}
	}

	@Override
	protected ServletInfoBean vaildJsonData(String content, ServletInfoBean obj) {
		SaveTaskEntity save=(SaveTaskEntity)obj;
		try {
			JSONObject json = JSONObject.fromObject(content);
			logger.info("获取的入参为"+json.toString());
			//防止参数重复错误
			String regex="\\[\".*\",\".*\"\\]";
			Pattern pattren=Pattern.compile(regex);
			Matcher mat = pattren.matcher(json.toString());
			if(mat.find()){
				save.setErrorMessage("JSON格式解析错误");
				return  save;
			}
			//強制校验tokenId
			if(json.get("tokenId")!=null&&!json.get("tokenId").toString().equals("")&&!json.get("tokenId").toString().equalsIgnoreCase("null")){
				save.setTokenId(json.get("tokenId").toString());
			}else{
				save.setErrorMessage("tokenId");
				return save;
			}
		
			//定向任务判断
			String recommend="0";
			if(json.get("recommend")!=null&&json.get("recommend").toString().equals("1"))recommend=json.get("recommend").toString();
			save.setRecommend(recommend);
			//用户校验前置判断
			if(json.get("userName")!=null&&!json.get("userName").toString().equals("")&&!json.get("userName").toString().equalsIgnoreCase("null")){
				save.setUserName(json.get("userName").toString());
			}else{
				save.setErrorMessage("userName");
				return save;
			}
			//根据TOKEN_ID获取坐标
			AgentTaskToken token=taskHandlerService.getAgentTaskToken(save.getTokenId(),save.getUserName());
			if(token==null){
				save.setErrorMessage("token没有找到");
				return save;
			}
			save.setCoordinateX(token.getX());
			save.setCoordinateY(token.getY());
			if(!token.getUserName().equals(save.getUserName())){
				save.setErrorMessage("userName");
				logger.error("token用户名不一致 校验失败");
				taskHandlerService.deleteAgentTaskTokenForSave(save.getTokenId(),save.getUserName());
				return save;
			}
			
			//主被动任务判断
			String taskId=null;
			String baseId = null;
			String adCode = null;
			if(json.get("taskId")!=null&&!json.get("taskId").toString().equals("")&&!json.get("taskId").toString().equalsIgnoreCase("null")){
				taskId = json.get("taskId").toString();
			}
			taskId = (taskId == null) ? "" : taskId;
			String scoreId="";
			if(json.get("scoreId")!=null&&!json.get("scoreId").toString().equals("")&&!json.get("scoreId").toString().equalsIgnoreCase("null")){
				scoreId = json.get("scoreId").toString();
			}
			if(taskId!=null&&!taskId.equals("")&&new Integer(taskId)>0){
				if(scoreId==null||scoreId.equals("")||new Integer(scoreId)<1){
					scoreId="1";
				}
			}
			save.setScoreId(scoreId);
			save.setTaskId(taskId);
			
			if(json.get("dataName")!=null&&!json.get("dataName").toString().equals("")&&!json.get("dataName").toString().equalsIgnoreCase("null")){
				save.setDataName(json.get("dataName").toString());
			}else{
				save.setErrorMessage("dataName");
				taskHandlerService.deleteAgentTaskTokenForSave(save.getTokenId(),save.getUserName());
				return save;
			}
			
			
			
			if(json.get("baseId")!=null&&!json.get("baseId").toString().equals("")&&!json.get("baseId").toString().equalsIgnoreCase("null")){
				baseId = json.get("baseId").toString();
			}
			if(json.get("adCode")!=null&&!json.get("adCode").toString().equals("")&&!json.get("adCode").toString().equalsIgnoreCase("null")){
				adCode = json.get("adCode").toString();
			}
			if (baseId != null) {
				if ("".equals(baseId)) {
					save.setErrorMessage("baseId");
					taskHandlerService.deleteAgentTaskTokenForSave(save.getTokenId(),save.getUserName());
					return save;
				}
				if ((adCode == null || "".equals(adCode)) || adCode.length() != 6) {
					save.setErrorMessage("adCode");
					taskHandlerService.deleteAgentTaskTokenForSave(save.getTokenId(),save.getUserName());
					return save;
				}
				save.setBaseId(baseId);
				save.setAdCode(adCode);
				
			}else{
				if (!this.valieateXY(save.getCoordinateX(), save.getCoordinateY(),logger)) {
					save.setErrorMessage("X,Y坐标错误");
					taskHandlerService.deleteAgentTaskTokenForSave(save.getTokenId(),save.getUserName());
					return save;
				}
				if (adCode != null && !"".equals(adCode) && adCode.length() != 6) {
					save.setErrorMessage("adCode");
					taskHandlerService.deleteAgentTaskTokenForSave(save.getTokenId(),save.getUserName());
					return save;
				} else if (adCode == null || "".equals(adCode)) {
					// 没有ADCODE 自动获取
					adCode = this.getAdCode(save.getCoordinateX(), save.getCoordinateY(), true);
				}
				//设备信息为空强制不让保存
				if(json.get("deviceInfo")!=null&&!json.get("deviceInfo").toString().equals("")&&!json.get("deviceInfo").toString().equalsIgnoreCase("null")){
					save.setDeviceInfo(json.get("deviceInfo").toString());
				}else{
					save.setErrorMessage("deviceInfo");
					taskHandlerService.deleteAgentTaskTokenForSave(save.getTokenId(),save.getUserName());
					return save;
				}
				save.setAdCode(adCode);
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			save.setErrorMessage("JSON数据格式错误");
		}
		return save;
	}

}

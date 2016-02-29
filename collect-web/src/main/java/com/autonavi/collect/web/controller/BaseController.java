package com.autonavi.collect.web.controller;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.time.DateUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import autonavi.online.framework.property.PropertiesConfig;
import autonavi.online.framework.property.PropertiesConfigUtil;
import autonavi.online.framework.util.json.JsonBinder;

import com.autonavi.collect.bean.CollectDicScoreDetail;
import com.autonavi.collect.bean.CollectTaskClazz;
import com.autonavi.collect.bean.CollectTaskImg;
import com.autonavi.collect.constant.CommonConstant;
import com.autonavi.collect.constant.CommonConstant.TASK_EXTRA_OPERATION;
import com.autonavi.collect.constant.CommonConstant.TASK_IMG_STATUS;
import com.autonavi.collect.des.BaseEnc;
import com.autonavi.collect.des.BaseEncNew;
import com.autonavi.collect.des.BaseEncOld;
import com.autonavi.collect.entity.CollectTaskAppEntity;
import com.autonavi.collect.entity.TaskExtraInfoEntity;
import com.autonavi.collect.exception.BusinessException;
import com.autonavi.collect.exception.BusinessExceptionEnum;
import com.autonavi.collect.exception.BusinessRunException;
import com.autonavi.collect.service.TaskCollectUtilService;
import com.autonavi.collect.web.bean.CheckDataInfoBean;
import com.autonavi.collect.web.bean.ResultDesc;
import com.autonavi.collect.web.bean.ResultEntity;
import com.autonavi.collect.web.bean.TaskExtraInfoWebEntity;
import com.autonavi.collect.web.constant.WebConstant;
import com.autonavi.collect.web.des.EncWrapperOld;
import com.autonavi.collect.web.util.PropertyUtils;

public abstract class BaseController<T> {
	private Logger logger = LogManager.getLogger(this.getClass());
	protected static final String SUCCESS = "success";
	protected static long deadLineTimeOld = -1;
	protected static long deadLineTimeNew = -1;
	private PropertiesConfig pc = null;

	@Autowired
	private TaskCollectUtilService taskCollectUtilService;
	
	public BaseController()throws Exception{
		if (pc == null){
			pc = PropertiesConfigUtil.getPropertiesConfigInstance();
			InputStream is = getClass().getResourceAsStream("/myip");
			if (is != null) {
				try {
					String myip = IOUtils.toString(is);
					WebConstant.myip=myip;
				} catch (IOException e) {
					logger.error("读取myip文件错误，启动失败!");
					System.exit(0);
				} finally {
					IOUtils.closeQuietly(is);
				}
			} else {
				logger.error("myip文件不存在，启动失败!");
				System.exit(0);
			}
		}
			
	}

	private static String flag = null;

	/**
	 * 回写token_id加密的方式
	 * 
	 * @param check
	 * @param tokenId
	 * @return
	 */
	protected String cryptorTokenId(int check, String tokenId) {
		// 算法替代 0代表最新版本加解密 -1代表上个版本加解密 -2代表content改造之前
		if (check == 0) {
			return tokenId;
		}
		if (check == -1) {
			return tokenId;
		}
		if (check == -2) {
			return BaseEncOld.disp_encode(tokenId);
		}
		return "";
	}

	protected String getBizProperty(String key) {
		return (String) pc.getProperty(key);
	}

	/**
	 * 校验加密算法的合法性
	 * 
	 * @param req
	 * @param resp
	 * @param obj
	 * @param logger
	 * @param items
	 * @return
	 */
	protected int checkCryptorVersion(HttpServletRequest req,
			HttpServletResponse resp, T obj, Logger logger, List<FileItem> items) {
		boolean useDes = false;
		try {
			useDes = new Boolean(
					this.getBizProperty(WebConstant.USE_CONTENT_DES));
		} catch (Exception e1) {
			logger.warn("获取加密标异常", e1);
		}
		if (useDes) {
			if (flag == null) {
				try {
					deadLineTimeOld = DateUtils.parseDate(
							getBizProperty(
									CommonConstant.PROP_DEAD_LINE_TIME_OLD)
									.toString(),
							new String[] { "yyyy-MM-dd HH:mm:ss" }).getTime();
					deadLineTimeNew = DateUtils.parseDate(
							getBizProperty(
									CommonConstant.PROP_DEAD_LINE_TIME_NEW)
									.toString(),
							new String[] { "yyyy-MM-dd HH:mm:ss" }).getTime();
				} catch (Exception e) {
					e.printStackTrace();
					return 3;
				}
				flag = "";
			}
			// 启用全新的content字段
			String content = req.getParameter("content");
			if(content==null) content="";
			// 处理文件上传的特殊情况
			if (items != null && items.size() > 0
					&& ServletFileUpload.isMultipartContent(req)) {
				try {
					for (FileItem item : items) {
						if (item.isFormField()) {
							String key = item.getFieldName();
							String value = item.getString();
							if (key.equals("content")) {
								content = value;
								break;
							}
						}
					}
				} catch (Exception e) {

				}
			}
			Date date = new Date();
			if (deadLineTimeOld == -1) {
				return 3;
			}
			if (deadLineTimeNew == -1) {
				return 3;
			}
			if (content == null && date.getTime() < deadLineTimeOld) {
				return -2;
			} else if (content == null && date.getTime() >= deadLineTimeOld) {
				// 超过兼容时间 旧的请求方式失效
				req = new EncWrapperOld(req);
				return 2;
			} else {
				// 采取新的content方式
				// 转码
				CheckDataInfoBean checkObj = this.vaildJsonData(
						BaseEncNew.disp_decode(content, "utf-8"), obj);
				if (checkObj != null && checkObj.getErrorMessage() != null) {
					boolean isError = false;
					// 兼容今后换加密算法的情况
					if (date.getTime() < deadLineTimeNew) {
						checkObj = this.vaildJsonData(
								BaseEnc.disp_decode(content, "utf-8"), obj);
						if (checkObj.getErrorMessage() != null) {
							isError = true;
						}
					} else {
						isError = true;
					}
					if (isError) {
						logger.info("请求参数 【" + checkObj.getErrorMessage()
								+ "】为空!");
						return 1;
					}
					return -1;
				}
				return 0;
			}
		} else {
			String content = req.getParameter("content");
			if(content==null) content="{}";
			CheckDataInfoBean checkObj = this.vaildJsonData(content, obj);
			if (checkObj != null && checkObj.getErrorMessage() != null) {
				logger.info("请求参数 【" + checkObj.getErrorMessage() + "】为空!");
				return 1;
			}
			return 0;
		}

	}

	protected CheckDataInfoBean vaildJsonData(String content, T obj) {
		return new CheckDataInfoBean();
	}

	public void writeError(ResultEntity entity, HttpServletRequest req,
			HttpServletResponse resp, BusinessException e) {
		ResultDesc desc=new ResultDesc();
		desc.setCode(e.getSqlExpEnum().getCode());
		desc.setMsg(e.getSqlExpEnum().getMessage());
		entity.setStatus(desc);
		writeResult(entity, req, resp);
	}

	public void writeError(ResultEntity entity, HttpServletRequest req,
			HttpServletResponse resp, BusinessRunException e) {
		ResultDesc desc=new ResultDesc();
		desc.setCode(e.getSqlExpEnum().getCode());
		desc.setMsg(e.getSqlExpEnum().getMessage());
		entity.setStatus(desc);
		writeResult(entity, req, resp);
	}

	protected void writeErrorResult(ResultEntity entity, String errorCode,
			String result, HttpServletResponse resp, HttpServletRequest req) {
		ResultDesc desc=new ResultDesc();
		desc.setCode(errorCode);
		desc.setMsg(result);
		entity.setStatus(desc);
		entity.setResult(result);
		writeResult(entity, req, resp);

	}

	protected String getIpAddr(HttpServletRequest request) {
		String ip = request.getHeader("x-forwarded-for");
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("WL-Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr();
		}
		return ip;
	}

	public void writeResult(ResultEntity entity, HttpServletRequest req,
			HttpServletResponse resp) {
		resp.setContentType("text/html;charset=utf-8");
//		Object data = entity.getResult();
		PrintWriter out = null;
		boolean jsonP = false;

//		// 返回json结果
//		JSONObject jsonResult = new JSONObject();
//		// 错误对象
//		JSONObject errObj = new JSONObject();
//		errObj.accumulate("code", entity.getCode());
//		errObj.accumulate("msg", entity.getDesc());
//		jsonResult.accumulate("status", errObj);
//
//		if (data instanceof Map) {
//			JSONObject obj = JSONObject.fromObject(data);
//			jsonResult.accumulate("result", obj);
//		} else {
//			jsonResult.accumulate("result", data);
//		}
//
//		if (entity.getTotalCount() != null) {
//			jsonResult.accumulate("totalCount", entity.getTotalCount());
//		}
//		if (entity.getEnterPriseName() != null) {
//			jsonResult.accumulate("enterPriseName", entity.getEnterPriseName());
//		}
//		if (entity.getTotalScore() != null) {
//			jsonResult.accumulate("totalScore", entity.getTotalScore());
//		}
		String jsonResult=JsonBinder.buildNonNullBinder(false).toJson(entity);
		logger.info("jsonResult:" + jsonResult.toString());
		String cb = req.getParameter("callback");
		if (cb != null) {
			jsonP = true;
			resp.setContentType("text/javascript");
		}
		try {
			out = resp.getWriter();
			if (jsonP) {
				out.write(cb + "(");
			}
			out.print(jsonResult.toString());
			if (jsonP) {
				out.write(");");
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			out.close();
		}
	}

	/**
	 * 校验坐标格式
	 * 
	 * @param x
	 * @param y
	 * @param logger
	 * @return
	 */
	protected boolean valieateXY(String x, String y, Logger logger) {
		double xValue = 0.0;
		double yValue = 0.0;

		try {
			xValue = Double.parseDouble(x);
			yValue = Double.parseDouble(y);
		} catch (Exception e) {
			logger.warn("x,y 格式不对! x=" + x + ";y=" + y);
			return false;
		}

		if (!(xValue > 0 && xValue <= 180)) {
			return false;
		}
		if (!(yValue > 0 && yValue <= 90)) {
			return false;
		}
		return true;
	}

	protected String[] getOffsetXY(String x, String y) {

		String[] offsetList = taskCollectUtilService.offsetXY(x, y);
		if (offsetList.length == 2) {
			return offsetList;
		} else {
			throw new BusinessRunException(
					BusinessExceptionEnum.PAGE_QUERY_ERROR);
		}
	}
	protected Integer fetchAdcode(double x, double y) {

		return new Integer(taskCollectUtilService.fetchAdcode(x, y));
	}

	/**
	 * 获取ADCODE
	 * 
	 * @param x
	 * @param y
	 * @param isGPS
	 * @return
	 */
	protected String getAdCode(String x, String y, boolean isGPS) {
		if (isGPS) {
			String[] offsetList = taskCollectUtilService.offsetXY(x, y);
			if (offsetList.length == 2) {
				x = offsetList[0];
				y = offsetList[1];
			}
		}
		double corrX = Double.parseDouble(x);
		double corrY = Double.parseDouble(y);
		String adCode = taskCollectUtilService.fetchAdcode(corrX, corrY);
		if (adCode == null || "".equals(adCode) || (adCode.length() != 6)) {
			throw new BusinessRunException(
					BusinessExceptionEnum.FETCH_ADCODE_ERROR);
		}
		return adCode;
	}

	/**
	 * 
	 * @param entity
	 * @param req
	 * @param resp
	 * @param termainFlag
	 */
	public void writeResult(ResultEntity entity, HttpServletRequest req,
			HttpServletResponse resp, String termainFlag) {
		if (termainFlag != null && ("html".equalsIgnoreCase(termainFlag))) {
			resp.setHeader("Access-Control-Allow-Origin", "*");
		}
		writeResult(entity, req, resp);
	}

	protected void sendMsg(HttpServletResponse response, String content) {
		response.setContentType("text/html;charset=utf-8");
		PrintWriter writer = null;
		try {
			writer = response.getWriter();
			writer.write(content);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (writer != null) {
				writer.close();
			}
		}
	}

	protected void generateTaskAppEntity(List<CollectTaskAppEntity> l) {

		if (l != null) {
			Map<Long, CollectDicScoreDetail> m1 = CollectScoreLevelInit.scoreLevels;
			for (CollectTaskAppEntity entity : l) {
				entity.setScore(m1.get(entity.getScoreId()).getValue());
			}
		}

	}
	
	protected String getCollectImgUrl(String imageName)throws Exception{
		if(imageName==null||imageName.equals("")){
			return null;
		}
		String commonUrl =  this.getBizProperty(WebConstant.COLLECT_IMG_URL);
		String[] spiltOne = imageName.split("\\.");
		String[] splitTwo = spiltOne[0].split("_");
		StringBuffer sb = new StringBuffer("");
		for (int i = 0; i < splitTwo.length - 1; i++) {
			sb.append(File.separator);
			sb.append(splitTwo[i]);
		}
		return commonUrl+sb.toString()+File.separator+imageName;
	}

	protected List<String> getOrginImgUrls(String url) throws Exception {
		if (url == null) {
			return new ArrayList<String>();
		}
		String[] spiltOne = url.split("\\.");
		String[] splitTwo = spiltOne[0].split("_");
		List<String> list = new ArrayList<String>();
		String commonUrl = this.getBizProperty(WebConstant.ORGIN_IMG_URL);
		StringBuffer sb = new StringBuffer("");
		StringBuffer sb_1 = new StringBuffer("");
		for (int i = 0; i < splitTwo.length - 1; i++) {
			sb_1.append(splitTwo[i]);
			sb_1.append("_");
			if (i < splitTwo.length - 2) {
				sb.append("/");
				sb.append(splitTwo[i]);
			}
		}
		commonUrl = commonUrl + sb.toString() + "/" + sb_1.toString();
		for (int i = 0; i < new Integer(splitTwo[splitTwo.length - 1]); i++) {
			list.add(commonUrl + i + "." + spiltOne[1]);
		}
		return list;
	}
	@SuppressWarnings("unchecked")
	protected TaskExtraInfoWebEntity getCollectTaskExtrasInfo(String extras,Long baseId,Long userId,TaskCollectUtilService taskCollectUtilService,Long collectClazzId)throws Exception{
		List<CollectTaskImg> result_0 =new ArrayList<CollectTaskImg>();
		List<TaskExtraInfoEntity> result_1 =new ArrayList<TaskExtraInfoEntity>();
		TaskExtraInfoWebEntity webEntity=new TaskExtraInfoWebEntity();
		JsonBinder binder=JsonBinder.buildNormalBinder(false);
		Map<Long,Long> existBatchId=new HashMap<Long,Long>();
		if(baseId!=null&&collectClazzId!=null
				&&taskCollectUtilService.checkTaskClazzInfo(baseId, "baseId",collectClazzId.toString())){
			logger.error("baseId["+baseId+"]类型["+collectClazzId+"]错误");
			throw new BusinessException(BusinessExceptionEnum.PARAM_FORMAT_EXP);
		}
		List<Object> _extras=binder.fromJson(extras, List.class, binder.getCollectionType(List.class, Object.class));
		for(Object extra:_extras){
			Object _operation=PropertyUtils.getValue(extra, "operation");
			Object _batchId=PropertyUtils.getValue(extra, "batchId");
			Object _obj=PropertyUtils.getValue(extra, "value.imgs");
			Object _level=PropertyUtils.getValue(extra, "level");
			Object _clazzId=PropertyUtils.getValue(extra, "value.collectClassId");
			Object _value=PropertyUtils.getValue(extra, "value");
			//兼容 如果有
			Object _money=PropertyUtils.getValue(extra, "value.money");
			Object _userMoney=PropertyUtils.getValue(extra, "value.userMoney");
			if(_userMoney!=null){
				_money=_userMoney;
			}else{
				_userMoney=_money;
			}
			Object _customMoney=PropertyUtils.getValue(extra, "value.customMoney");
			Object _versionNo=PropertyUtils.getValue(extra, "value.versionNo");
			if(!(_operation instanceof String)){
				logger.error("operation格式错误!");
				throw new BusinessException(BusinessExceptionEnum.PARAM_FORMAT_EXP);
			}
			if(!(_level instanceof String)){
				logger.error("level格式错误!");
				throw new BusinessException(BusinessExceptionEnum.PARAM_FORMAT_EXP);
			}
			Integer oper=Integer.valueOf((String)_operation);
			if(!oper.equals(TASK_EXTRA_OPERATION.UPDATE.getCode())
					&&!oper.equals(TASK_EXTRA_OPERATION.DELETE.getCode())){
				logger.error("operation类型错误错误!");
				throw new BusinessException(BusinessExceptionEnum.PARAM_FORMAT_EXP);
			}
			if(oper.equals(TASK_EXTRA_OPERATION.DELETE.getCode())&&(_batchId==null||_batchId.equals(""))){
				logger.error("删除的操作必须提供batchId!");
				throw new BusinessException(BusinessExceptionEnum.PARAM_FORMAT_EXP);
			}
			if(!(_clazzId instanceof String)){
				logger.error("collectClassId格式错误!");
				throw new BusinessException(BusinessExceptionEnum.PARAM_FORMAT_EXP);
			}
            if(_obj!=null&&!(_obj instanceof List)){
            	logger.error("imgs格式错误!");
				throw new BusinessException(BusinessExceptionEnum.PARAM_FORMAT_EXP);
			} 
            if(!(_value instanceof Map)){
				logger.error("value格式错误!");
				throw new BusinessException(BusinessExceptionEnum.PARAM_FORMAT_EXP);
			}
            if(!(_money instanceof String)){
				logger.error("money格式错误!");
				throw new BusinessException(BusinessExceptionEnum.PARAM_FORMAT_EXP);
			}
            if(!(_userMoney instanceof String)){
				logger.error("userMoney格式错误!");
				throw new BusinessException(BusinessExceptionEnum.PARAM_FORMAT_EXP);
			}
            if(_obj==null){
            	_obj=new ArrayList<Object>();
            }
            List<Object> _l=(List<Object>)_obj;
            boolean moneyChange=false;
            if(_batchId==null||_batchId.equals("")){//以后变为兼容老模式 
            	if(baseId!=null&&!taskCollectUtilService.checkTaskLevelExist(baseId, _level.toString())){
            		//不存在这个节点的情况下 或者是新任务第一次保存 //生成一个新ID
            		_batchId=taskCollectUtilService.getPlatFromUniqueId()+"";
            		moneyChange=true;
            	}else if(baseId!=null&&taskCollectUtilService.checkTaskLevelExist(baseId, _level.toString())){
            		logger.error("baseId["+baseId+"]已经存储过节点["+_level.toString()+"] 必须提供 batchId");
            		throw new BusinessException(BusinessExceptionEnum.TASK_STEP_IS_SAVED);
            	}else{
            		//不存在这个节点的情况下 或者是新任务第一次保存 //生成一个新ID
            		_batchId=taskCollectUtilService.getPlatFromUniqueId()+"";
            		moneyChange=true;
            	}
            }
            else{
            	if(baseId!=null){
            		if(!taskCollectUtilService.checkTaskLevelExist(baseId, _level.toString())){
                		//不存在这个节点的情况下并且校验成功
                		if(taskCollectUtilService.checkPlatFromBatchId(Long.valueOf(_batchId.toString()),userId)){
                			moneyChange=true;
                		}else{
                			logger.error("baseId["+baseId+"]已经存储过节点["+_level.toString()+"]的batchId["+_batchId+"]不存在或者已经超时");
                    		throw new BusinessException(BusinessExceptionEnum.BATCHID_CACHE_IS_NOT_FOUND);
                		}  		
                	}else if(!taskCollectUtilService.checkTaskLevelBatchInfo(baseId,  _level.toString(), _batchId.toString(),_clazzId.toString(),_money.toString())){
                		logger.error("batchId["+_batchId+"]或collectClassId["+_clazzId+"]和"+_level.toString()+"不对应!");
                		throw new BusinessException(BusinessExceptionEnum.PARAM_FORMAT_EXP);
                	}
            	}else{
            		logger.error("batchId["+_batchId+"]已经存储过节点 必须提供 baseId");
            		throw new BusinessException(BusinessExceptionEnum.PARAM_FORMAT_EXP);
            	}
            }
            if(oper.equals(TASK_EXTRA_OPERATION.DELETE.getCode())){
            	moneyChange=true;
            }
            Map<String,Object> _map=(Map<String,Object>)_value;
    		_map.put("batchId", _batchId);
    		CollectTaskClazz clazz=taskCollectUtilService.getCollectTaskClazzByIdFromCache(Long.valueOf(_clazzId.toString()));
    		_map.put("collectClassName", clazz==null?"":clazz.getClazzName());
            TaskExtraInfoEntity entity=new TaskExtraInfoEntity();
			entity.setOperation(_operation.toString());
			entity.setLevel(_level.toString());
			entity.setValue(_value);
			entity.setCollectClassId(_clazzId.toString());
			entity.setCollectClassName(clazz==null?"":clazz.getClazzName());
			entity.setBatchId(_batchId.toString());
			entity.setMoney(_money.toString());
			entity.setMoneyChange(moneyChange);
			entity.setVersionNo(_versionNo==null?"":_versionNo.toString());
			entity.setUserMoney(_userMoney.toString());
			entity.setCustomMoney(_customMoney==null?"":_customMoney.toString());
			result_1.add(entity);
			Long tempBatchId=0L;
			Long batchId=Long.valueOf(_batchId.toString());
			if(existBatchId.containsKey(batchId)){
				tempBatchId=existBatchId.get(batchId);
			}else{
				tempBatchId=taskCollectUtilService.getPlatFromUniqueId();
				existBatchId.put(batchId, tempBatchId);
			}
            //没有提供图片信息的情况下且batcId为空(第一次保存) 默认提供一张信息 不带坐标 用来站位
//            if(_l.size()==0){
//            	CollectTaskImg img=new CollectTaskImg();
//            	img.setBaseId(baseId);
//            	img.setImageIndex(1);
//            	img.setImageStatus(TASK_IMG_STATUS.STATION.getCode());
//            	img.setTaskClazzId(Long.valueOf(_clazzId.toString()));
//            	img.setImageH5Id(imageH5Id);
//            	img.setImageBatchId(batchId);
//            	img.setTempBatchId(tempBatchId);
//            	img.setCollectOffsetX(116.0);
//            	img.setCollectOffsetY(40.0);
//            	result_0.add(img);
//            	
//            }
            if(oper.equals(TASK_EXTRA_OPERATION.UPDATE.getCode())){
            	//仅在更新情况下解析图片信息
            	Set<Integer> existIndex=new HashSet<Integer>();
            	Set<String> existH5Id=new HashSet<String>();
    			for(Object _img:_l){
    				String index=PropertyUtils.
    						getValue(_img, "index").toString();
    				if(index==null){
    					logger.error("index没有提供!");
    					throw new BusinessException(BusinessExceptionEnum.PARAM_FORMAT_EXP);
    				}
    				String imgType=PropertyUtils.
    						getValue(_img, "imgType").toString();
    				if(imgType==null){
    					logger.error("imgType没有提供!");
    					throw new BusinessException(BusinessExceptionEnum.PARAM_FORMAT_EXP);
    				}
    				String lat=PropertyUtils.
    						getValue(_img, "lat").toString();
    				if(lat==null){
    					logger.error("lat没有提供!");
    					throw new BusinessException(BusinessExceptionEnum.PARAM_FORMAT_EXP);
    				}
    				String lon=PropertyUtils.
    						getValue(_img, "lon").toString();
    				if(lon==null){
    					logger.error("lon没有提供!");
    					throw new BusinessException(BusinessExceptionEnum.PARAM_FORMAT_EXP);
    				}
    				String imgH5Id=PropertyUtils.
    						getValue(_img, "imgH5Id").toString();
    				if(imgH5Id==null){
    					logger.error("imgH5Id没有提供!");
    					throw new BusinessException(BusinessExceptionEnum.PARAM_FORMAT_EXP);
    				}
    				if(!existH5Id.add(imgH5Id)){
    					logger.error("imgH5Id重复");
    					throw new BusinessException(BusinessExceptionEnum.PARAM_FORMAT_EXP);
    				}
    				CollectTaskImg img=new CollectTaskImg();
    				img.setBaseId(baseId);
    				Integer _index=Integer.valueOf(index);
    				if(!existIndex.add(_index)){
    					logger.error("index重复");
    					throw new BusinessException(BusinessExceptionEnum.PARAM_FORMAT_EXP);
    				}
    				img.setImageIndex(_index);
    				img.setImageFlag(imgType);
    				img.setCollectX(Double.valueOf(lon));
    				img.setCollectY(Double.valueOf(lat));
    				img.setImageBatchId(batchId);
    				//坐标转换
    				String[] offset=this.getOffsetXY(img.getCollectX().toString(), img.getCollectY().toString());
    				img.setCollectOffsetX(new Double(offset[0]));
    				img.setCollectOffsetY(new Double(offset[1]));
    				img.setCollectAdcode(this.fetchAdcode(new Double(offset[0]), new Double(offset[1])));
    				double[] xy=taskCollectUtilService.transferCoordinate(CommonConstant.GPS_SYSTEM.BAIDU.getCode(), CommonConstant.GPS_SYSTEM.DEFAULT.getCode(), img.getCollectOffsetX(), img.getCollectOffsetY());
    				img.setCollectX(xy[0]);
    				img.setCollectY(xy[1]);
    				img.setTaskClazzId(Long.valueOf(_clazzId.toString()));
    				if(oper.equals(TASK_EXTRA_OPERATION.UPDATE.getCode())){
    					img.setImageStatus(TASK_IMG_STATUS.USE.getCode());
    				}else{
    					img.setImageStatus(TASK_IMG_STATUS.UNUSE.getCode());
    				}
    				img.setImageH5Id(imgH5Id);
    				img.setTempBatchId(tempBatchId);
    				result_0.add(img);
    			}
    			existIndex.clear();
    			existH5Id.clear();
            }
            
		}
		webEntity.setCollectTaskImgList(result_0);
		webEntity.setTaskExtraInfoEntityList(result_1);
		return webEntity;
	}

}

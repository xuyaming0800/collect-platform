package com.autonavi.collect.web.controller;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import autonavi.online.framework.constant.Miscellaneous;
import autonavi.online.framework.sharding.uniqueid.UniqueIDHolder;

import com.autonavi.collect.bean.CollectTaskBase;
import com.autonavi.collect.bean.CollectTaskImg;
import com.autonavi.collect.constant.CommonConstant;
import com.autonavi.collect.constant.CommonConstant.TASK_IMG_STATUS;
import com.autonavi.collect.entity.CollectTaskSubmitEntity;
import com.autonavi.collect.entity.CollectTaskSubmitReturnEntity;
import com.autonavi.collect.exception.BusinessException;
import com.autonavi.collect.exception.BusinessExceptionEnum;
import com.autonavi.collect.exception.BusinessRunException;
import com.autonavi.collect.service.InitiativeTaskService;
import com.autonavi.collect.service.PassiveTaskService;
import com.autonavi.collect.service.TaskCollectUtilService;
import com.autonavi.collect.service.TaskTokenService;
import com.autonavi.collect.util.FileUtil;
import com.autonavi.collect.web.bean.CheckDataInfoBean;
import com.autonavi.collect.web.bean.ResultDesc;
import com.autonavi.collect.web.bean.ResultEntity;
import com.autonavi.collect.web.bean.TaskSubmitInfoEntity;
import com.autonavi.collect.web.constant.WebConstant;


@Controller
public class CollectTaskSubmitController extends BaseController<TaskSubmitInfoEntity> {
	
	private Logger logger = LogManager.getLogger(this.getClass());
	
	@Autowired
	private TaskCollectUtilService taskCollectUtilService;
	
	@Autowired
	private TaskTokenService taskTokenService;
	
	@Autowired
	private PassiveTaskService passiveTaskService;
	@Autowired
	private InitiativeTaskService initiativeTaskService;
	
	public CollectTaskSubmitController() throws Exception {
		super();
	}
	
	static final String IMG_PATH = "/img";
	static final String UPLOAD_TEMP="/temp";
	private static String IMG_ROOT = "/collect-img";
	private static String IMAGE_SUFFIX = ".jpg";
	private static Boolean USEWATERMAKER=false;
	
	@RequestMapping("/uploadUserData")
	public void submitTask(HttpServletRequest req,
			HttpServletResponse resp,@RequestParam("image") MultipartFile file){
        logger.info("用户数据上传接口请求开始");
        TaskSubmitInfoEntity uploadEntity = new TaskSubmitInfoEntity();
		ResultEntity entity = new ResultEntity();
		DiskFileItemFactory factory = new DiskFileItemFactory();
		ServletFileUpload fileUpload = new ServletFileUpload(factory);
		factory.setSizeThreshold(80 * 1024);
		List<FileItem> items=null;
		List<CollectTaskImg> imgList=new ArrayList<CollectTaskImg>();
		try {
			items = fileUpload.parseRequest(req);
		} catch (FileUploadException e) {
			logger.error("====File upload exception====", e);
			this.writeErrorResult(entity, BusinessExceptionEnum.PRASE_IMAGE_ERROR.getCode(), BusinessExceptionEnum.PRASE_IMAGE_ERROR.getMessage(), resp, req);
			return;
		}
		int check=this.checkCryptorVersion(req, resp, uploadEntity, logger,items);
		if(check>0){
			if(check==3){
				this.writeErrorResult(entity, "401", "系统内部异常", resp, req);
				return;
			}
			if(check==1){
				this.writeErrorResult(entity, "201", "参数无法解析", resp, req);
				return;
			}
			if(check==2){
				this.writeErrorResult(entity, BusinessExceptionEnum.TASK_TOKEN_ERROR.getCode(), BusinessExceptionEnum.TASK_TOKEN_ERROR.getMessage(), resp, req);
				return;
			}
		}else if(check==-2){
			logger.info("用户疑似作弊IP=["+this.getIpAddr(req)+"]");
			this.writeErrorResult(entity, "201", "无法解析全部参数", resp, req);
			return;
		}else{
//			String baseId = uploadEntity.getCollectTaskBase().getId().toString();
//			String isLocate = uploadEntity.getIsLocate();
//			String scoreId=uploadEntity.getCollectTaskBase().getRewardId().toString();
			try {
				if (!ServletFileUpload.isMultipartContent(req)) {
					logger.warn("只能是 multipart/form-data 类型数据");
					this.writeErrorResult(entity, BusinessExceptionEnum.PRASE_IMAGE_ERROR.getCode(), BusinessExceptionEnum.PRASE_IMAGE_ERROR.getMessage(), resp, req);
					return;
				}
				
				// 图片上传
				imgList=this.uploadImage(uploadEntity,req, file, null);
				
				//验证码校验开始 //验证码功能暂时注释掉 打开后需要调整
				this.checkNeedVaildCode(uploadEntity);
				
				
//				if(uploadEntity.getRecommend()!=null&&uploadEntity.getRecommend().equals("1")){
//					logger.info("Allot TASK SAVE ID="+uploadEntity.getTokenId()+" user="+uploadEntity.getUserName());
//				}else{
//					uploadEntity.setRecommend("0");
//				}
				/* 判断是否 主动任务(如果taskId是空，表示是主动任务) */
				boolean activeTask = uploadEntity.getCollectTaskBase().getPassiveId() == null;
				String urlPath=req.getScheme()+"://"+WebConstant.myip+":"+req.getServerPort()+IMG_ROOT+IMG_PATH;
				CollectTaskSubmitEntity submitEntity=new CollectTaskSubmitEntity();
				submitEntity.setCollectTaskBase(uploadEntity.getCollectTaskBase());
				submitEntity.setCollectTaskImgList(imgList);
				submitEntity.setToken(uploadEntity.getToken());
				if(activeTask){
					//主动任务
//					uploadEntity.getCollectTaskBase().setRewardId(CollectScoreLevelInit.currentScoreTypes.get(CommonConstant.TASK_TYPE_INITIATIVE).getId());
					//主动任务提交 暂时不实现
					logger.info("开始数据入库-主动任务");
					CollectTaskSubmitReturnEntity _entity=initiativeTaskService.taskSubmit(submitEntity);
					try {
						initiativeTaskService.taskPackageSyncAudit(_entity);
					} catch (Exception e) {
						logger.error(e.getMessage());
					}
					try {
						if(USEWATERMAKER)
						initiativeTaskService.taskSyncWaterImage(imgList, urlPath, _entity);
					} catch (Exception e) {
						logger.error(e.getMessage());
					}
					
				}else{
					//被动任务提交
				    logger.info("开始数据入库-被动任务");
				    if(uploadEntity.getIsLost().equals("0")){
				    	//缺失任务
				    	submitEntity.setLost(true);
				    	CollectTaskSubmitReturnEntity _entity=passiveTaskService.taskSubmit(submitEntity);
				    	try {
							passiveTaskService.taskPackageSyncAudit(_entity);
						} catch (Exception e) {
							logger.error(e.getMessage());
						}
				    	
				    }else{
				    	submitEntity.setLost(false);
				    	CollectTaskSubmitReturnEntity _entity=passiveTaskService.taskSubmit(submitEntity);
				    	try {
							passiveTaskService.taskPackageSyncAudit(_entity);
						} catch (Exception e) {
							logger.error(e.getMessage());
						}
				    	try {
				    		if(USEWATERMAKER)
							passiveTaskService.taskSyncWaterImage(imgList, urlPath, _entity);
						} catch (Exception e) {
							logger.error(e.getMessage());
						}
				    }
					
				}
				ResultDesc desc=new ResultDesc();
				desc.setCode("0");
				desc.setMsg(BaseController.SUCCESS);
				entity.setStatus(desc);
				
			} catch (BusinessRunException e) {
				this.removeImage(imgList, req);
				this.writeErrorResult(entity, e.getSqlExpEnum().getCode(), e.getSqlExpEnum().getMessage(), resp, req);
				return;
			} catch (BusinessException e) {
				this.removeImage(imgList, req);
				this.writeErrorResult(entity, e.getSqlExpEnum().getCode(), e.getSqlExpEnum().getMessage(), resp, req);
				return;
			}catch (Exception e) {
				this.removeImage(imgList, req);
				logger.error("====Unknown exception====", e);
				this.writeErrorResult(entity, BusinessExceptionEnum.INSERT_AGENT_TASK_ERROR.getCode(), BusinessExceptionEnum.INSERT_AGENT_TASK_ERROR.getMessage(), resp, req);
				return;
			}
			// 输出结果
			this.writeResult(entity, req, resp, null);
			
		}
		
	}
	@Override
	protected CheckDataInfoBean vaildJsonData(String content,TaskSubmitInfoEntity entity){
		CollectTaskBase submit=new CollectTaskBase();
		entity.setCollectTaskBase(submit);
		CheckDataInfoBean obj=new CheckDataInfoBean();
		String tokenId="";
		try {
			boolean isDevMode=new Boolean(this.getBizProperty(WebConstant.DEV_MODE).toString());
			JSONObject json = JSONObject.fromObject(content);
			logger.info("获取的入参为"+json.toString());
			//防止参数重复错误
			String regex="\\[\".*\",\".*\"\\]";
			Pattern pattren=Pattern.compile(regex);
			Matcher mat = pattren.matcher(json.toString());
			if(mat.find()){
				obj.setErrorMessage("JSON格式解析错误");
				return  obj;
			}
			//強制校验tokenId
			if(json.get("tokenId")!=null&&!json.get("tokenId").toString().equals("")&&!json.get("tokenId").toString().equalsIgnoreCase("null")){
				tokenId=json.get("tokenId").toString();
			}
		
			//定向任务判断
//			String recommend="0";
//			if(json.get("recommend")!=null&&json.get("recommend").toString().equals("1"))recommend=json.get("recommend").toString();
//			save.setRecommend(recommend);
			//用户校验前置判断
			if(json.get("userName")!=null&&!json.get("userName").toString().equals("")&&!json.get("userName").toString().equalsIgnoreCase("null")){
				submit.setCollectUserId(taskCollectUtilService.getUserIdCache(json.get("userName").toString()));
			}else{
				obj.setErrorMessage("userName");
				return obj;
			}
			//MD5校验
			if(json.get("md5Validate")!=null&&!json.get("md5Validate").toString().equals("")&&!json.get("md5Validate").toString().equalsIgnoreCase("null")){
				entity.setMd5Validate(json.get("md5Validate").toString());
			}else{
				obj.setErrorMessage("md5Validate");
				if(!isDevMode)
					  taskTokenService.updateTaskTokenStatus(submit.getCollectUserId(), tokenId, CommonConstant.TOKEN_STATUS_CHEAT);
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
				submit.setPassiveId(new Long(taskId));
			}
			//业主-品类
			if (json.get("ownerId") != null && !json.get("ownerId").toString().equals("")
					&& !json.get("ownerId").toString().equalsIgnoreCase("null")) {
				submit.setOwnerId(Long.valueOf(json.get("ownerId").toString()));
			}else{
				boolean activeTask = submit.getPassiveId() == null;
				if(activeTask){
					logger.warn("暂时兼容广告拍拍主动任务! 设置为主动1");
					submit.setOwnerId(1L);
					
				}else{
					logger.warn("暂时兼容广告拍拍被动任务! 设置为被动2");
					submit.setOwnerId(2L);
				}
			}
			//判定是否使用Token校验
			Boolean isUseToken=taskTokenService.checkTokenUseStatus(submit.getOwnerId());
			if(isUseToken){
				if(tokenId==null){
					obj.setErrorMessage("tokenId");
					return obj;
				}
				//校验Token信息 此处的校验至校验了入参 文件的真实的MD5还需要比对
				Boolean check=taskTokenService.checkTaskToken(submit.getCollectUserId(), tokenId,baseId, entity.getMd5Validate(),submit.getOwnerId(),true);
				if(!check){
					if(!isDevMode)
						  taskTokenService.updateTaskTokenStatus(submit.getCollectUserId(), tokenId, CommonConstant.TOKEN_STATUS_CHEAT);
					obj.setErrorMessage("token校验失败");
					return obj;
				}
				entity.setToken(tokenId);
			}
			
			
			
			if(json.get("collectClassId")!=null&&!json.get("collectClassId").toString().equals("")&&!json.get("collectClassId").toString().equalsIgnoreCase("null")){
				submit.setTaskClazzId(new Long(json.get("collectClassId").toString()));
			}
			//主动任务必须提供类别
			if(taskId==null&&submit.getTaskClazzId()==null){
				obj.setErrorMessage("collectClassId不能为空");
				return obj;
			}
			
			taskId = (taskId == null) ? "" : taskId;
			
			if(json.get("dataName")!=null&&!json.get("dataName").toString().equals("")&&!json.get("dataName").toString().equalsIgnoreCase("null")){
				submit.setCollectDataName(json.get("dataName").toString());
			}else{
				obj.setErrorMessage("dataName");
				if(!isDevMode)
				  taskTokenService.updateTaskTokenStatus(submit.getCollectUserId(), tokenId, CommonConstant.TOKEN_STATUS_CHEAT);
				return obj;
			}
			
			
			if (baseId != null) {
				if ("".equals(baseId)) {
					obj.setErrorMessage("baseId");
					if(!isDevMode)
					  taskTokenService.updateTaskTokenStatus(submit.getCollectUserId(), tokenId, CommonConstant.TOKEN_STATUS_CHEAT);
					return obj;
				}
				submit.setId(new Long(baseId));
				
			}
			//设备信息校验
			if(json.get("deviceInfo")!=null&&!json.get("deviceInfo").toString().equals("")&&!json.get("deviceInfo").toString().equalsIgnoreCase("null")){
				submit.setDeviceInfo(json.get("deviceInfo").toString());
			}else{
				obj.setErrorMessage("deviceInfo");
				if(!isDevMode)
					  taskTokenService.updateTaskTokenStatus(submit.getCollectUserId(), tokenId, CommonConstant.TOKEN_STATUS_CHEAT);
				return obj;
			}
			//缺失
			if(json.get("isLost")!=null&&!json.get("isLost").toString().equals("")&&!json.get("isLost").toString().equalsIgnoreCase("null")){
				entity.setIsLost(json.get("isLost").toString());
			}
			
			//如下封装其他参数
			//批量标志
			if(json.get("isBatch")!=null&&!json.get("isBatch").toString().equals("")&&!json.get("isBatch").toString().equalsIgnoreCase("null")){
				entity.setIsBatch(json.get("isBatch").toString());
			}
			//本地保存标记
            if(json.get("isLocate")!=null&&!json.get("isLocate").toString().equals("")&&!json.get("isLocate").toString().equalsIgnoreCase("null")){
            	entity.setIsLocate(json.get("isLocate").toString());
			}
            //批量批次标记
            if(json.get("pack")!=null&&!json.get("pack").toString().equals("")&&!json.get("pack").toString().equalsIgnoreCase("null")){
				entity.setPack(json.get("pack").toString());
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			obj.setErrorMessage("JSON数据格式错误");
		}
		return obj;
	}
	private List<CollectTaskImg> uploadImage(TaskSubmitInfoEntity uploadEntity,HttpServletRequest req, MultipartFile item, String set) {

//		if (!ServletFileUpload.isMultipartContent(req)) {
//			AgentTaskUploadServlet.logger.warn("只能是 multipart/form-data 类型数据");
//			throw new AppException(AppExceptionEnum.PRASE_IMAGE_ERROR);
//		}

		try {
			List<CollectTaskImg> list=new ArrayList<CollectTaskImg>();
			boolean isDevMode=new Boolean(this.getBizProperty(WebConstant.DEV_MODE).toString());
			if (item!=null&&!item.isEmpty()) {
					// 生成图片
					list=this.write2Image(item,req,uploadEntity);
			}
			if(list==null||list.size()==0){
				logger.warn("没有采集附件!");
				this.removeImage(list, req);
				throw new BusinessRunException(201, "没有采集附件!");
			}
			// 验证form表单参数
	        for(CollectTaskImg img:list ){
	        	String result = this.valdateParam(img);
				if (result != null && !result.equals("")) {
					this.removeImage(list, req);
					if(!isDevMode)
						  taskTokenService.updateTaskTokenStatus(uploadEntity.getCollectTaskBase().getCollectUserId(), uploadEntity.getToken(), CommonConstant.TOKEN_STATUS_CHEAT);
					throw new BusinessRunException(201, "请求参数 【" + result + "】为空 或 不符合规范!");
				}
				// 自动获取ADCODE
				if (img.getCollectAdcode()== null || img.getCollectAdcode().equals("")) {
					img.setCollectAdcode(new Integer(this.getAdCode(img.getCollectX().toString(), img.getCollectY().toString(), true)));
				}
				// 验证图片格式
				if (img.getImgName().endsWith(CollectTaskSubmitController.IMAGE_SUFFIX)&&!this.validateImageGPG(img.getImgName(),req)) {
					logger.warn("上传图片格式非JPG格式!");
					this.removeImage(list, req);
					if(!isDevMode)
						  taskTokenService.updateTaskTokenStatus(uploadEntity.getCollectTaskBase().getCollectUserId(), uploadEntity.getToken(), CommonConstant.TOKEN_STATUS_CHEAT);
					throw new BusinessRunException(BusinessExceptionEnum.IMAGE_TYPE_ERROR);
					// throw new AppException(AppExceptionEnum.IMAGE_TYPE_ERROR);
				}
				//校验MP4格式//预留
				// 图片标记判断 //暂时注释
//				if (!FaskImgCheck.parseImage(this.getImgPathByImageId(img.getImgName(),req))) {
//					logger.warn("图片标记错误!");
//                  this.removeImage(imgList, req);	
//				    if(!isDevMode)
//					     taskTokenService.updateTaskTokenStatus(uploadEntity.getCollectTaskBase().getCollectUserId(), uploadEntity.getToken(), CommonConstant.TOKEN_STATUS_CHEAT);
//					throw new BusinessRunException(BusinessExceptionEnum.TASK_VALIDATE_ERROR);
//				}
				
	        }
			return list;
			
		} catch (BusinessRunException ex) {
			throw ex;
		} catch (Exception e) {
			logger.error("图片解析异常,请重新提交", e);
			throw new BusinessRunException(BusinessExceptionEnum.PRASE_IMAGE_ERROR);
		}

	}
	private List<CollectTaskImg> write2Image(MultipartFile item,HttpServletRequest req,TaskSubmitInfoEntity entity) {
		ZipInputStream zin = null;// 输入源
		BufferedInputStream bin = null;
		InputStreamReader infoR = null;
		BufferedReader infoBuf = null;
		FileInputStream fis = null;
		String _imgName=null;
		try {
			Map<String,String> imageNameMap=new HashMap<String,String>();
			Map<String,CollectTaskImg> collectTaskImgMap=new HashMap<String,CollectTaskImg>();
			List<CollectTaskImg> imgList=new ArrayList<CollectTaskImg>();
			Boolean isSuccess=true;
			boolean isDevMode=new Boolean(this.getBizProperty(WebConstant.DEV_MODE).toString());
			//InputStream in=item.getInputStream();
			//现将压缩文件拷贝到本地缓存目录
			String _path = FileUtil.getImagePath();
			_imgName = this.getImgName(_path);
			BufferedOutputStream outStream=null;
			FileOutputStream fo=null;
			InputStream in=null;
			
			try {
				in=item.getInputStream();
				if(USEWATERMAKER){
					fo=new FileOutputStream(new File(this.getTempPath(_imgName,req)));
				}else{
					fo=new FileOutputStream(new File(this.getTempPath(_imgName)));
				}
				
				outStream = new BufferedOutputStream(fo);
				int length = 0;
				byte[] buf = new byte[1024];
				// in.read(buf) 每次读到的数据存放在 buf 数组中
				while ((length = in.read(buf)) != -1) {
					outStream.write(buf, 0, length);
				}
				outStream.flush();
			} catch (Exception e1) {
				logger.warn("上传附件出现异常",e1);
				throw new BusinessRunException(BusinessExceptionEnum.PRASE_IMAGE_ERROR);
				
			} finally{
				if(outStream!=null){
					outStream.close();
				}
				if(fo!=null){
					fo.close();
				}
				if(in!=null){
					in.close();
				}
			}
			//校验本地文件的MD5
			if(USEWATERMAKER){
				if (!FileUtil.checkImageFile(this.getTempPath(_imgName,req), entity.getMd5Validate())) {
					logger.warn("附件MD5验证不通过!");
					throw new BusinessRunException(BusinessExceptionEnum.MD5_VALIDATE_IMAGE_ERROR);
					// throw new AppException(AppExceptionEnum.MD5_VALIDATE_IMAGE_ERROR);
				}
				fis=new FileInputStream(this.getTempPath(_imgName,req));
			}else{
				if (!FileUtil.checkImageFile(this.getTempPath(_imgName), entity.getMd5Validate())) {	
					logger.warn("附件MD5验证不通过!");
					throw new BusinessRunException(BusinessExceptionEnum.MD5_VALIDATE_IMAGE_ERROR);
				}
				fis=new FileInputStream(this.getTempPath(_imgName));
			}
			zin = new ZipInputStream(fis);// 输入源
			bin = new BufferedInputStream(zin);
			infoR = new InputStreamReader(bin,"utf-8");
			infoBuf = new BufferedReader(infoR);
			ZipEntry entry;
			File Fout = null;
			
			try {
				Set<String> existIndex=new HashSet<String>();
				Set<String> existH5Id=new HashSet<String>();
				Set<String> existImageName=new HashSet<String>();
				Map<String,Long> existLevel=new HashMap<String,Long>();
				loop:while ((entry = zin.getNextEntry()) != null
						&& !entry.isDirectory()) {
					 
					if (entry.getName().equals("ImageInfo.txt")) {

						String line;
						StringBuffer buffer = new StringBuffer();
						while ((line = infoBuf.readLine()) != null) {
							buffer.append(line);
						}
						//logger.info("获得的描述文件为: "+buffer.toString());
						JSONArray json = JSONArray
								.fromObject(buffer.toString());
						int index=0;
						for(Object obj:json){
							JSONObject js=JSONObject.fromObject(obj);
							if(js.containsKey("imageName")){
								index++;
								CollectTaskImg img=this.parseJSON2Img(js);
								if(img.getImageIndex()==null){
									img.setImageIndex(index);
									img.setImageBatchId(1000L);
									img.setImageStatus(TASK_IMG_STATUS.USE.getCode());
									img.setImageH5Id("old"+index);
								}
								if(img.getLevel()!=null&&img.getImageBatchId()!=null){
									if(existLevel.containsKey(img.getLevel())){
										if(!existLevel.get(img.getLevel()).equals(img.getImageBatchId())){
											logger.error("batchId["+img.getImageBatchId()+"]出现在不同的层级");
											isSuccess=false;
											break loop;
										}
									}else{
										existLevel.put(img.getLevel(), img.getImageBatchId());
									}
								}else{
									logger.warn("暂时兼容旧的模式 不做层级和批次号的完整匹配");
								}
								if(img.getImageBatchId()!=null&&img.getImageH5Id()!=null){
									if(!existH5Id.add(img.getImageBatchId()+"_"+img.getImageH5Id())){
										logger.error("上传的图片H5页面ID[batchId_H5ID="+img.getImageBatchId()+"_"+img.getImageH5Id()+"]重复");
										isSuccess=false;
										break loop;
									}
								}else{
									logger.warn("暂时兼容旧的模式 不做层级和批次号的完整匹配");
								}
								if(img.getImgName()==null){
									logger.error("上传的图片名称为空");
									isSuccess=false;
									break loop;
								}
								else if(!existImageName.add(img.getImgName())){
									logger.error("上传的描述文件中图片名称重复["+img.getImgName()+"]");
									isSuccess=false;
									break loop;
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
								String batchId=img.getImageBatchId()==null?"":img.getImageBatchId().toString();
								if(img.getImageIndex()!=null&&!existIndex.add(img.getImageIndex()+"_"+batchId)){
									logger.error("上传的图片序列号重复");
									isSuccess=false;
									break loop;
								}
								collectTaskImgMap.put(img.getImgName(), img);
							}
							
						}

//					} else if(entry.getName().endsWith(CollectTaskSubmitController.IMAGE_SUFFIX)) {
//						//图片格式校验取消 暂时不做处理
					} else {
						FileOutputStream out=null;
						BufferedOutputStream bout=null;
						String _suffix=entry.getName().substring(entry.getName().lastIndexOf("."));
						 try {
							 logger.info("获取上传的图片名称:"+entry.getName());
							 String path = FileUtil.getImagePath(UniqueIDHolder.getIdWorker().nextId(Miscellaneous.getMyid()),Miscellaneous.getMyid());
							 String imgPath="";
							 if(USEWATERMAKER){
								 imgPath = this.getImgPath(path,req);
							 }else{
								 imgPath = this.getImgPath(path);
							 }
							 String imgName = this.getImgName(path);
							 String file=this.getWholeImgPath(imgPath,imgName,_suffix);
							 Fout=new File(file);
							 out=new FileOutputStream(Fout);
							 bout=new BufferedOutputStream(out);
							 byte[] buf = new byte[1024];
							 int length=0;
							 while ((length = bin.read(buf)) != -1) {
							     bout.write(buf, 0, length);
							 }
							 imageNameMap.put(entry.getName(), imgName+_suffix);
						} catch (Exception e) {
							logger.warn("生成上传图片异常", e);
							isSuccess=false;
							break loop;
							 
						} finally{
							if(bout!=null)
							    bout.close();
							 if(out!=null)
							    out.close();
						}
//					} else{
//						if(!isDevMode)
//							  taskTokenService.updateTaskTokenStatus(entity.getCollectTaskBase().getCollectUserId(), entity.getToken(), CommonConstant.TOKEN_STATUS_CHEAT);
//						throw new BusinessRunException(BusinessExceptionEnum.IMAGE_TYPE_ERROR);
					}

				}
				
				
				//处理结果
				
				for(String name:imageNameMap.keySet()){
					CollectTaskImg img=collectTaskImgMap.get(name);
					if(img==null){
						logger.error("图片名称["+name+"]没有在描述文件中找到");
						isSuccess=false;
						img=new CollectTaskImg();
						img.setImgName(imageNameMap.get(name));
						imgList.add(img);
						
					}else{
						img.setImgName(imageNameMap.get(name));
						imgList.add(img);
					}
					
				}
				if(!isSuccess){
					BusinessRunException e=new BusinessRunException(BusinessExceptionEnum.IMAGE_INFO_ERROR);
					if(!isDevMode)
						  taskTokenService.updateTaskTokenStatus(entity.getCollectTaskBase().getCollectUserId(), entity.getToken(), CommonConstant.TOKEN_STATUS_CHEAT);
					throw e;
				}
				//校验层级和批次是否已经正确保存过 如果服务端不存在步骤 则不上传对应的图片 并给出警告 
				Set<String> deleteBatchSet=new HashSet<String>();
				List<CollectTaskImg> submitList=new ArrayList<CollectTaskImg>();
				List<CollectTaskImg> deleteList=new ArrayList<CollectTaskImg>();
				for(String key:existLevel.keySet()){
					if(entity.getCollectTaskBase().getId()==null){
						logger.error("baseId为空");
						isSuccess=false;
						break;
					}
					boolean checkLevel=taskCollectUtilService.checkTaskLevelBatchInfo(entity.getCollectTaskBase().getId(), 
							key, existLevel.get(key).toString());
					if(!checkLevel){
						logger.warn("level["+key+"] batchId["+existLevel.get(key).toString()+"]没有找到 这个层级对应的图片的数据不会被上传");
						deleteBatchSet.add(key+"_"+existLevel.get(key).toString());
					}
				}
				if(!isSuccess){
					throw new BusinessRunException(BusinessExceptionEnum.PRASE_IMAGE_ERROR);
				}
				
				for(CollectTaskImg _img:imgList){
					Long _imgBatchId=_img.getImageBatchId();
					String _level=_img.getLevel();
					if(_level!=null&&_imgBatchId!=null&&deleteBatchSet.contains(_level+"_"+_imgBatchId)){
						logger.warn("需要删除图片img=["+_img.getImgName()+"] 因为在服务端没有对应的层级["+_level+"]和批次["+_imgBatchId+"]的步骤");
						deleteList.add(_img);
					}else{
						submitList.add(_img);
					}
				}
				//清理图片
				this.removeImage(deleteList, req);
				return submitList;
			} catch (BusinessRunException e){
				//清理图片
				this.removeImage(imgList, req);
				throw e;
			} catch (Exception e) {
				logger.warn("生成上传图片异常", e);
				//清理图片
				this.removeImage(imgList, req);
				throw new BusinessRunException(BusinessExceptionEnum.PRASE_IMAGE_ERROR);
			}
		}catch (BusinessRunException e){
			throw e;
		} catch (Exception e) {
			logger.warn("生成上传图片异常", e);
			throw new BusinessRunException(BusinessExceptionEnum.PRASE_IMAGE_ERROR);

		} finally{
			try{
				try {
					if(infoBuf!=null)
					  infoBuf.close();
				} catch (IOException e) {
					logger.warn("生成上传图片异常", e);
			          throw new BusinessRunException(BusinessExceptionEnum.PRASE_IMAGE_ERROR);
				}
				try {
					if(infoR!=null)
						infoR.close();
				} catch (IOException e) {
					logger.warn("生成上传图片异常", e);
					throw new BusinessRunException(BusinessExceptionEnum.PRASE_IMAGE_ERROR);
				}
				try {
					if(bin!=null)
						bin.close();
				} catch (IOException e) {
					logger.warn("生成上传图片异常", e);
					throw new BusinessRunException(BusinessExceptionEnum.PRASE_IMAGE_ERROR);
				}
				try {
					if(zin!=null)
						zin.close();
				} catch (IOException e) {
					logger.warn("生成上传图片异常", e);
					throw new BusinessRunException(BusinessExceptionEnum.PRASE_IMAGE_ERROR);
				}
				try {
					if(fis!=null)
						fis.close();
				} catch (IOException e) {
					logger.warn("生成上传图片异常", e);
					throw new BusinessRunException(BusinessExceptionEnum.PRASE_IMAGE_ERROR);
				}
			}catch (BusinessRunException e){
				throw e;
				
			}finally{
				if(_imgName!=null){
					if(USEWATERMAKER){
						this.removeTemp(this.getTempPath(_imgName, req), req);
					}else{
						this.removeTemp(this.getTempPath(_imgName), req);
					}
					
				}
			}
			
			
		}
	}
	
	private CollectTaskImg parseJSON2Img(JSONObject json)throws Exception{
		CollectTaskImg img=new CollectTaskImg();
		img.setImageStatus(TASK_IMG_STATUS.USE.getCode());
		if(json.get("positionX")!=null&&!json.get("positionX").toString().equals("")&&!json.get("positionX").toString().equalsIgnoreCase("null")&&!json.get("positionX").toString().equalsIgnoreCase("nan")){
			img.setPositionX(new Double(json.get("positionX").toString()));
		}
		if(json.get("positionY")!=null&&!json.get("positionY").toString().equals("")&&!json.get("positionY").toString().equalsIgnoreCase("null")&&!json.get("positionY").toString().equalsIgnoreCase("nan")){
			img.setPositionY(new Double(json.get("positionY").toString()));
		}
		if(json.get("positionZ")!=null&&!json.get("positionZ").toString().equals("")&&!json.get("positionZ").toString().equalsIgnoreCase("null")&&!json.get("positionZ").toString().equalsIgnoreCase("nan")){
			img.setPositionZ(new Double(json.get("positionZ").toString()));
		}
		if(json.get("pointType")!=null&&!json.get("pointType").toString().equals("")&&!json.get("pointType").toString().equalsIgnoreCase("null")){
			img.setLocationType(new Integer(json.get("pointType").toString()));
		}
		if(json.get("pointLevel")!=null&&!json.get("pointLevel").toString().equals("")&&!json.get("pointLevel").toString().equalsIgnoreCase("null")){
			img.setGpsCount(new Integer(json.get("pointLevel").toString()));
		}
//		if(json.get("adCode")!=null&&!json.get("adCode").toString().equals("")&&!json.get("adCode").toString().equalsIgnoreCase("null")){
//			img.setCollectAdcode(new Integer(json.get("adCode").toString()));
//		}
		if(json.get("photoTime")!=null&&!json.get("photoTime").toString().equals("")&&!json.get("photoTime").toString().equalsIgnoreCase("null")){
			img.setPhotoTime(DateUtils.parseDate(json.get("photoTime").toString(), new String[]{"yyyy-MM-dd HH:mm:ss"}).getTime()/1000);
		}
		if(json.get("gpsTime")!=null&&!json.get("gpsTime").toString().equals("")&&!json.get("gpsTime").toString().equalsIgnoreCase("null")){
			img.setGpsTime(DateUtils.parseDate(json.get("gpsTime").toString(), new String[]{"yyyy-MM-dd HH:mm:ss"}).getTime()/1000);
		}
		if(json.get("pointAccury")!=null&&!json.get("pointAccury").toString().equals("")&&!json.get("pointAccury").toString().equalsIgnoreCase("null")){
			img.setGpsAccuracy(new Double(json.get("pointAccury").toString()));
		}
		if(json.get("position")!=null&&!json.get("position").toString().equals("")&&!json.get("position").toString().equalsIgnoreCase("null")){
			img.setPosition(new Double(json.get("position").toString()));
		}
		if(json.get("x")!=null&&!json.get("x").toString().equals("")&&!json.get("x").toString().equalsIgnoreCase("null")){
			img.setCollectX(new Double(json.get("x").toString()));
		}
		if(json.get("y")!=null&&!json.get("y").toString().equals("")&&!json.get("y").toString().equalsIgnoreCase("null")){
			img.setCollectY(new Double(json.get("y").toString()));
		}
		if(json.get("imageName")!=null&&!json.get("imageName").toString().equals("")&&!json.get("imageName").toString().equalsIgnoreCase("null")){
			img.setImgName(json.get("imageName").toString());
		}
		if(json.get("index")!=null&&!json.get("index").toString().equals("")&&!json.get("index").toString().equalsIgnoreCase("null")){
			img.setImageIndex(new Integer(json.get("index").toString()));
			img.setImageH5Id("old"+img.getImageIndex().toString());
			img.setImageBatchId(1000L);
			img.setImageStatus(TASK_IMG_STATUS.USE.getCode());
		}
		//
		if(json.get("imgType")!=null&&!json.get("imgType").toString().equals("")&&!json.get("imgType").toString().equalsIgnoreCase("null")){
			img.setImageFlag(json.get("imgType").toString());
		}
		if(json.get("imgH5Id")!=null&&!json.get("imgH5Id").toString().equals("")&&!json.get("imgH5Id").toString().equalsIgnoreCase("null")){
			img.setImageH5Id(json.get("imgH5Id").toString());
		}
		if(json.get("batchId")!=null&&!json.get("batchId").toString().equals("")&&!json.get("batchId").toString().equalsIgnoreCase("null")){
			img.setImageBatchId(Long.valueOf(json.get("batchId").toString()));
		}
		if(json.get("level")!=null&&!json.get("level").toString().equals("")&&!json.get("level").toString().equalsIgnoreCase("null")){
			img.setLevel(json.get("level").toString());
		}
		
		return img;
		
	}

	/**
	 * 201310/22/1/random
	 * 
	 * @param path
	 * @return www.xxx.com/201310/22/1
	 */
	private String getImgPath(String path,HttpServletRequest req) {
		String imgPath = path.substring(0, path.lastIndexOf(File.separator));
		String serverPath = req.getSession().getServletContext().getRealPath("");
		serverPath=serverPath.substring(0,serverPath.lastIndexOf(File.separator))+IMG_ROOT+IMG_PATH;
		StringBuilder result = new StringBuilder();
		result.append(serverPath).append(File.separator).append(imgPath);
		return result.toString();
	}
	
	private String getImgPath(String path) {
		String imgPath = path.substring(0, path.lastIndexOf(File.separator));
		String serverPath = this.getBizProperty(WebConstant.COLLECT_IMG_REAL_PATH)+CollectTaskSubmitController.IMG_PATH;
		StringBuilder result = new StringBuilder();
		result.append(serverPath).append(File.separator).append(imgPath);
		return result.toString();
	}
	private String getTempPath(String tempFile,HttpServletRequest req) {
		String path = req.getSession().getServletContext().getRealPath("");
		path=path.substring(0,path.lastIndexOf(File.separator))+IMG_ROOT+UPLOAD_TEMP;
		String serverPath = path;
		File file = new File(serverPath);
		if (!file.exists()) {
			synchronized (CollectTaskSubmitController.class) {
				if (!file.exists()) {
					file.mkdirs();
				}
			}
		}
		StringBuilder result = new StringBuilder();
		result.append(serverPath).append(File.separator).append(tempFile);
		return result.toString();
	}
	
	private String getTempPath(String tempFile){
		String serverPath = this.getBizProperty(WebConstant.COLLECT_IMG_REAL_PATH)+CollectTaskSubmitController.UPLOAD_TEMP;
		File file = new File(serverPath);
		if (!file.exists()) {
			synchronized (CollectTaskSubmitController.class) {
				if (!file.exists()) {
					file.mkdirs();
				}
			}
		}
		StringBuilder result = new StringBuilder();
		result.append(serverPath).append(File.separator).append(tempFile);
		return result.toString();
	}

	/**
	 * 201310/22/1/random
	 * 
	 * @param path
	 * @return 201310_22_1_random
	 */
	private String getImgName(String path) {
		return StringUtils.replace(path, File.separator, "_");// 201310_22_1_random
	}

	/**
	 * 获得图片完整路径
	 * 
	 * @param imgPath
	 *            www.xxx.com/201310/22/1
	 * @param imgName
	 *            201310_22_1_random
	 * @return www.xxx.com/201310/22/1/201310_22_1_random.jpg
	 */
	private String getWholeImgPath(String imgPath, String imgName,String suffix) {
		File file = new File(imgPath);
		if (!file.exists()) {
			synchronized (CollectTaskSubmitController.class) {
				if (!file.exists()) {
					file.mkdirs();
				}
			}
		}
		StringBuilder path = new StringBuilder();
		path.append(imgPath).append(File.separator).append(imgName).append(suffix);
		return path.toString();
	}

	/**
	 * 201310_22_1_random
	 * 
	 * @param imageId
	 * @return
	 */
	private String getImgPathByImageId(String imageId,HttpServletRequest req) {
		String path = req.getSession().getServletContext().getRealPath("");
		path=path.substring(0,path.lastIndexOf(File.separator))+IMG_ROOT+IMG_PATH;
		if (StringUtils.isEmpty(imageId)) {
			logger.error("imageId参数不能为空");
			throw new BusinessRunException(BusinessExceptionEnum.PARAM_FORMAT_EXP);
		}

		String imgName = StringUtils.replace(imageId, "_", File.separator);
		String resultPath = imgName.substring(0, imgName.lastIndexOf(File.separator));
		StringBuilder imgPath = new StringBuilder(path);
		imgPath.append(File.separator).append(resultPath).append(File.separator).append(imageId);
//		if (!imageId.endsWith(CollectTaskSubmitController.IMAGE_SUFFIX)) {
//			imgPath.append(CollectTaskSubmitController.IMAGE_SUFFIX);
//		}
		return imgPath.toString();
	}
	/**
	 * 201310_22_1_random
	 * 
	 * @param imageId
	 * @return
	 */
	private String getImgPathByImageId(String imageId) {
		String path = this.getBizProperty(WebConstant.COLLECT_IMG_REAL_PATH)+CollectTaskSubmitController.IMG_PATH;
		if (StringUtils.isEmpty(imageId)) {
			logger.error("imageId参数不能为空");
			throw new BusinessRunException(BusinessExceptionEnum.PARAM_FORMAT_EXP);
		}

		String imgName = StringUtils.replace(imageId, "_", File.separator);
		String resultPath = imgName.substring(0, imgName.lastIndexOf(File.separator));
		StringBuilder imgPath = new StringBuilder(path);
		imgPath.append(File.separator).append(resultPath).append(File.separator).append(imageId);
//		if (!imageId.endsWith(CollectTaskSubmitController.IMAGE_SUFFIX)) {
//			imgPath.append(CollectTaskSubmitController.IMAGE_SUFFIX);
//		}
		return imgPath.toString();
	}
	/**
	 * 非空验证
	 * 
	 * @param uploadEntity
	 * @return
	 */
	private String valdateParam(CollectTaskImg uploadEntity) {

		Double x = uploadEntity.getCollectX();
		Double y = uploadEntity.getCollectY();
		Integer pointLevel = uploadEntity.getGpsCount();
		Integer pointType = uploadEntity.getLocationType();
		// String enterPriseCode = uploadEntity.getEnterPriseCode();
		Integer adCode = uploadEntity.getCollectAdcode();

		if (x == null || "".equals(x)) {
			return "x";
		}
		if (y == null || "".equals(y)) {
			return "y";
		}

		// 对于新版运维系统中enterPriceCode 可以为空，所以去掉enterPriceCode参数校验

//		if (enterPriseCode == null || "".equals(enterPriseCode)) {
//			return "enterPriseCode";
//		}
		if (pointLevel == null || "".equals(pointLevel)) {
			return "pointLevel";
		}
		if (pointType == null || "".equals(pointType)) {
			return "pointType";
		}
		// adCode没有将根据经纬度自动计算
		// if (adCode == null || "".equals(adCode)) {
		// return false;
		// }
		if (adCode != null && !"".equals(adCode) && adCode.toString().length() != 6) {
			return "adCode";
		}
		return "";
	}
	/**
	 * 验证图片GPG格式
	 * 
	 * @param imageId
	 *            201310_22_1_random
	 * @return
	 */

	private boolean validateImageGPG(String imageId,HttpServletRequest req) {
		// String path = getServletContext().getRealPath("/upload");
		// String filePath = path + File.separator + imageId + IMAGE_SUFFIX;
		if(USEWATERMAKER){
			return FileUtil.validateImage(this.getImgPathByImageId(imageId,req));
		}else{
			return FileUtil.validateImage(this.getImgPathByImageId(imageId));
		}
		
	}

//	// md5验证
//	private boolean checkImageMD5(String imageId, String md5Str,HttpServletRequest req) {
//		// String path = getServletContext().getRealPath("/upload");
//		// String filePath = path + File.separator + imageId + IMAGE_SUFFIX;
//		String filePath = this.getImgPathByImageId(imageId,req);
//		return FileUtil.checkImageFile(filePath, md5Str);
//	}
	private void removeImage(List<CollectTaskImg> imgList,HttpServletRequest req) {
		if(imgList!=null){
			for(CollectTaskImg img:imgList){
				String filePath="";
				if(USEWATERMAKER){
					filePath = this.getImgPathByImageId(img.getImgName(),req);
				}else{
					filePath = this.getImgPathByImageId(img.getImgName());
				}
				logger.info("删除异常图片" + filePath);
				try {
					File file = new File(filePath);
					file.delete();
				} catch (Exception e) {
					logger.warn("删除图片异常@@@@不能影响业务!");
				}
			}
			imgList.clear();
		}
		
	}
	/**
	 * 删除临时文件
	 * @param tempPath
	 * @param req
	 */
    private void removeTemp(String tempPath,HttpServletRequest req) {
		
    	try {
			File file = new File(tempPath);
			file.delete();
		} catch (Exception e) {
			logger.warn("删除图片异常@@@@不能影响业务!");
		}
	}
	/**
	 * 校验是否愿需要弹出验证码 暂时不实现
	 * @param entity
	 * @return
	 */
	private boolean checkNeedVaildCode(TaskSubmitInfoEntity entity){
		//批量提交 //验证码功能暂时注释掉 打开后需要调整
//		if(uploadEntity.getIsBatch()!=null&&uploadEntity.getIsBatch().equals("0")){
//			if(uploadEntity.getPack()==null){
//				logger.info("批量提交时候 终端提供的批次号为空");
//				this.writeErrorResult(entity, BusinessExceptionEnum.PARAM_IS_NULL.getCode(), BusinessExceptionEnum.PARAM_IS_NULL.getMessage(), resp, req);
//				return;
//			}
//			boolean b=taskHandlerService.checkCountIsOut(uploadEntity.getUserName(), uploadEntity.getPack());
//			if(!b){
//				//提示弹出验证码
//				logger.info("提示客户端弹出验证码 用户名["+uploadEntity.getUserName()+"]");
//				this.writeErrorResult(entity, AppExceptionEnum.SECURECODE_NEED.getCode(), AppExceptionEnum.SECURECODE_NEED.getMessage(), resp, req);
//				return;
//			}
//		}else{
//			boolean b=taskHandlerService.checkUserIsTimeOut(uploadEntity.getUserName());
//			if(!b){
//				//提示弹出验证码
//				logger.info("提示客户端弹出验证码 用户名["+uploadEntity.getUserName()+"]");
//				this.writeErrorResult(entity, AppExceptionEnum.SECURECODE_NEED.getCode(), AppExceptionEnum.SECURECODE_NEED.getMessage(), resp, req);
//				if (tokenId != null && (baseId == null || baseId.equals("")) && !isLocate.equals("1")) {
//					logger.info("DELETE TOKEN ("+uploadEntity.getUserName()+")"+tokenId+" baseId:"+baseId+" isLocate="+isLocate);
//					taskHandlerService.deleteAgentTaskTokenForSave(tokenId,uploadEntity.getUserName());
//				}
//				return;
//			}
//		}
		//验证码校验结束
		return true;
	}

}

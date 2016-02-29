package com.gd.app.web;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.gd.app.entity.AgentTaskToken;
import com.gd.app.entity.AgentTaskUploadEntity;
import com.gd.app.entity.ResultEntity;
import com.gd.app.entity.ServletInfoBean;
import com.gd.app.exception.AppException;
import com.gd.app.exception.AppExceptionEnum;
import com.gd.app.service.TaskHandlerService;
import com.gd.app.util.DateUtil;
import com.gd.app.util.FaskImgCheck;
import com.gd.app.util.FileUtil;

@Controller
public class AgentTaskUploadController extends BaseController {
	private Logger logger = LogManager.getLogger(this.getClass());
	@Resource
	private TaskHandlerService taskHandlerService;
	
	static final String IMG_PATH = "/img";
	private static String IMAGE_SUFFIX = ".jpg";
	
	@RequestMapping("/uploadUserData")
	public void submitTask(HttpServletRequest req,
			HttpServletResponse resp,@RequestParam("image") MultipartFile file){
        logger.info("用户数据上传接口请求开始");
		
		AgentTaskUploadEntity uploadEntity = new AgentTaskUploadEntity();
		ResultEntity entity = new ResultEntity();
		DiskFileItemFactory factory = new DiskFileItemFactory();
		ServletFileUpload fileUpload = new ServletFileUpload(factory);
		factory.setSizeThreshold(80 * 1024);
		List<FileItem> items=null;
		try {
			items = fileUpload.parseRequest(req);
		} catch (FileUploadException e) {
			logger.error("====File upload exception====", e);
			this.writeErrorResult(entity, AppExceptionEnum.PRASE_IMAGE_ERROR.getCode(), AppExceptionEnum.PRASE_IMAGE_ERROR.getMessage(), resp, req);
			return;
		}
		int check=this.checkCryptorVersion(req, resp, uploadEntity, logger,items);
		if(check>0){
			if(check==3){
				this.writeErrorResult(entity, "401", "系统内部异常", resp, req);
				return;
			}
			if(check==1){
				logger.info("用户 "+uploadEntity.getUserName()+"疑似作弊IP=["+this.getIpAddr(req)+"]");
				this.writeErrorResult(entity, "201", "参数无法解析", resp, req);
				return;
			}
			if(check==2){
				String user=req.getParameter("user");
				logger.info("用户 ["+user+"]疑似作弊 IP=["+this.getIpAddr(req)+"]");
				this.writeErrorResult(entity, AppExceptionEnum.TASK_TOKEN_ERROR.getCode(), AppExceptionEnum.TASK_TOKEN_ERROR.getMessage(), resp, req);
				return;
			}
		}else if(check==-2){
			logger.info("用户疑似作弊IP=["+this.getIpAddr(req)+"]");
			this.writeErrorResult(entity, "201", "无法解析全部参数", resp, req);
			return;
		}else{
		
			String tokenId = uploadEntity.getTokenId();
			String baseId = uploadEntity.getBaseId();
			String isLocate = uploadEntity.getIsLocate();
			String scoreId=uploadEntity.getScoreId();
			if(uploadEntity.getTaskId()==null||uploadEntity.getTaskid().indexOf("-")!=-1){
				scoreId=null;
				uploadEntity.setScoreId(scoreId);
				uploadEntity.setTaskid(null);
			}
			try {
				entity.setCode("0");
				AgentTaskToken token = null;
				if (!ServletFileUpload.isMultipartContent(req)) {
					logger.warn("只能是 multipart/form-data 类型数据");
					this.writeErrorResult(entity, AppExceptionEnum.PRASE_IMAGE_ERROR.getCode(), AppExceptionEnum.PRASE_IMAGE_ERROR.getMessage(), resp, req);
					return;
				}
				
				if (uploadEntity.getTokenId() == null) {
					logger.info("CHECK TOKEN ERROR("+uploadEntity.getUserName()+")，疑似作弊，用户名称为 " + uploadEntity.getUserName() + " 任务名称 " + uploadEntity.getDataName());
					this.writeErrorResult(entity, AppExceptionEnum.TASK_VALIDATE_ERROR.getCode(), AppExceptionEnum.TASK_VALIDATE_ERROR.getMessage(), resp, req);
					return;

				}else{
					logger.info("新任务 TOKEN="+uploadEntity.getTokenId());
					token = taskHandlerService.getAgentTaskToken(uploadEntity.getTokenId(),uploadEntity.getUserName());
					logger.info("====获取的token="+token);
				}
				uploadEntity.setX(token.getX());
				uploadEntity.setY(token.getY());
				
				// 图片上传
				this.uploadImage(uploadEntity, req, file, null);
				
				//验证码校验开始
				//批量提交
				if(uploadEntity.getIsBatch()!=null&&uploadEntity.getIsBatch().equals("0")){
					if(uploadEntity.getPack()==null){
						logger.info("批量提交时候 终端提供的批次号为空");
						this.writeErrorResult(entity, AppExceptionEnum.PARAM_IS_NULL.getCode(), AppExceptionEnum.PARAM_IS_NULL.getMessage(), resp, req);
						return;
					}
					boolean b=taskHandlerService.checkCountIsOut(uploadEntity.getUserName(), uploadEntity.getPack());
					if(!b){
						//提示弹出验证码
						logger.info("提示客户端弹出验证码 用户名["+uploadEntity.getUserName()+"]");
						this.writeErrorResult(entity, AppExceptionEnum.SECURECODE_NEED.getCode(), AppExceptionEnum.SECURECODE_NEED.getMessage(), resp, req);
						return;
					}
				}else{
					boolean b=taskHandlerService.checkUserIsTimeOut(uploadEntity.getUserName());
					if(!b){
						//提示弹出验证码
						logger.info("提示客户端弹出验证码 用户名["+uploadEntity.getUserName()+"]");
						this.writeErrorResult(entity, AppExceptionEnum.SECURECODE_NEED.getCode(), AppExceptionEnum.SECURECODE_NEED.getMessage(), resp, req);
						if (tokenId != null && (baseId == null || baseId.equals("")) && !isLocate.equals("1")) {
							logger.info("DELETE TOKEN ("+uploadEntity.getUserName()+")"+tokenId+" baseId:"+baseId+" isLocate="+isLocate);
							taskHandlerService.deleteAgentTaskTokenForSave(tokenId,uploadEntity.getUserName());
						}
						return;
					}
				}
				//验证码校验结束
				
				if(uploadEntity.getRecommend()!=null&&uploadEntity.getRecommend().equals("1")){
					logger.info("Allot TASK SAVE ID="+uploadEntity.getTokenId()+" user="+uploadEntity.getUserName());
				}else{
					uploadEntity.setRecommend("0");
				}
				
				
				
				if (uploadEntity.getImageId() == null || "".equals(uploadEntity.getImageId())) {
					logger.warn("终端没有传入图片流");
					// 修改为成功失败信息JSON格式统一模式
					this.writeErrorResult(entity, AppExceptionEnum.PARAM_IS_NULL.getCode(), AppExceptionEnum.PARAM_IS_NULL.getMessage(), resp, req);
					if (tokenId != null && (baseId == null || baseId.equals("")) && !isLocate.equals("1")) {
						logger.info("DELETE TOKEN ("+uploadEntity.getUserName()+")"+tokenId+" baseId:"+baseId+" isLocate="+isLocate);
						taskHandlerService.deleteAgentTaskTokenForSave(tokenId,uploadEntity.getUserName());
					}
					return;
					// throw new AppException(AppExceptionEnum.PA(baseId==null||isLocate.equals("0"))RAM_IS_NULL);
				}

				// 验证图片格式
				if (!this.validateImageGPG(uploadEntity.getImageId(),req)) {
					logger.warn("上传图片格式非GPG格式!");
					// 修改为成功失败信息JSON格式统一模式
					this.writeErrorResult(entity, AppExceptionEnum.IMAGE_TYPE_ERROR.getCode(), AppExceptionEnum.IMAGE_TYPE_ERROR.getMessage(), resp, req);
					if (tokenId != null && (baseId == null || baseId.equals("")) && !isLocate.equals("1")) {
						logger.info("DELETE TOKEN ("+uploadEntity.getUserName()+")"+tokenId+" baseId:"+baseId+" isLocate="+isLocate);
						taskHandlerService.deleteAgentTaskTokenForSave(tokenId,uploadEntity.getUserName());
					}
					return;
					// throw new AppException(AppExceptionEnum.IMAGE_TYPE_ERROR);
				}
				// 验证图片完整性
				if (uploadEntity.getMd5Validate() != null) {
					if (!this.checkImageMD5(uploadEntity.getImageId(), uploadEntity.getMd5Validate(),req)) {
						logger.warn("图片MD5验证不通过!");
						// 修改为成功失败信息JSON格式统一模式
						this.writeErrorResult(entity, AppExceptionEnum.MD5_VALIDATE_IMAGE_ERROR.getCode(), AppExceptionEnum.MD5_VALIDATE_IMAGE_ERROR.getMessage(), resp, req);
						if (tokenId != null && (baseId == null || baseId.equals("")) && !isLocate.equals("1")) {
							logger.info("DELETE TOKEN ("+uploadEntity.getUserName()+")"+tokenId+" baseId:"+baseId+" isLocate="+isLocate);
							taskHandlerService.deleteAgentTaskTokenForSave(tokenId,uploadEntity.getUserName());
						}
						return;
						// throw new AppException(AppExceptionEnum.MD5_VALIDATE_IMAGE_ERROR);
					}
				}
				// MD5强制校验 开始
				token.setImgId(uploadEntity.getImageId());
				if (!this.checkImageMD5(uploadEntity.getImageId(), token.getImgMd5(),req) || uploadEntity.getUserName() != null && token.getUserName() != null && !token.getUserName().equals(uploadEntity.getUserName()) || uploadEntity.getX() != null && token.getX() != null && !token.getX().equals(uploadEntity.getX()) || uploadEntity.getY() != null && token.getY() != null && !token.getY().equals(uploadEntity.getY())) {
					logger.info("CHECK TOKEN ERROR("+uploadEntity.getUserName()+")"+tokenId+"，疑似作弊，用户名称为 " + uploadEntity.getUserName() + " 任务名称 " + uploadEntity.getDataName());
					this.writeErrorResult(entity, AppExceptionEnum.TASK_VALIDATE_ERROR.getCode(), AppExceptionEnum.TASK_VALIDATE_ERROR.getMessage(), resp, req);
					token.setStatus(2);
					if (baseId != null) {
						token.setBaseId(new Long(baseId));

						/** 认定为作弊的任务，把该任务的状态置为2（标记为审核不通过），并且在搜索引擎中删除该数据 */
						try {
							taskHandlerService.relaseCheatTask(baseId,
									uploadEntity.getTaskId(), true,uploadEntity.getUserName(),uploadEntity.getRecommend());
						} catch (AppException e) {

						}
					}

					taskHandlerService.updateAgentTaskToken(token);

					return;
				}
				// 图片标记判断
				if (!FaskImgCheck.parseImage(this.getImgPathByImageId(uploadEntity.getImageId(),req))) {
					logger.info("CHECK TOKEN ERROR("+uploadEntity.getUserName()+")"+tokenId+"，疑似作弊，用户名称为 " + uploadEntity.getUserName() + "任务名称 " + uploadEntity.getDataName());
					this.writeErrorResult(entity, AppExceptionEnum.TASK_VALIDATE_ERROR.getCode(), AppExceptionEnum.TASK_VALIDATE_ERROR.getMessage(), resp, req);
					token.setStatus(2);
					if (baseId != null) {
						token.setBaseId(new Long(baseId));

						/** 认定为作弊的任务，把该任务的状态置为2（标记为审核不通过），并且在搜索引擎中删除该数据 */
						try {
							taskHandlerService.relaseCheatTask(baseId,
									uploadEntity.getTaskId(), true,uploadEntity.getUserName(),uploadEntity.getRecommend());
						} catch (AppException e) {

						}
					}

					taskHandlerService.updateAgentTaskToken(token);
					return;
				}

				// MD5强制校验 开始 结束
				// scoreId校验
				if(uploadEntity.getTaskId()!=null&&!uploadEntity.getTaskId().equals("")&&new Integer(uploadEntity.getTaskId())>0){
					if(scoreId==null||scoreId.equals("")||new Integer(scoreId)<1){
						scoreId="1";
						uploadEntity.setScoreId(scoreId);
					}
				}
				
				// 兼容版本
				String pointAcc = uploadEntity.getPointAccury();
				if (pointAcc == null || "".equals(pointAcc)) {
					uploadEntity.setPointAccury("0");
				}
				// 兼容版本
				String position = uploadEntity.getPosition();
				if (position == null || "".equals(position)) {
					uploadEntity.setPosition("-1");
				}

				
			    logger.info("开始数据入库");
				taskHandlerService.saveSubmitUploadTask(uploadEntity,token);
				

				entity.setDesc(BaseController.SUCCESS);
				
				
				
			} catch (AppException e) {
				// entity.setCode(e.getSqlExpEnum().getCode());
				// entity.setDesc(e.getSqlExpEnum().getMessage());
				// 修改为成功失败信息JSON格式统一模式
				// 删除之前图片
				this.removeImage(uploadEntity,req);

				this.writeErrorResult(entity, e.getSqlExpEnum().getCode(), e.getSqlExpEnum().getMessage(), resp, req);
				logger.error("上传图片服务发生异常,删除图片. taskId=" + uploadEntity.getTaskId() + ",imageId = " + uploadEntity.getImageId(), e);
				if (tokenId != null && (baseId == null || baseId.equals("")) && !isLocate.equals("1")) {
					logger.info("DELETE TOKEN ("+uploadEntity.getUserName()+")"+tokenId+" baseId:"+baseId+" isLocate="+isLocate);
					taskHandlerService.deleteAgentTaskTokenForSave(tokenId,uploadEntity.getUserName());
				}
				return;
			}catch (Exception e) {
				logger.error("====Unknown exception====", e);
				if (tokenId != null && (baseId == null || baseId.equals("")) && !isLocate.equals("1")) {
					logger.info("DELETE TOKEN ("+uploadEntity.getUserName()+")"+tokenId+" baseId:"+baseId+" isLocate="+isLocate);
					taskHandlerService.deleteAgentTaskTokenForSave(tokenId,uploadEntity.getUserName());
				}
				logger.info("OPERATION ERROR("+uploadEntity.getUserName()+")"+tokenId+"，疑似作弊，用户名称为 " + uploadEntity.getUserName() + "任务名称 " + uploadEntity.getDataName());
				this.writeErrorResult(entity, AppExceptionEnum.INSERT_AGENT_TASK_ERROR.getCode(), AppExceptionEnum.INSERT_AGENT_TASK_ERROR.getMessage(), resp, req);
				return;
			}
			// 输出结果
			this.writeResult(entity, req, resp, uploadEntity.getTerminaFlag());
			
		}
		
	}
	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected AgentTaskUploadEntity vaildJsonData(String content,ServletInfoBean obj){
		AgentTaskUploadEntity ae=(AgentTaskUploadEntity)obj;
		try {
			JSONObject json = JSONObject.fromObject(content);
			logger.info("获取的入参为"+json.toString());
			//防止参数重复错误
			String regex="\\[\".*\",\".*\"\\]";
			Pattern pattren=Pattern.compile(regex);
			Matcher mat = pattren.matcher(json.toString());
			if(mat.find()){
				ae.setErrorMessage("JSON格式解析错误");
				return  ae;
			}
			for(Object keys:json.keySet()){
				String key=keys.toString();
				String value=json.get(key).toString();
				if(!value.equals("")&&!value.equalsIgnoreCase("null")){
					key=key.substring(0,1).toUpperCase()+key.substring(1);
					Class clazz=ae.getClass();
					if(key.equals("PhotoTime")||key.equals("GpsTime")){
						Method m=clazz.getMethod("set"+key, new Class[]{Date.class});
						m.invoke(ae, new Object[]{DateUtil.convertStringToDate("yyyy-MM-dd HH:mm:ss", value)});
					}else{
						Method m=clazz.getMethod("set"+key, new Class[]{String.class});
						m.invoke(ae, new Object[]{value});
					}
					
				}
				
				
			}
			if(ae.getUserName()==null||ae.getUserName().equals("")){
				ae.setErrorMessage("JSON格式解析错误");
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			ae.setErrorMessage("JSON格式解析错误");
		} 
		
		return  ae;
	}
	private void uploadImage(AgentTaskUploadEntity taskUpload, HttpServletRequest req, MultipartFile item, String set) {

//		if (!ServletFileUpload.isMultipartContent(req)) {
//			AgentTaskUploadServlet.logger.warn("只能是 multipart/form-data 类型数据");
//			throw new AppException(AppExceptionEnum.PRASE_IMAGE_ERROR);
//		}

		try {
			
			if (item!=null&&!item.isEmpty()) {
					String path = FileUtil.getImagePath();
					String imgPath = this.getImgPath(path,req);
					String imgName = this.getImgName(path);
					logger.info("图片路径=" + imgPath);
					// String fileName = filePath.substring(filePath.lastIndexOf(File.separator) + 1);
					// fileName = fileName.replace(IMAGE_SUFFIX, "");
					taskUpload.setImageId(imgName);
					String file = this.getWholeImgPath(imgPath, imgName);
					// 生成图片
					this.write2Image(item, file);

			}
			// 验证form表单参数
			String result = this.valdateParam(taskUpload);
			if (result != null && !result.equals("")) {
				throw new AppException(201, "请求参数 【" + result + "】为空 或 不符合规范!");
			}
			// 自动获取ADCODE
			if (taskUpload.getAdCode() == null || taskUpload.getAdCode().equals("")) {
				taskUpload.setAdCode(this.getAdCode(taskUpload.getX(), taskUpload.getY(), true));
			}
		} catch (AppException ex) {
			throw ex;
		} catch (Exception e) {
			logger.error("图片解析异常,请重新提交", e);
			throw new AppException(AppExceptionEnum.PRASE_IMAGE_ERROR);
		}

	}
	private void write2Image(MultipartFile item, String filePath) {
		try {
			BufferedOutputStream outStream = new BufferedOutputStream(new FileOutputStream(new File(filePath)));
			InputStream in = item.getInputStream();
			int length = 0;
			byte[] buf = new byte[1024];
			logger.info(filePath+" 获取上传文件的大小：" + item.getSize());
			// in.read(buf) 每次读到的数据存放在 buf 数组中
			while ((length = in.read(buf)) != -1) {
				outStream.write(buf, 0, length);
			}
			in.close();
			outStream.flush();
			outStream.close();
			logger.info(filePath+" 上传成功");
		} catch (Exception e) {
			logger.warn("生成上传图片异常", e);
			throw new AppException(AppExceptionEnum.PRASE_IMAGE_ERROR);
		}
	}

	/**
	 * 201310/22/1/random
	 * 
	 * @param path
	 * @return www.xxx.com/201310/22/1
	 */
	private String getImgPath(String path,HttpServletRequest req) {
		String imgPath = path.substring(0, path.lastIndexOf(File.separator));
		String serverPath = req.getSession().getServletContext().getRealPath(AgentTaskUploadController.IMG_PATH);
		StringBuilder result = new StringBuilder();
		result.append(serverPath).append(File.separator).append(imgPath);
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
	private String getWholeImgPath(String imgPath, String imgName) {
		File file = new File(imgPath);
		if (!file.exists()) {
			synchronized (AgentTaskUploadController.class) {
				if (!file.exists()) {
					file.mkdirs();
				}
			}
		}
		StringBuilder path = new StringBuilder();
		path.append(imgPath).append(File.separator).append(imgName).append(AgentTaskUploadController.IMAGE_SUFFIX);
		return path.toString();
	}

	/**
	 * 201310_22_1_random
	 * 
	 * @param imageId
	 * @return
	 */
	private String getImgPathByImageId(String imageId,HttpServletRequest req) {
		String path = req.getSession().getServletContext().getRealPath(AgentTaskUploadController.IMG_PATH);
		if (StringUtils.isEmpty(imageId)) {
			logger.error("imageId参数不能为空");
			throw new AppException(AppExceptionEnum.PARAM_FORMAT_EXP);
		}

		String imgName = StringUtils.replace(imageId, "_", File.separator);
		String resultPath = imgName.substring(0, imgName.lastIndexOf(File.separator));
		StringBuilder imgPath = new StringBuilder(path);
		imgPath.append(File.separator).append(resultPath).append(File.separator).append(imageId);
		if (!imageId.endsWith(AgentTaskUploadController.IMAGE_SUFFIX)) {
			imgPath.append(AgentTaskUploadController.IMAGE_SUFFIX);
		}
		return imgPath.toString();
	}
	/**
	 * 非空验证
	 * 
	 * @param uploadEntity
	 * @return
	 */
	private String valdateParam(AgentTaskUploadEntity uploadEntity) {

		String taskType = uploadEntity.getTaskType();
		String dataName = uploadEntity.getDataName();
		String x = uploadEntity.getX();
		String y = uploadEntity.getY();
		String pointLevel = uploadEntity.getPointLevel();
		String pointType = uploadEntity.getPointType();
		String userName = uploadEntity.getUserName();
		// String enterPriseCode = uploadEntity.getEnterPriseCode();
		String adCode = uploadEntity.getAdCode();

		if (taskType == null || "".equals(taskType)) {
			return "taskType";
		}
		if (dataName == null || "".equals(dataName) || dataName.trim().length() == 0) {
			return "dataName";
		}

		if (x == null || "".equals(x)) {
			return "x";
		}
		if (y == null || "".equals(y)) {
			return "y";
		}
		if (userName == null || "".equals(userName)) {
			return "userName";
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
		if (adCode != null && !"".equals(adCode) && adCode.length() != 6) {
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
		return FileUtil.validateImage(this.getImgPathByImageId(imageId,req));
	}

	// md5验证
	private boolean checkImageMD5(String imageId, String md5Str,HttpServletRequest req) {
		// String path = getServletContext().getRealPath("/upload");
		// String filePath = path + File.separator + imageId + IMAGE_SUFFIX;
		String filePath = this.getImgPathByImageId(imageId,req);
		return FileUtil.checkImageFile(filePath, md5Str);
	}
	private void removeImage(AgentTaskUploadEntity uploadEntity,HttpServletRequest req) {
		if (uploadEntity != null && uploadEntity.getImageId() != null) {
			// String path = getServletContext().getRealPath("/upload");
			// String filePath = path + File.separator + uploadEntity.getImageId() + IMAGE_SUFFIX;
			String filePath = this.getImgPathByImageId(uploadEntity.getImageId(),req);
			logger.info("删除异常图片" + filePath);
			try {
				File file = new File(filePath);
				file.delete();
			} catch (Exception e) {
				logger.warn("删除图片异常@@@@不能影响业务!");
			}
		}
	}

}

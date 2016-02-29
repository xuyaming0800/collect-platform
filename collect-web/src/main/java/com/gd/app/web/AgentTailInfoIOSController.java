package com.gd.app.web;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.zip.GZIPInputStream;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.gd.app.entity.AgentTailInfo;
import com.gd.app.entity.ResultEntity;
import com.gd.app.exception.AppException;
import com.gd.app.exception.AppExceptionEnum;
import com.gd.app.service.AgentTailInfoService;

/**
 * 轨迹上报 IOS端
 * @author ziyu.wei
 *
 */
@Controller
public class AgentTailInfoIOSController extends AgentTainInfoController {
	
	private @Resource AgentTailInfoService service;
	
	private Logger logger = LogManager.getLogger(AgentTailInfoIOSController.class);
	
	@RequestMapping("/userTrailIOS")
	public void userTrail(HttpServletRequest request,
			HttpServletResponse response) {
		logger.info("IOS端客户端调用保存轨迹信息");
		//request.setCharacterEncoding("utf-8");
		ResultEntity entity = new ResultEntity();
		
		String agentTailInfoString = null;
		try {
			agentTailInfoString = parseFromFileToString(request);
		} catch (AppException e) {
			logger.error("", e);
			writeError(entity, request, response, e);
		}
		
		logger.info("解压后得数据: " + agentTailInfoString);

		List<AgentTailInfo> list = null;
		
		try {
			list = parseMessage(agentTailInfoString, entity);
		} catch (AppException e) {
			logger.error("解析数据有误", e);
			writeError(entity, request, response, e);
			return;
		}
		
		if (null == list) {
			logger.info("数据为空");
			this.writeError(entity, request, response, new AppException(1, "数据为空"));
			return;
		}

		try {
			service.batchSaveAgentTailInfos(list);

			// 数据保存成功，处理返回结果
			entity.setCode("0");
			entity.setDesc("success");
			writeResult(entity, request, response);
			
			logger.info("数据保存成功");
		} catch (Exception e) {
			// e.printStackTrace();
			logger.error("轨迹信息保存失败", e);
			// 处理错误后返回结果
			entity.setCode("1");
			writeErrorResult(entity,
					AppExceptionEnum.TAIL_SAVE_ERROR.getCode(),
					AppExceptionEnum.TAIL_SAVE_ERROR.getMessage(), response,
					request);
		}
	}
	
	/**
	 * 解析上传文件
	 * @param request
	 * @return
	 * @throws AppException
	 */
	private String parseFromFileToString(HttpServletRequest request) throws AppException{
		DiskFileItemFactory factory = new DiskFileItemFactory();
		
		ServletFileUpload upload = new ServletFileUpload(factory);
		
		if (! ServletFileUpload.isMultipartContent(request)) {
			logger.info("不是文件上传");
			throw new AppException(1, "没有上传的文件");
		}
		
		try {
			List<FileItem> fileitems = upload.parseRequest(request);
			
			String datamd5 = null;
			for (FileItem item: fileitems) {
				if (item.isFormField()) {
					String field = item.getFieldName();
					if ("datamd5".equals(field)) {
						datamd5 = item.getString();
					}
				}
			}
			
			logger.info("datamd5:" + datamd5);
			if (null == datamd5) {
				throw new AppException(1, "没有MD5码");
			}
			
			StringBuffer sb = new StringBuffer();
			
			for (FileItem item: fileitems) {
				if (! item.isFormField()) {
					String res = parseInputStream(item, datamd5);
					if (null != res) {
						sb.append(res);
					} else {
						logger.info("解析该文件返回的信息是null");
					}
				}
				
			}
			
			return sb.toString();
		} catch (FileUploadException e) {
			logger.error("文件上传失败", e);
			throw new AppException(1, "文件上传失败");
		}
		
	}
	
	
	private String getMd5(String str) throws NoSuchAlgorithmException {
		MessageDigest md = MessageDigest.getInstance("MD5");
		
		 byte[] result = md.digest(str.getBytes());
		 StringBuilder stringBuilder = new StringBuilder();
         for (int i = 0; i < result.length; i++) {
             byte now = result[i];
             String hex = Integer.toHexString(now & 0xff);
             if (hex.length() < 2) {
                 stringBuilder.append("0");
             }
             stringBuilder.append(hex);
         }
         return stringBuilder.toString();
	}
	
	/**
	 * 解析流
	 * @param fileItem
	 * @return
	 * @throws AppException
	 */
	private String parseInputStream(FileItem fileItem, String datamd5) throws AppException{
		BufferedReader br = null;
		InputStreamReader isr = null;
		InputStream is = null;
		try {
			is = fileItem.getInputStream();
			
			/*is.mark(-1);
			if (! validataMD5(is, datamd5)) {
				logger.info("MD5校验失败");
				throw new AppException(1, "MD5校验失败");
			}
			is.reset();*/
			
			logger.info("解析压缩文件");
			
			br = new BufferedReader(new InputStreamReader(new GZIPInputStream(is), "UTF-8"));
			
			StringBuffer sb = new StringBuffer();
			String line = null;
			
			while ((line = br.readLine()) != null) {
				sb.append(line);
			}
			
			logger.info("解析该文件得到的信息是: " + sb);
			
			String md5 = getMd5(sb.toString());
			
			logger.info("本地MD5加密后：" + md5);
			
			if (! md5.equalsIgnoreCase(datamd5)) {
				throw new AppException(1, "MD5校验失败");
			}
			
			return sb.toString();
		} catch (Exception e) {
			//e.printStackTrace();
			logger.error("解析流失败", e);
		} finally {
			if (null != is) {
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
			if (null != isr) {
				try {
					isr.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
			if (null != br) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		return null;
	}

}

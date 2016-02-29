package com.gd.app.web;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.lang.time.DateUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import autonavi.online.framework.property.PropertiesConfig;
import autonavi.online.framework.property.PropertiesConfigUtil;

import com.gd.app.desdata.BaseEnc;
import com.gd.app.desdata.BaseEncNew;
import com.gd.app.desdata.BaseEncOld;
import com.gd.app.desdata.EncWrapperOld;
import com.gd.app.entity.ResultEntity;
import com.gd.app.entity.ScoreLevel;
import com.gd.app.entity.ServletInfoBean;
import com.gd.app.entity.TaskAppEntity;
import com.gd.app.exception.AppException;
import com.gd.app.exception.AppExceptionEnum;
import com.gd.app.service.TaskUtilService;
import com.gd.app.util.SysProps;

public abstract class BaseController {
	private Logger logger = LogManager.getLogger(this.getClass());
	protected static final String SUCCESS = "success";
	protected static long deadLineTimeOld = -1;
	protected static long deadLineTimeNew = -1;
	private PropertiesConfig pc=null;
	@Resource
	private TaskUtilService taskUtilService;
	
	private static String flag=null;
    /**
     * 回写token_id加密的方式
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
	protected String getBizProperty(String key)throws Exception{
		if(pc==null){
			pc=PropertiesConfigUtil.getPropertiesConfigInstance();
		}
		return (String)pc.getProperty(key);
	}
	
    /**
     * 校验加密算法的合法性
     * @param req
     * @param resp
     * @param obj
     * @param logger
     * @param items
     * @return
     */
	protected int checkCryptorVersion(HttpServletRequest req,
			HttpServletResponse resp, ServletInfoBean obj, Logger logger,
			List<FileItem> items) {
		if(flag==null){
			try {
				deadLineTimeOld=DateUtils
				.parseDate(
						getBizProperty(
										SysProps.PROP_DEAD_LINE_TIME_OLD)
								.toString(),
						new String[] { "yyyy-MM-dd HH:mm:ss" }).getTime();
				deadLineTimeNew=DateUtils
						.parseDate(
								getBizProperty(
												SysProps.PROP_DEAD_LINE_TIME_NEW)
										.toString(),
								new String[] { "yyyy-MM-dd HH:mm:ss" }).getTime();
			} catch (Exception e) {
				e.printStackTrace();
				return 3;
			}
			flag="";
		}
		// 启用全新的content字段
		String content = req.getParameter("content");
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
			obj = this.vaildJsonData(BaseEncNew.disp_decode(content, "utf-8"),
					obj);
			if (obj.getErrorMessage() != null) {
				boolean isError = false;
				// 兼容今后换加密算法的情况
				if (date.getTime() < deadLineTimeNew) {
					obj = this.vaildJsonData(
							BaseEnc.disp_decode(content, "utf-8"), obj);
					if (obj.getErrorMessage() != null) {
						isError = true;
					}
				} else {
					isError = true;
				}
				if (isError) {
					logger.info("请求参数 【" + obj.getErrorMessage() + "】为空!");
					return 1;
				}
				return -1;
			}
			return 0;
		}

	}

	protected abstract ServletInfoBean vaildJsonData(String content, ServletInfoBean obj);
	
	public void writeError(ResultEntity entity, HttpServletRequest req,  HttpServletResponse resp, AppException e) {
        entity.setCode(e.getSqlExpEnum().getCode());
        entity.setDesc(e.getSqlExpEnum().getMessage());
        writeResult(entity, req, resp);
    }

	protected void writeErrorResult(ResultEntity entity, String errorCode,
			String result, HttpServletResponse resp, HttpServletRequest req) {
		entity.setCode(errorCode);
		// entity.setDesc(BaseServlet.FAILURE_CN);
		entity.setDesc(result);
		entity.setResultData(result);
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
		Object data = entity.getResultData();
		PrintWriter out = null;
		boolean jsonP = false;

		// 返回json结果
		JSONObject jsonResult = new JSONObject();
		// 错误对象
		JSONObject errObj = new JSONObject();
		errObj.accumulate("code", entity.getCode());
		errObj.accumulate("msg", entity.getDesc());
		jsonResult.accumulate("status", errObj);

		if (data instanceof Map) {
			JSONObject obj = JSONObject.fromObject(data);
			jsonResult.accumulate("result", obj);
		} else {
			jsonResult.accumulate("result", data);
		}

		if (entity.getTotalCount() != null) {
			jsonResult.accumulate("totalCount", entity.getTotalCount());
		}
		if (entity.getEnterPriseName() != null) {
			jsonResult.accumulate("enterPriseName", entity.getEnterPriseName());
		}
		if (entity.getTotalScore() != null) {
			jsonResult.accumulate("totalScore", entity.getTotalScore());
		}
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
	 * @param x
	 * @param y
	 * @param logger
	 * @return
	 */
	protected boolean valieateXY(String x, String y,Logger logger) {
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
	
	protected String[] getOffsetXY(String x,String y){
    	
		String[] offsetList =taskUtilService.offsetXY(x, y);
		if (offsetList.length == 2) {
			return offsetList;
		}else{
			return null;
		}
    }
	/**
     * 获取ADCODE
     * @param x
     * @param y
     * @param isGPS
     * @return
     */
    protected String getAdCode(String x,String y,boolean isGPS){
    	if (isGPS) {
			String[] offsetList = taskUtilService.offsetXY(x, y);
			if (offsetList.length == 2) {
				x = offsetList[0];
				y = offsetList[1];
			}
		}
        double corrX = Double.parseDouble(x);
		double corrY = Double.parseDouble(y);
		String adCode =taskUtilService.fetchAdcode(corrX, corrY);
		if (adCode == null || "".equals(adCode) || (adCode.length() != 6)) {
			throw new AppException(AppExceptionEnum.FETCH_ADCODE_ERROR);
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
    public void writeResult(ResultEntity entity, HttpServletRequest req,  HttpServletResponse resp, String termainFlag) {
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
protected void generateTaskAppEntity(List<TaskAppEntity> l ){
    	
    	if(l!=null){
    		List<ScoreLevel> l1=ScoreLevelInit.scoreLevels;
        	for(TaskAppEntity obj:l){
        		if(obj.getScore().equals("-1")){
        			for(ScoreLevel s:l1){
        				if(s.getIsPassive().equals("1")&&s.getStatus().equals("0")&&s.getMin()==-1){
        					obj.setScore(s.getPrice()+"");
        					obj.setScoreId(s.getId().toString());
        					break;
        				}
        			}
        		}else{
        			Long count=new Long(obj.getScore());
        			boolean b=false;
        			for(ScoreLevel s:l1){
        				if(s.getIsPassive().equals("1")&&s.getStatus().equals("0")&&count>=s.getMin()&&count<=s.getMax()){
        					obj.setScore(s.getPrice()+"");
        					obj.setScoreId(s.getId().toString());
        					b=true;
        					break;
        				}
        			}
        			if(!b){
        				for(ScoreLevel s:l1){
            				if(s.getIsPassive().equals("1")&&s.getStatus().equals("0")&&s.getMin()==-1){
            					obj.setScore(s.getPrice()+"");
            					obj.setScoreId(s.getId().toString());
            					break;
            				}
            			}
        			}
        		}
        	}
    	}
		
    }

}

package cn.dataup.mgr.web.controller;

import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import autonavi.online.framework.util.json.JsonBinder;
import cn.dataup.mgr.web.bean.ResultEntity;

import com.autonavi.collect.exception.BusinessException;
import com.autonavi.collect.exception.BusinessRunException;

public class BaseController {
	private Logger logger = LogManager.getLogger(this.getClass());
	
	protected void writeError(ResultEntity entity, HttpServletRequest req,
			HttpServletResponse resp, BusinessException e) {
		entity.setCode(e.getSqlExpEnum().getCode());
		entity.setMsg(e.getSqlExpEnum().getMessage());
		writeResult(entity, req, resp);
	}

	protected void writeError(ResultEntity entity, HttpServletRequest req,
			HttpServletResponse resp, BusinessRunException e) {
		entity.setCode(e.getSqlExpEnum().getCode());
		entity.setMsg(e.getSqlExpEnum().getMessage());
		writeResult(entity, req, resp);
	}

	protected void writeErrorResult(ResultEntity entity, String errorCode,
			String result, HttpServletResponse resp, HttpServletRequest req) {
		entity.setCode(errorCode);
		entity.setMsg(result);
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

	protected void writeResult(ResultEntity entity, HttpServletRequest req,
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

}

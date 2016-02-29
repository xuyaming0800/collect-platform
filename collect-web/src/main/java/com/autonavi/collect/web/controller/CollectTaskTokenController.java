package com.autonavi.collect.web.controller;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.autonavi.collect.bean.CollectTaskToken;
import com.autonavi.collect.entity.CollectTokenCheckEntity;
import com.autonavi.collect.exception.BusinessExceptionEnum;
import com.autonavi.collect.exception.BusinessRunException;
import com.autonavi.collect.service.TaskCollectUtilService;
import com.autonavi.collect.service.TaskTokenService;
import com.autonavi.collect.web.bean.CheckDataInfoBean;
import com.autonavi.collect.web.bean.ResultDesc;
import com.autonavi.collect.web.bean.ResultEntity;


@Controller
public class CollectTaskTokenController extends BaseController<CollectTaskToken> {
	public CollectTaskTokenController() throws Exception {
		super();
		// TODO Auto-generated constructor stub
	}

	private Logger logger = LogManager.getLogger(this.getClass());
	@Autowired
	private TaskTokenService taskTokenService;
	@Autowired
	private TaskCollectUtilService taskCollectUtilService;

	@RequestMapping("/saveToken")
	public void saveToken(HttpServletRequest request,
			HttpServletResponse response) {
		CollectTaskToken token = new CollectTaskToken();
		int check = this.checkCryptorVersion(request, response, token, logger,
				null);
		ResultEntity entity = new ResultEntity();
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
				this.writeErrorResult(entity,
						BusinessExceptionEnum.TASK_TOKEN_ERROR.getCode(),
						BusinessExceptionEnum.TASK_TOKEN_ERROR.getMessage(),
						response, request);
				return;
			}
		} else if (check == -2) {
			this.writeErrorResult(entity, "201", "参数无法解析", response, request);
			return;
		} else {
			String agent = request.getHeader("user-agent");

			if (agent != null) {
				token.setHeader(agent);
			}
			token.setIp(getIpAddr(request));
			try {
				if ((token.getUserId() != null && !token.getUserId().toString()
						.equals(""))
						&& (token.getAttachmentMd5() != null && !token.getAttachmentMd5()
								.equals(""))
//						&& (token.getTokenX() != null && !token.getTokenX().equals(""))
//						&& (token.getTokenY() != null && !token.getTokenY().equals(""))
						) {
					CollectTokenCheckEntity _entity=taskTokenService.addAgentTaskToken(token);
					token.setToken(_entity.getToken());
					logger.info("用户 " + token.getUserId()
							+ " 获取Token成功 Token为" + token.getToken());
					ResultDesc desc=new ResultDesc();
					desc.setCode("0");
					desc.setMsg(this.cryptorTokenId(check, token.getToken()));
					entity.setStatus(desc);
					entity.setIsNewToken(_entity.getIsNew());

				} else {
					logger.info("用户 " + token.getUserId() + " 疑似攻击 IP="
							+ token.getIp());
					this.writeErrorResult(entity,
							BusinessExceptionEnum.TASK_TOKEN_ERROR.getCode(),
							BusinessExceptionEnum.TASK_TOKEN_ERROR.getMessage(),
							response, request);
					return;
				}

				// entity.setDesc(tokens.getId().toString());

			} catch (BusinessRunException e) {
				logger.info("用户 " + token.getUserId() + " 获取Token失败 原因为"
						+ e.getSqlExpEnum().getMessage());
				this.writeErrorResult(entity, e.getSqlExpEnum().getCode(), e
						.getSqlExpEnum().getMessage(), response, request);
				return;
			} catch (Exception e) {
				this.writeErrorResult(entity,
						BusinessExceptionEnum.TASK_TOKEN_ERROR.getCode(),
						BusinessExceptionEnum.TASK_TOKEN_ERROR.getMessage(), response,
						request);
				return;
			}
			this.writeResult(entity, request, response);
		}
	}

	protected  CheckDataInfoBean vaildJsonData(String content, CollectTaskToken token) {
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
			if (obj.get("user") != null
					&& !obj.get("user").toString().equals("")
					&& !obj.get("user").toString().equalsIgnoreCase("null")) {
				String user=obj.get("user").toString();
				token.setUserId(taskCollectUtilService.getUserIdCache(user));
			} else {
				bean.setErrorMessage("user参数为空");
				return bean;
			}
			if (obj.get("md5") != null && !obj.get("md5").toString().equals("")
					&& !obj.get("md5").toString().equalsIgnoreCase("null")) {
				token.setAttachmentMd5(obj.get("md5").toString());
			} else {
				bean.setErrorMessage("md5参数为空");
				return bean;
			}
			//业主-品类
			if (obj.get("ownerId") != null && !obj.get("ownerId").toString().equals("")
					&& !obj.get("ownerId").toString().equalsIgnoreCase("null")) {
				token.setOwnerId(Long.valueOf(obj.get("ownerId").toString()));
			}else{
				logger.warn("暂时兼容广告拍拍! 设置为1");
				token.setOwnerId(1L);
			}
//			if (obj.get("x") != null && !obj.get("x").toString().equals("")
//					&& !obj.get("x").toString().equalsIgnoreCase("null")) {
//				token.setTokenX(new Double(obj.get("x").toString()));
//			} else {
//				bean.setErrorMessage("x参数为空");
//				return bean;
//			}
//			if (obj.get("y") != null && !obj.get("y").toString().equals("")
//					&& !obj.get("y").toString().equalsIgnoreCase("null")) {
//				token.setTokenY(new Double(obj.get("y").toString()));
//			} else {
//				bean.setErrorMessage("y参数为空");
//				return bean;
//			}
			// if(!valieateXY(token.getX(),token.getY())){
			// token.setErrorMessage("X,Y坐标格式错误");
			// return token;
			// }
		} catch (Exception e) {
			// TODO Auto-generated catch block
			bean.setErrorMessage("JSON数据格式错误");
		}
		return bean;

    }
}


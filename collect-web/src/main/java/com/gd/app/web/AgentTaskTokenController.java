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
import com.gd.app.entity.ServletInfoBean;
import com.gd.app.exception.AppException;
import com.gd.app.exception.AppExceptionEnum;
import com.gd.app.service.TaskHandlerService;

@Controller
public class AgentTaskTokenController extends BaseController {
	private Logger logger = LogManager.getLogger(this.getClass());
	@Resource
	private TaskHandlerService taskHandlerService;

	@RequestMapping("/saveToken")
	public void saveToken(HttpServletRequest request,
			HttpServletResponse response) {
		AgentTaskToken token = new AgentTaskToken();
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
				logger.info("用户 " + token.getUserName() + "疑似作弊IP=["
						+ this.getIpAddr(request) + "]");
				this.writeErrorResult(entity, "201", "参数无法解析", response,
						request);
				return;
			}
			if (check == 2) {
				String user = request.getParameter("user");
				logger.info("用户 [" + user + "]疑似作弊 IP=["
						+ this.getIpAddr(request) + "]");
				this.writeErrorResult(entity,
						AppExceptionEnum.TASK_TOKEN_ERROR.getCode(),
						AppExceptionEnum.TASK_TOKEN_ERROR.getMessage(),
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
				if ((token.getUserName() != null && !token.getUserName()
						.equals(""))
						&& (token.getImgMd5() != null && !token.getImgMd5()
								.equals(""))
						&& (token.getX() != null && !token.getX().equals(""))
						&& (token.getY() != null && !token.getY().equals(""))) {
					token=taskHandlerService.addAgentTaskToken(token);
					logger.info("用户 " + token.getUserName()
							+ " 获取Token成功 TokenId为" + token.getTokenId());
					entity.setCode("0");
					entity.setDesc(this.cryptorTokenId(check, token.getTokenId()));

				} else {
					logger.info("用户 " + token.getUserName() + " 疑似攻击 IP="
							+ token.getIp());
					this.writeErrorResult(entity,
							AppExceptionEnum.TASK_TOKEN_ERROR.getCode(),
							AppExceptionEnum.TASK_TOKEN_ERROR.getMessage(),
							response, request);
					return;
				}

				// entity.setDesc(tokens.getId().toString());

			} catch (AppException e) {
				logger.info("用户 " + token.getUserName() + " 获取Token失败 原因为"
						+ e.getSqlExpEnum().getMessage());
				this.writeErrorResult(entity, e.getSqlExpEnum().getCode(), e
						.getSqlExpEnum().getMessage(), response, request);
				return;
			} catch (Exception e) {
				this.writeErrorResult(entity,
						AppExceptionEnum.TASK_TOKEN_ERROR.getCode(),
						AppExceptionEnum.TASK_TOKEN_ERROR.getMessage(), response,
						request);
				return;
			}
			this.writeResult(entity, request, response);
		}
	}

	protected AgentTaskToken vaildJsonData(String content, ServletInfoBean objs) {
		AgentTaskToken token = (AgentTaskToken) objs;
		try {
			JSONObject obj = JSONObject.fromObject(content);
			logger.info("获取的入参为" + obj.toString());
			// 防止参数重复错误
			String regex = "\\[\".*\",\".*\"\\]";
			Pattern pattren = Pattern.compile(regex);
			Matcher mat = pattren.matcher(obj.toString());
			if (mat.find()) {
				token.setErrorMessage("JSON格式解析错误");
				return token;
			}
			if (obj.get("user") != null
					&& !obj.get("user").toString().equals("")
					&& !obj.get("user").toString().equalsIgnoreCase("null")) {
				token.setUserName(obj.get("user").toString());
			} else {
				token.setErrorMessage("user参数为空");
				return token;
			}
			if (obj.get("md5") != null && !obj.get("md5").toString().equals("")
					&& !obj.get("md5").toString().equalsIgnoreCase("null")) {
				token.setImgMd5(obj.get("md5").toString());
			} else {
				token.setErrorMessage("md5参数为空");
				return token;
			}
			if (obj.get("x") != null && !obj.get("x").toString().equals("")
					&& !obj.get("x").toString().equalsIgnoreCase("null")) {
				token.setX(obj.get("x").toString());
			} else {
				token.setErrorMessage("x参数为空");
				return token;
			}
			if (obj.get("y") != null && !obj.get("y").toString().equals("")
					&& !obj.get("y").toString().equalsIgnoreCase("null")) {
				token.setY(obj.get("y").toString());
			} else {
				token.setErrorMessage("y参数为空");
				return token;
			}
			// if(!valieateXY(token.getX(),token.getY())){
			// token.setErrorMessage("X,Y坐标格式错误");
			// return token;
			// }
		} catch (Exception e) {
			// TODO Auto-generated catch block
			token.setErrorMessage("JSON数据格式错误");
		}
		return token;
	}

}

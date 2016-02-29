package com.gd.app.web;

import java.awt.image.BufferedImage;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.gd.app.entity.ResultEntity;
import com.gd.app.entity.SecureCode;
import com.gd.app.entity.ServletInfoBean;
import com.gd.app.exception.AppException;
import com.gd.app.exception.AppExceptionEnum;
import com.gd.app.service.TaskHandlerService;
import com.gd.app.service.TaskUtilService;

/**
 * 验证码相关controller
 * 
 * @author yaming.xu
 * 
 */
@Controller
public class AgentSecureCodeController extends BaseController {
	private Logger logger = LogManager.getLogger(this.getClass());
	@Resource
	private TaskHandlerService taskHandlerService;
	@Resource
	private TaskUtilService taskUtilService;

	@RequestMapping("/getSecureCode")
	public void processSecureCode(HttpServletRequest req,
			HttpServletResponse resp) {
		SecureCode sc = new SecureCode();
		ResultEntity entity = new ResultEntity();
		int check = this.checkCryptorVersion(req, resp, sc, logger, null);
		if (check > 0) {
			if (check == 3) {
				this.writeErrorResult(entity, "401", "系统内部异常", resp, req);
				return;
			}
			if (check == 1) {
				logger.info("用户 疑似作弊IP=[" + this.getIpAddr(req) + "]");
				this.writeErrorResult(entity, "201", "无法解析全部参数", resp, req);
				return;
			}
			if (check == 2) {
				String user = req.getParameter("userName");
				logger.info("用户 [" + user + "]疑似作弊 IP=[" + this.getIpAddr(req)
						+ "]");
				this.writeErrorResult(entity, "405", "任务非法", resp, req);
				return;
			}
			return;
		} else if (check == -2) {
			logger.info("用户疑似作弊IP=[" + this.getIpAddr(req) + "]");
			this.writeErrorResult(entity, "201", "无法解析全部参数", resp, req);
			return;
		} else {
			// 获取
			if (sc.getCode() == null) {
				try {
					Map<String,Object> m=taskUtilService.generateSecureCode();
					String code = (String)m.get("code");
					BufferedImage image=(BufferedImage)m.get("img");
					ImageIO.write(image, "JPEG", resp.getOutputStream());
					if (sc.getBatch().equals("0")) {
						taskHandlerService.saveSecureCodeForBatch(
								sc.getUserName(), code);
					} else {
						taskHandlerService.saveSecureCode(sc.getUserName(),
								code);
					}

				} catch (Exception e) {
					// TODO Auto-generated catch block
					entity.setCode(AppExceptionEnum.SECURECODE_GEN_ERROR
							.getCode());
					entity.setDesc(AppExceptionEnum.SECURECODE_GEN_ERROR
							.getMessage());
					this.writeError(entity, req, resp, new AppException(
							AppExceptionEnum.SECURECODE_GEN_ERROR));
				}
			} else {
				// 校验
				try {
					boolean b = false;
					String flag = "";
					if (sc.getBatch().equals("0")) {
						flag = taskHandlerService.checkSecureCodeForBatch(
								sc.getUserName(), sc.getCode());
						if (!flag.equals("")) {
							entity.setCode("0");
							entity.setDesc(flag);

						} else {
							entity.setCode(AppExceptionEnum.SECURECODE_INVALID
									.getCode());
							entity.setDesc(AppExceptionEnum.SECURECODE_INVALID
									.getMessage());
							this.writeError(
									entity,
									req,
									resp,
									new AppException(
											AppExceptionEnum.SECURECODE_INVALID));
							return;
						}
					} else {
						b = taskHandlerService.checkSecureCode(
								sc.getUserName(), sc.getCode());
						if (b) {
							entity.setCode("1");
						} else {
							entity.setCode(AppExceptionEnum.SECURECODE_INVALID
									.getCode());
							entity.setDesc(AppExceptionEnum.SECURECODE_INVALID
									.getMessage());
							this.writeError(
									entity,
									req,
									resp,
									new AppException(
											AppExceptionEnum.SECURECODE_INVALID));
							return;
						}
					}
					this.writeResult(entity, req, resp);
				} catch (Exception e) {
					entity.setCode(AppExceptionEnum.PAGE_QUERY_ERROR.getCode());
					entity.setDesc(AppExceptionEnum.PAGE_QUERY_ERROR
							.getMessage());
					this.writeError(entity, req, resp, new AppException(
							AppExceptionEnum.PAGE_QUERY_ERROR));
					return;
				}
			}
		}

	}

	@Override
	protected ServletInfoBean vaildJsonData(String content, ServletInfoBean obj) {
		SecureCode save=(SecureCode)obj;
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
			if(json.get("userName")!=null&&!json.get("userName").toString().equals("")&&!json.get("userName").toString().equalsIgnoreCase("null")){
				save.setUserName(json.get("userName").toString());
			}else{
				save.setErrorMessage("userName");
				return save;
			}
			if(json.get("code")!=null&&!json.get("code").toString().equals("")&&!json.get("code").toString().equalsIgnoreCase("null")){
				save.setCode(json.get("code").toString());
			}
			if(json.get("batch")!=null&&!json.get("batch").toString().equals("")&&!json.get("batch").toString().equalsIgnoreCase("null")){
				save.setBatch(json.get("batch").toString());
			}else{
				save.setBatch("1");
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			save.setErrorMessage("JSON数据格式错误");
		}
		return save;
	}

}

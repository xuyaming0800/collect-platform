package cn.dataup.mgr.web.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import cn.dataup.mgr.web.bean.ResultEntity;
import cn.dataup.mgr.web.component.PropertityUtilComponent;
import cn.dataup.mgr.web.constant.WebConstant;

import com.autonavi.collect.exception.BusinessException;
import com.autonavi.collect.exception.BusinessRunException;
import com.autonavi.collect.mgr.entity.SimpleUserInfo;
import com.autonavi.collect.service.CollectMgrUtilService;

@Controller
public class IndexController extends BaseController {
	private Logger logger = LogManager.getLogger(this.getClass());

	@Autowired
	private CollectMgrUtilService collectMgrUtilService;
	@Autowired
	private PropertityUtilComponent propertityUtilComponent;
	
	@RequestMapping("/login")
	public void userLogin(@RequestParam("userName") String userName,
			@RequestParam("password") String password,HttpServletRequest request,
			HttpServletResponse response) {
		ResultEntity entity = new ResultEntity();
		try {
			//暂时简单处理
			if(userName!=null&&userName.equals("admin")&&password!=null&&password.equals("sjlm#2015")){
				SimpleUserInfo info=new SimpleUserInfo();
				info.setUserName("admin");
				info.setId(1L);
				//放入redis缓存Session
				logger.info("sessionid="+request.getSession().getId());
				collectMgrUtilService.setUserInfoToSession(propertityUtilComponent.getBizProperty(WebConstant.COLLECT_MGR_USER_SESSION)+request.getSession().getId(),info,
						Integer.valueOf(propertityUtilComponent.getBizProperty(WebConstant.SESSION_TIME_OUT)));
				entity.setMsg("登陆成功");
				entity.setCode("0");
			}else{
				entity.setMsg("登陆失败-用户名或者密码错误");
				entity.setCode("1");
			}
		} catch (BusinessRunException e) {
			this.writeErrorResult(entity, e.getSqlExpEnum().getCode(), e.getSqlExpEnum().getMessage(), response, request);
			return;
		} catch (BusinessException e) {
			this.writeErrorResult(entity, e.getSqlExpEnum().getCode(), e.getSqlExpEnum().getMessage(), response, request);
			return;
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			this.writeErrorResult(entity, "-1", e.getMessage(), response, request);
			return;
		}
		this.writeResult(entity, request, response);
	}
	@RequestMapping("/logout")
	public String userLogout(HttpServletRequest res){
		try {
			collectMgrUtilService.releaseUserInfoToSession(propertityUtilComponent.getBizProperty(WebConstant.COLLECT_MGR_USER_SESSION)+res.getSession().getId(), SimpleUserInfo.class);
		} catch (BusinessRunException e) {
			logger.error(e.getMessage(),e);
		} catch (BusinessException e) {
			logger.error(e.getMessage(),e);
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
		return "login";
	}

}

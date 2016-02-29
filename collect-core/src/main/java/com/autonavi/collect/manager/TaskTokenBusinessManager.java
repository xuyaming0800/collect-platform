package com.autonavi.collect.manager;

import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import autonavi.online.framework.constant.Miscellaneous;

import com.autonavi.collect.bean.CollectTaskToken;
import com.autonavi.collect.business.CollectCore;
import com.autonavi.collect.constant.CommonConstant;
import com.autonavi.collect.dao.CollectTaskTokenDao;
import com.autonavi.collect.entity.CollectTokenCheckEntity;
import com.autonavi.collect.exception.BusinessException;
import com.autonavi.collect.exception.BusinessExceptionEnum;

/**
 * Token相关核心方法管理类
 * @author xuyaming
 *
 */
@Component
public class TaskTokenBusinessManager {
	private Logger logger = LogManager.getLogger(this.getClass());
	@Autowired
	private CollectTaskTokenDao collectTaskTokenDao=null;
	@Autowired
	private ApplyTaskToken applyTaskToken;
	
	@Autowired
	private CheckTaskToken checkTaskToken;
	@Autowired
	private CheckTokenMd5 checkTokenMd5;
	
	@Autowired
	private UpdateTaskTokenStatus updateTaskTokenStatus;
	
	
	
	
	public UpdateTaskTokenStatus getUpdateTaskTokenStatus() {
		return updateTaskTokenStatus;
	}
	public CheckTaskToken getCheckTaskToken() {
		return checkTaskToken;
	}
	public ApplyTaskToken getApplyTaskToken() {
		return applyTaskToken;
	}


	public CheckTokenMd5 getCheckTokenMd5() {
		return checkTokenMd5;
	}


	/**
	 * 申请Token
	 * @author xuyaming
	 *
	 */
	@Component
	public class ApplyTaskToken implements CollectCore<String,CollectTaskToken>{
		@Autowired
		public ApplyTaskToken(TaskTokenBusinessManager taskTokenBusinessManager){
			
		}

		@Override
		public String execute(CollectTaskToken token) throws BusinessException {
			if(token==null){
				throw new BusinessException(BusinessExceptionEnum.TASK_TOKEN_INFO_NULL);
			}
			token.setToken(UUID.randomUUID().toString()+"_"+Miscellaneous.getMyid());
			collectTaskTokenDao.saveTaskToken(token);
			return token.getToken();
		}
		
	}
	/**
	 * 检测Token合法性
	 * @author xuyaming
	 *
	 */
	@Component
	public class CheckTaskToken implements CollectCore<Boolean,CollectTaskToken>{
		@Autowired
		public CheckTaskToken(TaskTokenBusinessManager taskTokenBusinessManager){
			
		}

		@Override
		public Boolean execute(CollectTaskToken token) throws BusinessException {
			if(token==null){
				throw new BusinessException(BusinessExceptionEnum.TASK_TOKEN_INFO_NULL);
			}
			
			CollectTaskToken _token=(CollectTaskToken)collectTaskTokenDao.getTaskTokenByToken(token.getToken(), token.getUserId(),token.getOwnerId());
			if(_token==null){
				logger.warn("未找到用户"+token.getUserId()+" 的TOKEN值 "+token.getToken());
				return false;
			}
			if(_token.getBaseId()!=null&&(token.getBaseId()==null||(token.getBaseId()!=null&&!_token.getBaseId().equals(token.getBaseId())))){
				logger.warn("用户"+token.getUserId()+" 的TOKEN值 "+token.getToken()+" 状态错误");
				return false;
			}
			//token状态错误
			if(_token.getTokenStatus().intValue()!=CommonConstant.TOKEN_STATUS_NEW){
				logger.warn("用户"+token.getUserId()+" 的TOKEN值 "+token.getToken()+" 状态错误");
				return false;
			}
			if(token.getAttachmentMd5()!=null&&!token.getAttachmentMd5().equals(_token.getAttachmentMd5())){
				logger.warn("用户"+token.getUserId()+" 的TOKEN值 "+token.getToken()+" MD5校验错误");
//				//疑似作弊
//				_token.setTokenStatus(CommonConstant.TOKEN_STATUS_CHEAT);
//				updateTaskTokenStatus.execute(_token);
				return false;
			}
			return true;
		}
		
	}
	
	/**
	 * 检测Token合法性
	 * @author xuyaming
	 *
	 */
	@Component
	public class CheckTokenMd5 implements CollectCore<CollectTokenCheckEntity,CollectTaskToken>{
		@Autowired
		public CheckTokenMd5(TaskTokenBusinessManager taskTokenBusinessManager){
			
		}

		@Override
		public CollectTokenCheckEntity execute(CollectTaskToken token) throws BusinessException {
			CollectTokenCheckEntity entity=new CollectTokenCheckEntity();
			entity.setIsNew(false);
			if(token==null){
				throw new BusinessException(BusinessExceptionEnum.TASK_TOKEN_INFO_NULL);
			}
			if(token.getAttachmentMd5()!=null){
				//获取Token时校验是否MD5已经存在 防止相同Md5重复获取Token使用
				CollectTaskToken _token=(CollectTaskToken)collectTaskTokenDao.getTaskTokenByMd5(token.getAttachmentMd5(), token.getUserId(),token.getOwnerId());
				if(_token!=null){
					if(_token.getTokenStatus().intValue()!=CommonConstant.TOKEN_STATUS_NEW){
						logger.error("用户"+token.getUserId()+" 的TOKEN值 "+token.getToken()+" 状态错误");
						throw new BusinessException(BusinessExceptionEnum.TASK_TOKEN_STATUS_ERROR);
					}
					entity.setToken(_token.getToken());
					return entity;
				}else{
					entity.setIsNew(true);
					return entity;
				}
			}
			throw new BusinessException(BusinessExceptionEnum.TASK_TOKEN_INFO_NULL);
		}
		
	}
	
	/**
	 * 更新Token状态
	 * @author xuyaming
	 *
	 */
	@Component
	public class UpdateTaskTokenStatus implements CollectCore<Integer,CollectTaskToken>{
		@Autowired
		public UpdateTaskTokenStatus(TaskTokenBusinessManager taskTokenBusinessManager){
			
		}

		@Override
		public Integer execute(CollectTaskToken token) throws BusinessException {
			if(token==null){
				throw new BusinessException(BusinessExceptionEnum.TASK_TOKEN_INFO_NULL);
			}
			return (Integer)collectTaskTokenDao.updateTaskTokenStatus(token);
		}
		
	}
	
	
}

package cn.dataup.collect.shape.component;

import javax.annotation.Resource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import autonavi.online.framework.util.json.JsonBinder;

import com.autonavi.collect.exception.BusinessException;
import com.autonavi.collect.exception.BusinessExceptionEnum;

@Component
public class RedisUtilComponent {
	private Logger logger = LogManager.getLogger(this.getClass());
	@Resource
	private JedisPool jedisPool;
	
	public Jedis getRedisInstance()throws BusinessException{
		Jedis jedis=null;
		try {
			jedis = jedisPool.getResource();
			return jedis;
		} catch (Exception e) {
			if(e instanceof BusinessException){
				throw (BusinessException)e;
			}else{
				jedisPool.returnBrokenResource(jedis);
				logger.error(e.getMessage(),e);
				throw new BusinessException(BusinessExceptionEnum.REDIS_EXCEPTION);
			}
		}
	}
	
    public void setRedisStringCache(Jedis jedis,String key,String value,int expire)throws BusinessException{
    	if(jedis==null){
    		throw new BusinessException(BusinessExceptionEnum.REDIS_EXCEPTION);
    	}
    	try {
			jedis.set(key,value);
			if(expire>0){
				jedis.expire(key, expire);
			}
		} catch (Exception e) {
			if(e instanceof BusinessException){
				throw (BusinessException)e;
			}else{
				logger.error(e.getMessage(),e);
				jedisPool.returnBrokenResource(jedis);
				throw new BusinessException(BusinessExceptionEnum.REDIS_EXCEPTION);
			}
		}
    }
    public void setRedisStringCache(String key,String value,int expire)throws BusinessException{
    	Jedis jedis=null;
    	try {
    		jedis=this.getRedisInstance();
			jedis.set(key,value);
			if(expire>0){
				jedis.expire(key, expire);
			}
		} catch (Exception e) {
			if(e instanceof BusinessException){
				throw (BusinessException)e;
			}else{
				logger.error(e.getMessage(),e);
				jedisPool.returnBrokenResource(jedis);
				throw new BusinessException(BusinessExceptionEnum.REDIS_EXCEPTION);
			}
		}finally{
			this.returnRedis(jedis);
		}
    }
	
	public <T> void setRedisJsonCache(Jedis jedis,String key,T object,int expire)throws BusinessException{
		if(jedis==null){
    		throw new BusinessException(BusinessExceptionEnum.REDIS_EXCEPTION);
    	}
		try {
			jedis.set(key, JsonBinder.buildNormalBinder(false).toJson(object));
			if(expire>0){
				jedis.expire(key, expire);
			}
		} catch (Exception e) {
			if(e instanceof BusinessException){
				throw (BusinessException)e;
			}else{
				logger.error(e.getMessage(),e);
				jedisPool.returnBrokenResource(jedis);
				throw new BusinessException(BusinessExceptionEnum.REDIS_EXCEPTION);
			}
		}
	}
	
	public <T> void setRedisJsonCache(String key,T object,int expire)throws BusinessException{
		Jedis jedis=null;
		try {
			jedis=this.getRedisInstance();
			jedis.set(key, JsonBinder.buildNormalBinder(false).toJson(object));
			if(expire>0){
				jedis.expire(key, expire);
			}
		} catch (Exception e) {
			if(e instanceof BusinessException){
				throw (BusinessException)e;
			}else{
				logger.error(e.getMessage(),e);
				jedisPool.returnBrokenResource(jedis);
				throw new BusinessException(BusinessExceptionEnum.REDIS_EXCEPTION);
			}
		}finally{
			this.returnRedis(jedis);
		}
	}
	
	public <T> void setRedisJsonCache(Jedis jedis,String key,T object,JsonBinder binder,int expire)throws BusinessException{
		if(jedis==null){
    		throw new BusinessException(BusinessExceptionEnum.REDIS_EXCEPTION);
    	}
		try {
			jedis.set(key, binder.toJson(object));
			if(expire>0){
				jedis.expire(key, expire);
			}
		} catch (Exception e) {
			if(e instanceof BusinessException){
				throw (BusinessException)e;
			}else{
				logger.error(e.getMessage(),e);
				jedisPool.returnBrokenResource(jedis);
				throw new BusinessException(BusinessExceptionEnum.REDIS_EXCEPTION);
			}
		}
	}
	
	public <T> void setRedisJsonCache(String key,T object,JsonBinder binder,int expire)throws BusinessException{
		Jedis jedis=null;
		try {
			jedis=this.getRedisInstance();
			jedis.set(key, binder.toJson(object));
			if(expire>0){
				jedis.expire(key, expire);
			}
		} catch (Exception e) {
			if(e instanceof BusinessException){
				throw (BusinessException)e;
			}else{
				logger.error(e.getMessage(),e);
				jedisPool.returnBrokenResource(jedis);
				throw new BusinessException(BusinessExceptionEnum.REDIS_EXCEPTION);
			}
		}finally{
			this.returnRedis(jedis);
		}
	}
	
	public <T> T getRedisJsonCache(Jedis jedis,String key,Class<T> clazz)throws BusinessException{
		if(jedis==null){
    		throw new BusinessException(BusinessExceptionEnum.REDIS_EXCEPTION);
    	}
		try {
			String value=jedis.get(key);
			if(value!=null){
				return JsonBinder.buildNormalBinder(false).fromJson(jedis.get(key), clazz);
			}
			return null;
		} catch (Exception e) {
			if(e instanceof BusinessException){
				throw (BusinessException)e;
			}else{
				logger.error(e.getMessage(),e);
				jedisPool.returnBrokenResource(jedis);
				throw new BusinessException(BusinessExceptionEnum.REDIS_EXCEPTION);
			}
		}
	}
	
	public <T> T getRedisJsonCache(String key,Class<T> clazz)throws BusinessException{
		Jedis jedis=null;
		try {
			jedis=this.getRedisInstance();
			String value=jedis.get(key);
			if(value!=null){
				return JsonBinder.buildNormalBinder(false).fromJson(jedis.get(key), clazz);
			}
			return null;
		} catch (Exception e) {
			if(e instanceof BusinessException){
				throw (BusinessException)e;
			}else{
				logger.error(e.getMessage(),e);
				jedisPool.returnBrokenResource(jedis);
				throw new BusinessException(BusinessExceptionEnum.REDIS_EXCEPTION);
			}
		}finally{
			this.returnRedis(jedis);
		}
	}
	
	public <T> T getRedisJsonCache(Jedis jedis,String key,Class<T> clazz,JsonBinder binder)throws BusinessException{
		if(jedis==null){
    		throw new BusinessException(BusinessExceptionEnum.REDIS_EXCEPTION);
    	}
		try {
			String value=jedis.get(key);
			if(value!=null){
				return binder.fromJson(jedis.get(key), clazz);
			}
			return null;
		} catch (Exception e) {
			if(e instanceof BusinessException){
				throw (BusinessException)e;
			}else{
				logger.error(e.getMessage(),e);
				jedisPool.returnBrokenResource(jedis);
				throw new BusinessException(BusinessExceptionEnum.REDIS_EXCEPTION);
			}
		}
	}
	public <T> T getRedisJsonCache(String key,Class<T> clazz,JsonBinder binder)throws BusinessException{
		Jedis jedis=null;
		try {
			jedis=this.getRedisInstance();
			String value=jedis.get(key);
			if(value!=null){
				return binder.fromJson(jedis.get(key), clazz);
			}
			return null;
		} catch (Exception e) {
			if(e instanceof BusinessException){
				throw (BusinessException)e;
			}else{
				logger.error(e.getMessage(),e);
				jedisPool.returnBrokenResource(jedis);
				throw new BusinessException(BusinessExceptionEnum.REDIS_EXCEPTION);
			}
		}finally{
			this.returnRedis(jedis);
		}
	}
	public void releaseRedisCache(Jedis jedis,String key)throws BusinessException{
		if(jedis==null){
    		throw new BusinessException(BusinessExceptionEnum.REDIS_EXCEPTION);
    	}
		try {
			jedis.del(key);
		} catch (Exception e) {
			if(e instanceof BusinessException){
				throw (BusinessException)e;
			}else{
				logger.error(e.getMessage(),e);
				jedisPool.returnBrokenResource(jedis);
				throw new BusinessException(BusinessExceptionEnum.REDIS_EXCEPTION);
			}
		}
	}
	public void releaseRedisCache(String key)throws BusinessException{
		Jedis jedis=null;
		try {
			jedis=this.getRedisInstance();
			jedis.del(key);
		} catch (Exception e) {
			if(e instanceof BusinessException){
				throw (BusinessException)e;
			}else{
				logger.error(e.getMessage(),e);
				jedisPool.returnBrokenResource(jedis);
				throw new BusinessException(BusinessExceptionEnum.REDIS_EXCEPTION);
			}
		}finally{
			this.returnRedis(jedis);
		}
	}
	
	
	public void returnRedis(Jedis jedis){
		try {
			jedisPool.returnResource(jedis);
		} catch (Exception e) {
			logger.warn("redis前面已经出错,已经调用过returnBrokenResource方法("+e.getMessage()+")");
		}
	}
	public void lockIdByRedis(String prefix,String id, BusinessExceptionEnum enums, int expire,Jedis jedis) throws BusinessException {
		if(jedis==null){
    		throw new BusinessException(BusinessExceptionEnum.REDIS_EXCEPTION);
    	}
		Long returnValue = 1L;
		try {
			String key=prefix+"_"+id;
			returnValue = jedis.sadd(key, id);
			if (returnValue == 0) {
				// 给当前taskId加锁
				// 注意：旧版本的jedis失败是返回-1，目前使用的2.4.2失败返回0，成功返回1
				// 并发重复，保存失败
				logger.info("key=["+key+"]正在被锁定");
				throw new BusinessException(enums);
			}
			logger.info("key=["+key+"]将锁定"+expire+"秒");
			jedis.expire(key, expire);
		} catch (Exception e) {
			if(e instanceof BusinessException){
				throw (BusinessException)e;
			}else{
				logger.error(e.getMessage(),e);
				jedisPool.returnBrokenResource(jedis);
				throw new BusinessException(BusinessExceptionEnum.REDIS_EXCEPTION);
			}
		}
	}
	
	
	public void lockIdByRedis(String prefix,String id, BusinessExceptionEnum enums, int expire) throws BusinessException {
		Jedis jedis=null;
		Long returnValue = 1L;
		try {
			jedis=this.getRedisInstance();
			String key=prefix+"_"+id;
			returnValue = jedis.sadd(key, id);
			if (returnValue == 0) {
				// 给当前taskId加锁
				// 注意：旧版本的jedis失败是返回-1，目前使用的2.4.2失败返回0，成功返回1
				// 并发重复，保存失败
				logger.info("key=["+key+"]正在被锁定");
				throw new BusinessException(enums);
			}
			logger.info("key=["+key+"]将锁定"+expire+"秒");
			jedis.expire(key, expire);
		} catch (Exception e) {
			if(e instanceof BusinessException){
				throw (BusinessException)e;
			}else{
				logger.error(e.getMessage(),e);
				jedisPool.returnBrokenResource(jedis);
				throw new BusinessException(BusinessExceptionEnum.REDIS_EXCEPTION);
			}
		}finally{
			this.returnRedis(jedis);
		}
	}
	
	public boolean checkIdIsLockedByRedis(String prefix,String id) throws BusinessException{
		Jedis jedis=null;
		try {
			jedis=this.getRedisInstance();
			String key=prefix+"_"+id;
			if(jedis.exists(key)){
				return true;
			}
			return false;
		} catch (Exception e) {
			if(e instanceof BusinessException){
				throw (BusinessException)e;
			}else{
				logger.error(e.getMessage(),e);
				jedisPool.returnBrokenResource(jedis);
				throw new BusinessException(BusinessExceptionEnum.REDIS_EXCEPTION);
			}
		}finally{
			this.returnRedis(jedis);
		}
	}
	
	
    public void releaseIdByRedis(String prefix,String id,Jedis jedis) throws BusinessException {
    	if(jedis==null){
    		throw new BusinessException(BusinessExceptionEnum.REDIS_EXCEPTION);
    	}
    	try {
			String key=prefix+"_"+id;
			logger.info("请求释放key=["+key+"]");
			jedis.del(key);
		} catch (Exception e) {
			if(e instanceof BusinessException){
				throw (BusinessException)e;
			}else{
				logger.error(e.getMessage(),e);
				jedisPool.returnBrokenResource(jedis);
				throw new BusinessException(BusinessExceptionEnum.REDIS_EXCEPTION);
			}
		}
	}
    
    public void releaseIdByRedis(String prefix,String id) throws BusinessException {
    	Jedis jedis=null;
    	try {
    		jedis=this.getRedisInstance();
			String key=prefix+"_"+id;
			logger.info("请求释放key=["+key+"]");
			jedis.del(key);
		} catch (Exception e) {
			if(e instanceof BusinessException){
				throw (BusinessException)e;
			}else{
				logger.error(e.getMessage(),e);
				jedisPool.returnBrokenResource(jedis);
				throw new BusinessException(BusinessExceptionEnum.REDIS_EXCEPTION);
			}
		}finally{
			this.returnRedis(jedis);
		}
	}
    public void releaseIdsByRedis(String[] keys) throws BusinessException {
    	Jedis jedis=null;
    	try {
    		jedis=this.getRedisInstance();
    		for(String key:keys){
    			logger.info("请求释放key=["+key+"]");
    			jedis.del(key);
    		}
		} catch (Exception e) {
			if(e instanceof BusinessException){
				throw (BusinessException)e;
			}else{
				logger.error(e.getMessage(),e);
				jedisPool.returnBrokenResource(jedis);
				throw new BusinessException(BusinessExceptionEnum.REDIS_EXCEPTION);
			}
		}finally{
			this.returnRedis(jedis);
		}
	}
//	
//	
//	
//	public Jedis lockPackageIdByRedis(Long packageId, BusinessExceptionEnum enums, int expire,Jedis jedis) throws BusinessException {
//		Long returnValue = 1L;
//		try {
//			if(jedis==null)
//			jedis = jedisPool.getResource();
//			String key=CommonConstant.REDIS_LOCK_PACKAGE_KEY+"_"+packageId.toString();
//			returnValue = jedis.sadd(key, packageId.toString());
//			jedis.expire(key, expire);
//			if (returnValue == 0) {
//				// 给当前taskId加锁
//				// 注意：旧版本的jedis失败是返回-1，目前使用的2.4.2失败返回0，成功返回1
//				// 并发重复，保存失败
//				throw new BusinessException(enums);
//			}
//		} catch (Exception e) {
//			if (jedis != null) {
//				jedisPool.returnBrokenResource(jedis);
//				jedis=null;
//			}
//			if(e instanceof BusinessException){
//				throw (BusinessException)e;
//			}else{
//				logger.error(e.getMessage(),e);
//				throw new BusinessException(BusinessExceptionEnum.REDIS_EXCEPTION);
//			}
//		}
//		return jedis;
//	}
//	
//	public void releasePackageIdByRedis(Long packageId,Jedis jedis) throws BusinessException {
//		try {
//			String key=CommonConstant.REDIS_LOCK_PACKAGE_KEY+"_"+packageId.toString();
//			jedis.del(key);
//		} catch (Exception e) {
//			if (jedis != null) {
//				jedisPool.returnBrokenResource(jedis);
//				jedis=null;
//			}
//			if(e instanceof BusinessException){
//				throw (BusinessException)e;
//			}else{
//				logger.error(e.getMessage(),e);
//				throw new BusinessException(BusinessExceptionEnum.REDIS_EXCEPTION);
//			}
//		}
//	}
//	public Jedis lockTaskIdByRedis(Long passiveId, BusinessExceptionEnum enums, int expire,Jedis jedis) throws BusinessException {
//		Long returnValue = 1L;
//		try {
//			if(jedis==null)
//			jedis = jedisPool.getResource();
//			String key=CommonConstant.REDIS_LOCK_TASK_KEY+"_"+passiveId.toString();
//			returnValue = jedis.sadd(key, passiveId.toString());
//			/*
//			 * 在expire（秒）时间内不允许对相同的任务进行操作， 另外因为目前新框架的切面逻辑是在service结束后才会提交，
//			 * 所以如果在finally里释放当前的taskId，还是会出现并发的问题，
//			 * 最终采用直接设置过期时间10秒，最后也不释放，完全等待10秒的过期时间。
//			 */
//			jedis.expire(key, expire);
//			if (returnValue == 0) {
//				// 给当前taskId加锁
//				// 注意：旧版本的jedis失败是返回-1，目前使用的2.4.2失败返回0，成功返回1
//				// 并发重复，保存失败
//				throw new BusinessException(enums);
//				
//			}
//		} catch (Exception e) {
//			if (jedis != null) {
//				jedisPool.returnBrokenResource(jedis);
//				jedis=null;
//			}
//			if(e instanceof BusinessException){
//				throw (BusinessException)e;
//			}else{
//				logger.error(e.getMessage(),e);
//				throw new BusinessException(BusinessExceptionEnum.REDIS_EXCEPTION);
//			}
//		}
//		return jedis;
//	}
//	public void releaseTaskIdByRedis(Long passiveId,Jedis jedis) throws BusinessException {
//		try {
//			String key=CommonConstant.REDIS_LOCK_TASK_KEY+"_"+passiveId.toString();
//			jedis.del(key);
//		} catch (Exception e) {
//			if (jedis != null) {
//				jedisPool.returnBrokenResource(jedis);
//				jedis=null;
//			}
//			if(e instanceof BusinessException){
//				throw (BusinessException)e;
//			}else{
//				logger.error(e.getMessage(),e);
//				throw new BusinessException(BusinessExceptionEnum.REDIS_EXCEPTION);
//			}
//		}
//	}
	public String getRedisStringCache(String key,Jedis jedis)throws BusinessException{
		if(jedis==null){
    		throw new BusinessException(BusinessExceptionEnum.REDIS_EXCEPTION);
    	}
		try {
			return jedis.get(key);
		} catch (Exception e) {
			if(e instanceof BusinessException){
				throw (BusinessException)e;
			}else{
				logger.error(e.getMessage(),e);
				jedisPool.returnBrokenResource(jedis);
				throw new BusinessException(BusinessExceptionEnum.REDIS_EXCEPTION);
			}
		}
	}
	public String getRedisStringCache(String key)throws BusinessException{
		Jedis jedis=null;
		try {
			jedis=this.getRedisInstance();
			return jedis.get(key);
		} catch (Exception e) {
			if(e instanceof BusinessException){
				throw (BusinessException)e;
			}else{
				logger.error(e.getMessage(),e);
				jedisPool.returnBrokenResource(jedis);
				throw new BusinessException(BusinessExceptionEnum.REDIS_EXCEPTION);
			}
		}finally{
			this.returnRedis(jedis);
		}
	}

}

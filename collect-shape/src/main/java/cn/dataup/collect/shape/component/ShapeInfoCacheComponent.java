package cn.dataup.collect.shape.component;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PostConstruct;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import redis.clients.jedis.Jedis;
import autonavi.online.framework.property.PropertiesConfig;
import autonavi.online.framework.property.PropertiesConfigUtil;
import autonavi.online.framework.sharding.dao.DaoHelper;
import autonavi.online.framework.util.json.JsonBinder;
import cn.dataup.collect.shape.dao.CollectShapeDao;

import com.autonavi.collect.bean.CollectShapeInfo;
import com.autonavi.collect.bean.CollectTaskClazzShape;
import com.autonavi.collect.constant.CommonConstant;
import com.mapabc.service.RegionSearch;

@Component
public class ShapeInfoCacheComponent {
	private Logger logger = LogManager.getLogger(this.getClass());
	private PropertiesConfig pc = null;
	private Map<Long, RegionSearch> regionSearchMap = new ConcurrentHashMap<Long, RegionSearch>();
	@Autowired
	private CollectShapeDao collectShapeDao;
	@Autowired
	private RedisUtilComponent redisUtilComponent;

	private ShapeInfoCacheComponent() throws Exception {
		if (pc == null)
			pc = PropertiesConfigUtil.getPropertiesConfigInstance();
	}

	@SuppressWarnings("unchecked")
	@PostConstruct
	private void init() throws Exception {
		Jedis jedis = null;
		try {
			logger.info("初始化shape信息");
			// shape根路径
			String path = pc.getProperty(CommonConstant.SHAPE_FILE_ROOT)
					.toString();
			logger.info("shape的根目录为" + path);
			// 从数据库中获得所有shape文件信息并初始化
			List<CollectShapeInfo> shapes = (List<CollectShapeInfo>) collectShapeDao
					.getAllValidShapes(CommonConstant.getSingleDataSourceKey());
			if (shapes != null) {
				logger.info("shape文件共" + shapes.size() + "个");
				// 初始化Shape并缓存到内存不释放
				for (CollectShapeInfo shapeInfo : shapes) {
					String filePath = path + shapeInfo.getShapePath();
					this.regionSearchMap.put(shapeInfo.getId(),
							new RegionSearch(filePath));
				}

			} else {
				logger.info("未获取到任何shape文件");
			}
			logger.info("初始化shape文件完毕");
			logger.info("缓存分类和SHAPE的关系");
			Set<Integer> dsSet = DaoHelper.getAllDSKey();
			jedis = redisUtilComponent.getRedisInstance();
			for (Integer dsKey : dsSet) {
				List<CollectTaskClazzShape> clazzShapes = (List<CollectTaskClazzShape>) collectShapeDao
						.getAllValidTaskClazzShapes(dsKey);
				for (CollectTaskClazzShape clazzShape : clazzShapes) {
					redisUtilComponent.setRedisJsonCache(
							jedis,
							CommonConstant.TASK_CLAZZ_SHAPE_PREFIX
									+ clazzShape.getTaskClazzId(), clazzShape,
							JsonBinder.buildNormalBinder(false), 0);
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		} finally {
			if (jedis != null) {
				redisUtilComponent.returnRedis(jedis);
			}
		}

	}
   /**
    * 获取SHAPE服务
    * @param taskClazzId
    * @return
    * @throws Exception
    */
	public RegionSearch getRegionSearch(Long taskClazzId)throws Exception {
		CollectTaskClazzShape shape=redisUtilComponent.getRedisJsonCache(
				CommonConstant.TASK_CLAZZ_SHAPE_PREFIX + taskClazzId, CollectTaskClazzShape.class,
				JsonBinder.buildNormalBinder(false));
		if(shape!=null){
			return regionSearchMap.get(shape.getShapeId());
		}
		return null;
	}
}

package com.autonavi.collect.constant;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import autonavi.online.framework.property.PropertiesConfigUtil;

public class CommonConstant {
	private static Logger logger = LogManager.getLogger(CommonConstant.class);

	public static final String REDIS_LOCK_TASK_KEY = "COLL_CORE_save_submit_lock_task_id";
	public static final String REDIS_LOCK_PACKAGE_KEY = "COLL_CORE_save_submit_lock_package_id";
	public static final String REDIS_LOCK_ORIGIN_PACKAGE_KEY = "COLL_CORE_save_submit_lock_origin_package_id";
	
//	public static final String REDIS_LOCK_INITIATIVE_PACKAGE_KEY = "save_submit_lock_initiative_package_id";
//	public static final String REDIS_LOCK_INITIATIVE_TASK_KEY = "save_submit_lock_initiative_task_id";
	
	public static final String REDIS_LOCK_TASK_INSERT_KEY = "COLL_CORE_save_submit_lock_insert";
	//分类锁
	public static final String REDIS_LOCK_TASK_CLAZZ_KEY="COLL_CORE_REDIS_LOCK_TASK_CLAZZ_KEY";
	
	//用户第一次访问插入锁
	public static final String REDIS_LOCK_USER_FIRST_KEY="COLL_CORE_REDIS_LOCK_USER_FIRST_KEY";

	public static final Integer PACKAGE_EMPTY_TASK_COUNT = 0;

	public enum ALLOT_USER_STATUS {
		// 分配用户有效0 分配用户无效1
		VALID(0), INVALID(1);
		private int code;

		public int getCode() {
			return code;
		}

		private ALLOT_USER_STATUS(int code) {
			this.code = code;
		}
	}

	public enum GPS_SYSTEM {
		// 0 GPS 1百度坐标系 2国测局坐标系(amap)
		DEFAULT(0), BAIDU(1), GCJ(2);
		private int code;

		public int getCode() {
			return code;
		}

		private GPS_SYSTEM(int code) {
			this.code = code;
		}
	}

	public enum TASK_STATUS {
		// 0 未分配 1已领取(待采集) 2冻结(无效任务) 3已保存(待提交) 4已提交(待审核) 5完成(已审核) 6已分配 7 未找到 8 超时 9 申诉 10 删除 11 初审
		UNALLOT(0), RECEIVE(1), FREEZE(2), SAVE(3), SUBMIT(4), FINISH(5), ALLOT(
				6), NOT_FOUND(7), TIME_OUT(8), RE_AUDIT(9),DELETE(10),FIRST_AUDIT(11);

		private int code;

		public int getCode() {
			return code;
		}

		private TASK_STATUS(int code) {
			this.code = code;
		}
	}

	public enum TASK_PACKAGE_TYPE {
		// 0 户外大厦 1公交车站 2楼宇（小区）
		BUILDING(0), BUS_STATION(1), HOUSING(2);

		private int code;

		public int getCode() {
			return code;
		}

		private TASK_PACKAGE_TYPE(int code) {
			this.code = code;
		}
	}
	
	public enum TASK_ATTR_STATUS {
		// 0不通过 1通过
		PASS("1"), FAIL("0");

		private String code;

		public String getCode() {
			return code;
		}

		private TASK_ATTR_STATUS(String code) {
			this.code = code;
		}
	}

	public enum TASK_VERIFY_STATUS {
		// 3 审核通过 4审核不通过 6 冻结期(初审)通过 7 冻结期(初审)不通过 
		PASS(3), FAIL(4),FIRST_PASS(6),FIRST_FAIL(7);

		private int code;

		public int getCode() {
			return code;
		}

		private TASK_VERIFY_STATUS(int code) {
			this.code = code;
		}
	}

	public enum TASK_SUBMIT_STATUS {
		// 0 提交成功 1 提交失败
		SUCCESS(0), ERROR(1);
		private int code;

		public int getCode() {
			return code;
		}

		private TASK_SUBMIT_STATUS(int code) {
			this.code = code;
		}
	}

	public enum TASK_CLAZZ_STATUS {
		// 0 提交成功 1 提交失败
		VAILD(0), INVAILD(1);
		private int code;

		public int getCode() {
			return code;
		}

		private TASK_CLAZZ_STATUS(int code) {
			this.code = code;
		}
	}

	public enum TASK_CLAZZ_TYPE {
		// 0 目录 1 分类
		MENU(0), ITEM(1);
		private int code;

		public int getCode() {
			return code;
		}

		private TASK_CLAZZ_TYPE(int code) {
			this.code = code;
		}
	}
	
	public enum TASK_CLAZZ_PAY_TYPE {
		// 0 固定 1 最小 2 最大
		FIXED(0), MIN(1),MAX(2);
		private int code;

		public int getCode() {
			return code;
		}

		private TASK_CLAZZ_PAY_TYPE(int code) {
			this.code = code;
		}
	}

	public enum TASK_TYPE {
		// 0 被动 1 主动
		PASSIVE(0), INITIATIVE(1);
		private int code;

		public int getCode() {
			return code;
		}

		private TASK_TYPE(int code) {
			this.code = code;
		}
	}
	
	public enum TOKEN_USE {
		// 0 使用 1 不使用
		YES(0), NO(1);
		private int code;

		public int getCode() {
			return code;
		}

		private TOKEN_USE(int code) {
			this.code = code;
		}
	}
	
	public enum TASK_IMG_STATUS {
		// 0 使用 1 不使用 2站位
		USE(0), UNUSE(1),STATION(2),PASS(3),FAIL(4);
		private int code;

		public int getCode() {
			return code;
		}

		private TASK_IMG_STATUS(int code) {
			this.code = code;
		}
	}
	
	public enum RESOURCE_STATUS {
		// 0 使用 1 不使用 
		USE(0), UNUSE(1);
		private int code;

		public int getCode() {
			return code;
		}

		private RESOURCE_STATUS(int code) {
			this.code = code;
		}
	}
	
	public enum TASK_EXTRA_OPERATION {
		// 0 更新 1 删除
		UPDATE(0), DELETE(1);
		private int code;

		public int getCode() {
			return code;
		}

		private TASK_EXTRA_OPERATION(int code) {
			this.code = code;
		}
	}
	
	public enum SEND_MESSAGE_ERROR_TYPE {
		// 0 审核系统-写入  1 审核系统-回写错误 2 打水印-写入 
		AUDIT_OUT(0), AUDIT_IN(1),WATER_OUT(2);
		private int code;

		public int getCode() {
			return code;
		}

		private SEND_MESSAGE_ERROR_TYPE(int code) {
			this.code = code;
		}
	}
	
	public enum SEND_MESSAGE_ERROR_STATUS {
		// 0 新建  1 已处理
		NEW(0), PROCESSED(1);
		private int code;

		public int getCode() {
			return code;
		}

		private SEND_MESSAGE_ERROR_STATUS(int code) {
			this.code = code;
		}
	}
	
	public enum USER_CACHE_TYPE {
		// 0 REDIS  1 其他
		REDIS(0), OTHER(1);
		private int code;

		public int getCode() {
			return code;
		}

		private USER_CACHE_TYPE(int code) {
			this.code = code;
		}
	}
	
	public enum SHAPE_FILE_STATUS {
		// 0 有效  1 无效
		VALID(0), INVALID(1);
		private int code;

		public int getCode() {
			return code;
		}

		private SHAPE_FILE_STATUS(int code) {
			this.code = code;
		}
	}
	public enum TASK_CLAZZ_SHAPE_STATUS {
		// 0 有效  1 无效
		VALID(0), INVALID(1);
		private int code;

		public int getCode() {
			return code;
		}

		private TASK_CLAZZ_SHAPE_STATUS(int code) {
			this.code = code;
		}
	}

	public static final String PROP_SINGLE_DATASOURCE_KEY = "SINGLE_DATASOURCE_KEY";

	/**
	 * 获取单一来源数据库编号
	 * 
	 * @return
	 */
	public static int getSingleDataSourceKey() {
		Integer dsKey = 1;
		try {
			dsKey = new Integer((String) PropertiesConfigUtil
					.getPropertiesConfigInstance().getProperty(
							PROP_SINGLE_DATASOURCE_KEY));
		} catch (Exception e) {
			logger.error("获取单一数据源失败,请检查,将返回默认数据源编号1", e);
		}
		return dsKey.intValue();
	}

	/**
	 * 主动任务相关时限常量
	 */
	public static final String INITIATIVE_SAVE_TIME = "INITIATIVE_SAVE_TIME";
	public static final String INITIATIVE_VERIFY_TIME = "INITIATIVE_VERIFY_TIME";
	public static final String INITIATIVE_VERIFY_FREEZE_TIME = "INITIATIVE_VERIFY_FREEZE_TIME";
	public static final String INITIATIVE_RELEASE_FREEZE_TIME = "INITIATIVE_RELEASE_FREEZE_TIME";

	/**
	 * 许亚明从SysProps转移过来--开始
	 */

	// 用户名称缓存前缀
	public static final String USER_CACHE_PREFIX = "COLL_CORE_CUCP_";
	public static final String USER_ID_CACHE_PREFIX = "COLL_CORE_CUIDCP_";

	// 任务分类缓存前缀
	public static final String TASK_CLAZZ_CACHE_PREFIX = "TCCP_";
	public static final String TASK_CLAZZ_INITIATIVE_MENU_CACHE_PREFIX = "TCIMCP_";
	public static final String TASK_CLAZZ_PASSIVE_MENU_CACHE_PREFIX = "TCPMCP_";
	
	// TOKEN获取缓存前缀
	public static final String TOKEN_GET_CACHE_PREFIX = "COLL_CORE_TOK_GET_CP_";
	public static final String TOKEN_USE_CACHE_PREFIX = "COLL_CORE_TOK_USE_CP_";
	
	//BATCHID缓存前置标记
	public static final String BATCHID_CACHE_PREFIX="COLL_CORE_BAT_CP_";
	
	//BATCHID获取锁缓存前置标记
    public static final String BATCHID_USER_PREFIX="COLL_CORE_BAT_UP_";
    
    //CLAZZ_SHAPE缓存标记
    public static final String TASK_CLAZZ_SHAPE_PREFIX="COLL_SHP_TA_CL_SHP_";
	

	// SCORE状态
	public static final int SCORE_STATUS_OK = 0;
	public static final int SCORE_STATUS_CANCEL = 1;

	// Token类型常量
	// 新建Token
	public static final int TOKEN_STATUS_NEW = 0;
	// 任务提交后的Token状态
	public static final int TOKEN_STATUS_COMPLTETE = 1;
	// 任务删除后的Token状态
	public static final int TOKEN_STATUS_DEL = 2;
	// 疑似作弊的Token状态
	public static final int TOKEN_STATUS_CHEAT = 3;

	// 被动类型
	public static final int TASK_TYPE_PASSIVE = 0;
	// 主动类型
	public static final int TASK_TYPE_INITIATIVE = 1;

	// 被动任务超时时间属性标记
	public static final String PROP_TASK_SAVE_MAX_TIME = "TASK_SAVE_MAX_TIME";

	public static final String PROP_ROOT = "/gdAppServerNew";
	//token获取时间间隔
	public static final String PROP_TOKEN_GET_MIN_SPLIT = "TOKEN_GET_MIN_SPLIT";
	//新保存和提交的时间间隔
	public static final String PROP_TASK_INSERT_MIN_SPLIT = "TASK_INSERT_MIN_SPLIT";
	public static final String PROP_DEAD_LINE_TIME_OLD = "deadLineTimeOld";
	public static final String PROP_DEAD_LINE_TIME_NEW = "deadLineTimeNew";
	public static final String PACKFILE_PATH = "packfile_path";
	public static final String REPEATED_FAZHI = "REPEATED_FAZHI";
	public static final String SEARCHINDEX_NAME = "SEARCHINDEX_NAME";
	public static final String DEFAULT_SEARCH_RANGE = "DEFAULT_SEARCH_RANGE";
	public static final String AUDITBACK_SEARCH_INDEXNAME = "AUDITBACK_SEARCH_INDEXNAME";
	public static final String PROP_IMAGE_URL = "IMAGE_URL";
	public static final String PROP_IMAGE_URL_NEW = "IMAGE_URL_NEW";
	public static final String PROP_MAX_SUBMIT_TASK_SPLIT = "MAX_SUBMIT_TASK_SPLIT";
	public static final String PROP_MAX_SUBMIT_TASK_BATCH = "MAX_SUBMIT_TASK_BATCH";
	public static final String PROP_MAX_SUBMIT_TIME = "MAX_SUBMIT_TIME";
	public static final String PROP_SECURE_CODE_PATH = "SecureCodePath";
	public static final String PROP_SECURE_CODE_ARRAY = "SecureCodeArray";
	public static final String PROP_SECURE_CODE_TIME = "SecureCodeTime";
	public static final String PROP_SHAPEFILE_PATH = "shapefile_path";
	public static final String PROP_ANDROID_PAGE_SIZE = "ANDROID_PAGE_SIZE";
	public static final String PROP_USER_CACHE_EXPIRE="USER_CACHE_EXPIRE";

	public static final String COLLECT_INDEX_NAME = "COLLECT_INDEX_NAME";
	public static final String COLLECT_INDEX_NAME_TYPE = "COLLECT_INDEX_NAME_TYPE";

	public static final String COLLECT_NOUSER_INDEX_NAME = "COLLECT_NOUSER_INDEX_NAME";
	public static final String COLLECT_NOUSER_INDEX_NAME_TYPE = "COLLECT_NOUSER_INDEX_NAME_TYPE";
	
	public static final String COLLECT_INITIATIVE_TASK_POINT_INDEX_NAME="COLLECT_INITIATIVE_TASK_POINT_INDEX_NAME";
	public static final String COLLECT_INITIATIVE_TASK_POINT_INDEX_NAME_TYPE="COLLECT_INITIATIVE_TASK_POINT_INDEX_NAME_TYPE";
	
	public static final String COLLECT_INITIATIVE_H5_TASK_POINT_INDEX_NAME="COLLECT_INITIATIVE_H5_TASK_POINT_INDEX_NAME";
	public static final String COLLECT_INITIATIVE_H5_TASK_POINT_INDEX_NAME_TYPE="COLLECT_INITIATIVE_H5_TASK_POINT_INDEX_NAME_TYPE";
	/**
	 * 原始数据
	 */
	public static final String COLLECT_INITIATIVE_RESOURCE_TASK_POINT_INDEX_NAME="COLLECT_INITIATIVE_RESOURCE_TASK_POINT_INDEX_NAME";
	public static final String COLLECT_INITIATIVE_RESOURCE_TASK_POINT_INDEX_NAME_TYPE="COLLECT_INITIATIVE_RESOURCE_TASK_POINT_INDEX_NAME_TYPE";

	public static final String COLLECT_DATATYPE_NAME = "COLLECT_DATATYPE_NAME";
	
	public static final String COLLECT_TASK_EXTRA_TABLE="COLLECT_TASK_EXTRA_TABLE";

	/**
	 * 结束
	 */

	/**
	 * 消息队列-发送
	 */
	public static final String COLLECT_SYNC_QUEUE_OUT_HOST = "COLLECT_SYNC_QUEUE_OUT_HOST";
	public static final String COLLECT_SYNC_QUEUE_OUT_PORT = "COLLECT_SYNC_QUEUE_OUT_PORT";
	public static final String COLLECT_SYNC_QUEUE_OUT_NAME = "COLLECT_SYNC_QUEUE_OUT_NAME";
	/**
	 * 消息队列-接收
	 */
	public static final String COLLECT_SYNC_QUEUE_IN_HOST = "COLLECT_SYNC_QUEUE_IN_HOST";
	public static final String COLLECT_SYNC_QUEUE_IN_PORT = "COLLECT_SYNC_QUEUE_IN_PORT";
	public static final String COLLECT_SYNC_QUEUE_IN_NAME = "COLLECT_SYNC_QUEUE_IN_NAME";
	
	
	/**
	 * 消息队列-打水印发送
	 */
	public static final String WATER_SYNC_QUEUE_OUT_HOST = "WATER_SYNC_QUEUE_OUT_HOST";
	public static final String WATER_SYNC_QUEUE_OUT_PORT = "WATER_SYNC_QUEUE_OUT_PORT";
	public static final String WATER_SYNC_QUEUE_OUT_NAME = "WATER_SYNC_QUEUE_OUT_NAME";
	
	/**
	 * BATCHID(批次ID)的超时时间
	 */
	public static final String MAX_BATCHID_VALID_TIME="MAX_BATCHID_VALID_TIME";
	/**
	 * BATCHID(批次ID)的获取间隔
	 */
	public static final String MIN_BATCHID_SPLIT_TIME="MIN_BATCHID_SPLIT_TIME";
	
	/**
	 * SHAPE文件根目录
	 */
    public static final String SHAPE_FILE_ROOT="SHAPE_FILE_ROOT";
    /**
     * 远程H5系统地址
     */
    public static final String REMOTE_H5_URL="REMOTE_H5_URL";
	/**
	 * 系统ID
	 */
	public static final String COLLECT_SYSTEM_UNIQUE_ID = "COLLECT_SYSTEM_UNIQUE_ID";

	public final static String ORGIN_IMG_URL = "ORGIN_IMG_URL";
	public final static String COLLECT_IMG_URL = "COLLECT_IMG_URL";
}

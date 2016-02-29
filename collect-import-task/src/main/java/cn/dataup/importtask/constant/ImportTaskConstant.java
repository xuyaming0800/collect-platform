package cn.dataup.importtask.constant;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import autonavi.online.framework.property.PropertiesConfigUtil;



public class ImportTaskConstant {
	private static Logger logger = LogManager.getLogger(ImportTaskConstant.class);

	public static final String IMPORT_PASSIVE_DATABASE_KEY = "IMPORT_PASSIVE_DATABASE_KEY";
	public static final String COLLECT_PASSIVE_DATABASE_KEY = "COLLECT_PASSIVE_DATABASE_KEY";

	/**
	 * 获取单一来源数据库编号
	 * 
	 * @return
	 */
	public static int getImportDataSourceKey() {
		Integer dsKey = 1;
		try {
			dsKey = new Integer((String) PropertiesConfigUtil.getPropertiesConfigInstance().getProperty(IMPORT_PASSIVE_DATABASE_KEY));
		} catch (Exception e) {
			logger.error("获取单一数据源失败,请检查,将返回默认数据源编号1", e);
		}
		return dsKey.intValue();
	}
	/**
	 * 获取单一来源数据库编号
	 * 
	 * @return
	 */
	public static int getCollectDataSourceKey() {
		Integer dsKey = 1;
		try {
			dsKey = new Integer((String) PropertiesConfigUtil.getPropertiesConfigInstance().getProperty(COLLECT_PASSIVE_DATABASE_KEY));
		} catch (Exception e) {
			logger.error("获取单一数据源失败,请检查,将返回默认数据源编号1", e);
		}
		return dsKey.intValue();
	}
}

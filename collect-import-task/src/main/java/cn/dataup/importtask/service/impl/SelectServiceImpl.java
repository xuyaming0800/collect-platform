package cn.dataup.importtask.service.impl;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.time.StopWatch;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultConnectionKeepAliveStrategy;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.autonavi.collect.bean.CollectOriginalCoordinate;
import com.autonavi.collect.bean.CollectPassivePackage;
import com.autonavi.collect.bean.CollectPassiveTask;
import com.gd.app.util.StringUtil;

import cn.dataup.importtask.constant.ImportTaskConstant;
import cn.dataup.importtask.dao.CollectDataSelectDao;
import cn.dataup.importtask.entity.ImportTaskEntity;
import cn.dataup.importtask.entity.InputDataProjectName;
import cn.dataup.importtask.service.ImportDemoService;
import cn.dataup.importtask.service.SelectService;

@Service
public class SelectServiceImpl implements SelectService {

	@Autowired
	private CollectDataSelectDao collectDataSelectDao;
	@Autowired
	private ImportDemoService importDemoService;
	private Logger logger = Logger.getLogger(SelectServiceImpl.class);

	private CloseableHttpClient httpclient = openHttpClient();

	private CloseableHttpClient openHttpClient() {
		return HttpClients.custom()
				.setKeepAliveStrategy(new DefaultConnectionKeepAliveStrategy() {
					@Override
					public long getKeepAliveDuration(HttpResponse response,
							HttpContext context) {
						long keepAlive = super.getKeepAliveDuration(response,
								context);
						if (keepAlive == -1) {
							keepAlive = 60000;
						}
						return keepAlive;
					}
				}).build();
	}

	@Override
	public Long setGeocodingOrImportData(String tableName, Integer geoOrImport)
			throws Exception {
		// 计算总数
		Long recordCount = getCountByTableNameAndGeoOrImportFlag(tableName,
				geoOrImport);
		int size = 1000;
		Long block = recordCount / size;
		int rest = (int) (recordCount % size);
		if (block > 0) {// 超过1000条
			for (Long l = 0L; l < block; ++l) {
				selectCollectPassivePackageListWithPageAndGeoOrImportFlag(
						tableName, size, geoOrImport);
			}
			if (rest > 0) {
				selectCollectPassivePackageListWithPageAndGeoOrImportFlag(
						tableName, rest,  geoOrImport);
			}
		} else {// 不够1000条
			selectCollectPassivePackageListWithOutPageAndGeoOrImportFlag(
					tableName, geoOrImport);
		}
		return recordCount;
	}

	/**
	 * 计算需要处理的数据总数量
	 * 
	 * @param tableName
	 *            表名
	 * @param geoOrImport
	 *            计算GEO或导入的标识
	 * @return
	 */
	private Long getCountByTableNameAndGeoOrImportFlag(String tableName,
			Integer geoOrImport) throws Exception {
		Long recordCount = null;
		if (geoOrImport == 1) {// 计算GeoCoding的值
			// 读取数据量
			recordCount = (Long) collectDataSelectDao.getCountByTableName(
					ImportTaskConstant.getImportDataSourceKey(), tableName,
					geoOrImport);
		} else if (geoOrImport == 2) {// 导入新数据库
			recordCount = (Long) collectDataSelectDao.getCountByTableName(
					ImportTaskConstant.getImportDataSourceKey(), tableName,
					geoOrImport);
		}
		return recordCount;
	}

	@SuppressWarnings("unchecked")
	private List<InputDataProjectName> selectCollectPassivePackageListWithOutPageAndGeoOrImportFlag(
			String tableName, Integer geoOrImport) throws Exception {
		List<InputDataProjectName> inputDataProjectNameList = (List<InputDataProjectName>) collectDataSelectDao
				.selectCollectPassivePackageListWithOutLimit(
						ImportTaskConstant.getImportDataSourceKey(), tableName,
						geoOrImport);
		if (geoOrImport == 1) {
			geoCoding(inputDataProjectNameList);// 聚合地址分级结果（计算GeoCoding）
			importOldDataBaseByList(tableName, inputDataProjectNameList);
		} else if (geoOrImport == 2) {
			importData(inputDataProjectNameList, tableName);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	private List<InputDataProjectName> selectCollectPassivePackageListWithPageAndGeoOrImportFlag(
			String tableName, int size, Integer geoOrImport)
			throws Exception {
		List<InputDataProjectName> inputDataProjectNameList = null;
		inputDataProjectNameList = (List<InputDataProjectName>) collectDataSelectDao
				.selectCollectPassivePackageListWithLimit(
						ImportTaskConstant.getImportDataSourceKey(), tableName,
						0L, size, geoOrImport);
		try {
			if (geoOrImport == 1) {
				geoCoding(inputDataProjectNameList);// 聚合地址分级结果（计算GeoCoding）
				importOldDataBaseByList(tableName, inputDataProjectNameList);//导入原始数据库
			} else if (geoOrImport == 2) {
				importData(inputDataProjectNameList, tableName);
			}
		} catch (Exception e) {
			logger.error(e);
		}
		return null;
	}

	/**
	 * 计算GeoCoding，即把数据导入原始数据库
	 * @param tableName
	 * @param inputDataProjectNameList
	 */
	private void importOldDataBaseByList(String tableName,
			List<InputDataProjectName> inputDataProjectNameList) {
		// 批量导数据到原始数据库
		if (inputDataProjectNameList.size() > 0)
			collectDataSelectDao.insertCollectPassivePackageList(
					ImportTaskConstant.getImportDataSourceKey(),
					inputDataProjectNameList, tableName);
	}
	
	/**
	 * 计算GeoCoding，封装实体
	 * @author lxs
	 * @param inputDataProjectNameList
	 * @return
	 * @throws Exception
	 */
	private List<InputDataProjectName> geoCoding(
			List<InputDataProjectName> inputDataProjectNameList)
			throws Exception {
		try {
			httpclient = openHttpClient();// 开启连接
			StopWatch sw = new StopWatch(); 
			for (InputDataProjectName entity : inputDataProjectNameList) {
				// 计算GeoCoding
				sw.reset();
				sw.start();
				String address = entity.getAddressGeocode();
				String city = entity.getTaskCity();
				// 计算code
				String xml = getDataFromHttp(address, city);
				if(!StringUtil.isEmpty(xml)){//xml不为空
					String count = xml.substring(
							xml.indexOf("<count>") + "<count>".length(),
							xml.indexOf("</count>"));
					if (Integer.parseInt(count) > 0) {// 表示有数据
						String adcode = xml.substring(xml.indexOf("<adcode>")
								+ "<adcode>".length(), xml.indexOf("</adcode>"));
						String x = xml.substring(
								xml.indexOf("<x>") + "<x>".length(),
								xml.indexOf("</x>"));
						String y = xml.substring(
								xml.indexOf("<y>") + "<y>".length(),
								xml.indexOf("</y>"));
						String splitLevel = xml.substring(
								xml.indexOf("<addrSplitInfo>")
								+ "<addrSplitInfo>".length(),
								xml.indexOf("</addrSplitInfo>"));
						entity.setAddressGeocode(adcode);
						entity.setCoordX(Double.parseDouble(x));
						entity.setCoordY(Double.parseDouble(y));
						entity.setSplitLevel(splitLevel);
						entity.setGeoStatus(1);
						// splitLevel这个参数是做什么的？
						// splitLevel = URLEncoder.encode(splitLevel, "GBK");
					} else {// 失败
						entity.setGeoStatus(2);
					}
				} else {
					entity.setGeoStatus(2);
				}
				sw.stop();
				System.out.println("计算GeoCoding值--task_id="+entity.getTaskId()+"=="+sw);
			}
		} finally {
			httpclient.close();
		}
		return inputDataProjectNameList;
	}

	/**
	 * 拼接地址，获取坐标等信息
	 * 
	 * @param address
	 * @param city
	 * @return
	 * @throws UnsupportedEncodingException
	 * @throws IOException
	 * @throws ClientProtocolException
	 */
	private String getDataFromHttp(String address, String city)
			throws UnsupportedEncodingException, IOException,
			ClientProtocolException {
		String request = city + address;
		request = "http://123.57.213.15:8888/cgi-bin/geocode.cgi?address="
				+ URLEncoder.encode(request, "GBK");
		HttpGet httpGet = new HttpGet(request);
		HttpResponse httpResponse = httpclient.execute(httpGet);
		HttpEntity entity = httpResponse.getEntity();
		String result = EntityUtils.toString(entity, "GBK");
		return result;
	}

	/**
	 * 获取表名称集合，开头统一
	 */
	@SuppressWarnings("unchecked")
	public List<String> getTables(String startWtih) {
		List<String> newTabList = new ArrayList<String>();
		List<String> tables = (List<String>) collectDataSelectDao
				.getTables(ImportTaskConstant.getImportDataSourceKey());
		for (String str : tables) {
			if (str.startsWith(startWtih)) {
				newTabList.add(str);
			}
		}
		return newTabList;
	}

	@Override
	public Long getCountSuccessCalculateGeoCoding(String tableName,
			Integer geoOrImport) throws Exception {
		return (Long) this.collectDataSelectDao.getCountByTableName(
				ImportTaskConstant.getImportDataSourceKey(), tableName,
				geoOrImport);
	}

	/**
	 * 封装对象，调用导库接口实现换库
	 * 
	 * @param inputDataProjectNameList
	 * @param tableName
	 */
	private void importData(
			List<InputDataProjectName> inputDataProjectNameList,
			String tableName) throws Exception {
		ArrayList<ImportTaskEntity> list = new ArrayList<ImportTaskEntity>();
		for (InputDataProjectName inputDataProjectName : inputDataProjectNameList) {
			ImportTaskEntity entity = new ImportTaskEntity();
			CollectPassivePackage collectPassivePackage = new CollectPassivePackage();
			collectPassivePackage.setTaskPackageCate(inputDataProjectName
					.getAdType());// 广告类型
			collectPassivePackage.setTaskPackageName(inputDataProjectName
					.getTaskCity());// 城市
			collectPassivePackage.setTaskPackageDesc(inputDataProjectName
					.getAddressGeocode());//
			collectPassivePackage.setTaskPackagePay(32D);// 多少钱
			entity.setCollectPassivePackage(collectPassivePackage);
			List<CollectPassiveTask> collectPassiveTaskList = new ArrayList<CollectPassiveTask>();
			CollectPassiveTask task = new CollectPassiveTask();
			task.setDataName(task.getDataName());
			collectPassiveTaskList.add(task);
			List<CollectOriginalCoordinate> collectOriginalCoordinateList = new ArrayList<CollectOriginalCoordinate>();
			CollectOriginalCoordinate cood = new CollectOriginalCoordinate();
			cood.setTaskSampleImg("2015_01_01_157_test_1.jpg");
			cood.setOriginalX(inputDataProjectName.getCoordX());
			cood.setOriginalY(inputDataProjectName.getCoordY());
			cood.setCoordinateStatus(0);
			cood.setOriginalAdcode(110108);
			collectOriginalCoordinateList.add(cood);
			entity.setCollectPassiveTaskList(collectPassiveTaskList);
			entity.setCollectOriginalCoordinateList(collectOriginalCoordinateList);
			list.add(entity);
		}
		boolean importFlag = importDemoService.importTasks(list);
		setImportStatusByImportFlag(inputDataProjectNameList, tableName,
				importFlag);
	}

	/**
	 * 根据导入方法返回的数据，来批量更新原始数据库的import_status字段
	 * 
	 * @param inputDataProjectNameList
	 * @param tableName
	 * @param importFlag
	 */
	private void setImportStatusByImportFlag(
			List<InputDataProjectName> inputDataProjectNameList,
			String tableName, boolean importFlag) throws Exception {
		String ids = collectIds(inputDataProjectNameList);
		Integer importStatusFlag = null;
		if (importFlag) {// 导入成功批量修改导入成功字段
			// 收集id
			importStatusFlag = 1;
			collectDataSelectDao.updateImportStatusByTaskIdsAndImportStatus(
					ImportTaskConstant.getImportDataSourceKey(), ids,
					tableName, importStatusFlag);
		} else {
			importStatusFlag = 2;
			collectDataSelectDao.updateImportStatusByTaskIdsAndImportStatus(
					ImportTaskConstant.getImportDataSourceKey(), ids,
					tableName, importStatusFlag);
		}
	}

	/**
	 * 采集id集合
	 * 
	 * @param inputDataProjectNameList
	 * @return
	 */
	private String collectIds(
			List<InputDataProjectName> inputDataProjectNameList) {
		String ids = "-1";
		for (InputDataProjectName entity : inputDataProjectNameList) {
			ids += "," + entity.getTaskId().toString();
		}
		return ids;
	}

	/**
	 * 采集异常，返回前台显示
	 */
	@Override
	public String getExceptionAllinformation(Exception e) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw, true);
		e.printStackTrace(pw);
		pw.flush();
		sw.flush();
		return sw.toString();
	}
	
	// 创建数据
	public static void main(String[] args) throws SQLException {
		Connection connection = null;
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			System.out.println("找不到驱动程序类 ，加载驱动失败！");
			e.printStackTrace();
		}
		// 连接MySql数据库，用户名和密码都是root
		String url = "jdbc:mysql://123.57.213.13:3304/import_passive_task_dev";
		String username = "importuser";
		String password = "aA111111";
		try {
			connection = DriverManager.getConnection(url, username, password);
		} catch (SQLException se) {
			System.out.println("数据库连接失败！");
			se.printStackTrace();
		}
		String tableName = "input_data_20150409_projectname_copy";
		Statement preparedStatement = connection.createStatement();
		for (int i = 1; i <= 24000; i++) {
			String sqlString = "insert into "
					+ tableName
					+ " ( task_id,task_package_name,task_demo_pic,task_city,"
					+ "task_type,address_geocode,address_type,address_all,coord_x,coord_y,coord_type,x,y,geocode_level,"
					+ "geocode_score,project_name,ad_name,ad_start_time,ad_end_time) values("
					+ ""
					+ i
					+ ",null,null,\"北京市\",null,\"云景里\",\"公交站\",null,null,null,null,null,null,null,null,null,null,null,null);";
			preparedStatement.executeUpdate(sqlString);
			System.out.println("当前执行到task_id==" + i + "条");
		}
	}
}

package cn.dataup.importtask.web.controller;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.dataup.importtask.entity.MyTableEntity;
import cn.dataup.importtask.service.SelectService;

@Controller
public class ImportPassiveTaskController {
	@Autowired
	private SelectService selectService;
	private Logger logger = LogManager.getLogger(this.getClass());

	/**
	 * 首页－获取所有的表名
	 * @param map
	 * @return
	 */
	@RequestMapping(value = "/master", method = RequestMethod.GET)
	public String index(Map<String, Object> map) {
		logger.info("map入参========>" + map);
//		这个实体是做什么用的？
		map.put("table", new MyTableEntity());
//		过滤数据库表标识
		String startWtih = "input_data_";
		List<String> result = selectService.getTables(startWtih);
		map.put("tableList", result);
		return "/jsp/index";
	}

	/**
	 * 计算GeoCoding，同时批量导入原始数据库
	 * @param tableName 数据库表名
	 * @return
	 * @throws Exception 
	 */
	@RequestMapping(value = "/setGeoCoding", method = RequestMethod.POST)
	public @ResponseBody String setGeoCode(
			@RequestParam("tableName") String tableName) throws Exception {
		logger.info("tableName入参========>" + tableName);
//		1为：计算GeoCoding
		final Integer geoOrImport = 1;
		Long total = null;
		long beginTime = new Date().getTime();
		try {
			total = selectService.setGeocodingOrImportData(tableName,geoOrImport);
		} catch (Exception e) {
			return selectService.getExceptionAllinformation(e);
		}
		long endTime = new Date().getTime();
//		显示出来进度
		Long fallCalculateGeo = selectService.getCountSuccessCalculateGeoCoding(tableName,geoOrImport);
		return "{\"total\":\""+total+"\",\"fall\":\""+fallCalculateGeo+"\",\"errorLog\":\""+fallCalculateGeo+"\",\"time\":\""+(endTime-beginTime)+"\"}";
	}

	/**
	 * 导入新的数据库表中
	 * @param tableName 表名
	 * @return
	 * @throws Exception 
	 */
	@RequestMapping(value = "/importData", method = RequestMethod.POST)
	public @ResponseBody String importTask(
			@RequestParam("tableName") String tableName) throws Exception {
		logger.info("tableName入参========>" + tableName);
		final Integer geoOrImport = 2;// 2：导入操作
		Long total = null;
		long beginTime = new Date().getTime();
		try {
			total = selectService.setGeocodingOrImportData(tableName,geoOrImport);
		} catch (Exception e) {
			return selectService.getExceptionAllinformation(e);
		}
		long endTime = new Date().getTime();
		// 显示出来进度
		Long fallImport = selectService.getCountSuccessCalculateGeoCoding(tableName,geoOrImport);
		return "{\"total\":\""+total+"\",\"fall\":\""+fallImport+"\",\"time\":\""+(endTime-beginTime)+"\"}";
	}
}

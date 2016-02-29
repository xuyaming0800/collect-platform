package com.gd.app.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.Resource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import autonavi.online.framework.property.PropertiesConfig;
import autonavi.online.framework.property.PropertiesConfigUtil;

import com.gd.app.appenum.AuditStatusEnum;
import com.gd.app.appenum.TaskTypeEnum;
import com.gd.app.dao.AgentComplaintDao;
import com.gd.app.dao.AgentLocationDao;
import com.gd.app.dao.AgentTaskBaseDao;
import com.gd.app.dao.TaskScoreDao;
import com.gd.app.entity.AgentTask;
import com.gd.app.entity.DistrictQueryEntity;
import com.gd.app.entity.ScoreLevel;
import com.gd.app.entity.TaskAppEntity;
import com.gd.app.exception.AppException;
import com.gd.app.exception.AppExceptionEnum;
import com.gd.app.exception.SearchException;
import com.gd.app.service.AgentTaskService;
import com.gd.app.util.CoordinateConvert;
import com.gd.app.util.DateUtil;
import com.gd.app.util.GISTool;
import com.gd.app.util.LanLngs;

//@Service("agentTaskService")
public class AgentTaskServiceImpl implements AgentTaskService {
	private Logger logger = LogManager.getLogger(this.getClass());
	private static String PROVINCE_END = "0000";
	private static String CITY_END = "00";
	private PropertiesConfig pc=null;
	private static ConcurrentHashMap<String, String> municipalitiesMap = new ConcurrentHashMap<String, String>();
	static {
        municipalitiesMap.put("310100", "上海城区");
        municipalitiesMap.put("310200", "上海郊县");
        municipalitiesMap.put("110100", "北京城区");
        municipalitiesMap.put("110200", "北京郊县");
        municipalitiesMap.put("500100", "重庆城区");
        municipalitiesMap.put("500200", "重庆郊县");
        municipalitiesMap.put("120100", "天津城区");
        municipalitiesMap.put("120200", "天津郊县");
    }
	public AgentTaskServiceImpl()throws Exception{
		pc=PropertiesConfigUtil.getPropertiesConfigInstance();
	}
	
	@Resource
	AgentComplaintDao agentComplaintDao;
	@Resource
	AgentTaskBaseDao agentTaskBaseDao;
	@Resource
	AgentLocationDao agentLocationDao;
	@Resource
	TaskScoreDao taskScoreDao;
	@Resource
	private SearchEngineServiceImp searchEngineService;

	@Override
	public void saveAgentComplaint(String phone, String userName, String content) {
		try {
			agentComplaintDao.addAgentComplaint(userName, phone, content);
		} catch (Exception e) {
			logger.error("保存用户意见失败", e);
			throw new AppException(AppExceptionEnum.PAGE_QUERY_ERROR);
		}

	}

	@Override
	public void saveAgentComplaint(String phone, String userName,
			String content, String deviceSys, String deviceModel) {
		try {
			agentComplaintDao.addAgentComplaint(userName, phone, content,
					deviceSys, deviceModel);
		} catch (Exception e) {
			logger.error("保存用户意见失败", e);
			throw new AppException(AppExceptionEnum.PAGE_QUERY_ERROR);
		}

	}

	@Override
	@SuppressWarnings("unchecked")
	public List<AgentTask> queryTotalGroupByStatus(String userName) {
		List<AgentTask> resultList = new ArrayList<AgentTask>();
		List<?> list = null;

		/**
		 * sql语句调整，统计用户每种状态下任务数据 由之前的 group by 改成 union all
		 * 连接三个sql语句（用户下每种状态查询一次）
		 * */
		try {
			list = (List<?>) agentTaskBaseDao.queryBaseStatusCount(userName);
		} catch (Exception e) {
			logger.error("查询用户任务[状态]失败", e);
			throw new AppException(AppExceptionEnum.QUERY_ERROR);
		}
		if ((list == null) || (list.size() == 0)) {
			return resultList;
		}
		int totalCount = 0;
		AgentTask task = null;

		/** 提交待审 */

		Map<String, Object> map = (Map<String, Object>) list.get(0);
		int t1 = ((Long) map.get("total")).intValue();
		totalCount = totalCount + t1;
		task = new AgentTask();
		task.setTaskstatus("0");
		task.setTotal(t1);
		resultList.add(task);

		/** 审核通过 */
		if (map != null) {
			map.clear();
		}
		map = (Map<String, Object>) list.get(1);
		int t2 = ((Long) map.get("total")).intValue();
		totalCount = totalCount + t2;
		task = new AgentTask();
		task.setTaskstatus("1");
		task.setTotal(t2);
		resultList.add(task);

		/** 审核不通过 */
		if (map != null) {
			map.clear();
		}
		map = (Map<String, Object>) list.get(2);
		int t3 = ((Long) map.get("TOTAL")).intValue();
		totalCount = totalCount + t3;
		task = new AgentTask();
		task.setTaskstatus("2");
		task.setTotal(t3);
		resultList.add(task);

		/*
		 * while (lt.hasNext()) { task = new AgentTask(); ListOrderedMap map =
		 * (ListOrderedMap) lt.next(); String status =
		 * map.get("STATUS").toString(); int total = ((BigDecimal)
		 * map.get("total")).intValue(); totalCount = totalCount + total;
		 * task.setTaskstatus(status); task.setTotal(total);
		 * resultList.add(task); }
		 */
		// 总数
		task = new AgentTask();
		task.setTaskstatus("3");
		task.setTotal(totalCount);
		resultList.add(task);

		return resultList;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<ScoreLevel> getAllScoreLevel() {

		List<ScoreLevel> l = (List<ScoreLevel>) taskScoreDao.getAllScoreLevel();
		return l;
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<AgentTask> queryAgentTask(String userName, int taskStatus,
			int endResult, int beginResult, List<ScoreLevel> l1, String version) {
		List<?> list;
		List<AgentTask> resultList = new ArrayList<AgentTask>();
		// 全部
		try {
			if (taskStatus == 3) {
				list = (List<?>) agentTaskBaseDao.queryUserTask(userName,
						taskStatus, beginResult, endResult, true);
			} else {
				list = (List<?>) agentTaskBaseDao
						.queryUserTask(userName, taskStatus, beginResult,
								endResult - beginResult, false);
			}
			if ((list == null) || (list.size() == 0))
				return resultList;

			Iterator<?> lt = list.iterator();
			while (lt.hasNext()) {
				AgentTask task = new AgentTask();
				Map<String, Object> map = (Map<String, Object>) lt.next();
				String taskId = map.get("TASK_ID") == null ? "" : map.get(
						"TASK_ID").toString();
				String status = map.get("STATUS").toString();
				String dataName = map.get("DATA_NAME").toString();
				String baseid = map.get("ID").toString();
				Object canAppealStr = map.get("CAN_APPEAL");
				int canAppeal = (canAppealStr==null?1:(Integer)canAppealStr);
				String temp = "";
				String subTime = DateUtil.parseString(new Date(),
						"yyyy-MM-dd HH:mm:ss");
				// Date
				// maxTime=DateUtil.convertStringToDate("yyyy-MM-dd HH:mm:ss",
				// "2014-04-23 00:00:00");
				// Date
				// maxTime=DateUtil.convertStringToDate("yyyy-MM-dd HH:mm:ss",
				// "2014-04-25 00:00:00");
				Date maxTime = DateUtil.convertStringToDate(
						"yyyy-MM-dd HH:mm:ss", "2014-04-30 00:00:00");
				if (map.get("SUBMIT_TIME") != null) {
					subTime = map.get("SUBMIT_TIME").toString();
				}
				Date time = DateUtil.convertStringToDate("yyyy-MM-dd HH:mm:ss",
						subTime);
				// 旧版本兼容模式
				if (version == null || version.equals("")) {
					int score = 0;
					if (map.get("SCORE") != null) {
						score = ((BigDecimal) map.get("SCORE")).intValue();
					}
					if (map.get("SCORE_NEW") != null) {
						String score_new = map.get("SCORE_NEW").toString();
						if (!score_new.equals("")) {
							if (score_new.indexOf(".") != -1)
								score_new = score_new.substring(0,
										score_new.indexOf("."));
							score = new Integer(score_new).intValue();
						}

					}
					task.setScore(score + "");
				} else {// 新版本 若被动任务且ID=1 则直接置为空不显示 其他被动任务记录分数 同时缓存分数 若主动任务
						// 直接记录分数 不缓存分数
						// 主动任务在时间点后审核回传会自动套用新模式
					task.setScore("");
					if (map.get("SCORE_ID") != null && status.equals("1")) {
						String score_id = map.get("SCORE_ID").toString();
						if (!score_id.equals("") && !score_id.equals("1")) {
							for (ScoreLevel obj : l1) {
								if (obj.getId().toString().equals(score_id)) {
									task.setScore(obj.getPrice());
									if (obj.getIsPassive().equals("1")) {
										// 被动任务分数缓存 后面恢复
										temp = obj.getPrice();
									}
									break;
								}
							}
						}
					}
					// 时间点前提交 直接置为空不显示 主动任务在时间点后提交的一定不会进入此判断
					if (time.getTime() < maxTime.getTime()
							|| !status.equals("1")) {
						task.setScore("");
					}
					// 处理当被动任务做的时候已经客户端上线 且在是时间点之前提交的 直接给予分数显示 通过被动任务缓存写回
					if (task.getScore().equals("") && !temp.equals("")
							&& status.equals("1")) {
						task.setScore(temp);
					}
				}

				String taskType = "";
				if (map.get("TASK_TYPE") != null) {
					taskType = getTaskType(map.get("TASK_TYPE").toString());
				}
				String desc = "";
				if (map.get("COMMENTS") != null) {
					desc = map.get("COMMENTS").toString();
				}
				// 设值
				task.setDataname(dataName);
				task.setSubmittime(subTime);
				task.setTaskid(taskId);
				task.setTaskstatus(AuditStatusEnum.getMessageByCode(status));
				task.setTasktype(taskType);
				task.setBaseid(baseid);
				task.setCanAppeal(canAppeal);
				task.setDesc(desc);
				resultList.add(task);
			}

		} catch (Exception e) {
			logger.error("查询用户任务[状态]失败", e);
			throw new AppException(AppExceptionEnum.QUERY_ERROR);
		}
		return resultList;
	}

	@SuppressWarnings("unchecked")
	@Override
	public int queryAgentCount(String userName, int taskStatus) {
		int count = 0;
		List<?> list = null;
		try {
			if (taskStatus == 3) {
				list = (List<?>) agentTaskBaseDao.queryUserTaskCount(userName,
						taskStatus, true);
			} else {
				list = (List<?>) agentTaskBaseDao.queryUserTaskCount(userName,
						taskStatus, false);
			}
			if ((list != null) && (list.size() > 0)) {
				Map<String, Object> map = (Map<String, Object>) list.get(0);
				count = ((Long) map.get("total")).intValue();
			}
		} catch (Exception e) {
			logger.error("查询用户审核状态下的量失败!", e);
			throw new AppException(AppExceptionEnum.QUERY_ERROR);
		}
		return count;
	}

	private String getTaskType(String code) {
		return TaskTypeEnum.getMessageByCode(code);
	}

	@Override
	public List<Map<String, String>> queryAutoTask(String x, String y,
			String radius, int pageSize, int pageNo, boolean isGPS) {
		double currX = 0.0;
		double currY = 0.0;
		String adCode = "";
		try {
			// 如果是GPS定位,需要做偏转 若是lbs则不需要偏
			if (isGPS) {
				String offsetCorrdinate = CoordinateConvert.GPS2Deflexion(x
						+ "," + y);
				String[] offsetXY = offsetCorrdinate.split(",");
				x = offsetXY[0];
				y = offsetXY[1];
			}

			currX = Double.parseDouble(x);
			currY = Double.parseDouble(y);

			com.mapabc.spatial.RegionSearch rs = com.mapabc.spatial.RegionSearch
					.getInstance();
			adCode = rs.getPointADCode(currX, currY);

		} catch (Exception e) {
			e.printStackTrace();
			logger.error("半径和经纬度格式不正确 x=" + x + ",y=" + y + ",radius=" + radius);
			throw new AppException(AppExceptionEnum.PARAM_X_RADIUS);
		}

		Map<String, Object> paraMap = new HashMap<String, Object>();
		paraMap.put("range", Integer.parseInt(radius));
		paraMap.put("x", currX + "");
		paraMap.put("y", currY + "");
		paraMap.put("sortField", new String[] { "location" });
		paraMap.put("sortType", new String[] { "asc" });
		paraMap.put("adcode", adCode);
		paraMap.put("page", pageNo);
		paraMap.put("number", 200);

		HashMap<String, Object[]> tmpMap = new HashMap<String, Object[]>();
		tmpMap.put("status", new Object[] { "0", "1", "3" });
		paraMap.put("customMap", tmpMap);

		List<Map<String, Object>> list = null;
		try {
			/**
			 * 此处暂时只查agent_task_base 的索引 agent_main_address的索引需要切片
			 * 同时增加一个状态status=1
			 */
			list = searchEngineService
					.findAutoTaskDataFromSearchEngine(paraMap);
		} catch (SearchException e) {
			logger.error("搜索引擎异常，无法查询附近的被动任务 ", e);
			throw new AppException(AppExceptionEnum.PAGE_QUERY_ERROR);
		}
		if (list == null || list.size() == 0) {
			return null;
		}
		return processList(list);
	}

	@SuppressWarnings("unused")
	private List<Map<String, String>> processList(List<Map<String, Object>> list) {
		List<Map<String, String>> dataList = new ArrayList<Map<String, String>>();
		for (Map<String, Object> map : list) {
			Map<String, String> entity = new HashMap<String, String>();
			String submitStatus = "0";
			String x = "0.0";
			String y = "0.0";
			String taskType = "0";
			String dataName = "";
			String taskId = "";
			if (map.get("submitStatus") != null) {
				submitStatus = map.get("submitStatus").toString();
			}
			if (map.get("x") != null) {
				x = map.get("x").toString();
			}
			if (map.get("y") != null) {
				y = map.get("y").toString();
			}

			if (map.get("id") != null) {
				taskId = map.get("id").toString();
			}
			if (map.get("data_name") != null) {
				dataName = map.get("data_name").toString();
			}
			if (map.get("task_type") != null) {
				taskType = map.get("task_type").toString();
			}

			entity.put("id", taskId);
			entity.put("dataname", dataName);
			entity.put("tasktype", taskType);
			entity.put("submitStatus", submitStatus);
			entity.put("x", "0.0");
			entity.put("y", "0.0");
			entity.put("distance", "--");
			entity.put("angle", "--");
			dataList.add(entity);
		}
		return dataList;
	}

	@Override
	public int queryDistrictCountByKeyWord(String adCode, String dataName) {
		int count = 0;
		if (!adCode.endsWith(CITY_END)) {
			count = fetchDistrictByKeyWordCount(adCode, dataName);
		} else {
			count = fetchCityByKeyWordCount(adCode, dataName);
		}
		return count;
	}

	@SuppressWarnings("unchecked")
	private int fetchDistrictByKeyWordCount(String adCode, String dataName) {
		List<?> list = null;
		try {
			list = (List<?>) agentTaskBaseDao.getPassiveTaskCountByAdcode(
					adCode, "%" + dataName + "%");
			if (list == null || list.size() == 0)
				return 0;
			return new Integer(((Map<String, Object>) list.get(0)).get("total")
					.toString());
		} catch (Exception e) {
			logger.error("统计区务量失败!", e);
			throw new AppException(AppExceptionEnum.QUERY_ERROR);
		}
	}

	private int fetchCityByKeyWordCount(String adCode, String dataName) {
		int maxAdcode = 0;
		int minAdcode = 0;
		String subStr = "";
		int count = 0;
		// 上海之类市
		if (adCode.endsWith(PROVINCE_END)) {
			subStr = adCode.substring(0, adCode.lastIndexOf(PROVINCE_END));
			subStr = String.valueOf(Integer.parseInt(subStr) + 1)
					+ PROVINCE_END;
		} else if (adCode.endsWith(CITY_END)) {
			subStr = adCode.substring(0, adCode.lastIndexOf(CITY_END));
			subStr = String.valueOf(Integer.parseInt(subStr) + 1) + CITY_END;
		}
		minAdcode = Integer.parseInt(adCode);
		maxAdcode = Integer.parseInt(subStr);
		count = staticsCityTaskCount(minAdcode, maxAdcode, dataName);
		return count;
	}

	/**
	 * 统计市任务量
	 * 
	 * @param minAdcode
	 * @param maxAdcode
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private int staticsCityTaskCount(int minAdcode, int maxAdcode,
			String dataName) {
		List<?> list = null;
		try {
			list = (List<?>) agentTaskBaseDao.getPassiveTaskCountByAdcodeScope(
					minAdcode, maxAdcode, "%" + dataName + "%");
			if (list == null || list.size() == 0)
				return 0;
			return new Integer(((Map<String, Object>) list.get(0)).get("total")
					.toString());
		} catch (Exception e) {
			logger.error("统计市/省任务量失败!", e);
			throw new AppException(AppExceptionEnum.QUERY_ERROR);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<TaskAppEntity> queryTaskPageByDistrictId(String districtId,
			String userName, String dataName, int endResult, int beginResult) {
		List<Map<String, Object>> list = null;
		String subStr = "";
		int minAdcode = 0;
		int maxAdcode = 0;
		try {
			if (!districtId.endsWith(CITY_END)) {
				list = (List<Map<String, Object>>) agentTaskBaseDao
						.getPassiveTaskByAdcode(districtId, "%" + dataName
								+ "%", beginResult, endResult);
			} else {
				if (districtId.endsWith(PROVINCE_END)) {
					subStr = districtId.substring(0,
							districtId.lastIndexOf(PROVINCE_END));
					subStr = String.valueOf(Integer.parseInt(subStr) + 1)
							+ PROVINCE_END;
				} else {
					subStr = districtId.substring(0,
							districtId.lastIndexOf(CITY_END));
					subStr = String.valueOf(Integer.parseInt(subStr) + 1)
							+ CITY_END;
				}
				minAdcode = Integer.parseInt(districtId);
				maxAdcode = Integer.parseInt(subStr);
				list = (List<Map<String, Object>>) agentTaskBaseDao
						.getPassiveTaskByAdcodeScope(minAdcode, maxAdcode, "%"
								+ dataName + "%", beginResult, endResult);
			}
		} catch (Exception e) {
			logger.error("根据区id查询该区所有任务!", e);
			throw new AppException(AppExceptionEnum.QUERY_ERROR);
		}
		if (list == null || list.size() == 0) {
			return null;
		}
		return processListTask(list);
	}

	private List<TaskAppEntity> processListTask(List<Map<String, Object>> list) {
		List<TaskAppEntity> resultTaskList = new ArrayList<TaskAppEntity>();
		for (Map<String, Object> map : list) {
			TaskAppEntity task = new TaskAppEntity();
			task.setId(((Long) map.get("id")).longValue());
			task.setDataname(map.get("DATA_NAME").toString());
			task.setTasktype(((Integer) map.get("TASK_TYPE")).intValue());
			String submitStatus = "0";
			String poi = "";
			String prename = "";
			if (map.get("submitStatus") != null) {
				submitStatus = map.get("submitStatus").toString();
			}
			if (map.get("poi") != null) {
				poi = map.get("poi").toString();
			}
			if (map.get("prename") != null) {
				prename = map.get("prename").toString();
			}
			if (map.get("lost_count") == null
					|| map.get("lost_count").toString().equals("")) {
				task.setScore("-1");
			} else {
				task.setScore(map.get("lost_count").toString());
			}
			task.setSubmitStatus(submitStatus);
			task.setPoi(poi);
			task.setPrename(prename);

			resultTaskList.add(task);
		}
		return resultTaskList;
	}

	@Override
	public List<TaskAppEntity> queryTaskByRadiusInSearch(int taskType,
			String x, String y, String radius, String userName,
			String dataName, int page, int num, boolean isGPS) {
		double currX = 0.0;
		double currY = 0.0;

		try {
			// 如果是GPS定位,需要做偏转 若是lbs则不需要偏
			if (isGPS) {
				String offsets = CoordinateConvert.GPS2Deflexion(x + "," + y);
				String[] offsetXY = offsets.split(",");
				x = offsetXY[0];
				y = offsetXY[1];
			}
			currX = Double.parseDouble(x);
			currY = Double.parseDouble(y);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("半径和经纬度格式不正确 x=" + x + ",y=" + y + ",radius=" + radius);
			throw new AppException(AppExceptionEnum.PARAM_X_RADIUS);
		}

		Map<String, Object> paraMap = new HashMap<String, Object>();
		paraMap.put("keyword", "*" + dataName);
		paraMap.put("range", Integer.parseInt(radius));
		paraMap.put("x", currX + "");
		paraMap.put("y", currY + "");
		paraMap.put("page", page);
		paraMap.put("number", num);
		paraMap.put("sortField", new String[] { "location" });
		paraMap.put("sortType", new String[] { "asc" });

		HashMap<String, Object[]> tmpMap = new HashMap<String, Object[]>();
		tmpMap.put("data_source", new Object[] { "1" });
		tmpMap.put("task_type", new Object[] { taskType });
		tmpMap.put("status", new Object[] { "0" });
		paraMap.put("customMap", tmpMap);

		List<Map<String, Object>> list = null;

		try {
			list = searchEngineService.findTaskAllDataFromSearchEngine(paraMap);
		} catch (SearchException e) {
			logger.error("搜索引擎异常，无法查询附近的被动任务 ", e);
			throw new AppException(AppExceptionEnum.PAGE_QUERY_ERROR);
		}

		return processListBySearch(list);
	}

	private List<TaskAppEntity> processListBySearch(
			List<Map<String, Object>> list) {
		List<TaskAppEntity> resultTaskList = new ArrayList<TaskAppEntity>();
		Iterator<Map<String, Object>> lt = list.iterator();
		long total = 0;
		if (list.size() > 0)
			total = new Long(list.get(list.size() - 1).get("total").toString());
		while (lt.hasNext()) {
			TaskAppEntity task = new TaskAppEntity();
			Map<String, Object> map = (Map<String, Object>) lt.next();
			if (map.get("total") == null) {
				task.setTotal(total);
				task.setId(Long.parseLong(map.get("id").toString()));
				task.setDataname(map.get("data_name").toString());
				task.setTasktype(Integer.parseInt(map.get("task_type")
						.toString()));
				// String submitStatus = "4";
				String poi = "";
				String prename = "";
				// if (map.get("submitStatus") != null) {
				// submitStatus = map.get("submitStatus").toString();
				// }
				if (map.get("poi") != null) {
					poi = map.get("poi").toString();
				}
				if (map.get("prename") != null) {
					prename = map.get("prename").toString();
				}
				if (map.get("lost_count") == null
						|| map.get("lost_count").toString().equals("")) {
					task.setScore("-1");
				} else {
					task.setScore(map.get("lost_count").toString());
				}
				task.setSubmitStatus("4");
				task.setPoi(poi);
				task.setPrename(prename);

				resultTaskList.add(task);
			}

		}
		return resultTaskList;
	}

	@SuppressWarnings("unchecked")
	@Override
	public int queryTaskByRadiusCount(int taskType, String x, String y,
			String radius, String dataName, boolean isGPS) {
		double currX = 0.0;
		double currY = 0.0;
		double rad = 0.0;

		double[] corrdinate = null;
		try {
			// 如果是GPS定位,需要做偏转 若是lbs则不需要偏
			if (isGPS) {
				String offsets = CoordinateConvert.GPS2Deflexion(x + "," + y);
				String[] offsetXY = offsets.split(",");
				x = offsetXY[0];
				y = offsetXY[1];
			}
			currX = Double.parseDouble(x);
			currY = Double.parseDouble(y);
			rad = Double.parseDouble(radius);
			corrdinate = getCorrdinate(currX, currY, rad);
		} catch (Exception e) {
			logger.error("半径和经纬度格式不正确 x=" + x + ",y=" + y + ",radius=" + radius);
			throw new AppException(AppExceptionEnum.PARAM_X_RADIUS);
		}

		List<?> list = null;
		try {
			list = (List<?>) agentTaskBaseDao.getNearPassiveTaskCount(taskType,
					corrdinate[0], corrdinate[1], corrdinate[2], corrdinate[3],
					"%" + dataName + "%");
		} catch (Exception e) {
			logger.error("查询附近任务量失败!", e);
			throw new AppException(AppExceptionEnum.PAGE_QUERY_ERROR);
		}
		if ((list == null) || (list.size() == 0))
			return 0;

		return new Integer(((Map<String, Object>) list.get(0)).get("total")
				.toString());
	}

	private double[] getCorrdinate(double currX, double currY, double rad) {
		double[] corrdinates = new double[4];
		try {
			LanLngs lanLngs = GISTool.getOtherPoint(rad, currX, currY);
			corrdinates[1] = lanLngs.getMaxX();
			corrdinates[0] = lanLngs.getMinX();
			corrdinates[3] = lanLngs.getMaxY();
			corrdinates[2] = lanLngs.getMinY();
		} catch (Exception e) {
			logger.error("计算坐标范围异常");
			throw new AppException(AppExceptionEnum.PARAM_X_RADIUS);
		}
		return corrdinates;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<TaskAppEntity> queryTaskByRadiusRd(int taskType, String x,
			String y, String radius, String userName, String dataName,
			int endResult, int beginResult, boolean isGPS) {
        double currX = 0.0;
        double currY = 0.0;
        double rad = 0.0;
        double[] corrdinate = null;

        try {
            //如果是GPS定位,需要做偏转 若是lbs则不需要偏
            if (isGPS) {
                String offsets = CoordinateConvert.GPS2Deflexion(x + "," + y);
                String[] offsetXY = offsets.split(",");
                x = offsetXY[0];
                y = offsetXY[1];
            }
            currX = Double.parseDouble(x);
            currY = Double.parseDouble(y);
            rad = Double.parseDouble(radius);
            corrdinate = getCorrdinate(currX, currY, rad);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("半径和经纬度格式不正确 x=" + x + ",y=" + y + ",radius=" + radius);
            throw new AppException(AppExceptionEnum.PARAM_X_RADIUS);
        }

        // 查询数据库
        List<Map<String,Object>> list = (List<Map<String,Object>>)agentTaskBaseDao.getNearPassiveTask(taskType, corrdinate[0], corrdinate[1], corrdinate[2], corrdinate[3], "%"+ dataName +"%", beginResult, endResult);
        if (list == null||list.size()==0)
            return null;
        return processListTask(list);
	}

	@Override
	public int queryUserCountByKeyWord(String userName, String dataName) {
		int count=0;
		count=fetchUserByKeyWordCount(userName,dataName);
		return count;
	}
	@SuppressWarnings("unchecked")
	@Override
	public List<TaskAppEntity> queryTaskPageByUser(String userName,
			String dataName, int endResult, int beginResult) {
		List<Map<String,Object>> list = null;
        try {
           list=(List<Map<String,Object>>)agentTaskBaseDao.getAllotPassiveByUser("%"+dataName+"%", userName, beginResult, endResult);
        } catch (Exception e) {
            logger.error("根据用户查询任务!", e);
            throw new AppException(AppExceptionEnum.QUERY_ERROR);
        }
        if (list == null || list.size() == 0) {
            return null;
        }
        return processListTask(list);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<TaskAppEntity> queryTaskPageByUserRd(String userName,
			String dataName, int endResult, int beginResult) {
		List<Map<String,Object>> list = null;
        try {
           list=(List<Map<String,Object>>)agentTaskBaseDao.getAllotPassiveByUserAndRoad("%"+dataName+"%", userName, beginResult, endResult);
        } catch (Exception e) {
            logger.error("根据用户查询任务!", e);
            throw new AppException(AppExceptionEnum.QUERY_ERROR);
        }
        if (list == null || list.size() == 0) {
            return null;
        }
        return processListTask(list);
	}
	@SuppressWarnings("unchecked")
	private int fetchUserByKeyWordCount(String userName, String dataName) {
        List<?> list = null;
        try {
            list = (List<?>)agentTaskBaseDao.getAllotPassiveCountByUser("%"+dataName+"%", userName);
            if (list == null||list.size()==0)
                return 0;
            return new Integer(((Map<String, Object>) list.get(0)).get("total")
					.toString());
        } catch (Exception e) {
            logger.error("统计用户定向任务量失败!", e);
            throw new AppException(AppExceptionEnum.QUERY_ERROR);
        }
   }

	@SuppressWarnings("unchecked")
	@Override
	public List<DistrictQueryEntity> queryTaskCountByAdCode(String adCode) {
		List<DistrictQueryEntity> resultList = new ArrayList<DistrictQueryEntity>();
        //对直辖市的adCode作转换
		int maxAdcode = 0;
	    int minAdcode = 0;
	    String subStr = "";
        if (municipalitiesMap.containsKey(adCode)) {
            adCode = adCode.substring(0, 2) + PROVINCE_END;
        }
        if (!adCode.endsWith(CITY_END)) {
            adCode = adCode.substring(0, 4) + CITY_END;
        }

        if (adCode.endsWith(PROVINCE_END)) {
            subStr = adCode.substring(0, adCode.lastIndexOf(PROVINCE_END));
            subStr = String.valueOf(Integer.parseInt(subStr) + 1) + PROVINCE_END;
        } else if (adCode.endsWith(CITY_END)) {
            subStr = adCode.substring(0, adCode.lastIndexOf(CITY_END));
            subStr = String.valueOf(Integer.parseInt(subStr) + 1) + CITY_END;
        }
        minAdcode = Integer.parseInt(adCode);
        maxAdcode = Integer.parseInt(subStr);
        int count = 0;
        try {
            List<Map<String,Object>> list = (List<Map<String,Object>>)agentTaskBaseDao.polyPassiveTaskByAdcode(minAdcode, maxAdcode);
            if ((list == null) || (list.size() == 0))
                return resultList;

            Map<String, String> statisMap = new HashMap<String, String>();
            long provincTaskCount = 0;
            for (Map<String,Object> map:list) {
                DistrictQueryEntity task = new DistrictQueryEntity();
                count = ((Long) map.get("TOTAL")).intValue();
                String districtId = map.get("DISTRICT_ID").toString();

                provincTaskCount = provincTaskCount + count;
                //把省/市分开
                if (districtId.endsWith(CITY_END) && (!municipalitiesMap.containsKey(districtId))) {
                    statisMap.put(districtId, count + "");
                }
                task.setTotal(count);
                task.setDistrictid(districtId);
                if (!municipalitiesMap.containsKey(districtId)) {
                    resultList.add(task);
                }
            }
            //累计省/市 数据
            for (String key : statisMap.keySet()) {
                if (key.endsWith(PROVINCE_END)) {
                    statisMap.put(key, provincTaskCount + "");
                } else {
                    String startWith = key.substring(0, 4);
                    int cityTaskCount = 0;
                    for (DistrictQueryEntity entity : resultList) {
                        if (entity.getDistrictid().startsWith(startWith)) {
                            cityTaskCount = cityTaskCount + entity.getTotal();
                        }
                    }
                    statisMap.put(key, cityTaskCount + "");
                }
            }
            //补充省/市名称
            for (DistrictQueryEntity entity : resultList) {
                String key = entity.getDistrictid();
                String cityName = queryCityName(key);
                entity.setDistrictname(cityName);
                if (statisMap.containsKey(key)) {
                    entity.setTotal(Integer.parseInt(statisMap.get(key)));
                }
            }
        } catch (Exception e) {
            logger.error("统计区县任务量失败!", e);
            throw new AppException(AppExceptionEnum.QUERY_ERROR);
        }
        return resultList;
	}
	/**
     * 获取省/市名称
     * 
     * @param adCode
     * 
     */
    @SuppressWarnings("unchecked")
	private String queryCityName(String adCode) {
        String cityName = "";
        try {
            List<Map<String,Object>> list = (List<Map<String,Object>>)agentLocationDao.getAgentLocationByAdcode(adCode);
            if (list == null || list.size() < 1)
                return cityName;
            Map<String,Object> map = list.get(0);
            if (map.get("county") != null) {
                return map.get("county").toString();
            }
            if (map.get("city") != null) {
                return map.get("city").toString();
            } else if (map.get("province") != null) {
                return map.get("province").toString();
            }
            return cityName;
        } catch (Exception e) {
            logger.error("查询省/市名称失败!", e);
            throw new AppException(AppExceptionEnum.QUERY_ERROR);
        }
    }

	@SuppressWarnings({"unchecked" })
	@Override
	public Map<String, Integer> queryMyConquer(String userName) {
		Map<String, Integer> m=new HashMap<String, Integer>();
		List<Map<String,Object>> l=(List<Map<String,Object>>)agentTaskBaseDao.queryCityCountByUser(userName);
		if(l==null||l.size()==0){
			m.put("count", 0);
		}
		else{
			Map<String,Object> m1=(Map<String,Object>)l.get(0);
			m.put("count", ((Long)m1.get("total")).intValue());
		}
		return m;
	}

	@Override
	public void addPassiveTaskBack(String userName, String taskId,
			String dataName, String errorCode, String comment, String deviceInfo) {
		if(comment==null)comment="";
		if(errorCode==null)errorCode="";
		if(deviceInfo==null)deviceInfo="";
		try {
			agentTaskBaseDao.addPassiveTaskBack(userName, taskId, dataName, errorCode, comment, deviceInfo);
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			throw new AppException(AppExceptionEnum.PAGE_QUERY_ERROR);
		}
		
	}

	@Override
	public String getProperty(String key) {
		// TODO Auto-generated method stub
		return (String)pc.getProperty(key);
	}

}

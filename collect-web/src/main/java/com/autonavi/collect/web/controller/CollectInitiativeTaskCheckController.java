package com.autonavi.collect.web.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.autonavi.collect.entity.ActiveTaskAroundSearchEntity;
import com.autonavi.collect.entity.ActiveTaskAroundSearchResultEntity;
import com.autonavi.collect.exception.BusinessExceptionEnum;
import com.autonavi.collect.exception.BusinessRunException;
import com.autonavi.collect.service.TaskCollectUtilService;
import com.autonavi.collect.service.TaskSearchService;
import com.autonavi.collect.web.bean.CheckDataInfoBean;
import com.autonavi.collect.web.bean.CheckInitiativeEntity;
import com.autonavi.collect.web.bean.ResultDesc;
import com.autonavi.collect.web.bean.ResultEntity;
import com.autonavi.collect.web.constant.WebConstant.CHECK_TASK_ARROUND;

@Controller
public class CollectInitiativeTaskCheckController extends
		BaseController<CheckInitiativeEntity> {
	public CollectInitiativeTaskCheckController() throws Exception {
		super();
		// TODO Auto-generated constructor stub
	}

	private Logger logger = LogManager.getLogger(this.getClass());
	private final Integer REDIS=1000; 
	@Autowired
	private TaskSearchService taskSearchService;
	@Autowired
	private TaskCollectUtilService taskCollectUtilService;

	@RequestMapping("/queryArroundPointNew")
	public void queryArroundPointNew(HttpServletRequest request,
			HttpServletResponse response) {
		ResultEntity entity = new ResultEntity();
		CheckInitiativeEntity query = new CheckInitiativeEntity();
		query.setCheckType(CHECK_TASK_ARROUND.ARROUND_H5.getCode());
		logger.info("搜索附近已采集的主动任务点(H5)");
		try {
			int check = this.checkCryptorVersion(request, response, query,
					logger, null);
			if (check > 0) {
				if (check == 3) {
					this.writeErrorResult(entity, "401", "系统内部异常", response,
							request);
					return;
				}
				if (check == 1) {
					this.writeErrorResult(entity, "201", "参数无法解析", response,
							request);
					return;
				}
				if (check == 2) {
					this.writeErrorResult(entity, "201", "参数无法解析", response,
							request);
					return;
				}
			} else if (check == -2) {
				this.writeErrorResult(entity, "201", "参数无法解析", response,
						request);
				return;
			} else {
				ActiveTaskAroundSearchEntity bean=new ActiveTaskAroundSearchEntity();
				bean.setX(Double.valueOf(query.getX()));
				bean.setY(Double.valueOf(query.getY()));
				bean.setClazzId(Long.valueOf(query.getCollectClassId()));
				bean.setOwnerId(query.getOwnerId());
				bean.setBatchId(query.getBatchId());
				bean.setImageFlag(query.getImageFlag());
				bean.setRadius(query.getRadius());
				if(query.getBaseId()!=null){
					bean.setBaseId(new Long(query.getBaseId()));
				}
				Boolean flag=taskSearchService.activeH5TaskAroundSearchCheck(bean);
				query.setCheckResult(flag);
				ResultDesc desc=new ResultDesc();
				desc.setCode("0");
				desc.setMsg(BaseController.SUCCESS);
				entity.setStatus(desc);
				entity.setResult(query);
				//entity.setResultData(resultList);
				// entity.setTotalCount(queryResult.getCount().toString());

			}
		} catch (BusinessRunException e) {
			this.writeErrorResult(entity, e.getSqlExpEnum().getCode(), e
					.getSqlExpEnum().getMessage(), response, request);
			return;
			// } catch (BusinessException e) {
			// this.writeErrorResult(entity, e.getSqlExpEnum().getCode(), e
			// .getSqlExpEnum().getMessage(), response, request);
			// return;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			this.writeErrorResult(entity,
					BusinessExceptionEnum.ACTIVE_TASK_AROUND_SEARCH_ERROR.getCode(),
					BusinessExceptionEnum.ACTIVE_TASK_AROUND_SEARCH_ERROR.getMessage(),
					response, request);
		}

		this.writeResult(entity, request, response);
	}
	
	@RequestMapping("/queryArroundPoint")
	public void queryArroundPoint(HttpServletRequest request,
			HttpServletResponse response) {
		ResultEntity entity = new ResultEntity();

		CheckInitiativeEntity query = new CheckInitiativeEntity();
		query.setCheckType(CHECK_TASK_ARROUND.ARROUND.getCode());
		logger.info("搜索附近已采集的主动任务点");
		try {
			int check = this.checkCryptorVersion(request, response, query,
					logger, null);
			if (check > 0) {
				if (check == 3) {
					this.writeErrorResult(entity, "401", "系统内部异常", response,
							request);
					return;
				}
				if (check == 1) {
					this.writeErrorResult(entity, "201", "参数无法解析", response,
							request);
					return;
				}
				if (check == 2) {
					this.writeErrorResult(entity, "201", "参数无法解析", response,
							request);
					return;
				}
			} else if (check == -2) {
				this.writeErrorResult(entity, "201", "参数无法解析", response,
						request);
				return;
			} else {
				ActiveTaskAroundSearchEntity bean=new ActiveTaskAroundSearchEntity();
				bean.setX(Double.valueOf(query.getX()));
				bean.setY(Double.valueOf(query.getY()));
				bean.setClazzId(Long.valueOf(query.getCollectClassId()));
				bean.setOwnerId(query.getOwnerId());
				if(query.getBaseId()!=null){
					bean.setBaseId(new Long(query.getBaseId()));
				}
				Boolean flag=taskSearchService.activeTaskAroundSearchCheck(bean);
				query.setCheckResult(flag);
				ResultDesc desc=new ResultDesc();
				desc.setCode("0");
				desc.setMsg(BaseController.SUCCESS);
				entity.setStatus(desc);
				entity.setResult(query);
				//entity.setResultData(resultList);
				// entity.setTotalCount(queryResult.getCount().toString());

			}
		} catch (BusinessRunException e) {
			this.writeErrorResult(entity, e.getSqlExpEnum().getCode(), e
					.getSqlExpEnum().getMessage(), response, request);
			return;
			// } catch (BusinessException e) {
			// this.writeErrorResult(entity, e.getSqlExpEnum().getCode(), e
			// .getSqlExpEnum().getMessage(), response, request);
			// return;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			this.writeErrorResult(entity,
					BusinessExceptionEnum.ACTIVE_TASK_AROUND_SEARCH_ERROR.getCode(),
					BusinessExceptionEnum.ACTIVE_TASK_AROUND_SEARCH_ERROR.getMessage(),
					response, request);
		}

		this.writeResult(entity, request, response);
	}
	
	@RequestMapping("/queryArroundPoints")
	public void queryArroundPoints(HttpServletRequest request,
			HttpServletResponse response) {
		ResultEntity entity = new ResultEntity();

		CheckInitiativeEntity query = new CheckInitiativeEntity();
		query.setCheckType(CHECK_TASK_ARROUND.POINT.getCode());
		logger.info("搜索附近已采集的主动任务点");
		try {
			int check = this.checkCryptorVersion(request, response, query,
					logger, null);
			if (check > 0) {
				if (check == 3) {
					this.writeErrorResult(entity, "401", "系统内部异常", response,
							request);
					return;
				}
				if (check == 1) {
					this.writeErrorResult(entity, "201", "参数无法解析", response,
							request);
					return;
				}
				if (check == 2) {
					this.writeErrorResult(entity, "201", "参数无法解析", response,
							request);
					return;
				}
			} else if (check == -2) {
				this.writeErrorResult(entity, "201", "参数无法解析", response,
						request);
				return;
			} else {
				ActiveTaskAroundSearchEntity bean=new ActiveTaskAroundSearchEntity();
				bean.setX(Double.valueOf(query.getX()));
				bean.setY(Double.valueOf(query.getY()));
				bean.setClazzId(new Long(query.getCollectClassId()));
				bean.setOwnerId(query.getOwnerId());
				//bean.setUserId(query.getUserId());
				bean.setRadius(REDIS);
				List<ActiveTaskAroundSearchResultEntity> l=taskSearchService.vaildActiveTaskAroundSearch(bean);
				List<CheckInitiativeEntity> result=new ArrayList<CheckInitiativeEntity>();
				for(ActiveTaskAroundSearchResultEntity _entity:l){
					CheckInitiativeEntity ent=new CheckInitiativeEntity();
					String[] locations=_entity.getLocation().split(",");
					if(locations!=null&&locations.length==2){
						ent.setX(locations[0]);
						ent.setY(locations[1]);
					}
					result.add(ent);
				}
				ResultDesc desc=new ResultDesc();
				desc.setCode("0");
				desc.setMsg(BaseController.SUCCESS);
				entity.setStatus(desc);
				entity.setResult(result);
				//entity.setResultData(resultList);
				// entity.setTotalCount(queryResult.getCount().toString());

			}
		} catch (BusinessRunException e) {
			this.writeErrorResult(entity, e.getSqlExpEnum().getCode(), e
					.getSqlExpEnum().getMessage(), response, request);
			return;
			// } catch (BusinessException e) {
			// this.writeErrorResult(entity, e.getSqlExpEnum().getCode(), e
			// .getSqlExpEnum().getMessage(), response, request);
			// return;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			this.writeErrorResult(entity,
					BusinessExceptionEnum.ACTIVE_TASK_AROUND_SEARCH_ERROR.getCode(),
					BusinessExceptionEnum.ACTIVE_TASK_AROUND_SEARCH_ERROR.getMessage(),
					response, request);
		}

		this.writeResult(entity, request, response);
	}

	protected CheckDataInfoBean vaildJsonData(String content,
			CheckInitiativeEntity entity) {
		CheckDataInfoBean bean = new CheckDataInfoBean();
		try {
			JSONObject obj = JSONObject.fromObject(content);
			logger.info("获取的入参为" + obj.toString());
			// 防止参数重复错误
			String regex = "\\[\".*\",\".*\"\\]";
			Pattern pattren = Pattern.compile(regex);
			Matcher mat = pattren.matcher(obj.toString());
			if (mat.find()) {
				bean.setErrorMessage("JSON格式解析错误");
				return bean;
			}
			if(obj.get("ownerId") != null
					&& !obj.get("ownerId").toString().equals("")
					&& !obj.get("ownerId").toString()
							.equalsIgnoreCase("null")){
				entity.setOwnerId(Long.valueOf(obj.get("ownerId").toString()));
			}else{
				entity.setOwnerId(1L);
				logger.warn("兼容模式，设置ownerId为1");
			}
			if(entity.getCheckType().equals(CHECK_TASK_ARROUND.ARROUND.getCode())){
				if (obj.get("collectClassId") != null
						&& !obj.get("collectClassId").toString().equals("")
						&& !obj.get("collectClassId").toString()
								.equalsIgnoreCase("null")) {
					String collectClassId = obj.get("collectClassId").toString();
					entity.setCollectClassId(collectClassId);
				} else {
					bean.setErrorMessage("collectClassId参数为空");
					return bean;
				}
			}
			if(entity.getCheckType().equals(CHECK_TASK_ARROUND.POINT.getCode())){
				if (obj.get("collectClassId") != null
						&& !obj.get("collectClassId").toString().equals("")
						&& !obj.get("collectClassId").toString()
								.equalsIgnoreCase("null")) {
					String collectClassId = obj.get("collectClassId").toString();
					entity.setCollectClassId(collectClassId);
				} else {
					bean.setErrorMessage("collectClassId参数为空");
					return bean;
				}
			}
			
			if(entity.getCheckType().equals(CHECK_TASK_ARROUND.ARROUND_H5.getCode())){
				if (obj.get("collectClassId") != null
						&& !obj.get("collectClassId").toString().equals("")
						&& !obj.get("collectClassId").toString()
								.equalsIgnoreCase("null")) {
					String collectClassId = obj.get("collectClassId").toString();
					entity.setCollectClassId(collectClassId);
				} else {
					bean.setErrorMessage("collectClassId参数为空");
					return bean;
				}
				if (obj.get("imageFlag") != null
						&& !obj.get("imageFlag").toString().equals("")
						&& !obj.get("imageFlag").toString()
								.equalsIgnoreCase("null")) {
					String imageFlag = obj.get("imageFlag").toString();
					entity.setImageFlag(imageFlag);
				} else {
					bean.setErrorMessage("imageFlag参数为空");
					return bean;
				}
				if (obj.get("batchId") != null
						&& !obj.get("batchId").toString().equals("")
						&& !obj.get("batchId").toString()
								.equalsIgnoreCase("null")) {
					String batchId = obj.get("batchId").toString();
					entity.setBatchId(Long.valueOf(batchId));
				} else {
					bean.setErrorMessage("batchId参数为空");
					return bean;
				}
				if (obj.get("radius") != null
						&& !obj.get("radius").toString().equals("")
						&& !obj.get("radius").toString()
								.equalsIgnoreCase("null")) {
					String radius = obj.get("radius").toString();
					entity.setRadius(Integer.valueOf(radius));
				} else {
					bean.setErrorMessage("radius参数为空");
					return bean;
				}
			}
			
			if (obj.get("x") != null && !obj.get("x").toString().equals("")
					&& !obj.get("x").toString().equalsIgnoreCase("null")) {
				String x = obj.get("x").toString();
				entity.setX(x);
			} else {
				bean.setErrorMessage("x参数为空");
				return bean;
			}
			if (obj.get("y") != null && !obj.get("y").toString().equals("")
					&& !obj.get("y").toString().equalsIgnoreCase("null")) {
				String y = obj.get("y").toString();
				entity.setY(y);
			} else {
				bean.setErrorMessage("y参数为空");
				return bean;
			}
			if (obj.get("baseId") != null && !obj.get("baseId").toString().equals("")
					&& !obj.get("baseId").toString().equalsIgnoreCase("null")) {
				String baseId = obj.get("baseId").toString();
				entity.setBaseId(baseId);
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			bean.setErrorMessage("JSON数据格式错误");
		}
		return bean;

	}

}

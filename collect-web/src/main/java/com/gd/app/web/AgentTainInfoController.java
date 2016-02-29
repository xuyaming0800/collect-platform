package com.gd.app.web;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.zip.GZIPInputStream;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.gd.app.entity.AgentTailInfo;
import com.gd.app.entity.ResultEntity;
import com.gd.app.entity.ServletInfoBean;
import com.gd.app.exception.AppException;
import com.gd.app.exception.AppExceptionEnum;
import com.gd.app.service.AgentTailInfoService;

/**
 * android端轨迹上报
 * 
 * @author ziyu.wei
 *
 */
@Controller
public class AgentTainInfoController extends BaseController {
	
	private @Resource AgentTailInfoService service;
	
	private Logger logger = LogManager.getLogger(AgentTainInfoController.class);
	
	private final static SimpleDateFormat sdf = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss");

	@Override
	protected ServletInfoBean vaildJsonData(String content, ServletInfoBean obj) {
		
		return null;
	}
	
	@RequestMapping("/userTrail")
	public void userTrail(HttpServletRequest request,
			HttpServletResponse response) {
		logger.info("客户端调用保存轨迹信息");
		ResultEntity entity = new ResultEntity();
		// 获取文件信息
		String agentTailInfoGzipString = request.getParameter("gpsData");
		
		logger.info("信息：" + agentTailInfoGzipString);
		
		// 解压后得字符串
		String agentTailInfoString = null;
		
		try {
			agentTailInfoString = uncompress(agentTailInfoGzipString);
		} catch (AppException e) {
			writeError(entity, request, response, e);
			return;
		}
		
		//TODO 测试使用，不用对字符串压缩
	//	agentTailInfoString = agentTailInfoGzipString;
		
		List<AgentTailInfo> list = null;
		
		try {
			list = parseMessage(agentTailInfoString, entity);
		} catch (AppException e) {
			logger.error("", e);
			writeError(entity, request, response, e);
			return;
		}
		
		if (null == list) {
			this.writeError(entity, request, response, new AppException(1, "数据为空"));
			return;
		}
		
		try {
			service.batchSaveAgentTailInfos(list);

			// 数据保存成功，处理返回结果
			entity.setCode("0");
			entity.setDesc("success");
			writeResult(entity, request, response);
		} catch (Exception e) {
			// e.printStackTrace();
			logger.error("轨迹信息保存失败", e);
			// 处理错误后返回结果
			entity.setCode("1");
			writeErrorResult(entity,
					AppExceptionEnum.TAIL_SAVE_ERROR.getCode(),
					AppExceptionEnum.TAIL_SAVE_ERROR.getMessage(), response,
					request);
		}
	}
	
	/**
	 * 解压缩
	 * 
	 * @param str
	 * @return
	 * @throws IOException
	 */
	public String uncompress(String str) {
		if (str == null || str.length() == 0) {
			return str;
		}

		String result = null;

		ByteArrayOutputStream out = null;
		ByteArrayInputStream in = null;

		try {
			out = new ByteArrayOutputStream();
			in = new ByteArrayInputStream(str.getBytes("ISO-8859-1"));
			GZIPInputStream gunzip = new GZIPInputStream(in);
			byte[] buffer = new byte[1024];
			int n;
			while ((n = gunzip.read(buffer)) >= 0) {
				out.write(buffer, 0, n);
			}

			result = out.toString();
		} catch (Exception e) {
			logger.error("", e);
			throw new AppException(1, "压缩格式有误");
		} finally {
			if (null != out) {
				try {
					out.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			if (null != in) {
				try {
					in.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		return result;
	}
	
	
	/**
	 * 解析和校验数据
	 * 
	 * @param agentTailInfoString
	 * @return
	 */
	protected List<AgentTailInfo> parseMessage(String agentTailInfoString,
			ResultEntity entity) throws AppException {
		if (null == agentTailInfoString)
			return null;
		List<AgentTailInfo> agentTailInfoList = new ArrayList<AgentTailInfo>();

		String[] agentTailInfoStrArr = agentTailInfoString.split(";");
		for (String agentTailInfoStr : agentTailInfoStrArr) {
			// 分解数据
			String[] cols = agentTailInfoStr.split(",");

			AgentTailInfo ati = new AgentTailInfo();
			// 组装数据
			String username = cols[0], imei = cols[1], lon = cols[2], lat = cols[3], alt = cols[4], speed = cols[5], direction = cols[6], accuary = cols[7], gpsdate = cols[8], pointCount = cols[9];

			// 组装数据
			ati.setUserName(username);
			ati.setImei(imei);
			ati.setX(lon);
			ati.setY(lat);
			ati.setAltitude(alt);
			ati.setSpeed(speed);
			ati.setDirection(direction);
			ati.setPointAccuracy(accuary);
			ati.setPointCount(pointCount);

			if (!validateAgentTailInfo(ati)) {
				throw new AppException(1, "参数不能为空");
			}

			if (!valieateXY(lon, lat)) {
				throw new AppException(1, "坐标位置有问题");
			}

			String[] offsetXY = getOffsetXY(lon, lat);
			if (offsetXY == null) {
				throw new AppException(1, "偏移量计算有问题");
			}

			try {
				Date gpsDate = sdf.parse(gpsdate);

				ati.setGpsTime(gpsDate);
			} catch (Exception e) {
				throw new AppException(1, "日期格式有误");
			}

			ati.setOffsetX(offsetXY[0]);
			ati.setOffsetY(offsetXY[1]);

			agentTailInfoList.add(ati);
		}

		return agentTailInfoList;
	}
	
	private boolean validateAgentTailInfo(AgentTailInfo ati) {
		if (isNullOrBlank(ati.getAltitude())) {
			return false;
		}
		if (isNullOrBlank(ati.getDirection())) {
			return false;
		}
		if (isNullOrBlank(ati.getImei())) {
			return false;
		}
		if (isNullOrBlank(ati.getPointAccuracy())) {
			return false;
		}
		if (isNullOrBlank(ati.getPointCount())) {
			return false;
		}
		if (isNullOrBlank(ati.getSpeed())) {
			return false;
		}
		if (isNullOrBlank(ati.getUserName())) {
			return false;
		}
		if (isNullOrBlank(ati.getX())) {
			return false;
		}
		if (isNullOrBlank(ati.getY())) {
			return false;
		}

		return true;
	}
	
	private final boolean isNullOrBlank(String str) {
		return null == str || "".equals(str.trim());
	}
	
	private boolean valieateXY(String x, String y) {
		double xValue = 0.0;
		double yValue = 0.0;

		try {
			xValue = Double.parseDouble(x);
			yValue = Double.parseDouble(y);
		} catch (Exception e) {
			logger.warn("x,y 格式不对! x=" + x + ";y=" + y);
			return false;
		}

		if (!(xValue > 0 && xValue <= 180)) {
			return false;
		}
		if (!(yValue > 0 && yValue <= 90)) {
			return false;
		}
		return true;
	}

}

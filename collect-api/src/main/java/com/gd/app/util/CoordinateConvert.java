package com.gd.app.util;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import autonavi.online.framework.property.PropertiesConfigUtil;

import com.mapabc.gds.offset.CoordOffset;
import com.mapabc.gds.offset.Lonlat;

public class CoordinateConvert {
	private static CoordOffset offset = null;
	private static Logger log =LogManager.getLogger(CoordinateConvert.class);

	public static void init() {
		CoordinateConvert.getCoordOffSet();
	}

	private static CoordOffset getCoordOffSet() {
		if (CoordinateConvert.offset == null) {
			//			CoordinateConvert.offset = CoordOffset.getInstance(CoordinateConvert.class.getResource("/packfile.dat").getPath());
//			String path = CoordinateConvert.class.getResource("/") + "/conf/staticResources.properties";
//			path = path.replace("file:", "");
//			CoordinateConvert.log.info("staticResources.properties=" + path);
			try {
				String packfilePath = (String)PropertiesConfigUtil.getPropertiesConfigInstance().getProperty(SysProps.PACKFILE_PATH);
				CoordinateConvert.log.info("packfile.dat=" + packfilePath);
				CoordinateConvert.offset = CoordOffset.getInstance(packfilePath);
			} catch (Exception e) {
				CoordinateConvert.log.error("获得packfile文件路径失败：" + e.toString());
			} finally {
			}
		}
		return CoordinateConvert.offset;
	}

	/**
	 * GPS坐标转换到偏转后坐标
	 * 
	 * @param xyStr坐标串,格式如：118.75190,32.048845;118.75290,32.046123
	 * @return
	 */
	public static String GPS2Deflexion(String xyStr) {
		// 如果项目初始话的时候没有开启加载坐标偏转，那么不做偏转或反偏转，以防止内存溢出
		// if(offset == null) {
		// return xyStr;
		// }

		StringBuffer res = new StringBuffer();
		String[] llArr = xyStr.split(";");

		int i = 0;
		for (String ll : llArr) {
			String[] inputArr = ll.split(",");
			Lonlat inputLL = new Lonlat();
			inputLL.setLon(Double.valueOf(inputArr[0].trim()));
			inputLL.setLat(Double.valueOf(inputArr[1].trim()));
			Lonlat outLL = new Lonlat();
			CoordinateConvert.getCoordOffSet().offsetCoord(inputLL, outLL);

			if (i == llArr.length - 1) {
				res.append(outLL.getLon() + "," + outLL.getLat());
			} else {
				res.append(outLL.getLon() + "," + outLL.getLat() + ";");
			}
			i++;
		}
		return res.toString();
	}

	/**
	 * 偏转后坐标转换回GPS坐标
	 * 
	 * @param xyStr偏转后坐标串,格式如：118.75190,32.048845;118.75290,32.046123
	 * @return
	 */
	public static String Deflexion2GPS(String xyStr) {
		// 如果项目初始话的时候没有开启加载坐标偏转，那么不做偏转或反偏转，以防止内存溢出
		// if(offset == null) {
		// return xyStr;
		// }

		StringBuffer res = new StringBuffer();
		String[] llArr = xyStr.split(";");

		int i = 0;
		for (String ll : llArr) {
			String[] inputArr = ll.split(",");
			Lonlat inputLL = new Lonlat();
			inputLL.setLon(Double.valueOf(inputArr[0].trim()));
			inputLL.setLat(Double.valueOf(inputArr[1].trim()));

			Lonlat tempLL = new Lonlat();
			CoordinateConvert.getCoordOffSet().offsetCoord(inputLL, tempLL);

			Lonlat outLL = new Lonlat();
			outLL.setLon((2 * inputLL.getLon() - tempLL.getLon()));
			outLL.setLat((2 * inputLL.getLat() - tempLL.getLat()));

			if (i == llArr.length - 1) {
				res.append(outLL.getLon() + "," + outLL.getLat());
			} else {
				res.append(outLL.getLon() + "," + outLL.getLat() + ";");
			}
			i++;
		}
		return res.toString();
	}

	public static String changeToDegree(String input) {
		String[] re = input.split(",");
		double one = Float.parseFloat(re[0]);
		double two = Float.parseFloat(re[1]);
		double three = Float.parseFloat(re[2]);

		return String.valueOf((one + two / 60 + three / 3600));
	}
}

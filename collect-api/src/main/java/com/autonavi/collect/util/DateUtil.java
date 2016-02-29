package com.autonavi.collect.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class DateUtil {
	private static Logger log =LogManager.getLogger(DateUtil.class);
	
    public static final String  SIMPLE_DATE_FORT = "yyyy-MM-dd HH:mm:ss";
	public static final String DEFAULT_DATE_PATTERN = "yyyy-MM-dd";
	public static final SimpleDateFormat SDF = new SimpleDateFormat(
			DEFAULT_DATE_PATTERN);

	public static boolean isBlank(String s) {
		return (s == null || s.trim().length() == 0);
	}

	/**
	 * 日期转成字符串
	 * 
	 * @param aDate
	 *            Date格式日期
	 * @param pattern
	 *            时间格式
	 * @return
	 */
	public static final String parseString(Date dt, String pattern) {
		String returnValue = "";
		if (dt != null) {
			SimpleDateFormat format = new SimpleDateFormat(pattern);
			returnValue = format.format(dt);
		}
		return returnValue;
	}
	
	public static Date parse(String date, String format) {
		if (isBlank(date))
			return null;

		SimpleDateFormat formatter = SDF;
		if (isNotBlank(format) && !DEFAULT_DATE_PATTERN.equals(format))
			formatter = new SimpleDateFormat(format);

		try {
			return formatter.parse(date);
		} catch (ParseException e) {
			log.error("Parse date " + date + " java.util.Date Error!", e);
			return null;
		}
	}
	
	public static boolean isNotBlank(String s) {
		return !isBlank(s);
	}
	public static String getYesterDayEndStr(){
		Calendar cal= Calendar.getInstance();
		cal.setTime(new Date());
		cal.add(Calendar.DATE, -1);
		Date yesterDay = cal.getTime();
		String end = getCustomFormatDate(yesterDay.getTime(), "yyyy-MM-dd") + " 23:59:59";	
		return end;	
	}
	public static String getCustomFormatDate(long time, String formatStyle){
		Date cup = new Date();
		cup.setTime(time);
		SimpleDateFormat sdf = new SimpleDateFormat(formatStyle);
		String strDate = sdf.format(cup);
		return strDate;
	}
	public static final Date convertStringToDate(String aMask, String strDate)
			throws ParseException {
		SimpleDateFormat df = null;
		Date date = null;
		df = new SimpleDateFormat(aMask);

		if (log.isDebugEnabled()) {
			log.debug("converting '" + strDate + "' to date with mask '"
					+ aMask + "'");
		}

		try {
			date = df.parse(strDate);
		} catch (ParseException pe) {
			// log.error("ParseException: " + pe);
			throw new ParseException(pe.getMessage(), pe.getErrorOffset());
		}

		return (date);
	}
	
	public static String getCurrentDateTime() {

		return parseString(new Date(), SIMPLE_DATE_FORT);
	}

	public static String getCurrentDate() {
		return parseString(new Date(), DEFAULT_DATE_PATTERN);
	}

	public static String rollDateByStr(String dateString,
			String dateFormat, int rollDay) {

		if (StringUtils.isBlank(dateFormat)) {
			dateFormat = DEFAULT_DATE_PATTERN;
		}

		SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);

		Calendar cd = Calendar.getInstance();
		try {
			Date date = sdf.parse(dateString);
			String todaydate = sdf.format(date);
			cd.setTime(sdf.parse(todaydate));
			cd.add(Calendar.DATE, rollDay);
			return sdf.format(cd.getTime());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static void main(String[] args) {

		// System.out.println(rollDateByStr("2014-01-06 10:23:11",
		// SIMPLE_DATE_FORT, -2));
		//
		// System.out.println(parseString(new Date(), SIMPLE_DATE_FORT));

		System.out.println(getCurrentDate());

		System.out.println(getCurrentDateTime());

	}

}

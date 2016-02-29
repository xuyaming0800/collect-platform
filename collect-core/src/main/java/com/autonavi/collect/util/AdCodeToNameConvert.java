package com.autonavi.collect.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.core.io.ClassPathResource;

public class AdCodeToNameConvert {
	private static AdCodeToNameConvert instance;
	private static Logger log = LogManager.getLogger(AdCodeToNameConvert.class);
	private Map<String, String> map;

	private AdCodeToNameConvert() {

	}

	private static final Object lock = new Object();

	public static AdCodeToNameConvert getInstance() throws Exception {
		synchronized (lock) {
			if (instance == null) {
				InputStream in=null;
				InputStreamReader reader=null;
				BufferedReader in2=null;
				try {
					instance = new AdCodeToNameConvert();
					instance.map = new HashMap<String, String>();
					ClassPathResource res = new ClassPathResource(
							"/data/County_out.csv", AdCodeToNameConvert.class);
					in = res.getInputStream();
					reader = new InputStreamReader(in, "GBK");
					in2 = new BufferedReader(reader);
					String y = null;
					int count = 0;
					while ((y = in2.readLine()) != null) {// 一行一行读
						if (count > 0) {
							String[] split=y.split(",");
							instance.map.put(split[2], split[0]);
						}
						count++;
					}
					log.info("共加载ADCODE信息["+count+"]条");
				} finally{
					if(in2!=null)in2.close();
					if(reader!=null)reader.close();
					if(in!=null)in.close();
				}
			}
		}
		return instance;
	}
	public String fromAdCodeToName(String adcode){
		return map.get(adcode);
	}

}

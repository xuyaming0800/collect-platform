package com.gd.app.util;

import java.util.Random;

import autonavi.online.framework.property.PropertiesConfigUtil;

import com.gd.app.security.identifyingCode.IdentifyingCode;
import com.gd.app.security.identifyingCode.IdentifyingCodeClassFactory;

public class SecureCodeUtil {
	private static Random r=new Random();
	public static IdentifyingCode getSecureCodeInstance()throws Exception{
		String path=PropertiesConfigUtil
				.getPropertiesConfigInstance()
				.getProperty(SysProps.PROP_SECURE_CODE_PATH).toString();
		String array=PropertiesConfigUtil
				.getPropertiesConfigInstance()
				.getProperty(SysProps.PROP_SECURE_CODE_ARRAY).toString();
		IdentifyingCode identifyingCode = new IdentifyingCode();
		IdentifyingCodeClassFactory factory = new IdentifyingCodeClassFactory(
				SecureCodeUtil.class.getResource(path)
						.toString());
		String[] className=array.split(",");
		int index=r.nextInt(className.length);
		identifyingCode.setIdentifyingCodeHandle(factory.getHandle(className[index]));
		return identifyingCode;
	}

}

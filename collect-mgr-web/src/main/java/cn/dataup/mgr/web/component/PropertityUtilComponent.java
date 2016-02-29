package cn.dataup.mgr.web.component;

import org.springframework.stereotype.Component;

import autonavi.online.framework.property.PropertiesConfig;
import autonavi.online.framework.property.PropertiesConfigUtil;

@Component
public class PropertityUtilComponent {
	private static PropertityUtilComponent object=null;
	private PropertiesConfig pc = null;
	private PropertityUtilComponent()throws Exception{
		if(object==null){
			if (pc == null)
				pc = PropertiesConfigUtil.getPropertiesConfigInstance();
			object=this;
		}
		
	}
	public PropertityUtilComponent getInstance(){
		return object;
	}
	public String getBizProperty(String key) {
		return (String) pc.getProperty(key);
	}

}

package cn.dataup.collect.util.DataUtil;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import autonavi.online.framework.util.json.JsonBinder;
import cn.dataup.collect.util.bean.DataImgEntiy;
import cn.dataup.collect.util.bean.DateEntity;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )throws Exception
    {
    	FileInputStream  in = new FileInputStream(new File("/Users/xuyaming/Downloads/123456"));
    	InputStreamReader reader=new InputStreamReader(in,"utf-8");
    	BufferedReader bf = new BufferedReader(reader);
    	String line="";
    	List<DateEntity> l=new ArrayList<DateEntity>();
    	Map<Long,DateEntity > m=new HashMap<Long,DateEntity>();
    	while ((line = bf.readLine()) != null) {
    		String[] s=line.split("', '");
    		DateEntity entity=null;
    		String id=s[0].replaceAll("'", "");
    		String name=s[6].replaceAll("'", "");
    		String x=s[1].replaceAll("'", "");
    		String y=s[2].replaceAll("'", "");
    		String type=s[3].replaceAll("'", "");
    		String path=s[4].replaceAll("'", "");
    		String time=s[5].replaceAll("'", "");
    		DataImgEntiy img=new DataImgEntiy();
    		img.setX(x);
    		img.setY(y);
    		String[] p=path.split("_");
    		img.setUrl("http://123.57.213.13/collect-img/img/"+p[0]+"/"+p[1]+"/"+p[2]+"/"+path);
    		if(m.containsKey(Long.valueOf(id))){
    			entity=m.get(Long.valueOf(id));
    		}else{
    			entity=new DateEntity();
    			m.put(Long.valueOf(id), entity);
    			entity.setId(id);
    			entity.setName(name);
    			entity.setTypeName(type);
    			entity.setSubmitTime(time);
    			l.add(entity);
    		}
    		entity.getImgs().add(img);
    	}
    	bf.close();
    	reader.close();
    	in.close();
    	JsonBinder binder=JsonBinder.buildNormalBinder(false);
    	
    	System.out.print(binder.toJson(l));
    }
}

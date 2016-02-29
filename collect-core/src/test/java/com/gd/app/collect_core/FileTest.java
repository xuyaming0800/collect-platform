package com.gd.app.collect_core;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FileUtils;

import autonavi.online.framework.util.json.JsonBinder;
public class FileTest {
	public static void main(String[] args)throws Exception {
		// TODO Auto-generated method stub
		File file = new File("/Java/temp/641747891567722496/ImageInfo.txt");
		JsonBinder binder=JsonBinder.buildNormalBinder(false);
		String json=FileUtils.readFileToString(file);
		//System.out.println(json);
		List<Map<String,String>> result=binder.fromJson(json, List.class, binder.getCollectionType(List.class, Map.class));
		Set<String> _set=new HashSet<String>();
		Set<String> _setU=new HashSet<String>();
		Set<String> _setUi=new HashSet<String>();
		Map<String,String> _map=new HashMap<String,String>();
		for(Map<String,String> _m:result){
			String fileName=_m.get("imageName");
			String unique=_m.get("imgH5Id")+"_"+_m.get("batchId");
			String unique_index=_m.get("index")+"_"+_m.get("batchId");
			String batchId=_m.get("batchId");
			String level=_m.get("level");
			if(!_set.add(fileName)){
				System.out.println("fileName:"+_m);
			}
			if(!_setU.add(unique)){
				System.out.println("unique:"+_m);
			}
			if(!_setUi.add(unique_index)){
				System.out.println("unique_index:"+_m);
			}
			if(_map.containsKey(batchId)){
				if(!level.equals(_map.get(batchId))){
					System.out.println("batchO:"+batchId);
					System.out.println("batchO:"+_map.get(batchId));
					System.out.println("batchN:"+level);
				}
				
			}else{
				_map.put(batchId, level);
			}
			File _f=new File("/Java/temp/641747891567722496/"+fileName);
			if(!_f.exists()){
				System.out.println("fileNotExist:"+_m);
			}		
			
		}
		file=new File("/Java/temp/641747891567722496");
		for(File f:file.listFiles()){
			if(!_set.contains(f.getName())){
				System.out.println("extraFile:"+f.getName());
				
			}
		}
		
	}

}

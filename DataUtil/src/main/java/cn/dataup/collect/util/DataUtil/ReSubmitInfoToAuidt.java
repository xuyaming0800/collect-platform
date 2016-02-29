package cn.dataup.collect.util.DataUtil;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.autonavi.audit.entity.ResultEntity;
import com.autonavi.audit.util.JsonBinder;

import net.sf.json.JSONObject;
import cn.dataup.collect.util.SendCollectInfoJsonToAuditQueue;
import cn.dataup.collect.util.bean.QueueInfoEntity;

public class ReSubmitInfoToAuidt {

	public static void main(String[] args)throws Exception {
		InputStream in=null;
		InputStreamReader reader=null;
		BufferedReader bf=null;
		QueueInfoEntity entity=new QueueInfoEntity();
		entity.setHost("182.92.184.70");
		entity.setPort(5672);
		entity.setQueueName("collect_out");
		File dir=new File("/Java/temp/1");
		if(!dir.isDirectory()){
			System.exit(0);
		}
		File[] files=dir.listFiles();
		if(files==null||files.length==0){
			System.exit(0);
		}
		for(File f:files){
			try {
				System.out.println(f.getName());
				in = new FileInputStream(f);
				reader=new InputStreamReader(in,"utf-8");
				bf = new BufferedReader(reader);
				String line=null;
				while ((line = bf.readLine()) != null) {
				   if(line.indexOf("com.autonavi.audit.entity.CollectAudit")!=-1){
						String regex = "\\{.*\\}";
						Pattern pattren = Pattern.compile(regex);
						Matcher mat = pattren.matcher(line);
						if (mat.find()) {
							String message=mat.group();
							System.out.println(message);
							JsonBinder binder=JsonBinder.buildNormalBinder();
							ResultEntity en=binder.fromJson(message, ResultEntity.class);
							SendCollectInfoJsonToAuditQueue.execute(entity, en);
							
						}
				   }
				}
			} finally {
				if(bf!=null){
					bf.close();
				}
				if(reader!=null){
					bf.close();
				}
				if(in!=null){
					in.close();
				}
			}
			
		}
		

	}

}

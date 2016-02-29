package com.gd.app.collect_core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import autonavi.online.framework.sharding.uniqueid.support.IdWorkerFromSnowflake;


public class Test7 {

	public static void main(String[] args)throws Exception {
//		File file = new File("/Users/xuyaming/Downloads/11111.txt");
//		List<String> contents = FileUtils.readLines(file);
//		for(String s:contents){
//			String[] sss=s.split(",");
//			System.out.println("update cc_html5_page_info set collect_class_id="+sss[3]+" where id="+sss[0]+";");
//		}
//		File dir=new File("/Users/xuyaming/Downloads/646564898498674688/1");
//		File[] files=dir.listFiles();
//		for(File f:files){
//			System.out.println(f.getName()+":"+FileUtil.validateImage(f.getAbsolutePath()));
//		}
//		HttpPost httpPost = new HttpPost("http://localhost:8080/cc-web/openapi?serviceid=666005");  
//		HttpGet httpGet = new HttpGet("http://localhost:8080/cc-web/openapi?serviceid=666005&collectClassId=中文");  
//        HttpClient client = new DefaultHttpClient(); 
//        Map<String, String> params=new HashMap<String,String>();
//        params.put("ownerId", "3");
//        params.put("collectClassId", "中文");
//        List<NameValuePair> valuePairs = new ArrayList<NameValuePair>(params.size());  
//        for(Map.Entry<String, String> entry : params.entrySet()){  
//            NameValuePair nameValuePair = new BasicNameValuePair(entry.getKey(), String.valueOf(entry.getValue()));  
//            valuePairs.add(nameValuePair);  
//        }  
//        UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(valuePairs, "UTF-8");  
//        httpPost.setEntity(formEntity);
//        HttpResponse resp = client.execute(httpPost);  
//        
//        HttpEntity entity = resp.getEntity();  
//        String respContent = EntityUtils.toString(entity , "UTF-8").trim();  
//        httpPost.abort();  
//        
//        client.execute(httpGet);
//        client.getConnectionManager().shutdown();  
//        System.out.println(respContent);
		
		IdWorkerFromSnowflake ids=new IdWorkerFromSnowflake();
		int workId=31;
		int dbid=31;
		System.out.println(ids.nextId(31,31));
		System.out.println(ids.nextId(20,20));
		System.out.println(ids.nextId(0,0));
	

	}
	
	
	public class TestGosn{
		private TestCode status;
		private Object result;
		public TestCode getStatus() {
			return status;
		}
		public void setStatus(TestCode status) {
			this.status = status;
		}
		public Object getResult() {
			return result;
		}
		public void setResult(Object result) {
			this.result = result;
		}
		
		
	}
	public static class TestResult{
		private String baseId;
		private String basePackageId;
		private String collectDataName;
		private String endTime;
		private String submitTime;
		private List<Map<String,String>> extras;
		public String getBaseId() {
			return baseId;
		}
		public void setBaseId(String baseId) {
			this.baseId = baseId;
		}
		public String getBasePackageId() {
			return basePackageId;
		}
		public void setBasePackageId(String basePackageId) {
			this.basePackageId = basePackageId;
		}
		public String getCollectDataName() {
			return collectDataName;
		}
		public void setCollectDataName(String collectDataName) {
			this.collectDataName = collectDataName;
		}
		public String getEndTime() {
			return endTime;
		}
		public void setEndTime(String endTime) {
			this.endTime = endTime;
		}
		public String getSubmitTime() {
			return submitTime;
		}
		public void setSubmitTime(String submitTime) {
			this.submitTime = submitTime;
		}
		public List<Map<String,String>> getExtras() {
			return extras;
		}
		public void setExtras(List<Map<String,String>> extras) {
			this.extras = extras;
		}
		
		
		
	}
	public class TestCode{
		private String code;
		private String msg;
		public String getCode() {
			return code;
		}
		public void setCode(String code) {
			this.code = code;
		}
		public String getMsg() {
			return msg;
		}
		public void setMsg(String msg) {
			this.msg = msg;
		}
		
	}
	
	public class Person{
		private String name;
		private Object extra;
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public Object getExtra() {
			return extra;
		}
		public void setExtra(Object extra) {
			this.extra = extra;
		}
		
		
	}

}

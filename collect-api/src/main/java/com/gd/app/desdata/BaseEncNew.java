package com.gd.app.desdata;

import sun.misc.BASE64Decoder;



public class BaseEncNew {

	public BaseEncNew() {
		// TODO Auto-generated constructor stub
	}
	
	static  String base64_chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/";
	
	public static String disp_encode(String org)
	{
		return 	BASE64Encoder.encode(CryptorNew.RC4Crypt(org.getBytes()));
	}

	public static String disp_decode(String org,String set )
	{
		
		String s="";
		try {
			byte[] b=new BASE64Decoder().decodeBuffer(org);
			s=new String(CryptorNew.RC4Crypt(b),set);
		} catch (Exception e) {
			// TODO Auto-generated catch block
		}
		return 	s;
	}
	public static void main(String arg[]){
		  CryptorNew.RC4Init("4cea79d4-3406-463f-9593-09cc9264cb82");
		//String json = "{\"scoreId\":\"1\",\"coordtype\":\"0\",\"isLocate\":\"1\",\"positionX\":\"1.0\",\"positionY\":\"2.0\",\"positionZ\":\"3.0\",\"deviceInfo\":\"93f0cb0614\",\"baseId\":\"21403381\",\"taskid\":\"-11\",\"tasktype\":\"0\",\"dataname\":\"大幅度大声道\" ,\"pointType\":\"0\",\"pointLevel\":\"6\",\"code\":\"11\",\"userName\":\"中文\",\"photoTime\":\"2014-05-08 00:00:00\",\"gpsTime\":\"2014-05-08 00:00:00\",\"terminaFlag\":\"\",\"addressType\":\"-1\",\"adCode\":\"110108\",\"pointAccury\":\"4.0\",\"position\":\"4.0\",\"md5Validate\":\"3F8B0D4FE19BAF01CD531931247398E0\",\"recommend\":\"0\",\"tokenId\":\"2ee52767-2939-48fe-aadc-2a9c6f06fa4d\",\"charset\":\"utf-8\",\"isBatch\":\"1\"}";
		  String json="{\"user\":\"ceshi1\",\"md5\":\"111\",\"x\":\"0.1\",\"y\":\"0.2\",\"charset\":\"utf-8\"}";
		  //String json="{\"scoreId\":\"\",\"coordtype\":\"0\",\"isLocate\":\"1\",\"positionX\":\"1.0\",\"positionY\":\"2.0\",\"positionZ\":\"3.0\",\"deviceInfo\":\"93f0cb0614\",\"baseId\":\"\",\"taskid\":\"\",\"tasktype\":\"0\",\"dataname\":\"换个方式稍等\" ,\"pointType\":\"0\",\"pointLevel\":\"6\",\"code\":\"11\",\"userName\":\"xym1\",\"photoTime\":\"2014-05-08 00:00:00\",\"gpsTime\":\"2014-05-08 00:00:00\",\"terminaFlag\":\"\",\"addressType\":\"-1\",\"adCode\":\"110108\",\"pointAccury\":\"4.0\",\"position\":\"4.0\",\"md5Validate\":\"3F8B0D4FE19BAF01CD531931247398E0\",\"recommend\":\"0\",\"tokenId\":\"e01dc9c5-0c01-4693-b0d0-167f16ec3435\",\"charset\":\"utf-8\",\"isBatch\":\"0\",\"pack\":\"497b4e2107b5439aa4ba263df191c1c3\"}";
		  //JSONObject json1 = JSONObject.fromObject(json);
		  
		  json=disp_encode(json);
		  //json="nrh2W7EfsXEqu88LbbujnpPq1eM+lnqd5hlqCVJB7p9u2YKysVI1yggo++XwDDtYi9OWu89z2p5KUhWHkHStGG84/ekbuzOYw44zN9/ZDWaeVF/SeFiWnm5XT5JTYCli0Y3RrKafjNlql/t+W7dw2HEuajBSFYjwhdKaSbjQhQIsG1SmABq/EZs07/UjWNS+utDpbq3rKGJ6W9aUIAGuOtZD7hRozKsQINj6HJXXukVGzxrpmgorwuWGtR/v9pL6EapbZPaYn7q2hNvAyPJPMSwbTWApQjPd";
		  System.out.println(json);
		  System.out.println(disp_decode(json,"utf-8"));
	}

}

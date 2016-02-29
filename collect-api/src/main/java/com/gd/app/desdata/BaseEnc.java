package com.gd.app.desdata;

import sun.misc.BASE64Decoder;




public class BaseEnc {

	public BaseEnc() {
		// TODO Auto-generated constructor stub
	}
	
	
	public static String disp_encode(String org)
	{
		return 	BASE64Encoder.encode(Cryptor.RC4Crypt(org.getBytes()));
	}

	public static String disp_decode(String org,String set )
	{
		
		String s="";
		try {
			byte[] b=new BASE64Decoder().decodeBuffer(org);
			s=new String(Cryptor.RC4Crypt(b),set);
		} catch (Exception e) {
			// TODO Auto-generated catch block
		}
		return 	s;
	}

}

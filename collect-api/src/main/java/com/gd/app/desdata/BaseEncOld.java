package com.gd.app.desdata;

import java.io.IOException;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;




public class BaseEncOld {

	public BaseEncOld() {
		// TODO Auto-generated constructor stub
	}
	
	static  String base64_chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/";
	
	public static String disp_encode(String org)
	{
		org=new BASE64Encoder().encode(org.getBytes());
		int reverseNum =  2 +  (int)(7.0 *  Math.random()); 
		
		StringBuffer ret = new StringBuffer();
		ret.append("1");	//标识为第一种加密模式
		int length = org.length();
		int looptime = length / reverseNum;
		for(int i = 0 ; i < looptime ; i ++ )
		{
			for( int j = 0 ; j < reverseNum ; j ++)
			{
				ret.append(  org.charAt(i*reverseNum + reverseNum - j -1) );
			}
		}

		for ( int i = 0 ; i < length % reverseNum ; i ++ )
		{
			ret.append( org.charAt(org.length() - i - 1));
		}

		ret.append(base64_chars.charAt(reverseNum));

		return ret.toString();	
	}

	public static String disp_decode(String org,String set )
	{
		org=org.replaceAll(" ", "+");
		if (org.length() < 3)
			return "";
		if(!org.substring(0,1).equals("1"))
			return "";
		org = org.substring(1); //截取前一个字符；
		
		int reverseNum = base64_chars.indexOf(org.substring(org.length()-1));
		if( reverseNum == -1)
			return "";
		
		StringBuffer ret = new StringBuffer();
		int length = org.length() -1 ;
		int looptime = length / reverseNum;
		for(int i = 0 ; i < looptime ; i ++ )
		{
			for( int j = 0 ; j < reverseNum ; j ++)
			{
				ret.append(org.charAt(i*reverseNum + reverseNum - j - 1));
			}
		}

		for ( int i = 0 ; i < length % reverseNum ; i ++ )
		{
			ret.append(org.charAt(org.length() - i - 2));
		}
		String result=null;
		try {
			byte[] b=new BASE64Decoder().decodeBuffer(ret.toString());
			result = new String(b,set);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return result;	
	}
	public static String disp_decode(String org )
	{
		org=org.replaceAll(" ", "+");
		if (org.length() < 3)
			return "";
		if(!org.substring(0,1).equals("1"))
			return "";
		org = org.substring(1); //截取前一个字符；
		
		int reverseNum = base64_chars.indexOf(org.substring(org.length()-1));
		if( reverseNum == -1)
			return "";
		
		StringBuffer ret = new StringBuffer();
		int length = org.length() -1 ;
		int looptime = length / reverseNum;
		for(int i = 0 ; i < looptime ; i ++ )
		{
			for( int j = 0 ; j < reverseNum ; j ++)
			{
				ret.append(org.charAt(i*reverseNum + reverseNum - j - 1));
			}
		}

		for ( int i = 0 ; i < length % reverseNum ; i ++ )
		{
			ret.append(org.charAt(org.length() - i - 2));
		}
		String result=null;
		try {
			byte[] b=new BASE64Decoder().decodeBuffer(ret.toString());
			result = new String(b);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return result;	
	}
	
	

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		BaseEncOld test = new BaseEncOld();
		try{
			String org = "中行个";
			//String baseout = new BASE64Encoder().encode(org.getBytes());
			String dispencode = test.disp_encode(org);
			System.out.println(dispencode);
			

			//System.out.println(dispencode);
			String dispdecode = test.disp_decode("1rNXY0JyeiIiOiQWShRXYkJCL6ISZtFmTkW+mbWuIpeuhae+pcWug4WuuxWujoeOszVnIsICgl1WYOJXZtlHeiojIhh2YiwiI6ICdlNnc40iZ0VnIDRWYiwiIiojIlR2b4ATMwETMSNXaiwiIl1WbvNWZwIiOiQmbr9GdiwiI6ICZJ5WZ3EWZjRjI0MTL0QWOzYDNtYDMzkTN50iZ5M2Y5ATL4I2Y0YjMlRmIsIiMulUZjlmdhJiOi8mZ9JCZlNmYI","utf-8");
			//byte [] orgcheck = new BASE64Decoder().decodeBuffer(dispdecode);
			System.out.println(new String(dispdecode));
			int n = 0;
		}catch(Exception ee){
			ee.printStackTrace();
		}
	}

}

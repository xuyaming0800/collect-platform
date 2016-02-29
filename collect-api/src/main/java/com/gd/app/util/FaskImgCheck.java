package com.gd.app.util;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;



public class FaskImgCheck {
	public static final Logger log =LogManager.getLogger(FaskImgCheck.class);
	public static final String CHECK_VALUE = "456789:CDEFGHIJSTUVWXYZcdefghijstuvwxyz";
	public static final String fileSuffix = "jpg";
	public static boolean parseImage(final String imagePath){
		boolean isOk = false;
		
		BufferedInputStream bufferStream = null;
		try {
			bufferStream = new BufferedInputStream(new FileInputStream(imagePath));
			int buf_size = 1024; 
			byte[] buffer = new byte[buf_size];  
	        while(-1 != bufferStream.read(buffer,0,buf_size)){  
	        	String str = new String(buffer,"GBK");
            	if(str.indexOf(CHECK_VALUE)>-1){
            		isOk = true;
            		return true;
            	}
	        }  
	        if(!isOk){
        	  return false;
	       } 
		 }catch (Exception e) {
			  return false;
		 }finally{
			 try {
				bufferStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		 }
		return false;
	}

}

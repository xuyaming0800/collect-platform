package cn.dataup.collect.util.DataUtil;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.geo.util.CoordUtils;

import cn.dataup.collect.util.bean.DataImgEntiy;

public class App1 {
	public static void main(String[] args) throws Exception {
		FileInputStream  in = new FileInputStream(new File("/Users/xuyaming/Downloads/all_point.txt"));
    	InputStreamReader reader=new InputStreamReader(in,"utf-8");
    	BufferedReader bf = new BufferedReader(reader);
    	File file_out = new File("/Users/xuyaming/Downloads/all_point_out.txt");
    	String line="";
    	List<DataImgEntiy> l=new ArrayList<DataImgEntiy>();
    	while ((line = bf.readLine()) != null) {
    		String[] s=line.split(",");
    		double[] xy=new double[2];
    		CoordUtils.bd2gps(Double.valueOf(s[1]), Double.valueOf(s[0]), xy);
    		DataImgEntiy img=new DataImgEntiy();
    		img.setX(String.valueOf(xy[0]));
    		img.setY(String.valueOf(xy[1]));
    		l.add(img);
    		FileUtils.write(file_out,xy[0]+","+xy[1],"utf-8", true);
            FileUtils.write(file_out,"\n","utf-8",true);
    	}
    	bf.close();
    	reader.close();
    	in.close();
    	
    	System.out.println();

	}
}

package com.gd.app.collect_core;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.geo.util.CoordUtils;

import autonavi.online.framework.sharding.uniqueid.support.IdWorkerFromSnowflake;

import com.autonavi.collect.constant.CommonConstant;
import com.autonavi.collect.entity.CoordinateTransferEntity;

public class Test3 {

	public static void main(String[] args) {
		IdWorkerFromSnowflake ids=new IdWorkerFromSnowflake();
		int workId=31;
		int dbid=31;
//		long a=1432188377;
//		long b=a+100;
//		long c=a+200;
//		long d=a+300;
//		long e=a+400;
//		long f=a+500;
//		long g=a+600;
		//创建文件对象
        File file = new File("/Users/xuyaming/Downloads/autonavi_poi_sh.txt");
        File file_out = new File("/Users/xuyaming/Downloads/out_shanghai.sql");
        try {
            //使用readLines读取每一行，生成List
            List<String> contents = FileUtils.readLines(file);
            for(int x=1;x<=1;x++){
            	//遍历输出contents
                int count=1;
                for (String line : contents) {
                    String[] s=line.split(",");
//                    s[1]=s[1]+"-测试"+x;
                    int i=0;
                    if(s.length!=6){
                    	System.out.println(line);
                    }
        			com.mapabc.spatial.RegionSearch rs = com.mapabc.spatial.RegionSearch.getInstance();
    				String adcode = rs.getPointADCode(new Double(s[3]), new Double(s[4]));
    				CoordinateTransferEntity entity=new CoordinateTransferEntity();
         			entity.setFromGpsSys(CommonConstant.GPS_SYSTEM.GCJ.getCode());
         			entity.setToGpsSys(CommonConstant.GPS_SYSTEM.BAIDU.getCode());
         			entity.setX(new Double(s[3]));
         			entity.setY(new Double(s[4]));
         			double[] xy=new double[2];
         			CoordUtils.gcj2bd(entity.getX(), entity.getY(), xy);
         			s[3]=String.valueOf(xy[0]);
         			s[4]=String.valueOf(xy[1]);
//         			FileUtils.write(file_out,"update  collect_original_coordinate set ORIGINAL_X="+s[3].trim()+",ORIGINAL_Y="+s[4].trim()+" where id="+c+";","utf-8", true);
//                    FileUtils.write(file_out,"\n","utf-8",true);
//                    FileUtils.write(file_out,"update  collect_base_original_coordinate set ORIGINAL_X="+s[3].trim()+",ORIGINAL_Y="+s[4].trim()+" where id="+f+";","utf-8", true);
//                    FileUtils.write(file_out,"\n","utf-8",true);
         			long a=ids.nextId(workId, dbid);
                    FileUtils.write(file_out,"insert into collect_passive_package values("+a+",'"+s[1]+"','"+s[5]+"',1,10,0,"+i+",unix_timestamp(now()),unix_timestamp(now()),null,null,1435420542,'某广告"+x+"',72,72,24,0,null);","utf-8", true);
                    FileUtils.write(file_out,"\n","utf-8",true);
                    FileUtils.write(file_out,"insert into collect_passive_task values("+ids.nextId(workId, dbid)+",'测试广告任务"+x+"',0,unix_timestamp(now()),unix_timestamp(now()),null,null,'',null,null,null,1435420542,"+a+",null,0);","utf-8", true);
                    FileUtils.write(file_out,"\n","utf-8",true);
                    FileUtils.write(file_out,"insert into collect_original_coordinate values("+ids.nextId(workId, dbid)+",null,'"+s[3].trim()+"','"+s[4].trim()+"',"+adcode+",'2015_01_01_157_test_1.jpg',0,"+a+");","utf-8", true);
                    FileUtils.write(file_out,"\n","utf-8",true);
//                    FileUtils.write(file_out,"insert into collect_base_package values("+d+",'"+a+"','"+s[1]+"','"+s[5]+"',1,10,6,"+i+",unix_timestamp(now()),unix_timestamp(now()),null,null,1435420542,'某广告"+x+"',null,72,72,24);","utf-8", true);
//                    FileUtils.write(file_out,"\n","utf-8",true);
//                    FileUtils.write(file_out,"insert into collect_task_base(id,passive_id,task_package_id,data_name,task_status,task_type,create_time,update_time,allot_end_time,release_freeze_time) values("+e+","+b+","+d+",'测试广告任务"+x+"',6,0,unix_timestamp(now()),unix_timestamp(now()),1435420542,0);","utf-8", true);
//                    FileUtils.write(file_out,"\n","utf-8",true);
//                    FileUtils.write(file_out,"insert into collect_base_original_coordinate values("+f+",null,'"+s[3].trim()+"','"+s[4].trim()+"',"+adcode+",'2015_01_01_157_test_1.jpg',0,"+a+");","utf-8", true);
//                    FileUtils.write(file_out,"\n","utf-8",true);
//                    FileUtils.write(file_out,"insert into collect_allot_base_user values("+g+",null,575946127350693888,"+d+",0);","utf-8", true);
//                    FileUtils.write(file_out,"\n","utf-8",true);
//                    FileUtils.write(file_out,"insert into collect_allot_base_user values("+(++g)+",null,1,"+d+",0);","utf-8", true);
//                    FileUtils.write(file_out,"\n","utf-8",true);
//                    FileUtils.write(file_out,"insert into collect_allot_base_user values("+(++g)+",null,596155958686646272,"+d+",0);","utf-8", true);
//                    FileUtils.write(file_out,"\n","utf-8",true);
//                    FileUtils.write(file_out,"insert into collect_allot_base_user values("+(++g)+",null,598267761390518272,"+d+",0);","utf-8", true);
//                    FileUtils.write(file_out,"\n","utf-8",true);
//                    a++;
//                    b++;
//                    c++;
//                    d++;
//                    e++;
//                    f++;
//                    g++;
                    count++;
                }
            }
            FileUtils.write(file_out, "", "utf-8", true);
        } catch (IOException e1) {
            e1.printStackTrace();
        }

	}

}

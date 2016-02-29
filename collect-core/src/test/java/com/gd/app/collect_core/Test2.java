package com.gd.app.collect_core;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;

public class Test2 {

	public static void main(String[] args) {
		long a=1431327972;
		long b=a+100;
		long c=a+200;
		long d=a+300;
		long e=a+400;
		long f=a+500;
		long g=a+600;
		//创建文件对象
        File file = new File("/Users/xuyaming/Downloads/baidu_poi_ouput.txt");
        File file_out = new File("/Users/xuyaming/Downloads/out.sql");
        try {
            //使用readLines读取每一行，生成List
            List<String> contents = FileUtils.readLines(file);
            for(int x=1;x<=30;x++){
            	//遍历输出contents
                int count=1;
                for (String line : contents) {
                    String[] s=line.split(",");
                    s[1]=s[1]+"-测试"+x;
                    int i=0;
                    int adcode=110112;
                    if(s[2].equals("公交站")){
                    	i=1;
                    }else if(s[2].equals("住宅")){
                    	i=2;
                    }else{
                    	i=0;
                    }
                    if(count>12){
                    	adcode=110106;
                    }
                    if(count>15){
                    	adcode=110108;
                    }
                    if(count==17){
                    	adcode=110106;
                    }
                    FileUtils.write(file_out,"insert into collect_passive_package values("+a+",'"+s[1]+"','"+s[3]+s[5]+"',1,10,0,"+i+",unix_timestamp(now()),unix_timestamp(now()),null,null,1435420542,'某广告"+x+"',72,72,24,0);","utf-8", true);
                    FileUtils.write(file_out,"\n","utf-8",true);
                    FileUtils.write(file_out,"insert into collect_passive_task values("+b+",'测试广告任务"+x+"',0,unix_timestamp(now()),unix_timestamp(now()),null,null,'',null,null,null,1435420542,"+a+",null,0);","utf-8", true);
                    FileUtils.write(file_out,"\n","utf-8",true);
                    FileUtils.write(file_out,"insert into collect_original_coordinate values("+c+",null,'"+s[6].trim()+"','"+s[7].trim()+"',"+adcode+",'2015_01_01_157_test_1.jpg',0,"+a+");","utf-8", true);
                    FileUtils.write(file_out,"\n","utf-8",true);
                    FileUtils.write(file_out,"insert into collect_base_package values("+d+",'"+a+"','"+s[1]+"','"+s[3]+s[5]+"',1,10,0,"+i+",unix_timestamp(now()),unix_timestamp(now()),null,null,1435420542,'某广告"+x+"',null,72,72,24);","utf-8", true);
                    FileUtils.write(file_out,"\n","utf-8",true);
                    FileUtils.write(file_out,"insert into collect_task_base(id,passive_id,task_package_id,data_name,task_status,task_type,create_time,update_time,allot_end_time,release_freeze_time) values("+e+","+b+","+d+",'测试广告任务"+x+"',0,0,unix_timestamp(now()),unix_timestamp(now()),1435420542,0);","utf-8", true);
                    FileUtils.write(file_out,"\n","utf-8",true);
                    FileUtils.write(file_out,"insert into collect_base_original_coordinate values("+f+",null,'"+s[6].trim()+"','"+s[7].trim()+"',"+adcode+",'2015_01_01_157_test_1.jpg',0,"+a+");","utf-8", true);
                    FileUtils.write(file_out,"\n","utf-8",true);
                    FileUtils.write(file_out,"insert into collect_allot_base_user values("+g+",null,575946127350693888,"+d+",0);","utf-8", true);
                    FileUtils.write(file_out,"\n","utf-8",true);
                    FileUtils.write(file_out,"insert into collect_allot_base_user values("+(++g)+",null,1,"+d+",0);","utf-8", true);
                    FileUtils.write(file_out,"\n","utf-8",true);
                    FileUtils.write(file_out,"insert into collect_allot_base_user values("+(++g)+",null,596155958686646272,"+d+",0);","utf-8", true);
                    FileUtils.write(file_out,"\n","utf-8",true);
                    FileUtils.write(file_out,"insert into collect_allot_base_user values("+(++g)+",null,598267761390518272,"+d+",0);","utf-8", true);
                    FileUtils.write(file_out,"\n","utf-8",true);
                    a++;
                    b++;
                    c++;
                    d++;
                    e++;
                    f++;
                    g++;
                    count++;
                }
            }
            FileUtils.write(file_out, "", "utf-8", true);
        } catch (IOException e1) {
            e1.printStackTrace();
        }

	}

}

<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <base href="<%=basePath%>">
    
    <title>My JSP 'Test.jsp' starting page</title>
    
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
	<!--
	<link rel="stylesheet" type="text/css" href="styles.css">
	-->

  </head>
  
  <body>
  <form action="saveToken" method="post">
  <textarea rows="4" cols="100" name="content">
  </textarea>
  <br>
  <input type="submit" value="获取Token">
  </form>
  
  <form action="saveTask" method="post">
  <textarea rows="4" cols="100" name="content">
  </textarea>
  <br>
  <input type="submit" value="保存任务">
  </form>
  
  <form action="uploadUserData" method="post" enctype="multipart/form-data" >
  <textarea rows="4" cols="100" name="content">
  </textarea>
  <br>
  <input type="file" name="image">
  <br>
  <input type="submit" value="提交任务">
  </form>
  
  
  <form action="queryTaskPackage" method="post">
  <textarea rows="4" cols="100" name="content">
  </textarea>
  <br>
  <input type="submit" value="查找我的任务包">
  </form>
  
   <form action="queryPackageTasks" method="post">
  <textarea rows="4" cols="100" name="content">
  </textarea>
  <br>
  <input type="submit" value="查找我的任务包下的步骤">
  </form>
  
  <form action="queryTaskDetails" method="post">
  <textarea rows="4" cols="100" name="content">
  </textarea>
  <br>
  <input type="submit" value="查找我的任务的图片">
  </form>
  
  
  <form action="queryTaskCountByDistrict" method="post">
  <textarea rows="4" cols="100" name="content">
  </textarea>
  <br>
  <input type="submit" value="查找区域下的可领取任务数量">
  </form>
  
  <form action="queryTaskByDistrict" method="post">
  <textarea rows="4" cols="100" name="content">
  </textarea>
  <br>
  <input type="submit" value="查找区域下的可领取任务">
  </form>
  
  <form action="queryTaskByRd" method="post">
  <textarea rows="4" cols="100" name="content">
  </textarea>
  <br>
  <input type="submit" value="查找附近可领取任务">
  </form>
  
  <form action="receiveTask" method="post">
  <textarea rows="4" cols="100" name="content">
  </textarea>
  <br>
  <input type="submit" value="领取任务">
  </form>
  
  <form action="deletePackage" method="post">
  <textarea rows="4" cols="100" name="content">
  </textarea>
  <br>
  <input type="submit" value="删除任务包">
  </form>
  
  
<!--   <form action="getSecureCode" method="post"> -->
<!--   <textarea rows="4" cols="100" name="content"> -->
<!--   </textarea> -->
<!--   <br> -->
<!--   <input type="submit" value="getSecureCode"> -->
<!--   </form> -->
<!--   <form action="agentComplaint" method="post"> -->
<!--   <input type="hidden" name="phone" value="13810162343"/> -->
<!--   <input type="hidden" name="content" value="中文"/> -->
<!--   <input type="hidden" name="userName" value="ceshi1"/> -->
<!--   <input type="hidden" name="deviceSys" value="android"/> -->
<!--   <input type="hidden" name="deviceModel" value="sdsada"/> -->
  
<!--   <input type="submit" value="agentComplaint"> -->
<!--   </form> -->
  </body>
</html>

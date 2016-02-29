<!DOCTYPE html>
<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>
<html lang="en">
<head>
<meta charset="utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta name="viewport" content="width=device-width, initial-scale=1">
<meta name="description" content="">
<meta name="author" content="">
<link rel="icon" href="favicon.ico">

<title>Dashboard Template for Bootstrap</title>

<!-- Bootstrap core CSS -->
<link href="<%=path%>/resources/css/bootstrap.min.css" rel="stylesheet">

<!-- Custom styles for this template -->
<link href="<%=path%>/resources/css/dashboard.css" rel="stylesheet">
<link href="<%=path%>/resources/css/aof.css" rel="stylesheet">

<!-- Just for debugging purposes. Don't actually copy these 2 lines! -->
<!--[if lt IE 9]><script src="../../assets/<%=path%>/resources/js/ie8-responsive-file-warning.js"></script><![endif]-->
<script src="<%=path%>/resources/js/ie-emulation-modes-warning.js"></script>

<!-- HTML5 shim and Respond.js IE8 support of HTML5 elements and media queries -->
<!--[if lt IE 9]>
      <script src="https://oss.maxcdn.com/html5shiv/3.7.2/html5shiv.min.js"></script>
      <script src="https://oss.maxcdn.com/respond/1.4.2/respond.min.js"></script>
    <![endif]-->
<script src="<%=path%>/resources/js/jquery-1.11.1.min.js"></script>
<script type="text/javascript">
function initJsonFileTable(msg){
	for(var i=0;i<msg.result.base.length;i++){
		var html="<tr><td>"+msg.result.base[i].baseName+"</td><td><a target='_blank' href='<%=path%>"+msg.result.base[i].basePath+"'>"+msg.result.base[i].baseName+"</a></td><td>"+msg.result.base[i].lastModify+"</td><td><a href='javascript:editDevBase(\""+msg.result.base[i].baseName+"\")'>查看</a></td></tr>";
		$("#base").find("tbody").append(html)
	}
	for(var i=0;i<msg.result.biz.length;i++){
		var html="<tr><td>"+msg.result.biz[i].bizName+"</td><td><a target='_blank' href='biz_show_dev/"+msg.result.biz[i].bizName+"'>"+msg.result.biz[i].bizName+"</a></td><td>"+msg.result.biz[i].lastModify+"</td><td><a href='javascript:editDevBiz(\""+msg.result.biz[i].bizName+"\")'>查看</a></td></tr>";
		$("#biz").find("tbody").append(html)
	}
}
function editDevBase(name){
	window.parent.$("#main").attr("src","base_edit_dev.jsp?fileName="+name);
}
function editDevBiz(name){
	window.parent.$("#main").attr("src","biz_edit_dev.jsp?fileName="+name);
}
function refreshTaskClazz(){
    $.ajax({
		    type : "POST",
		    url : "../manager/refreshCollectClazz",
		    dataType : "json",
			data : {},
			success : function(msg) {
						if (msg.code == 0) {
						  alert(msg.msg)
						} else {
						  alert(msg.msg)
						}
					},
			 error:function(){
			   location.href="../session_timeout.jsp";
		   }
		  });
}
$(document).ready(function(){
	$("span.glyphicon-chevron-up").click(function(){
		$(this).hide();
		$(this).siblings("span.glyphicon-chevron-down").show();
		$(this).parents("table").find("thead").hide();
		$(this).parents("table").find("tbody").hide();
	});
	$("span.glyphicon-chevron-down").click(function(){
		$(this).hide();
		$(this).siblings("span.glyphicon-chevron-up").show();
		$(this).parents("table").find("thead").show();
		$(this).parents("table").find("tbody").show();
	});
	$("span.glyphicon-plus").click(function(){
		if($(this).attr("id")=="addCore"){
			window.parent.$("#main").attr("src","base_edit_dev.jsp");
		}else if($(this).attr("id")=="addBiz"){
			window.parent.$("#main").attr("src","biz_edit_dev.jsp");
		}
	});
	/* $.ajax({
		   type: "POST",
		   url: "../manager/index_dev",
		   dataType:"json",
		   data:"" ,
		   success: function(msg){
		     if(msg.code==0){
		    	 initJsonFileTable(msg)
		     }else{
		    	
		     }
		   }
	}); */
});
</script>
</head>

<body>

	<div class="container-fluid">
		<div class="row">
			<div class="col-sm-12 col-md-12 main">
				<table class="table" id="base">
					<caption>平台管理工具列表 
					<span class="glyphicon glyphicon-chevron-down" style="display:none" title="展开"></span>
					<span class="glyphicon glyphicon-chevron-up"   title="收起"></span>
					</caption>
					<thead>
						<tr>
							<th width="33%">工具名称</th>
							<th width="34%">工具描述</th>
							<th width="13%">操作</th>
						</tr>
						<tr>
							<td width="33%">采集品类管理</td>
							<td width="34%">用来维护采集任务的采集品类</td>
							<td width="13%"><a href="javascript:refreshTaskClazz()">刷新</a></td>
						</tr>
					</thead>
					<tbody>
					</tbody>
				</table>
			</div>
		</div>
	</div>

	<!-- Bootstrap core JavaScript
    ================================================== -->
	<!-- Placed at the end of the document so the pages load faster -->
	<script src="<%=path%>/resources/js/bootstrap.min.js"></script>
	<script src="<%=path%>/resources/js/docs.min.js"></script>
	<!-- IE10 viewport hack for Surface/desktop Windows 8 bug -->
	<script src="<%=path%>/resources/js/ie10-viewport-bug-workaround.js"></script>
</body>
</html>

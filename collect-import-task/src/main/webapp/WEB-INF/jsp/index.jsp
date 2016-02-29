<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<html>
<head>
<script type="text/javascript"
	src="http://code.jquery.com/jquery-1.11.2.min.js"></script>
<link rel="stylesheet"
	href="http://cdn.bootcss.com/bootstrap/3.3.4/css/bootstrap.min.css">
<script type="text/javascript">
	$(document).ready(
			function() {
				//换表
				$("#importButton").click(
						function() {
							var id = $("#id").val();
							var tableName = $("#id>option:selected").html();
							$("#waitTip").show().html("正在导入...");
							$("#errorLog").html("");
							$.ajax({
								url : "/collect-import-task/importData",
								type : "post",
								data : {
									tableName : tableName
								},
								success : function(data) {
								
									if (data.indexOf("Exception") > 0) {
										$("#waitTip").html("程序出错，导入终止");
										$("#errorLog").html(data);
									} else {
										var re = eval("(" + data + ")");
										$("#waitTip").html(
												"导入新的数据库结果：成功（"
														+ (re.total - re.fall)
														+ "）--- 失败（" + re.fall
														+ "）--- 总计（" + re.total
														+ "）["+re.time/1000+"秒]");
									}
								},
								error : function(data) {
									$("#waitTip").html("导入失败，请检查代码");
								}
							});
						});
				//设置GeoCoding
				$("#GeoCodingButton").click(
						function() {
							var id = $("#id").val();
							var tableName = $("#id>option:selected").html();
							$("#waitTip").show().html("正在计算...");
							$("#errorLog").html("");
							$.ajax({
								url : "/collect-import-task/setGeoCoding",
								type : "post",
								data : {
									tableName : tableName
								},
								success : function(data) {
									//成功启用import
									$("#importButton").removeAttr("disabled");
									if (data.indexOf("Exception") > 0) {
										$("#waitTip").html("程序出错，计算终止");
										$("#errorLog").html(data);
									} else {
										var re = eval("(" + data + ")");
										$("#waitTip").html(
												"计算GeoCoding结果：成功（"
														+ (re.total - re.fall)
														+ "）--- 失败（" + re.fall
														+ "）--- 总计（" + re.total
														+ "）["+re.time/1000+"秒]");
									}
								},
								error : function(data) {
									$("#waitTip").html("计算失败，请检查代码");
								}
							});
						});
			});
</script>
<style type="text/css">
button {
	margin-left: 10px;
}

font {
	color: red;
}

p {
	margin-top: 100px;
}
</style>
</head>
<body class="container">
	<div style="margin-top: 15%;">
		<form id="table" class="form-horizontal">
			<div class="form-group">
				<center>
					<table>
						<td>
							<h4 style="font-size: 24;">表名称：</h4>
						</td>
						<td><select class="form-control" id="id" name="id">
								<c:forEach items="${tableList }" var="tab">
									<option value="">${tab }</option>
								</c:forEach>
						</select></td>
						<td>
							<button type="button" class="btn btn-primary btn"
								id="GeoCodingButton">计算GeoCoding</button>
						</td>
						<td>
							<button type="button" class="btn btn-primary btn"
								id="importButton" disabled="disabled">导入</button>
						</td>
					</table>
				</center>
				<center>
					<font id="waitTip" style="display: none;">正在计算...</font>
				</center>
			</div>
		</form>
		<p id="errorLog"></p>
	</div>
</body>
</html>
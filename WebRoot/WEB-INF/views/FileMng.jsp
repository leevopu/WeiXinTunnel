<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8" import="java.util.*" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <title>素材管理</title>
    <%
    	String 	 path = request.getContextPath();
	%>
    <style type="text/css">
       .input
        {	/* 类选择器:按钮样式 */
        	color: blue;      
	        font-family: "宋体";  
	        font-size: 15px;  
            text-align: center;
            background-color:#C6E2FF; 
            height:30px;
            width:200px;
            border-left-width:1px;
            border-top-width:1px;
            border-right-width:1px;
            border-bottom-width:1px;
        }
        .input:hover{  
	        color:red;  
	        border: none;  
	        cursor: hand;  
	        cursor: pointer;  
		 }  
    </style>
</head>
<body>
	<div align="center" style="height: 15%;">
    	<h1>素材管理</h1>
    </div>
<div style="height: 500px;width: 100%;overflow-y: false;" align="center">
		<form name="myform" action="uploadServlet" method="post" enctype="multipart/form-data">
			<div align="left" style="width: 50%;">
			发送人：<input type="text" class="input" id="sender"  name="sender"/>&nbsp;&nbsp;&nbsp;
			&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
			接收人：<input type="text" class="input" id="receiver"name="receiver"/>
			</div>
			<br>
			<div align="left" style="width: 50%;">
				素材类型：
				<select name="msgType" id="msgType" style="width: 180px;text-align: center;">
					<option value="">请选择...</option> 
					<option value="image">图片</option> 
					<option value="text">文本</option> 
					<option value="video">视频</option> 
					<option value="file">文件</option> 
				</select>
				<br><br>
				请选择文件:<br><br> <input type="file" name="myfile" id="upload-file" class="input"><br> <br>
				<input type="submit" name="submit" value="提交">
			</div>
		</form>
	</div>
</body>
 <script  type="text/javascript">
 </script>
</html>

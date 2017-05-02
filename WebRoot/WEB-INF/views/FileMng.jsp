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
<div style="height: 500px;width: 100%;overflow-y: false;">
		<form name="myform" action="uploadServlet" method="post" enctype="multipart/form-data">
			<div  align="center">
			发送人：<input type="text" class="input" id="sender"  name="sender"/>
			接收人：<input type="text" class="input" id="receiver"name="receiver"/>
			</div>
			<br>
			素材类型：<input type="text" list="types" class="input" name="msgType"  id="mytext"><br>
			<datalist id="types">
				<option value="text">文本</option> 
				<option value="image">图片</option> 
				<option value="video">视频</option> 
				<option value="file">文件</option> 
			</datalist>
			请选择文件:<br> <input type="file" name="myfile" id="upload-file" class="input"><br> <br>
			<input type="submit" name="submit" value="提交">
		</form>
	</div>
</body>
 <script  type="text/javascript">
 function totxt(){ 
	 debugger
	 var msgTypes = ["text","image","video","file"];
	 var msgTexts = ["文本","图片","视频","文件"];
	 var text =  document.getElementById("mytext");
	 for (var i = 0; i < msgTypes.length; i++) {
		var msgType = msgTypes[i];
		var msgText = msgTexts[i];
		if(text.value==msgType){
			text.innerHTML=msgText;
			return;
		}	
	}
	 //给Id为mytext的文本框赋值 
	//.InnerHTML=seltxt;
	 } 
 </script>
</html>

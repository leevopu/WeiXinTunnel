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
	.smart-green {
		margin-left: auto;
		margin-right: auto;
		max-width: 600px;
		background: #E6F6F0;
		padding: 30px 30px 20px 30px;
		font: 12px Arial, Helvetica, sans-serif;
		color: #666;
	}
	
	.smart-green h1 {
		font: 24px  sans-serif;
		padding: 20px 0px 20px 40px;
		display: block;
		margin: -30px -30px 10px -30px;
		color: #FFF;
		background: #EE6666;
		text-shadow: 1px 1px 1px #949494;
		border-radius: 5px 5px 0px 0px;
		border-bottom: 1px solid #89AF4C;
	}
	
	.smart-green h1 span {
		display: block;
		font-size: 11px;
		color: #FFF;
	}
	
	.smart-green label {
		display: block;
		margin: 0px 0px 5px;
	}
	
	.smart-green label span {
		float: left;
		margin-top: 10px;
		color: #5E5E5E;
	}
	
	.smart-green input[type="text"],.smart-green textarea, .smart-green select {
		color: #555;
		height: 30px;
		line-height: 15px;
		width: 100%;
		padding: 0px 0px 0px 10px;
		margin-top: 2px;
		border: 1px solid #E5E5E5;
		background: #FBFBFB;
		outline: 0;
		-webkit-box-shadow: inset 1px 1px 2px rgba(238, 238, 238, 0.2);
		box-shadow: inset 1px 1px 2px rgba(238, 238, 238, 0.2);
		font: normal 14px/14px 宋体;
	}
	
	.smart-green textarea {
		height: 60px;
		padding-top: 10px;
	}
	
	.smart-green select {
		background: no-repeat right,
			-moz-linear-gradient(top, #FBFBFB 0%, #E9E9E9 100%);
		background: no-repeat right,
			-webkit-gradient(linear, left top, left bottom, color-stop(0%, #FBFBFB),
			color-stop(100%, #E9E9E9));
		appearance: none;
		-webkit-appearance: none;
		-moz-appearance: none;
		text-indent: 0.01px;
		text-overflow: '';
		width: 100%;
		height: 30px;
	}
	
	.smart-green .button {
		background-color: #9DC45F;
		border-radius: 5px;
		-webkit-border-radius: 5px;
		-moz-border-border-radius: 5px;
		border: none;
		padding: 10px 25px 10px 25px;
		color: #FFF;
		text-shadow: 1px 1px 1px #949494;
	}
	
	.smart-green .button:hover {
		background-color: #80A24A;
	}
	
</style>
</head>
<body>
	<div class="smart-green">
		<form name="myform" action="uploadServlet" method="post" enctype="multipart/form-data">
			<h1>
				素材管理 
				<br/>
				<span>企业公众号素材管理.</span>
			</h1>
			<!-- <label> 
				<span>发 送 人 :</span> 
				<input id="fromUser" type="text"name="fromUser"/>
			</label>  -->
			<label> 
				<span>接 收 人 [OA的PK(,|分割)]:</span> 
				<input id="toUser"type="text" name="toUser" />
			</label> 
			<label> 
				<span>触发时间 [若不填则默认立即发送]:</span> 
				<input type="text" class="sang_Calender"id="sendTime"name="sendTime"/>
			</label> 
			<label> 
				<span>消息类型 :</span> 
				<select name="msgType" id="msgType" onchange="typeChange(this.options[this.options.selectedIndex].value)">
					<option value="">请选择...</option> 
					<option value="image">图片[微信仅支持：jpg、png]</option> 
					<option value="mpnews">图文</option>
					<option value="text">文本</option> 
					<option value="video">视频</option> 
					<option value="file">文件</option> 
				</select>
			</label> 
			<div id = "hiddenFlag" style="display: none">
				<label> 
					<span>标题 :</span> 
					<input id="title" type="text" name="title"/>
				</label>
			</div>  
			<label> 
				<span>文本 :</span> 
				<textarea id="text"name="text"></textarea>
			</label>
			<label> 
				<span>请选择文件 :</span>
				<br/><br/>
				<input type="file" name="myfile" id="upload-file"  style="border: 3px;">
			</label>
			<label> 
				<span>&nbsp;</span> <br/>
				<input type="submit" name="submit" class="button" value="提交" />
			</label>
		</form>
	</div>
</body>
 <script src="<%=path%>/js/datetime.js" type="text/javascript" ></script>
 <script type="text/javascript">
 	function typeChange(v){
 		if ("mpnews"==v){
 			document.getElementById("hiddenFlag").style.display="block";
 		}else
 		{
 			document.getElementById("hiddenFlag").style.display="none";
		}
 	}
 </script>
</html>

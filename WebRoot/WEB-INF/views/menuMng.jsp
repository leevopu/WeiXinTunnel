<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8" import="java.util.*" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <title>菜单管理</title>
    <%
    	String 	 path = request.getContextPath();
		String   str  =(String)request.getAttribute("str");
	%>
    <script src="<%=path%>/js/jquery.min.js" type="text/javascript" ></script>
    <style type="text/css">
        th, td
        {
            border: 1px solid #b5d6e6;
            font-size: 14px;
            font-weight: normal;
            text-align: center;
            vertical-align: middle;
        }
        th
        {
            background-color: Gray;
        }    
        tr{
        	height: 40px;
        }
        .input
        {	/* 类选择器:按钮样式 */
        	color: blue;      
        	background-color: #FFFFFF;  
	        font-family: "宋体";  
	        font-size: 15px;  
	        font-weight: normal;
            text-align: center;
            background-color:#C6E2FF; 
            height:30px;
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
    	<h1>公众号菜单管理</h1>
    </div>
<div style="height: 500px;width: 100%;overflow-y: false;">
	<div align="left">
		<input id="add" 	class="input" type="button"		value="新增" />
		&nbsp;
		<input id="remove"  class="input" type="button" 	value="删除" /> 
	</div>
	
	<div style="border:1px solid #F00;overflow-y: true;">
		<table id="table" align="center" cellspacing="0" width="100%" >
			<tr>
				<th style="width: 160px;"></th>
				<th style="width: 160px;">菜单名</th>
				<th style="width: 160px;">菜单id</th>
				<th style="width: 160px;">菜单类型</th>
				<th style="width: 160px;">上级菜单</th>
			</tr>
		</table>
	</div>
	<form action="save.do" method="post">
		<div align="center">
			<input id= "result" name="result" type="hidden" />
			<input  class="input"  align="middle" type="submit" onclick="saveBefore()" value="保存" />
		</div>
	</form>
</div>
</body>
 <script  type="text/javascript">
 		$(document).ready(function () {	
 			$("#add").bind("click", addRow);
 	    	$("#remove").bind("click", removeRow);
 	    	load();
 		});
    	
    	var table = document.getElementById("table");
    	var menusList ;
    	
    	//初始化加载菜单列表
    	function load(){
    		var datas =<%=str %>;
    		menusList = dataFormat(datas.button);
    		for (var i = 0; i < menusList.length; i++) {
    			addRow();//新增行
    			var menu = menusList[i];
    			var row = table.rows[i+1];
    			for (var j = 1; j < row.cells.length; j++) {
    				var cell = row.cells[j];
    				cell.childNodes[0].value  = menu[j-1];
    			}
			}
    	}
    	//数据解析：一、二级菜单数据处理 
    	function dataFormat(datas){
    		var arr = [];//菜单数组
    		for (var i = 0; i < datas.length; i++) {
    			var menuList   = []; //单条菜单数据 一级
    			var childsMenu = [];//子菜单集合
    			var data = datas[i];
    			data.parent="无";
    			if(data.hasOwnProperty("sub_button")&&data.sub_button.length>1){
    				data.key = "FIRST_FLOOR";
    				data.type= "click";
    				var buttons = data.sub_button;
    				for (var j = 0; j < buttons.length; j++) {
    					var childMenu = [];
						var button = buttons[j];
						button.parent = data.name;
						childMenu.push(button.name,button.key,button.type,button.parent);
						childsMenu.push(childMenu);
					}
    			}
    			menuList.push(data.name,data.key,data.type,data.parent);
    			arr.push(menuList);
    			Array.prototype.push.apply(arr, childsMenu); 
			}
    		return arr;
    	}
    	//在最后插入新行
    	function addRow() {
    		var td1="<td height=\"40px\"><input name=\"ckb\"  type=\"checkbox\"/> </td>";
    		var td2="<td height=\"40px\"><input name=\"name\" type=\"text\" style=\"border: none;\"/> </td>";
    		var td3="<td height=\"40px\"><input name=\"key\"  type=\"text\" style=\"border: none;\"/> </td>";
    		var td4="<td height=\"40px\"><input name=\"type\" type=\"text\" style=\"border: none;\"/> </td>";
    		var td5="<td height=\"40px\"><input name=\"parent\" type=\"text\" style=\"border: none;\"/> </td>";
    	    var newRow = "<tr>"+td1+td2+td3+td4+td5+"</tr>";
    	    $("#table tr:last").after(newRow);
    	}
    	//删除指定行
    	function removeRow() {
    		var i=0;
    		var n=0;
    		$("input[name=\"ckb\"]:checked").each(function() { //遍历选中的checkbox
    			i++;
    			n = $(this).parents("tr").index();  // 获取checkbox所在行的序列
            });
    		if(i==0){
    			alert("请选择要删除的行！");
    		}else
    		if(i>1){
    			alert("每次只能删除一行，请重新选择！");
    		}else{
    			var isDel = confirm("确定要删除这一行数据？");
    			if(isDel){
                    $("table").find("tr:eq("+n+")").remove();
    			}
    		}
    	}
    	
    	//保存之前数据格式处理
    	
		function saveBefore() {
			var length = table.rows.length;
			var menus1 = [];//一级菜单
			var menus2 = [];//二级菜单
			for (var i = 1; i < length; i++) {
				var row = table.rows[i];
				var menu ={};
				for (var j = 1; j < row.cells.length; j++) { //遍历Row中的每一列
					var cell = row.cells[j].childNodes[0];//..[0]为input元素
					var key = cell.name;	//input框的name属性
					var value = cell.value; //input框的value
					menu[key] = value;
				}
				if("无"==menu.parent){
					menus1.push(menu);
				}else {
					menus2.push(menu);
				}
			}
			//遍历 ：将二级菜单 放入对应的一级菜单中
			for (var k = 0; k < menus1.length; k++) {
				var mn1 = menus1[k];
				var name = mn1.name;
				var arr = [];
				for (var n = 0; n < menus2.length; n++) {
					var parent = menus2[n].parent;
					if(name == parent){
						var mn2 = menus2[n];
						mn2["sub_button"] = [];
						delete mn2["parent"];
						arr.push(mn2);
					}
				}
				if(arr.length>0){//有二级菜单的一级菜单选项
					mn1["sub_button"]=arr;
				}
				delete mn1["parent"];
			}
			var result = {};
			result["button"] = menus1;
			result = JSON.stringify(result);
			document.getElementById("result").value=result;
		}
	</script>
</html>

<%@ page language="java" import="java.util.*" pageEncoding="utf8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <base href="<%=basePath%>">
    
    <title>My JSP 'index.jsp' starting page</title>
    
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
	
  </head>
  
  <body>
   	用户注册
   	<form action="${pageContext.request.contextPath }/user/login" method="post">
   		<table border="1" cellspacing="1" cellpadding="1">
   			<tr>
   				<td>用户名：<input type="text" name="username"></td>
   			</tr>
   			<tr>
   				<td>密码：<input type="password" name="password"></td>
   			</tr>
   			<tr>
   				<td>邮箱：<input type="text" name="email"></td>
   			</tr>
   			<tr>
   				<td>手机：<input type="text" name="phone"></td>
   			</tr>
   			<tr><td><input type="submit" value="注册"></td></tr>
   		</table>
   	</form>
  </body>
</html>

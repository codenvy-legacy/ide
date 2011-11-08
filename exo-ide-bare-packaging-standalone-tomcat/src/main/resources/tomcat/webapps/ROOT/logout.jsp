<%@ page language="java" contentType="text/html; charset=UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<title>eXo IDE</title>
</head>
<body>
<%@page import="java.util.*"%>
<%
   session.invalidate();
   //response.sendRedirect("/IDE/Application.html");
   response.sendRedirect("/");
%>
</body>
</html>

<%@ page language="java" contentType="text/html; charset=UTF-8"%>
<%--
  ~ CODENVY CONFIDENTIAL
  ~ __________________
  ~
  ~ [2012] - [2013] Codenvy, S.A.
  ~ All Rights Reserved.
  ~
  ~ NOTICE:  All information contained herein is, and remains
  ~ the property of Codenvy S.A. and its suppliers,
  ~ if any.  The intellectual and technical concepts contained
  ~ herein are proprietary to Codenvy S.A.
  ~ and its suppliers and may be covered by U.S. and Foreign Patents,
  ~ patents in process, and are protected by trade secret or copyright law.
  ~ Dissemination of this information or reproduction of this material
  ~ is strictly forbidden unless prior written permission is obtained
  ~ from Codenvy S.A..
  --%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<body>
<%@page import="java.util.*"%>
<%
System.out.println(">>>>> "+request.getAttribute("ws"));
   response.sendRedirect(request.getHeader("Referer"));
%>
</body>
</html>
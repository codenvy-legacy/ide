<%--
   CODENVY CONFIDENTIAL
   __________________

   [2012] - [2013] Codenvy, S.A.
   All Rights Reserved.

   NOTICE:  All information contained herein is, and remains
   the property of Codenvy S.A. and its suppliers,
   if any.  The intellectual and technical concepts contained
   herein are proprietary to Codenvy S.A.
   and its suppliers and may be covered by U.S. and Foreign Patents,
   patents in process, and are protected by trade secret or copyright law.
   Dissemination of this information or reproduction of this material
   is strictly forbidden unless prior written permission is obtained
   from Codenvy S.A..
  --%>

<%@ page language="java" contentType="text/html; charset=UTF-8" %>

<%
    String username = request.getParameter("username");
    String password = request.getParameter("password");
    if (!(username == null || password == null)) {
        response.sendRedirect("j_security_check?j_username=" + username + "&j_password=" + password);
        return;
    }
%>
<html>
<head>
    <title>Codenvy Login Page</title>
    <link rel="stylesheet" type="text/css" href="loginPageStyle.css">
    <script type="text/javascript" src="popup.js"></script>
    <script type="text/javascript">
        var REST_SERVICE_CONTEXT = "/rest";

        function getXmlHTTP() {
            var xmlHttp = null;
            if (window.XMLHttpRequest) {
                xmlHttp = new XMLHttpRequest();
            }
            else if (window.ActiveXObject) {
                xmlHttp = new ActiveXObject("Microsoft.XMLHTTP");
            }
            return xmlHttp;
        }
    </script>
</head>
<body bgcolor="#f3f3f3" style="font-family: 'Roboto',sans-serif">
    <div class="logo">
        <img alt="Codenvy" src="/ide/_app/logoCodenvy2x.png">
    </div>
    <div class="container center">
        <div id="loginFormId">
            <h2>Sign in to Codenvy</h2>
            <hr>
            <form method="POST" action='<%= "j_security_check"%>'>
                <div class="inputLabel">User ID:</div>
                <div class="field">
                    <input type="text" id="userId" class="input" name="j_username" value="ide">
                </div>
                <div class="inputLabel">Password:</div>
                <div class="field">
                    <input type="password" id="userPassword" class="input" name="j_password" value="codenvy123">
                </div>
                <div class="field">
                    <input type="submit" value="Sign In" id="loginButton" class="button"/>
                    <input type="reset" value="Reset" class="button">
                </div>
            </form>
            <div style="padding-bottom: 30px">
                <table border="0" align="center">
                    <thead>
                        <tr>
                            <td style="padding: 2" align="center">User ID / Password</td>
                            <td style="padding: 2" align="center">User role</td>
                        </tr>
                    </thead>
                    </tbody>
                        <tr>
                            <td style="padding: 2" align="left">ide/codenvy123</td>
                            <td style="padding: 2" align="left">developers</td>
                        </tr>
                    </tbody>
                </table>
            </div>
        </div>
    </div>
</body>
</html>

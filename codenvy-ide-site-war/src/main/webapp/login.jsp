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

        function showRegisterForm() {
            document.getElementById("loginFormId").style.display = "none";
            document.getElementById("registerForm").style.display = "block";
            document.getElementById("newUserID").value = "";
            document.getElementById("newUserPassword").value = "";
        }

        function showloginFormId() {
            document.getElementById("registerForm").style.display = "none";
            document.getElementById("loginFormId").style.display = "block";
        }

        function isEmail(login) {
            var pattern = /^(([^<>()[\]\\.,;:\s@\"]+(\.[^<>()[\]\\.,;:\s@\"]+)*)|(\".+\"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
            return pattern.test(login);
        }

        function registerNewUser() {
            var login = document.getElementById("newUserID").value;
            var password = document.getElementById("newUserPassword").value;
            var email = login;
            if (!isEmail(login)) {
                email += "@localhost"
            }

            var body = "{";
            body += "\"id\":\"" + login + "\",";
            body += "\"password\":\"" + password + "\",";
            body += "\"firstName\":\"" + login + "\",";
            body += "\"lastName\":\"" + login + "\",";
            body += "\"email\":\"" + email + "\"";
            body += "}";

            var url = REST_SERVICE_CONTEXT + "/users/person";
            var xmlHttp = getXmlHTTP();
            xmlHttp.open('POST', url, true);

            xmlHttp.onreadystatechange = function () {
                if (xmlHttp.readyState == 4) {
                    if (xmlHttp.status == 201) {
                        alert("User " + login + " created.");
                        document.getElementById("userId").value = login;
                        document.getElementById("userPassword").value = password;
                        showloginFormId();
                    }
                    else {
                        alert("Can't register user.");
                    }
                }
            }

            xmlHttp.setRequestHeader("Content-type", "application/json");
            xmlHttp.send(body);
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
            <div>
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
            <div class="field center">
                <input type="button" value="New User" class="button" onclick="showRegisterForm();"/>
            </div>
        </div>

        <div id="registerForm" class="container" style="display:none;">
            <h2>Register new user</h2>
            <hr>
            <div style="width:1px; height:3px;"></div>
            <div class="inputLabel">User ID:</div>
            <div class="field">
                <input id="newUserID" type="text" name="userid" class="input">
            </div>
            <div class="inputLabel">Password:</div>
            <div class="field">
                <input id="newUserPassword" type="password" name="password" class="input">
            </div>
            <div style="width:1px; height:3px;"></div>
            <div class="field">
                <td align="right"><input type="button" class="button" value="Register" onclick="registerNewUser();"></td>
                <td align="left"><input type="button" class="button" value="Cancel" onclick="showloginFormId();"></td>
            </div>
        </div>
    </div>
</body>
</html>

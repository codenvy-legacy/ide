<%--

    Copyright (C) 2012 eXo Platform SAS.

    This is free software; you can redistribute it and/or modify it
    under the terms of the GNU Lesser General Public License as
    published by the Free Software Foundation; either version 2.1 of
    the License, or (at your option) any later version.

    This software is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
    Lesser General Public License for more details.

    You should have received a copy of the GNU Lesser General Public
    License along with this software; if not, write to the Free
    Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
    02110-1301 USA, or see the FSF site: http://www.fsf.org.

--%>
<%@ page language="java" contentType="text/html; charset=UTF-8" %>

<%-- Login over Open ID or OAuth --%>
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
            document.getElementById("federatedloginFormId").style.display = "none";
            document.getElementById("registerForm").style.display = "block";
            document.getElementById("newUserID").value = "";
            document.getElementById("newUserPassword").value = "";
        }

        function showloginFormId() {
            document.getElementById("registerForm").style.display = "none";
            document.getElementById("loginFormId").style.display = "block";
            document.getElementById("federatedloginFormId").style.display = "block";
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

        function open_popup_openid(provider) {
            var popup = new Popup(
                    '<%= request.getContextPath() %>/rest/ide/openid/authenticate?popup=&favicon=&openid_provider=' + provider + '&redirect_after_login=/site/index.html',
                    "/site/index.html",
                    450,
                    500);
            popup.open_window();
        }

        function open_popup_oauth(provider, scopes) {
            var url = '<%= request.getContextPath() %>/rest/ide/oauth/authenticate?oauth_provider=' + provider + '&mode=federated_login&redirect_after_login=/site/index.html';
            for (var i = 0; i < scopes.length; i++) {
                url += ('&scope=' + scopes[i]);
            }
            var popup = new Popup(url, "/site/index.html", 450, 500);
            popup.open_window();
        }
    </script>
</head>
<body bgcolor="white">
<table width="100%" height="100%">
    <tr align="center" valign="bottom">
        <td><img alt="ide" src="/ide/_app/codenvy-logo.png"></td>
    </tr>
    <tr align="center" valign="top">
        <td>
            <form id="loginFormId" method="POST" action='<%= "j_security_check"%>'>
                <table border="0" cellspacing="5">
                    <tr>
                        <th align="right">User ID:</th>
                        <td align="left"><input type="text" id="userId" name="j_username" value="ide"></td>
                    </tr>
                    <tr>
                        <th align="right">Password:</th>
                        <td align="left"><input type="password" id="userPassword" name="j_password" value="codenvy123"></td>
                    </tr>
                    <tr>
                        <td align="right"><input type="submit" value="Log In" id="loginButton"/></td>
                        <td align="left"><input type="reset" value="Reset"></td>
                    <tr>
                        <td colspan="3">
                            <table border="0">
                                <tr bgcolor="#DDDDDD">
                                    <td style="padding: 2" align="center">User ID / Password</td>
                                    <td style="padding: 2" align="center">User role</td>
                                </tr>
                                <tr>
                                    <td style="padding: 2" align="left">ide/codenvy123</td>
                                    <td style="padding: 2" align="left">developers</td>
                                </tr>
                            </table>
                        </td>
                    </tr>
                    <tr>
                        <td colspan="3" align="center">
                            <input type="button" value="New User" onclick="showRegisterForm();"/>
                        </td>
                    </tr>
                </table>
            </form>
            <div id="federatedloginFormId">
                <button
                        onclick="window.location.replace('<%= request.getContextPath() %>/rest/ide/oauth/authenticate?oauth_provider=google&mode=federated_login&scope=https://www.googleapis.com/auth/userinfo.profile&scope=https://www.googleapis.com/auth/userinfo.email&scope=https://www.googleapis.com/auth/appengine.admin&scope=https://www.google.com/m8/feeds&redirect_after_login=/site/index.html');">
                    <img src="http://www.google.com/favicon.ico"/>&nbsp; Sign in with a Google Account (OAuth)
                </button>
                <button
                        onclick="window.location.replace('<%= request.getContextPath() %>/rest/ide/oauth/authenticate?oauth_provider=github&scope=user&scope=repo&mode=federated_login&redirect_after_login=/site/index.html');">
                    <img src="octocat.png"/>&nbsp; Sign in with a GitHub Account(OAuth)
                </button>
                <br/>
                <button
                        onclick="open_popup_oauth('google', ['https://www.googleapis.com/auth/userinfo.profile', 'https://www.googleapis.com/auth/userinfo.email', 'https://www.googleapis.com/auth/appengine.admin', 'https://www.google.com/m8/feeds'])">
                    <img src="http://www.google.com/favicon.ico"/>&nbsp; Sign in with a Google Account (OAuth Popup)
                </button>
                <button onclick="open_popup_oauth('github', ['user', 'repo'])">
                    <img src="octocat.png"/>&nbsp; Sign in with a GitHub Account (OAuth Popup)
                </button>
                <br/>
                <br/>
                <button
                        onclick="window.location.replace('<%= request.getContextPath() %>/rest/ide/openid/authenticate?openid_provider=google&favicon=&redirect_after_login=/site/index.html');">
                    <img src="http://www.google.com/favicon.ico"/>&nbsp; Sign in with a Google Account (OpenId)
                </button>
                <button onclick="open_popup_openid('google');">
                    <img src="http://www.google.com/favicon.ico"/>&nbsp; Sign in with a Google Account (OpenId Popup)
                </button>
            </div>
            <div id="registerForm" style="display:none;">
                <table border="0" cellspacing="5">
                    <tr>
                        <th colspan="2">Register new user</th>
                    </tr>
                    <tr>
                        <td colspan="2">
                            <div style="width:1px; height:3px;"></div>
                        </td>
                    </tr>
                    <tr>
                        <th align="right">User ID:</th>
                        <td align="left"><input id="newUserID" type="text" name="userid"></td>
                    </tr>
                    <tr>
                        <th align="right">Password:</th>
                        <td align="left"><input id="newUserPassword" type="password" name="password"></td>
                    </tr>
                    <tr>
                        <td colspan="2">
                            <div style="width:1px; height:3px;"></div>
                        </td>
                    </tr>
                    <tr>
                        <td align="right"><input type="button" value="Register" onclick="registerNewUser();"></td>
                        <td align="left"><input type="button" value="Cancel" onclick="showloginFormId();"></td>
                    </tr>
                </table>
            </div>
        </td>
    </tr>
</table>
</body>
</html>

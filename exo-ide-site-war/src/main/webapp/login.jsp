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
<%@page language="java" contentType="text/html; charset=UTF-8" %>

<%@page import="org.exoplatform.ide.authentication.openid.OpenIDUser" %>
<%@page import="java.net.URLEncoder" %>

<%
   OpenIDUser user = (OpenIDUser)request.getSession().getAttribute("openid.user");
   if (user != null)
   {
      request.getSession().removeAttribute("openid.user");
      response.sendRedirect("j_security_check?j_username=" + URLEncoder.encode(user.getAttribute("email")) +
                                            "&j_password=" + URLEncoder.encode(user.getIdentifier().getIdentifier()));
      return;
   }
%>
<html>
   <head>
      <title>eXo IDE Login Page</title>
      <script type="text/javascript" src="popup.js"></script>
      <script type="text/javascript">
         var REST_SERVICE_CONTEXT = "/rest";

         function getXmlHTTP()
         {
            var xmlHttp = null;
            if (window.XMLHttpRequest)
            {
               xmlHttp = new XMLHttpRequest();
            }
            else if (window.ActiveXObject)
            {
               xmlHttp = new ActiveXObject("Microsoft.XMLHTTP");
            }
            return xmlHttp;
         }

         function showRegisterForm()
         {
            document.getElementById("loginForm").style.display="none";
            document.getElementById("federatedLoginForm").style.display="none";
            document.getElementById("registerForm").style.display="block";
            document.getElementById("newUserID").value = "";
            document.getElementById("newUserPassword").value = "";
         }

         function showLoginForm()
         {
            document.getElementById("registerForm").style.display="none";
            document.getElementById("loginForm").style.display="block";
            document.getElementById("federatedLoginForm").style.display="block";
         }

         function isEmail(login)
         {
            var pattern = /^(([^<>()[\]\\.,;:\s@\"]+(\.[^<>()[\]\\.,;:\s@\"]+)*)|(\".+\"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
            return pattern.test(login);
         }

         function registerNewUser()
         {
            var login = document.getElementById("newUserID").value;
            var password = document.getElementById("newUserPassword").value;
            var email = login;
            if (!isEmail(login))
            {
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

            xmlHttp.onreadystatechange = function()
            {
                if (xmlHttp.readyState == 4)
                {
                    if (xmlHttp.status == 201)
                    {
                       alert("User " + login + " created.");
                       document.getElementById("userId").value = login;
                       document.getElementById("userPassword").value = password;
                       showLoginForm();
                    }
                    else
                    {
                       alert("Can't register user.");
                    }
                }
            }

            xmlHttp.setRequestHeader("Content-type", "application/json");
            xmlHttp.send(body);
         }

         function open_popup(provider)
         {
            var popup = new Popup(
                '<%= request.getContextPath() %>/rest/ide/auth/openid/authenticate?popup=&favicon=&openid_provider=' + provider,
                "/site/index.html",
                450,
                500);
            popup.open_window();
         }
      </script>
   </head>
   <body bgcolor="white">
      <table width="100%" height="100%">
         <tr align="center" valign="bottom">
            <td><img alt="ide" src="/IDE/eXo-IDE-Logo.png"></td>
         </tr>
         <tr align="center" valign="top">
            <td>
               <form id="loginForm" method="POST" action='<%= "j_security_check"%>'>
                  <table border="0" cellspacing="5">
                     <tr>
                        <th align="right">User ID:</th>
                        <td align="left"><input type="text" id="userId" name="j_username" value="exo"></td>
                     </tr>
                     <tr>
                        <th align="right">Password:</th>
                        <td align="left"><input type="password" id="userPassword" name="j_password" value="exo"></td>
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
                                 <td style="padding: 2" align="left">exo/exo</td>
                                 <td style="padding: 2" align="left">administrators and developers</td>
                              </tr>
                           </table>
                        </td>
                     </tr>
                     <tr>
                        <td colspan="3" align="center">
                           <input type="button" value="New User" onclick="showRegisterForm();" />
                        </td>
                     </tr>
                  </table>
               </form>
               <div id="federatedLoginForm">
                  <button onclick="window.location.replace('<%= request.getContextPath() %>/rest/ide/auth/openid/authenticate?openid_provider=google&favicon=&redirect_after_login=/site/index.html');">
                     <img src="http://www.google.com/favicon.ico" />&nbsp; Sign in with a Google Account</button>
                  <button onclick="open_popup('google');">
                     <img src="http://www.google.com/favicon.ico" />&nbsp; Sign in with a Google Account popup</button>
               </div>
               <div id="registerForm" style="display:none;">
                  <table border="0" cellspacing="5">
                     <tr>
                        <th colspan="2">Register new user</th>
                     </tr>
                     <tr>
                        <td colspan="2"><div style="width:1px; height:3px;"></div></td>
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
                        <td colspan="2"><div style="width:1px; height:3px;"></div></td>
                     </tr>
                     <tr>
                        <td align="right"><input type="button" value="Register" onclick="registerNewUser();"></td>
                        <td align="left"><input type="button" value="Cancel" onclick="showLoginForm();"></td>
                     </tr>
                  </table>
               </div>
            </td>
         </tr>
      </table>
   </body>
</html>

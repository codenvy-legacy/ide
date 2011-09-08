<html>

<!-- %
   if(!request.isSecure())
   {
      String location = "https://" + request.getServerName() + ":8443" + "/IDE/Application.html";
      response.sendRedirect(location);
      return;
   } 
   
% -->

<head>
	<title>eXo IDE Login Page</title>
	
<script type="text/javascript">

var REST_SERVICE_CONTEXT = "/rest";

function getXmlHTTP() {
    var xmlhttp = null;

    if (window.XMLHttpRequest)
    { 
	xmlhttp = new XMLHttpRequest(); // Firefox, Safari
    } 
    else if (window.ActiveXObject) // Internet Explorer 
    {
	xmlhttp = new ActiveXObject("Microsoft.XMLHTTP");
    }    

    return xmlhttp; 
}

function showRegisterForm() {
	document.getElementById("loginForm").style.display="none";
	document.getElementById("registerForm").style.display="block";
	document.getElementById("newUserID").value = "";
	document.getElementById("newUserPassword").value = "";
}

function showLoginForm() {
	document.getElementById("registerForm").style.display="none";
	document.getElementById("loginForm").style.display="block";
}

function registerNewUser() {
	var login = document.getElementById("newUserID").value;
	var password = document.getElementById("newUserPassword").value;
	
    var body = "{";
	    body += "\"id\":\"" + login + "\",";
	    body += "\"password\":\"" + password + "\",";
	    body += "\"firstName\":\"" + login + "\",";
	    body += "\"lastName\":\"" + login + "\",";
	    body += "\"email\":\"" + login + "@localhost.com\"";
    body += "}";
    
    var url = REST_SERVICE_CONTEXT + "/users/person";
    var xmlhttp = getXmlHTTP();    
    xmlhttp.open('POST', url, true);
    
    /* The callback function */
    xmlhttp.onreadystatechange = function() {
        if (xmlhttp.readyState == 4) {
            if (xmlhttp.status == 201) {
                alert("User " + login + " created.");
                document.getElementById("userId").value = login;
            	document.getElementById("userPassword").value = password;
                showLoginForm();
            }
            else {
            	alert("Can't register user.");
            }
        }
    }
    
    /* Send the POST request */
    xmlhttp.setRequestHeader("Content-type", "application/json");
    xmlhttp.send(body);
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

			<form id="loginForm" method="POST" action='<%=response.encodeURL("j_security_check")%>'>
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
				<td colspan="2"><div style="width:1px; height:3px;"></div></td>
			</tr>

			<tr>
				<td align="right"><input type="submit" value="Log In"></td>
				<td align="left"><input type="reset" value="Reset"></td>
			</tr>

			<tr>
				<td colspan="2"><div style="width:1px; height:5px;"></div></td>
			</tr>

			<tr>
				<td colspan="2">
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
				<td colspan="2"><hr></td>
			</tr>
			
			<tr>
				<td>&nbsp;</td>
				<td>
					<input type="button" value="New User" onclick="showRegisterForm();" />
				</td>
			</tr>
			
		</table>
			</form>
		
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


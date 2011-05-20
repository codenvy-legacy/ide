<html>
<%
   if(!request.isSecure())
   {
      String location = "https://" + request.getServerName() + ":8443" + "/IDE/Application.html";
      response.sendRedirect(location);
      return;
   } 
   
%>
<head>
<title>eXo IDE Login Page</title>
<body bgcolor="white">
<table width="100%" height="100%">
	<tr align="center" valign="bottom">
		<td><img alt="ide" src="/IDE/eXo-IDE-Logo.png"></td>
	</tr>
	<tr align="center" valign="top">
		<td>
		<form method="POST"
			action='<%=response.encodeURL("j_security_check")%>'>
		<table border="0" cellspacing="5">
			<tr>
				<th align="right">Username:</th>
				<td align="left"><input type="text" name="j_username"
					value="john"></td>
			</tr>
			<tr>
				<th align="right">Password:</th>
				<td align="left"><input type="password" name="j_password"
					value="gtn"></td>
			</tr>
			<tr>
				<td align="right"><input type="submit" value="Log In"></td>
				<td align="left"><input type="reset" value="reset"></td>
			</tr>
			<tr>
				<td colspan="2">
				<table border="0">
					<tr bgcolor="#DDDDDD">
						<td style="padding: 2" align="center">User id/password</td>
						<td style="padding: 2" align="center">User role</td>
					</tr>
					<tr>
						<td style="padding: 2" align="left">root/gtn</td>
						<td style="padding: 2" align="left">administrators and developers</td>
					</tr>
					<tr>
						<td style="padding: 2" align="left">john/gtn</td>
						<td style="padding: 2" align="left">developers</td>
					</tr>
					<tr>
						<td style="padding: 2" align="left">admin/gtn</td>
						<td style="padding: " align="left">administrators</td>
					</tr>
				</table>
				</td>
			</tr>
		</table>
		</form>
		</td>
	</tr>
</table>
</body>
</html>

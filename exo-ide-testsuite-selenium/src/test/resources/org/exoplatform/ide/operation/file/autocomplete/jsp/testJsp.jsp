<html>
<%response.setHeader("Session-Timeout","true");%>
<head>
<title>eXo IDE Login Page</title>
  <style>
  
  </style>
</head>
<body bgcolor="white">
<%  
 
%>
  <script>
 var a = "";
    
  </script>
<table width="100%" height="100%">
    <tr align="center" valign="bottom">
        <td><img alt="ide" src="../../eXo-IDE-Logo.png"></td>
    </tr>

    <tr align="center" valign="top">
        <td>
        <form method="POST"
            action='<%=response.encodeURL("j_security_check")%>'>
        </form>
        </td>
    </tr>
</table>
</body>
</html>
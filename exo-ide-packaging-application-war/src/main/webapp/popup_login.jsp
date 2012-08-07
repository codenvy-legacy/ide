<%@page language="java" contentType="text/html; charset=UTF-8" %>

<html>
   <head>
      <script type='text/javascript'>
         window.close();
      </script>
   </head>
   <!-- If popup window not closed - display HTML body. -->
   <body style='font-family: Verdana, Bitstream Vera Sans, sans-serif; font-size: 13px; font-weight: bold;'>
      <div align='center' style='margin: 100 auto; border: dashed 1px #CACACA; width: 450px;'>
         <% if (session.getAttribute("openid.user") != null) { %>
         <p>Authentication successful. Please, close this window.</p>
         <% } else { %>
         <p>Authentication failed. Result: <%= session.getAttribute("openid.mode") %>. Please, close this window.</p>
         <% } %>
         <img src='../IDE/images/logo/exo_logo.png' alt='exo_logo.png'/>
      </div>
   </body>
</html>

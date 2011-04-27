<html>
   <head>
     <%
       import org.exoplatform.services.security.Identity;
       import org.exoplatform.services.security.ConversationState;
     %>
   </head>
   <script>
      var a='';
   </script>
   
   <body>
     <%
       ConversationState curentState = ConversationState.getCurrent();
       if (curentState != null)
       {
         Identity identity = curentState.getIdentity();
         for (int i = 0; i < 3; i++)
         {
           System.out.println("Hello " + identity.getUserId());
         }
                  
         String a;
       }
     %>
   </body>
</html>
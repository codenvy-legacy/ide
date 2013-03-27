/*
 * Copyright (C) 2010 eXo Platform SAS.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package com.codenvy.ide.template;

import com.codenvy.ide.rest.MimeType;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version @version $Id: $
 */

public class FileTemplates
{

   private static final Map<String, String> TEMPLATES = new HashMap<String, String>();

   static
   {
      addXMLFileContent();
      addEmptyHTML();
      addGroovyContent();
      addEmptyTXT();
      addGadgetContent();
      addGtmplTemplate();
      addRubyTemplate();
      addPhpTemplate();
      addJspTemplate();
      addJavaTemplate();
      addPythonTemplate();
   }

   public static String getTemplateFor(String mimeType)
   {
      String content = TEMPLATES.get(mimeType);
      if (content == null)
      {
         content = "";
      }
      return content;
   }

   private static void addJspTemplate()
   {
      String content =
         "<%@page contentType=\"text/html\" import=\"java.util.Date\"%>\n"
            + "<html>\n"
            + "  <body>\n"
            + "    <div align=\"center\">\n"
            + "      <center>\n"
            + "       <table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"460\"   bgcolor=\"#EEFFCA\">\n"
            + "        <tr><td width=\"100%\"><font size=\"6\" color=\"#008000\">&nbsp;Date example</font></td></tr>\n"
            + "        <tr><td width=\"100%\"><b>&nbsp;Current Date and time is:&nbsp;<font color=\"#FF0000\"><%=new Date().toString()%> </font></b></td></tr>\n"
            + "     </table>\n" + "  </center>\n" + " </div>\n" + " </body>\n" + "</html>";
      TEMPLATES.put(MimeType.APPLICATION_JSP, content);
   }

   private static void addJavaTemplate()
   {
      String content =
         "public class HelloWorldApp {\n" + "    public static void main(String[] args) {\n"
            + "      System.out.println(\"Hello World!\");\n" + "    }\n}";
      TEMPLATES.put(MimeType.APPLICATION_JAVA, content);
   }

   private static void addPhpTemplate()
   {
      String content =
         "<html>\n" + "  <head>\n" + "     <title>PHP Test</title>\n" + "  </head>\n" + "  <body>\n"
            + "     <?php echo '<p>Hello World</p>'; ?>\n" + "  </body>\n" + "</html>";
      TEMPLATES.put(MimeType.APPLICATION_PHP, content);
   }

   private static void addGtmplTemplate()
   {
      String content =
         "<html>\n" + "   <head>\n" + "     <%\n" + "       import org.exoplatform.services.security.Identity;\n"
            + "       import org.exoplatform.services.security.ConversationState;\n" + "     %>\n" + "   </head>\n"
            + "   <body>\n" + "     <%\n" + "       ConversationState curentState = ConversationState.getCurrent();\n"
            + "       if (curentState != null)\n" + "       {\n"
            + "         Identity identity = curentState.getIdentity();\n" + "         3.times\n" + "         {\n"
            + "           println \"Hello \" + identity.getUserId();\n" + "         }\n" + "       }\n" + "     %>\n"
            + "   </body>\n" + "</html>";
      TEMPLATES.put(MimeType.GROOVY_TEMPLATE, content);
   }

   private static void addRubyTemplate()
   {
      String content =
         "# Ruby Sample program\n" + "class HelloClass\n" + "  def sayHello\n" + "    puts( \"Hello, world!\" )\n"
            + "  end\n" + "end\n\n" +

            "ob = HelloClass.new\n" + "ob.sayHello\n";

      TEMPLATES.put(MimeType.APPLICATION_RUBY, content);
   }

   private static void addXMLFileContent()
   {
      String content = "<?xml version='1.0' encoding='UTF-8'?>\n";
      TEMPLATES.put(MimeType.TEXT_XML, content);
   }

   private static void addEmptyHTML()
   {
      String content =
         "" + "<html>\r\n"
            + "  <head>\r\n    <meta http-equiv=\"content-type\" content=\"text/html; charset=UTF-8\">\r\n"
            + "    <title></title>\r\n" + "  </head>\r\n" + "  <body>\r\n" + "  </body>\r\n" + "</html>";
      TEMPLATES.put(MimeType.TEXT_HTML, content);
   }

   private static void addGroovyContent()
   {
      String content =
         "// simple groovy script\n" + "import javax.ws.rs.Path\n" + "import javax.ws.rs.GET\n"
            + "import javax.ws.rs.PathParam\n\n" + "@Path(\"/my-service\")\n" + "public class HelloWorld {\n"
            + "  @GET\n" + "  @Path(\"helloworld/{name}\")\n"
            + "  public String hello(@PathParam(\"name\") String name) {\n" + "    return \"Hello \" + name\n"
            + "  }\n" + "}\n";

      TEMPLATES.put(MimeType.GROOVY_SERVICE, content);
   }

   private static void addEmptyTXT()
   {
      TEMPLATES.put(MimeType.TEXT_PLAIN, "");
   }

   private static void addGadgetContent()
   {
      String content =
         "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n" + "<Module>\n" + "<ModulePrefs title=\"Hello World!\" />\n"
            + "<Content type=\"html\">\n" + "<![CDATA[ Hello, world! ]]>" + "</Content>" + "</Module>";

      TEMPLATES.put(MimeType.GOOGLE_GADGET, content);
   }

   private static void addPythonTemplate()
   {
      String content =
         "# Hello world python program\n"
            + "print \"Hello World!\";";
      TEMPLATES.put(MimeType.TEXT_X_PYTHON, content);
   }
}

/*
 * Copyright (C) 2003-2007 eXo Platform SAS.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see<http://www.gnu.org/licenses/>.
 */
package org.exoplatform.ide.client.model.template;

import java.util.HashMap;

import org.exoplatform.gwtframework.commons.rest.MimeType;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version @version $Id: $
 */

public class FileTemplates
{

   private static final HashMap<String, String> templates = new HashMap<String, String>();

   static
   {
      addXMLFileContent();
      //addHTMLContent();
      addEmptyHTML();
      addGroovyContent();
      addEmptyTXT();
      //addTXTContent();
      addGadgetContent();
      addGtmplTemplate();
      addUWAWidgetContent();
      addClassPathTemplate();
   }

   public static String getTemplateFor(String mimeType)
   {
      String content = templates.get(mimeType);
      if (content == null)
      {
         content = "";
      }

      return content;
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

      templates.put(MimeType.GROOVY_TEMPLATE, content);
   }

   private static void addXMLFileContent()
   {
      String content = "<?xml version='1.0' encoding='UTF-8'?>\n";

      templates.put(MimeType.TEXT_XML, content);
   }
   
   private static void addClassPathTemplate()
   {
      String content =
      "{\n"
  +"\"entries\": [\n"
    +"{\n"
       +"\"kind\": \"file\",\n"
       +"\"path\": \"jcr://repository/dev-monit#/Test.groovy\"\n"
          +"},\n"
          +"{"
       +"\"kind\": \"dir\",\n"
       +"\"path\": \"jcr://repository/dev-monit#/test/\"\n"
          +"}\n"
          +"]\n"
          +"}";

      templates.put(MimeType.APPLICATION_GROOVY_CLASSPATH, content);
   }

   private static void addEmptyHTML()
   {
      String content =
         "" + "<html>\r\n" + "  <head>\r\n" + "    <title></title>\r\n" + "  </head>\r\n" + "  <body>\r\n"
            + "  </body>\r\n" + "</html>";

      templates.put(MimeType.TEXT_HTML, content);
   }

   private static void addHTMLContent()
   {
      String content =
         "<html>\n" + "  <head>\n" + "    <title>HTML Example</title>\n" + "    <script type='text/javascript'>\n"
            + "      function foo(bar, baz) {\n" + "        alert('quux');\n" + "        return bar + baz + 1;\n"
            + "      }\n" + "    </script>\n" + "    <style type='text/css'>\n" + "      div.border {\n"
            + "        border: 1px solid black;\n" + "        padding: 3px;\n" + "      }\n" + "      #foo code {\n"
            + "        font-family: courier, monospace;\n" + "        font-size: 80%;\n" + "        color: #448888;\n"
            + "      }\n" + "    </style>\n" + "  </head>\n" + "  <body>\n" + "    <p>Hello</p>\n" + "  </body>\n"
            + "</html>";

      templates.put(MimeType.TEXT_HTML, content);
   }

   private static void addGroovyContent()
   {
      String content =
         "// simple groovy script\n" + "import javax.ws.rs.Path\n" + "import javax.ws.rs.GET\n"
            + "import javax.ws.rs.PathParam\n\n" + "@Path(\"/my-service\")\n" + "public class HelloWorld {\n"
            + "  @GET\n" + "  @Path(\"helloworld/{name}\")\n"
            + "  public String hello(@PathParam(\"name\") String name) {\n" + "    return \"Hello \" + name\n"
            + "  }\n" + "}\n";

      templates.put(MimeType.GROOVY_SERVICE, content);
   }

   private static void addEmptyTXT()
   {
      templates.put(MimeType.TEXT_PLAIN, "");
   }

   private static void addGadgetContent()
   {
      String content =
         "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n" + "<Module>\n" + "<ModulePrefs title=\"Hello World!\" />\n"
            + "<Content type=\"html\">\n" + "<![CDATA[Hello, world!]]>" + "</Content>" + "</Module>";

      templates.put(MimeType.GOOGLE_GADGET, content);
   }

   private static void addUWAWidgetContent()
   {
      String content =
         "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n"
            + "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\" "
            + "\"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">\n"
            + "<html xmlns=\"http://www.w3.org/1999/xhtml\" \n"
            + "  xmlns:widget=\"http://www.netvibes.com/ns/\">\n"
            + "    <head>\n"
            + "    <meta name=\"author\" content=\"John Doe\" />\n"
            + "    <meta name=\"description\" content=\"A descriptive description\" />\n"
            + "    <meta name=\"apiVersion\" content=\"1.0\" />\n"
            + "    <meta name=\"autoRefresh\" content=\"20\" />\n"
            + "    <meta name=\"debugMode\" content=\"true\" />\n"
            + "    <link rel=\"stylesheet\" type=\"text/css\"\n"
            + "       href=\"http://www.netvibes.com/themes/uwa/style.css\" />\n"
            + "    <script type=\"text/javascript\"\n"
            + "       src=\"http://www.netvibes.com/js/UWA/load.js.php?env=Standalone\"></script>\n"
            + "    <title>Title of the Widget</title>\n"
            + "    <link rel=\"icon\" type=\"image/png\"\n"
            + "      href=\"http://www.example.com/favicon.ico\" />\n"
            + "<!-- Add your UWA preferences as needed -->\n"
            + "    <widget:preferences>\n"
            + "    </widget:preferences>\n"
            + "    <style type=\"text/css\">\n"
            + "        /* Add your CSS rules */ \n"
            + "    </style>\n\n"
            + "    <script type=\"text/javascript\">\n"
            + "// this is just some sample code\n"
            + "// you should delete it all to place your own code instead\n\n"
            + "      // this is how you would declare a global JS object\n"
            + "     var YourWidgetName = {};\n\n"
            + "      // this is how you would declare a global JS variable\n"
            + "     YourWidgetName.yourVariable = \"My value\";\n\n"
            + "      // this is how you would declare a global 'display()' function\n"
            + "     YourWidgetName.display = function(argument) {\n"
            + "        // display code\n"
            + "     }\n\n"
            + "      // widget.onLoad is the first method called,\n"
            + "      // nothing can be done without it,\n"
            + "      // the rest of the code must be triggered from here - not <body onload=\"\">.\n"
            + "      widget.onLoad = function() {\n"
            + "        // sample Ajax request for a feed, with 'YourWidgetName.display()' used as the callback method\n"
            + "        UWA.Data.getFeed(widget.getValue('url'), YourWidgetName.display);\n" + "      }\n"
            + "    </script>\n" + "  </head>\n" + "  <body>\n" + "    <p>Hello world!</p>\n" + "  </body>\n"
            + "</html>\n";
      templates.put(MimeType.UWA_WIDGET, content);
   }

}

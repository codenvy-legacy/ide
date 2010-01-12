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
package org.exoplatform.ideall.client.model.template;

import java.util.HashMap;

import org.exoplatform.gwt.commons.rest.MimeType;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version @version $Id: $
 */

public class FileTemplates
{
   
   private static final HashMap<String, String> templates = new HashMap<String, String>();

   static {
      addXMLFileContent();
      //addHTMLContent();
      addEmptyHTML();
      addGroovyContent();
      addEmptyTXT();
      //addTXTContent();
      addGadgetContent();
   }
   
   public static String getTemplateFor(String mimeType) {
      String content = templates.get(mimeType);
      if (content == null) {
         content = "";
      }
      
      return content;
   }
   
   private static void addXMLFileContent() {
      String content = "<?xml version='1.0' encoding='UTF-8'?>\n";
      
      templates.put(MimeType.TEXT_XML, content);
   }
   
   private static void addEmptyHTML() {
      String content = "" +
      		"<html>\r\n" +
      		"  <head>\r\n" +
      		"    <title></title>\r\n" +
      		"  </head>\r\n" +
      		"  <body>\r\n" +
      		"  </body>\r\n" +
      		"</html>";
      
      templates.put(MimeType.TEXT_HTML, content);
   }
   
   private static void addHTMLContent() {
      String content = "<html>\n" + "  <head>\n" + "    <title>HTML Example</title>\n" + "    <script type='text/javascript'>\n"
      + "      function foo(bar, baz) {\n" + "        alert('quux');\n" + "        return bar + baz + 1;\n"
      + "      }\n" + "    </script>\n" + "    <style type='text/css'>\n" + "      div.border {\n"
      + "        border: 1px solid black;\n" + "        padding: 3px;\n" + "      }\n" + "      #foo code {\n"
      + "        font-family: courier, monospace;\n" + "        font-size: 80%;\n" + "        color: #448888;\n"
      + "      }\n" + "    </style>\n" + "  </head>\n" + "  <body>\n" + "    <p>Hello</p>\n" + "  </body>\n"
      + "</html>";
      
      templates.put(MimeType.TEXT_HTML, content);
   }
   
   private static void addGroovyContent() {
      String content = "// simple groovy script\n" + "import javax.ws.rs.Path\n" + "import javax.ws.rs.GET\n"
      + "import javax.ws.rs.PathParam\n\n" + "@Path(\"/\")\n" + "public class HelloWorld {\n" + "  @GET\n"
      + "  @Path(\"helloworld/{name}\")\n" + "  public String hello(@PathParam(\"name\") String name) {\n"
      + "    return \"Hello \" + name\n" + "  }\n" + "}\n";
      
      templates.put(MimeType.SCRIPT_GROOVY, content);
   }
   
   private static void addEmptyTXT() {
      templates.put(MimeType.TEXT_PLAIN, "");      
   }
   
   private static void addGadgetContent() {
      String content = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n" + "<Module>\n"
      + "<ModulePrefs title=\"Hello World!\" />\n" + "<Content type=\"html\">\n" + "<![CDATA[\n" + "Hello, world!\n"
      + "]]>" + "</Content>" + "</Module>";

      templates.put(MimeType.GOOGLE_GADGET, content);      
   }

}

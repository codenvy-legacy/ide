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
package org.exoplatform.ide.operation.file;

import static org.junit.Assert.assertTrue;

import org.exoplatform.ide.BaseTest;
import org.junit.Test;

/**
 * Created by The eXo Platform SAS.
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id:
 *
 */
public class PreviewHtmlFile extends BaseTest
{
   
   /**
    * IDE-65: Preview HTML File 
    * @throws Exception
    */
   @Test
   public void previewHtmlFile() throws Exception
   {
      final String text1 = "<html>\n"
         +"<head>\n"
         +"<title>HTML Example</title>\n"
         +"<script type='text/javascript'>\n"
         +"function foo(bar, baz) {\n"
         +"alert('quux');\n"
         +"return bar + baz + 1;\n"
         +"}\n"
         +"</script>\n"
         +"<style type='text/css'>\n"
         +"div.border {\n"
         +"border: 1px solid black;\n"
         +"padding: 3px;\n"
         +"}\n"
         +"#foo code {\n"
         +"font-family: courier, monospace;\n"
         +"font-size: 80%;\n"
         +"color: #448888;\n"
         +"}\n"
         +"</style>\n"
         +"</head>\n"
         +"<body>\n"
         +"<p>Hello</p>\n"
         +"</body>\n"
         +"</html>\n";
      
      final String text2 = "<html>\n"
         +"<head>\n"
         +"<title>HTML Example</title>\n"
         +"<script type='text/javascript'>\n"
         +"function foo(bar, baz) {\n"
         +"alert('quux');\n"
         +"return bar + baz + 1;\n"
         +"}\n"
         +"</script>\n"
         +"<style type='text/css'>\n"
         +"div.border {\n"
         +"border: 1px solid black;\n"
         +"padding: 3px;\n"
         +"}\n"
         +"#foo code {\n"
         +"font-family: courier, monospace;\n"
         +"font-size: 80%;\n"
         +"color: #448888;\n"
         +"}\n"
         +"</style>\n"
         +"</head>\n"
         +"<body>\n"
         +"<p><b><i>Changed Content.</i></b></p>\n"
         +"<img src=\"http://www.google.com.ua/intl/en_com/images/logo_plain.png\"></img>\n"
         +"</body>\n"
         +"</html>";
      
      Thread.sleep(1000);
      
      openNewFileFromToolbar("HTML File");
      Thread.sleep(1000);
      
      //delete default content
      deletePreviousContentByDeleteButton(75);
      Thread.sleep(100);
      
      typeText(text1);
      
      Thread.sleep(5000);
      
      //check is Show Preview button disabled
      assertTrue(selenium.isElementPresent("//div[@title='Show Preview']/div[@elementenabled='false']"));
      
      //open menu Run
      selenium.mouseDownAt("//td[@class='exo-menuBarItem' and @menubartitle='Run']", "");
      Thread.sleep(1000);
      
      //check is Show Preview disabled
      assertTrue(selenium.isElementPresent(
         "//td[@class='exo-popupMenuTitleFieldDisabled']/nobr[text()='Show Preview']"));
      
      deletePreviousContentByBackspaceButton(500);
      
      Thread.sleep(500);
      
      typeText(text2);
      
      saveAsByTopMenu("Test.html");
      
      Thread.sleep(1000);
      
      //check is Show Preview button enabled
      assertTrue(selenium.isElementPresent("//div[@title='Show Preview']/div[@elementenabled='true']"));
      
      //open menu Run
      selenium.mouseDownAt("//td[@class='exo-menuBarItem' and @menubartitle='Run']", "");
      Thread.sleep(1000);
      
      //check is Show Preview enabled
      assertTrue(selenium.isElementPresent(
         "//td[@class='exo-popupMenuTitleField']/nobr[text()='Show Preview']"));
      
      selenium.mouseDownAt("//td[@class='exo-popupMenuTitleField']/nobr[text()='Show Preview']", "");
      Thread.sleep(3000);
      
      //is Preview Tab present
      assertTrue(selenium.isElementPresent("scLocator=//TabSet[ID=\"ideOperationFormTabSet\"]/tab[ID=Preview]/"));
      
      selenium.selectFrame("//iframe[@src='http://127.0.0.1:8888/rest/private/jcr/repository/dev-monit/Test.html']");
      
      assertTrue(selenium.isElementPresent("//p/b/i[text()='Changed Content.']"));
      
      assertTrue(selenium.isElementPresent("//img[@src='http://www.google.com.ua/intl/en_com/images/logo_plain.png']"));
      
      selectMainFrame();
      
   }
   
   private void deletePreviousContentByDeleteButton(int numberOfSymbols)
   {
      for (int i = 0; i < numberOfSymbols; i++)
      {
         selenium.keyPress("//body[@class='editbox']/", "\\46");
      }
   }
   
   private void deletePreviousContentByBackspaceButton(int numberOfSymbols)
   {
      for (int i = 0; i < numberOfSymbols; i++)
      {
         selenium.keyPress("//body[@class='editbox']/", "\\8");
      }
   }

}

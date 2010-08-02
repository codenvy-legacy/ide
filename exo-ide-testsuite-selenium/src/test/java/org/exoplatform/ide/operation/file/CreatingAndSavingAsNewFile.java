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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.exoplatform.ide.BaseTest;
import org.junit.Test;

/**
 * Created by The eXo Platform SAS.
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id:
 *
 */
public class CreatingAndSavingAsNewFile extends BaseTest
{
   @Test
   public void testCreatingAndSavingAsNewFiles() throws Exception
   {
      
      createTestFolder();
      
      testFileSaveAs("REST Service", "groovy", "TestGroovyFile.groovy");
//      assertTrue(selenium.isElementPresent("scLocator=//TreeGrid[ID=\"ideItemTreeGrid\"]/body/row["
//            + "name=TestGroovyFile.groovy]/col[fieldName=name||0]"));
      
      testFileSaveAs("Text File", "txt", "TestTextFile.txt");
//      assertTrue(selenium.isElementPresent("scLocator=//TreeGrid[ID=\"ideItemTreeGrid\"]/body/row["
//            + "name=TestTextFile.txt]/col[fieldName=name||0]"));
      
      testFileSaveAs("XML File", "xml", "TestXmlFile.xml");
//      assertTrue(selenium.isElementPresent("scLocator=//TreeGrid[ID=\"ideItemTreeGrid\"]/body/row["
//         + "name=TestXmlFile.xml]/col[fieldName=name||0]"));
      
      testFileSaveAs("HTML File", "html", "TestHtmlFile.html");
//      assertTrue(selenium.isElementPresent("scLocator=//TreeGrid[ID=\"ideItemTreeGrid\"]/body/row["
//         + "TestHtmlFile.html]/col[fieldName=name||0]"));
      
      testFileSaveAs("JavaScript File", "js", "TestJavascriptFile.js");
//      assertTrue(selenium.isElementPresent("scLocator=//TreeGrid[ID=\"ideItemTreeGrid\"]/body/row["
//         + "TestJavascriptFile.js]/col[fieldName=name||0]"));
      
      testFileSaveAs("CSS file", "css", "TestCssFile.css");
//      assertTrue(selenium.isElementPresent("scLocator=//TreeGrid[ID=\"ideItemTreeGrid\"]/body/row["
//         + "TestCssFile.css]/col[fieldName=name||0]"));
      
      testFileSaveAs("Google Gadget", "xml", "TestGoogleGadget.xml");
//      assertTrue(selenium.isElementPresent("scLocator=//TreeGrid[ID=\"ideItemTreeGrid\"]/body/row["
//         + "TestGoogleGadget.xml]/col[fieldName=name||0]"));
      
      testFileSaveAs("Netvibes Widget", "html", "TestNetvibesWidget.html");
//      assertTrue(selenium.isElementPresent("scLocator=//TreeGrid[ID=\"ideItemTreeGrid\"]/body/row["
//         + "TestNetvibesWidget.html]/col[fieldName=name||0]"));
      
      testFileSaveAs("Groovy Template file", "gtmpl", "TestGroovyTemplate.gtmpl");
//      assertTrue(selenium.isElementPresent("scLocator=//TreeGrid[ID=\"ideItemTreeGrid\"]/body/row["
//         + "TestGroovyTemplate.gtmpl]/col[fieldName=name||0]"));
      
      testFileSaveAs("Groovy Script", "groovy", "TestGroovyScript.groovy");
//      assertTrue(selenium.isElementPresent("scLocator=//TreeGrid[ID=\"ideItemTreeGrid\"]/body/row["
//         + "TestGroovyScript.groovy]/col[fieldName=name||0]"));
      
      testFilesCreatedOnServer();
      
      deleteTestFolder();
      
   }
   
   private void createTestFolder() throws InterruptedException
   {
      selenium.click("scLocator=//TreeGrid[ID=\"ideItemTreeGrid\"]/body/row[name=dev-monit||0]/col[1]");
      selenium.mouseDownAt("//div[@title='New']//img", "");
      selenium.mouseUpAt("//div[@title='New']//img", "");
      selenium.mouseDownAt("//td[@class=\"exo-popupMenuTitleField\"]//nobr[contains(text(), \"Folder\")]", "");
      assertTrue(selenium.isTextPresent("Name of new folder:"));
      assertTrue(selenium.isElementPresent("scLocator=//Window[ID=\"ideCreateFolderForm\"]"));
      assertTrue(selenium.isElementPresent("scLocator=//Window[ID=\"ideCreateFolderForm\"]//input"));
      assertTrue(selenium.isElementPresent("scLocator=//IButton[ID=\"ideCreateFolderFormCreateButton\"]"));
      assertTrue(selenium.isElementPresent("scLocator=//IButton[ID=\"ideCreateFolderFormCancelButton\"]"));
      selenium
         .click("scLocator=//DynamicForm[ID=\"ideCreateFolderFormDynamicForm\"]/item[name=ideCreateFolderFormNameField]/element");
      selenium
         .type(
            "scLocator=//DynamicForm[ID=\"ideCreateFolderFormDynamicForm\"]/item[name=ideCreateFolderFormNameField]/element",
            "");
      selenium
         .type(
            "scLocator=//DynamicForm[ID=\"ideCreateFolderFormDynamicForm\"]/item[name=ideCreateFolderFormNameField]/element",
            "Test");
      selenium
         .keyPress(
            "scLocator=//DynamicForm[ID=\"ideCreateFolderFormDynamicForm\"]/item[name=ideCreateFolderFormNameField]/element",
            "\\13");
      Thread.sleep(1000);
      assertFalse(selenium.isElementPresent("scLocator=//Window[ID=\"ideCreateFolderForm\"]"));

      Thread.sleep(1000);
      
      assertTrue(selenium.isTextPresent("Test"));
   }
   
   private void deleteTestFolder() throws InterruptedException
   {
      selenium.open("/org.exoplatform.ide.IDEApplication/IDEApplication.html?gwt.codesvr=127.0.0.1:9997");
      selenium.waitForPageToLoad("10000");
      
      selenium.click("scLocator=//TreeGrid[ID=\"ideItemTreeGrid\"]/body/row[name=Test]/col[1]");
      selenium.mouseDownAt("//div[@title='Delete Item(s)...']//img", "");
      selenium.mouseUpAt("//div[@title='Delete Item(s)...']//img", "");
      
      Thread.sleep(1000);
      assertTrue(selenium.isElementPresent("scLocator=//Window[ID=\"ideDeleteItemForm\"]/"));
      assertTrue(selenium.isElementPresent("scLocator=//IButton[ID=\"ideDeleteItemFormOkButton\"]/"));
      assertTrue(selenium.isElementPresent("scLocator=//IButton[ID=\"ideDeleteItemFormCancelButton\"]/"));
      selenium.click("scLocator=//IButton[ID=\"ideDeleteItemFormOkButton\"]/");
      Thread.sleep(1000);
      assertFalse(selenium.isElementPresent("scLocator=//TreeGrid[ID=\"ideItemTreeGrid\"]/body/row[name=Test]/col[1]"));
   }
   
   private void testFileSaveAs(String menuTitle, String fileExtention, String fileName) throws InterruptedException
   {
      selenium.click("scLocator=//TreeGrid[ID=\"ideItemTreeGrid\"]/body/row[name=Test]/col[1]");
      selenium.mouseDownAt("//div[@title='New']//img", "");
      selenium.mouseUpAt("//div[@title='New']//img", "");
      
      Thread.sleep(1000);
      
      selenium.mouseDownAt("//td[@class=\"exo-popupMenuTitleField\"]//nobr[contains(text(), \""
         + menuTitle + "\")]", "");
      
      Thread.sleep(1000);
      
      assertTrue(selenium.isTextPresent("Untitled file." + fileExtention));
      selenium.mouseDownAt("//div[@title='New']//img", "");
      selenium.mouseUpAt("//div[@title='New']//img", "");
      selenium.mouseDownAt("//td[@class=\"exo-popupMenuTitleField\"]//nobr[contains(text(), \""
         + menuTitle + "\")]", "");
      
      Thread.sleep(1000);
      
      assertTrue(selenium.isTextPresent("Untitled file 1." + fileExtention));
      selenium.mouseDownAt("//div[@title='Save As...']//img", "");
      selenium.mouseUpAt("//div[@title='Save As...']//img", "");
      assertTrue(selenium.isElementPresent("scLocator=//Window[ID=\"ideAskForValueDialog\"]"));
      assertTrue(selenium.isTextPresent("Save file as"));
      assertTrue(selenium.isElementPresent("scLocator=//Window[ID=\"ideAskForValueDialog\"]/item[0]" 
         + "[Class=\"DynamicForm\"]/item[name=ideAskForValueDialogValueField" 
         + "||title=ideAskForValueDialogValueField||Class=TextItem]/element"));
      assertTrue(selenium.isElementPresent("scLocator=//IButton[ID=\"ideAskForValueDialogOkButton\"]/"));
      assertTrue(selenium.isElementPresent("scLocator=//IButton[ID=\"ideAskForValueDialogCancelButton\"]/"));
      selenium.click("scLocator=//Window[ID=\"ideAskForValueDialog\"]/item[0]"
         + "[Class=\"DynamicForm\"]/item[name=ideAskForValueDialogValueField" 
         + "||title=ideAskForValueDialogValueField||Class=TextItem]/element");
      selenium.type("scLocator=//Window[ID=\"ideAskForValueDialog\"]/item[0]" 
         + "[Class=\"DynamicForm\"]/item[name=ideAskForValueDialogValueField" 
         + "||title=ideAskForValueDialogValueField||Class=TextItem]/element", "");
      selenium.type("scLocator=//Window[ID=\"ideAskForValueDialog\"]/item[0][Class=\"DynamicForm\"]"
         + "/item[name=ideAskForValueDialogValueField||title=ideAskForValueDialogValueField" 
         + "||Class=TextItem]/element", fileName);
      selenium.click("scLocator=//IButton[ID=\"ideAskForValueDialogOkButton\"]/");
      
      Thread.sleep(1000);
      
      selenium.click("scLocator=//TabSet[ID=\"ideEditorFormTabSet\"]/tab[index=1]/icon");
      
      Thread.sleep(1000);
      
      selenium.click("scLocator=//TabSet[ID=\"ideEditorFormTabSet\"]/tab[index=0]/icon");
      assertTrue(selenium.isElementPresent("scLocator=//Dialog[ID=\"isc_globalWarn\"]/"));
      assertTrue(selenium.isElementPresent("scLocator=//Dialog[ID=\"isc_globalWarn\"]/yesButton/"));
      assertTrue(selenium.isElementPresent("scLocator=//Dialog[ID=\"isc_globalWarn\"]/noButton/"));
      selenium.click("scLocator=//Dialog[ID=\"isc_globalWarn\"]/noButton/");
      
      Thread.sleep(1000);
      
      assertTrue(selenium.isElementPresent("scLocator=//TreeGrid[ID=\"ideItemTreeGrid\"]/body/row["
         + fileName + "]/col[fieldName=name||0]"));
   }
   
   private void testFilesCreatedOnServer()
   {
      String url = "/rest/private/jcr/repository/dev-monit/Test/";
      selenium.open(url);
      selenium.waitForPageToLoad("10000");
      assertEquals("WEBDAV Browser", selenium.getTitle());
      assertTrue(selenium.isElementPresent("link=TestGroovyFile.groovy"));
      assertTrue(selenium.isElementPresent("link=TestTextFile.txt"));
      assertTrue(selenium.isElementPresent("link=TestXmlFile.xml"));
      assertTrue(selenium.isElementPresent("link=TestHtmlFile.html"));
      assertTrue(selenium.isElementPresent("link=TestJavascriptFile.js"));
      assertTrue(selenium.isElementPresent("link=TestCssFile.css"));
      assertTrue(selenium.isElementPresent("link=TestGoogleGadget.xml"));
      assertTrue(selenium.isElementPresent("link=TestNetvibesWidget.html"));
      assertTrue(selenium.isElementPresent("link=TestGroovyTemplate.gtmpl"));
      assertTrue(selenium.isElementPresent("link=TestGroovyScript.groovy"));
   }

}

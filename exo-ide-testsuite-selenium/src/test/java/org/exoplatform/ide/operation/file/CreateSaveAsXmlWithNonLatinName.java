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

import static org.junit.Assert.*;

import org.exoplatform.ide.BaseTest;
import org.junit.Test;

/**
 * Created by The eXo Platform SAS.
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id:
 *
 */
public class CreateSaveAsXmlWithNonLatinName extends BaseTest
{
   
   @Test
   public void testCreateAndSaveAsXmlWithNonLatinName() throws Exception
   {
      //test save as file
      Thread.sleep(10000);
      selenium.click("scLocator=//TreeGrid[ID=\"ideItemTreeGrid\"]/body/row[name=dev-monit||0]/col[1]");
      selenium.mouseDownAt("//div[@title='New']//img", "");
      selenium.mouseUpAt("//div[@title='New']//img", "");
      selenium.mouseDownAt("//td[@class=\"exo-popupMenuTitleField\"]//nobr[contains(text(), \"XML File\")]", "");
      assertTrue(selenium.isTextPresent("Untitled file.xml"));
      assertTrue(selenium.isElementPresent("//div[@title='Save']/div[@elementenabled='false']"));
      assertTrue(selenium.isElementPresent("//div[@title='Save As...']/div[@elementenabled='true']"));
      Thread.sleep(500);
      selenium.keyDown("//body[@class='editbox']/", "\\35");
      selenium.keyDown("//body[@class='editbox']/", "\\13");
      selenium.keyUp("//body[@class='editbox']/", "\\13");
      Thread.sleep(100);
      selenium.typeKeys("//body[@class='editbox']/", "<test>");
      selenium.keyDown("//body[@class='editbox']/", "\\13");
      selenium.keyUp("//body[@class='editbox']/", "\\13");
      selenium.typeKeys("//body[@class='editbox']/", "</test>");
      Thread.sleep(1000);
      saveAsUsingToolbarButton("Тестовый Файл.xml");
      Thread.sleep(1000);
      assertTrue(selenium.isElementPresent("scLocator=//TabSet[ID=\"ideEditorFormTabSet\"]/tab[title=Тестовый Файл.xml||index=0]"));
      
      //check file properties
      showAndCheckProperties("54", "text/xml", "Тестовый Файл.xml");
      
      closeTab("0");
      
      //check file on server
      checkFileOnWebDav();
      
      Thread.sleep(1000);
      
      assertTrue(selenium.isElementPresent("scLocator=//TreeGrid[ID=\"ideItemTreeGrid\"]/body/row[name=Тестовый Файл.xml]/col[0]"));
      
      openFileFromNavigationTreeWithCodeEditor("Тестовый Файл.xml");
      
      Thread.sleep(1000);
      assertTrue(selenium.isElementPresent("scLocator=//TabSet[ID=\"ideEditorFormTabSet\"]/tab[index=0]/"));
      
      showAndCheckProperties("54", "text/xml", "Тестовый Файл.xml");
      Thread.sleep(1000);
      //change file content
      selenium.keyDown("//body[@class='editbox']/", "\\35");
      selenium.keyDown("//body[@class='editbox']/", "\\13");
      selenium.keyUp("//body[@class='editbox']/", "\\13");
      for (int i = 0; i < 15; i++)
      {
         selenium.keyPress("//body[@class='editbox']/", "\\46");
      }
      Thread.sleep(100);
      selenium.typeKeys("//body[@class='editbox']/", "<settings/>");
      Thread.sleep(1000);
      
      //save as file
      saveAsUsingToolbarButton("Новий тестовий файл.xml");
      Thread.sleep(1000);
      assertTrue(selenium.isElementPresent("scLocator=//TabSet[ID=\"ideEditorFormTabSet\"]/tab[title=Новий тестовий файл.xml]"));
      
      showAndCheckProperties("51", "text/xml", "Новий тестовий файл.xml");
      
      saveCurrentFile();
      
      closeTab("0");
      
      Thread.sleep(500);
      
      checkTwoFilesOnWebDav();

      
      assertTrue(selenium.isElementPresent("scLocator=//TreeGrid[ID=\"ideItemTreeGrid\"]/body/row[name=Тестовый Файл.xml]/col[0]"));
      assertTrue(selenium.isElementPresent("scLocator=//TreeGrid[ID=\"ideItemTreeGrid\"]/body/row[name=Новий тестовий файл.xml]/col[0]"));
      
      selenium.click("scLocator=//TreeGrid[ID=\"ideItemTreeGrid\"]/body/row[name=Тестовый Файл.xml]/col[1]");
      
      selenium.mouseDownAt("//div[@title='Delete Item(s)...']//img", "");
      selenium.mouseUpAt("//div[@title='Delete Item(s)...']//img", "");
      Thread.sleep(1000);
      selenium.click("scLocator=//IButton[ID=\"ideDeleteItemFormOkButton\"]/");
      Thread.sleep(2000);

      selenium.click("scLocator=//TreeGrid[ID=\"ideItemTreeGrid\"]/body/row[name=Новий тестовий файл.xml]/col[1]");

      selenium.mouseDownAt("//div[@title='Delete Item(s)...']//img", "");
      selenium.mouseUpAt("//div[@title='Delete Item(s)...']//img", "");
      Thread.sleep(1000);
      selenium.click("scLocator=//IButton[ID=\"ideDeleteItemFormOkButton\"]/");
      Thread.sleep(2000);
      
      assertFalse(selenium.isElementPresent("scLocator=//TreeGrid[ID=\"ideItemTreeGrid\"]/body/row[name=Тестовый Файл.xml]/col[1]"));
      assertFalse(selenium.isElementPresent("scLocator=//TreeGrid[ID=\"ideItemTreeGrid\"]/body/row[name=Новий тестовий файл.xml]/col[1]"));
   }
   
   private void checkFileOnWebDav() throws Exception
   {
      selenium.open("http://127.0.0.1:8888/rest/private/jcr/repository/dev-monit/");
      selenium.waitForPageToLoad("10000");
      Thread.sleep(1000);
      
      assertTrue(selenium.isElementPresent("link=Тестовый Файл.xml"));
      selenium.click("link=Тестовый Файл.xml");
      Thread.sleep(1000);
      assertTrue(selenium.isElementPresent("//test"));
      Thread.sleep(5000);
      selenium.goBack();
      
      selenium.waitForPageToLoad("10000");
      
      Thread.sleep(1000);
      
      selenium.goBack();
      selenium.waitForPageToLoad("20000");
   }
   
   private void checkTwoFilesOnWebDav() throws Exception
   {
      selenium.openWindow("http://127.0.0.1:8888/rest/private/jcr/repository/dev-monit/", "WEBDAV Browser");
      selenium.waitForPopUp("WEBDAV Browser", "10000");
      selenium.selectPopUp("WEBDAV Browser");
      
      assertTrue(selenium.isElementPresent("link=Тестовый Файл.xml"));
      assertTrue(selenium.isElementPresent("link=Новий тестовий файл.xml"));
      selenium.click("link=Тестовый Файл.xml");
      Thread.sleep(1000);
      assertTrue(selenium.isElementPresent("//test"));
      Thread.sleep(5000);
      selenium.goBack();
      
      Thread.sleep(1000);
      
      selenium.click("link=Новий тестовий файл.xml");
      Thread.sleep(1000);
      assertTrue(selenium.isElementPresent("//settings"));
      
      selenium.goBack();
      
      Thread.sleep(1000);
      
      selenium.getEval("selenium.browserbot.getCurrentWindow().close()");
      selenium.selectWindow("IDEall");
   }
     
   private void showAndCheckProperties(String contentLength, String contentType, String displayName) throws Exception
   {
      selenium.mouseDownAt("//div[@title='Show Properties']//img", "");
      selenium.mouseUpAt("//div[@title='Show Properties']//img", "");
      assertTrue(selenium.isElementPresent("scLocator=//TabSet[ID=\"ideOperationFormTabSet\"]/tab[ID=Properties]/"));
      assertTrue(selenium.isElementPresent("scLocator=//DynamicForm[ID=\"ideDynamicPropertiesForm\"]"));
      assertEquals("Content Length :", selenium.getText("scLocator=//DynamicForm[ID=\"ideDynamicPropertiesForm\"]/item[name=idePropertiesTextContentLength]/title"));
      assertEquals(contentLength, selenium.getText("scLocator=//DynamicForm[ID=\"ideDynamicPropertiesForm\"]/item[name=idePropertiesTextContentLength]/textbox"));
      assertEquals("Content Node Type :", selenium.getText("scLocator=//DynamicForm[ID=\"ideDynamicPropertiesForm\"]/item[name=idePropertiesTextContentNodeType]/title"));
      assertEquals("nt:resource", selenium.getText("scLocator=//DynamicForm[ID=\"ideDynamicPropertiesForm\"]/item[name=idePropertiesTextContentNodeType]/textbox"));
      assertEquals("Content Type :", selenium.getText("scLocator=//DynamicForm[ID=\"ideDynamicPropertiesForm\"]/item[name=idePropertiesTextContentType]/title"));
      assertEquals(contentType, selenium.getText("scLocator=//DynamicForm[ID=\"ideDynamicPropertiesForm\"]/item[name=idePropertiesTextContentType]/textbox"));
      assertEquals("Creation Date :", selenium.getText("scLocator=//DynamicForm[ID=\"ideDynamicPropertiesForm\"]/item[name=idePropertiesTextCreationDate]/title"));
      assertEquals("Display Name :", selenium.getText("scLocator=//DynamicForm[ID=\"ideDynamicPropertiesForm\"]/item[name=idePropertiesTextDisplayName]/title"));
      assertEquals(displayName, selenium.getText("scLocator=//DynamicForm[ID=\"ideDynamicPropertiesForm\"]/item[name=idePropertiesTextDisplayName]/textbox"));
      assertEquals("File Node Type :", selenium.getText("scLocator=//DynamicForm[ID=\"ideDynamicPropertiesForm\"]/item[name=idePropertiesTextFileNodeType]/title"));
      assertEquals("nt:file", selenium.getText("scLocator=//DynamicForm[ID=\"ideDynamicPropertiesForm\"]/item[name=idePropertiesTextFileNodeType]/textbox"));
      assertEquals("Last Modified :", selenium.getText("scLocator=//DynamicForm[ID=\"ideDynamicPropertiesForm\"]/item[name=idePropertiesTextLastModified]/title"));
   }
   
}

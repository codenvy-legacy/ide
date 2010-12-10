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
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.net.URLEncoder;

import org.exoplatform.common.http.client.ModuleException;
import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.MenuCommands;
import org.exoplatform.ide.TestConstants;
import org.exoplatform.ide.ToolbarCommands;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.junit.AfterClass;
import org.junit.Test;

/**
 * IDE-47: Creating and "Saving As" new XML file with non-latin name. 
 * 
 * Created by The eXo Platform SAS.
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id:
 *
 */
public class CreateSaveAsXmlWithNonLatinNameTest extends BaseTest
{

   /**
    * 
    */
   private static final String XML_FILE = "Ð¢ÐµÑ�Ñ‚Ð¾Ð²Ñ‹Ð¹ Ð¤Ð°Ð¹Ð».xml";

   /**
    * 
    */
   private static final String NEW_XML_FILE = "Ð�Ð¾Ð²Ð¸Ð¹ Ñ‚ÐµÑ�Ñ‚Ð¾Ð²Ð¸Ð¹ Ñ„Ð°Ð¹Ð».xml";

   /**
    * 
    */
   private static final String XML_CONTENT = "<?xml version='1.0' encoding='UTF-8'?>\n"
                                             + "<test>test</test>";
   
   /**
    * 
    */
   private static final String XML_CONTENT_2 = "<?xml version='1.0' encoding='UTF-8'?>\n"
                                             + "<settings>test</settings>";
   
   private final static String URL = BASE_URL + REST_CONTEXT + "/" + WEBDAV_CONTEXT + "/" + REPO_NAME + "/" + WS_NAME + "/";

   //IDE-47: Creating and "Saving As" new XML file with non-latin name 
   @Test
   public void testCreateAndSaveAsXmlWithNonLatinName() throws Exception
   {

      Thread.sleep(TestConstants.SLEEP);
      selectRootOfWorkspaceTree();
      IDE.toolbar().runCommandFromNewPopupMenu(MenuCommands.New.XML_FILE);
      Thread.sleep(TestConstants.SLEEP_SHORT);
      assertTrue(selenium.isTextPresent("Untitled file.xml"));
      IDE.toolbar().checkButtonEnabled(ToolbarCommands.File.SAVE, false);
      IDE.toolbar().checkButtonEnabled(ToolbarCommands.File.SAVE_AS, true);
      Thread.sleep(TestConstants.SLEEP_SHORT);
      deleteFileContent();
      Thread.sleep(TestConstants.SLEEP_SHORT);
      typeTextIntoEditor(0, XML_CONTENT);
      Thread.sleep(TestConstants.SLEEP);
      saveAsUsingToolbarButton(XML_FILE);
      Thread.sleep(TestConstants.SLEEP);
      assertTrue(selenium.isElementPresent("scLocator=//TabSet[ID=\"ideEditorFormTabSet\"]/tab[title=" + XML_FILE
         + "||index=0]"));

      //check file properties
      showAndCheckProperties(String.valueOf(XML_CONTENT.length()+1), MimeType.TEXT_XML, XML_FILE);

      IDE.editor().closeTab(0);

      //check file on server
      checkFileOnWebDav(XML_FILE);

      Thread.sleep(TestConstants.SLEEP*3);
      assertElementPresentInWorkspaceTree(XML_FILE);

      openFileFromNavigationTreeWithCodeEditor(XML_FILE, false);

      Thread.sleep(TestConstants.SLEEP);
      assertTrue(selenium.isElementPresent("scLocator=//TabSet[ID=\"ideEditorFormTabSet\"]/tab[index=0]/"));

      showAndCheckProperties(String.valueOf(XML_CONTENT.length()+1), MimeType.TEXT_XML, XML_FILE);
      Thread.sleep(TestConstants.SLEEP);
      //change file content
      deleteFileContent();
      Thread.sleep(TestConstants.SLEEP_SHORT);
      typeTextIntoEditor(0, XML_CONTENT_2);

      //save as file
      saveAsUsingToolbarButton(NEW_XML_FILE);
      Thread.sleep(TestConstants.SLEEP);
      assertTrue(selenium.isElementPresent("scLocator=//TabSet[ID=\"ideEditorFormTabSet\"]/tab[title=" + NEW_XML_FILE
         + "]"));

      showAndCheckProperties(String.valueOf(XML_CONTENT_2.length()+1), MimeType.TEXT_XML, NEW_XML_FILE);

      saveCurrentFile();

      IDE.editor().closeTab(0);

      Thread.sleep(TestConstants.SLEEP_SHORT);

      checkTwoFilesOnWebDav(XML_FILE, NEW_XML_FILE);

      Thread.sleep(TestConstants.SLEEP);

      assertElementPresentInWorkspaceTree(XML_FILE);
      assertElementPresentInWorkspaceTree(NEW_XML_FILE);

      selectItemInWorkspaceTree(XML_FILE);
      deleteSelectedItems();
      Thread.sleep(TestConstants.SLEEP);

      selectItemInWorkspaceTree(NEW_XML_FILE);
      deleteSelectedItems();
      Thread.sleep(TestConstants.SLEEP);

      assertElementNotPresentInWorkspaceTree(XML_FILE);
      assertElementNotPresentInWorkspaceTree(NEW_XML_FILE);

   }

   private void checkFileOnWebDav(String fileName) throws Exception
   {
//      selenium.open(BASE_URL + "rest/private/"+WEBDAV_CONTEXT+"/repository/dev-monit/");
      selenium.open(URL);
      selenium.waitForPageToLoad("" + TestConstants.IDE_LOAD_PERIOD);
      Thread.sleep(TestConstants.SLEEP_SHORT);

      assertTrue(selenium.isElementPresent("link=" + fileName));
      selenium.click("link=" + fileName);
      Thread.sleep(TestConstants.SLEEP_SHORT);
      assertTrue(selenium.isElementPresent("//test"));
      Thread.sleep(TestConstants.SLEEP_SHORT);
      selenium.goBack();
      selenium.waitForPageToLoad("" + TestConstants.IDE_LOAD_PERIOD);

      Thread.sleep(TestConstants.SLEEP_SHORT);

      selenium.goBack();
      selenium.waitForPageToLoad("" + TestConstants.IDE_LOAD_PERIOD);
      refresh();
   }

   private void checkTwoFilesOnWebDav(String fileName1, String fileName2) throws Exception
   {
      selenium.openWindow(BASE_URL + "rest/private/"+WEBDAV_CONTEXT+"/repository/dev-monit/", "WEBDAV Browser");
      selenium.waitForPopUp("WEBDAV Browser", "10000");
      selenium.selectPopUp("WEBDAV Browser");

      assertTrue(selenium.isElementPresent("link=" + fileName1));
      assertTrue(selenium.isElementPresent("link=" + fileName2));
      selenium.click("link=" + fileName1);
      Thread.sleep(TestConstants.SLEEP);
      assertTrue(selenium.isElementPresent("//test"));
      Thread.sleep(TestConstants.SLEEP);
      selenium.goBack();

      Thread.sleep(TestConstants.SLEEP);

      selenium.click("link=" + fileName2);
      Thread.sleep(TestConstants.SLEEP);
      assertTrue(selenium.isElementPresent("//settings"));

      selenium.goBack();

      Thread.sleep(TestConstants.SLEEP);

      selenium.getEval("selenium.browserbot.getCurrentWindow().close()");
      selenium.selectWindow(PAGE_NAME);
   }

   private void showAndCheckProperties(String contentLength, String contentType, String displayName) throws Exception
   {
      selenium.mouseDownAt("//div[@title='Show Properties']//img", "");
      selenium.mouseUpAt("//div[@title='Show Properties']//img", "");
      assertTrue(selenium.isElementPresent("scLocator=//TabSet[ID=\"ideOperationFormTabSet\"]/tab[ID=Properties]/"));
      assertTrue(selenium.isElementPresent("scLocator=//DynamicForm[ID=\"ideDynamicPropertiesForm\"]"));
      assertEquals(
         "Content Length :",
         selenium
            .getText("scLocator=//DynamicForm[ID=\"ideDynamicPropertiesForm\"]/item[name=idePropertiesTextContentLength]/title"));
      assertEquals(
         contentLength,
         selenium
            .getText("scLocator=//DynamicForm[ID=\"ideDynamicPropertiesForm\"]/item[name=idePropertiesTextContentLength]/textbox"));
      assertEquals(
         "Content Node Type :",
         selenium
            .getText("scLocator=//DynamicForm[ID=\"ideDynamicPropertiesForm\"]/item[name=idePropertiesTextContentNodeType]/title"));
      assertEquals(
         "nt:resource",
         selenium
            .getText("scLocator=//DynamicForm[ID=\"ideDynamicPropertiesForm\"]/item[name=idePropertiesTextContentNodeType]/textbox"));
      assertEquals(
         "Content Type :",
         selenium
            .getText("scLocator=//DynamicForm[ID=\"ideDynamicPropertiesForm\"]/item[name=idePropertiesTextContentType]/title"));
      assertEquals(
         contentType,
         selenium
            .getText("scLocator=//DynamicForm[ID=\"ideDynamicPropertiesForm\"]/item[name=idePropertiesTextContentType]/textbox"));
      assertEquals(
         "Creation Date :",
         selenium
            .getText("scLocator=//DynamicForm[ID=\"ideDynamicPropertiesForm\"]/item[name=idePropertiesTextCreationDate]/title"));
      assertEquals(
         "Display Name :",
         selenium
            .getText("scLocator=//DynamicForm[ID=\"ideDynamicPropertiesForm\"]/item[name=idePropertiesTextDisplayName]/title"));
      assertEquals(
         displayName,
         selenium
            .getText("scLocator=//DynamicForm[ID=\"ideDynamicPropertiesForm\"]/item[name=idePropertiesTextDisplayName]/textbox"));
      assertEquals(
         "File Node Type :",
         selenium
            .getText("scLocator=//DynamicForm[ID=\"ideDynamicPropertiesForm\"]/item[name=idePropertiesTextFileNodeType]/title"));
      assertEquals(
         "nt:file",
         selenium
            .getText("scLocator=//DynamicForm[ID=\"ideDynamicPropertiesForm\"]/item[name=idePropertiesTextFileNodeType]/textbox"));
      assertEquals(
         "Last Modified :",
         selenium
            .getText("scLocator=//DynamicForm[ID=\"ideDynamicPropertiesForm\"]/item[name=idePropertiesTextLastModified]/title"));
   }
   
   
   @AfterClass
   public static void tearDown()
   {
      try
      {
         VirtualFileSystemUtils.delete(URL + URLEncoder.encode(XML_FILE,"UTF-8"));
         VirtualFileSystemUtils.delete(URL + URLEncoder.encode(NEW_XML_FILE,"UTF-8"));
      }
      catch (IOException e)
      {
         e.printStackTrace();
      }
      catch (ModuleException e)
      {
         e.printStackTrace();
      }
   }

}

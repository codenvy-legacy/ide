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
package org.exoplatform.ide.operation.templates;

import static org.junit.Assert.*;

import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.MenuCommands;
import org.exoplatform.ide.TestConstants;
import org.junit.AfterClass;
import org.junit.Test;

/**
 * Created by The eXo Platform SAS.
 *	
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id:   ${date} ${time}
 *
 */
public class CreateFileFromTemplateTest extends BaseTest
{
   
   private static final String FOLDER = "Testtemplate";
   
   private static final String GROOVY_REST_SERVICE = "Groovy REST Service";
   
   private static final String EMPTY_XML = "Empty XML";
   
   private static final String EMPTY_HTML = "Empty HTML";
   
   private static final String EMPTY_TEXT = "Empty TEXT";
   
   private static final String GOOGLE_GADGET = "Google Gadget";
   
   private static final String GROOVY_FILE_NAME = "Test Groovy File.groovy";
   
   private static final String XML_FILE_NAME = "Test Xml File.xml";
   
   private static final String HTML_FILE_NAME = "Test Html File.html";
   
   private static final String TEXT_FILE_NAME = "Test Text File.txt";
   
   private static final String GOOGLE_GADGET_FILE_NAME = "Test Gadget File.xml";
   
   @AfterClass
   public static void tearDown()
   {
      cleanRepository(REST_CONTEXT + "/" + WEBDAV_CONTEXT + "/" + REPO_NAME + "/" + WS_NAME + "/");
   }
   
   //IDE-76:Create File from Template
   @Test
   public void testCreateFileFromTemplate() throws Exception
   {
      // -------- 1 ----------
      Thread.sleep(TestConstants.SLEEP);
      
      //TODO*************change******change add folder for locked file
      createFolder(FOLDER);
      //*************************
      
      Thread.sleep(TestConstants.SLEEP);
      // -------- 2-4 ----------
      testTemplate(GROOVY_REST_SERVICE, GROOVY_FILE_NAME);
      
      // -------- 5 ----------
      //Repeat steps 2-4 with items "Empty XML",  "Empty HTML", "Empty TEXT", "Google Gadget" item of left panel.
      testTemplate(EMPTY_XML, XML_FILE_NAME);
      testTemplate(EMPTY_HTML, HTML_FILE_NAME);
      testTemplate(GOOGLE_GADGET, GOOGLE_GADGET_FILE_NAME);
      testTemplate(EMPTY_TEXT, TEXT_FILE_NAME);
      
      //test files created on server
      selenium.open(REST_CONTEXT + "/" + WEBDAV_CONTEXT + "/" + REPO_NAME + "/" + WS_NAME + "/" + FOLDER + "/" );
      selenium.waitForPageToLoad("10000");
      Thread.sleep(TestConstants.SLEEP);
      testFileCreatedOnServer(GROOVY_FILE_NAME);
      testFileCreatedOnServer(XML_FILE_NAME);
      testFileCreatedOnServer(HTML_FILE_NAME);
      testFileCreatedOnServer(GOOGLE_GADGET_FILE_NAME);
      testFileCreatedOnServer(TEXT_FILE_NAME);
      selenium.goBack();
      selenium.waitForPageToLoad("30000");
      Thread.sleep(TestConstants.SLEEP);
      
      // -------- 6 ----------
      //Remove created files.
    
      
      //******change******
      openOrCloseFolder(FOLDER);      
      //****************
      
      selectItemInWorkspaceTree(GROOVY_FILE_NAME);
      deleteSelectedItems();
      Thread.sleep(TestConstants.SLEEP);
      assertElementNotPresentInWorkspaceTree(GROOVY_FILE_NAME);
      
      selectItemInWorkspaceTree(XML_FILE_NAME);
      deleteSelectedItems();
      Thread.sleep(TestConstants.SLEEP);
      assertElementNotPresentInWorkspaceTree(XML_FILE_NAME);
      
      selectItemInWorkspaceTree(HTML_FILE_NAME);
      deleteSelectedItems();
      Thread.sleep(TestConstants.SLEEP);
      assertElementNotPresentInWorkspaceTree(HTML_FILE_NAME);
      
      selectItemInWorkspaceTree(GOOGLE_GADGET_FILE_NAME);
      deleteSelectedItems();
      Thread.sleep(TestConstants.SLEEP);
      assertElementNotPresentInWorkspaceTree(GOOGLE_GADGET_FILE_NAME);
      
      selectItemInWorkspaceTree(TEXT_FILE_NAME);
      deleteSelectedItems();
      Thread.sleep(TestConstants.SLEEP);
      assertElementNotPresentInWorkspaceTree(TEXT_FILE_NAME);
      
      // -------- 7 ----------
      //Click on "File->New->From Template" top menu command.
      runCommandFromMenuNewOnToolbar(MenuCommands.New.FILE_FROM_TEMPLATE);
      Thread.sleep(TestConstants.SLEEP);
      
      //we will see the "Create file" dialog window.
      assertTrue(selenium.isElementPresent("scLocator=//DynamicForm[ID=\"ideCreateFileFromTemplateFormDynamicForm\"]"));
      //click cancel button
      selenium.click("scLocator=//IButton[ID=\"ideCreateFileFromTemplateFormCancelButton\"]/icon");
      Thread.sleep(TestConstants.SLEEP);
   }
   
   private void testFileCreatedOnServer(String fileName)
   {
     
      //TODO********change****change add folder for locked file
      assertTrue(selenium.isElementPresent("//div[@id='main']/a[@href='" 
         + BASE_URL +  REST_CONTEXT + "/" + WEBDAV_CONTEXT + "/" + REPO_NAME + "/" + WS_NAME + "/" + FOLDER + "/"
         + fileName + "' and text()=' " + fileName + "']"));
      //*************
   }
   
   private void testTemplate(String templateName, String fileName) throws Exception
   {
      // ---------2--------
      //Click on "New->From Template" button.
      runCommandFromMenuNewOnToolbar(MenuCommands.New.FILE_FROM_TEMPLATE);
      Thread.sleep(TestConstants.SLEEP);
      TemplateUtils.checkCreateFileFromTemplateWindow(selenium);
      // -------3-------
      //Select "Groovy REST Service" item in the "Create file" window, 
      //change "File Name" field text on "Test Groovy File.groovy" name, click on "Create" button.
      TemplateUtils.selectItemInTemplateList(selenium, templateName);
      selenium.type("scLocator=//DynamicForm[ID=\"ideCreateFileFromTemplateFormDynamicForm\"]/item[" 
         + "name=ideCreateFileFromTemplateFormFileNameField||title=File Name]/element", fileName);
      Thread.sleep(TestConstants.SLEEP_SHORT);
      selenium.click("scLocator=//IButton[ID=\"ideCreateFileFromTemplateFormCreateButton\"]/");
      Thread.sleep(TestConstants.SLEEP);
      //new file with appropriate titles and highlighting should be opened in the Content Panel
      assertEquals(fileName + " *", getTabTitle(0));
      // --------4------------
      //Click on "File->Save File As" top menu command and save file "Test Groovy File.groovy".
      saveAsByTopMenu(fileName);
      Thread.sleep(TestConstants.SLEEP);
      //new file with appropriate name should be appeared in the root folder of  
      //"Workspace" panel in the "Gadget " window and in the root folder of  "Server" window.
      assertEquals(fileName, getTabTitle(0));
      assertElementPresentInWorkspaceTree(fileName);
      closeTab("0");
   }
}
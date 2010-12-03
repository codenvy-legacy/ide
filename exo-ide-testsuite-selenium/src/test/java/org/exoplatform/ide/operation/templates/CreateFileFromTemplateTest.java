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

import static org.exoplatform.ide.operation.templates.TemplateUtils.NAME_FIELD_LOCATOR;
import static org.exoplatform.ide.operation.templates.TemplateUtils.checkCreateButtonEnabled;
import static org.exoplatform.ide.operation.templates.TemplateUtils.checkDeleteButtonEnabled;
import static org.exoplatform.ide.operation.templates.TemplateUtils.checkNameFieldEnabled;
import static org.exoplatform.ide.operation.templates.TemplateUtils.closeCreateFromTemplateForm;
import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.UUID;

import org.exoplatform.common.http.client.HTTPResponse;
import org.exoplatform.common.http.client.ModuleException;
import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.MenuCommands;
import org.exoplatform.ide.TestConstants;
import org.exoplatform.ide.ToolbarCommands;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
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
   
   private static final String URL = BASE_URL + REST_CONTEXT + "/" + WEBDAV_CONTEXT + "/" + REPO_NAME + "/" + WS_NAME + "/";
   
   private static String FOLDER;
   
   @BeforeClass
   public static void setUp() throws Exception
   {
      FOLDER = UUID.randomUUID().toString();
      try
      {
         VirtualFileSystemUtils.mkcol(URL + FOLDER);
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
   
   @AfterClass
   public static void tearDown()
   {
      cleanRepository(REST_CONTEXT + "/" + WEBDAV_CONTEXT + "/" + REPO_NAME + "/" + WS_NAME + "/");
      try
      {
         VirtualFileSystemUtils.delete(URL + FOLDER);
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
   
   //IDE-76:Create File from Template
   @Test
   public void testCreateFileFromTemplate() throws Exception
   {
      // -------- 1 ----------
      Thread.sleep(TestConstants.SLEEP);
      IDE.toolbar().runCommand(ToolbarCommands.File.REFRESH);
      selectItemInWorkspaceTree(FOLDER);
      // -------- 2-4 ----------
      testTemplate(GROOVY_REST_SERVICE, GROOVY_FILE_NAME);
      
      // -------- 5 ----------
      //Repeat steps 2-4 with items "Empty XML",  "Empty HTML", "Empty TEXT", "Google Gadget" item of left panel.
      testTemplate(EMPTY_XML, XML_FILE_NAME);
      testTemplate(EMPTY_HTML, HTML_FILE_NAME);
      testTemplate(GOOGLE_GADGET, GOOGLE_GADGET_FILE_NAME);
      testTemplate(EMPTY_TEXT, TEXT_FILE_NAME);
   }
   
   @Test
   public void testEnablingDisablingElements() throws Exception
   {
      refresh();
      
      //---- 1 ----------
      //call create file from template form
      IDE.toolbar().runCommandFromNewPopupMenu(MenuCommands.New.FILE_FROM_TEMPLATE);
      
      checkNameFieldEnabled(selenium, false);
      checkCreateButtonEnabled(selenium, false);
      checkDeleteButtonEnabled(selenium, false);
      
      //---- 2 ----------
      //select template in list
      TemplateUtils.selectItemInTemplateList(selenium, EMPTY_HTML);
      
      checkNameFieldEnabled(selenium, true);
      checkCreateButtonEnabled(selenium, true);
      checkDeleteButtonEnabled(selenium, false);
      Thread.sleep(TestConstants.SLEEP);
      
      String text = selenium.getValue(NAME_FIELD_LOCATOR);
      assertEquals("Untitled file.html", text);
      
      //---- 3 ----------
      //deselect template
      selenium.controlKeyDown();
      TemplateUtils.selectItemInTemplateList(selenium, EMPTY_HTML);
      selenium.controlKeyUp();
      Thread.sleep(TestConstants.REDRAW_PERIOD);
      
      checkNameFieldEnabled(selenium, false);
      checkDeleteButtonEnabled(selenium, false);
      checkCreateButtonEnabled(selenium, false);
      
      //---- 4 ----------
      //select several templates
      TemplateUtils.selectItemInTemplateList(selenium, EMPTY_HTML);
      selenium.controlKeyDown();
      TemplateUtils.selectItemInTemplateList(selenium, EMPTY_TEXT);
      selenium.controlKeyUp();
      Thread.sleep(TestConstants.REDRAW_PERIOD);
      
      checkNameFieldEnabled(selenium, false);
      checkDeleteButtonEnabled(selenium, false);
      checkCreateButtonEnabled(selenium, false);
      
      //---- 5 ----------
      //select one template
      TemplateUtils.selectItemInTemplateList(selenium, EMPTY_XML);
      //remove text from name field
      selenium.type(NAME_FIELD_LOCATOR, "");
      Thread.sleep(TestConstants.REDRAW_PERIOD);
      
      checkCreateButtonEnabled(selenium, false);
      
      //---- 6 ----------
      //type some text to name field
      selenium.type(NAME_FIELD_LOCATOR, "a");
      Thread.sleep(TestConstants.REDRAW_PERIOD);
      
      checkCreateButtonEnabled(selenium, true);
      
      closeCreateFromTemplateForm(selenium);
   }
   
   private void testTemplate(String templateName, String fileName) throws Exception
   {
      // ---------2--------
      //Click on "New->From Template" button.
      IDE.toolbar().runCommandFromNewPopupMenu(MenuCommands.New.FILE_FROM_TEMPLATE);
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
      assertEquals(fileName + " *", IDE.editor().getTabTitle(0));
      // --------4------------
      //Click on "File->Save File As" top menu command and save file "Test Groovy File.groovy".
      saveAsByTopMenu(fileName);
      Thread.sleep(TestConstants.SLEEP);
      //new file with appropriate name should be appeared in the root folder of  
      //"Workspace" panel in the "Gadget " window and in the root folder of  "Server" window.
      assertEquals(fileName, IDE.editor().getTabTitle(0));
      assertElementPresentInWorkspaceTree(fileName);
      IDE.editor().closeTab(0);
      
      //check file created on server
      HTTPResponse response = VirtualFileSystemUtils.get(URL + FOLDER + "/" + fileName);
      assertEquals(200, response.getStatusCode());
   }
   
}

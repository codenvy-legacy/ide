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

import static org.exoplatform.ide.CloseFileUtils.closeUnsavedFileAndDoNotSave;
import static org.junit.Assert.*;

import org.exoplatform.common.http.client.ModuleException;
import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.MenuCommands;
import org.exoplatform.ide.TestConstants;
import org.exoplatform.ide.ToolbarCommands;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.exoplatform.ide.CloseFileUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.UUID;

/**
 * Created by The eXo Platform SAS.
 *	
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id:   ${date} ${time}
 *
 */
public class SaveFileAsTemplateTest extends BaseTest
{
   private static final String FILE_NAME = "RestServiceTemplate.groovy";

   private final static String URL = BASE_URL + REST_CONTEXT + "/" + WEBDAV_CONTEXT + "/" + REPO_NAME + "/" + WS_NAME + "/";

   private static final String REST_SERVICE_TEMPLATE_NAME = "test REST template";
   
   private static final String REST_SERVICE_TEMPLATE_DESCRIPTION = "test REST Service template description";
   
   private static final String REST_SERVICE_FILE_NAME = "TestRestServiceFile";
   
   private static final String TEXT = "// test groovy file template";
   
   private static final String NAME_FIELD_LOCATOR = "scLocator=//DynamicForm[ID=\"ideSaveAsTemplateFormDynamicForm\"]/item[" 
      + "name=ideSaveAsTemplateFormNameField]/element";
   
   private static String FOLDER_NAME;
   
   @BeforeClass
   public static void setUp()
   {
      FOLDER_NAME = UUID.randomUUID().toString();
      String filePath ="src/test/resources/org/exoplatform/ide/operation/templates/RestServiceTemplate.groovy";
      try
      {
         VirtualFileSystemUtils.mkcol(URL + FOLDER_NAME);
         VirtualFileSystemUtils.put(filePath, MimeType.GROOVY_SERVICE, URL + FOLDER_NAME + "/" + FILE_NAME);
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
      cleanRegistry();
      try
      {
         VirtualFileSystemUtils.delete(URL + FOLDER_NAME);
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

   //IDE-62:Save File as Template
   @Test
   public void testSaveFileAsTemplate() throws Exception
   {
      //-------- 1 ----------
      //open file with text
      Thread.sleep(TestConstants.SLEEP);
      runToolbarButton(ToolbarCommands.File.REFRESH);
      selectItemInWorkspaceTree(FOLDER_NAME);
      runToolbarButton(ToolbarCommands.File.REFRESH);
      openFileFromNavigationTreeWithCodeEditor(FILE_NAME, false);
      
      //--------- 2 --------
      //Click on "File->Save As Template" top menu item, 
      //set "Name" field on "test REST template", 
      //"Description" field on "test REST Service template description", and then click on "Save" button.
      runTopMenuCommand(MenuCommands.File.FILE, MenuCommands.File.SAVE_AS_TEMPLATE);
      Thread.sleep(TestConstants.SLEEP);
      // check "Save file as template" dialog window
      TemplateUtils.checkSaveAsTemplateWindow(selenium);
      
      //check save button disabled
      checkSaveButtonEnabled(false);
      
      //type some text to name field
      selenium.type(NAME_FIELD_LOCATOR, "a");
      Thread.sleep(TestConstants.REDRAW_PERIOD);
      
      //check save button enabled
      checkSaveButtonEnabled(true);
      
      //remove text from name field
      selenium.type(NAME_FIELD_LOCATOR, "");
      Thread.sleep(TestConstants.REDRAW_PERIOD);
      
      //check save button disabled
      checkSaveButtonEnabled(false);
      
      
      //set name
      selenium.type(NAME_FIELD_LOCATOR, REST_SERVICE_TEMPLATE_NAME);
      //set description
      selenium.type("scLocator=//DynamicForm[ID=\"ideSaveAsTemplateFormDynamicForm\"]/item[" 
         + "name=ideSaveAsTemplateFormDescriptionField]/element", REST_SERVICE_TEMPLATE_DESCRIPTION);
      //click save button
      selenium.click("scLocator=//IButton[ID=\"ideSaveAsTemplateFormSaveButton\"]/");
      Thread.sleep(TestConstants.SLEEP);
      //check info dialog, that template crated successfully
      assertTrue(selenium.isElementPresent("scLocator=//Dialog[ID=\"isc_globalWarn\"]/header/"));
      assertTrue(selenium.isElementPresent("scLocator=//Dialog[ID=\"isc_globalWarn\"]/okButton/"));
      assertTrue(selenium.isTextPresent("Template created successfully!"));
      //click ok button
      selenium.click("scLocator=//Dialog[ID=\"isc_globalWarn\"]/okButton/");
      
      //------------ 3 ----------
      //Click on "New->From Template" button and then click on "test groovy template" item.
      runCommandFromMenuNewOnToolbar(MenuCommands.New.FILE_FROM_TEMPLATE);
      Thread.sleep(TestConstants.SLEEP);
      
      // check create "Create file dialog window"
      TemplateUtils.checkCreateFileFromTemplateWindow(selenium);
      
      TemplateUtils.selectItemInTemplateList(selenium, REST_SERVICE_TEMPLATE_NAME);
      
      //------------ 4 ----------
      //Change "File Name.groovy" field text on "Test Groovy File.groovy" name, click on "Create" button.
      selenium.type("scLocator=//DynamicForm[ID=\"ideCreateFileFromTemplateFormDynamicForm\"]/item[" 
         + "name=ideCreateFileFromTemplateFormFileNameField||title=File Name]/element", 
         REST_SERVICE_FILE_NAME);
      Thread.sleep(TestConstants.SLEEP_SHORT);
      //click Create button
      selenium.click("scLocator=//IButton[ID=\"ideCreateFileFromTemplateFormCreateButton\"]/");
      Thread.sleep(TestConstants.SLEEP);
      //there should be new tab with title "Test Groovy File.groovy", 
      //first line "// test groovy file template" in content and with "Groovy" 
      //highlighting opened in the Content Panel.
      assertEquals(REST_SERVICE_FILE_NAME + " *", getTabTitle(1));
      assertTrue(getTextFromCodeEditor(0).startsWith(TEXT));
      
      //------------ 5 ----------
      //Close files "Test File.groovy" and "Test Groovy File.groovy".
      closeUnsavedFileAndDoNotSave(1);
      Thread.sleep(TestConstants.SLEEP_SHORT);
      CloseFileUtils.closeTab(0);
      Thread.sleep(TestConstants.SLEEP_SHORT);
   }
   
   private void checkSaveButtonEnabled(boolean enabled)
   {
      if (enabled)
      {
         assertTrue(selenium.isElementPresent("//div[@eventproxy='ideSaveAsTemplateForm']//td[@class='buttonTitle' and text()='Save']"));
         assertFalse(selenium.isElementPresent("//div[@eventproxy='ideSaveAsTemplateForm']//td[@class='buttonTitleDisabled' and text()='Save']"));
      }
      else
      {
         assertFalse(selenium.isElementPresent("//div[@eventproxy='ideSaveAsTemplateForm']//td[@class='buttonTitle' and text()='Save']"));
         assertTrue(selenium.isElementPresent("//div[@eventproxy='ideSaveAsTemplateForm']//td[@class='buttonTitleDisabled' and text()='Save']"));
      }
   }
   
}
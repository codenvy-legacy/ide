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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.MenuCommands;
import org.exoplatform.ide.ToolbarCommands;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.exoplatform.ide.core.SaveAsTemplate;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;

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
   
   private static final String FOLDER_NAME = SaveFileAsTemplateTest.class.getSimpleName();
   
   @BeforeClass
   public static void setUp()
   {
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
   }
   
   @AfterClass
   public static void tearDown()
   {
      try
      {
         VirtualFileSystemUtils.delete(URL + FOLDER_NAME);
      }
      catch (IOException e)
      {
         e.printStackTrace();
      }
   }

   //IDE-62:Save File as Template
   @Test
   public void testSaveFileAsTemplate() throws Exception
   {
      IDE.WORKSPACE.waitForItem(WS_URL);
      IDE.TOOLBAR.waitForButtonEnabled(ToolbarCommands.File.REFRESH, true);
      IDE.WORKSPACE.selectRootItem();
      IDE.TOOLBAR.runCommand(ToolbarCommands.File.REFRESH);
      IDE.WORKSPACE.waitForItem(WS_URL + FOLDER_NAME + "/");
      IDE.WORKSPACE.selectItem(WS_URL + FOLDER_NAME + "/");
      IDE.TOOLBAR.runCommand(ToolbarCommands.File.REFRESH);
      IDE.WORKSPACE.waitForItem(WS_URL + FOLDER_NAME + "/" + FILE_NAME);
      //-------- 1 ----------
      //open file with text
      IDE.NAVIGATION.openFileFromNavigationTreeWithCodeEditor(WS_URL + FOLDER_NAME + "/" + FILE_NAME, false);
      
      //--------- 2 --------
      //Click on "File->Save As Template" top menu item, 
      //set "Name" field on "test REST template", 
      //"Description" field on "test REST Service template description", and then click on "Save" button.
      IDE.MENU.runCommand(MenuCommands.File.FILE, MenuCommands.File.SAVE_AS_TEMPLATE);
      IDE.SAVE_AS_TEMPLATE.waitForDialog();
      // check "Save file as template" dialog window
      IDE.SAVE_AS_TEMPLATE.checkSaveAsTemplateWindow();
      
      //check save button disabled
      IDE.SAVE_AS_TEMPLATE.checkButtonState(SaveAsTemplate.SAVE_BUTTON_ID, false);
      
      //type some text to name field
      IDE.SAVE_AS_TEMPLATE.typeNameToInputField("a");
      
      //check save button enabled
      IDE.SAVE_AS_TEMPLATE.checkButtonState(SaveAsTemplate.SAVE_BUTTON_ID, true);
      
      //remove text from name field
      IDE.SAVE_AS_TEMPLATE.typeNameToInputField("");
      
      //check save button disabled
      IDE.SAVE_AS_TEMPLATE.checkButtonState(SaveAsTemplate.SAVE_BUTTON_ID, false);
      
      //set name
      IDE.SAVE_AS_TEMPLATE.typeNameToInputField(REST_SERVICE_TEMPLATE_NAME);
      
      //set description
      IDE.SAVE_AS_TEMPLATE.typeDescriptionToInputField(REST_SERVICE_TEMPLATE_DESCRIPTION);
      //click save button
      IDE.SAVE_AS_TEMPLATE.clickSaveButton();
      //check info dialog, that template crated successfully
      IDE.INFORMATION_DIALOG.waitForInfoDialog("Template created successfully!");
      //click ok button
      IDE.INFORMATION_DIALOG.clickOk();
      IDE.INFORMATION_DIALOG.waitForInfoDialogNotPresent();
      
      //------------ 3 ----------
      //Click on "New->From Template" button and then click on "test groovy template" item.
      IDE.TOOLBAR.runCommandFromNewPopupMenu(MenuCommands.New.FILE_FROM_TEMPLATE);
      IDE.TEMPLATES.waitForFileFromTemplateForm();
      
      // check "Create file" dialog window
      IDE.TEMPLATES.checkCreateFileFromTemplateWindow();
      IDE.TEMPLATES.selectFileTemplate(REST_SERVICE_TEMPLATE_NAME);
      
      //------------ 4 ----------
      //Change "File Name.groovy" field text on "Test Groovy File.groovy" name, click on "Create" button.
      IDE.TEMPLATES.typeNameToInputField(REST_SERVICE_FILE_NAME);
      //click Create button
      IDE.TEMPLATES.clickCreateButton();
      IDE.EDITOR.waitTabPresent(1);
      //there should be new tab with title "Test Groovy File.groovy", 
      //first line "// test groovy file template" in content and with "Groovy" 
      //highlighting opened in the Content Panel.
      assertEquals(REST_SERVICE_FILE_NAME + " *",IDE.EDITOR.getTabTitle(1));
      assertTrue(IDE.EDITOR.getTextFromCodeEditor(0).startsWith(TEXT));
      
      //------------ 5 ----------
      //Close files "Test File.groovy" and "Test Groovy File.groovy".
     //IDE.EDITOR.closeUnsavedFileAndDoNotSave(1);
     IDE.EDITOR.closeTabIgnoringChanges(1);     
     IDE.EDITOR.closeFile(0);
   }
   
}
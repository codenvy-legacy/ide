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
package org.exoplatform.ide.operation.upload;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

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
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: $
 *
 */
public class OpenFileByPathTest extends BaseTest
{   
   private static final String OPEN_FILE_BY_PATH_WINDOW_ID = "ideOpenFileByPathWindow";   
   
   private static final String OPEN_FILE_BY_PATH_FORM_ID = "ideOpenFileByPathForm";

   private static final String OPEN_FILE_BY_PATH_FORM_OPEN_BUTTON_ID = "ideOpenFileByPathFormOpenButton";

   private static final String OPEN_FILE_BY_PATH_FORM_CANCEL_BUTTON_ID = "ideOpenFileByPathFormCancelButton";

   private static final String OPEN_FILE_BY_PATH_FORM_FILE_PATH_FIELD_NAME = "ideOpenFileByPathFormFilePathField";
   
   private static final String OPEN_FILE_BY_PATH_FORM_FILE_PATH_FIELD_LOCATOR = "scLocator=//DynamicForm[ID=\"" + OPEN_FILE_BY_PATH_FORM_ID + "\"]/item[name=" + OPEN_FILE_BY_PATH_FORM_FILE_PATH_FIELD_NAME + "]/element";   
   
   private static final String FILE_NAME = OpenFileByPathTest.class.getSimpleName() + "__приклад.grs";
   
   private static final String NOT_FOUND_ERROR_MESSAGE = "404 Not Found\n\n\nPossible reasons:\nService is not deployed.\nResource not found.";
   
   private String fileUrl;
   
   private String secondWorkspaceName;
   
   private final static String TEST_FOLDER = OpenFileByPathTest.class.getSimpleName();

   private final static String URL = BASE_URL + REST_CONTEXT + "/" + WEBDAV_CONTEXT + "/" + REPO_NAME + "/" + WS_NAME + "/";

   @BeforeClass
   public static void setUp()
   {
      try
      {
         VirtualFileSystemUtils.mkcol(URL + TEST_FOLDER);
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
   
   @Test
   public void testOpenFileByPath() throws Exception
   { 
      selenium.refresh();
      selenium.waitForPageToLoad(TestConstants.IDE_LOAD_PERIOD + "");
      Thread.sleep(TestConstants.PAGE_LOAD_PERIOD);
      
      // open folder
      IDE.toolbar().runCommand(ToolbarCommands.File.REFRESH);
      selectItemInWorkspaceTree(TEST_FOLDER);
      IDE.toolbar().runCommand(ToolbarCommands.File.REFRESH);   

      // create file 
      createSaveAndCloseFile(MenuCommands.New.REST_SERVICE_FILE, FILE_NAME, 0);     
      
      // get file's url
      selectItemInWorkspaceTree(FILE_NAME);
      fileUrl = getSelectedItemUrl();
      
      // switch on to second workspace
      secondWorkspaceName = getNonActiveWorkspaceName();
      selectWorkspace(secondWorkspaceName);     
      
      // call Open File By Path form
      IDE.menu().runCommand(MenuCommands.File.FILE, MenuCommands.File.OPEN_FILE_BY_PATH);
      assertTrue(selenium.isElementPresent("scLocator=//Window[ID=\"" + OPEN_FILE_BY_PATH_WINDOW_ID + "\"]"));
      assertTrue(selenium.isElementPresent(OPEN_FILE_BY_PATH_FORM_FILE_PATH_FIELD_LOCATOR));      
      checkOpenButton(false);
      checkCancelButtonEnabled();      
      
      // trying to type file path
      selenium.click(OPEN_FILE_BY_PATH_FORM_FILE_PATH_FIELD_LOCATOR);
      selenium.type(OPEN_FILE_BY_PATH_FORM_FILE_PATH_FIELD_LOCATOR, "h");      
      checkOpenButton(true);
      
      // empty file path field
      selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_BACK_SPACE);      
      Thread.sleep(TestConstants.REDRAW_PERIOD);
      checkOpenButton(false);
      
      // close form by clicking "Cancel" button
      selenium.click("scLocator=//IButton[ID=\"" + OPEN_FILE_BY_PATH_FORM_CANCEL_BUTTON_ID + "\"]/");
      Thread.sleep(TestConstants.REDRAW_PERIOD);
      assertFalse(selenium.isElementPresent("scLocator=//Window[ID=\"" + OPEN_FILE_BY_PATH_WINDOW_ID + "\"]"));
      
      checkClosingFormByEscapeKey();
      
      // trying to open file by wrong url and using "Enter" key
      IDE.menu().runCommand(MenuCommands.File.FILE, MenuCommands.File.OPEN_FILE_BY_PATH);
      selenium.type(OPEN_FILE_BY_PATH_FORM_FILE_PATH_FIELD_LOCATOR, "h");
      selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_ENTER);
      
      selenium.isTextPresent(NOT_FOUND_ERROR_MESSAGE);
      selenium.click("scLocator=//Dialog[ID=\"isc_globalWarn\"]/okButton/");

      Thread.sleep(TestConstants.SLEEP);
      
      assertTrue(selenium.isElementPresent("scLocator=//Window[ID=\"" + OPEN_FILE_BY_PATH_WINDOW_ID + "\"]"));            
      
      // close window by clicking the "close" button
      selenium.click("scLocator=//Window[ID=\"" + OPEN_FILE_BY_PATH_WINDOW_ID + "\"]/closeButton/");
      
      // trying to open file by correct url and using "Open" key
      openFileByFilePath(fileUrl);
      
      IDE.editor().closeTab(0);
      
      // return to initial workspace
      selectWorkspace(WS_NAME);
   }

   private void checkClosingFormByEscapeKey() throws Exception, InterruptedException
   {
      // close form by clicking "Esc" key 
      IDE.menu().runCommand(MenuCommands.File.FILE, MenuCommands.File.OPEN_FILE_BY_PATH);
      selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_ESCAPE);
      Thread.sleep(TestConstants.REDRAW_PERIOD);      
      assertFalse(selenium.isElementPresent("scLocator=//Window[ID=\"" + OPEN_FILE_BY_PATH_WINDOW_ID + "\"]"));
   }
   
   private void checkOpenButton(boolean enabled)
   {
      assertTrue(selenium.isElementPresent("scLocator=//IButton[ID=\"" + OPEN_FILE_BY_PATH_FORM_OPEN_BUTTON_ID + "\"]/"));
      
      if (enabled)
      {
         assertFalse(selenium.isElementPresent(
            "//div[@eventproxy='" + OPEN_FILE_BY_PATH_FORM_OPEN_BUTTON_ID + "']//td[@class='buttonTitleDisabled' and text()='Open']"));
      }
      else
      {
         assertTrue(selenium.isElementPresent(
            "//div[@eventproxy='" + OPEN_FILE_BY_PATH_FORM_OPEN_BUTTON_ID + "']//td[@class='buttonTitleDisabled' and text()='Open']"));
      }
   }
   
   private void checkCancelButtonEnabled()
   {
      assertTrue(selenium.isElementPresent(
         "scLocator=//IButton[ID=\"" + OPEN_FILE_BY_PATH_FORM_CANCEL_BUTTON_ID + "\"]/"));
      assertTrue(selenium.isElementPresent(
      "//div[@eventproxy='" + OPEN_FILE_BY_PATH_FORM_CANCEL_BUTTON_ID + "']//td[@class='buttonTitle' and text()='Cancel']"));
   }
   
   @AfterClass
   public static void tearDown()
   {
      try
      {
         VirtualFileSystemUtils.delete(URL+TEST_FOLDER);
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

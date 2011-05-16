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
import org.exoplatform.gwtframework.commons.rest.MimeType;
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

   private static final String OPEN_BUTTON_ID = "ideOpenFileByPathFormOpenButton";

   private static final String CANCEL_BUTTON_ID = "ideOpenFileByPathFormCancelButton";

   private static final String FILE_PATH_FIELD_NAME = "ideOpenFileByPathFormFilePathField";
   
   private static final String OPEN_FILE_BY_PATH_FORM_FILE_PATH_FIELD_LOCATOR = "scLocator=//DynamicForm[ID=\"" + OPEN_FILE_BY_PATH_FORM_ID + "\"]/item[name=" + FILE_PATH_FIELD_NAME + "]/element";   
   
   private static final String FILE_NAME = OpenFileByPathTest.class.getSimpleName() + ".grs";
   
   private static final String NOT_FOUND_ERROR_MESSAGE = "404 Not Found\n\n\nPossible reasons:\nService is not deployed.\nResource not found.";
   
   private String fileUrl;
   
   private String secondWorkspaceName;
   
   private final static String TEST_FOLDER = OpenFileByPathTest.class.getSimpleName();

   private final static String URL = BASE_URL + REST_CONTEXT + "/" + WEBDAV_CONTEXT + "/" + REPO_NAME + "/" + WS_NAME + "/";

   @BeforeClass
   public static void setUp()
   {
      final String filePath = "src/test/resources/org/exoplatform/ide/operation/upload/open-file-by-path.grs";
      try
      {
         VirtualFileSystemUtils.mkcol(URL + TEST_FOLDER);
         VirtualFileSystemUtils.put(filePath, MimeType.GROOVY_SERVICE,
            TestConstants.NodeTypes.EXO_GROOVY_RESOURCE_CONTAINER, WS_URL + TEST_FOLDER + "/" + FILE_NAME);
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
      waitForRootElement();
      IDE.TOOLBAR.waitForButtonEnabled(ToolbarCommands.File.REFRESH, true, TestConstants.WAIT_PERIOD * 10);
      
      // open folder
      IDE.TOOLBAR.runCommand(ToolbarCommands.File.REFRESH);
      IDE.WORKSPACE.waitForItem(WS_URL + TEST_FOLDER + "/");
      IDE.NAVIGATION.assertItemVisible(WS_URL+ TEST_FOLDER + "/");
      IDE.WORKSPACE.selectItem(WS_URL + TEST_FOLDER + "/");
      IDE.TOOLBAR.runCommand(ToolbarCommands.File.REFRESH);
      IDE.WORKSPACE.waitForItem(WS_URL+ TEST_FOLDER + "/" + FILE_NAME);

      // create file 
      IDE.WORKSPACE.selectItem(WS_URL+ TEST_FOLDER + "/" + FILE_NAME);
      // get file's url
      IDE.NAVIGATION.assertItemVisible(WS_URL+ TEST_FOLDER + "/" + FILE_NAME);
      fileUrl = getSelectedItemUrl();
      
      // switch on to second workspace
//      secondWorkspaceName = getNonActiveWorkspaceName();
      secondWorkspaceName = WS_NAME_2;
      IDE.SELECT_WORKSPACE.changeWorkspace(secondWorkspaceName);
      
      // call Open File By Path form
      IDE.MENU.runCommand(MenuCommands.File.FILE, MenuCommands.File.OPEN_FILE_BY_PATH);
      waitForElementPresent(OPEN_FILE_BY_PATH_WINDOW_ID);
      assertTrue(selenium.isElementPresent(OPEN_FILE_BY_PATH_WINDOW_ID));
      assertTrue(selenium.isElementPresent(FILE_PATH_FIELD_NAME));
      checkButtonState(OPEN_BUTTON_ID, false);
      checkButtonState(CANCEL_BUTTON_ID, true);
      
      // trying to type file path
      selenium.type(FILE_PATH_FIELD_NAME, "h");
      Thread.sleep(TestConstants.ANIMATION_PERIOD);
      checkButtonState(OPEN_BUTTON_ID, true);
      
      // empty file path field
      selenium.type(FILE_PATH_FIELD_NAME, "");
      Thread.sleep(TestConstants.ANIMATION_PERIOD);
      checkButtonState(OPEN_BUTTON_ID, false);
      
      // close form by clicking "Cancel" button
      selenium.click(CANCEL_BUTTON_ID);
      waitForElementNotPresent(OPEN_FILE_BY_PATH_WINDOW_ID);
      
      // trying to open file by wrong url
      IDE.MENU.runCommand(MenuCommands.File.FILE, MenuCommands.File.OPEN_FILE_BY_PATH);
      selenium.type(FILE_PATH_FIELD_NAME, "h");
      selenium.click(OPEN_BUTTON_ID);
      
      selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_ENTER);
      
      selenium.isTextPresent(NOT_FOUND_ERROR_MESSAGE);
      selenium.click("scLocator=//Dialog[ID=\"isc_globalWarn\"]/okButton/");

      Thread.sleep(TestConstants.SLEEP);
      
      assertTrue(selenium.isElementPresent("scLocator=//Window[ID=\"" + OPEN_FILE_BY_PATH_WINDOW_ID + "\"]"));            
      
      // close window by clicking the "close" button
      selenium.click("scLocator=//Window[ID=\"" + OPEN_FILE_BY_PATH_WINDOW_ID + "\"]/closeButton/");
      
      // trying to open file by correct url and using "Open" key
      openFileByFilePath(fileUrl);
      
     IDE.EDITOR.closeTab(0);
      
      // return to initial workspace
      selectWorkspace(WS_NAME);
   }

   private void checkClosingFormByEscapeKey() throws Exception, InterruptedException
   {
      // close form by clicking "Esc" key 
      IDE.MENU.runCommand(MenuCommands.File.FILE, MenuCommands.File.OPEN_FILE_BY_PATH);
      selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_ESCAPE);
      Thread.sleep(TestConstants.REDRAW_PERIOD);      
      assertFalse(selenium.isElementPresent("scLocator=//Window[ID=\"" + OPEN_FILE_BY_PATH_WINDOW_ID + "\"]"));
   }
   
   private void checkOpenButton(boolean enabled)
   {
      assertTrue(selenium.isElementPresent("scLocator=//IButton[ID=\"" + OPEN_BUTTON_ID + "\"]/"));
      
      if (enabled)
      {
         assertFalse(selenium.isElementPresent(
            "//div[@eventproxy='" + OPEN_BUTTON_ID + "']//td[@class='buttonTitleDisabled' and text()='Open']"));
      }
      else
      {
         assertTrue(selenium.isElementPresent(
            "//div[@eventproxy='" + OPEN_BUTTON_ID + "']//td[@class='buttonTitleDisabled' and text()='Open']"));
      }
   }
   
   private void checkCancelButtonEnabled()
   {
      assertTrue(selenium.isElementPresent(
         "scLocator=//IButton[ID=\"" + CANCEL_BUTTON_ID + "\"]/"));
      assertTrue(selenium.isElementPresent(
      "//div[@eventproxy='" + CANCEL_BUTTON_ID + "']//td[@class='buttonTitle' and text()='Cancel']"));
   }
   
   /**
    * Check the state of button (enabled, disabled) by button id.
    * 
    * @param buttonId - the id of button
    * @param isEnabled - is enabled
    */
   public void checkButtonState(String buttonId, boolean isEnabled)
   {
      assertTrue(selenium.isElementPresent("//div[@id='" + buttonId + "' and @button-enabled='" + String.valueOf(isEnabled) + "']"));
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

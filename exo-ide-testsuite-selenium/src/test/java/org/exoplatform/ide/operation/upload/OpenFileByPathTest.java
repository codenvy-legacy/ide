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

import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.MenuCommands;
import org.exoplatform.ide.TestConstants;
import org.exoplatform.ide.ToolbarCommands;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;

/**
 * Test for "Open file by path" dialog.
 * 
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: $
 *
 */
public class OpenFileByPathTest extends BaseTest
{
   private static final String OPEN_FILE_BY_PATH_WINDOW_LOCATOR = "//div[@view-id='ideOpenFileByPathWindow']";

   private static final String OPEN_BUTTON_ID = "ideOpenFileByPathFormOpenButton";

   private static final String CANCEL_BUTTON_ID = "ideOpenFileByPathFormCancelButton";

   private static final String FILE_PATH_FIELD_NAME = "ideOpenFileByPathFormFilePathField";

   private static final String FILE_NAME = OpenFileByPathTest.class.getSimpleName() + ".grs";

   private static final String NOT_FOUND_ERROR_MESSAGE = "Service is not deployed.\nParent folder not found.";

   private String fileUrl;

   private final static String TEST_FOLDER = OpenFileByPathTest.class.getSimpleName();

   private final static String URL = BASE_URL + REST_CONTEXT + "/" + WEBDAV_CONTEXT + "/" + REPO_NAME + "/" + WS_NAME
      + "/";

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
      }
   }

   @Test
   public void testOpenFileByPath() throws Exception
   {
      IDE.WORKSPACE.waitForRootItem();
      IDE.TOOLBAR.waitForButtonEnabled(ToolbarCommands.File.REFRESH, true);

      // open folder
      IDE.TOOLBAR.runCommand(ToolbarCommands.File.REFRESH);
      IDE.WORKSPACE.waitForItem(WS_URL + TEST_FOLDER);
      IDE.NAVIGATION.assertItemVisible(WS_URL + TEST_FOLDER);
      IDE.WORKSPACE.selectItem(WS_URL + TEST_FOLDER);
      IDE.TOOLBAR.runCommand(ToolbarCommands.File.REFRESH);
      IDE.WORKSPACE.waitForItem(WS_URL + TEST_FOLDER + "/" + FILE_NAME);

      // create file 
      IDE.WORKSPACE.selectItem(WS_URL + TEST_FOLDER + "/" + FILE_NAME);
      // get file's url
      IDE.NAVIGATION.assertItemVisible(WS_URL + TEST_FOLDER + "/" + FILE_NAME);
      fileUrl = getSelectedItemUrl();

      // switch on to second workspace
     // IDE.SELECT_WORKSPACE.changeWorkspace(WS_NAME_2);

      // call Open File By Path form
      IDE.MENU.runCommand(MenuCommands.File.FILE, MenuCommands.File.OPEN_FILE_BY_PATH);
      waitForElementPresent(OPEN_FILE_BY_PATH_WINDOW_LOCATOR);
      assertTrue(selenium().isElementPresent(OPEN_FILE_BY_PATH_WINDOW_LOCATOR));
      assertTrue(selenium().isElementPresent(FILE_PATH_FIELD_NAME));
      checkButtonState(OPEN_BUTTON_ID, false);
      checkButtonState(CANCEL_BUTTON_ID, true);

      // trying to type file path
      selenium().type(FILE_PATH_FIELD_NAME, "h");
      Thread.sleep(TestConstants.ANIMATION_PERIOD);
      checkButtonState(OPEN_BUTTON_ID, true);

      // empty file path field
      selenium().type(FILE_PATH_FIELD_NAME, "");
      Thread.sleep(TestConstants.ANIMATION_PERIOD);
      checkButtonState(OPEN_BUTTON_ID, false);

      // close form by clicking "Cancel" button
      selenium().click(CANCEL_BUTTON_ID);
      waitForElementNotPresent(OPEN_FILE_BY_PATH_WINDOW_LOCATOR);

      // trying to open file by wrong url
      IDE.MENU.runCommand(MenuCommands.File.FILE, MenuCommands.File.OPEN_FILE_BY_PATH);
      selenium().type(FILE_PATH_FIELD_NAME, "h");
      selenium().click(OPEN_BUTTON_ID);

      IDE.WARNING_DIALOG.waitOpened();
    //TODO  IDE.WARNING_DIALOG.checkIsOpened(NOT_FOUND_ERROR_MESSAGE);
      IDE.WARNING_DIALOG.clickOk();

      Thread.sleep(TestConstants.SLEEP);

      assertFalse(selenium().isElementPresent(OPEN_FILE_BY_PATH_WINDOW_LOCATOR));

      // close window
      
      //after closing of the error message window, open file by path window also  is closing
      //selenium().click(CANCEL_BUTTON_ID);
      waitForElementNotPresent(OPEN_FILE_BY_PATH_WINDOW_LOCATOR);

      // trying to open file by correct url and using "Open" key
      IDE.MENU.runCommand(MenuCommands.File.FILE, MenuCommands.File.OPEN_FILE_BY_PATH);
      waitForElementPresent(OPEN_FILE_BY_PATH_WINDOW_LOCATOR);
      assertTrue(selenium().isElementPresent(OPEN_FILE_BY_PATH_WINDOW_LOCATOR));
      assertTrue(selenium().isElementPresent(FILE_PATH_FIELD_NAME));
      checkButtonState(OPEN_BUTTON_ID, false);
      checkButtonState(CANCEL_BUTTON_ID, true);
      selenium().type(FILE_PATH_FIELD_NAME, fileUrl);
      Thread.sleep(TestConstants.ANIMATION_PERIOD);
      checkButtonState(OPEN_BUTTON_ID, true);
      selenium().click(OPEN_BUTTON_ID);
      waitForElementNotPresent(OPEN_FILE_BY_PATH_WINDOW_LOCATOR);
      IDE.EDITOR.waitTabPresent(0);

      IDE.EDITOR.closeFile(0);

      // return to initial workspace
    //  IDE.SELECT_WORKSPACE.changeWorkspace(WS_NAME);
   }

   /**
    * Check the state of button (enabled, disabled) by button id.
    * 
    * @param buttonId - the id of button
    * @param isEnabled - is enabled
    */
   public void checkButtonState(String buttonId, boolean isEnabled)
   {
      assertTrue(selenium().isElementPresent("//div[@id='" + buttonId + "' and @button-enabled='"
         + String.valueOf(isEnabled) + "']"));
   }

   @AfterClass
   public static void tearDown()
   {
      try
      {
         VirtualFileSystemUtils.delete(URL + TEST_FOLDER);
      }
      catch (IOException e)
      {
      }
   }
}

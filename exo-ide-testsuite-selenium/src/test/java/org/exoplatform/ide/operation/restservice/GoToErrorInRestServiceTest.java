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
package org.exoplatform.ide.operation.restservice;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.MenuCommands;
import org.exoplatform.ide.TestConstants;
import org.exoplatform.ide.ToolbarCommands;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;

/**
 * @author <a href="mailto:oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id:
 *
 */
public class GoToErrorInRestServiceTest extends BaseTest
{
   private final static String FILE_WITH_ERROR = "RestServiceWithError.grs";

   private final static String TEST_FOLDER = GoToErrorInRestServiceTest.class.getSimpleName();

   private final static String FILE_WITH_ERROR_FOR_CHANGING = "RestServiceWithErrorForChanging.grs";

   private final static String URL = WS_URL + TEST_FOLDER + "/";

   @BeforeClass
   public static void setUp()
   {

      String filePath = "src/test/resources/org/exoplatform/ide/operation/restservice/";

      try
      {
         VirtualFileSystemUtils.mkcol(URL);
         VirtualFileSystemUtils.put(filePath + "RestServiceWithError.groovy", MimeType.GROOVY_SERVICE, URL
            + FILE_WITH_ERROR);
         VirtualFileSystemUtils.put(filePath + "RestServiceWithErrorForChanging.groovy", MimeType.GROOVY_SERVICE, URL
            + FILE_WITH_ERROR_FOR_CHANGING);
      }
      catch (IOException e)
      {
         e.printStackTrace();
      }
   }

   @Test
   public void testGoToErrorInOpenedFile() throws Exception
   {
      IDE.WORKSPACE.waitForRootItem();
      openAndValidateRestService();
      //check, validation fails
      final String validationMsg = IDE.OUTPUT.getOutputMessageText(1);
      assertTrue(validationMsg.contains("validation failed"));
      //click on validation message to go to error
      IDE.OUTPUT.clickOnErrorMessage(1);
      Thread.sleep(TestConstants.REDRAW_PERIOD);
      //check, cursor go to position
      assertEquals("3 : 9", IDE.STATUSBAR.getCursorPosition());
      
      IDE.EDITOR.closeFile(0);
   }

   @Test
   public void testGoToErrorInClosedFile() throws Exception
   {
      //refresh, to clear console and close it
      selenium().refresh();
      IDE.WORKSPACE.waitForRootItem();

      openAndValidateRestService();
      //check, validation fails
      final String validationMsg = IDE.OUTPUT.getOutputMessageText(1);
      assertTrue(validationMsg.contains("validation failed"));
      //close tab
      IDE.EDITOR.closeFile(0);

      //click on validation message to go to error
      IDE.OUTPUT.clickOnErrorMessage(1);
      IDE.EDITOR.waitTabPresent(0);
      Thread.sleep(TestConstants.EDITOR_OPEN_PERIOD);

      //file must be opened and cursor must stay on error
      assertEquals(FILE_WITH_ERROR, IDE.EDITOR.getTabTitle(0));
      
      assertEquals("3 : 9", IDE.STATUSBAR.getCursorPosition());

      //---- 3 -----------------
      //open new rest service file and check, that cursor doesn't go to position 3 : 9
      IDE.TOOLBAR.runCommandFromNewPopupMenu(MenuCommands.New.REST_SERVICE_FILE);
      IDE.EDITOR.waitTabPresent(1);
      Thread.sleep(TestConstants.EDITOR_OPEN_PERIOD);

      assertEquals("1 : 1", IDE.STATUSBAR.getCursorPosition());
      
      IDE.EDITOR.closeTabIgnoringChanges(1);
      IDE.EDITOR.closeFile(0);
   }

   @Test
   public void testGoToErrorIfOtherTabSelected() throws Exception
   {
      //refresh, to clear console and close it
      selenium().refresh();
      IDE.WORKSPACE.waitForRootItem();

      openAndValidateRestService();
      //check, validation fails
      final String validationMsg = IDE.OUTPUT.getOutputMessageText(1);
      assertTrue(validationMsg.contains("validation failed"));
      
      //open another tab
      IDE.TOOLBAR.runCommandFromNewPopupMenu(MenuCommands.New.XML_FILE);
      IDE.EDITOR.waitTabPresent(1);

      //click on validation message to go to error
      IDE.OUTPUT.clickOnErrorMessage(1);
      Thread.sleep(TestConstants.EDITOR_OPEN_PERIOD);

      //check, tab with rest service must be opened
      Assert.assertTrue(IDE.EDITOR.isEditorTabSelected(FILE_WITH_ERROR));

      assertEquals("3 : 9", IDE.STATUSBAR.getCursorPosition());

      //---- 3 -----------------
      IDE.EDITOR.closeTabIgnoringChanges(1);

      //open new rest service file and check, that cursor doesn't go to position 3 : 9
      IDE.TOOLBAR.runCommandFromNewPopupMenu(MenuCommands.New.REST_SERVICE_FILE);
      IDE.EDITOR.waitTabPresent(1);

      assertEquals("1 : 1", IDE.STATUSBAR.getCursorPosition());
      
      IDE.EDITOR.closeTabIgnoringChanges(1);
      IDE.EDITOR.closeFile(0);
   }

   @Test
   public void testGoToErrorIfFileIsDeleted() throws Exception
   {
      //refresh, to clear console and close it
      selenium().refresh();
      IDE.WORKSPACE.waitForRootItem();

      openAndValidateRestService();
      //check, validation fails
      final String validationMsg = IDE.OUTPUT.getOutputMessageText(1);
      assertTrue(validationMsg.contains("validation failed"));
      
      //close tab
      IDE.EDITOR.closeFile(0);
      Thread.sleep(TestConstants.SLEEP);

      //---- 2 -----------------
      //delete file
      IDE.NAVIGATION.assertItemVisible(URL + FILE_WITH_ERROR);
      IDE.NAVIGATION.deleteSelectedItems();

      //---- 3 -----------------
      //click on validation message to go to error
      IDE.OUTPUT.clickOnErrorMessage(1);

      IDE.ERROR_DIALOG.waitIsOpened();
      //check, error dialog appeared
      IDE.ERROR_DIALOG.checkMessageContains("404");

      //click Ok button
      IDE.ERROR_DIALOG.clickOk();
      IDE.ERROR_DIALOG.waitIsClosed();
      //---- 5 -----------------
      //open new rest service file and check, that cursor doesn't go to position 3 : 9
      IDE.TOOLBAR.runCommandFromNewPopupMenu(MenuCommands.New.REST_SERVICE_FILE);
      IDE.EDITOR.waitTabPresent(0);

      assertEquals("1 : 1", IDE.STATUSBAR.getCursorPosition());
      IDE.EDITOR.closeTabIgnoringChanges(0);

   }

   @Test
   public void testGoToErrorAfterChangingFile() throws Exception
   {
      //refresh, to clear console and close it
      selenium().refresh();
      IDE.WORKSPACE.waitForRootItem();
      IDE.WORKSPACE.waitForItem(WS_URL + TEST_FOLDER + "/");

      //---- 1 -----------------
      //open file    
      IDE.WORKSPACE.doubleClickOnFolder(WS_URL + TEST_FOLDER + "/");
      IDE.WORKSPACE.waitForItem(WS_URL + TEST_FOLDER + "/" + FILE_WITH_ERROR_FOR_CHANGING);
      IDE.WORKSPACE.doubleClickOnFile(WS_URL + TEST_FOLDER + "/" + FILE_WITH_ERROR_FOR_CHANGING);
      IDE.EDITOR.waitTabPresent(0);
      
      //---- 2 -----------------
      //press validate button
      IDE.TOOLBAR.runCommand(ToolbarCommands.Run.VALIDATE_GROOVY_SERVICE);
      IDE.OUTPUT.waitForMessageShow(1);
      
      //check, validation fails
      String validationMsg = IDE.OUTPUT.getOutputMessageText(1);
      assertTrue(validationMsg.contains("validation failed"));

      //---- 3 -----------------
      //click on validation message to go to error
      IDE.OUTPUT.clickOnErrorMessage(1);
      Thread.sleep(TestConstants.REDRAW_PERIOD);
      
      //fix validation error
      IDE.EDITOR.clickOnEditor(0);
      IDE.EDITOR.selectIFrameWithEditor(0);
      //go to error
      for (int i = 0; i < 6; i++)
      {
         selenium().keyPressNative("" + java.awt.event.KeyEvent.VK_LEFT);
         Thread.sleep(TestConstants.TYPE_DELAY_PERIOD);
      }
      //delete# unnecessary  space
      selenium().keyPressNative("" + java.awt.event.KeyEvent.VK_BACK_SPACE);
      Thread.sleep(TestConstants.REDRAW_PERIOD);
      IDE.selectMainFrame();
      Thread.sleep(TestConstants.SLEEP * 2);

      //---- 4 -----------------
      //press validate button
      IDE.TOOLBAR.runCommand(ToolbarCommands.Run.VALIDATE_GROOVY_SERVICE);
      IDE.OUTPUT.waitForMessageShow(2);
      
      //check, validation fails
      validationMsg = IDE.OUTPUT.getOutputMessageText(2);
      assertTrue(validationMsg.contains("validation failed"));

      //---- 5 -----------------
      //click on validation message to go to error
      IDE.OUTPUT.clickOnErrorMessage(2);
      Thread.sleep(TestConstants.REDRAW_PERIOD);

      //check cursor went to position
      assertEquals("3 : 3", IDE.STATUSBAR.getCursorPosition());

      //---- 6 -----------------
      //click on first validation message to check, 
      //that cursor can go to previous error (event, it is already fixed)
      IDE.OUTPUT.clickOnErrorMessage(1);

      //check cursor went to position
      assertEquals("1 : 9", IDE.STATUSBAR.getCursorPosition());

      //---- 7 -----------------
      //delete some text and check
      //that cursor stays if try to go to nonexistent line
      IDE.OUTPUT.clickOnErrorMessage(2);
      
      //click on editor
      IDE.EDITOR.clickOnEditor(0);
      IDE.EDITOR.deleteFileContent(0);

      //type some text
      IDE.EDITOR.typeTextIntoEditor(0, "public void TestClass(){}");

      //go to middle
      IDE.EDITOR.selectIFrameWithEditor(0);
      //select all
      selenium().keyPressNative("" + java.awt.event.KeyEvent.VK_LEFT);
      Thread.sleep(TestConstants.TYPE_DELAY_PERIOD);
      selenium().keyPressNative("" + java.awt.event.KeyEvent.VK_LEFT);
      Thread.sleep(TestConstants.TYPE_DELAY_PERIOD);
      IDE.selectMainFrame();

      assertEquals("1 : 24", IDE.STATUSBAR.getCursorPosition());

      //when 3d line is deleted, try to go to it
      IDE.OUTPUT.clickOnErrorMessage(2);
      assertEquals("1 : 24", IDE.STATUSBAR.getCursorPosition());
      
      IDE.EDITOR.closeTabIgnoringChanges(0);
   }

   private void openAndValidateRestService() throws Exception
   {
      //---- 1 -----------------
      //open file
      IDE.WORKSPACE.waitForItem(WS_URL + TEST_FOLDER + "/");
      IDE.WORKSPACE.doubleClickOnFolder(WS_URL + TEST_FOLDER + "/");
      IDE.WORKSPACE.waitForItem(URL + FILE_WITH_ERROR);
      IDE.WORKSPACE.doubleClickOnFile(URL + FILE_WITH_ERROR);
      IDE.EDITOR.waitTabPresent(0);
      
      //---- 2 -----------------
      //press validate button
      IDE.TOOLBAR.runCommand(ToolbarCommands.Run.VALIDATE_GROOVY_SERVICE);
      IDE.OUTPUT.waitForMessageShow(1);
   }

   @AfterClass
   public static void tearDown()
   {
      try
      {
         VirtualFileSystemUtils.delete(URL);
      }
      catch (IOException e)
      {
         e.printStackTrace();
      }
   }

}

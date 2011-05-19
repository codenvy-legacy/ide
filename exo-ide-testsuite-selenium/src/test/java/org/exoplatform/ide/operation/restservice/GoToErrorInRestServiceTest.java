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

import org.exoplatform.common.http.client.ModuleException;
import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.MenuCommands;
import org.exoplatform.ide.TestConstants;
import org.exoplatform.ide.ToolbarCommands;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.junit.After;
import org.junit.AfterClass;
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

   private final static String URL = BASE_URL +  REST_CONTEXT + "/" + WEBDAV_CONTEXT + "/" + REPO_NAME + "/" + WS_NAME + "/" + TEST_FOLDER
      + "/";

   @BeforeClass
   public static void setUp()
   {

      String filePath = "src/test/resources/org/exoplatform/ide/operation/restservice/";

      try
      {
         VirtualFileSystemUtils.mkcol(URL);
         VirtualFileSystemUtils.put(filePath + "RestServiceWithError.groovy", MimeType.GROOVY_SERVICE, URL + FILE_WITH_ERROR);
         VirtualFileSystemUtils.put(filePath + "RestServiceWithErrorForChanging.groovy", MimeType.GROOVY_SERVICE, URL
            + FILE_WITH_ERROR_FOR_CHANGING);
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
   public void testGoToErrorInOpenedFile() throws Exception
   {
      Thread.sleep(TestConstants.SLEEP);

      openAndValidateRestService();

      //---- 1 -----------------
      //click on validation message to go to error
      selenium.clickAt("//div[@id='ideOutputContent']/div/table//font[@color='#880000']/span", "5,5");
      Thread.sleep(TestConstants.SLEEP);

      //check, cursor go to position
      assertEquals("3 : 9", getCursorPositionUsingStatusBar());

   }

   @Test
   public void testGoToErrorInClosedFile() throws Exception
   {
      //refresh, to clear console and close it
      selenium.refresh();
      selenium.waitForPageToLoad("" + TestConstants.IDE_LOAD_PERIOD);
      Thread.sleep(TestConstants.SLEEP);

      openAndValidateRestService();
      //---- 1 -----------------
      //close tab
     //IDE.EDITOR.tryCloseTabWithNonSaving(0);
     IDE.EDITOR.closeTabIgnoringChanges(0);

      //---- 2 -----------------
      //click on validation message to go to error
      selenium.clickAt("//div[@id='ideOutputContent']/div/table//font[@color='#880000']/span", "5,5");
      Thread.sleep(TestConstants.SLEEP);

      //file must be opened and cursor must stay on error
      assertEquals(FILE_WITH_ERROR,IDE.EDITOR.getTabTitle(0));

      assertEquals("3 : 9", getCursorPositionUsingStatusBar());

      //---- 3 -----------------
      //open new rest service file and check, that cursor doesn't go to position 3 : 9
      IDE.TOOLBAR.runCommandFromNewPopupMenu(MenuCommands.New.REST_SERVICE_FILE);

      assertEquals("1 : 1", getCursorPositionUsingStatusBar());
   }

   @Test
   public void testGoToErrorIfOtherTabSelected() throws Exception
   {
      //refresh, to clear console and close it
      selenium.refresh();
      selenium.waitForPageToLoad("" + TestConstants.IDE_LOAD_PERIOD);
      waitForRootElement();

      openAndValidateRestService();
      //---- 1 -----------------
      //open another tab
      IDE.TOOLBAR.runCommandFromNewPopupMenu(MenuCommands.New.XML_FILE);

      //---- 2 -----------------
      //click on validation message to go to error
      selenium.clickAt("//div[@id='ideOutputContent']/div/table//font[@color='#880000']/span", "5,5");
      Thread.sleep(TestConstants.SLEEP);

      //check, tab with rest service must be opened
     IDE.EDITOR.checkEditorTabSelected(FILE_WITH_ERROR, true);

      assertEquals("3 : 9", getCursorPositionUsingStatusBar());

      //---- 3 -----------------
      //close file
     //IDE.EDITOR.tryCloseTabWithNonSaving(1);
     IDE.EDITOR.closeTabIgnoringChanges(1);

      //open new rest service file and check, that cursor doesn't go to position 3 : 9
      IDE.TOOLBAR.runCommandFromNewPopupMenu(MenuCommands.New.REST_SERVICE_FILE);

      assertEquals("1 : 1", getCursorPositionUsingStatusBar());
   }

   @Test
   public void testGoToErrorIfFileIsDeleted() throws Exception
   {
      //refresh, to clear console and close it
      selenium.refresh();
      selenium.waitForPageToLoad("" + TestConstants.IDE_LOAD_PERIOD);
      waitForRootElement();
      
      openAndValidateRestService();
      //---- 1 -----------------
      //close tab
     //IDE.EDITOR.tryCloseTabWithNonSaving(0);
     IDE.EDITOR.closeTabIgnoringChanges(0);

      //---- 2 -----------------
      //delete file
      IDE.NAVIGATION.assertItemVisible(URL + FILE_WITH_ERROR);
      IDE.NAVIGATION.deleteSelectedItems();

      //---- 3 -----------------
      //click on validation message to go to error
      selenium.clickAt("//div[@id='ideOutputContent']/div/table//font[@color='#880000']/span", "5,5");
      Thread.sleep(TestConstants.SLEEP);

      //check, error dialog appeared
      assertTrue(selenium.isElementPresent("exoWarningDialog"));
      final String textFromErrDialog = selenium.getText("exoWarningDialog");
      System.out.println(textFromErrDialog);
      assertTrue(textFromErrDialog.contains("404"));

      //---- 4 -----------------
      //click Ok button
      selenium.click("exoWarningDialogOkButton");
      Thread.sleep(TestConstants.REDRAW_PERIOD);

      //---- 5 -----------------
      //open new rest service file and check, that cursor doesn't go to position 3 : 9
      IDE.TOOLBAR.runCommandFromNewPopupMenu(MenuCommands.New.REST_SERVICE_FILE);

      assertEquals("1 : 1", getCursorPositionUsingStatusBar());
   }

   @Test
   public void testGoToErrorAfterChangingFile() throws Exception
   {
      //refresh, to clear console and close it
      selenium.refresh();
      selenium.waitForPageToLoad("" + TestConstants.IDE_LOAD_PERIOD);
      waitForRootElement();
      
      //---- 1 -----------------
      //open file    
      IDE.NAVIGATION.clickOpenIconOfFolder(WS_URL + TEST_FOLDER + "/");
      IDE.WORKSPACE.waitForItem(URL + FILE_WITH_ERROR_FOR_CHANGING);     
      IDE.NAVIGATION.openFileFromNavigationTreeWithCodeEditor(URL + FILE_WITH_ERROR_FOR_CHANGING, false);

      //---- 2 -----------------
      //press validate button
      IDE.TOOLBAR.runCommand(ToolbarCommands.Run.VALIDATE_GROOVY_SERVICE);
      Thread.sleep(TestConstants.PAGE_LOAD_PERIOD);

      //check, validation fails
      String validationMsg =
         selenium.getText("//div[@id='ideOutputContent']/div/table//font[@color='#880000']/span");
      assertTrue(validationMsg.contains("validation failed"));

      //---- 3 -----------------
      //click on validation message to go to error
      selenium.clickAt("//div[@id='ideOutputContent']/div/table//font[@color='#880000']/span", "5,5");
      Thread.sleep(TestConstants.SLEEP);

      //fix validation error
     IDE.EDITOR.selectIFrameWithEditor(0);
      //click on editor

      //TODO******************fix****************************
      selenium.clickAt("//body[@class='editbox']", "5,5");
      //**************************************************
      //go to error
      for (int i = 0; i < 6; i++)
      {
         selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_LEFT);
         Thread.sleep(TestConstants.TYPE_DELAY_PERIOD);
      }
      //delete# unnecessary  space
      selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_BACK_SPACE);
      Thread.sleep(TestConstants.TYPE_DELAY_PERIOD);

      IDE.selectMainFrame();

      //---- 4 -----------------
      //press validate button
      Thread.sleep(TestConstants.SLEEP);
      IDE.TOOLBAR.runCommand(ToolbarCommands.Run.VALIDATE_GROOVY_SERVICE);

      //check, validation fails
      validationMsg =
         selenium.getText("//div[@id='ideOutputContent']/div[2]/table//font[@color='#880000']/span");
      assertTrue(validationMsg.contains("validation failed"));

      //---- 5 -----------------
      //click on validation message to go to error
      Thread.sleep(TestConstants.SLEEP);
      selenium.clickAt("//div[@id='ideOutputContent']/div[2]/table//font[@color='#880000']/span", "5,5");
      Thread.sleep(TestConstants.SLEEP);

      //check cursor went to position
      assertEquals("3 : 3", getCursorPositionUsingStatusBar());

      //---- 6 -----------------
      //click on first validation message to check, 
      //that cursor can go to previous error (event, it is already fixed)
      selenium.clickAt("//div[@id='ideOutputContent']/div/table//font[@color='#880000']/span", "5,5");
      Thread.sleep(TestConstants.SLEEP);

      //check cursor went to position
      assertEquals("1 : 9", getCursorPositionUsingStatusBar());

      //---- 7 -----------------
      //delete some text and check
      //that cursor stays if try to go to nonexistent line
      selenium.clickAt("//div[@id='ideOutputContent']/div[2]/table//font[@color='#880000']/span", "5,5");
      Thread.sleep(TestConstants.SLEEP);

     IDE.EDITOR.selectIFrameWithEditor(0);
      //click on editor

      //TODO******************fix****************************
      selenium.clickAt("//body[@class='editbox']", "5,5");
      //*************************************************
      //select all
      selenium.keyDownNative("" + java.awt.event.KeyEvent.VK_CONTROL);
      selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_A);
      selenium.keyUpNative("" + java.awt.event.KeyEvent.VK_CONTROL);
      //delete
      selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_DELETE);
      IDE.selectMainFrame();

      Thread.sleep(TestConstants.SLEEP);

      //type some text
     IDE.EDITOR.typeTextIntoEditor(0, "public void TestClass(){}");

      //go to middle
     IDE.EDITOR.selectIFrameWithEditor(0);
      //select all
      selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_LEFT);
      Thread.sleep(TestConstants.TYPE_DELAY_PERIOD);
      selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_LEFT);
      Thread.sleep(TestConstants.TYPE_DELAY_PERIOD);
      IDE.selectMainFrame();

      assertEquals("1 : 24", getCursorPositionUsingStatusBar());

      //when 3d line is deleted, try to go to it
      selenium.clickAt("//div[@id='ideOutputContent']/div[2]/table//font[@color='#880000']/span", "5,5");
      Thread.sleep(TestConstants.SLEEP);

      assertEquals("1 : 24", getCursorPositionUsingStatusBar());

      Thread.sleep(TestConstants.SLEEP);

   }

   private void openAndValidateRestService() throws Exception
   {
      //---- 1 -----------------
      //open file
      IDE.TOOLBAR.runCommand(ToolbarCommands.File.REFRESH);
      Thread.sleep(TestConstants.SLEEP);
      IDE.NAVIGATION.assertItemVisible(WS_URL + TEST_FOLDER + "/");
      IDE.NAVIGATION.clickOpenIconOfFolder(WS_URL + TEST_FOLDER + "/");
      IDE.WORKSPACE.waitForItem(URL + FILE_WITH_ERROR);
      IDE.NAVIGATION.openFileFromNavigationTreeWithCodeEditor(URL + FILE_WITH_ERROR, false);
      Thread.sleep(TestConstants.SLEEP);

      //---- 2 -----------------
      //press validate button
      IDE.TOOLBAR.runCommand(ToolbarCommands.Run.VALIDATE_GROOVY_SERVICE);
      Thread.sleep(TestConstants.PAGE_LOAD_PERIOD);

      //check, validation fails
      final String validationMsg =
         selenium.getText("//div[@id='ideOutputContent']/div/table//font[@color='#880000']/");
      assertTrue(validationMsg.contains("validation failed"));
   }

   @After
   public void afterMethod() throws Exception
   {
      //check, if opened, close two files
//     IDE.EDITOR.tryCloseTabWithNonSaving(0);
//     IDE.EDITOR.tryCloseTabWithNonSaving(0);
     IDE.EDITOR.closeTabIgnoringChanges(0);
     IDE.EDITOR.closeTabIgnoringChanges(0);
   }

   @AfterClass
   public static void tearDown()
   {
      try
      {
         VirtualFileSystemUtils.delete(URL + FILE_WITH_ERROR);
         VirtualFileSystemUtils.delete(URL + FILE_WITH_ERROR_FOR_CHANGING);
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

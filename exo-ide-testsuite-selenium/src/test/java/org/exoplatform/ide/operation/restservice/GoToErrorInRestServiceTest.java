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
import org.exoplatform.ide.ToolbarCommands;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.exoplatform.ide.vfs.shared.Link;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.Keys;

import java.io.IOException;
import java.util.Map;

/**
 * @author <a href="mailto:oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id:
 *
 */
public class GoToErrorInRestServiceTest extends BaseTest
{
   private final static String FILE_WITH_ERROR = "RestServiceWithError.grs";

   private final static String PROJECT = GoToErrorInRestServiceTest.class.getSimpleName();

   private final static String FILE_WITH_ERROR_FOR_CHANGING = "RestServiceWithErrorForChanging.grs";

   @BeforeClass
   public static void setUp()
   {

      String filePath = "src/test/resources/org/exoplatform/ide/operation/restservice/";

      try
      {
         Map<String, Link> project = VirtualFileSystemUtils.createDefaultProject(PROJECT);
         Link link = project.get(Link.REL_CREATE_FILE);
         VirtualFileSystemUtils.createFileFromLocal(link, FILE_WITH_ERROR, MimeType.GROOVY_SERVICE, filePath
            + "RestServiceWithError.groovy");
         VirtualFileSystemUtils.createFileFromLocal(link, FILE_WITH_ERROR_FOR_CHANGING, MimeType.GROOVY_SERVICE,
            filePath + "RestServiceWithErrorForChanging.groovy");
      }
      catch (IOException e)
      {
         e.printStackTrace();
      }
   }

   @Test
   public void testGoToErrorInOpenedFile() throws Exception
   {
      IDE.PROJECT.EXPLORER.waitOpened();
      IDE.LOADER.waitClosed();
      IDE.PROJECT.OPEN.openProject(PROJECT);
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT);
      IDE.LOADER.waitClosed();

      openAndValidateRestService();
      //check, validation fails
      final String validationMsg = IDE.OUTPUT.getOutputMessage(1);
      assertTrue(validationMsg.contains("validation failed"));
      //click on validation message to go to error
      IDE.OUTPUT.clickOnErrorMessage(1);
      IDE.STATUSBAR.waitCursorPositionControl();
      //check, cursor go to position
      assertEquals("3 : 9", IDE.STATUSBAR.getCursorPosition());

      IDE.EDITOR.closeFile(FILE_WITH_ERROR);
      IDE.EDITOR.waitTabNotPresent(FILE_WITH_ERROR);
   }

   @Test
   public void testGoToErrorInClosedFile() throws Exception
   {
      driver.navigate().refresh();
      IDE.LOADER.waitClosed();
      IDE.PROJECT.EXPLORER.waitOpened();
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT);
      IDE.LOADER.waitClosed();

      openAndValidateRestService();
      //check, validation fails
      final String validationMsg = IDE.OUTPUT.getOutputMessage(1);
      assertTrue(validationMsg.contains("validation failed"));
      //close tab
      IDE.EDITOR.closeFile(FILE_WITH_ERROR);
      IDE.EDITOR.waitTabNotPresent(FILE_WITH_ERROR);

      //click on validation message to go to error
      IDE.OUTPUT.clickOnErrorMessage(1);

      IDE.EDITOR.waitActiveFile(PROJECT + "/" + FILE_WITH_ERROR);

      //file must be opened and cursor must stay on error
      assertEquals(FILE_WITH_ERROR, IDE.EDITOR.getTabTitle(1));

      assertEquals("3 : 9", IDE.STATUSBAR.getCursorPosition());

      //---- 3 -----------------
      //open new rest service file and check, that cursor doesn't go to position 3 : 9
      IDE.TOOLBAR.runCommandFromNewPopupMenu(MenuCommands.New.REST_SERVICE_FILE);
      IDE.EDITOR.waitActiveFile(PROJECT + "/Untitled file.grs");

      assertEquals("1 : 1", IDE.STATUSBAR.getCursorPosition());

      IDE.EDITOR.closeFile(FILE_WITH_ERROR);
      IDE.EDITOR.waitTabNotPresent(FILE_WITH_ERROR);
      IDE.EDITOR.closeTabIgnoringChanges(1);
   }

   @Test
   public void testGoToErrorIfOtherTabSelected() throws Exception
   {
      driver.navigate().refresh();
      IDE.LOADER.waitClosed();
      IDE.PROJECT.EXPLORER.waitOpened();
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT);
      IDE.LOADER.waitClosed();

      openAndValidateRestService();
      //check, validation fails
      final String validationMsg = IDE.OUTPUT.getOutputMessage(1);
      assertTrue(validationMsg.contains("validation failed"));

      //open another tab
      IDE.TOOLBAR.runCommandFromNewPopupMenu(MenuCommands.New.XML_FILE);
      IDE.EDITOR.waitActiveFile(PROJECT + "/Untitled file.xml");

      //click on validation message to go to error
      IDE.OUTPUT.clickOnErrorMessage(1);

      //check, tab with rest service must be opened
      assertTrue(IDE.EDITOR.isEditorTabSelected(FILE_WITH_ERROR));
      IDE.STATUSBAR.waitCursorPositionAt("3 : 9");
      assertEquals("3 : 9", IDE.STATUSBAR.getCursorPosition());

      //---- 3 -----------------
      IDE.EDITOR.closeTabIgnoringChanges(2);

      //open new rest service file and check, that cursor doesn't go to position 3 : 9
      IDE.TOOLBAR.runCommandFromNewPopupMenu(MenuCommands.New.REST_SERVICE_FILE);
      IDE.EDITOR.waitActiveFile(PROJECT + "/Untitled file.grs");
      IDE.STATUSBAR.waitCursorPositionControl();
      assertEquals("1 : 1", IDE.STATUSBAR.getCursorPosition());

      IDE.EDITOR.closeFile(FILE_WITH_ERROR);
      IDE.EDITOR.waitTabNotPresent(FILE_WITH_ERROR);
   }

   @Test
   public void testGoToErrorIfFileIsDeleted() throws Exception
   {
      driver.navigate().refresh();
      IDE.LOADER.waitClosed();
      IDE.PROJECT.EXPLORER.waitOpened();
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT);
      IDE.LOADER.waitClosed();

      openAndValidateRestService();
      //check, validation fails
      final String validationMsg = IDE.OUTPUT.getOutputMessage(1);
      assertTrue(validationMsg.contains("validation failed"));

      //close tab
      IDE.EDITOR.closeFile(FILE_WITH_ERROR);
      IDE.EDITOR.waitTabNotPresent(FILE_WITH_ERROR);

      IDE.PROJECT.EXPLORER.selectItem(PROJECT + "/" + FILE_WITH_ERROR);
      IDE.DELETE.deleteSelectedItems();

      //---- 3 -----------------
      //click on validation message to go to error
      IDE.OUTPUT.clickOnErrorMessage(1);

      IDE.WARNING_DIALOG.waitOpened();
      //check, error dialog appeared
      IDE.WARNING_DIALOG.clickOk();
      IDE.WARNING_DIALOG.waitClosed();
      //---- 5 -----------------
      //open new rest service file and check, that cursor doesn't go to position 3 : 9
      IDE.TOOLBAR.runCommandFromNewPopupMenu(MenuCommands.New.REST_SERVICE_FILE);
      IDE.EDITOR.waitActiveFile(PROJECT + "/Untitled file.grs");
      IDE.STATUSBAR.waitCursorPositionControl();
      assertEquals("1 : 1", IDE.STATUSBAR.getCursorPosition());
   }

   @Test
    public void testGoToErrorAfterChangingFile() throws Exception
    {
       driver.navigate().refresh();
       IDE.LOADER.waitClosed();
       IDE.PROJECT.EXPLORER.waitOpened();
       IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + FILE_WITH_ERROR_FOR_CHANGING);
       IDE.LOADER.waitClosed();
       
       IDE.PROJECT.EXPLORER.openItem(PROJECT + "/" + FILE_WITH_ERROR_FOR_CHANGING);
       IDE.EDITOR.waitActiveFile(PROJECT + "/" + FILE_WITH_ERROR_FOR_CHANGING);

       //press validate button
       IDE.TOOLBAR.runCommand(ToolbarCommands.Run.VALIDATE_GROOVY_SERVICE);
       IDE.OUTPUT.waitForMessageShow(1, 5);

       //check, validation fails
       String validationMsg = IDE.OUTPUT.getOutputMessage(1);
       assertTrue(validationMsg.contains("validation failed"));

       //click on validation message to go to error
       IDE.OUTPUT.clickOnErrorMessage(1);
       IDE.STATUSBAR.waitCursorPositionAt("1 : 9");
       
       IDE.EDITOR.moveCursorLeft(0, 6);
       //delete# unnecessary  space
       IDE.EDITOR.typeTextIntoEditor(0, Keys.BACK_SPACE.toString());
       IDE.EDITOR.waitFileContentModificationMark(FILE_WITH_ERROR_FOR_CHANGING);
       
       //press validate button
       IDE.TOOLBAR.runCommand(ToolbarCommands.Run.VALIDATE_GROOVY_SERVICE);
       IDE.OUTPUT.waitForMessageShow(2, 5);
       
       
       //check, validation fails
       validationMsg = IDE.OUTPUT.getOutputMessage(2);
       assertTrue(validationMsg.contains("validation failed"));

       //click on validation message to go to error
       IDE.OUTPUT.clickOnErrorMessage(2);
       IDE.STATUSBAR.waitCursorPositionAt("3 : 3");
       assertEquals("3 : 3", IDE.STATUSBAR.getCursorPosition());

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
       IDE.EDITOR.moveCursorLeft(0, 2);

       assertEquals("1 : 24", IDE.STATUSBAR.getCursorPosition());

       //when 3d line is deleted, try to go to it
       IDE.OUTPUT.clickOnErrorMessage(2);
       assertEquals("1 : 24", IDE.STATUSBAR.getCursorPosition());

    }

   private void openAndValidateRestService() throws Exception
   {
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + FILE_WITH_ERROR);
      IDE.PROJECT.EXPLORER.openItem(PROJECT + "/" + FILE_WITH_ERROR);
      IDE.EDITOR.waitActiveFile(PROJECT + "/" + FILE_WITH_ERROR);

      IDE.TOOLBAR.runCommand(ToolbarCommands.Run.VALIDATE_GROOVY_SERVICE);
      IDE.OUTPUT.waitForMessageShow(1, 5);
   }

   @AfterClass
   public static void tearDown()
   {
      try
      {
         VirtualFileSystemUtils.delete(WS_URL + PROJECT);
      }
      catch (IOException e)
      {
         e.printStackTrace();
      }
   }

}

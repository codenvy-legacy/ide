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
package org.exoplatform.ide.operation.cutcopy;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.exoplatform.common.http.HTTPStatus;
import org.exoplatform.common.http.client.HTTPResponse;
import org.exoplatform.common.http.client.ModuleException;
import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.MenuCommands;
import org.exoplatform.ide.TestConstants;
import org.exoplatform.ide.ToolbarCommands;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.exoplatform.ide.core.Dialogs;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;
import java.util.UUID;

/**
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: $
 */
public class CutFoldersAndFilesTest extends BaseTest
{

   private static final String FOLDER_1 = CutFoldersAndFilesTest.class.getSimpleName() + "-1";
   
   private static final String FOLDER_2 = CutFoldersAndFilesTest.class.getSimpleName() + "-2";
   
   private static final String FOLDER_3 = CutFoldersAndFilesTest.class.getSimpleName() + "-1-1";
   
   private static final String FILE_1 = "gadget.xml";
   
   private static final String FILE_2 = "test.groovy";
   
   private static final String FILE_3 = "gadget1.xml";
   
   private final static String URL = BASE_URL + REST_CONTEXT + "/" + WEBDAV_CONTEXT + "/" + REPO_NAME + "/" + WS_NAME + "/";
   
   private static final String RANDOM_CONTENT_1 = UUID.randomUUID().toString();
   
   private static final String RANDOM_CONTENT_2 = UUID.randomUUID().toString();
   
   private static final String RANDOM_CONTENT_3 = UUID.randomUUID().toString();
   
   /**
    * BeforeClass create such structure:
    * FOLDER_1
    *    FOLDER_3
    *    FILE_1 - file with sample content
    *    FILE_2 - file with sample content
    * FOLDER_2
    *    FOLDER_3
    *    FILE_3 - file with sample content
    */
   @Ignore
   @BeforeClass
   public static void setUp()
   {
      
      try
      {
         VirtualFileSystemUtils.mkcol(URL + FOLDER_1);
         VirtualFileSystemUtils.mkcol(URL + FOLDER_2);
         VirtualFileSystemUtils.mkcol(URL + FOLDER_1 + "/" + FOLDER_3);
         VirtualFileSystemUtils.mkcol(URL + FOLDER_2 + "/" + FOLDER_3);
         VirtualFileSystemUtils.put(RANDOM_CONTENT_1.getBytes(), MimeType.GOOGLE_GADGET, URL + FOLDER_1 + "/" + FILE_1);
         VirtualFileSystemUtils.put(RANDOM_CONTENT_2.getBytes(), MimeType.APPLICATION_GROOVY, URL + FOLDER_1 + "/" + FILE_2);
         VirtualFileSystemUtils.put(RANDOM_CONTENT_3.getBytes(), MimeType.GOOGLE_GADGET, URL + FOLDER_2 + "/" + FILE_3);
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
  
   /**
    *  Test from TestLink IDE-117
    * @throws Exception
    */
   @Ignore
   @Test
   public void testCutOperation() throws Exception
   {
      waitForRootElement();
      IDE.navigator().selectRootOfWorkspace();
      IDE.toolbar().runCommand(ToolbarCommands.File.REFRESH);
      
      IDE.navigator().selectItem(WS_URL + FOLDER_1 + "/"); 
      IDE.toolbar().runCommand(ToolbarCommands.File.REFRESH);
      IDE.navigator().selectItem(WS_URL + FOLDER_2 + "/");
      IDE.toolbar().runCommand(ToolbarCommands.File.REFRESH);
      
      //Open Gadget window, open all created files.
      IDE.navigator().openFileFromNavigationTreeWithCodeEditor(FILE_1, false);

      //Open Gadget window, open all created files.
      IDE.navigator().openFileFromNavigationTreeWithCodeEditor(FILE_2, false);

      IDE.navigator().openFileFromNavigationTreeWithCodeEditor(FILE_3, false);

      //Select file "%FOLDER%-1/gadgetxml", and folder "%FOLDER%-2".
      selenium.controlKeyDown();
      IDE.navigator().selectRow(7);
      IDE.navigator().selectRow(3);
      IDE.navigator().selectRow(5);
      selenium.controlKeyUp();
      Thread.sleep(TestConstants.ANIMATION_PERIOD);

      checkButtonsDisabled();

      IDE.navigator().selectRootOfWorkspace();

      //Select files "test 1/gadgetxml", and "test 2/gadgetxml".
      selenium.controlKeyDown();
      IDE.navigator().selectRow(0);
      IDE.navigator().selectRow(3);
      IDE.navigator().selectRow(7);
      selenium.controlKeyUp();
      Thread.sleep(TestConstants.ANIMATION_PERIOD);

      checkButtonsDisabled();

      IDE.navigator().selectRow(0);
      //Select folders "test 1/test 1.1", and root folder.
      selenium.controlKeyDown();
      Thread.sleep(TestConstants.ANIMATION_PERIOD);
      IDE.navigator().selectRow(2);
      selenium.controlKeyUp();
      Thread.sleep(TestConstants.REDRAW_PERIOD);

      checkButtonsDisabled();

      Thread.sleep(TestConstants.REDRAW_PERIOD);

      IDE.navigator().selectRootOfWorkspace();

      //Select "test 1/gadgetxml", "test 1/test 1.1" items in the Workspace Panel and press the "Cut" toolbar button.
      selenium.controlKeyDown();
      Thread.sleep(TestConstants.ANIMATION_PERIOD);
      IDE.navigator().selectRow(0);
      IDE.navigator().selectRow(3);
      IDE.navigator().selectRow(2);
      selenium.controlKeyUp();
      Thread.sleep(TestConstants.REDRAW_PERIOD);

      IDE.menu().runCommand(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.CUT_MENU);

      checkPasteButton(true);

      IDE.navigator().selectRow(5);

      IDE.menu().runCommand(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.PASTE_MENU);

      assertTrue(selenium.isTextPresent("412 Precondition Failed"));
      assertTrue(selenium.isElementPresent(Dialogs.Locators.SC_WARN_DIALOG));
      assertTrue(selenium.isTextPresent("Precondition Failed"));
      assertTrue(selenium.isElementPresent(Dialogs.Locators.SC_WARN_DIALOG_OK_BTN));
      IDE.dialogs().clickOkButton();

      checkPasteButton(true);

      IDE.navigator().selectRootOfWorkspace();

      selenium.controlKeyDown();
      Thread.sleep(TestConstants.REDRAW_PERIOD);
      //deselect root of navigation tree
      IDE.navigator().selectRow(0);
      IDE.navigator().selectRow(2);
      IDE.navigator().selectRow(3);
      IDE.navigator().selectRow(4);
      selenium.controlKeyUp();
      Thread.sleep(TestConstants.REDRAW_PERIOD);

      IDE.menu().runCommand(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.CUT_MENU);

      IDE.navigator().selectRootOfWorkspace();

      IDE.menu().runCommand(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.PASTE_MENU);

      IDE.editor().selectTab(0);

      assertEquals(RANDOM_CONTENT_1, IDE.editor().getTextFromCodeEditor(0));

      IDE.editor().selectTab(1);

      assertEquals(RANDOM_CONTENT_2, IDE.editor().getTextFromCodeEditor(1));
      
      checkFilesAndFoldersOnServer();

      IDE.navigator().selectRow(1);

      checkPasteButton(false);
   }
   
   private void checkFilesAndFoldersOnServer() throws Exception
   {
      assertEquals(HTTPStatus.OK, VirtualFileSystemUtils.get(URL + FOLDER_1).getStatusCode());
      assertEquals(HTTPStatus.OK, VirtualFileSystemUtils.get(URL + FOLDER_3).getStatusCode());
      assertEquals(HTTPStatus.OK, VirtualFileSystemUtils.get(URL + FOLDER_2).getStatusCode());
      final HTTPResponse fileResponse1 = VirtualFileSystemUtils.get(URL + FILE_1);
      assertEquals(HTTPStatus.OK, fileResponse1.getStatusCode());
      assertEquals(RANDOM_CONTENT_1, new String(fileResponse1.getData()));
      final HTTPResponse fileResponse2 = VirtualFileSystemUtils.get(URL + FILE_2);
      assertEquals(HTTPStatus.OK, fileResponse2.getStatusCode());
      assertEquals(RANDOM_CONTENT_2, new String(fileResponse2.getData()));
      
      //children of FOLDER_2
      assertEquals(HTTPStatus.OK, VirtualFileSystemUtils.get(URL + FOLDER_2 + "/" + FOLDER_3).getStatusCode());
      final HTTPResponse fileResponse3 = VirtualFileSystemUtils.get(URL + FOLDER_2 + "/" + FILE_3);
      assertEquals(HTTPStatus.OK, fileResponse3.getStatusCode());
      assertEquals(RANDOM_CONTENT_3, new String(fileResponse3.getData()));
   }

   /**
    * Check "Paste" buttons state (enabled/disabled).
    * 
    * @throws Exception
    */
   private void checkPasteButton(boolean enabled) throws Exception
   {
      IDE.toolbar().assertButtonEnabled(MenuCommands.Edit.PASTE_TOOLBAR, enabled);
      IDE.menu().checkCommandEnabled(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.PASTE_MENU, enabled);

   }

   /**
    * Check copy/cut/paste buttons are disabled in top menu and on toolbar.
    * 
    * @throws Exception
    */
   private void checkButtonsDisabled() throws Exception
   {
      IDE.toolbar().assertButtonEnabled(MenuCommands.Edit.CUT_TOOLBAR, false);
      IDE.toolbar().assertButtonEnabled(MenuCommands.Edit.COPY_TOOLBAR, false);
      IDE.toolbar().assertButtonEnabled(MenuCommands.Edit.PASTE_TOOLBAR, false);
      IDE.menu().checkCommandEnabled(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.PASTE_MENU, false);
      IDE.menu().checkCommandEnabled(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.CUT_MENU, false);
      IDE.menu().checkCommandEnabled(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.COPY_MENU, false);
   }

   /**
    * Clear test results.
    * 
    * @throws Exception
    */
   @Ignore
   @AfterClass
   public static void tearDown() throws Exception
   {
      try
      {
         VirtualFileSystemUtils.delete(URL + FOLDER_1);
         VirtualFileSystemUtils.delete(URL + FOLDER_2);
         VirtualFileSystemUtils.delete(URL + FOLDER_3);
         VirtualFileSystemUtils.delete(URL + FILE_1);
         VirtualFileSystemUtils.delete(URL + FILE_2);
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

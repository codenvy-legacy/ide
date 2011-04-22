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

import java.io.IOException;
import java.util.UUID;

import org.exoplatform.common.http.HTTPStatus;
import org.exoplatform.common.http.client.HTTPResponse;
import org.exoplatform.common.http.client.ModuleException;
import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.MenuCommands;
import org.exoplatform.ide.TestConstants;
import org.exoplatform.ide.ToolbarCommands;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

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
      IDE.NAVIGATION.selectRootOfWorkspace();
      IDE.TOOLBAR.runCommand(ToolbarCommands.File.REFRESH);
      
      IDE.NAVIGATION.selectItem(WS_URL + FOLDER_1 + "/"); 
      IDE.TOOLBAR.runCommand(ToolbarCommands.File.REFRESH);
      IDE.NAVIGATION.selectItem(WS_URL + FOLDER_2 + "/");
      IDE.TOOLBAR.runCommand(ToolbarCommands.File.REFRESH);
      
      //Open Gadget window, open all created files.
      IDE.NAVIGATION.openFileFromNavigationTreeWithCodeEditor(FILE_1, false);

      //Open Gadget window, open all created files.
      IDE.NAVIGATION.openFileFromNavigationTreeWithCodeEditor(FILE_2, false);

      IDE.NAVIGATION.openFileFromNavigationTreeWithCodeEditor(FILE_3, false);

      //Select file "%FOLDER%-1/gadgetxml", and folder "%FOLDER%-2".
      selenium.controlKeyDown();
      IDE.NAVIGATION.selectRow(7);
      IDE.NAVIGATION.selectRow(3);
      IDE.NAVIGATION.selectRow(5);
      selenium.controlKeyUp();
      Thread.sleep(TestConstants.ANIMATION_PERIOD);

      checkButtonsDisabled();

      IDE.NAVIGATION.selectRootOfWorkspace();

      //Select files "test 1/gadgetxml", and "test 2/gadgetxml".
      selenium.controlKeyDown();
      IDE.NAVIGATION.selectRow(0);
      IDE.NAVIGATION.selectRow(3);
      IDE.NAVIGATION.selectRow(7);
      selenium.controlKeyUp();
      Thread.sleep(TestConstants.ANIMATION_PERIOD);

      checkButtonsDisabled();

      IDE.NAVIGATION.selectRow(0);
      //Select folders "test 1/test 1.1", and root folder.
      selenium.controlKeyDown();
      Thread.sleep(TestConstants.ANIMATION_PERIOD);
      IDE.NAVIGATION.selectRow(2);
      selenium.controlKeyUp();
      Thread.sleep(TestConstants.REDRAW_PERIOD);

      checkButtonsDisabled();

      Thread.sleep(TestConstants.REDRAW_PERIOD);

      IDE.NAVIGATION.selectRootOfWorkspace();

      //Select "test 1/gadgetxml", "test 1/test 1.1" items in the Workspace Panel and press the "Cut" toolbar button.
      selenium.controlKeyDown();
      Thread.sleep(TestConstants.ANIMATION_PERIOD);
      IDE.NAVIGATION.selectRow(0);
      IDE.NAVIGATION.selectRow(3);
      IDE.NAVIGATION.selectRow(2);
      selenium.controlKeyUp();
      Thread.sleep(TestConstants.REDRAW_PERIOD);

      IDE.MENU.runCommand(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.CUT_MENU);

      checkPasteButton(true);

      IDE.NAVIGATION.selectRow(5);

      IDE.MENU.runCommand(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.PASTE_MENU);

      IDE.WARNING_DIALOG.checkIsOpened("412 Precondition Failed");
      IDE.WARNING_DIALOG.clickOk();

      checkPasteButton(true);

      IDE.NAVIGATION.selectRootOfWorkspace();

      selenium.controlKeyDown();
      Thread.sleep(TestConstants.REDRAW_PERIOD);
      //deselect root of navigation tree
      IDE.NAVIGATION.selectRow(0);
      IDE.NAVIGATION.selectRow(2);
      IDE.NAVIGATION.selectRow(3);
      IDE.NAVIGATION.selectRow(4);
      selenium.controlKeyUp();
      Thread.sleep(TestConstants.REDRAW_PERIOD);

      IDE.MENU.runCommand(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.CUT_MENU);

      IDE.NAVIGATION.selectRootOfWorkspace();

      IDE.MENU.runCommand(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.PASTE_MENU);

     IDE.EDITOR.selectTab(0);

      assertEquals(RANDOM_CONTENT_1,IDE.EDITOR.getTextFromCodeEditor(0));

     IDE.EDITOR.selectTab(1);

      assertEquals(RANDOM_CONTENT_2,IDE.EDITOR.getTextFromCodeEditor(1));
      
      checkFilesAndFoldersOnServer();

      IDE.NAVIGATION.selectRow(1);

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
      IDE.TOOLBAR.assertButtonEnabled(MenuCommands.Edit.PASTE_TOOLBAR, enabled);
      IDE.MENU.checkCommandEnabled(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.PASTE_MENU, enabled);

   }

   /**
    * Check copy/cut/paste buttons are disabled in top menu and on toolbar.
    * 
    * @throws Exception
    */
   private void checkButtonsDisabled() throws Exception
   {
      IDE.TOOLBAR.assertButtonEnabled(MenuCommands.Edit.CUT_TOOLBAR, false);
      IDE.TOOLBAR.assertButtonEnabled(MenuCommands.Edit.COPY_TOOLBAR, false);
      IDE.TOOLBAR.assertButtonEnabled(MenuCommands.Edit.PASTE_TOOLBAR, false);
      IDE.MENU.checkCommandEnabled(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.PASTE_MENU, false);
      IDE.MENU.checkCommandEnabled(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.CUT_MENU, false);
      IDE.MENU.checkCommandEnabled(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.COPY_MENU, false);
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

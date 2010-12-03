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
package org.exoplatform.ide.operation.browse.locks;

import static org.junit.Assert.fail;

import java.io.IOException;

import org.exoplatform.common.http.client.ModuleException;
import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.MenuCommands;
import org.exoplatform.ide.TestConstants;
import org.exoplatform.ide.ToolbarCommands;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Check the work of Lock/Unlock feature.
 * 
 * Test is Lock/Unlock button correctly changes state,
 * while changing tabs in editor.
 * 
 * Test is Lick/Unlock button saves its state after refresh.
 * 
 * @author <a href="tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: Sep 21, 2010 $
 *
 */
public class LockFileTest extends LockFileAbstract
{
   private static final String URL = BASE_URL + REST_CONTEXT + "/" + WEBDAV_CONTEXT + "/" + REPO_NAME + "/" + WS_NAME + "/";
   
   private static String FOLDER_NAME;

   private static final String FILE_NAME_1 = "file-" + LockFileTest.class.getSimpleName() + "_1";
   
   private static final String FILE_NAME_2 = "file-" + LockFileTest.class.getSimpleName() + "_2";
   
   @Before
   public void setUp()
   {
      FOLDER_NAME = LockFileTest.class.getSimpleName() + "-" + System.currentTimeMillis();
      try
      {
         VirtualFileSystemUtils.mkcol(URL + FOLDER_NAME);
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
   
   @After
   public void tierDown()
   {
      deleteCookies();
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
   
   @Test
   public void testLockFileManually() throws Exception
   {
      Thread.sleep(TestConstants.SLEEP);
      IDE.toolbar().runCommand(ToolbarCommands.File.REFRESH);
      selectItemInWorkspaceTree(FOLDER_NAME);
      IDE.menu().checkCommandVisibility(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.LOCK_FILE, false);
      
      IDE.toolbar().checkButtonExistAtLeft(ToolbarCommands.Editor.LOCK_FILE, false);

      //----- 1 ------------
      //open new XML file
      IDE.toolbar().runCommandFromNewPopupMenu(MenuCommands.New.XML_FILE);
      
      //check menu and button on toolbar
      IDE.menu().checkCommandVisibility(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.LOCK_FILE, true);
      IDE.menu().checkCommandEnabled(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.LOCK_FILE, false);
      IDE.toolbar().checkButtonExistAtLeft(ToolbarCommands.Editor.LOCK_FILE, true);
      IDE.toolbar().checkButtonEnabled(MenuCommands.Edit.LOCK_FILE, false);
      
      //----- 2 ------------
      //save XML file
      saveAsUsingToolbarButton(FILE_NAME_1);
      IDE.menu().checkCommandEnabled(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.LOCK_FILE, true);
      IDE.toolbar().checkButtonEnabled(MenuCommands.Edit.LOCK_FILE, true);
      checkFileLocking(FILE_NAME_1, false);
      
      //----- 3 ------------
      //lock XML file
      IDE.toolbar().runCommand(ToolbarCommands.Editor.LOCK_FILE);
      
      IDE.menu().checkCommandVisibility(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.UNLOCK_FILE, true);
      IDE.menu().checkCommandEnabled(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.UNLOCK_FILE, true);
      IDE.toolbar().checkButtonExistAtLeft(ToolbarCommands.Editor.UNLOCK_FILE, true);
      IDE.toolbar().checkButtonEnabled(MenuCommands.Edit.UNLOCK_FILE, true);
      
      checkFileLocking(FILE_NAME_1, false);
      
      //----- 4 ------------
      //open new HTML file
      IDE.toolbar().runCommandFromNewPopupMenu(MenuCommands.New.HTML_FILE);
      
      IDE.menu().checkCommandVisibility(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.LOCK_FILE, true);
      IDE.menu().checkCommandEnabled(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.LOCK_FILE, false);
      IDE.toolbar().checkButtonExistAtLeft(ToolbarCommands.Editor.LOCK_FILE, true);
      IDE.toolbar().checkButtonEnabled(MenuCommands.Edit.LOCK_FILE, false);
      
      //----- 5 ------------
      //select XML file tab
      IDE.editor().selectTab(0);
      
      IDE.menu().checkCommandVisibility(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.UNLOCK_FILE, true);
      IDE.menu().checkCommandEnabled(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.UNLOCK_FILE, true);
      IDE.toolbar().checkButtonExistAtLeft(ToolbarCommands.Editor.UNLOCK_FILE, true);
      IDE.toolbar().checkButtonEnabled(MenuCommands.Edit.UNLOCK_FILE, true);
      
      //----- 6 ------------
      //unlock XML file
      IDE.toolbar().runCommand(ToolbarCommands.Editor.UNLOCK_FILE);
      
      IDE.menu().checkCommandEnabled(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.LOCK_FILE, true);
      IDE.toolbar().checkButtonEnabled(MenuCommands.Edit.LOCK_FILE, true);
      
      //----- 7 ------------
      //select HTML file, save file, lock
      IDE.editor().selectTab(1);
      saveAsUsingToolbarButton(FILE_NAME_2);
      IDE.toolbar().runCommand(ToolbarCommands.Editor.LOCK_FILE);
      
      IDE.menu().checkCommandEnabled(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.UNLOCK_FILE, true);
      IDE.toolbar().checkButtonEnabled(MenuCommands.Edit.UNLOCK_FILE, true);
      
      //----- 8 ------------
      //close HTML file, open and check, that file is unlocked
      IDE.editor().closeTab(1);
      checkIsTabPresentInEditorTabset(FILE_NAME_2, false);
      openFileFromNavigationTreeWithCodeEditor(FILE_NAME_2, false);
      
      IDE.menu().checkCommandVisibility(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.LOCK_FILE, true);
      IDE.menu().checkCommandEnabled(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.LOCK_FILE, true);
      IDE.toolbar().checkButtonExistAtLeft(ToolbarCommands.Editor.LOCK_FILE, true);
      IDE.toolbar().checkButtonEnabled(MenuCommands.Edit.LOCK_FILE, true);
      
      //----- 9 ------------
      //lock file
      IDE.toolbar().runCommand(ToolbarCommands.Editor.LOCK_FILE);
      
      IDE.menu().checkCommandEnabled(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.UNLOCK_FILE, true);
      IDE.toolbar().checkButtonEnabled(MenuCommands.Edit.UNLOCK_FILE, true);
      
      //----- 10 ------------
      //create new file and close it
      IDE.toolbar().runCommandFromNewPopupMenu(MenuCommands.New.GROOVY_TEMPLATE_FILE);
      
      //check menu and button on toolbar
      IDE.menu().checkCommandVisibility(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.LOCK_FILE, true);
      IDE.menu().checkCommandEnabled(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.LOCK_FILE, false);
      IDE.toolbar().checkButtonExistAtLeft(ToolbarCommands.Editor.LOCK_FILE, true);
      IDE.toolbar().checkButtonEnabled(MenuCommands.Edit.LOCK_FILE, false);
      
      IDE.editor().closeUnsavedFileAndDoNotSave(2);
      Thread.sleep(TestConstants.SLEEP);
      
      //----- 11 ------------
      //check, that HTML file is locked
//      checkIsEditorTabSelected(FILE_NAME_2, true);
      IDE.menu().checkCommandEnabled(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.UNLOCK_FILE, true);
      IDE.toolbar().checkButtonEnabled(MenuCommands.Edit.UNLOCK_FILE, true);
      
      //----- 12 ------------
      //check XML file is unlocked
      IDE.editor().selectTab(0);
      IDE.menu().checkCommandEnabled(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.LOCK_FILE, true);
      IDE.toolbar().checkButtonEnabled(MenuCommands.Edit.LOCK_FILE, true);
   }
   
   @Test
   public void testLockFileStaysAfterRefresh() throws Exception
   {
      createFileViaWebDav(FILE_NAME_1);
      createFileViaWebDav(FILE_NAME_2);
      refresh();
      
      IDE.toolbar().runCommand(ToolbarCommands.File.REFRESH);
      selectItemInWorkspaceTree(FOLDER_NAME);
      IDE.toolbar().runCommand(ToolbarCommands.File.REFRESH);
      
      //----- 1 ------------
      //open files
      openFileFromNavigationTreeWithCodeEditor(FILE_NAME_1, false);
      IDE.menu().checkCommandEnabled(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.LOCK_FILE, true);
      IDE.toolbar().checkButtonEnabled(ToolbarCommands.Editor.LOCK_FILE, true);
      
      openFileFromNavigationTreeWithCodeEditor(FILE_NAME_2, false);
      
      IDE.editor().selectTab(0);
      
      //----- 2 ------------
      //lock file
      IDE.toolbar().runCommand(ToolbarCommands.Editor.LOCK_FILE);
      
      IDE.menu().checkCommandEnabled(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.UNLOCK_FILE, true);
      IDE.toolbar().checkButtonEnabled(ToolbarCommands.Editor.UNLOCK_FILE, true);
      
      //----- 3 ------------
      //refresh IDE
      refresh();
      
      Thread.sleep(TestConstants.SLEEP);
      
      checkIsEditorTabSelected(FILE_NAME_1, true);
      IDE.menu().checkCommandEnabled(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.UNLOCK_FILE, true);
      IDE.toolbar().checkButtonEnabled(ToolbarCommands.Editor.UNLOCK_FILE, true);
      
      //----- 4 ------------
      //select second tab and check, that file is not locked
      IDE.editor().selectTab(1);
      IDE.menu().checkCommandEnabled(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.LOCK_FILE, true);
      IDE.toolbar().checkButtonEnabled(ToolbarCommands.Editor.LOCK_FILE, true);
   }
   
   private void createFileViaWebDav(String fileName)
   {
      final String filePath = "src/test/resources/org/exoplatform/ide/operation/browse/locks/test.html";
      try
      {
         VirtualFileSystemUtils.put(filePath, MimeType.TEXT_HTML, URL + FOLDER_NAME + "/" + fileName);
      }
      catch (IOException e)
      {
         e.printStackTrace();
         fail("Can't put file to webdav");
      }
      catch (ModuleException e)
      {
         e.printStackTrace();
         fail("Can't put file to webdav");
      }
   }

}

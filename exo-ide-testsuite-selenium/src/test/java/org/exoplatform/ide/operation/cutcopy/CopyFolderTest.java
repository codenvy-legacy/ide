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

import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.MenuCommands;
import org.exoplatform.ide.ToolbarCommands;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * IDE-116:Copy folder.
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */
public class CopyFolderTest extends BaseTest
{

   private static final String FILE_1 = "test"; 
   
   private final static String FOLDER_1 = "Test 1";

   private final static String FOLDER_1_1 = "Test 1.1";
   
   private static final String FILE_CONTENT_1 = "file content";
   
   /**
    * BeforeClass create such structure:
    * FOLDER_1
    *    FOLDER_1_1
    *       FILE_GROOVY - file with sample content
    */
   @BeforeClass
   public static void setUp()
   {
      try
      {
         VirtualFileSystemUtils.mkcol(WS_URL + FOLDER_1);
         VirtualFileSystemUtils.mkcol(WS_URL + FOLDER_1 + "/" +FOLDER_1_1);
         VirtualFileSystemUtils.put(FILE_CONTENT_1.getBytes(), MimeType.APPLICATION_GROOVY, WS_URL + FOLDER_1 + "/"  + FOLDER_1_1 + "/" + FILE_1);
      }
      catch (Exception e)
      {
         e.printStackTrace();
      }
   }
   
   @AfterClass
   public static void tearDown()
   {
      try
      {
         VirtualFileSystemUtils.delete(WS_URL +FOLDER_1);
         VirtualFileSystemUtils.delete(WS_URL +FOLDER_1_1);
      }
      catch (Exception e)
      {
         e.printStackTrace();
      }
   }
   
   
   /*
    * Create folder "/Test 1"
    * Create folder "/Test 1/Test 1.1" 
    * Create new groovy script
    * 
    * Type "groovy file content"
    * Save file as "/Test 1/Test 1.1/test.groovy"
    * 
    * Select folder "/Test 1/Test 1.1"
    * 
    * Check Paste must be disabled
    * Check Copy must be enabled
    * 
    * Call "Edit/Copy" in menu
    * 
    * Check Paste must be enabled
    * 
    * Select root in workspace tree and call "Edit/Paste"
    * 
    * Edit currently opened file
    * Call "Ctrl+S"
    * Close opened file
    * 
    * Open "/Test 1.1/test.groovy"
    * Check it content
    * 
    */
   @Test
   public void copyOperationTestIde116() throws Exception
   {
      waitForRootElement();
     
      IDE.NAVIGATION.selectItem(WS_URL + FOLDER_1 + "/");
      IDE.TOOLBAR.runCommand(ToolbarCommands.File.REFRESH);
      
      IDE.NAVIGATION.selectItem(WS_URL + FOLDER_1 + "/" + FOLDER_1_1 + "/");
      IDE.TOOLBAR.runCommand(ToolbarCommands.File.REFRESH);
      
      IDE.NAVIGATION.openFileFromNavigationTreeWithCodeEditor(WS_URL + FOLDER_1 + "/" + FOLDER_1_1 + "/" + FILE_1, false);

      /* 
      * Select folder "/Test 1/Test 1.1"
      */
      IDE.NAVIGATION.selectItem(WS_URL + FOLDER_1 + "/" + FOLDER_1_1 + "/");

      /*
       * Check Copy must be enabled
       */
      IDE.MENU.checkCommandEnabled(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.COPY_MENU, true);
      IDE.TOOLBAR.assertButtonExistAtLeft(MenuCommands.Edit.COPY_TOOLBAR, true);
      IDE.TOOLBAR.assertButtonEnabled(MenuCommands.Edit.COPY_TOOLBAR, true);

      /* 
       * Check Paste must be disabled
       */
      IDE.MENU.checkCommandEnabled(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.PASTE_MENU, false);
      IDE.TOOLBAR.assertButtonExistAtLeft(MenuCommands.Edit.PASTE_TOOLBAR, true);
      IDE.TOOLBAR.assertButtonEnabled(MenuCommands.Edit.PASTE_TOOLBAR, false);

      /* 
       * Call "Edit/Copy" in menu
       */
      IDE.MENU.runCommand(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.COPY_MENU);

      /* 
       * Check Paste must be enabled
       */
      IDE.MENU.checkCommandEnabled(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.PASTE_MENU, true);
      IDE.TOOLBAR.assertButtonEnabled(MenuCommands.Edit.PASTE_TOOLBAR, true);

      /* 
       * Select root in workspace tree and call "Edit/Paste"
       */
      IDE.NAVIGATION.selectRootOfWorkspace();
      IDE.MENU.runCommand(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.PASTE_MENU);
      
      /*
       * Check new folder appeared in root folder
       */
      IDE.NAVIGATION.selectRootOfWorkspace();
      IDE.TOOLBAR.runCommand(ToolbarCommands.File.REFRESH);
      
      IDE.NAVIGATION.assertItemVisible(WS_URL + FOLDER_1 + "/");      
      IDE.NAVIGATION.assertItemVisible(WS_URL + FOLDER_1_1 + "/");

      /*
       * Change text in file.
       */
     IDE.EDITOR.typeTextIntoEditor(0, "updated");

      saveCurrentFile();

      /* 
       * Close opened file
       */
     IDE.EDITOR.closeTab(0);
      
      /* 
       * Open "/Test 1.1/test.groovy"
       */
      IDE.NAVIGATION.selectRootOfWorkspace();
      IDE.TOOLBAR.runCommand(ToolbarCommands.File.REFRESH);

      IDE.NAVIGATION.selectItem(WS_URL + FOLDER_1_1 + "/");
      IDE.TOOLBAR.runCommand(ToolbarCommands.File.REFRESH);

      IDE.NAVIGATION.openFileFromNavigationTreeWithCodeEditor(WS_URL + FOLDER_1_1 + "/" + FILE_1, false);

      /*
       * Check file content
       */
      assertEquals(FILE_CONTENT_1,IDE.EDITOR.getTextFromCodeEditor(0));
   }

}

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
 * IDE-115:Copy file.
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */
public class CopyFileTest extends BaseTest
{
   
   private static final String FOLDER_1 = CopyFileTest.class.getSimpleName() + "-1";
   
   private static final String FILE_GROOVY = "test.groovy";
   
   private static final String FILE_CONTENT_1 = "world";
   
   private static final String FILE_CONTENT_2 = "hello ";
   
   /**
    * BeforeClass create such structure:
    * FOLDER_1
    *    FILE_GROOVY - file with sample content
    */
   @BeforeClass
   public static void setUp()
   {
      try
      {
         VirtualFileSystemUtils.mkcol(WS_URL + FOLDER_1);
         VirtualFileSystemUtils.put(FILE_CONTENT_1.getBytes(), MimeType.APPLICATION_GROOVY, WS_URL + FOLDER_1 + "/" + FILE_GROOVY);
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
         VirtualFileSystemUtils.delete(WS_URL + FOLDER_1);
         VirtualFileSystemUtils.delete(WS_URL + FILE_GROOVY);
      }
      catch (Exception e)
      {
         e.printStackTrace();
      }
   }

   /*
    * Create folder "Test 1" in root. After this folder "Test 1" must be selected.
    * Create Groovy Script file
    * Type "hello"
    * Save as "test.groovy"
    * Close editor
    * Select "/Test1/test.groovy" file
    * 
    * Check Cut and Copy commands must be enabled
    * Check Paste command must be disabled
    * Click Copy command on toolbar
    * 
    * Check Paste must be enabled 
    * 
    * Select Root in workspace panel
    * Click Paste command
    * 
    * Check Paste command must be disabled
    * 
    * Open "Test 1" folder
    * Open file "/Test 1/test.groovy"
    * Type "file content"
    * Call "Ctrl+S"
    * Close file 
    * 
    * Open file "/Test 1/test.groovy"
    * 
    * Open "/test.groovy"
    * Check content of the file must be "hello"
    * 
    * Close both files
    * 
    * Delete files
    * 
    */

   @Test
   public void testCopyFile() throws Exception
   {
      waitForRootElement();
      
      IDE.NAVIGATION.selectItem(WS_URL + FOLDER_1 + "/");
      
      IDE.TOOLBAR.runCommand(ToolbarCommands.File.REFRESH);
      IDE.NAVIGATION.selectItem(WS_URL + FOLDER_1 + "/" + FILE_GROOVY); 

      /*
       * Check Cut and Copy commands must be enabled
       */
      IDE.MENU.checkCommandEnabled(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.COPY_MENU, true);
      IDE.TOOLBAR.assertButtonExistAtLeft(MenuCommands.Edit.COPY_TOOLBAR, true);
      IDE.TOOLBAR.assertButtonEnabled(MenuCommands.Edit.COPY_TOOLBAR, true);

      IDE.MENU.checkCommandEnabled(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.CUT_MENU, true);
      IDE.TOOLBAR.assertButtonExistAtLeft(MenuCommands.Edit.CUT_TOOLBAR, true);
      IDE.TOOLBAR.assertButtonEnabled(MenuCommands.Edit.CUT_TOOLBAR, true);

      /*
       * Check Paste command must be disabled
       */
      IDE.MENU.checkCommandEnabled(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.PASTE_MENU, false);
      IDE.TOOLBAR.assertButtonExistAtLeft(MenuCommands.Edit.PASTE_TOOLBAR, true);
      IDE.TOOLBAR.assertButtonEnabled(MenuCommands.Edit.PASTE_TOOLBAR, false);

      /*
       * Click Copy command on toolbar
       */
      IDE.TOOLBAR.runCommand(MenuCommands.Edit.COPY_TOOLBAR);

      /*
       * Check Paste must be enabled 
       */
      IDE.MENU.checkCommandEnabled(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.PASTE_MENU, true);
      IDE.TOOLBAR.assertButtonEnabled(MenuCommands.Edit.PASTE_TOOLBAR, true);

      /*
       * Select Root in workspace panel
       */
      IDE.NAVIGATION.selectRootOfWorkspace();

      /*
       * Click Paste command
       */
      IDE.TOOLBAR.runCommand(MenuCommands.Edit.PASTE_TOOLBAR);

      /*
       * Check Paste command must be disabled
       */
      IDE.MENU.checkCommandEnabled(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.PASTE_MENU, false);
      IDE.TOOLBAR.assertButtonEnabled(MenuCommands.Edit.PASTE_TOOLBAR, false);

      /*
       * Open "Test 1" folder
       */
      IDE.NAVIGATION.selectItem(WS_URL + FOLDER_1 + "/");

      /*
       * Open file "/Test 1/test.groovy"
       */
      IDE.NAVIGATION.openFileFromNavigationTreeWithCodeEditor(WS_URL + FILE_GROOVY, false);

      /*
       * Type "file content"
       */
     IDE.EDITOR.typeTextIntoEditor(0, FILE_CONTENT_2);

      /*
       * Save file
       */
      saveCurrentFile();

      /*
       * Close file
       */
     IDE.EDITOR.closeTab(0);

      /*
       * Open "/test.groovy"
       */
      IDE.NAVIGATION.selectRootOfWorkspace();
      IDE.TOOLBAR.runCommand(MenuCommands.File.REFRESH_TOOLBAR);
      IDE.NAVIGATION.selectItem(WS_URL + FOLDER_1 + "/");
      IDE.TOOLBAR.runCommand(MenuCommands.File.REFRESH_TOOLBAR);
      
      /*
       * Select FILE_GROOVY file in FOLDER_1
       */
      IDE.NAVIGATION.selectItem(WS_URL + FOLDER_1 + "/" + FILE_GROOVY); 
      
      /*
       * Open FILE_GROOVY file from FOLDER_1
       */
      IDE.NAVIGATION.openSelectedFileWithCodeEditor(false);
      
      /*
       * Select FILE_GROOVY file in root folder
       */
      IDE.NAVIGATION.selectItem(WS_URL + FILE_GROOVY);
      
      /*
       * Open FILE_GROOVY file from root folder
       */
      IDE.NAVIGATION.openSelectedFileWithCodeEditor(false);

      /*
       * Check files content
       */
      assertEquals(FILE_CONTENT_1,IDE.EDITOR.getTextFromCodeEditor(0));
      assertEquals(FILE_CONTENT_2 + FILE_CONTENT_1,IDE.EDITOR.getTextFromCodeEditor(1));

   }

}

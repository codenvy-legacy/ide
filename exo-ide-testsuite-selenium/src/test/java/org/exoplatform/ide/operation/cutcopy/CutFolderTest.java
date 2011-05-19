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

import org.exoplatform.gwtframework.commons.rest.HTTPStatus;
import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.MenuCommands;
import org.exoplatform.ide.ToolbarCommands;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * IDE-112 : Cut folder
 * 
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: $
 *
 */
public class CutFolderTest extends BaseTest
{

   private static final String FILE_CONTENT = "file content";

   private static final String CHANGED_FILE_CONTENT = "changed ";

   /**
    * Create next folders' structure in the workspace root:
    * folder 1/
    *    folder 2/
    *        file.groovy - file with sample content  
    * folder 2/
    */
   @BeforeClass
   public static void setUp()
   {
      try
      {
         VirtualFileSystemUtils.mkcol(WS_URL + "folder 1");
         VirtualFileSystemUtils.mkcol(WS_URL + "folder 1/folder 2");
         VirtualFileSystemUtils.put(FILE_CONTENT.getBytes(), MimeType.APPLICATION_GROOVY, WS_URL
            + "folder 1/folder 2/file.groovy");
         VirtualFileSystemUtils.mkcol(WS_URL + "folder 2");
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
         VirtualFileSystemUtils.delete(WS_URL + "folder 1");
         VirtualFileSystemUtils.delete(WS_URL + "folder 2");
      }
      catch (Exception e)
      {
         e.printStackTrace();
      }
   }

   @Test
   public void testCutFolderOperation() throws Exception
   {
      waitForRootElement();
      /*
       * 1. Check, that "/folder 1", "/folder 2", "/folder 1/folder 2", "/folder 1/folder 2/file.groovy" are presents
       */
      IDE.WORKSPACE.selectRootItem();
      IDE.TOOLBAR.runCommand(ToolbarCommands.File.REFRESH);
      IDE.NAVIGATION.assertItemVisible(WS_URL + "folder 1/");
      IDE.NAVIGATION.assertItemVisible(WS_URL + "folder 2/");

      IDE.WORKSPACE.selectItem(WS_URL + "folder 1/");
      IDE.TOOLBAR.runCommand(ToolbarCommands.File.REFRESH);
      IDE.NAVIGATION.assertItemVisible(WS_URL + "folder 1/folder 2/");

      IDE.WORKSPACE.selectItem(WS_URL + "folder 1/folder 2/");
      IDE.TOOLBAR.runCommand(ToolbarCommands.File.REFRESH);
      IDE.NAVIGATION.assertItemVisible(WS_URL + "folder 1/folder 2/file.groovy");

      /*
       * 2.Open file "test 1/test 2/test.groovy".
       */
      IDE.NAVIGATION.openFileFromNavigationTreeWithCodeEditor(WS_URL + "folder 1/folder 2/file.groovy", false);

      /*
       * Paste commands are disabled, Cut/Copy are enabled
       */
      IDE.TOOLBAR.assertButtonEnabled(MenuCommands.Edit.PASTE_TOOLBAR, false);
      IDE.TOOLBAR.assertButtonEnabled(MenuCommands.Edit.CUT_TOOLBAR, true);
      IDE.TOOLBAR.assertButtonEnabled(MenuCommands.Edit.COPY_TOOLBAR, true);

      IDE.MENU.checkCommandEnabled(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.PASTE_MENU, false);
      IDE.MENU.checkCommandEnabled(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.CUT_MENU, true);
      IDE.MENU.checkCommandEnabled(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.COPY_MENU, true);

      /*
       * 3. Select folder "folder 1/folder 1.1". Click on "Cut" toolbar button.
       */
      IDE.WORKSPACE.selectItem(WS_URL + "folder 1/folder 2/");
      IDE.TOOLBAR.runCommand(MenuCommands.Edit.CUT_TOOLBAR);

      /*
       * Paste commands are enabled.
       */
      IDE.TOOLBAR.assertButtonEnabled(MenuCommands.Edit.PASTE_TOOLBAR, true);
      IDE.MENU.checkCommandEnabled(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.PASTE_MENU, true);

      /*
       * 4. Select file "folder 1/folder 1.1/test.groovy" in the Workspace Panel.
       */
      IDE.WORKSPACE.selectItem(WS_URL + "folder 1/folder 2/file.groovy");

      /*
       * Paste commands are enabled.
       */
      IDE.MENU.checkCommandEnabled(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.PASTE_MENU, true);
      IDE.TOOLBAR.assertButtonEnabled(MenuCommands.Edit.PASTE_TOOLBAR, true);

      /*
       * 5. Select folder "folder 1/folder 1.1/" and click on "Paste" toolbar button.
       */
      IDE.WORKSPACE.selectItem(WS_URL + "folder 1/folder 2/");
      IDE.TOOLBAR.runCommand(MenuCommands.Edit.PASTE_TOOLBAR);

      /*
       * Error message about impossibility to paste folder into the itself should be displayed. 
       */
      IDE.WARNING_DIALOG.checkIsOpened("Can't move items in the same directory!");
      IDE.WARNING_DIALOG.clickOk();

      /*
       * After closing error message dialog the toolbar button "Paste" and topmenu command "Edit->Paste Items" 
       * should be still enabled.
       */
      IDE.MENU.checkCommandEnabled(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.PASTE_MENU, true);
      IDE.TOOLBAR.assertButtonEnabled(MenuCommands.Edit.PASTE_TOOLBAR, true);

      /*
       * 6. Select root item and then click on "Paste" toolbar button.
       */
      IDE.WORKSPACE.selectRootItem();
      IDE.TOOLBAR.runCommand(MenuCommands.Edit.PASTE_TOOLBAR);

      /*
       * Error message about impossibility to paste folder with the existed name should be displayed. 
       */
      IDE.WARNING_DIALOG.checkIsOpened();
      IDE.WARNING_DIALOG.clickOk();

      /*
       * After closing error message dialog the toolbar button "Paste" and topmenu command "Edit->Paste Items" 
       * should be still enabled.
       */
      IDE.MENU.checkCommandEnabled(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.PASTE_MENU, true);
      IDE.TOOLBAR.assertButtonEnabled(MenuCommands.Edit.PASTE_TOOLBAR, true);

      /*
       * 7. Select "test 2" item and then select "Edit->Paste Items" topmenu command.
       */
      IDE.WORKSPACE.selectItem(WS_URL + "folder 2/");
      IDE.MENU.runCommand(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.PASTE_MENU);

      /*
       * Check, that file name stays the same (IDE-225 issue).
       */
      assertEquals("file.groovy",IDE.EDITOR.getTabTitle(0));
      IDE.NAVIGATION.assertItemNotVisible(WS_URL + "folder 1/folder 2/");
      checkItemsOnWebDav();

      /*
       * 10. Change content of opened file "file.groovy", 
       * save file, close file tab and open file "folder 2/folder 2/file.groovy".
       */
     IDE.EDITOR.typeTextIntoEditor(0, CHANGED_FILE_CONTENT);
      saveCurrentFile();
     IDE.EDITOR.closeFile(0);

      IDE.WORKSPACE.selectItem(WS_URL + "folder 2/folder 2/");
      IDE.TOOLBAR.runCommand(ToolbarCommands.File.REFRESH);

      IDE.NAVIGATION.openFileFromNavigationTreeWithCodeEditor(WS_URL + "folder 2/folder 2/file.groovy", false);
     IDE.EDITOR.checkIsTabPresentInEditorTabset("file.groovy", true);
      assertEquals(CHANGED_FILE_CONTENT + FILE_CONTENT,IDE.EDITOR.getTextFromCodeEditor(0));

      IDE.NAVIGATION.assertItemVisible(WS_URL + "folder 1/");
      IDE.NAVIGATION.assertItemVisible(WS_URL + "folder 2/");
      IDE.NAVIGATION.assertItemVisible(WS_URL + "folder 2/folder 2/");
      IDE.NAVIGATION.assertItemVisible(WS_URL + "folder 2/folder 2/file.groovy");

      /*
       * check there is no "folder 1/folder 2" in the tree
       */
      IDE.NAVIGATION.assertItemNotVisible(WS_URL + "folder 1/folder 2/");

      /*
       * close editor
       */
     IDE.EDITOR.closeFile(0);
   }

   /**
    * Check, that FOLDER_1,  
    * FOLDER_2, FOLDER_2/FOLDER2, FOLDER_2/FOLDER_2/FILE_1
    * are present on webdav.
    * 
    * And FOLDER_1/FOLDER_2 are not present.
    * 
    * @throws Exception
    */
   private void checkItemsOnWebDav() throws Exception
   {
      assertEquals(200, VirtualFileSystemUtils.get(WS_URL + "folder 1").getStatusCode());
      assertEquals(HTTPStatus.NOT_FOUND, VirtualFileSystemUtils.get(WS_URL + "folder 1/folder 2").getStatusCode());
      assertEquals(200, VirtualFileSystemUtils.get(WS_URL + "folder 2").getStatusCode());
      assertEquals(200, VirtualFileSystemUtils.get(WS_URL + "folder 2/folder 2").getStatusCode());
      assertEquals(200, VirtualFileSystemUtils.get(WS_URL + "folder 2/folder 2/file.groovy").getStatusCode());
   }

}

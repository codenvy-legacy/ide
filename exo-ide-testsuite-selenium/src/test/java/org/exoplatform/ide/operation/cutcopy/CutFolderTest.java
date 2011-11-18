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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

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
   private static final String PROJECT = CutFolderTest.class.getSimpleName();

   private static final String FILE_CONTENT = "file content";

   private static final String FOLDER1 = "folder1";

   private static final String FOLDER2 = "folder2";

   private static final String FILE = "file.groovy";

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
         VirtualFileSystemUtils.createDefaultProject(PROJECT);
         VirtualFileSystemUtils.mkcol(WS_URL + PROJECT + "/" + FOLDER1);
         VirtualFileSystemUtils.mkcol(WS_URL + PROJECT + "/" + FOLDER1 + "/" + FOLDER2);
         VirtualFileSystemUtils.put(FILE_CONTENT.getBytes(), MimeType.APPLICATION_GROOVY, WS_URL + PROJECT + "/"
            + FOLDER1 + "/" + FOLDER2 + "/" + FILE);
         VirtualFileSystemUtils.mkcol(WS_URL + PROJECT + "/" + FOLDER2);
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
         VirtualFileSystemUtils.delete(WS_URL + PROJECT);
      }
      catch (Exception e)
      {
         e.printStackTrace();
      }
   }

   @Test
   public void testCutFolderOperation() throws Exception
   {
      IDE.PROJECT.EXPLORER.waitOpened();
      IDE.PROJECT.OPEN.openProject(PROJECT);
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + FOLDER1);

      //Open folder 1
      IDE.PROJECT.EXPLORER.openItem(PROJECT + "/" + FOLDER1);
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + FOLDER1 + "/" + FOLDER2);

      //Open folder 2
      IDE.PROJECT.EXPLORER.openItem(PROJECT + "/" + FOLDER1 + "/" + FOLDER2);
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + FOLDER1 + "/" + FOLDER2 + "/" + FILE);

      //Paste commands are disabled, Cut/Copy are enabled
      assertFalse(IDE.TOOLBAR.isButtonEnabled(ToolbarCommands.File.PASTE));
      assertTrue(IDE.TOOLBAR.isButtonEnabled(MenuCommands.Edit.CUT_TOOLBAR));
      assertTrue(IDE.TOOLBAR.isButtonEnabled(MenuCommands.Edit.COPY_TOOLBAR));

      assertFalse(IDE.MENU.isCommandEnabled(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.PASTE_MENU));
      assertTrue(IDE.MENU.isCommandEnabled(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.CUT_MENU));
      assertTrue(IDE.MENU.isCommandEnabled(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.COPY_MENU));

      IDE.PROJECT.EXPLORER.selectItem(PROJECT + "/" + FOLDER1 + "/" + FOLDER2);
      IDE.TOOLBAR.runCommand(MenuCommands.Edit.CUT_TOOLBAR);

      //Paste commands are enabled.
      IDE.TOOLBAR.waitForButtonEnabled(ToolbarCommands.File.PASTE, true);
      assertTrue(IDE.TOOLBAR.isButtonEnabled(ToolbarCommands.File.PASTE));
      assertTrue(IDE.MENU.isCommandEnabled(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.PASTE_MENU));

      IDE.PROJECT.EXPLORER.selectItem(PROJECT + "/" + FOLDER1 + "/" + FOLDER2 + "/" + FILE);
      assertTrue(IDE.TOOLBAR.isButtonEnabled(ToolbarCommands.File.PASTE));
      assertTrue(IDE.MENU.isCommandEnabled(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.PASTE_MENU));

      //Paste in the same folder:
      IDE.PROJECT.EXPLORER.selectItem(PROJECT + "/" + FOLDER1);
      IDE.TOOLBAR.runCommand(ToolbarCommands.File.PASTE);

      IDE.WARNING_DIALOG.waitOpened();
      assertEquals(CutFileTest.WRONG_DESTINATION, IDE.WARNING_DIALOG.getWarningMessage());
      IDE.WARNING_DIALOG.clickOk();
      IDE.WARNING_DIALOG.waitClosed();

      assertTrue(IDE.TOOLBAR.isButtonEnabled(ToolbarCommands.File.PASTE));
      assertTrue(IDE.MENU.isCommandEnabled(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.PASTE_MENU));

      //Paste in folder, which contains with the same name:
      IDE.PROJECT.EXPLORER.selectItem(PROJECT);
      IDE.TOOLBAR.runCommand(MenuCommands.Edit.PASTE_TOOLBAR);
      IDE.WARNING_DIALOG.waitOpened();
      assertEquals(CutFileTest.WRONG_DESTINATION, IDE.WARNING_DIALOG.getWarningMessage());
      IDE.WARNING_DIALOG.clickOk();
      IDE.WARNING_DIALOG.waitClosed();

      assertTrue(IDE.TOOLBAR.isButtonEnabled(ToolbarCommands.File.PASTE));
      assertTrue(IDE.MENU.isCommandEnabled(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.PASTE_MENU));

      IDE.PROJECT.EXPLORER.selectItem(PROJECT + "/" + FOLDER2);
      IDE.MENU.runCommand(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.PASTE_MENU);

      checkItemsOnWebDav();

      IDE.PROJECT.EXPLORER.openItem(PROJECT + "/" + FOLDER2 + "/" + FOLDER2);
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + FOLDER2 + "/" + FOLDER2 + "/" + FILE);

      IDE.PROJECT.EXPLORER.openItem(PROJECT + "/" + FOLDER2 + "/" + FOLDER2 + "/" + FILE);
      IDE.EDITOR.waitActiveFile(PROJECT + "/" + FOLDER2 + "/" + FOLDER2 + "/" + FILE);
      assertEquals(FILE_CONTENT, IDE.EDITOR.getTextFromCodeEditor(0));

      assertTrue(IDE.PROJECT.EXPLORER.isItemVisible(PROJECT + "/" + FOLDER1));
      assertTrue(IDE.PROJECT.EXPLORER.isItemVisible(PROJECT + "/" + FOLDER2));
      assertTrue(IDE.PROJECT.EXPLORER.isItemVisible(PROJECT + "/" + FOLDER2 + "/" + FOLDER2));
      assertTrue(IDE.PROJECT.EXPLORER.isItemVisible(PROJECT + "/" + FOLDER2 + "/" + FOLDER2 + "/" + FILE));
      assertFalse(IDE.PROJECT.EXPLORER.isItemPresent(PROJECT + "/" + FOLDER1 + "/" + FOLDER2));
      IDE.EDITOR.closeFile(1);
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
      assertEquals(200, VirtualFileSystemUtils.get(WS_URL + PROJECT + "/" + FOLDER1).getStatusCode());
      assertEquals(404, VirtualFileSystemUtils.get(WS_URL + PROJECT + "/" + FOLDER1 + "/" + FOLDER2).getStatusCode());
      assertEquals(200, VirtualFileSystemUtils.get(WS_URL + PROJECT + "/" + FOLDER2).getStatusCode());
      assertEquals(200, VirtualFileSystemUtils.get(WS_URL + PROJECT + "/" + FOLDER2 + "/" + FOLDER2).getStatusCode());
      assertEquals(200, VirtualFileSystemUtils.get(WS_URL + PROJECT + "/" + FOLDER2 + "/" + FOLDER2 + "/" + FILE)
         .getStatusCode());
   }

}

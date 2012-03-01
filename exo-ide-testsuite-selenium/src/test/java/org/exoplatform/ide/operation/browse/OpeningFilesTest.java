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
package org.exoplatform.ide.operation.browse;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.MenuCommands;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * IDE-14 Opening file if some files were deleted from the same folder.
 * 
 * Created by The eXo Platform SAS.
 * 
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: ${date} ${time}
 * 
 */
public class OpeningFilesTest extends BaseTest
{
   private static final String PROJECT = OpeningFilesTest.class.getSimpleName();

   private static final String folderName = OpeningFilesTest.class.getSimpleName();

   private static final String file1Name = "File1";

   private static final String file2Name = "File2";

   private static final String file1Content = "New text file content for test.";

   @BeforeClass
   public static void setUp()
   {
      try
      {
         VirtualFileSystemUtils.createDefaultProject(PROJECT);
         VirtualFileSystemUtils.mkcol(WS_URL + PROJECT + "/" + folderName);
      }
      catch (Exception e)
      {
         fail("Can't create test folders");
      }
   }

   @AfterClass
   public static void TearDown()
   {
      try
      {
         VirtualFileSystemUtils.delete(WS_URL + PROJECT + "/");
      }
      catch (Exception e)
      {
         fail("Can't create test folders");
      }

   }

   @Test
   public void testDeleteFileAndOpenFromOneFolder() throws Exception
   {
      // open project and check
      IDE.PROJECT.EXPLORER.waitOpened();
      IDE.PROJECT.OPEN.openProject(PROJECT);
      IDE.LOADER.waitClosed();
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + folderName);
      IDE.PROJECT.EXPLORER.selectItem(PROJECT + "/" + folderName);

      // close welcome tab for easy numbered tabs and editors
      IDE.EDITOR.clickCloseEditorButton(0);
      IDE.LOADER.waitClosed();
      IDE.EDITOR.waitTabNotPresent(0);

      // create txt file. Change content
      IDE.TOOLBAR.runCommandFromNewPopupMenu(MenuCommands.New.TEXT_FILE);
      IDE.EDITOR.waitTabPresent(0);
      IDE.EDITOR.typeTextIntoEditor(0, file1Content);
      IDE.EDITOR.saveAs(0, file1Name);
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + folderName + "/" + file1Name);
      IDE.EDITOR.closeFile(file1Name);

      // create html file. Change content
      IDE.TOOLBAR.runCommandFromNewPopupMenu(MenuCommands.New.HTML_FILE);
      IDE.EDITOR.waitTabPresent(0);
      IDE.EDITOR.saveAs(0, file2Name);
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + folderName + "/" + file2Name);
      IDE.EDITOR.closeFile(0);

      // Delete second file and check
      IDE.PROJECT.EXPLORER.selectItem(PROJECT + "/" + folderName + "/" + file2Name);
      IDE.TOOLBAR.runCommand("Delete Item(s)...");
      IDE.DELETE.waitOpened();
      IDE.DELETE.clickOkButton();
      IDE.DELETE.waitClosed();
      IDE.PROJECT.EXPLORER.waitForItemNotPresent(PROJECT + "/" + folderName + "/" + file2Name);

      // open first file and check the saved content
      IDE.PROJECT.EXPLORER.openItem(PROJECT + "/" + folderName + "/" + file1Name);
      IDE.EDITOR.waitActiveFile(PROJECT + "/" + folderName + "/" + file1Name);
      assertEquals(file1Content, IDE.EDITOR.getTextFromCodeEditor(2));
      IDE.EDITOR.closeFile(0);

      // delete first file, delete folder and check deleting
      IDE.PROJECT.EXPLORER.selectItem(PROJECT + "/" + folderName);
      IDE.TOOLBAR.runCommand("Delete Item(s)...");
      IDE.DELETE.waitOpened();
      IDE.DELETE.clickOkButton();
      IDE.DELETE.waitClosed();
      IDE.PROJECT.EXPLORER.waitForItemNotPresent(PROJECT + "/" + folderName);
      assertTrue(IDE.PROJECT.EXPLORER.isItemPresent(PROJECT));
   }

}

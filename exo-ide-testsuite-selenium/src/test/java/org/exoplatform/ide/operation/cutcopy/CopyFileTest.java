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
   private static final String PROJECT = CopyFileTest.class.getSimpleName();

   private static final String FOLDER_1 = "folder";

   private static final String FILE_GROOVY = "testgroovy";

   private static final String FILE_CONTENT_1 = "world";

   private static final String FILE_CONTENT_2 = "hello ";

   /**
    * BeforeClass create such structure: FOLDER_1 FILE_GROOVY - file with
    * sample content
    */
   @BeforeClass
   public static void setUp()
   {
      try
      {
         VirtualFileSystemUtils.createDefaultProject(PROJECT);
         VirtualFileSystemUtils.mkcol(WS_URL + PROJECT + "/" + FOLDER_1);
         VirtualFileSystemUtils.put(FILE_CONTENT_1.getBytes(), MimeType.APPLICATION_GROOVY, WS_URL + PROJECT + "/"
            + FOLDER_1 + "/" + FILE_GROOVY);
      }
      catch (Exception e)
      {
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
      }
   }

   @Test
   public void testCopyFile() throws Exception
   {
      IDE.PROJECT.EXPLORER.waitOpened();
      IDE.PROJECT.OPEN.openProject(PROJECT);
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + FOLDER_1);

      IDE.PROJECT.EXPLORER.openItem(PROJECT + "/" + FOLDER_1);
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + FOLDER_1 + "/" + FILE_GROOVY);
      IDE.PROJECT.EXPLORER.selectItem(PROJECT + "/" + FOLDER_1 + "/" + FILE_GROOVY);

      /*
       * Check Cut and Copy commands must be enabled
       */
      IDE.MENU.waitCommandEnabled(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.COPY_MENU);
      IDE.TOOLBAR.waitButtonPresentAtLeft(MenuCommands.Edit.COPY_TOOLBAR);
      IDE.TOOLBAR.waitForButtonEnabled(MenuCommands.Edit.COPY_TOOLBAR);

      IDE.MENU.waitCommandEnabled(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.CUT_MENU);
      IDE.TOOLBAR.waitButtonPresentAtLeft(MenuCommands.Edit.CUT_TOOLBAR);
      IDE.TOOLBAR.waitForButtonEnabled(MenuCommands.Edit.CUT_TOOLBAR);

      /*
       * Check Paste command must be disabled
       */
      IDE.MENU.waitCommandDisabled(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.PASTE_MENU);
      IDE.TOOLBAR.waitButtonPresentAtLeft(MenuCommands.Edit.PASTE_TOOLBAR);
      IDE.TOOLBAR.waitForButtonDisabled(MenuCommands.Edit.PASTE_TOOLBAR);

      /*
       * Click Copy command on toolbar
       */
      IDE.TOOLBAR.runCommand(MenuCommands.Edit.COPY_TOOLBAR);

      /*
       * Check Paste must be enabled
       */
      IDE.MENU.waitCommandEnabled(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.PASTE_MENU);
      IDE.TOOLBAR.waitForButtonEnabled(MenuCommands.Edit.PASTE_TOOLBAR);

      /*
       * Select project in workspace panel
       */
      IDE.PROJECT.EXPLORER.selectItem(PROJECT);

      /*
       * Click Paste command
       */
      IDE.TOOLBAR.runCommand(MenuCommands.Edit.PASTE_TOOLBAR);
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + FILE_GROOVY);

      /*
       * Check Paste command must be disabled
       */
      IDE.MENU.waitCommandDisabled(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.PASTE_MENU);
      IDE.TOOLBAR.waitForButtonDisabled(MenuCommands.Edit.PASTE_TOOLBAR);

      IDE.PROJECT.EXPLORER.openItem(PROJECT + "/" + FOLDER_1);
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + FOLDER_1 + "/" + FILE_GROOVY);

      IDE.PROJECT.EXPLORER.openItem(PROJECT + "/" + FILE_GROOVY);
      IDE.EDITOR.waitActiveFile();
      IDE.EDITOR.typeTextIntoEditor(FILE_CONTENT_2);
      IDE.EDITOR.waitFileContentModificationMark(FILE_GROOVY);
      IDE.TOOLBAR.runCommand(ToolbarCommands.File.SAVE);
      IDE.EDITOR.waitNoContentModificationMark(FILE_GROOVY);
      IDE.EDITOR.closeFile(FILE_GROOVY);

      /*
       * Open files
       */
      IDE.PROJECT.EXPLORER.openItem(PROJECT + "/" + FOLDER_1 + "/" + FILE_GROOVY);
      IDE.EDITOR.waitActiveFile();

      IDE.PROJECT.EXPLORER.openItem(PROJECT + "/" + FILE_GROOVY);
      IDE.EDITOR.waitActiveFile();

      /*
       * Check files content
       */
      IDE.EDITOR.selectTab(1);
      assertEquals(FILE_CONTENT_1, IDE.EDITOR.getTextFromCodeEditor());
      IDE.EDITOR.selectTab(2);
      assertEquals(FILE_CONTENT_2 + FILE_CONTENT_1, IDE.EDITOR.getTextFromCodeEditor());
   }
}

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

import java.util.UUID;

/**
 * @author <a href="mailto:roman.iyvshyn@exoplatform.com">Roman Iyvshyn</a>
 * @author <a href="mailto:njusha.exo@gmail.com">Nadia Zavalko</a>
 * @version $Id: Aug 11, 2010
 *
 */
public class CutFileTest extends BaseTest
{
   private static final String PROJECT = CutFileTest.class.getSimpleName();

   private static final String FILE_NAME_1 = "CutFileTest.txt";

   private static final String FOLDER_NAME_1 = CutFileTest.class.getSimpleName() + "-1";

   private static final String FOLDER_NAME_2 = CutFileTest.class.getSimpleName() + "-2";

   private static final String RANDOM_CONTENT = UUID.randomUUID().toString();

   private static final String CUT_OPENED_FILE = "Can't cut opened file %s";

   private static final String CUT_FOLDER_WITH_OPENED_FILE = "Can't cut folder %s, it contains open file %s";
   
   public static final String WRONG_DESTINATION = "Destination folder already contains item with the same name.";
   @BeforeClass
   public static void setUp()
   {
      try
      {
         VirtualFileSystemUtils.createDefaultProject(PROJECT);
         VirtualFileSystemUtils.mkcol(WS_URL + PROJECT + "/" + FOLDER_NAME_1);
         VirtualFileSystemUtils.mkcol(WS_URL + PROJECT + "/" + FOLDER_NAME_2);
         VirtualFileSystemUtils.put(RANDOM_CONTENT.getBytes(), MimeType.TEXT_PLAIN, WS_URL + PROJECT + "/"
            + FOLDER_NAME_1 + "/" + FILE_NAME_1);
         VirtualFileSystemUtils.put(RANDOM_CONTENT.getBytes(), MimeType.TEXT_PLAIN, WS_URL + PROJECT + "/"
            + FOLDER_NAME_2 + "/" + FILE_NAME_1);
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

   //IDE-114
   @Test
   public void testCutFile() throws Exception
   {
      IDE.PROJECT.EXPLORER.waitOpened();
      IDE.PROJECT.OPEN.openProject(PROJECT);
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + FOLDER_NAME_1);
      IDE.PROJECT.EXPLORER.openItem(PROJECT + "/" + FOLDER_NAME_1);

      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + FOLDER_NAME_1 + "/" + FILE_NAME_1);
      IDE.PROJECT.EXPLORER.openItem(PROJECT + "/" + FOLDER_NAME_1 + "/" + FILE_NAME_1);
      IDE.EDITOR.waitActiveFile(PROJECT + "/" + FOLDER_NAME_1 + "/" + FILE_NAME_1);

      //Check Paste disabled:
      assertFalse(IDE.TOOLBAR.isButtonEnabled(ToolbarCommands.File.PASTE));
      assertFalse(IDE.MENU.isCommandEnabled(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.PASTE_MENU));
      //Check Copy enabled:
      assertTrue(IDE.MENU.isCommandEnabled(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.CUT_MENU));
      assertTrue(IDE.MENU.isCommandEnabled(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.COPY_MENU));
      assertTrue(IDE.TOOLBAR.isButtonEnabled(MenuCommands.Edit.CUT_TOOLBAR));
      assertTrue(IDE.TOOLBAR.isButtonEnabled(MenuCommands.Edit.COPY_TOOLBAR));

      IDE.PROJECT.EXPLORER.selectItem(PROJECT + "/" + FOLDER_NAME_1 + "/" + FILE_NAME_1);
      //Check Paste disabled:
      assertFalse(IDE.TOOLBAR.isButtonEnabled(ToolbarCommands.File.PASTE));
      assertFalse(IDE.MENU.isCommandEnabled(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.PASTE_MENU));
      //Check Copy enabled:
      assertTrue(IDE.MENU.isCommandEnabled(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.CUT_MENU));
      assertTrue(IDE.MENU.isCommandEnabled(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.COPY_MENU));
      assertTrue(IDE.TOOLBAR.isButtonEnabled(MenuCommands.Edit.CUT_TOOLBAR));
      assertTrue(IDE.TOOLBAR.isButtonEnabled(MenuCommands.Edit.COPY_TOOLBAR));

      IDE.PROJECT.EXPLORER.selectItem(PROJECT + "/" + FOLDER_NAME_1 + "/" + FILE_NAME_1);

      //Cut opened file causes warning message:
      IDE.MENU.runCommand(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.CUT_MENU);
      IDE.WARNING_DIALOG.waitOpened();
      assertEquals(String.format(CUT_OPENED_FILE, FILE_NAME_1), IDE.WARNING_DIALOG.getWarningMessage());
      IDE.WARNING_DIALOG.clickOk();
      IDE.WARNING_DIALOG.waitClosed();
      //Check Paste disabled:
      assertFalse(IDE.TOOLBAR.isButtonEnabled(ToolbarCommands.File.PASTE));
      assertFalse(IDE.MENU.isCommandEnabled(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.PASTE_MENU));

      //Cut folder, which contains opened file:
      IDE.PROJECT.EXPLORER.selectItem(PROJECT + "/" + FOLDER_NAME_1);
      IDE.MENU.runCommand(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.CUT_MENU);
      IDE.WARNING_DIALOG.waitOpened();
      assertEquals(String.format(CUT_FOLDER_WITH_OPENED_FILE, FOLDER_NAME_1, FILE_NAME_1),
         IDE.WARNING_DIALOG.getWarningMessage());
      IDE.WARNING_DIALOG.clickOk();
      IDE.WARNING_DIALOG.waitClosed();
      //Check Paste disabled:
      assertFalse(IDE.TOOLBAR.isButtonEnabled(ToolbarCommands.File.PASTE));
      assertFalse(IDE.MENU.isCommandEnabled(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.PASTE_MENU));

      //Close file :
      IDE.EDITOR.closeFile(FILE_NAME_1);
      IDE.EDITOR.waitTabNotPresent(FILE_NAME_1);

      IDE.PROJECT.EXPLORER.selectItem(PROJECT + "/" + FOLDER_NAME_1 + "/" + FILE_NAME_1);
      IDE.MENU.runCommand(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.CUT_MENU);

      IDE.TOOLBAR.waitForButtonEnabled(ToolbarCommands.File.PASTE, true);
      assertTrue(IDE.TOOLBAR.isButtonEnabled(ToolbarCommands.File.PASTE));
      assertTrue(IDE.MENU.isCommandEnabled(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.PASTE_MENU));

      IDE.PROJECT.EXPLORER.selectItem(PROJECT + "/" + FOLDER_NAME_2);
      IDE.MENU.runCommand(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.PASTE_MENU);

      IDE.WARNING_DIALOG.waitOpened();
      assertEquals(WRONG_DESTINATION,
         IDE.WARNING_DIALOG.getWarningMessage());
      IDE.WARNING_DIALOG.clickOk();
      IDE.WARNING_DIALOG.waitClosed();

      assertTrue(IDE.TOOLBAR.isButtonEnabled(ToolbarCommands.File.PASTE));
      assertTrue(IDE.MENU.isCommandEnabled(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.PASTE_MENU));

      IDE.PROJECT.EXPLORER.selectItem(PROJECT);
      IDE.MENU.runCommand(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.PASTE_MENU);
      assertEquals(404, VirtualFileSystemUtils.get(WS_URL + PROJECT + "/" + FOLDER_NAME_1 + "/" + FILE_NAME_1)
         .getStatusCode());
      assertEquals(200, VirtualFileSystemUtils.get(WS_URL + PROJECT + "/" + FILE_NAME_1).getStatusCode());

      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + FILE_NAME_1);
      IDE.PROJECT.EXPLORER.selectItem(PROJECT + "/" + FILE_NAME_1);
      assertFalse(IDE.TOOLBAR.isButtonEnabled(ToolbarCommands.File.PASTE));
      assertFalse(IDE.MENU.isCommandEnabled(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.PASTE_MENU));

      IDE.PROJECT.EXPLORER.openItem(PROJECT + "/" + FILE_NAME_1);
      IDE.EDITOR.waitActiveFile(PROJECT + "/" + FILE_NAME_1);
      assertEquals(RANDOM_CONTENT, IDE.EDITOR.getTextFromCodeEditor(1));
      IDE.EDITOR.closeFile(FILE_NAME_1);
   }
}

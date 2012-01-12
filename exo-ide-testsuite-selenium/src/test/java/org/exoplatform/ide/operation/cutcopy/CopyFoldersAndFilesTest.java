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
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.exoplatform.ide.core.Response;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.UUID;

/**
 * IDE-113:Copy folders and files.
 * 
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: $
 *
 */
public class CopyFoldersAndFilesTest extends BaseTest
{
   private static final String PROJECT = CopyFoldersAndFilesTest.class.getSimpleName();

   private static final String FOLDER_1 = CopyFoldersAndFilesTest.class.getSimpleName() + "-1";

   private static final String FOLDER_2 = CopyFoldersAndFilesTest.class.getSimpleName() + "-2";

   private static final String FOLDER_1_1 = CopyFoldersAndFilesTest.class.getSimpleName() + "-1-1";

   private static final String FOLDER_1_2 = CopyFoldersAndFilesTest.class.getSimpleName() + "-1-2";

   private static final String FILE_GADGET = "gadget_xml";

   private static final String FILE_GROOVY = "test_groovy";

   private static final String RANDOM_CONTENT_1 = UUID.randomUUID().toString();

   private static final String RANDOM_CONTENT_2 = UUID.randomUUID().toString();

   /**
    * BeforeClass create such structure:
    * PROJECT
    *   FOLDER_1
    *     FILE_GADGET - file with sample content
    *     FILE_GROOVY - file with sample content
    *     FOLDER_1_1
    *     FOLDER_1_2
    *   FOLDER_2
    */
   @BeforeClass
   public static void setUp()
   {
      try
      {
         VirtualFileSystemUtils.createDefaultProject(PROJECT);
         VirtualFileSystemUtils.mkcol(WS_URL + PROJECT + "/" + FOLDER_1);
         VirtualFileSystemUtils.mkcol(WS_URL + PROJECT + "/" + FOLDER_2);
         VirtualFileSystemUtils.mkcol(WS_URL + PROJECT + "/" + FOLDER_1 + "/" + FOLDER_1_1);
         VirtualFileSystemUtils.mkcol(WS_URL + PROJECT + "/" + FOLDER_1 + "/" + FOLDER_1_2);
         VirtualFileSystemUtils.put(RANDOM_CONTENT_1.getBytes(), MimeType.GOOGLE_GADGET, WS_URL + PROJECT + "/"
            + FOLDER_1 + "/" + FILE_GADGET);
         VirtualFileSystemUtils.put(RANDOM_CONTENT_2.getBytes(), MimeType.APPLICATION_GROOVY, WS_URL + PROJECT + "/"
            + FOLDER_1 + "/" + FILE_GROOVY);
      }
      catch (Exception e)
      {
      }
   }

   @AfterClass
   public static void tearDown() throws IOException
   {
      VirtualFileSystemUtils.delete(WS_URL + PROJECT);
   }

   /**
    * IDE-113.
    * 
    * @throws Exception
    */
   @Test
   public void testCopyFoldersAndFiles() throws Exception
   {
      IDE.PROJECT.EXPLORER.waitOpened();
      IDE.PROJECT.OPEN.openProject(PROJECT);
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + FOLDER_1);
      IDE.PROJECT.EXPLORER.openItem(PROJECT + "/" + FOLDER_1);

      //Open files:
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + FOLDER_1 + "/" + FILE_GROOVY);
      IDE.PROJECT.EXPLORER.openItem(PROJECT + "/" + FOLDER_1 + "/" + FILE_GROOVY);
      IDE.EDITOR.waitActiveFile(PROJECT + "/" + FOLDER_1 + "/" + FILE_GROOVY);
      IDE.PROJECT.EXPLORER.openItem(PROJECT + "/" + FOLDER_1 + "/" + FILE_GADGET);
      IDE.EDITOR.waitActiveFile(PROJECT + "/" + FOLDER_1 + "/" + FILE_GADGET);
      
      IDE.WORKSPACE.selectItem(PROJECT  + "/"+ FOLDER_1);

      assertTrue(IDE.TOOLBAR.isButtonPresentAtLeft(MenuCommands.Edit.PASTE_TOOLBAR));
      assertFalse(IDE.TOOLBAR.isButtonEnabled(MenuCommands.Edit.PASTE_TOOLBAR));

      // Call the "Edit->Copy Items" topmenu command.
      IDE.MENU.runCommand(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.COPY_MENU);
      assertTrue(IDE.TOOLBAR.isButtonEnabled(MenuCommands.Edit.PASTE_TOOLBAR));
      assertTrue(IDE.MENU.isCommandEnabled(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.PASTE_MENU));

      IDE.PROJECT.EXPLORER.selectItem(PROJECT + "/" + FOLDER_1 + "/" + FOLDER_1_1);
      IDE.DELETE.deleteSelectedItems();
      IDE.PROJECT.EXPLORER.waitForItemNotPresent(PROJECT + "/" + FOLDER_1 + "/" + FOLDER_1_1);

      IDE.PROJECT.EXPLORER.selectItem(PROJECT + "/" + FOLDER_1 + "/" + FILE_GADGET);
      IDE.DELETE.deleteSelectedItems();
      IDE.PROJECT.EXPLORER.waitForItemNotPresent(PROJECT + "/" + FOLDER_1 + "/" + FILE_GADGET);

      IDE.PROJECT.EXPLORER.selectItem(PROJECT + "/" + FOLDER_2);
      IDE.MENU.runCommand(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.PASTE_MENU);

      checkFilesAndFoldersOnServer();

      assertTrue(IDE.EDITOR.isTabPresentInEditorTabset(FILE_GROOVY));
      assertFalse(IDE.EDITOR.isTabPresentInEditorTabset(FILE_GADGET));

      assertTrue(IDE.MENU.isCommandVisible(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.PASTE_MENU));
      assertFalse(IDE.MENU.isCommandEnabled(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.PASTE_MENU));

      assertTrue(IDE.TOOLBAR.isButtonPresentAtLeft(MenuCommands.Edit.PASTE_TOOLBAR));
      assertFalse(IDE.TOOLBAR.isButtonEnabled(MenuCommands.Edit.PASTE_TOOLBAR));
   }

   private void checkFilesAndFoldersOnServer() throws Exception
   {
      assertEquals(200, VirtualFileSystemUtils.get(WS_URL + PROJECT + "/" + FOLDER_2 + "/" + FOLDER_1).getStatusCode());
      assertEquals(200, VirtualFileSystemUtils.get(WS_URL + PROJECT + "/" + FOLDER_2 + "/" + FOLDER_1 + "/" + FOLDER_1_2)
         .getStatusCode());
      assertEquals(200, VirtualFileSystemUtils.get(WS_URL + PROJECT + "/" + FOLDER_2 + "/" + FOLDER_1 + "/" + FILE_GROOVY)
         .getStatusCode());
      assertEquals(404, VirtualFileSystemUtils.get(WS_URL + PROJECT + "/" + FOLDER_2 + "/" + FOLDER_1 + "/" + FILE_GADGET)
         .getStatusCode());
      assertEquals(404, VirtualFileSystemUtils.get(WS_URL + PROJECT + "/" + FOLDER_2 + "/" + FOLDER_1 + "/" + FOLDER_1_1)
         .getStatusCode());

      Response response = VirtualFileSystemUtils.get(WS_URL + PROJECT + "/" + FOLDER_1 + "/" + FILE_GROOVY);
      assertEquals(200, response.getStatusCode());
      assertEquals(RANDOM_CONTENT_2, response.getData());

      response = VirtualFileSystemUtils.get(WS_URL + PROJECT + "/" + FOLDER_2 + "/" + FOLDER_1 + "/" + FILE_GROOVY);
      assertEquals(200, response.getStatusCode());
      assertEquals(RANDOM_CONTENT_2, response.getData());
   }

}

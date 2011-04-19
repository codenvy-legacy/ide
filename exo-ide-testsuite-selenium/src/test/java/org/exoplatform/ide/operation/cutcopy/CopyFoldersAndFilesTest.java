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

import java.util.UUID;

import org.exoplatform.common.http.HTTPStatus;
import org.exoplatform.common.http.client.HTTPResponse;
import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.MenuCommands;
import org.exoplatform.ide.ToolbarCommands;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * IDE-113:Copy folders and files.
 * 
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: $
 *
 */
public class CopyFoldersAndFilesTest extends BaseTest
{

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
    * FOLDER_1
    *    FILE_GADGET - file with sample content
    *    FILE_GROOVY - file with sample content
    *    FOLDER_1_1
    *    FOLDER_1_2
    * FOLDER_2
    */
   @BeforeClass
   public static void setUp()
   {
      try
      {
         VirtualFileSystemUtils.mkcol(WS_URL + FOLDER_1);
         VirtualFileSystemUtils.mkcol(WS_URL + FOLDER_2);
         VirtualFileSystemUtils.mkcol(WS_URL + FOLDER_1 + "/" + FOLDER_1_1 + "/");
         VirtualFileSystemUtils.mkcol(WS_URL + FOLDER_1 + "/" + FOLDER_1_2 + "/");
         VirtualFileSystemUtils.put(RANDOM_CONTENT_1.getBytes(), MimeType.GOOGLE_GADGET, WS_URL + FOLDER_1 + "/" + FILE_GADGET);
         VirtualFileSystemUtils.put(RANDOM_CONTENT_2.getBytes(), MimeType.APPLICATION_GROOVY, WS_URL + FOLDER_1 + "/" + FILE_GROOVY);
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
         VirtualFileSystemUtils.delete(WS_URL + FOLDER_2);
      }
      catch (Exception e)
      {
         e.printStackTrace();
      }
   }

   /**
    * IDE-113.
    * 
    * @throws Exception
    */
   @Test
   public void testCopyFoldersAndFiles() throws Exception
   {
      waitForRootElement();

      IDE.navigator().selectItem(WS_URL + FOLDER_1 + "/");
      IDE.toolbar().runCommand(ToolbarCommands.File.REFRESH);

      IDE.navigator().openFileFromNavigationTreeWithCodeEditor(WS_URL + FOLDER_1 + "/" + FILE_GROOVY, false);
      IDE.navigator().openFileFromNavigationTreeWithCodeEditor(WS_URL + FOLDER_1 + "/" + FILE_GADGET, false);

      IDE.navigator().selectItem(WS_URL + FOLDER_1 + "/");

      IDE.toolbar().assertButtonExistAtLeft(MenuCommands.Edit.PASTE_TOOLBAR, true);
      IDE.toolbar().assertButtonEnabled(MenuCommands.Edit.PASTE_TOOLBAR, false);

      // Call the "Edit->Copy Items" topmenu command.
      IDE.menu().runCommand(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.COPY_MENU);
      IDE.toolbar().assertButtonEnabled(MenuCommands.Edit.PASTE_TOOLBAR, true);
      IDE.menu().checkCommandEnabled(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.PASTE_MENU, true);

      IDE.navigator().selectItem(WS_URL + FOLDER_1 + "/" + FOLDER_1_1 + "/");
      IDE.navigator().deleteSelectedItems();

      IDE.navigator().selectItem(WS_URL + FOLDER_1 + "/" + FILE_GADGET);
      IDE.navigator().deleteSelectedItems();

      //IDE.navigator().selectRootOfWorkspace();
      IDE.navigator().selectItem(WS_URL + FOLDER_2 + "/");

      IDE.menu().runCommand(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.PASTE_MENU);

      checkFilesAndFoldersOnServer();

      IDE.editor().checkIsTabPresentInEditorTabset("test_groovy", true);

      IDE.menu().checkCommandVisibility(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.PASTE_MENU, true);
      IDE.menu().checkCommandEnabled(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.PASTE_MENU, false);

      IDE.toolbar().assertButtonExistAtLeft(MenuCommands.Edit.PASTE_TOOLBAR, true);
      IDE.toolbar().assertButtonEnabled(MenuCommands.Edit.PASTE_TOOLBAR, false);

   }

   private void checkFilesAndFoldersOnServer() throws Exception
   {
      assertEquals(HTTPStatus.OK, VirtualFileSystemUtils.get(WS_URL + FOLDER_2 + "/" + FOLDER_1).getStatusCode());
      assertEquals(HTTPStatus.OK, VirtualFileSystemUtils.get(WS_URL + FOLDER_2 + "/" + FOLDER_1 + "/" + FOLDER_1_2).getStatusCode());
      assertEquals(HTTPStatus.OK, VirtualFileSystemUtils.get(WS_URL + FOLDER_2 + "/" + FOLDER_1 + "/" + FILE_GROOVY).getStatusCode());

      final HTTPResponse fileResponse1 = VirtualFileSystemUtils.get(WS_URL + FOLDER_1 + "/" + FILE_GROOVY);
      assertEquals(HTTPStatus.OK, fileResponse1.getStatusCode());
      assertEquals(RANDOM_CONTENT_2, new String(fileResponse1.getData()));

      final HTTPResponse fileResponse2 =
         VirtualFileSystemUtils.get(WS_URL + FOLDER_2 + "/" + FOLDER_1 + "/" + FILE_GROOVY);
      assertEquals(HTTPStatus.OK, fileResponse2.getStatusCode());
      assertEquals(RANDOM_CONTENT_2, new String(fileResponse2.getData()));
   }

}

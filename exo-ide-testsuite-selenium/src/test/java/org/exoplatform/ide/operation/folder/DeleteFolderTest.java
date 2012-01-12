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
package org.exoplatform.ide.operation.folder;

import static org.junit.Assert.assertEquals;

import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.MenuCommands;
import org.exoplatform.ide.ToolbarCommands;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Created by The eXo Platform SAS.
 * @author <a href="mailto:vitaly.parfonov@gmail.com">Vitaly Parfonov</a>
 * @version $Id: $
*/
public class DeleteFolderTest extends BaseTest
{
   private final static String PROJECT = DeleteFolderTest.class.getSimpleName();

   private final static String FOLDER_NAME_TOOLBAR = "deleteFolderToolBarTest";

   private final static String FOLDER_NAME_MENU = "deleteFolderMenuTest";

   @BeforeClass
   public static void setUp()
   {
      try
      {
         VirtualFileSystemUtils.createDefaultProject(PROJECT);
         VirtualFileSystemUtils.mkcol(WS_URL + PROJECT + "/" + FOLDER_NAME_TOOLBAR);
         VirtualFileSystemUtils.mkcol(WS_URL + PROJECT + "/" + FOLDER_NAME_MENU);
      }
      catch (Exception e)
      {
      }
   }

   /**
    * Test to delete folder using ToolBar button. (TestCase IDE-18)
    * 
    * @throws Exception
    */
   @Test
   public void testDeleteFolderFromToolbar() throws Exception
   {
      IDE.PROJECT.EXPLORER.waitOpened();
      IDE.PROJECT.OPEN.openProject(PROJECT);

      IDE.PROJECT.EXPLORER.waitForItem(PROJECT);
      IDE.MENU.runCommand(MenuCommands.File.FILE, MenuCommands.File.REFRESH);

      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + FOLDER_NAME_TOOLBAR);
      IDE.PROJECT.EXPLORER.selectItem(PROJECT + "/" + FOLDER_NAME_TOOLBAR);

      IDE.TOOLBAR.runCommand(ToolbarCommands.File.DELETE);
      IDE.DELETE.waitOpened();
      IDE.DELETE.clickOkButton();
      IDE.DELETE.waitClosed();

      IDE.PROJECT.EXPLORER.waitForItemNotPresent(PROJECT + "/" + FOLDER_NAME_TOOLBAR);
      Assert.assertFalse(IDE.PROJECT.EXPLORER.isItemPresent(PROJECT + "/" + FOLDER_NAME_TOOLBAR));
      assertEquals(404, VirtualFileSystemUtils.get(WS_URL + PROJECT + "/" + FOLDER_NAME_TOOLBAR).getStatusCode());
   }

   /**
    * Test to delete folder using Main Menu. (TestCase IDE-18)
    * 
    * @throws Exception
    */
   @Test
   public void testDeleteFolderFromMainMenu() throws Exception
   {
      selenium.refresh();

      IDE.PROJECT.EXPLORER.waitForItem(PROJECT);
      IDE.MENU.runCommand(MenuCommands.File.FILE, MenuCommands.File.REFRESH);

      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + FOLDER_NAME_MENU);
      IDE.PROJECT.EXPLORER.selectItem(PROJECT + "/" + FOLDER_NAME_MENU);

      IDE.MENU.runCommand(MenuCommands.File.FILE, MenuCommands.File.DELETE);
      IDE.DELETE.waitOpened();
      IDE.DELETE.clickOkButton();
      IDE.DELETE.waitClosed();
      
      IDE.PROJECT.EXPLORER.waitForItemNotPresent(PROJECT + "/" + FOLDER_NAME_MENU);
      Assert.assertFalse(IDE.PROJECT.EXPLORER.isItemPresent(PROJECT + "/" + FOLDER_NAME_MENU));
      assertEquals(404, VirtualFileSystemUtils.get(WS_URL + PROJECT + "/" + FOLDER_NAME_MENU).getStatusCode());
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
}

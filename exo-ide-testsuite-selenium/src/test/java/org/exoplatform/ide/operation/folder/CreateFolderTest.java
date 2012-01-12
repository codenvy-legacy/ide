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
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by The eXo Platform SAS.
 * @author <a href="mailto:vitaly.parfonov@gmail.com">Vitaly Parfonov</a>
 * @version $Id: $
*/
public class CreateFolderTest extends BaseTest
{

   private static String FOLDER_NAME_DEFAULT = "New Folder";

   private static String PROJECT = CreateFolderTest.class.getSimpleName();

   @Test
   public void testCreateFolderNotInProject() throws Exception
   {
      IDE.PROJECT.EXPLORER.waitOpened();
      Assert.assertFalse(IDE.TOOLBAR.isButtonFromNewPopupMenuEnabled(MenuCommands.New.FOLDER));
      Assert.assertNull(IDE.PROJECT.EXPLORER.getCurrentProject());
   }

   /**
    * Test to create folder using main menu (TestCase IDE-3).
    * 
    * @throws Exception
    */
   @Test
   public void testCreateFolder() throws Exception
   {
      selenium.refresh();

      IDE.PROJECT.EXPLORER.waitOpened();
      IDE.PROJECT.CREATE.createProject(PROJECT);
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT);

      Assert.assertTrue(IDE.TOOLBAR.isButtonFromNewPopupMenuEnabled(MenuCommands.New.FOLDER));

      IDE.TOOLBAR.runCommandFromNewPopupMenu(MenuCommands.New.FOLDER);
      IDE.FOLDER.waitOpened();
      IDE.FOLDER.clickCreateButton();
      IDE.FOLDER.waitClosed();

      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + FOLDER_NAME_DEFAULT);
      assertEquals(200, VirtualFileSystemUtils.get(WS_URL + PROJECT + "/" + FOLDER_NAME_DEFAULT.replace(" ", "%20"))
         .getStatusCode());
   }

   /**
    * Checks the present of create folder form elements.
    */
   @AfterClass
   public static void tearDown()
   {
      try
      {
         VirtualFileSystemUtils.delete(WS_URL + PROJECT + "/");
      }
      catch (Exception e)
      {
      }
   }

}

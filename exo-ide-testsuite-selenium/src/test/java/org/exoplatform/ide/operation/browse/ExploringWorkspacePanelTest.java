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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Created by The eXo Platform SAS.
 * 
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: ${date} ${time}
 * 
 */
public class ExploringWorkspacePanelTest extends BaseTest
{

   private static final String PROJECT = ExploringWorkspacePanelTest.class.getSimpleName();

   private static final String FOLDER_2_2 = "folder-2-2";

   private static final String FOLDER_2_1 = "folder-2-1";

   private static final String FOLDER_2 = "folder-2";

   private static final String FOLDER_1_2 = "folder-1-2";

   private static final String FOLDER_1_1 = "folder-1-1";

   private static final String FOLDER_1 = "folder-1";

   @BeforeClass
   public static void setUp()
   {
      try
      {
         VirtualFileSystemUtils.createDefaultProject(PROJECT);
         VirtualFileSystemUtils.mkcol(WS_URL + PROJECT + "/" + FOLDER_1 + "/");
         VirtualFileSystemUtils.mkcol(WS_URL + PROJECT + "/" + FOLDER_1 + "/" + FOLDER_1_1);
         VirtualFileSystemUtils.mkcol(WS_URL + PROJECT + "/" + FOLDER_1 + "/" + FOLDER_1_2);
         VirtualFileSystemUtils.mkcol(WS_URL + PROJECT + "/" + FOLDER_2 + "/");
         VirtualFileSystemUtils.mkcol(WS_URL + PROJECT + "/" + FOLDER_2 + "/" + FOLDER_2_1);
         VirtualFileSystemUtils.mkcol(WS_URL + PROJECT + "/" + FOLDER_2 + "/" + FOLDER_2_2);
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

   /**
    * IDE-2 Exploring "Workspace" panel
    * 
    * @throws Exception
    */
   @Test
   public void testExplodeCollapseFolder() throws Exception
   {
      IDE.PROJECT.EXPLORER.waitOpened();
      IDE.PROJECT.OPEN.openProject(PROJECT);
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT);
      IDE.LOADER.waitClosed();
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + FOLDER_1);
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + FOLDER_2);

      // step1. Open folder 2. Check visible subfolders of folder #2. Check subfolders folder #1 is not visible.
      IDE.PROJECT.EXPLORER.clickOpenCloseButton(PROJECT + "/" + FOLDER_2);
      IDE.LOADER.waitClosed();
      IDE.PROJECT.EXPLORER.waitForItem(WS_URL + PROJECT + "/" + FOLDER_2 + "/" + FOLDER_2_1);
      IDE.PROJECT.EXPLORER.waitForItem(WS_URL + PROJECT + "/" + FOLDER_2 + "/" + FOLDER_2_2);
      assertTrue(IDE.PROJECT.EXPLORER.isItemVisible(WS_URL + PROJECT + "/" + FOLDER_2 + "/" + FOLDER_2_1));
      assertTrue(IDE.PROJECT.EXPLORER.isItemVisible(WS_URL + PROJECT + "/" + FOLDER_2 + "/" + FOLDER_2_2));

      // step2. Close folder 2. Check not visible subfolders of folder #2.
      // Open folder one and check visible subfolders of of folder#1.
      IDE.PROJECT.EXPLORER.clickOpenCloseButton(PROJECT + "/" + FOLDER_2);
      assertFalse(IDE.PROJECT.EXPLORER.isItemVisible(WS_URL + PROJECT + "/" + FOLDER_2 + "/" + FOLDER_2_1));
      assertFalse(IDE.PROJECT.EXPLORER.isItemVisible(WS_URL + PROJECT + "/" + FOLDER_2 + "/" + FOLDER_2_2));

      IDE.PROJECT.EXPLORER.clickOpenCloseButton(PROJECT + "/" + FOLDER_1);
      IDE.PROJECT.EXPLORER.waitForItem(WS_URL + PROJECT + "/" + FOLDER_1 + "/" + FOLDER_1_1);
      IDE.PROJECT.EXPLORER.waitForItem(WS_URL + PROJECT + "/" + FOLDER_1 + "/" + FOLDER_1_2);
      assertTrue(IDE.PROJECT.EXPLORER.isItemVisible(WS_URL + PROJECT + "/" + FOLDER_1 + "/" + FOLDER_1_1));
      assertTrue(IDE.PROJECT.EXPLORER.isItemVisible(WS_URL + PROJECT + "/" + FOLDER_1 + "/" + FOLDER_1_2));

      // step 3 collapse all folders and check their
      IDE.PROJECT.EXPLORER.clickOpenCloseButton(PROJECT + "/" + FOLDER_2);
      IDE.LOADER.waitClosed();
      IDE.PROJECT.EXPLORER.waitForItem(WS_URL + PROJECT + "/" + FOLDER_2 + "/" + FOLDER_2_1);
      IDE.PROJECT.EXPLORER.waitForItem(WS_URL + PROJECT + "/" + FOLDER_2 + "/" + FOLDER_2_2);

      assertTrue(IDE.PROJECT.EXPLORER.isItemVisible(WS_URL + PROJECT + "/" + FOLDER_1));
      assertTrue(IDE.PROJECT.EXPLORER.isItemVisible(WS_URL + PROJECT + "/" + FOLDER_2));
      assertTrue(IDE.PROJECT.EXPLORER.isItemVisible(WS_URL + PROJECT + "/" + FOLDER_2 + "/" + FOLDER_2_1));
      assertTrue(IDE.PROJECT.EXPLORER.isItemVisible(WS_URL + PROJECT + "/" + FOLDER_2 + "/" + FOLDER_2_2));
      assertTrue(IDE.PROJECT.EXPLORER.isItemVisible(WS_URL + PROJECT + "/" + FOLDER_1 + "/" + FOLDER_1_1));
      assertTrue(IDE.PROJECT.EXPLORER.isItemVisible(WS_URL + PROJECT + "/" + FOLDER_1 + "/" + FOLDER_1_2));

      // step 4 Roll up project folder and checking - workspace is empty
      IDE.PROJECT.EXPLORER.clickOpenCloseButton(PROJECT);
      IDE.LOADER.waitClosed();
      assertFalse(IDE.PROJECT.EXPLORER.isItemVisible(WS_URL + PROJECT + "/" + FOLDER_1));
      assertFalse(IDE.PROJECT.EXPLORER.isItemVisible(WS_URL + PROJECT + "/" + FOLDER_2));
      assertFalse(IDE.PROJECT.EXPLORER.isItemVisible(WS_URL + PROJECT + "/" + FOLDER_2 + "/" + FOLDER_2_1));
      assertFalse(IDE.PROJECT.EXPLORER.isItemVisible(WS_URL + PROJECT + "/" + FOLDER_2 + "/" + FOLDER_2_2));
      assertFalse(IDE.PROJECT.EXPLORER.isItemVisible(WS_URL + PROJECT + "/" + FOLDER_1 + "/" + FOLDER_1_1));
      assertFalse(IDE.PROJECT.EXPLORER.isItemVisible(WS_URL + PROJECT + "/" + FOLDER_1 + "/" + FOLDER_1_2));

      // step 5 Collapse project and check all folders present
      IDE.PROJECT.EXPLORER.clickOpenCloseButton(PROJECT);
      Thread.sleep(3000);
      assertTrue(IDE.PROJECT.EXPLORER.isItemVisible(WS_URL + PROJECT + "/" + FOLDER_1));
      assertTrue(IDE.PROJECT.EXPLORER.isItemVisible(WS_URL + PROJECT + "/" + FOLDER_2));
      assertTrue(IDE.PROJECT.EXPLORER.isItemVisible(WS_URL + PROJECT + "/" + FOLDER_2 + "/" + FOLDER_2_1));
      assertTrue(IDE.PROJECT.EXPLORER.isItemVisible(WS_URL + PROJECT + "/" + FOLDER_2 + "/" + FOLDER_2_2));
      assertTrue(IDE.PROJECT.EXPLORER.isItemVisible(WS_URL + PROJECT + "/" + FOLDER_1 + "/" + FOLDER_1_1));
      assertTrue(IDE.PROJECT.EXPLORER.isItemVisible(WS_URL + PROJECT + "/" + FOLDER_1 + "/" + FOLDER_1_2));
   }
}

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

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.MenuCommands;
import org.exoplatform.ide.TestConstants;
import org.exoplatform.ide.ToolbarCommands;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.exoplatform.ide.vfs.shared.Link;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import java.util.Map;

/**
 * Created by The eXo Platform SAS.
 * 
 * @author <a href="mailto:dmitry.ndp@gmail.com">Dmytro Nochevnov</a>
 * @version $Id:   ${date} ${time}
 *
 */
public class ItemOrderingTest extends BaseTest
{

   private static final String PROJECT = ItemOrderingTest.class.getSimpleName();

   private static final String TEST_FOLDER_1 = "folder-1";

   private static final String UPPERCASE_TEST_FOLDER_1 = "Folder-1";

   private static final String TEST_FOLDER_1_2 = "folder-1-2";

   private static final String TEST_FILE_1 = "file-1";

   private static final String UPPERCASE_TEST_FILE_1 = "File-1";

   private static final String TEST_FILE_1_2 = "file-1-2";

   @BeforeClass
   public static void setUp()
   {
      try
      {
         Map<String, Link> project = VirtualFileSystemUtils.createDefaultProject(PROJECT);

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
   public void testItemOrdering() throws Exception
   {
      IDE.PROJECT.EXPLORER.waitOpened();
      IDE.PROJECT.OPEN.openProject(PROJECT);
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT);
      IDE.LOADER.waitClosed();
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT);

      //close welcome tab for easy numbered tabs and editors
      IDE.EDITOR.clickCloseEditorButton(0);
      IDE.LOADER.waitClosed();
      IDE.EDITOR.waitTabNotPresent(0);

      // create test files
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT);
      IDE.TOOLBAR.runCommandFromNewPopupMenu(MenuCommands.New.XML_FILE);
      IDE.EDITOR.saveAs(0, TEST_FILE_1_2);
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + TEST_FILE_1_2);
      IDE.EDITOR.closeFile(0);

      IDE.TOOLBAR.runCommandFromNewPopupMenu(MenuCommands.New.XML_FILE);
      IDE.EDITOR.saveAs(0, UPPERCASE_TEST_FILE_1);
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + UPPERCASE_TEST_FILE_1);
      IDE.EDITOR.closeFile(0);

      IDE.TOOLBAR.runCommandFromNewPopupMenu(MenuCommands.New.XML_FILE);
      IDE.EDITOR.saveAs(0, TEST_FILE_1);
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + TEST_FILE_1);
      IDE.EDITOR.closeFile(0);

      // create test folders
      IDE.PROJECT.EXPLORER.selectItem(PROJECT);
      IDE.FOLDER.createFolder(TEST_FOLDER_1_2);
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + TEST_FOLDER_1_2);

      IDE.PROJECT.EXPLORER.selectItem(PROJECT);
      IDE.FOLDER.createFolder(UPPERCASE_TEST_FOLDER_1);
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + UPPERCASE_TEST_FOLDER_1);

      IDE.PROJECT.EXPLORER.selectItem(PROJECT);
      IDE.FOLDER.createFolder(TEST_FOLDER_1);
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + TEST_FOLDER_1);

      //check all elements in explorer
      checkItemOrderingInNavigationPanel();

      //serch all xml files and check
      IDE.PROJECT.EXPLORER.selectItem(PROJECT);
      IDE.TOOLBAR.runCommand(MenuCommands.File.SEARCH);
      IDE.SEARCH.waitPerformSearchOpened();
      IDE.SEARCH.setMimeTypeValue(MimeType.TEXT_XML);
      IDE.SEARCH.setMimeTypeValue("\n");
      IDE.SEARCH.clickSearchButton();
      IDE.SEARCH.waitSearchResultsOpened();
      assertTrue(IDE.SEARCH.isFilePresent(PROJECT + "/" + TEST_FILE_1_2));
      IDE.SEARCH.isFilePresent(PROJECT + "/" + UPPERCASE_TEST_FILE_1);
      IDE.SEARCH.isFilePresent(PROJECT + "/" + TEST_FILE_1);
   }

   private void checkItemOrderingInNavigationPanel() throws Exception
   {
      IDE.PROJECT.EXPLORER.isItemVisible(PROJECT + "/" + TEST_FILE_1_2);
      IDE.PROJECT.EXPLORER.isItemVisible(PROJECT + "/" + UPPERCASE_TEST_FILE_1);
      IDE.PROJECT.EXPLORER.isItemVisible(PROJECT + "/" + TEST_FILE_1);
      IDE.PROJECT.EXPLORER.isItemVisible(PROJECT + "/" + TEST_FOLDER_1_2);
      IDE.PROJECT.EXPLORER.isItemVisible(PROJECT + "/" + UPPERCASE_TEST_FOLDER_1);
      IDE.PROJECT.EXPLORER.isItemVisible(PROJECT + "/" + TEST_FOLDER_1);
   }

   
   @AfterClass
   public static void tearDown() throws Exception
   {
      VirtualFileSystemUtils.delete(WS_URL + PROJECT);
   }

}

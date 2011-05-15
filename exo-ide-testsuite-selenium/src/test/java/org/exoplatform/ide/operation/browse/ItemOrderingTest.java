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

import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.MenuCommands;
import org.exoplatform.ide.TestConstants;
import org.exoplatform.ide.ToolbarCommands;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Created by The eXo Platform SAS.
 *	
 * @author <a href="mailto:dmitry.ndp@gmail.com">Dmytro Nochevnov</a>
 * @version $Id:   ${date} ${time}
 *
 */
public class ItemOrderingTest extends BaseTest
{

   private static final String TEST_FOLDER_1 = "folder-1";

   private static final String UPPERCASE_TEST_FOLDER_1 = "Folder-1";

   private static final String TEST_FOLDER_1_2 = "folder-1-2";

   private static final String TEST_FILE_1 = "file-1";

   private static final String UPPERCASE_TEST_FILE_1 = "File-1";

   private static final String TEST_FILE_1_2 = "file-1-2";

   @BeforeClass
   public static void setUp()
   {
      cleanRepository(REST_CONTEXT + "/" + WEBDAV_CONTEXT + "/" + REPO_NAME + "/" + WS_NAME + "/");
   }

   @Test
   public void testItemOrdering() throws Exception
   {
      waitForRootElement();
      // create test files
      IDE.WORKSPACE.selectRootItem();
      createSaveAndCloseFile(MenuCommands.New.XML_FILE, TEST_FILE_1_2, 0);
      IDE.WORKSPACE.selectRootItem();
      createSaveAndCloseFile(MenuCommands.New.XML_FILE, UPPERCASE_TEST_FILE_1, 0);
      IDE.WORKSPACE.selectRootItem();
      createSaveAndCloseFile(MenuCommands.New.XML_FILE, TEST_FILE_1, 0);

      // create test folders
      IDE.WORKSPACE.selectRootItem();
      IDE.NAVIGATION.createFolder(TEST_FOLDER_1_2);
      IDE.WORKSPACE.selectRootItem();
      IDE.NAVIGATION.createFolder(UPPERCASE_TEST_FOLDER_1);
      IDE.WORKSPACE.selectRootItem();
      IDE.NAVIGATION.createFolder(TEST_FOLDER_1);

      checkItemOrderingInNavigationPanel();

      // test ordering within the Navigation Panel after the refreshing root folder
      IDE.WORKSPACE.selectRootItem();
      IDE.TOOLBAR.runCommand(ToolbarCommands.File.REFRESH);
      Thread.sleep(TestConstants.IDE_INITIALIZATION_PERIOD);

      // test ordering within the Search Panel
      IDE.WORKSPACE.selectRootItem();
      IDE.SEARCH.performSearch("/", "", MimeType.TEXT_XML);
      checkItemOrderngInSearchResultPanel();
   }

   private void checkItemOrderingInNavigationPanel() throws Exception
   {
      IDE.NAVIGATION.assertItemVisible(WS_URL + UPPERCASE_TEST_FOLDER_1 + "/");
      assertEquals(UPPERCASE_TEST_FOLDER_1, IDE.NAVIGATION.getRowTitle(2));

      IDE.NAVIGATION.assertItemVisible(WS_URL + TEST_FOLDER_1 + "/");
      assertEquals(TEST_FOLDER_1, IDE.NAVIGATION.getRowTitle(3));

      IDE.NAVIGATION.assertItemVisible(WS_URL + TEST_FOLDER_1_2 + "/");
      assertEquals(TEST_FOLDER_1_2, IDE.NAVIGATION.getRowTitle(4));

      IDE.NAVIGATION.assertItemVisible(WS_URL + UPPERCASE_TEST_FILE_1);
      assertEquals(UPPERCASE_TEST_FILE_1, IDE.NAVIGATION.getRowTitle(5));

      IDE.NAVIGATION.assertItemVisible(WS_URL + TEST_FILE_1);
      assertEquals(TEST_FILE_1, IDE.NAVIGATION.getRowTitle(6));

      IDE.NAVIGATION.assertItemVisible(WS_URL + TEST_FILE_1_2);
      assertEquals(TEST_FILE_1_2, IDE.NAVIGATION.getRowTitle(7));
   }

   private void checkItemOrderngInSearchResultPanel() throws Exception
   {
      IDE.NAVIGATION.assertItemVisibleInSearchTree(WS_URL + UPPERCASE_TEST_FILE_1);
      assertEquals(UPPERCASE_TEST_FILE_1, IDE.NAVIGATION.getRowTitleInSearchTree(2));

      IDE.NAVIGATION.assertItemVisibleInSearchTree(WS_URL + TEST_FILE_1);
      assertEquals(TEST_FILE_1, IDE.NAVIGATION.getRowTitleInSearchTree(3));

      IDE.NAVIGATION.assertItemVisibleInSearchTree(WS_URL + TEST_FILE_1_2);
      assertEquals(TEST_FILE_1_2, IDE.NAVIGATION.getRowTitleInSearchTree(4));
   }

   @AfterClass
   public static void tearDown() throws Exception
   {
      cleanRepository(REST_CONTEXT + "/" + WEBDAV_CONTEXT + "/" + REPO_NAME + "/" + WS_NAME);
   }

}

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
import org.exoplatform.ide.CloseFileUtils;
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
      Thread.sleep(TestConstants.SLEEP);
      
      // create test files
      selectRootOfWorkspaceTree();
      createSaveAndCloseFile(MenuCommands.New.XML_FILE, TEST_FILE_1_2, 0);
      selectRootOfWorkspaceTree();
      createSaveAndCloseFile(MenuCommands.New.XML_FILE, UPPERCASE_TEST_FILE_1, 0);      
      selectRootOfWorkspaceTree();
      createSaveAndCloseFile(MenuCommands.New.XML_FILE, TEST_FILE_1, 0);      
      
      // create test folders
      selectRootOfWorkspaceTree();
      createFolder(TEST_FOLDER_1_2);
      selectRootOfWorkspaceTree();
      createFolder(UPPERCASE_TEST_FOLDER_1);
      selectRootOfWorkspaceTree();
      createFolder(TEST_FOLDER_1);      

      checkItemOrderingInNavigationPanel();      

      // test ordering within the Navigation Panel after the refreshing root folder
      selectRootOfWorkspaceTree();
      runToolbarButton(ToolbarCommands.File.REFRESH);
      Thread.sleep(TestConstants.IDE_INITIALIZATION_PERIOD);      
      
      // test ordering within the Search Panel
      selectRootOfWorkspaceTree();
      performSearch("/", "", MimeType.TEXT_XML);
      
      checkItemOrderngInSearchResultPanel();
   }

   private void checkItemOrderingInNavigationPanel() throws Exception
   {
      assertElementPresentInWorkspaceTree(UPPERCASE_TEST_FOLDER_1);
      assertEquals(UPPERCASE_TEST_FOLDER_1, getItemNameFromWorkspaceTree(1));
      
      assertElementPresentInWorkspaceTree(TEST_FOLDER_1);
      assertEquals(TEST_FOLDER_1, getItemNameFromWorkspaceTree(2));
      
      assertElementPresentInWorkspaceTree(TEST_FOLDER_1_2);
      assertEquals(TEST_FOLDER_1_2, getItemNameFromWorkspaceTree(3));
      
      assertElementPresentInWorkspaceTree(UPPERCASE_TEST_FILE_1);
      assertEquals(UPPERCASE_TEST_FILE_1, getItemNameFromWorkspaceTree(4));
      
      assertElementPresentInWorkspaceTree(TEST_FILE_1);
      assertEquals(TEST_FILE_1, getItemNameFromWorkspaceTree(5));
      
      assertElementPresentInWorkspaceTree(TEST_FILE_1_2);
      assertEquals(TEST_FILE_1_2, getItemNameFromWorkspaceTree(6));
   }
   
   private void checkItemOrderngInSearchResultPanel() throws Exception
   {
      assertElementPresentSearchResultsTree(UPPERCASE_TEST_FILE_1);
      assertEquals(UPPERCASE_TEST_FILE_1, getItemNameFromSearchResultsTree(1));
      
      assertElementPresentSearchResultsTree(TEST_FILE_1);
      assertEquals(TEST_FILE_1, getItemNameFromSearchResultsTree(2));
      
      assertElementPresentSearchResultsTree(TEST_FILE_1_2);
      assertEquals(TEST_FILE_1_2, getItemNameFromSearchResultsTree(3));
   }   
   
   @AfterClass
   public static void tearDown() throws Exception
   {
      CloseFileUtils.closeTab(0);
      cleanRepository(REST_CONTEXT + "/" + WEBDAV_CONTEXT + "/" + REPO_NAME + "/" + WS_NAME);
   }   
}
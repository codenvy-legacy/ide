/*
 * Copyright (C) 2011 eXo Platform SAS.
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

import static junit.framework.Assert.assertTrue;

import org.exoplatform.common.http.client.ModuleException;
import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.TestConstants;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;

/**
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id:  Jan 17, 2011 5:04:41 PM anya $
 *
 */
public class CloseSelectedItemTest extends BaseTest
{
   private final static String ROOT_FOLDER = CloseSelectedItemTest.class.getSimpleName();

   private final static String FOLDER1 = "Folder1";

   private final static String FOLDER1_1 = "Folder1_1";

   private final static String FOLDER1_2 = "Folder1_2";

   private final static String FOLDER2 = "Folder2";

   private final static String FOLDER2_1 = "Folder2_1";

   private final static String FOLDER2_2 = "Folder2_2";

   private final static String FOLDER2_3 = "Folder2_3";

   private final static String FILE1 = "File1";

   private final static String FILE2 = "File2";

   private final static String FILE3 = "File3";

   private final static String URL = BASE_URL + REST_CONTEXT + "/" + WEBDAV_CONTEXT + "/" + REPO_NAME + "/" + WS_NAME
      + "/";

   @BeforeClass
   public static void setUp()
   {
      try
      {
         VirtualFileSystemUtils.mkcol(URL + ROOT_FOLDER);
         VirtualFileSystemUtils.mkcol(URL + ROOT_FOLDER + "/" + FOLDER1);
         VirtualFileSystemUtils.mkcol(URL + ROOT_FOLDER + "/" + FOLDER1 + "/" + FOLDER1_1);
         VirtualFileSystemUtils.mkcol(URL + ROOT_FOLDER + "/" + FOLDER1 + "/" + FOLDER1_2);
         VirtualFileSystemUtils.mkcol(URL + ROOT_FOLDER + "/" + FOLDER2);
         VirtualFileSystemUtils.mkcol(URL + ROOT_FOLDER + "/" + FOLDER2 + "/" + FOLDER2_1);
         VirtualFileSystemUtils.mkcol(URL + ROOT_FOLDER + "/" + FOLDER2 + "/" + FOLDER2_2);
         VirtualFileSystemUtils.mkcol(URL + ROOT_FOLDER + "/" + FOLDER2 + "/" + FOLDER2_3);
         VirtualFileSystemUtils.put(new byte[0], MimeType.TEXT_HTML, URL + ROOT_FOLDER + "/" + FOLDER1 + "/" + FILE1);
         VirtualFileSystemUtils.put(new byte[0], MimeType.TEXT_JAVASCRIPT, URL + ROOT_FOLDER + "/" + FOLDER2 + "/"
            + FILE2);
         VirtualFileSystemUtils.put(new byte[0], MimeType.TEXT_PLAIN, URL + ROOT_FOLDER + "/" + FILE3);
      }
      catch (IOException e)
      {
         e.printStackTrace();
      }
      catch (ModuleException e)
      {
         e.printStackTrace();
      }
   }

   /**
    * Test selection in sub folders, which are closed. 
    * 
    * @throws Exception
    */
   @Test
   public void testSelectSubFolders() throws Exception
   {
      Thread.sleep(TestConstants.SLEEP);
      openOrCloseFolder(ROOT_FOLDER);

      //Select one first sub folder
      selectItemInWorkspaceTree(FOLDER1);
      //Check status bar displays path to selected folder:
      String statusBarText = getStatusbarText();
      assertTrue(statusBarText.startsWith(WS_NAME + "/" + ROOT_FOLDER + "/" + FOLDER1));
      //Select second folder
      selenium.controlKeyDown();
      Thread.sleep(TestConstants.TYPE_DELAY_PERIOD);
      selectItemInWorkspaceTree(FOLDER2);
      selenium.controlKeyUp();
      //Check status bar number of selected folders:
      statusBarText = getStatusbarText();
      assertTrue(statusBarText.startsWith("Selected: 2 items"));

      //Close parent folder:
      openOrCloseFolder(ROOT_FOLDER);
      //Check the state of IDE when no selection in the Browser tree:
      statusBarText = getStatusbarText();
      assertTrue(statusBarText.startsWith("No items selected!"));
      //Try to create folder (has to be unavailable)
      checkCreateFolderButtonDisabled();

      //Select sub folder
      selectItemInWorkspaceTree(ROOT_FOLDER);
      statusBarText = getStatusbarText();
      assertTrue(statusBarText.startsWith(WS_NAME + "/" + ROOT_FOLDER));
      //Close workspace node
      openOrCloseFolder(WS_NAME);
      //Check the state of IDE when no selection in the Browser tree:
      statusBarText = getStatusbarText();
      assertTrue(statusBarText.startsWith("No items selected!"));
      checkCreateFolderButtonDisabled();

      //Open workspace node
      openOrCloseFolder(WS_NAME);
      //Check the state of IDE when no selection in the Browser tree:
      statusBarText = getStatusbarText();
      assertTrue(statusBarText.startsWith("No items selected!"));
      checkCreateFolderButtonDisabled();
   }

   /**
    * Tests selection files in the tree when they are selected 
    * in sub folders and then closed.
    * 
    * @throws Exception
    */
   @Test
   public void testSelectSubFiles() throws Exception
   {
      selenium.refresh();
      selenium.waitForPageToLoad("" + TestConstants.IDE_LOAD_PERIOD);
      Thread.sleep(TestConstants.SLEEP);

      //Open the tree folders:
      openOrCloseFolder(ROOT_FOLDER);
      openOrCloseFolder(FOLDER1);
      openOrCloseFolder(FOLDER2);
      //Select files
      selectItemInWorkspaceTree(FILE1);
      selenium.controlKeyDown();
      selectItemInWorkspaceTree(FILE2);
      selectItemInWorkspaceTree(FILE3);
      selenium.controlKeyUp();

      //Check status bar number of selected folders:
      String statusBarText = getStatusbarText();
      assertTrue(statusBarText.startsWith("Selected: 3 items"));

      //Close sub folder:
      openOrCloseFolder(FOLDER1);
      //Check status bar number of selected folders:
      statusBarText = getStatusbarText();
      assertTrue(statusBarText.startsWith("Selected: 2 items"));

      //Close second sub folder:
      openOrCloseFolder(FOLDER2);
      //Check selected item in browser tree:
      statusBarText = getStatusbarText();
      assertTrue(statusBarText.startsWith(WS_NAME + "/" + ROOT_FOLDER));

      //Close workspace:
      openCloseRootWorkspace();
      //Check the state of IDE when no selection in the Browser tree:
      statusBarText = getStatusbarText();
      assertTrue(statusBarText.startsWith("No items selected!"));
      //Try to create folder (has to be unavailable)
      checkCreateFolderButtonDisabled();
   }

   /**
    * Tests selection in the tree when items are selected in sub folders 
    * on different levels and then closed.
    * 
    * @throws Exception
    */
   @Test
   public void testSelectSubFoldersOnDifferentLevels() throws Exception
   {
      selenium.refresh();
      selenium.waitForPageToLoad("" + TestConstants.IDE_LOAD_PERIOD);
      Thread.sleep(TestConstants.SLEEP);

      //Open the tree folders:
      openOrCloseFolder(ROOT_FOLDER);
      openOrCloseFolder(FOLDER1);
      openOrCloseFolder(FOLDER2);

      //Select one first sub folder
      selectItemInWorkspaceTree(FOLDER1_1);
      //Check status bar displays path to selected folder:
      String statusBarText = getStatusbarText();
      assertTrue(statusBarText.startsWith(WS_NAME + "/" + ROOT_FOLDER + "/" + FOLDER1 + "/" + FOLDER1_1));
      //Select second folder
      selenium.controlKeyDown();
      Thread.sleep(TestConstants.TYPE_DELAY_PERIOD);
      selectItemInWorkspaceTree(FOLDER1_2);
      //Check status bar number of selected folders:
      statusBarText = getStatusbarText();
      assertTrue(statusBarText.startsWith("Selected: 2 items"));

      //Select third folder
      selectItemInWorkspaceTree(FOLDER2_1);
      //Check status bar number of selected folders:
      statusBarText = getStatusbarText();
      assertTrue(statusBarText.startsWith("Selected: 3 items"));

      //Select fourth folder
      selectItemInWorkspaceTree(FOLDER2_2);
      //Check status bar number of selected folders:
      statusBarText = getStatusbarText();
      assertTrue(statusBarText.startsWith("Selected: 4 items"));
      selenium.controlKeyUp();

      //Close one parent folder:
      openOrCloseFolder(FOLDER1);
      //Check status bar number of selected folders:
      statusBarText = getStatusbarText();
      assertTrue(statusBarText.startsWith("Selected: 2 items"));

      //Close second parent folder:
      openOrCloseFolder(FOLDER2);
      //Check the state of IDE when no selection in the Browser tree:
      statusBarText = getStatusbarText();
      assertTrue(statusBarText.startsWith("No items selected!"));
      //Try to create folder (has to be unavailable)
      checkCreateFolderButtonDisabled();

      //Select parent folder and subfolders:
      //Open parent folder
      openOrCloseFolder(FOLDER1);
      selectItemInWorkspaceTree(FOLDER1);
      selenium.controlKeyDown();
      selectItemInWorkspaceTree(FOLDER1_1);
      selectItemInWorkspaceTree(FOLDER1_2);
      selenium.controlKeyUp();

      //Check status bar number of selected folders:
      statusBarText = getStatusbarText();
      assertTrue(statusBarText.startsWith("Selected: 3 items"));

      //Close parent folder:
      openOrCloseFolder(FOLDER1);
      //Check status bar displays path to selected folder:
      statusBarText = getStatusbarText();
      assertTrue(statusBarText.startsWith(WS_NAME + "/" + ROOT_FOLDER + "/" + FOLDER1));
   }

   private void checkCreateFolderButtonDisabled() throws Exception
   {
      IDE.toolbar().runCommand("New");
      Thread.sleep(TestConstants.REDRAW_PERIOD);
      assertTrue(selenium
         .isElementPresent("//table[@class=\"exo-popupMenuTable\"]//td[@class=\"exo-popupMenuTitleFieldDisabled\"]//nobr[text()=\"Folder...\"]"));
      selenium.mouseDown("//div[@class='exo-lockLayer']/");
      Thread.sleep(TestConstants.REDRAW_PERIOD);
   }

   @AfterClass
   public static void tearDown()
   {
      try
      {
         VirtualFileSystemUtils.delete(URL + ROOT_FOLDER);
      }
      catch (IOException e)
      {
         e.printStackTrace();
      }
      catch (ModuleException e)
      {
         e.printStackTrace();
      }
   }
}

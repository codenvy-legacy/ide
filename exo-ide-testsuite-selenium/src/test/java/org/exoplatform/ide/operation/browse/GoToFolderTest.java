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

import java.io.IOException;

import org.exoplatform.common.http.client.ModuleException;
import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.MenuCommands;
import org.exoplatform.ide.TestConstants;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * IDE-96 Go to folder test
 * 
 * Created by The eXo Platform SAS.
 *	
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id:   ${date} ${time}
 *
 */
public class GoToFolderTest extends BaseTest
{
   private final static String URL = BASE_URL + REST_CONTEXT + "/" + WEBDAV_CONTEXT + "/" + REPO_NAME + "/" + WS_NAME
      + "/";

   private final static String FOLDER_1 = "GoToFolderTest1";

   private final static String FOLDER_2 = "GoToFolderTest2";

   private final static String FILE_1 = "GoToFolderTestFile1.xml";

   private final static String FILE_2 = "GoToFolderTestFile2.xml";

   @BeforeClass
   public static void setUp()
   {
      String filePath = "src/test/resources/org/exoplatform/ide/operation/file/empty.xml";
      try
      {
         VirtualFileSystemUtils.mkcol(URL + FOLDER_1);
         VirtualFileSystemUtils.mkcol(URL + FOLDER_2);
         VirtualFileSystemUtils.put(filePath, MimeType.TEXT_XML, URL + FOLDER_1 + "/" + FILE_1);
         VirtualFileSystemUtils.put(filePath, MimeType.TEXT_XML, URL + FOLDER_2 + "/" + FILE_2);

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

   @Test
   public void testGoToFolder() throws Exception
   {
      waitForRootElement();
      IDE.menu().checkCommandEnabled(MenuCommands.View.VIEW, MenuCommands.View.GO_TO_FOLDER, false);

      IDE.navigator().selectItem(WS_URL);
      IDE.menu().runCommand(MenuCommands.File.FILE, MenuCommands.File.REFRESH);
      //waitForRootElement();

      //Open first folder and file in it
      waitForRootElement();
      IDE.navigator().clickOpenIconOfFolder(WS_URL + FOLDER_1 + "/");
      waitForRootElement();
      IDE.navigator().openFileFromNavigationTreeWithCodeEditor(WS_URL + FOLDER_1 + "/" + FILE_1, false);
      waitForRootElement();
      waitForRootElement();
      //Close first folder
      IDE.navigator().clickOpenIconOfFolder(WS_URL + FOLDER_1 + "/");
      IDE.navigator().assertItemNotPresent(URL + FOLDER_1 + "/" + FILE_1);

      waitForRootElement();
      IDE.navigator().clickOpenIconOfFolder(WS_URL + FOLDER_2 + "/");
      Thread.sleep(TestConstants.SLEEP_SHORT);
      //Select second file
      IDE.navigator().selectItem(URL + FOLDER_2 + "/" + FILE_2);
      //Go to folder with first file
      IDE.menu().runCommand(MenuCommands.View.VIEW, MenuCommands.View.GO_TO_FOLDER);
      waitForRootElement();
      //Check file is shown in tree
      //TODO check selected state
      IDE.navigator().assertItemPresent(URL + FOLDER_1 + "/" + FILE_1);

      IDE.navigator().selectRootOfWorkspace();
      IDE.toolbar().runCommand("Refresh Selected Folder");

      IDE.navigator().clickOpenIconOfFolder(WS_URL + FOLDER_2 + "/");
      waitForRootElement();
      IDE.navigator().openFileFromNavigationTreeWithCodeEditor(WS_URL + FOLDER_2 + "/" + FILE_2, false);
      waitForRootElement();
      //Go to folder with first file
      IDE.menu().runCommand(MenuCommands.View.VIEW, MenuCommands.View.GO_TO_FOLDER);
      waitForRootElement();
      //TODO check selected state
      IDE.navigator().assertItemPresent(URL + FOLDER_2 + "/" + FILE_2);

      //Close opened tabs
      IDE.editor().closeTab(0);
      Thread.sleep(TestConstants.REDRAW_PERIOD);
      IDE.editor().closeTab(0);
      waitForRootElement();
      IDE.menu().checkCommandEnabled(MenuCommands.View.VIEW, MenuCommands.View.GO_TO_FOLDER, false);
   }

   @Test
   public void testGoToFolderSearchPanel() throws Exception
   {
      selenium.refresh();
      selenium.waitForPageToLoad("30000");
      Thread.sleep(TestConstants.PAGE_LOAD_PERIOD);
      waitForRootElement();
      //Close root workspace folder
      IDE.navigator().selectItem(WS_URL);
      IDE.menu().runCommand(MenuCommands.File.FILE, MenuCommands.File.REFRESH);
      //Thread.sleep(TestConstants.SLEEP_SHORT);

      IDE.navigator().clickOpenIconOfFolder(WS_URL);
      Thread.sleep(TestConstants.SLEEP_SHORT);
      IDE.navigator().assertItemNotPresent(URL + FOLDER_1 + "/" + FILE_1);
      IDE.navigator().assertItemNotPresent(URL + FOLDER_2 + "/" + FILE_2);
      IDE.navigator().assertItemNotPresent(URL + FOLDER_1 + "/");
      IDE.navigator().assertItemNotPresent(URL + FOLDER_2 + "/");

      performSearch("/", "", "");
      waitForRootElement();
      //Check files are found
      IDE.navigator().selectItemInSerchTree(URL + FOLDER_1 + "/" + FILE_1);
      IDE.navigator().selectItemInSerchTree(URL + FOLDER_2 + "/" + FILE_2);
      //Open second file
      //      selectItemInSearchResultsTree(FILE_2);
      openFileFromSearchResultsWithCodeEditor(URL + FOLDER_2 + "/" + FILE_2);
      //Go to folder with second file
      waitForRootElement();
      IDE.menu().checkCommandEnabled(MenuCommands.View.VIEW, MenuCommands.View.GO_TO_FOLDER, true);
      IDE.menu().runCommand(MenuCommands.View.VIEW, MenuCommands.View.GO_TO_FOLDER);
      waitForRootElement();

      //TODO check selected
      IDE.navigator().assertItemPresent(URL + FOLDER_2 + "/" + FILE_2);
      IDE.navigator().assertItemNotPresent(URL + FOLDER_1 + "/" + FILE_1);
      IDE.navigator().assertItemPresent(URL + FOLDER_1 + "/");
      IDE.navigator().assertItemPresent(URL + FOLDER_2 + "/");
   }

   @AfterClass
   public static void tearDown()
   {
      try
      {
         VirtualFileSystemUtils.delete(URL + FOLDER_1);
         VirtualFileSystemUtils.delete(URL + FOLDER_2);
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

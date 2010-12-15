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
package org.exoplatform.ide.search;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.exoplatform.common.http.client.ModuleException;
import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.MenuCommands;
import org.exoplatform.ide.TestConstants;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.exoplatform.ide.utils.AbstractTextUtil;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: $
 *
 */
public class SearchLoadFileTest extends BaseTest
{

   private final String restFileName = "Example.groovy";

   private final String gadgetFileName = "gadget.xml";

   private final String gadgetFileContent =
      "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n" + "<Module>\n" + "<ModulePrefs title=\"Hello World!\" />\n"
         + "<Content type=\"html\">\n" + "<![CDATA[ Hello, world!\n" + "Hello, world!\n" + "]]></Content></Module>";

   private final static String URL = BASE_URL + REST_CONTEXT + "/" + WEBDAV_CONTEXT + "/" + REPO_NAME + "/" + WS_NAME + "/";

   private final static String TEST_FOLDER = "testFolder";

   @BeforeClass
   public static void setUp()
   {
      try
      {
         VirtualFileSystemUtils.mkcol(URL + TEST_FOLDER);
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
    * IDE-33:Load found file in the Content Panel
    * 
    * @throws Exception
    */
   @Test
   public void testLoadFoundFile() throws Exception
   {
      Thread.sleep(TestConstants.PAGE_LOAD_PERIOD);
      
      selectItemInWorkspaceTree(TEST_FOLDER);
      IDE.toolbar().runCommandFromNewPopupMenu(MenuCommands.New.REST_SERVICE_FILE);
      Thread.sleep(TestConstants.SLEEP);
      saveAsByTopMenu(restFileName);
      Thread.sleep(TestConstants.SLEEP);
      assertElementPresentInWorkspaceTree(restFileName);
      IDE.editor().closeTab(0);
      selectRootOfWorkspaceTree();

      performSearch("/", "", "");
      Thread.sleep(TestConstants.SLEEP);
      assertElementPresentSearchResultsTree(restFileName);

      openFileFromSearchResultsWithCodeEditor(restFileName);
      Thread.sleep(TestConstants.SLEEP);
      assertEquals(restFileName, IDE.editor().getTabTitle(0));
     
      IDE.toolbar().checkButtonEnabled("Delete Item(s)...", false);
      IDE.toolbar().checkButtonEnabled("Cut Selected Item(s)", false);
      IDE.toolbar().checkButtonEnabled("Copy Selected Item(s)", false);
      IDE.toolbar().checkButtonEnabled("Paste Selected Item(s)", false);
      IDE.toolbar().checkButtonEnabled("Refresh Selected Folder", false);

      IDE.menu().checkCommandEnabled(MenuCommands.File.FILE, MenuCommands.File.DELETE, false);
      IDE.menu().checkCommandEnabled(MenuCommands.File.FILE, MenuCommands.File.DOWNLOAD, false);
      IDE.menu().checkCommandEnabled(MenuCommands.File.FILE, MenuCommands.File.DOWNLOAD_ZIPPED_FOLDER, false);
      IDE.menu().checkCommandEnabled(MenuCommands.File.FILE, MenuCommands.File.OPEN_LOCAL_FILE, false);
      IDE.menu().checkCommandEnabled(MenuCommands.File.FILE, MenuCommands.File.OPEN_WITH, true);
      IDE.menu().checkCommandEnabled(MenuCommands.File.FILE, MenuCommands.File.REFRESH, false);
      IDE.menu().checkCommandEnabled(MenuCommands.File.FILE, MenuCommands.File.RENAME, false);
      IDE.menu().checkCommandEnabled(MenuCommands.File.FILE, MenuCommands.File.SAVE, false);
      IDE.menu().checkCommandEnabled(MenuCommands.File.FILE, MenuCommands.File.SAVE_ALL, false);
      IDE.menu().checkCommandEnabled(MenuCommands.File.FILE, MenuCommands.File.SAVE_AS, false);
      IDE.menu().checkCommandEnabled(MenuCommands.File.FILE, MenuCommands.File.SAVE_AS_TEMPLATE, true);
      IDE.menu().checkCommandEnabled(MenuCommands.File.FILE, MenuCommands.File.SEARCH, true);
      IDE.menu().checkCommandEnabled(MenuCommands.File.FILE, MenuCommands.File.UPLOAD_FILE, false);

      IDE.menu().runCommand(MenuCommands.View.VIEW, MenuCommands.View.GO_TO_FOLDER);
      assertElementPresentInWorkspaceTree(restFileName);
      //TODO check selected

      IDE.toolbar().checkButtonEnabled("Delete Item(s)...", true);
      IDE.toolbar().checkButtonEnabled("Cut Selected Item(s)", true);
      IDE.toolbar().checkButtonEnabled("Copy Selected Item(s)", true);
      IDE.toolbar().checkButtonEnabled("Paste Selected Item(s)", false);
      IDE.toolbar().checkButtonEnabled("Refresh Selected Folder", true);

      IDE.menu().checkCommandEnabled(MenuCommands.File.FILE, MenuCommands.File.DELETE, true);
      IDE.menu().checkCommandEnabled(MenuCommands.File.FILE, MenuCommands.File.DOWNLOAD, true);
      IDE.menu().checkCommandEnabled(MenuCommands.File.FILE, MenuCommands.File.DOWNLOAD_ZIPPED_FOLDER, false);
      IDE.menu().checkCommandEnabled(MenuCommands.File.FILE, MenuCommands.File.OPEN_LOCAL_FILE, true);
      IDE.menu().checkCommandEnabled(MenuCommands.File.FILE, MenuCommands.File.OPEN_WITH, true);
      IDE.menu().checkCommandEnabled(MenuCommands.File.FILE, MenuCommands.File.REFRESH, true);
      IDE.menu().checkCommandEnabled(MenuCommands.File.FILE, MenuCommands.File.RENAME, true);
      IDE.menu().checkCommandEnabled(MenuCommands.File.FILE, MenuCommands.File.SAVE, false);
      IDE.menu().checkCommandEnabled(MenuCommands.File.FILE, MenuCommands.File.SAVE_ALL, false);
      IDE.menu().checkCommandEnabled(MenuCommands.File.FILE, MenuCommands.File.SAVE_AS, true);
      IDE.menu().checkCommandEnabled(MenuCommands.File.FILE, MenuCommands.File.SAVE_AS_TEMPLATE, true);
      IDE.menu().checkCommandEnabled(MenuCommands.File.FILE, MenuCommands.File.SEARCH, true);
      IDE.menu().checkCommandEnabled(MenuCommands.File.FILE, MenuCommands.File.UPLOAD_FILE, true);

      IDE.editor().closeTab(0);

      IDE.toolbar().runCommandFromNewPopupMenu(MenuCommands.New.GOOGLE_GADGET_FILE);
      Thread.sleep(TestConstants.SLEEP);
      deleteLinesInEditor(7);
      AbstractTextUtil.getInstance().typeTextToEditor(TestConstants.CODEMIRROR_EDITOR_LOCATOR, gadgetFileContent);
      saveAsByTopMenu(gadgetFileName);
      Thread.sleep(TestConstants.SLEEP);
      assertElementPresentInWorkspaceTree(gadgetFileName);
      IDE.editor().closeTab(0);
      selectRootOfWorkspaceTree();

      performSearch("/", "", "");
      Thread.sleep(TestConstants.SLEEP);
      assertElementPresentSearchResultsTree(gadgetFileName);

      openFileFromSearchResultsWithCodeEditor(gadgetFileName);
      Thread.sleep(TestConstants.SLEEP);
      assertEquals(gadgetFileName, IDE.editor().getTabTitle(0));

      IDE.toolbar().checkButtonEnabled("Delete Item(s)...", false);
      IDE.toolbar().checkButtonEnabled("Cut Selected Item(s)", false);
      IDE.toolbar().checkButtonEnabled("Copy Selected Item(s)", false);
      IDE.toolbar().checkButtonEnabled("Paste Selected Item(s)", false);
      IDE.toolbar().checkButtonEnabled("Refresh Selected Folder", false);

      IDE.menu().checkCommandEnabled(MenuCommands.File.FILE, MenuCommands.File.DELETE, false);
      IDE.menu().checkCommandEnabled(MenuCommands.File.FILE, MenuCommands.File.DOWNLOAD, false);
      IDE.menu().checkCommandEnabled(MenuCommands.File.FILE, MenuCommands.File.DOWNLOAD_ZIPPED_FOLDER, false);
      IDE.menu().checkCommandEnabled(MenuCommands.File.FILE, MenuCommands.File.OPEN_LOCAL_FILE, false);
      IDE.menu().checkCommandEnabled(MenuCommands.File.FILE, MenuCommands.File.OPEN_WITH, true);
      IDE.menu().checkCommandEnabled(MenuCommands.File.FILE, MenuCommands.File.REFRESH, false);
      IDE.menu().checkCommandEnabled(MenuCommands.File.FILE, MenuCommands.File.RENAME, false);
      IDE.menu().checkCommandEnabled(MenuCommands.File.FILE, MenuCommands.File.SAVE, false);
      IDE.menu().checkCommandEnabled(MenuCommands.File.FILE, MenuCommands.File.SAVE_ALL, false);
      IDE.menu().checkCommandEnabled(MenuCommands.File.FILE, MenuCommands.File.SAVE_AS, false);
      IDE.menu().checkCommandEnabled(MenuCommands.File.FILE, MenuCommands.File.SAVE_AS_TEMPLATE, true);
      IDE.menu().checkCommandEnabled(MenuCommands.File.FILE, MenuCommands.File.SEARCH, true);
      IDE.menu().checkCommandEnabled(MenuCommands.File.FILE, MenuCommands.File.UPLOAD_FILE, false);

      IDE.menu().runCommand(MenuCommands.View.VIEW, MenuCommands.View.GO_TO_FOLDER);
      assertElementPresentInWorkspaceTree(restFileName);
      //TODO check selected

      IDE.toolbar().checkButtonEnabled("Delete Item(s)...", true);
      IDE.toolbar().checkButtonEnabled("Cut Selected Item(s)", true);
      IDE.toolbar().checkButtonEnabled("Copy Selected Item(s)", true);
      IDE.toolbar().checkButtonEnabled("Paste Selected Item(s)", false);
      IDE.toolbar().checkButtonEnabled("Refresh Selected Folder", true);

      IDE.menu().checkCommandEnabled(MenuCommands.File.FILE, MenuCommands.File.DELETE, true);
      IDE.menu().checkCommandEnabled(MenuCommands.File.FILE, MenuCommands.File.DOWNLOAD, true);
      IDE.menu().checkCommandEnabled(MenuCommands.File.FILE, MenuCommands.File.DOWNLOAD_ZIPPED_FOLDER, false);
      IDE.menu().checkCommandEnabled(MenuCommands.File.FILE, MenuCommands.File.OPEN_LOCAL_FILE, true);
      IDE.menu().checkCommandEnabled(MenuCommands.File.FILE, MenuCommands.File.OPEN_WITH, true);
      IDE.menu().checkCommandEnabled(MenuCommands.File.FILE, MenuCommands.File.REFRESH, true);
      IDE.menu().checkCommandEnabled(MenuCommands.File.FILE, MenuCommands.File.RENAME, true);
      IDE.menu().checkCommandEnabled(MenuCommands.File.FILE, MenuCommands.File.SAVE, false);
      IDE.menu().checkCommandEnabled(MenuCommands.File.FILE, MenuCommands.File.SAVE_ALL, false);
      IDE.menu().checkCommandEnabled(MenuCommands.File.FILE, MenuCommands.File.SAVE_AS, true);
      IDE.menu().checkCommandEnabled(MenuCommands.File.FILE, MenuCommands.File.SAVE_AS_TEMPLATE, true);
      IDE.menu().checkCommandEnabled(MenuCommands.File.FILE, MenuCommands.File.SEARCH, true);
      IDE.menu().checkCommandEnabled(MenuCommands.File.FILE, MenuCommands.File.UPLOAD_FILE, true);

      selenium.controlKeyDown();
      selectItemInWorkspaceTree(restFileName);
      selenium.controlKeyUp();
      deleteSelectedItems();
      Thread.sleep(TestConstants.SLEEP);

      assertElementNotPresentInWorkspaceTree(restFileName);
      assertElementNotPresentInWorkspaceTree(gadgetFileName);

      Thread.sleep(TestConstants.SLEEP);
   }
   
   @AfterClass
   public static void tearDown()
   {
      cleanRepository(URL + TEST_FOLDER);
   }
}

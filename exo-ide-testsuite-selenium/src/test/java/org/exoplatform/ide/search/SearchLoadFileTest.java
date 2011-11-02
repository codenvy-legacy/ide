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

import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.MenuCommands;
import org.exoplatform.ide.TestConstants;
import org.exoplatform.ide.ToolbarCommands;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;

/**
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: $
 *
 */
public class SearchLoadFileTest extends BaseTest
{

   private final String restFileName = "Example.grs";

   private final String gadgetFileName = "gadget.xml";

   private final String gadgetFileContent = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n" + "<Module>\n"
      + "<ModulePrefs title=\"Hello World!\" />\n" + "<Content type=\"html\">\n" + "<![CDATA[ Hello, world!\n"
      + "Hello, world!\n" + "]]></Content></Module>";

   private final static String URL = BASE_URL + REST_CONTEXT + "/" + WEBDAV_CONTEXT + "/" + REPO_NAME + "/" + WS_NAME
      + "/";

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
   }

   /**
    * IDE-33:Load found file in the Content Panel
    * 
    * @throws Exception
    */
   @Test
   public void testLoadFoundFile() throws Exception
   {
      IDE.WORKSPACE.waitForRootItem();

      IDE.WORKSPACE.selectItem(WS_URL + TEST_FOLDER + "/");
      IDE.TOOLBAR.runCommandFromNewPopupMenu(MenuCommands.New.REST_SERVICE_FILE);
      IDE.EDITOR.waitTabPresent(0);
      saveAsByTopMenu(restFileName);
      IDE.WORKSPACE.waitForItem(WS_URL + TEST_FOLDER + "/" + restFileName);
      IDE.EDITOR.closeFile(0);
      IDE.WORKSPACE.selectRootItem();

      IDE.SEARCH.performSearch("/", "", "");
      IDE.SEARCH.waitSearchResultsPresent();
      IDE.NAVIGATION.assertItemVisibleInSearchTree(WS_URL + TEST_FOLDER + "/" + restFileName);

      IDE.WORKSPACE.doubleClickOnFileFromSearchTab(WS_URL + TEST_FOLDER + "/" + restFileName);
      IDE.EDITOR.waitTabPresent(0);
      assertEquals(restFileName, IDE.EDITOR.getTabTitle(0));

      IDE.TOOLBAR.assertButtonEnabled(ToolbarCommands.File.DELETE, false);
      IDE.TOOLBAR.assertButtonEnabled(ToolbarCommands.File.CUT_SELECTED_ITEM, false);
      IDE.TOOLBAR.assertButtonEnabled(ToolbarCommands.File.COPY_SELECTED_ITEM, false);
      IDE.TOOLBAR.assertButtonEnabled(ToolbarCommands.File.REFRESH, false);

      IDE.MENU.checkCommandEnabled(MenuCommands.File.FILE, MenuCommands.File.DELETE, false);
      IDE.MENU.checkCommandEnabled(MenuCommands.File.FILE, MenuCommands.File.DOWNLOAD, false);
      IDE.MENU.checkCommandEnabled(MenuCommands.File.FILE, MenuCommands.File.DOWNLOAD_ZIPPED_FOLDER, false);
      IDE.MENU.checkCommandEnabled(MenuCommands.File.FILE, MenuCommands.File.OPEN_LOCAL_FILE, false);
      IDE.MENU.checkCommandEnabled(MenuCommands.File.FILE, MenuCommands.File.REFRESH, false);
      IDE.MENU.checkCommandEnabled(MenuCommands.File.FILE, MenuCommands.File.RENAME, false);
      IDE.MENU.checkCommandEnabled(MenuCommands.File.FILE, MenuCommands.File.SAVE, false);
      IDE.MENU.checkCommandEnabled(MenuCommands.File.FILE, MenuCommands.File.SAVE_ALL, false);
      IDE.MENU.checkCommandEnabled(MenuCommands.File.FILE, MenuCommands.File.SAVE_AS, false);
      IDE.MENU.checkCommandEnabled(MenuCommands.File.FILE, MenuCommands.File.SAVE_AS_TEMPLATE, true);
      IDE.MENU.checkCommandEnabled(MenuCommands.File.FILE, MenuCommands.File.SEARCH, true);
      IDE.MENU.checkCommandEnabled(MenuCommands.File.FILE, MenuCommands.File.UPLOAD_FILE, false);

      IDE.MENU.runCommand(MenuCommands.View.VIEW, MenuCommands.View.GO_TO_FOLDER);
      IDE.WORKSPACE.waitForItem(WS_URL + TEST_FOLDER + "/" + restFileName);
      Thread.sleep(TestConstants.ANIMATION_PERIOD);
      //TODO add test selected

      IDE.TOOLBAR.assertButtonEnabled(ToolbarCommands.File.DELETE, true);
      IDE.TOOLBAR.assertButtonEnabled(ToolbarCommands.File.CUT_SELECTED_ITEM, true);
      IDE.TOOLBAR.assertButtonEnabled(ToolbarCommands.File.COPY_SELECTED_ITEM, true);
      IDE.TOOLBAR.assertButtonEnabled(ToolbarCommands.File.REFRESH, true);

      IDE.MENU.checkCommandEnabled(MenuCommands.File.FILE, MenuCommands.File.DELETE, true);
      IDE.MENU.checkCommandEnabled(MenuCommands.File.FILE, MenuCommands.File.DOWNLOAD, true);
      IDE.MENU.checkCommandEnabled(MenuCommands.File.FILE, MenuCommands.File.DOWNLOAD_ZIPPED_FOLDER, false);
      IDE.MENU.checkCommandEnabled(MenuCommands.File.FILE, MenuCommands.File.OPEN_LOCAL_FILE, true);
      IDE.MENU.checkCommandEnabled(MenuCommands.File.FILE, MenuCommands.File.REFRESH, true);
      IDE.MENU.checkCommandEnabled(MenuCommands.File.FILE, MenuCommands.File.RENAME, true);
      IDE.MENU.checkCommandEnabled(MenuCommands.File.FILE, MenuCommands.File.SAVE, false);
      IDE.MENU.checkCommandEnabled(MenuCommands.File.FILE, MenuCommands.File.SAVE_ALL, false);
      IDE.MENU.checkCommandEnabled(MenuCommands.File.FILE, MenuCommands.File.SAVE_AS, true);
      IDE.MENU.checkCommandEnabled(MenuCommands.File.FILE, MenuCommands.File.SAVE_AS_TEMPLATE, true);
      IDE.MENU.checkCommandEnabled(MenuCommands.File.FILE, MenuCommands.File.SEARCH, true);
      IDE.MENU.checkCommandEnabled(MenuCommands.File.FILE, MenuCommands.File.UPLOAD_FILE, true);

      IDE.EDITOR.closeFile(0);

      IDE.TOOLBAR.runCommandFromNewPopupMenu(MenuCommands.New.GOOGLE_GADGET_FILE);
      IDE.EDITOR.waitTabPresent(0);

      IDE.EDITOR.deleteLinesInEditor(7);

      IDE.EDITOR.typeTextIntoEditor(0, gadgetFileContent);
      IDE.NAVIGATION.saveFileAs(gadgetFileName);
      Thread.sleep(TestConstants.SLEEP);
      IDE.NAVIGATION.assertItemVisible(WS_URL + TEST_FOLDER + "/" + gadgetFileName);
      IDE.EDITOR.closeFile(0);
      IDE.WORKSPACE.selectRootItem();

      IDE.SEARCH.performSearch("/", "", "");
      IDE.SEARCH.waitSearchResultsPresent();
      IDE.NAVIGATION.assertItemVisibleInSearchTree(WS_URL + TEST_FOLDER + "/" + gadgetFileName);

      IDE.WORKSPACE.doubleClickOnFileFromSearchTab(WS_URL + TEST_FOLDER + "/" + gadgetFileName);
      IDE.EDITOR.waitTabPresent(0);
      assertEquals(gadgetFileName, IDE.EDITOR.getTabTitle(0));

      IDE.TOOLBAR.assertButtonEnabled(ToolbarCommands.File.DELETE, false);
      IDE.TOOLBAR.assertButtonEnabled(ToolbarCommands.File.CUT_SELECTED_ITEM, false);
      IDE.TOOLBAR.assertButtonEnabled(ToolbarCommands.File.COPY_SELECTED_ITEM, false);
      IDE.TOOLBAR.assertButtonEnabled(ToolbarCommands.File.REFRESH, false);

      IDE.MENU.checkCommandEnabled(MenuCommands.File.FILE, MenuCommands.File.DELETE, false);
      IDE.MENU.checkCommandEnabled(MenuCommands.File.FILE, MenuCommands.File.DOWNLOAD, false);
      IDE.MENU.checkCommandEnabled(MenuCommands.File.FILE, MenuCommands.File.DOWNLOAD_ZIPPED_FOLDER, false);
      IDE.MENU.checkCommandEnabled(MenuCommands.File.FILE, MenuCommands.File.OPEN_LOCAL_FILE, false);
      IDE.MENU.checkCommandEnabled(MenuCommands.File.FILE, MenuCommands.File.REFRESH, false);
      IDE.MENU.checkCommandEnabled(MenuCommands.File.FILE, MenuCommands.File.RENAME, false);
      IDE.MENU.checkCommandEnabled(MenuCommands.File.FILE, MenuCommands.File.SAVE, false);
      IDE.MENU.checkCommandEnabled(MenuCommands.File.FILE, MenuCommands.File.SAVE_ALL, false);
      IDE.MENU.checkCommandEnabled(MenuCommands.File.FILE, MenuCommands.File.SAVE_AS, false);
      IDE.MENU.checkCommandEnabled(MenuCommands.File.FILE, MenuCommands.File.SAVE_AS_TEMPLATE, true);
      IDE.MENU.checkCommandEnabled(MenuCommands.File.FILE, MenuCommands.File.SEARCH, true);
      IDE.MENU.checkCommandEnabled(MenuCommands.File.FILE, MenuCommands.File.UPLOAD_FILE, false);

      IDE.MENU.runCommand(MenuCommands.View.VIEW, MenuCommands.View.GO_TO_FOLDER);
      //TODO test selected
      IDE.WORKSPACE.waitForItem(WS_URL + TEST_FOLDER + "/" + restFileName);
      Thread.sleep(TestConstants.ANIMATION_PERIOD);

      IDE.TOOLBAR.assertButtonEnabled(ToolbarCommands.File.DELETE, true);
      IDE.TOOLBAR.assertButtonEnabled(ToolbarCommands.File.CUT_SELECTED_ITEM, true);
      IDE.TOOLBAR.assertButtonEnabled(ToolbarCommands.File.COPY_SELECTED_ITEM, true);
      IDE.TOOLBAR.assertButtonEnabled(ToolbarCommands.File.REFRESH, true);

      IDE.MENU.checkCommandEnabled(MenuCommands.File.FILE, MenuCommands.File.DELETE, true);
      IDE.MENU.checkCommandEnabled(MenuCommands.File.FILE, MenuCommands.File.DOWNLOAD, true);
      IDE.MENU.checkCommandEnabled(MenuCommands.File.FILE, MenuCommands.File.DOWNLOAD_ZIPPED_FOLDER, false);
      IDE.MENU.checkCommandEnabled(MenuCommands.File.FILE, MenuCommands.File.OPEN_LOCAL_FILE, true);
      IDE.MENU.checkCommandEnabled(MenuCommands.File.FILE, MenuCommands.File.REFRESH, true);
      IDE.MENU.checkCommandEnabled(MenuCommands.File.FILE, MenuCommands.File.RENAME, true);
      IDE.MENU.checkCommandEnabled(MenuCommands.File.FILE, MenuCommands.File.SAVE, false);
      IDE.MENU.checkCommandEnabled(MenuCommands.File.FILE, MenuCommands.File.SAVE_ALL, false);
      IDE.MENU.checkCommandEnabled(MenuCommands.File.FILE, MenuCommands.File.SAVE_AS, true);
      IDE.MENU.checkCommandEnabled(MenuCommands.File.FILE, MenuCommands.File.SAVE_AS_TEMPLATE, true);
      IDE.MENU.checkCommandEnabled(MenuCommands.File.FILE, MenuCommands.File.SEARCH, true);
      IDE.MENU.checkCommandEnabled(MenuCommands.File.FILE, MenuCommands.File.UPLOAD_FILE, true);
   }

   @AfterClass
   public static void tearDown() throws IOException
   {
      VirtualFileSystemUtils.delete(WS_URL + TEST_FOLDER);
   }
}

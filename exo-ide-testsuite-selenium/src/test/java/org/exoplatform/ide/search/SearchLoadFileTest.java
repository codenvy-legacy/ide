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

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

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

import java.io.IOException;
import java.util.Map;

import javax.validation.constraints.AssertFalse;

/**
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: $
 *
 */
public class SearchLoadFileTest extends BaseTest
{

   private static final String PROJECT = SearchLoadFileTest.class.getSimpleName();

   private final static String restFileName = "Example.groovy";

   private final static String gadgetFileName = "gadget.xml";

   private final String gadgetFileContent = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n" + "<Module>\n"
      + "<ModulePrefs title=\"Hello World!\" />\n" + "<Content type=\"html\">\n" + "<![CDATA[ Hello, world!\n"
      + "Hello, world!\n" + "]]></Content></Module>";

   private final static String TEST_FOLDER = "testFolder";

   static String pathToGadget = "src/test/resources/org/exoplatform/ide/search/Example.grs";

   static String pathToGroovy = "src/test/resources/org/exoplatform/ide/search/gadget.xml";

   @BeforeClass
   public static void setUp()
   {
      try
      {
         Map<String, Link> project = VirtualFileSystemUtils.createDefaultProject(PROJECT);
         VirtualFileSystemUtils.mkcol(WS_URL + PROJECT + "/" + TEST_FOLDER);
         VirtualFileSystemUtils.put(pathToGadget, MimeType.APPLICATION_GROOVY, WS_URL + PROJECT + "/" + TEST_FOLDER
            + "/" + restFileName);
         VirtualFileSystemUtils.put(pathToGadget, MimeType.GOOGLE_GADGET, WS_URL + PROJECT + "/" + TEST_FOLDER + "/"
            + gadgetFileName);
      }
      catch (IOException e)
      {
      }
   }

   /**
    * IDE-33:Load found file in the Content Panel
    * 
    * @throws Exception
    */
   @Ignore
   @Test
   //TODO after fix IDE-1483, IDE-1484  test should be complete
   public void testLoadFoundFile() throws Exception
   {
      //step 1 open project an folders
      IDE.PROJECT.EXPLORER.waitOpened();
      IDE.PROJECT.OPEN.openProject(PROJECT);
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT);

      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + TEST_FOLDER);
      IDE.SEARCH.performSearch("/" + PROJECT, "", MimeType.APPLICATION_GROOVY);

   /*   IDE.SEARCH.waitSearchResultsOpened();
      IDE.SEARCH.isFilePresent(PROJECT + "/" + TEST_FOLDER + "/" + restFileName);
      IDE.SEARCH.selectItem(PROJECT + "/" + TEST_FOLDER + "/" + restFileName);
      IDE.SEARCH.doubleClickOnFile(PROJECT + "/" + TEST_FOLDER + "/" + restFileName);
      IDE.EDITOR.waitActiveFile(PROJECT + "/" + TEST_FOLDER + "/" + restFileName);*/

      assertFalse(IDE.TOOLBAR.isButtonEnabled(ToolbarCommands.File.DELETE));
      assertFalse(IDE.TOOLBAR.isButtonEnabled(ToolbarCommands.File.CUT_SELECTED_ITEM));
      assertFalse(IDE.TOOLBAR.isButtonEnabled(ToolbarCommands.File.COPY_SELECTED_ITEM));
      assertFalse(IDE.TOOLBAR.isButtonEnabled(ToolbarCommands.File.REFRESH));

      assertFalse(IDE.MENU.isCommandEnabled(MenuCommands.File.FILE, MenuCommands.File.DOWNLOAD));
      assertFalse(IDE.MENU.isCommandEnabled(MenuCommands.File.FILE, MenuCommands.File.DOWNLOAD_ZIPPED_FOLDER));
      assertFalse(IDE.MENU.isCommandEnabled(MenuCommands.File.FILE, MenuCommands.File.OPEN_LOCAL_FILE));
      assertFalse(IDE.MENU.isCommandEnabled(MenuCommands.File.FILE, MenuCommands.File.REFRESH));
      assertFalse(IDE.MENU.isCommandEnabled(MenuCommands.File.FILE, MenuCommands.File.SAVE));
      assertFalse(IDE.MENU.isCommandEnabled(MenuCommands.File.FILE, MenuCommands.File.SAVE_AS));
      assertTrue(IDE.MENU.isCommandEnabled(MenuCommands.File.FILE, MenuCommands.File.SEARCH));
      assertTrue(IDE.MENU.isCommandEnabled(MenuCommands.File.FILE, MenuCommands.File.UPLOAD_FILE));

      IDE.MENU.runCommand(MenuCommands.View.VIEW, MenuCommands.View.GO_TO_FOLDER);
      //TODO after fix IDE-1483, IDE-1484  test should be complete
      Thread.sleep(3000);

      //-------------------------------------------------------------------------------------------------

      //
      //      IDE.WORKSPACE.doubleClickOnFileFromSearchTab(WS_URL + TEST_FOLDER + "/" + restFileName);
      //      IDE.EDITOR.waitTabPresent(0);
      //      assertEquals(restFileName, IDE.EDITOR.getTabTitle(0));
      //
      //      IDE.TOOLBAR.assertButtonEnabled(ToolbarCommands.File.DELETE, false);
      //      IDE.TOOLBAR.assertButtonEnabled(ToolbarCommands.File.CUT_SELECTED_ITEM, false);
      //      IDE.TOOLBAR.assertButtonEnabled(ToolbarCommands.File.COPY_SELECTED_ITEM, false);
      //      IDE.TOOLBAR.assertButtonEnabled(ToolbarCommands.File.REFRESH, false);
      //
      //      IDE.MENU.checkCommandEnabled(MenuCommands.File.FILE, MenuCommands.File.DELETE, false);
      //      IDE.MENU.checkCommandEnabled(MenuCommands.File.FILE, MenuCommands.File.DOWNLOAD, false);
      //      IDE.MENU.checkCommandEnabled(MenuCommands.File.FILE, MenuCommands.File.DOWNLOAD_ZIPPED_FOLDER, false);
      //      IDE.MENU.checkCommandEnabled(MenuCommands.File.FILE, MenuCommands.File.OPEN_LOCAL_FILE, false);
      //      IDE.MENU.checkCommandEnabled(MenuCommands.File.FILE, MenuCommands.File.REFRESH, false);
      //      IDE.MENU.checkCommandEnabled(MenuCommands.File.FILE, MenuCommands.File.RENAME, false);
      //      IDE.MENU.checkCommandEnabled(MenuCommands.File.FILE, MenuCommands.File.SAVE, false);
      //      IDE.MENU.checkCommandEnabled(MenuCommands.File.FILE, MenuCommands.File.SAVE_ALL, false);
      //      IDE.MENU.checkCommandEnabled(MenuCommands.File.FILE, MenuCommands.File.SAVE_AS, false);
      //      IDE.MENU.checkCommandEnabled(MenuCommands.File.FILE, MenuCommands.File.SAVE_AS_TEMPLATE, true);
      //      IDE.MENU.checkCommandEnabled(MenuCommands.File.FILE, MenuCommands.File.SEARCH, true);
      //      IDE.MENU.checkCommandEnabled(MenuCommands.File.FILE, MenuCommands.File.UPLOAD_FILE, false);
      //
      //      IDE.MENU.runCommand(MenuCommands.View.VIEW, MenuCommands.View.GO_TO_FOLDER);
      //      IDE.WORKSPACE.waitForItem(WS_URL + TEST_FOLDER + "/" + restFileName);
      //      Thread.sleep(TestConstants.ANIMATION_PERIOD);
      //      
      //
      //      IDE.TOOLBAR.assertButtonEnabled(ToolbarCommands.File.DELETE, true);
      //      IDE.TOOLBAR.assertButtonEnabled(ToolbarCommands.File.CUT_SELECTED_ITEM, true);
      //      IDE.TOOLBAR.assertButtonEnabled(ToolbarCommands.File.COPY_SELECTED_ITEM, true);
      //      IDE.TOOLBAR.assertButtonEnabled(ToolbarCommands.File.REFRESH, true);
      //
      //      IDE.MENU.checkCommandEnabled(MenuCommands.File.FILE, MenuCommands.File.DELETE, true);
      //      IDE.MENU.checkCommandEnabled(MenuCommands.File.FILE, MenuCommands.File.DOWNLOAD, true);
      //      IDE.MENU.checkCommandEnabled(MenuCommands.File.FILE, MenuCommands.File.DOWNLOAD_ZIPPED_FOLDER, false);
      //      IDE.MENU.checkCommandEnabled(MenuCommands.File.FILE, MenuCommands.File.OPEN_LOCAL_FILE, true);
      //      IDE.MENU.checkCommandEnabled(MenuCommands.File.FILE, MenuCommands.File.REFRESH, true);
      //      IDE.MENU.checkCommandEnabled(MenuCommands.File.FILE, MenuCommands.File.RENAME, true);
      //      IDE.MENU.checkCommandEnabled(MenuCommands.File.FILE, MenuCommands.File.SAVE, false);
      //      IDE.MENU.checkCommandEnabled(MenuCommands.File.FILE, MenuCommands.File.SAVE_ALL, false);
      //      IDE.MENU.checkCommandEnabled(MenuCommands.File.FILE, MenuCommands.File.SAVE_AS, true);
      //      IDE.MENU.checkCommandEnabled(MenuCommands.File.FILE, MenuCommands.File.SAVE_AS_TEMPLATE, true);
      //      IDE.MENU.checkCommandEnabled(MenuCommands.File.FILE, MenuCommands.File.SEARCH, true);
      //      IDE.MENU.checkCommandEnabled(MenuCommands.File.FILE, MenuCommands.File.UPLOAD_FILE, true);
      //
      //      IDE.EDITOR.closeFile(0);
      //
      //      IDE.TOOLBAR.runCommandFromNewPopupMenu(MenuCommands.New.GOOGLE_GADGET_FILE);
      //      IDE.EDITOR.waitTabPresent(0);
      //
      //      IDE.EDITOR.deleteLinesInEditor(0, 7);
      //
      //      IDE.EDITOR.typeTextIntoEditor(0, gadgetFileContent);
      //      IDE.NAVIGATION.saveFileAs(gadgetFileName);
      //      Thread.sleep(TestConstants.SLEEP);
      //      IDE.NAVIGATION.assertItemVisible(WS_URL + TEST_FOLDER + "/" + gadgetFileName);
      //      IDE.EDITOR.closeFile(0);
      //      IDE.WORKSPACE.selectRootItem();
      //
      //      IDE.SEARCH.performSearch("/", "", "");
      //      IDE.SEARCH.waitSearchResultsOpened();
      //      IDE.NAVIGATION.assertItemVisibleInSearchTree(WS_URL + TEST_FOLDER + "/" + gadgetFileName);
      //
      //      IDE.WORKSPACE.doubleClickOnFileFromSearchTab(WS_URL + TEST_FOLDER + "/" + gadgetFileName);
      //      IDE.EDITOR.waitTabPresent(0);
      //      assertEquals(gadgetFileName, IDE.EDITOR.getTabTitle(0));
      //
      //      IDE.TOOLBAR.assertButtonEnabled(ToolbarCommands.File.DELETE, false);
      //      IDE.TOOLBAR.assertButtonEnabled(ToolbarCommands.File.CUT_SELECTED_ITEM, false);
      //      IDE.TOOLBAR.assertButtonEnabled(ToolbarCommands.File.COPY_SELECTED_ITEM, false);
      //      IDE.TOOLBAR.assertButtonEnabled(ToolbarCommands.File.REFRESH, false);
      //
      //      IDE.MENU.checkCommandEnabled(MenuCommands.File.FILE, MenuCommands.File.DELETE, false);
      //      IDE.MENU.checkCommandEnabled(MenuCommands.File.FILE, MenuCommands.File.DOWNLOAD, false);
      //      IDE.MENU.checkCommandEnabled(MenuCommands.File.FILE, MenuCommands.File.DOWNLOAD_ZIPPED_FOLDER, false);
      //      IDE.MENU.checkCommandEnabled(MenuCommands.File.FILE, MenuCommands.File.OPEN_LOCAL_FILE, false);
      //      IDE.MENU.checkCommandEnabled(MenuCommands.File.FILE, MenuCommands.File.REFRESH, false);
      //      IDE.MENU.checkCommandEnabled(MenuCommands.File.FILE, MenuCommands.File.RENAME, false);
      //      IDE.MENU.checkCommandEnabled(MenuCommands.File.FILE, MenuCommands.File.SAVE, false);
      //      IDE.MENU.checkCommandEnabled(MenuCommands.File.FILE, MenuCommands.File.SAVE_ALL, false);
      //      IDE.MENU.checkCommandEnabled(MenuCommands.File.FILE, MenuCommands.File.SAVE_AS, false);
      //      IDE.MENU.checkCommandEnabled(MenuCommands.File.FILE, MenuCommands.File.SAVE_AS_TEMPLATE, true);
      //      IDE.MENU.checkCommandEnabled(MenuCommands.File.FILE, MenuCommands.File.SEARCH, true);
      //      IDE.MENU.checkCommandEnabled(MenuCommands.File.FILE, MenuCommands.File.UPLOAD_FILE, false);
      //
      //      IDE.MENU.runCommand(MenuCommands.View.VIEW, MenuCommands.View.GO_TO_FOLDER);
      //      //TODO test selected
      //      IDE.WORKSPACE.waitForItem(WS_URL + TEST_FOLDER + "/" + restFileName);
      //      Thread.sleep(TestConstants.ANIMATION_PERIOD);
      //
      //      IDE.TOOLBAR.assertButtonEnabled(ToolbarCommands.File.DELETE, true);
      //      IDE.TOOLBAR.assertButtonEnabled(ToolbarCommands.File.CUT_SELECTED_ITEM, true);
      //      IDE.TOOLBAR.assertButtonEnabled(ToolbarCommands.File.COPY_SELECTED_ITEM, true);
      //      IDE.TOOLBAR.assertButtonEnabled(ToolbarCommands.File.REFRESH, true);
      //
      //      IDE.MENU.checkCommandEnabled(MenuCommands.File.FILE, MenuCommands.File.DELETE, true);
      //      IDE.MENU.checkCommandEnabled(MenuCommands.File.FILE, MenuCommands.File.DOWNLOAD, true);
      //      IDE.MENU.checkCommandEnabled(MenuCommands.File.FILE, MenuCommands.File.DOWNLOAD_ZIPPED_FOLDER, false);
      //      IDE.MENU.checkCommandEnabled(MenuCommands.File.FILE, MenuCommands.File.OPEN_LOCAL_FILE, true);
      //      IDE.MENU.checkCommandEnabled(MenuCommands.File.FILE, MenuCommands.File.REFRESH, true);
      //      IDE.MENU.checkCommandEnabled(MenuCommands.File.FILE, MenuCommands.File.RENAME, true);
      //      IDE.MENU.checkCommandEnabled(MenuCommands.File.FILE, MenuCommands.File.SAVE, false);
      //      IDE.MENU.checkCommandEnabled(MenuCommands.File.FILE, MenuCommands.File.SAVE_ALL, false);
      //      IDE.MENU.checkCommandEnabled(MenuCommands.File.FILE, MenuCommands.File.SAVE_AS, true);
      //      IDE.MENU.checkCommandEnabled(MenuCommands.File.FILE, MenuCommands.File.SAVE_AS_TEMPLATE, true);
      //      IDE.MENU.checkCommandEnabled(MenuCommands.File.FILE, MenuCommands.File.SEARCH, true);
      //      IDE.MENU.checkCommandEnabled(MenuCommands.File.FILE, MenuCommands.File.UPLOAD_FILE, true);
   }

   @AfterClass
   public static void tearDown() throws IOException
   {
      VirtualFileSystemUtils.delete(WS_URL + PROJECT);
   }
}

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
import org.exoplatform.ide.utils.AbstractTextUtil;
import org.junit.AfterClass;
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

   /**
    * IDE-33:Load found file in the Content Panel
    * 
    * @throws Exception
    */
   @Test
   public void testLoadFoundFile() throws Exception
   {
      runCommandFromMenuNewOnToolbar(MenuCommands.New.REST_SERVICE_FILE);
      Thread.sleep(TestConstants.SLEEP);
      saveAsByTopMenu(restFileName);
      Thread.sleep(TestConstants.SLEEP);
      assertElementPresentInWorkspaceTree(restFileName);
      closeTab("0");
      selectRootOfWorkspaceTree();

      performSearch("/", "", "");
      Thread.sleep(TestConstants.SLEEP);
      assertElementPresentSearchResultsTree(restFileName);

      openFileFromSearchResultsWithCodeEditor(restFileName);
      Thread.sleep(TestConstants.SLEEP);
      assertEquals(restFileName, getTabTitle(0));
      /* checkMenuCommandState(MenuCommands.New.NEW, MenuCommands.New.CSS_FILE, false);
       checkMenuCommandState(MenuCommands.New.NEW, MenuCommands.New.FOLDER, false);
       checkMenuCommandState(MenuCommands.New.NEW, MenuCommands.New.FROM_TEMPLATE, false);
       checkMenuCommandState(MenuCommands.New.NEW, MenuCommands.New.GOOGLE_GADGET_FILE, false);
       checkMenuCommandState(MenuCommands.New.NEW, MenuCommands.New.GROOVY_SCRIPT_FILE, false);
       checkMenuCommandState(MenuCommands.New.NEW, MenuCommands.New.GROOVY_TEMPLATE_FILE, false);
       checkMenuCommandState(MenuCommands.New.NEW, MenuCommands.New.HTML_FILE, false);
       checkMenuCommandState(MenuCommands.New.NEW, MenuCommands.New.JAVASCRIPT_FILE, false);
       checkMenuCommandState(MenuCommands.New.NEW, MenuCommands.New.NETVIBES_WIDGET_FILE, false);
       checkMenuCommandState(MenuCommands.New.NEW, MenuCommands.New.REST_SERVICE_FILE, false);
       checkMenuCommandState(MenuCommands.New.NEW, MenuCommands.New.TEXT_FILE, false);
       checkMenuCommandState(MenuCommands.New.NEW, MenuCommands.New.XML_FILE, false);
       */
      checkToolbarButtonState("Delete Item(s)...", false);
      checkToolbarButtonState("Cut Selected Item(s)", false);
      checkToolbarButtonState("Copy Selected Item(s)", false);
      checkToolbarButtonState("Paste Selected Item(s)", false);
      checkToolbarButtonState("Refresh Selected Folder", false);

      checkMenuCommandState(MenuCommands.File.FILE, MenuCommands.File.DELETE, false);
      checkMenuCommandState(MenuCommands.File.FILE, MenuCommands.File.DOWNLOAD, false);
      checkMenuCommandState(MenuCommands.File.FILE, MenuCommands.File.DOWNLOAD_ZIPPED_FOLDER, false);
      checkMenuCommandState(MenuCommands.File.FILE, MenuCommands.File.OPEN_LOCAL_FILE, false);
      checkMenuCommandState(MenuCommands.File.FILE, MenuCommands.File.OPEN_WITH, true);
      checkMenuCommandState(MenuCommands.File.FILE, MenuCommands.File.REFRESH, false);
      checkMenuCommandState(MenuCommands.File.FILE, MenuCommands.File.RENAME, false);
      checkMenuCommandState(MenuCommands.File.FILE, MenuCommands.File.SAVE, false);
      checkMenuCommandState(MenuCommands.File.FILE, MenuCommands.File.SAVE_ALL, false);
      checkMenuCommandState(MenuCommands.File.FILE, MenuCommands.File.SAVE_AS, false);
      checkMenuCommandState(MenuCommands.File.FILE, MenuCommands.File.SAVE_AS_TEMPLATE, true);
      checkMenuCommandState(MenuCommands.File.FILE, MenuCommands.File.SEARCH, true);
      checkMenuCommandState(MenuCommands.File.FILE, MenuCommands.File.UPLOAD, false);

      runTopMenuCommand(MenuCommands.View.VIEW, MenuCommands.View.GO_TO_FOLDER);
      assertElementPresentInWorkspaceTree(restFileName);
      //TODO check selected

      checkToolbarButtonState("Delete Item(s)...", true);
      checkToolbarButtonState("Cut Selected Item(s)", true);
      checkToolbarButtonState("Copy Selected Item(s)", true);
      checkToolbarButtonState("Paste Selected Item(s)", false);
      checkToolbarButtonState("Refresh Selected Folder", true);

      checkMenuCommandState(MenuCommands.File.FILE, MenuCommands.File.DELETE, true);
      checkMenuCommandState(MenuCommands.File.FILE, MenuCommands.File.DOWNLOAD, true);
      checkMenuCommandState(MenuCommands.File.FILE, MenuCommands.File.DOWNLOAD_ZIPPED_FOLDER, false);
      checkMenuCommandState(MenuCommands.File.FILE, MenuCommands.File.OPEN_LOCAL_FILE, true);
      checkMenuCommandState(MenuCommands.File.FILE, MenuCommands.File.OPEN_WITH, true);
      checkMenuCommandState(MenuCommands.File.FILE, MenuCommands.File.REFRESH, true);
      checkMenuCommandState(MenuCommands.File.FILE, MenuCommands.File.RENAME, true);
      checkMenuCommandState(MenuCommands.File.FILE, MenuCommands.File.SAVE, false);
      checkMenuCommandState(MenuCommands.File.FILE, MenuCommands.File.SAVE_ALL, false);
      checkMenuCommandState(MenuCommands.File.FILE, MenuCommands.File.SAVE_AS, true);
      checkMenuCommandState(MenuCommands.File.FILE, MenuCommands.File.SAVE_AS_TEMPLATE, true);
      checkMenuCommandState(MenuCommands.File.FILE, MenuCommands.File.SEARCH, true);
      checkMenuCommandState(MenuCommands.File.FILE, MenuCommands.File.UPLOAD, true);

      closeTab("0");

      runCommandFromMenuNewOnToolbar(MenuCommands.New.GOOGLE_GADGET_FILE);
      Thread.sleep(TestConstants.SLEEP);
      deleteLinesInEditor(7);
      AbstractTextUtil.getInstance().typeTextToEditor(TestConstants.CODEMIRROR_EDITOR_LOCATOR, gadgetFileContent);
      saveAsByTopMenu(gadgetFileName);
      Thread.sleep(TestConstants.SLEEP);
      assertElementPresentInWorkspaceTree(gadgetFileName);
      closeTab("0");
      selectRootOfWorkspaceTree();

      performSearch("/", "", "");
      Thread.sleep(TestConstants.SLEEP);
      assertElementPresentSearchResultsTree(gadgetFileName);

      openFileFromSearchResultsWithCodeEditor(gadgetFileName);
      Thread.sleep(TestConstants.SLEEP);
      assertEquals(gadgetFileName, getTabTitle(0));

      checkToolbarButtonState("Delete Item(s)...", false);
      checkToolbarButtonState("Cut Selected Item(s)", false);
      checkToolbarButtonState("Copy Selected Item(s)", false);
      checkToolbarButtonState("Paste Selected Item(s)", false);
      checkToolbarButtonState("Refresh Selected Folder", false);

      checkMenuCommandState(MenuCommands.File.FILE, MenuCommands.File.DELETE, false);
      checkMenuCommandState(MenuCommands.File.FILE, MenuCommands.File.DOWNLOAD, false);
      checkMenuCommandState(MenuCommands.File.FILE, MenuCommands.File.DOWNLOAD_ZIPPED_FOLDER, false);
      checkMenuCommandState(MenuCommands.File.FILE, MenuCommands.File.OPEN_LOCAL_FILE, false);
      checkMenuCommandState(MenuCommands.File.FILE, MenuCommands.File.OPEN_WITH, true);
      checkMenuCommandState(MenuCommands.File.FILE, MenuCommands.File.REFRESH, false);
      checkMenuCommandState(MenuCommands.File.FILE, MenuCommands.File.RENAME, false);
      checkMenuCommandState(MenuCommands.File.FILE, MenuCommands.File.SAVE, false);
      checkMenuCommandState(MenuCommands.File.FILE, MenuCommands.File.SAVE_ALL, false);
      checkMenuCommandState(MenuCommands.File.FILE, MenuCommands.File.SAVE_AS, false);
      checkMenuCommandState(MenuCommands.File.FILE, MenuCommands.File.SAVE_AS_TEMPLATE, true);
      checkMenuCommandState(MenuCommands.File.FILE, MenuCommands.File.SEARCH, true);
      checkMenuCommandState(MenuCommands.File.FILE, MenuCommands.File.UPLOAD, false);

      runTopMenuCommand(MenuCommands.View.VIEW, MenuCommands.View.GO_TO_FOLDER);
      assertElementPresentInWorkspaceTree(restFileName);
      //TODO check selected

      checkToolbarButtonState("Delete Item(s)...", true);
      checkToolbarButtonState("Cut Selected Item(s)", true);
      checkToolbarButtonState("Copy Selected Item(s)", true);
      checkToolbarButtonState("Paste Selected Item(s)", false);
      checkToolbarButtonState("Refresh Selected Folder", true);

      checkMenuCommandState(MenuCommands.File.FILE, MenuCommands.File.DELETE, true);
      checkMenuCommandState(MenuCommands.File.FILE, MenuCommands.File.DOWNLOAD, true);
      checkMenuCommandState(MenuCommands.File.FILE, MenuCommands.File.DOWNLOAD_ZIPPED_FOLDER, false);
      checkMenuCommandState(MenuCommands.File.FILE, MenuCommands.File.OPEN_LOCAL_FILE, true);
      checkMenuCommandState(MenuCommands.File.FILE, MenuCommands.File.OPEN_WITH, true);
      checkMenuCommandState(MenuCommands.File.FILE, MenuCommands.File.REFRESH, true);
      checkMenuCommandState(MenuCommands.File.FILE, MenuCommands.File.RENAME, true);
      checkMenuCommandState(MenuCommands.File.FILE, MenuCommands.File.SAVE, false);
      checkMenuCommandState(MenuCommands.File.FILE, MenuCommands.File.SAVE_ALL, false);
      checkMenuCommandState(MenuCommands.File.FILE, MenuCommands.File.SAVE_AS, true);
      checkMenuCommandState(MenuCommands.File.FILE, MenuCommands.File.SAVE_AS_TEMPLATE, true);
      checkMenuCommandState(MenuCommands.File.FILE, MenuCommands.File.SEARCH, true);
      checkMenuCommandState(MenuCommands.File.FILE, MenuCommands.File.UPLOAD, true);

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
      cleanRepository(REST_CONTEXT + "/jcr/" + REPO_NAME + "/" + WS_NAME + "/");
   }
}

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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.MenuCommands;
import org.exoplatform.ide.ToolbarCommands;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.exoplatform.ide.vfs.shared.Link;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.Map;

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
   @Test
   //TODO after fix IDE-1483, IDE-1484  test should be complete
   public void testLoadFoundFile() throws Exception
   {
      //step 1 open project an folders, search groovy and check result
      IDE.PROJECT.EXPLORER.waitOpened();
      IDE.PROJECT.OPEN.openProject(PROJECT);
      IDE.WELCOME_PAGE.close();
      IDE.WELCOME_PAGE.waitClose();
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT);
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + TEST_FOLDER);
      IDE.PROJECT.EXPLORER.selectItem(PROJECT + "/" + TEST_FOLDER);
      IDE.SEARCH.performSearch("/" + PROJECT + "/" + TEST_FOLDER, "", MimeType.APPLICATION_GROOVY);
      IDE.SEARCH_RESULT.waitForItem(PROJECT + "/" + TEST_FOLDER + "/" + restFileName);

      IDE.SEARCH_RESULT.isItemPresent(PROJECT + "/" + TEST_FOLDER + "/" + restFileName);
      IDE.SEARCH_RESULT.openItem(PROJECT + "/" + TEST_FOLDER + "/" + restFileName);
      IDE.EDITOR.waitActiveFile(PROJECT + "/" + TEST_FOLDER + "/" + restFileName);

      chekButtonState();

      //step 2 check GOTO folder and close groovy file
      IDE.MENU.runCommand(MenuCommands.View.VIEW, MenuCommands.View.GO_TO_FOLDER);
      IDE.EDITOR.waitTabPresent(0);
      assertEquals(restFileName, IDE.EDITOR.getTabTitle(0));
      IDE.EDITOR.closeFile(0);

      //step 3 open social gadget file, edit, save. Search this file and check results 
      //TODO Tets fail issue IDE - IDE-1632
      IDE.PROJECT.EXPLORER.openItem(PROJECT + "/" + TEST_FOLDER + "/" + gadgetFileName);
      IDE.EDITOR.waitTabPresent(0);
      IDE.EDITOR.deleteFileContent(1);
      IDE.EDITOR.typeTextIntoEditor(1, gadgetFileContent);
      IDE.TOOLBAR.runCommand(ToolbarCommands.File.SAVE);
      IDE.LOADER.waitClosed();
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + TEST_FOLDER + "/" + gadgetFileName);
      IDE.EDITOR.closeFile(0);
      IDE.PROJECT.EXPLORER.selectItem(PROJECT);
      IDE.SEARCH.performSearch("/" + PROJECT, "", MimeType.GOOGLE_GADGET);
      IDE.SEARCH_RESULT.waitForItem(PROJECT + "/" + gadgetFileName);

      Thread.sleep(3000);

   }

   private void chekButtonState() throws Exception
   {
      IDE.TOOLBAR.waitButtonPresentAtLeft(ToolbarCommands.View.SHOW_OUTLINE);
      assertFalse(IDE.TOOLBAR.isButtonEnabled(ToolbarCommands.File.DELETE));
      assertFalse(IDE.TOOLBAR.isButtonEnabled(ToolbarCommands.File.CUT_SELECTED_ITEM));
      assertFalse(IDE.TOOLBAR.isButtonEnabled(ToolbarCommands.File.COPY_SELECTED_ITEM));
      assertFalse(IDE.TOOLBAR.isButtonEnabled(ToolbarCommands.File.PASTE));
      assertFalse(IDE.TOOLBAR.isButtonEnabled(ToolbarCommands.File.REFRESH));
      assertFalse(IDE.TOOLBAR.isButtonEnabled(ToolbarCommands.File.SEARCH));
      assertTrue(IDE.TOOLBAR.isButtonEnabled(ToolbarCommands.Editor.LOCK_FILE));
      assertTrue(IDE.TOOLBAR.isButtonEnabled(MenuCommands.Edit.FORMAT));
      IDE.MENU.isTopMenuEnabled(MenuCommands.File.FILE);
   }

   @AfterClass
   public static void tearDown() throws IOException
   {
      VirtualFileSystemUtils.delete(WS_URL + PROJECT);
   }
}

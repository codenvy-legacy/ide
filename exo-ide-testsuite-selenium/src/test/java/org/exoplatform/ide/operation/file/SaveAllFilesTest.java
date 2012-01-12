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
package org.exoplatform.ide.operation.file;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.MenuCommands;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * IDE-54:Save All Files
 * 
 * Created by The eXo Platform SAS.
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id:
 *
 */
public class SaveAllFilesTest extends BaseTest
{

   private static final String PROJECT = SaveAllFilesTest.class.getSimpleName();

   private static final String FOLDER_1 = "SaveAllFilesTest-1";

   private static final String FOLDER_2 = "SaveAllFilesTest-2";

   private static final String SAVED_XML = "Saved File.xml";

   private static final String SAVED_GROOVY = "Saved File.groovy";

   private static final String NEW_HTML = "Untitled file.html";

   private static final String NEW_TEXT = "Untitled file.txt";

   @BeforeClass
   public static void setUp()
   {
      try
      {
         VirtualFileSystemUtils.createDefaultProject(PROJECT);
         VirtualFileSystemUtils.mkcol(WS_URL + PROJECT + "/" + FOLDER_1);
         VirtualFileSystemUtils.mkcol(WS_URL + PROJECT + "/" + FOLDER_2);
      }
      catch (Exception e)
      {
      }
   }

   @AfterClass
   public static void tearDown()
   {
      try
      {
         VirtualFileSystemUtils.delete(WS_URL + PROJECT);
      }
      catch (Exception e)
      {
      }
   }

   //IDE-54:Save All Files
   @Test
   public void saveAllFiles() throws Exception
   {
      IDE.PROJECT.EXPLORER.waitOpened();
      IDE.PROJECT.OPEN.openProject(PROJECT);
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + FOLDER_1);
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + FOLDER_2);

      /*
       * 1. Create file "Saved File.xml" in "Folder1"
       */
      IDE.PROJECT.EXPLORER.selectItem(PROJECT + "/" + FOLDER_1);
      IDE.TOOLBAR.runCommandFromNewPopupMenu(MenuCommands.New.XML_FILE);
      IDE.EDITOR.waitActiveFile(PROJECT + "/" + FOLDER_1 + "/Untitled file.xml");

      assertFalse(IDE.MENU.isCommandEnabled(MenuCommands.File.FILE, MenuCommands.File.SAVE_ALL));
      IDE.EDITOR.saveAs(1, SAVED_XML);
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + FOLDER_1 + "/" + SAVED_XML);
      IDE.EDITOR.closeFile(1);

      /*
       * 2. Create "Saved File.groovy" in "Folder2"
       */
      IDE.PROJECT.EXPLORER.selectItem(PROJECT + "/" + FOLDER_2);
      IDE.TOOLBAR.runCommandFromNewPopupMenu(MenuCommands.New.GROOVY_SCRIPT_FILE);
      IDE.EDITOR.waitActiveFile(PROJECT + "/" + FOLDER_2 + "/Untitled file.groovy");

      assertFalse(IDE.MENU.isCommandEnabled(MenuCommands.File.FILE, MenuCommands.File.SAVE_ALL));
      IDE.EDITOR.saveAs(1, SAVED_GROOVY);
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + FOLDER_2 + "/" + SAVED_GROOVY);
      IDE.EDITOR.closeFile(1);

      /*
       * 3. Save All command must be disabled
       */
      assertFalse(IDE.MENU.isCommandEnabled(MenuCommands.File.FILE, MenuCommands.File.SAVE_ALL));

      /*
       * 4. Create HTML file from template "Empty HTML" and does not save it.
       */
      IDE.TEMPLATES.createFileFromTemplate("Empty HTML", NEW_HTML);

      /*
       * 5. Create TEXT from from template "Empty TEXT" and does not save it.
       */
      IDE.TEMPLATES.createFileFromTemplate("Empty TEXT", NEW_TEXT);
      
      /*
       * 6. Save All command must be disabled
       */
      assertFalse(IDE.MENU.isCommandEnabled(MenuCommands.File.FILE, MenuCommands.File.SAVE_ALL));

      /*
       * 7. Open and change content of files "Saved File.xml" and "Saved File.groovy"
       */
      IDE.PROJECT.EXPLORER.openItem(PROJECT + "/" + FOLDER_1 + "/" + SAVED_XML);
      IDE.EDITOR.waitActiveFile(PROJECT + "/" + FOLDER_1 + "/" + SAVED_XML);
      IDE.EDITOR.typeTextIntoEditor(4, "<root>admin</root>");
      
      IDE.WORKSPACE.doubleClickOnFile(PROJECT + "/" + FOLDER_2 + "/" + SAVED_GROOVY);
      IDE.EDITOR.waitActiveFile(PROJECT + "/" + FOLDER_2 + "/" + SAVED_GROOVY);
      IDE.EDITOR.typeTextIntoEditor(5, "changed content of file");

      /*
       * 8. Save All command must be enabled.
       */
      assertTrue(IDE.MENU.isCommandEnabled(MenuCommands.File.FILE, MenuCommands.File.SAVE_ALL));
      
      /*
       * 9. Run command "Save All" from menu 
       */
  //TODO   IDE.NAVIGATION.saveAllFiles();

      /*
       * 10. Files "Untitled file.html" and  "Untitled file.txt" must have marker "*" in editor
       *      Files "Saved File.xml" and "Saved File.groovy" must be without marker "*" in editor.
       */
      assertEquals(NEW_HTML + " *", IDE.EDITOR.getTabTitle(1));
      assertEquals(NEW_TEXT + " *", IDE.EDITOR.getTabTitle(2));
      assertEquals(SAVED_XML, IDE.EDITOR.getTabTitle(3));
      assertEquals(SAVED_GROOVY, IDE.EDITOR.getTabTitle(4));

      /*
       * 11. Save "Untitled file.html" to Folder1
       */
      IDE.PROJECT.EXPLORER.selectItem(PROJECT + "/" + FOLDER_1);
      IDE.EDITOR.selectTab(1);
      IDE.EDITOR.saveAs(1, NEW_HTML);
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + FOLDER_1 + "/" + NEW_HTML);
      IDE.EDITOR.closeFile(1);

      /*
       * 12. Save "Untitled file.txt" to Folder2
       */
      IDE.PROJECT.EXPLORER.selectItem(PROJECT + "/" + FOLDER_2);
      IDE.EDITOR.selectTab(1);
      IDE.EDITOR.saveAs(1, NEW_TEXT);
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + FOLDER_2 + "/" + NEW_TEXT);
      IDE.EDITOR.closeFile(1);

      /*
       * 13. Open "Untitled file.groovy" and "Untitled file.xml"
       */
      IDE.PROJECT.EXPLORER.openItem(PROJECT + "/" + FOLDER_1 + "/" + NEW_HTML);
      IDE.PROJECT.EXPLORER.openItem(PROJECT + "/" + FOLDER_2 + "/" + NEW_TEXT);

      /*
       * 14. Now Save As command must be disabled and all files in editor must does not have a marker "*"
       */
      assertFalse(IDE.MENU.isCommandEnabled(MenuCommands.File.FILE, MenuCommands.File.SAVE_ALL));
      assertEquals(SAVED_XML, IDE.EDITOR.getTabTitle(1));
      assertEquals(SAVED_GROOVY, IDE.EDITOR.getTabTitle(2));
      assertEquals(NEW_HTML, IDE.EDITOR.getTabTitle(3));
      assertEquals(NEW_TEXT, IDE.EDITOR.getTabTitle(4));

      IDE.EDITOR.closeFile(1);
      IDE.EDITOR.closeFile(1);
      IDE.EDITOR.closeFile(1);
      IDE.EDITOR.closeFile(1);
   }

}

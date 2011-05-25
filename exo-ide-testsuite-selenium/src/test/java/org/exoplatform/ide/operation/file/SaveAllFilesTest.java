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
         VirtualFileSystemUtils.mkcol(WS_URL + FOLDER_1);
         VirtualFileSystemUtils.mkcol(WS_URL + FOLDER_2);
      }
      catch (Exception e)
      {
         e.printStackTrace();
      }
   }
   
   @AfterClass
   public static void tearDown()
   {
      try
      {
         VirtualFileSystemUtils.delete(WS_URL + FOLDER_1);
         VirtualFileSystemUtils.delete(WS_URL + FOLDER_2);
      }
      catch (Exception e)
      {
         e.printStackTrace();
      }
   }

   //IDE-54:Save All Files
   //@Ignore
   @Test
   public void saveAllFiles() throws Exception
   {
      IDE.WORKSPACE.waitForRootItem();

      /*
       * 1. Create file "Saved File.xml" in "Folder1"
       */
      IDE.WORKSPACE.selectItem(WS_URL + FOLDER_1 + "/");
      IDE.TOOLBAR.runCommandFromNewPopupMenu(MenuCommands.New.XML_FILE);
      IDE.MENU.checkCommandEnabled(MenuCommands.File.FILE, MenuCommands.File.SAVE_ALL, false);
      IDE.NAVIGATION.saveFileAs(SAVED_XML);
      IDE.EDITOR.closeFile(0);

      /*
       * 2. Create "Saved File.groovy" in "Folder2"
       */
      IDE.WORKSPACE.selectItem(WS_URL + FOLDER_2 + "/");
      IDE.TOOLBAR.runCommandFromNewPopupMenu(MenuCommands.New.GROOVY_SCRIPT_FILE);
      IDE.MENU.checkCommandEnabled(MenuCommands.File.FILE, MenuCommands.File.SAVE_ALL, false);
      IDE.NAVIGATION.saveFileAs(SAVED_GROOVY);
      IDE.EDITOR.closeFile(0);

      /*
       * 3. Save All command must be disabled
       */
      IDE.MENU.checkCommandEnabled(MenuCommands.File.FILE, MenuCommands.File.SAVE_ALL, false);

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
      IDE.MENU.checkCommandEnabled(MenuCommands.File.FILE, MenuCommands.File.SAVE_ALL, false);

      /*
       * 7. Open and change content of files "Saved File.xml" and "Saved File.groovy"
       */
      IDE.WORKSPACE.doubleClickOnFile(WS_URL + FOLDER_1 + "/" + SAVED_XML);
      IDE.EDITOR.typeTextIntoEditor(2, "<root>admin</root>");

      IDE.WORKSPACE.doubleClickOnFile(WS_URL + FOLDER_2 + "/" + SAVED_GROOVY);
      IDE.EDITOR.typeTextIntoEditor(3, "changed content of file");

      /*
       * 8. Save All command must be enabled.
       */
      IDE.MENU.checkCommandEnabled(MenuCommands.File.FILE, MenuCommands.File.SAVE_ALL, true);

      /*
       * 9. Run command "Save All" from menu 
       */
      IDE.NAVIGATION.saveAllFiles();

      /*
       * 10. Files "Untitled file.html" and  "Untitled file.txt" must have marker "*" in editor
       *      Files "Saved File.xml" and "Saved File.groovy" must be without marker "*" in editor.
       */
      assertEquals(NEW_HTML + " *", IDE.EDITOR.getTabTitle(0));
      assertEquals(NEW_TEXT + " *", IDE.EDITOR.getTabTitle(1));
      assertEquals(SAVED_XML, IDE.EDITOR.getTabTitle(2));
      assertEquals(SAVED_GROOVY, IDE.EDITOR.getTabTitle(3));

      /*
       * 11. Save "Untitled file.html" to Folder1
       */
      IDE.WORKSPACE.selectItem(WS_URL + FOLDER_1 + "/");
      IDE.EDITOR.selectTab(0);
      IDE.NAVIGATION.saveFileAs(NEW_HTML);
      IDE.EDITOR.closeFile(0);

      /*
       * 12. Save "Untitled file.txt" to Folder2
       */
      IDE.WORKSPACE.selectItem(WS_URL + FOLDER_2 + "/");
      IDE.EDITOR.selectTab(0);
      IDE.NAVIGATION.saveFileAs(NEW_TEXT);
      IDE.EDITOR.closeFile(0);

      /*
       * 13. Open "Untitled file.groovy" and "Untitled file.xml"
       */
      IDE.WORKSPACE.doubleClickOnFile(WS_URL + FOLDER_1 + "/" + NEW_HTML);
      IDE.WORKSPACE.doubleClickOnFile(WS_URL + FOLDER_2 + "/" + NEW_TEXT);

      /*
       * 14. Now Save As command must be disabled and all files in editor must does not have a marker "*"
       */
      IDE.MENU.checkCommandEnabled(MenuCommands.File.FILE, MenuCommands.File.SAVE_ALL, false);
      assertEquals(SAVED_XML, IDE.EDITOR.getTabTitle(0));
      assertEquals(SAVED_GROOVY, IDE.EDITOR.getTabTitle(1));
      assertEquals(NEW_HTML, IDE.EDITOR.getTabTitle(2));
      assertEquals(NEW_TEXT, IDE.EDITOR.getTabTitle(3));

      IDE.EDITOR.closeFile(3);
      IDE.EDITOR.closeFile(2);
      IDE.EDITOR.closeFile(1);
      IDE.EDITOR.closeFile(0);
   }

}

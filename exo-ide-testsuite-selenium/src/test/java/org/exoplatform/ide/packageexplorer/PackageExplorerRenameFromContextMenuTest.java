/*
 * Copyright (C) 2012 eXo Platform SAS.
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
package org.exoplatform.ide.packageexplorer;

import java.util.Map;

import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.MenuCommands;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.exoplatform.ide.vfs.shared.Link;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.Keys;

/**
 * @author <a href="mailto:riuvshin@exoplatform.com">Roman Iuvshin</a>
 * @version 27.12.2012 22:49:12
 *
 */
public class PackageExplorerRenameFromContextMenuTest extends BaseTest
{
   private static final String PROJECT = "RenamePrj";

   private static final String NEW_PROJECT_NAME = "javanewname";

   private static final String FILE_NAME = "index.jsp";

   private static final String NEW_FILE_NAME = "newFileName.jsp";

   final static String filePath = "src/test/resources/org/exoplatform/ide/extension/maven/TestSpringProjectWithPOM.zip";

   private static final String SOURCE_FOLDER_NAME = "src/main/java";

   private static final String NEW_SOURCE_FOLDER_NAME = "src/main/" + NEW_PROJECT_NAME;

   private static final String REFERENCED_LIBRARIES = "Referenced Libraries";

   private static final String REF_LIB_ITEM = "junit-3.8.1.jar";

   static Map<String, Link> project;

   @BeforeClass
   public static void setUp()
   {
      try
      {
         project = VirtualFileSystemUtils.importZipProject(PROJECT, filePath);
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
         VirtualFileSystemUtils.delete(WS_URL + NEW_PROJECT_NAME);
      }
      catch (Exception e)
      {
      }
   }

   @Test
   public void renameProjectFromContextMenuTest() throws Exception
   {
      IDE.PROJECT.EXPLORER.waitOpened();
      IDE.PROJECT.OPEN.openProject(PROJECT);
      IDE.PROGRESS_BAR.waitProgressBarControlClose();
      IDE.PROJECT.PACKAGE_EXPLORER.waitPackageExplorerOpened();
      //rename project
      IDE.PROJECT.PACKAGE_EXPLORER.selectItemInPackageExplorer(PROJECT);
      IDE.PROJECT.PACKAGE_EXPLORER.openContextMenuOnSelectedItemInPackageExplorer(PROJECT);
      rename(NEW_PROJECT_NAME);
      IDE.PROJECT.PACKAGE_EXPLORER.waitItemInPackageExplorer(NEW_PROJECT_NAME);
   }

   @Test
   public void renameFileFromContextMenuTest() throws Exception
   {
      IDE.PROJECT.PACKAGE_EXPLORER.waitItemInPackageExplorer("src");
      IDE.PROJECT.PACKAGE_EXPLORER.openItemWithDoubleClick("src");
      IDE.PROJECT.PACKAGE_EXPLORER.waitItemInPackageExplorer("main");
      IDE.PROJECT.PACKAGE_EXPLORER.openItemWithDoubleClick("main");
      IDE.PROJECT.PACKAGE_EXPLORER.waitItemInPackageExplorer("webapp");
      IDE.PROJECT.PACKAGE_EXPLORER.openItemWithDoubleClick("webapp");

      IDE.PROJECT.PACKAGE_EXPLORER.openContextMenuOnSelectedItemInPackageExplorer(FILE_NAME);
      rename(NEW_FILE_NAME);
      IDE.PROJECT.PACKAGE_EXPLORER.waitItemInPackageExplorer(NEW_FILE_NAME);

      IDE.PROJECT.PACKAGE_EXPLORER.openItemWithDoubleClick("src");
   }

   @Test
   public void renameSourceFolderFromContextMenuTest() throws Exception
   {
      //TODO There is error issued in : IDE-2208
      IDE.PROJECT.PACKAGE_EXPLORER.openContextMenuOnSelectedItemInPackageExplorer(SOURCE_FOLDER_NAME);
      rename(NEW_PROJECT_NAME);
      IDE.PROGRESS_BAR.waitProgressBarControlClose();
      IDE.PROJECT.PACKAGE_EXPLORER.waitItemInPackageExplorer(NEW_SOURCE_FOLDER_NAME);
   }

   @Test
   public void renameReferencedLibrariesFromContextMenuTest() throws Exception
   {
      IDE.PROJECT.PACKAGE_EXPLORER.openContextMenuOnSelectedItemInPackageExplorer(REFERENCED_LIBRARIES);
      IDE.PROJECT.PACKAGE_EXPLORER.waitElementInContextMenuDisabled(MenuCommands.File.RENAME);
      IDE.PROJECT.PACKAGE_EXPLORER.typeKeys(Keys.ESCAPE.toString());
      IDE.PROJECT.PACKAGE_EXPLORER.waitContextMenuDisappear();

      IDE.PROJECT.PACKAGE_EXPLORER.openItemWithDoubleClick("Referenced Libraries");
      IDE.PROJECT.PACKAGE_EXPLORER.selectItemInPackageExplorer(REF_LIB_ITEM);
      IDE.PROJECT.PACKAGE_EXPLORER.openContextMenuOnSelectedItemInPackageExplorer(REF_LIB_ITEM);
      IDE.PROJECT.PACKAGE_EXPLORER.waitElementInContextMenuDisabled(MenuCommands.File.RENAME);
      IDE.PROJECT.PACKAGE_EXPLORER.typeKeys(Keys.ESCAPE.toString());
      IDE.PROJECT.PACKAGE_EXPLORER.waitContextMenuDisappear();

   }

   /**
    * @throws Exception
    * @throws InterruptedException
    */
   private void rename(String newName) throws Exception, InterruptedException
   {
      IDE.PROJECT.PACKAGE_EXPLORER.waitElementInContextMenuEnabled(MenuCommands.File.RENAME);
      IDE.PROJECT.PACKAGE_EXPLORER.clickOnItemInContextMenu(MenuCommands.File.RENAME);
      IDE.RENAME.waitOpened();
      IDE.RENAME.setNewName(newName);
      IDE.RENAME.clickRenameButton();
      IDE.LOADER.waitClosed();
   }
}
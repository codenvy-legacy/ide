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
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.Keys;

/**
 * @author <a href="mailto:riuvshin@exoplatform.com">Roman Iuvshin</a>
 * @version 27.12.2012 22:49:12
 *
 */
public class PackageExplorerDeleteFromContextMenuTest extends BaseTest
{
   private static final String PROJECT = "DeletePrj";

   private static final String FILE_NAME = "index.jsp";

   final static String filePath = "src/test/resources/org/exoplatform/ide/extension/maven/TestSpringProjectWithPOM.zip";

   private static final String SOURCE_FOLDER_NAME = "src/main/java";

   private static final String FOLDER_NAME = "WEB-INF";

   private static final String PACKAGE_NAME = "helloworld";

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

   @Test
   public void deleteFileFromContextMenuTest() throws Exception
   {
      IDE.PROJECT.EXPLORER.waitOpened();
      IDE.PROJECT.OPEN.openProject(PROJECT);
      IDE.PROGRESS_BAR.waitProgressBarControlClose();
      IDE.PROJECT.PACKAGE_EXPLORER.waitPackageExplorerOpened();

      IDE.PROJECT.PACKAGE_EXPLORER.waitItemInPackageExplorer("src");
      IDE.PROJECT.PACKAGE_EXPLORER.openItemWithDoubleClick("src");
      IDE.PROJECT.PACKAGE_EXPLORER.waitItemInPackageExplorer("main");
      IDE.PROJECT.PACKAGE_EXPLORER.openItemWithDoubleClick("main");
      IDE.PROJECT.PACKAGE_EXPLORER.waitItemInPackageExplorer("webapp");
      IDE.PROJECT.PACKAGE_EXPLORER.openItemWithDoubleClick("webapp");
      IDE.PROJECT.PACKAGE_EXPLORER.openContextMenuOnSelectedItemInPackageExplorer(FILE_NAME);
      delete();
      IDE.PROJECT.PACKAGE_EXPLORER.waitItemInPackageExplorerIsNotPresent(FILE_NAME);
   }

   @Test
   public void deleteFolderFromContextMenuTest() throws Exception
   {
      IDE.PROJECT.PACKAGE_EXPLORER.openContextMenuOnSelectedItemInPackageExplorer(FOLDER_NAME);
      delete();
      IDE.PROJECT.PACKAGE_EXPLORER.waitItemInPackageExplorerIsNotPresent(FOLDER_NAME);
      IDE.PROJECT.PACKAGE_EXPLORER.openItemWithDoubleClick("src");
   }

   @Test
   public void deletePackageFromContextMenuTest() throws Exception
   {
      IDE.PROJECT.PACKAGE_EXPLORER.waitItemInPackageExplorer(SOURCE_FOLDER_NAME);
      IDE.PROJECT.PACKAGE_EXPLORER.openItemWithDoubleClick(SOURCE_FOLDER_NAME);
      IDE.PROJECT.PACKAGE_EXPLORER.openContextMenuOnSelectedItemInPackageExplorer(PACKAGE_NAME);
      delete();
      IDE.PROJECT.PACKAGE_EXPLORER.waitItemInPackageExplorerIsNotPresent(PACKAGE_NAME);
   }

   @Test
   public void deleteReferencedLibrariesFromContextMenuTest() throws Exception
   {
      IDE.PROJECT.PACKAGE_EXPLORER.waitItemInPackageExplorer(REFERENCED_LIBRARIES);
      IDE.PROJECT.PACKAGE_EXPLORER.openContextMenuOnSelectedItemInPackageExplorer(REFERENCED_LIBRARIES);
      IDE.PROJECT.PACKAGE_EXPLORER.waitElementInContextMenuDisabled(MenuCommands.File.DELETE);
      IDE.PROJECT.PACKAGE_EXPLORER.typeKeys(Keys.ESCAPE.toString());
      IDE.PROJECT.PACKAGE_EXPLORER.waitContextMenuDisappear();

      IDE.PROJECT.PACKAGE_EXPLORER.openItemWithDoubleClick(REFERENCED_LIBRARIES);
      IDE.PROJECT.PACKAGE_EXPLORER.openContextMenuOnSelectedItemInPackageExplorer(REF_LIB_ITEM);
      IDE.PROJECT.PACKAGE_EXPLORER.waitElementInContextMenuDisabled(MenuCommands.File.DELETE);
      IDE.PROJECT.PACKAGE_EXPLORER.typeKeys(Keys.ESCAPE.toString());
      IDE.PROJECT.PACKAGE_EXPLORER.waitContextMenuDisappear();
   }

   @Test
   public void deleteProjectFromContextMenuTest() throws Exception
   {
      IDE.PROJECT.PACKAGE_EXPLORER.waitItemInPackageExplorer(PROJECT);
      IDE.PROJECT.PACKAGE_EXPLORER.openContextMenuOnSelectedItemInPackageExplorer(PROJECT);
      delete();
      IDE.PROJECT.PACKAGE_EXPLORER.waitItemInPackageExplorerIsNotPresent(PROJECT);
   }

   /**
   * @throws Exception
   * @throws InterruptedException
   */
   private void delete() throws Exception, InterruptedException
   {
      IDE.PROJECT.PACKAGE_EXPLORER.waitElementInContextMenuEnabled(MenuCommands.File.DELETE);
      IDE.PROJECT.PACKAGE_EXPLORER.clickOnItemInContextMenu(MenuCommands.File.DELETE);
      IDE.DELETE.waitOpened();
      IDE.DELETE.clickOkButton();
      IDE.LOADER.waitClosed();
   }
}
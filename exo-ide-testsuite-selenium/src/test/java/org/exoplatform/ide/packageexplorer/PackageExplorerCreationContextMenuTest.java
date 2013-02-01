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

/**
 * @author <a href="mailto:riuvshin@exoplatform.com">Roman Iuvshin</a>
 * @version 27.12.2012 22:49:12
 *
 */
public class PackageExplorerCreationContextMenuTest extends BaseTest
{
   private static final String PROJECT = "CreationPrj";

   final static String filePath = "src/test/resources/org/exoplatform/ide/extension/maven/TestSpringProjectWithPOM.zip";

   private static final String NEW_FOLDER_NAME = "newFolder";

   private static final String FILE_NAME = "file.txt";

   private static final String NEW_PACKAGE_NAME = "newpackagename";

   private static final String SOURCE_FOLDER = "src/main/java";

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
         VirtualFileSystemUtils.delete(WS_URL + PROJECT);
      }
      catch (Exception e)
      {
      }
   }

   @Test
   public void createNewFolderFromContextMenuTest() throws Exception
   {
      IDE.PROJECT.EXPLORER.waitOpened();
      IDE.PROJECT.OPEN.openProject(PROJECT);
      IDE.PROGRESS_BAR.waitProgressBarControlClose();
      IDE.PROJECT.PACKAGE_EXPLORER.waitPackageExplorerOpened();

      IDE.PROJECT.PACKAGE_EXPLORER.selectItemInPackageExplorer(PROJECT);
      IDE.PROJECT.PACKAGE_EXPLORER.openContextMenuOnSelectedItemInPackageExplorer(PROJECT);
      IDE.PROJECT.PACKAGE_EXPLORER.waitElementInContextMenuEnabled(MenuCommands.New.NEW);
      IDE.PROJECT.PACKAGE_EXPLORER.clickOnItemInContextMenu(MenuCommands.New.NEW);

      IDE.MENU.clickOnNewMenuItem(MenuCommands.New.FOLDER);
      IDE.FOLDER.waitOpened();
      IDE.FOLDER.typeFolderName(NEW_FOLDER_NAME);
      IDE.FOLDER.clickCreateButton();
      IDE.LOADER.waitClosed();
      IDE.PROJECT.PACKAGE_EXPLORER.waitItemInPackageExplorer(NEW_FOLDER_NAME);
   }

   @Test
   public void createNewFileFromContextMenuTest() throws Exception
   {
      IDE.PROJECT.PACKAGE_EXPLORER.waitPackageExplorerOpened();

      IDE.PROJECT.PACKAGE_EXPLORER.openContextMenuOnSelectedItemInPackageExplorer(PROJECT);
      IDE.PROJECT.PACKAGE_EXPLORER.waitElementInContextMenuEnabled(MenuCommands.New.NEW);
      IDE.PROJECT.PACKAGE_EXPLORER.clickOnItemInContextMenu(MenuCommands.New.NEW);

      IDE.MENU.clickOnNewMenuItem(MenuCommands.New.TEXT_FILE);
      IDE.EDITOR.waitActiveFile();
      IDE.MENU.runCommand(MenuCommands.File.FILE, MenuCommands.File.SAVE_AS);
      IDE.ASK_FOR_VALUE_DIALOG.waitOpened();
      IDE.ASK_FOR_VALUE_DIALOG.setValue(FILE_NAME);
      IDE.ASK_FOR_VALUE_DIALOG.clickOkButton();
      IDE.LOADER.waitClosed();
      IDE.PROJECT.PACKAGE_EXPLORER.waitItemInPackageExplorer(FILE_NAME);
   }

   @Test
   public void createNewPackageFromContextMenuTest() throws Exception
   {
      IDE.PROJECT.PACKAGE_EXPLORER.waitPackageExplorerOpened();
      IDE.PROJECT.PACKAGE_EXPLORER.openContextMenuOnSelectedItemInPackageExplorer(SOURCE_FOLDER);
      IDE.PROJECT.PACKAGE_EXPLORER.waitElementInContextMenuEnabled(MenuCommands.New.NEW);
      IDE.PROJECT.PACKAGE_EXPLORER.clickOnItemInContextMenu(MenuCommands.New.NEW);
      IDE.MENU.clickOnNewMenuItem(MenuCommands.New.PACKAGE);
      IDE.PROJECT.PACKAGE_EXPLORER.waitCreateNewPackageForm();
      IDE.PROJECT.PACKAGE_EXPLORER.typeNewPackageName(NEW_PACKAGE_NAME);
      IDE.PROJECT.PACKAGE_EXPLORER.clickCreateNewPackageButton();
      IDE.PROJECT.PACKAGE_EXPLORER.waitItemInPackageExplorer(NEW_PACKAGE_NAME);
   }
}
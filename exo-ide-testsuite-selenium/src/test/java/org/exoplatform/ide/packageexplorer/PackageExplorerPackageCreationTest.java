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
import org.exoplatform.ide.ToolbarCommands;
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
public class PackageExplorerPackageCreationTest extends BaseTest
{
   private static final String PROJECT = "PackageCreationPrj";

   private static final String PACKAGE_NAME = "org.codenvy";

   private static final String WRONG_PACKAGE_NAME = "ThisISWROng";

   final String filePath = "src/test/resources/org/exoplatform/ide/operation/java/JavaCommentsTest.zip";

   static Map<String, Link> project;

   @BeforeClass
   public static void setUp()
   {
      final String filePath = "src/test/resources/org/exoplatform/ide/operation/java/JavaCommentsTest.zip";

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
   public void createPackageTest() throws Exception
   {
      IDE.PROJECT.EXPLORER.waitOpened();
      IDE.PROJECT.OPEN.openProject(PROJECT);
      IDE.PROGRESS_BAR.waitProgressBarControlClose();
      IDE.PROJECT.PACKAGE_EXPLORER.waitPackageExplorerOpened();

      IDE.PROJECT.PACKAGE_EXPLORER.waitItemInPackageExplorer("src/main/java");
      IDE.PROJECT.PACKAGE_EXPLORER.openItemWithDoubleClick("src/main/java");
      IDE.PROJECT.PACKAGE_EXPLORER.selectItemInPackageExplorer("src/main/java");
      IDE.MENU.runCommand(MenuCommands.File.FILE, MenuCommands.New.NEW, MenuCommands.New.PACKAGE);
      IDE.LOADER.waitClosed();
      IDE.PROJECT.PACKAGE_EXPLORER.waitCreateNewPackageForm();
      IDE.PROJECT.PACKAGE_EXPLORER.typeNewPackageName(PACKAGE_NAME);
      IDE.PROJECT.PACKAGE_EXPLORER.clickCreateNewPackageButton();
      IDE.PROJECT.PACKAGE_EXPLORER.waitItemInPackageExplorer(PACKAGE_NAME);

      //checking that structure was created
      IDE.PROJECT.PACKAGE_EXPLORER.closePackageExplorer();
      IDE.PROJECT.EXPLORER.waitOpened();
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/src");
      IDE.PROJECT.EXPLORER.openItem(PROJECT + "/src");
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/src/main");
      IDE.PROJECT.EXPLORER.openItem(PROJECT + "/src/main");
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/src/main/java");
      IDE.PROJECT.EXPLORER.openItem(PROJECT + "/src/main/java");

      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/src/main/java/org");
      IDE.PROJECT.EXPLORER.openItem(PROJECT + "/src/main/java/org");
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/src/main/java/org/codenvy");
   }

   @Test
   public void createPackageWithWrongNameTest() throws Exception
   {
      IDE.TOOLBAR.runCommand(ToolbarCommands.PackageExplorer.PACKAGE_EXPLORER);
      IDE.PROJECT.PACKAGE_EXPLORER.waitPackageExplorerOpened();
      IDE.PROJECT.PACKAGE_EXPLORER.selectItemInPackageExplorer(PACKAGE_NAME);

      IDE.PROJECT.PACKAGE_EXPLORER.selectItemInPackageExplorer("src/main/java");
      IDE.MENU.runCommand(MenuCommands.File.FILE, MenuCommands.New.NEW, MenuCommands.New.PACKAGE);
      IDE.LOADER.waitClosed();
      IDE.PROJECT.PACKAGE_EXPLORER.waitCreateNewPackageForm();
      IDE.PROJECT.PACKAGE_EXPLORER.typeNewPackageName(Keys.BACK_SPACE.toString());
      IDE.PROJECT.PACKAGE_EXPLORER.waitEmptyNameFieldWarningInCreatePackageForm();
      IDE.PROJECT.PACKAGE_EXPLORER.typeNewPackageName(WRONG_PACKAGE_NAME);
      IDE.PROJECT.PACKAGE_EXPLORER.waitConventionWarningInCreatePackageForm();
      IDE.PROJECT.PACKAGE_EXPLORER.clickCreateNewPackageButton();
      IDE.PROJECT.PACKAGE_EXPLORER.waitItemInPackageExplorer(WRONG_PACKAGE_NAME);
   }

   @Test
   public void checkCreatePackageButtonTest() throws Exception
   {
      IDE.PROJECT.PACKAGE_EXPLORER.waitPackageExplorerOpened();
      IDE.PROJECT.PACKAGE_EXPLORER.selectItemInPackageExplorer(PROJECT);
      IDE.MENU.waitSubCommandDisabled(MenuCommands.File.FILE, MenuCommands.New.NEW, MenuCommands.New.PACKAGE);
      IDE.PROJECT.PACKAGE_EXPLORER.selectItemInPackageExplorer("src/main/java");
      IDE.MENU.waitSubCommandEnabled(MenuCommands.File.FILE, MenuCommands.New.NEW, MenuCommands.New.PACKAGE);
      IDE.PROJECT.PACKAGE_EXPLORER.selectItemInPackageExplorer("pom.xml");
      IDE.MENU.waitSubCommandDisabled(MenuCommands.File.FILE, MenuCommands.New.NEW, MenuCommands.New.PACKAGE);
      IDE.PROJECT.PACKAGE_EXPLORER.selectItemInPackageExplorer("Referenced Libraries");
      IDE.MENU.waitSubCommandDisabled(MenuCommands.File.FILE, MenuCommands.New.NEW, MenuCommands.New.PACKAGE);
      IDE.PROJECT.PACKAGE_EXPLORER.selectItemInPackageExplorer("Referenced Libraries");
      IDE.MENU.waitSubCommandDisabled(MenuCommands.File.FILE, MenuCommands.New.NEW, MenuCommands.New.PACKAGE);
      IDE.PROJECT.PACKAGE_EXPLORER.selectItemInPackageExplorer("src");
      IDE.MENU.waitSubCommandDisabled(MenuCommands.File.FILE, MenuCommands.New.NEW, MenuCommands.New.PACKAGE);
   }

}

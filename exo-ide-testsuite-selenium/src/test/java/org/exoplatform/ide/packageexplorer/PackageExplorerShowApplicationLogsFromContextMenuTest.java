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

import static org.junit.Assert.assertTrue;

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
public class PackageExplorerShowApplicationLogsFromContextMenuTest extends BaseTest
{
   private static final String PROJECT = "ShowLogsTestPrj";

   final static String filePath = "src/test/resources/org/exoplatform/ide/extension/maven/TestSpringProjectWithPOM.zip";

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
   public void showApplicationLogsFromContextMenuTest() throws Exception
   {
      IDE.PROJECT.EXPLORER.waitOpened();
      IDE.PROJECT.OPEN.openProject(PROJECT);
      IDE.PROGRESS_BAR.waitProgressBarControlClose();
      IDE.PROJECT.PACKAGE_EXPLORER.waitPackageExplorerOpened();

      IDE.PROJECT.PACKAGE_EXPLORER.selectItemInPackageExplorer(PROJECT);
      IDE.PROJECT.PACKAGE_EXPLORER.openContextMenuOnSelectedItemInPackageExplorer(PROJECT);
      IDE.PROJECT.PACKAGE_EXPLORER.waitElementInContextMenuEnabled(MenuCommands.Run.RUN_APPLICATION);
      IDE.PROJECT.PACKAGE_EXPLORER.waitElementInContextMenuDisabled(MenuCommands.Run.STOP_APPLICATION);
      IDE.PROJECT.PACKAGE_EXPLORER.waitElementInContextMenuDisabled(MenuCommands.Run.SHOW_LOGS);
      IDE.PROJECT.PACKAGE_EXPLORER.clickOnItemInContextMenu(MenuCommands.Run.RUN_APPLICATION);

      IDE.PROGRESS_BAR.waitProgressBarControlClose();
      IDE.OUTPUT.waitForMessageShow(3, 120);
      assertTrue(IDE.OUTPUT.getOutputMessage(3).contains("exoplatform."));
      IDE.PROJECT.PACKAGE_EXPLORER.openContextMenuOnSelectedItemInPackageExplorer(PROJECT);
      IDE.PROJECT.PACKAGE_EXPLORER.clickOnItemInContextMenu(MenuCommands.Run.SHOW_LOGS);
      IDE.LOADER.waitClosed();
      IDE.OUTPUT.waitForMessageShow(4, 120);
      assertTrue(IDE.OUTPUT.getOutputMessage(4).contains("INFO: Server startup in"));
   }
}
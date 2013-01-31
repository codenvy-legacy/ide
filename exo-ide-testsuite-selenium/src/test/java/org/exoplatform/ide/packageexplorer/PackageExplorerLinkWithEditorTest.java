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
public class PackageExplorerLinkWithEditorTest extends BaseTest
{
   private static final String PROJECT = "LinkEditorPrj";

   final static String filePath = "src/test/resources/org/exoplatform/ide/extension/maven/TestSpringProjectWithPOM.zip";

   private static final String FIRST_FILE = "index.jsp";

   private static final String SECOND_FILE = "web.xml";

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
   public void linkWithEditorTest() throws Exception
   {
      IDE.PROJECT.EXPLORER.waitOpened();
      IDE.PROJECT.OPEN.openProject(PROJECT);
      IDE.PROGRESS_BAR.waitProgressBarControlClose();
      IDE.PROJECT.PACKAGE_EXPLORER.waitPackageExplorerOpened();
      IDE.PROJECT.EXPLORER.clickCloseProjectExplorer(PROJECT);
      IDE.PROJECT.PACKAGE_EXPLORER.clickOnLinkWithEditorButton();

      IDE.PROJECT.PACKAGE_EXPLORER.openItemWithDoubleClick("src");
      IDE.PROJECT.PACKAGE_EXPLORER.waitItemInPackageExplorer("main");
      IDE.PROJECT.PACKAGE_EXPLORER.openItemWithDoubleClick("main");
      IDE.PROJECT.PACKAGE_EXPLORER.waitItemInPackageExplorer("webapp");
      IDE.PROJECT.PACKAGE_EXPLORER.openItemWithDoubleClick("webapp");
      IDE.PROJECT.PACKAGE_EXPLORER.waitItemInPackageExplorer(FIRST_FILE);
      IDE.PROJECT.PACKAGE_EXPLORER.openItemWithDoubleClick(FIRST_FILE);
      IDE.EDITOR.waitActiveFile();
      IDE.EDITOR.waitTabPresent(FIRST_FILE);
      IDE.PROJECT.PACKAGE_EXPLORER.waitItemInPackageExplorer("WEB-INF");
      IDE.PROJECT.PACKAGE_EXPLORER.openItemWithDoubleClick("WEB-INF");
      IDE.PROJECT.PACKAGE_EXPLORER.waitItemInPackageExplorer(SECOND_FILE);
      IDE.PROJECT.PACKAGE_EXPLORER.openItemWithDoubleClick(SECOND_FILE);
      IDE.EDITOR.waitActiveFile();
      IDE.EDITOR.waitTabPresent(SECOND_FILE);

      //close src folder
      IDE.PROJECT.PACKAGE_EXPLORER.openItemWithClickOnOpenIcon("src");

      IDE.PROJECT.PACKAGE_EXPLORER.waitItemInPackageExplorerIsNotPresent(FIRST_FILE);
      IDE.PROJECT.PACKAGE_EXPLORER.waitItemInPackageExplorerIsNotPresent(SECOND_FILE);
      IDE.EDITOR.selectTab(FIRST_FILE);
      IDE.PROJECT.PACKAGE_EXPLORER.waitItemInPackageExplorer(FIRST_FILE);
      IDE.PROJECT.PACKAGE_EXPLORER.waitItemInPackageExplorerIsNotPresent(SECOND_FILE);
      IDE.EDITOR.selectTab(SECOND_FILE);
      IDE.PROJECT.PACKAGE_EXPLORER.waitItemInPackageExplorer(SECOND_FILE);
      IDE.PROJECT.PACKAGE_EXPLORER.waitItemInPackageExplorer(FIRST_FILE);
   }
}
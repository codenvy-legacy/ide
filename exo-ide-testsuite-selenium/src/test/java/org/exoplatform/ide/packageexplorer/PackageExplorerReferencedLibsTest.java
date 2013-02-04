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
import org.openqa.selenium.Keys;

/**
 * @author <a href="mailto:riuvshin@exoplatform.com">Roman Iuvshin</a>
 * @version 27.12.2012 22:49:12
 *
 */
public class PackageExplorerReferencedLibsTest extends BaseTest
{
   private static final String PROJECT = "RefLibsPrj";

   private static final String DEPENDENCY =
      "\t<dependency>\n\t\t<groupId>junit</groupId>\n\t\t<artifactId>junit</artifactId>\n\t\t<version>4.10</version>\n\t\t<scope>test</scope>\n\t</dependency>";

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
   public void packageCreationTest() throws Exception
   {
      IDE.PROJECT.EXPLORER.waitOpened();
      IDE.PROJECT.OPEN.openProject(PROJECT);
      IDE.PROGRESS_BAR.waitProgressBarControlClose();
      IDE.PROJECT.PACKAGE_EXPLORER.waitPackageExplorerOpened();
      IDE.PROJECT.PACKAGE_EXPLORER.waitItemInPackageExplorer("Referenced Libraries");
      IDE.PROJECT.PACKAGE_EXPLORER.openItemWithDoubleClick("Referenced Libraries");
      IDE.PROJECT.PACKAGE_EXPLORER.waitItemInPackageExplorer("servlet-api-2.5.jar");
      IDE.PROJECT.PACKAGE_EXPLORER.waitItemInPackageExplorer("spring-webmvc-3.0.5.RELEASE.jar");
      IDE.PROJECT.PACKAGE_EXPLORER.waitItemInPackageExplorer("junit-3.8.1.jar");
      IDE.PROJECT.PACKAGE_EXPLORER.waitItemInPackageExplorerIsNotPresent("junit-4.10.jar");

      //adding dependency in pom
      IDE.PROJECT.PACKAGE_EXPLORER.openItemWithDoubleClick("pom.xml");
      IDE.EDITOR.waitActiveFile();
      IDE.GOTOLINE.goToLine(31);
      IDE.EDITOR.typeTextIntoEditor(Keys.ENTER.toString());
      IDE.EDITOR.typeTextIntoEditor(Keys.ARROW_UP.toString());
      IDE.EDITOR.typeTextIntoEditor(DEPENDENCY);
      IDE.EDITOR.typeTextIntoEditor(Keys.CONTROL.toString() + "s");
      IDE.LOADER.waitClosed();

      IDE.PROJECT.PACKAGE_EXPLORER.waitItemInPackageExplorer("junit-4.10.jar");
   }
}

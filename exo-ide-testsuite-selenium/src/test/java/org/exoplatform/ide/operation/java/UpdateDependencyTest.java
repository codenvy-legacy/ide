/*
 * Copyright (C) 2013 eXo Platform SAS.
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
package org.exoplatform.ide.operation.java;

import java.util.Map;

import org.exoplatform.ide.VirtualFileSystemUtils;
import org.exoplatform.ide.vfs.shared.Link;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.Keys;

/**
 * @author <a href="mailto:foo@bar.org">Foo Bar</a>
 * @version $Id: exo-jboss-codetemplates.xml 34360 2009-07-22 23:58:59Z aheritier $
 *
 */
public class UpdateDependencyTest extends ServicesJavaTextFuction
{
   private static final String PROJECT = UpdateDependencyTest.class.getSimpleName();

   private String DEPENDENCY =
      "    <dependency>\n      <groupId>net.twonky</groupId>\n      <artifactId>twonky-string-utils</artifactId>\n      <version>1.0</version>\n    </dependency>";

   @BeforeClass
   public static void setUp()
   {
      final String filePath = "src/test/resources/org/exoplatform/ide/operation/java/FormatTextTest.zip";
      try
      {
         Map<String, Link> project = VirtualFileSystemUtils.importZipProject(PROJECT, filePath);
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
   public void updateDependencyTets() throws Exception
   {
      IDE.PROJECT.EXPLORER.waitOpened();
      IDE.PROJECT.OPEN.openProject(PROJECT);
      IDE.PROGRESS_BAR.waitProgressBarControlClose();
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT);
      openJavaClassForFormat(PROJECT);
      IDE.PROJECT.PACKAGE_EXPLORER.openItemWithDoubleClick("pom.xml");
      IDE.EDITOR.waitActiveFile();
      IDE.GOTOLINE.goToLine(30);
      IDE.EDITOR.typeTextIntoEditor(Keys.END.toString());
      IDE.EDITOR.typeTextIntoEditor("\n");
      IDE.EDITOR.typeTextIntoEditor(DEPENDENCY);
      IDE.EDITOR.typeTextIntoEditor(Keys.CONTROL+"s");
      IDE.EDITOR.waitNoContentModificationMark("pom.xml");
      IDE.LOADER.waitClosed();
   }
}

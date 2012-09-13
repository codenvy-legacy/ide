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
package org.exoplatform.ide.operation.edit.outline;

import static org.junit.Assert.*;
import static org.junit.Assert.assertTrue;

import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.TestConstants;
import org.exoplatform.ide.ToolbarCommands;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.exoplatform.ide.core.Outline.TokenType;
import org.exoplatform.ide.operation.autocompletion.CodeAssistantBaseTest;
import org.exoplatform.ide.operation.java.DeleteCurrentLineWithKeysTest;
import org.exoplatform.ide.vfs.shared.Link;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.Map;

/**
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @author <a href="mailto:dmitry.ndp@gmail.com">Dmytro Nochevnov</a> 
 * @version $Id: Oct 25, 2010 $
 *
 */
public class CodeOutLineJavaTest extends CodeAssistantBaseTest
{
   private final static String FILE_NAME = "JavaCodeOutline.java";

   private final static String PROJECT = CodeOutLineJavaTest.class.getSimpleName();

   @BeforeClass
   public static void setUp()
   {
      final String filePath = "src/test/resources/org/exoplatform/ide/operation/java/calc.zip";

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
   public void testCodeOutLineJava() throws Exception
   {
      IDE.PROJECT.EXPLORER.waitOpened();
      IDE.PROJECT.OPEN.openProject(PROJECT);
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + FILE_NAME);
      IDE.LOADER.waitClosed();

      IDE.PROJECT.EXPLORER.openItem(PROJECT + "/" + FILE_NAME);
      IDE.EDITOR.waitActiveFile(PROJECT + "/" + FILE_NAME);

      // open outline panel
      IDE.TOOLBAR.runCommand(ToolbarCommands.View.SHOW_OUTLINE);

      // wait while outline tree is loaded
      Thread.sleep(TestConstants.SLEEP * 10);
     IDE.OUTLINE.isHiglightBorderPresent();

      IDE.OUTLINE.waitOutlineTreeVisible();

      // check for presence and visibility of outline tab
      assertTrue(IDE.OUTLINE.isOutlineTreePresent());
      assertTrue(IDE.OUTLINE.isJavaOutlineViewVisible());

      // expand outline tree
      IDE.OUTLINE.selectItem("JavaCodeOutline");
      assertEquals(IDE.OUTLINE.getItemLabel(1), "example");
      assertEquals(IDE.OUTLINE.getItemLabel(2), "import declarations");
      assertEquals(IDE.OUTLINE.getItemLabel(3), "JavaCodeOutline");

      IDE.OUTLINE.selectItem("import declarations");
      IDE.OUTLINE.expandSelectItem(2);
      assertEquals(IDE.OUTLINE.getItemLabel(3), "java.awt");
      assertEquals(IDE.OUTLINE.getItemLabel(4), "JavaCodeOutline");
 }

}

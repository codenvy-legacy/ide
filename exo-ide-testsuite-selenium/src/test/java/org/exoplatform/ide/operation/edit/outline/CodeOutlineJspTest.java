/*
 * Copyright (C) 2011 eXo Platform SAS.
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

import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.ToolbarCommands;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.exoplatform.ide.core.Outline.TokenType;
import org.exoplatform.ide.vfs.shared.Link;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Map;

/**
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: CodeOutlineJspTest Apr 27, 2011 10:47:14 AM evgen $
 *
 */
public class CodeOutlineJspTest extends BaseTest
{

   private final static String FILE_NAME = "JspCodeOutline.jsp";

   private final static String PROJECT = CodeOutlineJspTest.class.getSimpleName();

   private OutlineTreeHelper outlineTreeHelper;

   public CodeOutlineJspTest()
   {
      this.outlineTreeHelper = new OutlineTreeHelper();
   }

   @BeforeClass
   public static void setUp()
   {
      String filePath = "src/test/resources/org/exoplatform/ide/operation/edit/outline/test-jsp.jsp";
      try
      {
         Map<String, Link> project = VirtualFileSystemUtils.createDefaultProject(PROJECT);
         Link link = project.get(Link.REL_CREATE_FILE);
         VirtualFileSystemUtils.createFileFromLocal(link, FILE_NAME, MimeType.APPLICATION_JSP, filePath);
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
   public void testCodeOutlineJSP() throws Exception
   {
      IDE.PROJECT.EXPLORER.waitOpened();
      IDE.PROJECT.OPEN.openProject(PROJECT);
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + FILE_NAME);
      IDE.LOADER.waitClosed();

      IDE.PROJECT.EXPLORER.openItem(PROJECT + "/" + FILE_NAME);
      IDE.EDITOR.waitActiveFile(PROJECT + "/" + FILE_NAME);

      IDE.TOOLBAR.runCommand(ToolbarCommands.View.SHOW_OUTLINE);
      IDE.OUTLINE.waitOpened();
      IDE.OUTLINE.waitOutlineTreeVisible();

      checkTreeCorrectlyCreated();

      IDE.EDITOR.closeFile(FILE_NAME);
      IDE.EDITOR.waitTabNotPresent(FILE_NAME);
   }

   private void checkTreeCorrectlyCreated() throws Exception
   {
      // create initial outline tree map
      OutlineTreeHelper.init();

      // check is tree created correctly      
      outlineTreeHelper.checkOutlineTree();

      // expand outline tree
      outlineTreeHelper.expandOutlineTree();

      outlineTreeHelper.addOutlineItem("html", 1, TokenType.TAG);
      outlineTreeHelper.addOutlineItem("head", 2, TokenType.TAG);
      outlineTreeHelper.addOutlineItem("script", 8, TokenType.TAG);
      outlineTreeHelper.addOutlineItem("a", 9, TokenType.VARIABLE);
      outlineTreeHelper.addOutlineItem("body", 12, TokenType.TAG);
      outlineTreeHelper.addOutlineItem("java code", 13, TokenType.JSP_TAG);
      outlineTreeHelper.addOutlineItem("curentState", 14, TokenType.PROPERTY);
      outlineTreeHelper.addOutlineItem("identity", 17, TokenType.PROPERTY);
      outlineTreeHelper.addOutlineItem("i", 18, TokenType.PROPERTY);
      outlineTreeHelper.addOutlineItem("a", 23, TokenType.PROPERTY);

      // check is tree created correctly
      outlineTreeHelper.checkOutlineTree();
   }

}

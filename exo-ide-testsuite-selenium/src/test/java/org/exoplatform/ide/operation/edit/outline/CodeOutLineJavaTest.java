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

import java.util.Map;

import org.exoplatform.ide.ToolbarCommands;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.exoplatform.ide.operation.java.ServicesJavaTextFuction;
import org.exoplatform.ide.vfs.shared.Link;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @author <a href="mailto:dmitry.ndp@gmail.com">Dmytro Nochevnov</a> 
 * @version $Id: Oct 25, 2010 $
 *
 */
public class CodeOutLineJavaTest extends ServicesJavaTextFuction
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
      IDE.PROJECT.PACKAGE_EXPLORER.waitAndClosePackageExplorer();
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT);
      openSpringJavaTetsFile(PROJECT);

      //TODO Pause for build outline tree
      //after implementation method for check ready state, should be remove
      Thread.sleep(4000);

      // open outline panel
      IDE.TOOLBAR.runCommand(ToolbarCommands.View.SHOW_OUTLINE);

      // wait while outline tree is loaded
      IDE.OUTLINE.waitOutlineTreeVisible();
      IDE.OUTLINE.waitHiglightBorderJavaOutlinePresent();

      // check for presence and visibility of outline tab
      IDE.OUTLINE.waitJavaOutlineViewVisible();

      // expand all outline tree
      IDE.OUTLINE.selectItem("import declarations");
      IDE.OUTLINE.expandSelectItem(2);
      IDE.OUTLINE.selectItem("SumController");
      IDE.OUTLINE.expandSelectItem(3);
      checkAllNodes();
      checkMoveToTree();
      checkMoveInEditor();

   }

   //check all expand nodes
   private void checkAllNodes() throws Exception
   {
      //wait last node in tree
      IDE.OUTLINE.waitNodeWithSubNamePresent("handleRequestInternal(HttpServletRequest, HttpServletResponse)");
      IDE.OUTLINE.waitItemPresent("sumcontroller");
      IDE.OUTLINE.waitItemPresent("import declarations");
      IDE.OUTLINE.waitItemPresent("org.springframework.web.servlet.ModelAndView");
      IDE.OUTLINE.waitItemPresent("org.springframework.web.servlet.mvc.AbstractController");
      IDE.OUTLINE.waitItemPresent("javax.servlet.http.HttpServletRequest");
      IDE.OUTLINE.waitItemPresent("javax.servlet.http.HttpServletResponse");
      IDE.OUTLINE.waitItemPresent("SumController");
   }

   // check move in outline tree
   private void checkMoveToTree() throws Exception
   {
      IDE.OUTLINE.selectItem("org.springframework.web.servlet.ModelAndView");
      IDE.STATUSBAR.waitCursorPositionAt("2 : 1");

      IDE.OUTLINE.selectItem("org.springframework.web.servlet.mvc.AbstractController");
      IDE.STATUSBAR.waitCursorPositionAt("3 : 1");

      IDE.OUTLINE.selectItem("javax.servlet.http.HttpServletRequest");
      IDE.STATUSBAR.waitCursorPositionAt("4 : 1");

      IDE.OUTLINE.selectItem("javax.servlet.http.HttpServletResponse");
      IDE.STATUSBAR.waitCursorPositionAt("5 : 1");

      IDE.OUTLINE.selectItem("SumController");
      IDE.STATUSBAR.waitCursorPositionAt("10 : 1");

      IDE.OUTLINE.selectItem("handleRequestInternal(HttpServletRequest, HttpServletResponse)");
      IDE.STATUSBAR.waitCursorPositionAt("11 : 1");
   }

   //check move in editor and highlight elements in outline
   private void checkMoveInEditor() throws Exception
   {

      IDE.GOTOLINE.goToLine(2);
      IDE.OUTLINE.waitElementIsSelect("org.springframework.web.servlet.ModelAndView");

      IDE.GOTOLINE.goToLine(5);
      IDE.OUTLINE.waitElementIsSelect("javax.servlet.http.HttpServletResponse");

      IDE.GOTOLINE.goToLine(3);
      IDE.OUTLINE.waitElementIsSelect("org.springframework.web.servlet.mvc.AbstractController");

      IDE.GOTOLINE.goToLine(4);
      IDE.OUTLINE.waitElementIsSelect("javax.servlet.http.HttpServletRequest");

      IDE.GOTOLINE.goToLine(10);
      IDE.OUTLINE.waitElementIsSelect("SumController");

      IDE.GOTOLINE.goToLine(13);
      IDE.OUTLINE.waitElementIsSelect("handleRequestInternal(HttpServletRequest, HttpServletResponse)");
   }

}

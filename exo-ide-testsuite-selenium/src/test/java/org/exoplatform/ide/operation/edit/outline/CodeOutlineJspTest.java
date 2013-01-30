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

import java.util.Map;

import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.ToolbarCommands;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.exoplatform.ide.vfs.shared.Link;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: CodeOutlineJspTest Apr 27, 2011 10:47:14 AM evgen $
 * 
 */
public class CodeOutlineJspTest extends BaseTest
{

   private final static String FILE_NAME = "JspCodeOutline.jsp";

   private final static String PROJECT = CodeOutlineJspTest.class.getSimpleName();

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
      IDE.EDITOR.waitActiveFile();
      //TODO Pause for build outline tree
      //after implementation method for check ready state, should be remove
      Thread.sleep(4000);

      IDE.TOOLBAR.runCommand(ToolbarCommands.View.SHOW_OUTLINE);
      IDE.OUTLINE.waitOpened();
      IDE.OUTLINE.waitOutlineTreeVisible();

      checkTreeCorrectlyCreated();
      checkClickOnTreeElements();

      IDE.EDITOR.closeFile(FILE_NAME);
      IDE.EDITOR.waitTabNotPresent(FILE_NAME);
   }

   private void checkTreeCorrectlyCreated() throws Exception
   {
      IDE.GOTOLINE.goToLine(2);
      IDE.OUTLINE.waitElementIsSelect("head");
      IDE.OUTLINE.waitItemAtPosition("html", 1);
      IDE.OUTLINE.waitItemAtPosition("script", 3);
      IDE.OUTLINE.waitItemAtPosition("body", 4);

      IDE.GOTOLINE.goToLine(4);
      IDE.OUTLINE.waitElementIsSelect("java code");
      IDE.GOTOLINE.goToLine(9);
      IDE.OUTLINE.waitItemAtPosition("a : String", 5);
      IDE.GOTOLINE.goToLine(14);
      IDE.OUTLINE.waitElementIsSelect("curentState");
      IDE.OUTLINE.waitItemAtPosition("identity", 9);
      IDE.OUTLINE.waitItemAtPosition("i", 10);
      IDE.OUTLINE.waitItemAtPosition("a", 11);

   }

   private void checkClickOnTreeElements() throws Exception
   {
      // check variable a
      // click on 5 row need for reparse tree
      IDE.OUTLINE.selectRow(11);
      IDE.STATUSBAR.waitCursorPositionAt("23 : 1");

      // check javascript variable a
      IDE.OUTLINE.selectRow(5);
      IDE.STATUSBAR.waitCursorPositionAt("9 : 1");

      // check tag head
      IDE.OUTLINE.selectRow(2);
      IDE.STATUSBAR.waitCursorPositionAt("2 : 1");

      // check javascript javacode
      IDE.OUTLINE.selectRow(7);
      IDE.STATUSBAR.waitCursorPositionAt("13 : 1");
   }

}

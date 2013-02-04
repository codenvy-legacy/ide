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

import static org.junit.Assert.assertEquals;

import java.util.Map;

import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.MenuCommands;
import org.exoplatform.ide.ToolbarCommands;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.exoplatform.ide.vfs.shared.Link;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Created by The eXo Platform SAS.
 * 
 * 
 * @author <a href="musienko.maxim@gmail.com">Musienko Maksim</a>
 * @version $Id: ${date} ${time}
 */
public class CodeOutLineBaseOperationTest extends BaseTest
{
   private final static String PROJECT = CodeOutLineBaseOperationTest.class.getSimpleName();

   private final static String FILE_NAME = "GroovyTemplateCodeOutline.gtmpl";

   @BeforeClass
   public static void setUp()
   {
      String filePath = "src/test/resources/org/exoplatform/ide/operation/edit/outline/GroovyTemplateCodeOutline.gtmpl";
      try
      {
         Map<String, Link> project = VirtualFileSystemUtils.createDefaultProject(PROJECT);
         Link link = project.get(Link.REL_CREATE_FILE);
         VirtualFileSystemUtils.createFileFromLocal(link, FILE_NAME, MimeType.GROOVY_TEMPLATE, filePath);
      }
      catch (Exception e)
      {
      }
   }

   @AfterClass
   public static void tearDown() throws Exception
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
   public void testNavigationOnOutLineGroovyTemplate() throws Exception
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

      // click on second groovy code node
      IDE.OUTLINE.selectRow(2);
      IDE.OUTLINE.waitElementIsSelect("groovy code");

      // check, than cursor go to line
      IDE.STATUSBAR.waitCursorPositionAt("26 : 1");

      IDE.MENU.runCommand(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.DELETE_CURRENT_LINE);
      IDE.STATUSBAR.waitCursorPositionAt("26 : 1");
      IDE.GOTOLINE.goToLine(2);
      IDE.STATUSBAR.waitCursorPositionAt("2 : 1");
      IDE.OUTLINE.waitItemAtPosition("div", 12);
      assertEquals("div", IDE.OUTLINE.getItemLabel(12));
      assertEquals("a1 : Object", IDE.OUTLINE.getItemLabel(2));

      IDE.GOTOLINE.goToLine(28);
      IDE.OUTLINE.waitItemAtPosition("groovy code", 14);
      IDE.STATUSBAR.waitCursorPositionAt("28 : 1");
      assertEquals("groovy code", IDE.OUTLINE.getItemLabel(1));
      assertEquals("div", IDE.OUTLINE.getItemLabel(12));
      assertEquals("div", IDE.OUTLINE.getItemLabel(13));
      assertEquals("groovy code", IDE.OUTLINE.getItemLabel(14));
   }
}
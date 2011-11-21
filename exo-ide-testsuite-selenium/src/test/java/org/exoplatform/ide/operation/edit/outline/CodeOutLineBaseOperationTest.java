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
import static org.junit.Assert.assertTrue;

import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.TestConstants;
import org.exoplatform.ide.ToolbarCommands;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.exoplatform.ide.vfs.shared.Link;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Map;

/**
 * Created by The eXo Platform SAS.
 * 
 * 
 * @author <a href="musienko.maxim@gmail.com">Musienko Maksim</a>
 * @version $Id:   ${date} ${time}
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
         e.printStackTrace();
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
         e.printStackTrace();
      }
   }

   // IDE-178:Groovy Template Code Outline
   @Test
   public void testNavigationOnOutLineGroovyTemplate() throws Exception
   {
      IDE.PROJECT.EXPLORER.waitOpened();
      IDE.PROJECT.OPEN.openProject(PROJECT);
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + FILE_NAME);
      
      IDE.PROJECT.EXPLORER.openItem(PROJECT + "/" + FILE_NAME);
      IDE.EDITOR.waitActiveFile(PROJECT + "/" + FILE_NAME);

      IDE.TOOLBAR.runCommand(ToolbarCommands.View.SHOW_OUTLINE);
      IDE.OUTLINE.waitOpened();
      
      //click on second groovy code node
      IDE.OUTLINE.selectRow(2);
      
      //check, than cursor go to line
      assertEquals("26 : 1", IDE.STATUSBAR.getCursorPosition());
      IDE.EDITOR.deleteLinesInEditor(0, 7);
      
      //TODO redraw condition
      Thread.sleep(TestConstants.SLEEP);

      assertEquals("26 : 1", IDE.STATUSBAR.getCursorPosition());
      assertEquals("groovy code", IDE.OUTLINE.getItemLabel(1));
      
      IDE.OUTLINE.doubleClickItem(1);
      assertEquals("div", IDE.OUTLINE.getItemLabel(12));
      assertEquals("a1 : Object", IDE.OUTLINE.getItemLabel(2));
      assertTrue(IDE.OUTLINE.isItemSelected(1));

      IDE.GOTOLINE.goToLine(27);
      assertEquals("27 : 1", IDE.STATUSBAR.getCursorPosition());

      assertEquals("groovy code", IDE.OUTLINE.getItemLabel(1));
      assertEquals("div", IDE.OUTLINE.getItemLabel(12));
      assertEquals("a", IDE.OUTLINE.getItemLabel(13));
      assertEquals("groovy code", IDE.OUTLINE.getItemLabel(14));

      assertTrue(IDE.OUTLINE.isItemSelected(14));
   }
}
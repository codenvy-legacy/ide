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

import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.ToolbarCommands;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.exoplatform.ide.vfs.shared.Link;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Created by The eXo Platform SAS.
 * 
 * @author <a href="mailto:njusha.exo@gmail.com">Nadia Zavalko</a>
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: ${date} ${time}
 */
public class CodeOutLineGroovyTemplateTest extends BaseTest
{

   private final static String FILE_NAME = "GroovyTemplateCodeOutline.gtmpl";

   private final static String PROJECT = CodeOutLineGroovyTemplateTest.class.getSimpleName();

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
   public void testCreateOutlineTreeGroovyTemplate() throws Exception
   {

      //step 1 open groovy template, open outline
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

      //step 2 check nodes in outline tree after open
      IDE.OUTLINE.waitItemAtPosition("groovy code", 1);
      IDE.OUTLINE.waitItemAtPosition("groovy code", 2);
      IDE.OUTLINE.waitItemAtPosition("div", 3);
      IDE.OUTLINE.waitItemAtPosition("groovy code", 4);
      IDE.OUTLINE.waitItemAtPosition("div", 5);

      //step 3 check nodes in outline tree after open
      IDE.GOTOLINE.goToLine(2);
      IDE.OUTLINE.waitItemAtPosition("a1 : Object", 2);
      IDE.OUTLINE.waitItemAtPosition("a2() : boolean", 4);
      IDE.OUTLINE.waitItemAtPosition("a3(String) : void", 5);
      IDE.OUTLINE.waitItemAtPosition("a4 : Integer", 11);

      //step 4 open  node and check opened items
      IDE.GOTOLINE.goToLine(29);
      IDE.OUTLINE.waitElementIsSelect("groovy code");
      IDE.OUTLINE.waitItemAtPosition("div", 14);
      IDE.OUTLINE.waitItemAtPosition("groovy code", 16);

      //step 5 open all nodes and check opened items
      IDE.GOTOLINE.goToLine(34);
      IDE.OUTLINE.waitElementIsSelect("groovy code");
      IDE.OUTLINE.waitItemAtPosition("groovy code", 16);

      //step 6 goto a3(String) and chek cursor position
      IDE.OUTLINE.selectItem("a3");
      IDE.STATUSBAR.waitCursorPositionAt("7 : 1");

      //step 7 goto 'a' and chek cursor position
      IDE.OUTLINE.selectItem("a");
      IDE.STATUSBAR.waitCursorPositionAt("34 : 1");

      IDE.OUTLINE.selectItem("isSelected");
      IDE.STATUSBAR.waitCursorPositionAt("11 : 1");
   }

}

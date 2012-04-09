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

import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.ToolbarCommands;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.exoplatform.ide.vfs.shared.Link;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.Map;

/**
 * Created by The eXo Platform SAS.
 * @author <a href="dmitry.ndp@gmail.com">Dmytro Nochevnov</a>
 * @author <a href="mailto:njusha.exo@gmail.com">Nadia Zavalko</a>
 * @author <a href="mailto:azatsarynnyy@exoplatform.org">Artem Zatsarynnyy</a>
 * @version $Id:
 *
 */

public class CodeOutlineXmlTest extends BaseTest
{
   private final static String FILE_NAME = "XmlCodeOutline.xml";

   private final static String PROJECT = CodeOutlineXmlTest.class.getSimpleName();

   private OulineTreeHelper outlineTreeHelper;

   public CodeOutlineXmlTest()
   {
      this.outlineTreeHelper = new OulineTreeHelper();
   }

   @BeforeClass
   public static void setUp()
   {

      String filePath = "src/test/resources/org/exoplatform/ide/operation/edit/outline/XmlCodeOutline.xml";
      try
      {
         Map<String, Link> project = VirtualFileSystemUtils.createDefaultProject(PROJECT);
         Link link = project.get(Link.REL_CREATE_FILE);
         VirtualFileSystemUtils.createFileFromLocal(link, FILE_NAME, MimeType.TEXT_XML, filePath);
      }
      catch (IOException e)
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
      catch (IOException e)
      {
      }
   }

   /**
    * IDE-174:XML Code Outline
    * @throws Exception
    */
   @Test
   public void testXmlCodeOutline() throws Exception
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
   }

   private void checkTreeCorrectlyCreated() throws Exception
   {
      // expand outline tree
      outlineTreeHelper.expandOutlineTree();

      // TODO issue IDE-1499
      IDE.GOTOLINE.goToLine(5);
      IDE.GOTOLINE.goToLine(10);

      //check web-app and sub nodes
      IDE.OUTLINE.selectRow(1);
      Assert.assertEquals(IDE.OUTLINE.getItemLabel(1), "web-app");
      assertEquals("2 : 1", IDE.STATUSBAR.getCursorPosition());

      IDE.OUTLINE.selectRow(2);
      Assert.assertEquals(IDE.OUTLINE.getItemLabel(2), "display-name");
      assertEquals("3 : 1", IDE.STATUSBAR.getCursorPosition());

      //check context-param and node and sub nodes
      IDE.OUTLINE.selectRow(3);
      Assert.assertEquals(IDE.OUTLINE.getItemLabel(3), "context-param");
      assertEquals("7 : 1", IDE.STATUSBAR.getCursorPosition());

      IDE.OUTLINE.selectRow(4);
      Assert.assertEquals(IDE.OUTLINE.getItemLabel(4), "param-name");
      assertEquals("8 : 1", IDE.STATUSBAR.getCursorPosition());

      IDE.OUTLINE.selectRow(5);
      Assert.assertEquals(IDE.OUTLINE.getItemLabel(5), "param-value");
      assertEquals("12 : 1", IDE.STATUSBAR.getCursorPosition());

      //check context-param and node and sub nodes
      IDE.OUTLINE.selectRow(6);
      Assert.assertEquals(IDE.OUTLINE.getItemLabel(6), "context-param");
      assertEquals("19 : 1", IDE.STATUSBAR.getCursorPosition());

      IDE.OUTLINE.selectRow(7);
      Assert.assertEquals(IDE.OUTLINE.getItemLabel(7), "param-name");
      assertEquals("20 : 1", IDE.STATUSBAR.getCursorPosition());

      IDE.OUTLINE.selectRow(8);
      Assert.assertEquals(IDE.OUTLINE.getItemLabel(8), "param-value");
      assertEquals("21 : 1", IDE.STATUSBAR.getCursorPosition());

      //check cdata tag
      IDE.OUTLINE.selectRow(9);
      Assert.assertEquals(IDE.OUTLINE.getItemLabel(9), "CDATA");
      assertEquals("24 : 1", IDE.STATUSBAR.getCursorPosition());

      //check filter tag and sub nodes
      IDE.OUTLINE.selectRow(10);
      Assert.assertEquals(IDE.OUTLINE.getItemLabel(10), "filter");
      assertEquals("27 : 1", IDE.STATUSBAR.getCursorPosition());

      IDE.OUTLINE.selectRow(11);
      Assert.assertEquals(IDE.OUTLINE.getItemLabel(11), "filter-name");
      assertEquals("28 : 1", IDE.STATUSBAR.getCursorPosition());

      IDE.OUTLINE.selectRow(12);
      Assert.assertEquals(IDE.OUTLINE.getItemLabel(12), "filter-class");
      assertEquals("29 : 1", IDE.STATUSBAR.getCursorPosition());
   }
}

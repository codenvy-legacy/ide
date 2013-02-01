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

import java.io.IOException;
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
 * Test for code outline for html files.
 * 
 * @author <a href="mailto:njusha.exo@gmail.com">Nadia Zavalko</a>
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id:   ${date} ${time}
 *
 */
public class CodeOutLineHtmlTest extends BaseTest
{

   private final static String FILE_NAME = "HtmlCodeOutline.html";

   private final static String PROJECT = CodeOutLineHtmlTest.class.getSimpleName();

   @BeforeClass
   public static void setUp()
   {
      String filePath = "src/test/resources/org/exoplatform/ide/operation/edit/outline/HtmlCodeOutline.html";
      try
      {
         Map<String, Link> project = VirtualFileSystemUtils.createDefaultProject(PROJECT);
         Link link = project.get(Link.REL_CREATE_FILE);
         VirtualFileSystemUtils.createFileFromLocal(link, FILE_NAME, MimeType.TEXT_HTML, filePath);
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

   // IDE-175:Html Code Outline
   @Test
   public void testCodeOutLineHtml() throws Exception
   {
      IDE.PROJECT.EXPLORER.waitOpened();
      IDE.PROJECT.OPEN.openProject(PROJECT);
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + FILE_NAME);
      IDE.LOADER.waitClosed();

      IDE.PROJECT.EXPLORER.openItem(PROJECT + "/" + FILE_NAME);
      IDE.EDITOR.waitActiveFile();
      //this delay needed for reparse all html code on staging
      Thread.sleep(4000);
      IDE.TOOLBAR.runCommand(ToolbarCommands.View.SHOW_OUTLINE);
      IDE.OUTLINE.waitOpened();
      IDE.OUTLINE.waitOutlineTreeVisible();

      checkTreeCorrectlyCreatedAfterCollapseAllNodes();
   }

   private void checkTreeCorrectlyCreatedAfterCollapseAllNodes() throws Exception
   {
      //check html node
      IDE.GOTOLINE.goToLine(2);
      IDE.OUTLINE.waitItemAtPosition("body", 3);

      IDE.GOTOLINE.goToLine(3);
      IDE.OUTLINE.waitItemAtPosition("style", 7);

      IDE.GOTOLINE.goToLine(14);
      IDE.OUTLINE.waitItemAtPosition("style", 7);

      IDE.GOTOLINE.goToLine(15);
      IDE.OUTLINE.waitItemAtPosition("table", 9);

      IDE.GOTOLINE.goToLine(16);
      IDE.OUTLINE.waitItemAtPosition("td", 12);

      IDE.GOTOLINE.goToLine(20);
      IDE.OUTLINE.waitItemAtPosition("td", 17);

      IDE.GOTOLINE.goToLine(32);
      IDE.OUTLINE.waitItemAtPosition("today : Date", 23);

      //sheck key nodes 
      assertEquals(IDE.OUTLINE.getItemLabel(1), "html");
      assertEquals(IDE.OUTLINE.getItemLabel(2), "head");
      assertEquals(IDE.OUTLINE.getItemLabel(3), "meta");
      assertEquals(IDE.OUTLINE.getItemLabel(4), "link");
      assertEquals(IDE.OUTLINE.getItemLabel(5), "title");
      assertEquals(IDE.OUTLINE.getItemLabel(6), "script");
      assertEquals(IDE.OUTLINE.getItemLabel(7), "style");
      assertEquals(IDE.OUTLINE.getItemLabel(8), "body");
      assertEquals(IDE.OUTLINE.getItemLabel(9), "table");
      assertEquals(IDE.OUTLINE.getItemLabel(10), "thead");
      assertEquals(IDE.OUTLINE.getItemLabel(11), "tr");
      assertEquals(IDE.OUTLINE.getItemLabel(12), "td");
      assertEquals(IDE.OUTLINE.getItemLabel(13), "tbody");
      assertEquals(IDE.OUTLINE.getItemLabel(14), "tr");
      assertEquals(IDE.OUTLINE.getItemLabel(15), "td");
      assertEquals(IDE.OUTLINE.getItemLabel(16), "td");
      assertEquals(IDE.OUTLINE.getItemLabel(17), "td");
      assertEquals(IDE.OUTLINE.getItemLabel(18), "br");
      assertEquals(IDE.OUTLINE.getItemLabel(19), "br");
      assertEquals(IDE.OUTLINE.getItemLabel(20), "script");
      assertEquals(IDE.OUTLINE.getItemLabel(21), "prefs : gadgets.Prefs");
      assertEquals(IDE.OUTLINE.getItemLabel(22), "displayGreeting()");
      assertEquals(IDE.OUTLINE.getItemLabel(23), "today : Date");
      assertEquals(IDE.OUTLINE.getItemLabel(24), "html : String");
   }

   private void checkNavigateOnOutlineTree() throws Exception
   {
      IDE.OUTLINE.selectItem("script");
      IDE.STATUSBAR.waitCursorPositionAt("27 : 1");

      IDE.OUTLINE.selectItem("table");
      IDE.STATUSBAR.waitCursorPositionAt("14 : 1");

      IDE.OUTLINE.selectItem("head");
      IDE.STATUSBAR.waitCursorPositionAt("2 : 1");

      IDE.OUTLINE.selectItem("style");
      IDE.STATUSBAR.waitCursorPositionAt("36 : 1");

   }

}

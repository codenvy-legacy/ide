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

import static org.junit.Assert.assertTrue;

import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.ToolbarCommands;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.exoplatform.ide.vfs.shared.Link;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.Map;

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
      IDE.EDITOR.waitActiveFile(PROJECT + "/" + FILE_NAME);

      IDE.TOOLBAR.runCommand(ToolbarCommands.View.SHOW_OUTLINE);
      IDE.OUTLINE.waitOpened();
      IDE.OUTLINE.waitOutlineTreeVisible();

      checkTreeCorrectlyCreated();
   }

   private void checkTreeCorrectlyCreated() throws Exception
   {
      //check html node
      assertTrue(IDE.OUTLINE.isItemPresentById("html:TAG:1"));

      //check head tag and subnodes head
      assertTrue(IDE.OUTLINE.isItemPresentById("head:TAG:2"));
      assertTrue(IDE.OUTLINE.isItemPresentById("meta:TAG:3"));
      assertTrue(IDE.OUTLINE.isItemPresentById("link:TAG:4"));
      assertTrue(IDE.OUTLINE.isItemPresentById("title:TAG:5"));
      assertTrue(IDE.OUTLINE.isItemPresentById("script:TAG:6"));
      assertTrue(IDE.OUTLINE.isItemPresentById("style:TAG:7"));

      //check body tag and subnodes body
      assertTrue(IDE.OUTLINE.isItemPresentById("body:TAG:13"));

      //check table tag and subnodes table
      assertTrue(IDE.OUTLINE.isItemPresentById("table:TAG:14"));
      assertTrue(IDE.OUTLINE.isItemPresentById("thead:TAG:15"));
      assertTrue(IDE.OUTLINE.isItemPresentById("tr:TAG:16"));
      assertTrue(IDE.OUTLINE.isItemPresentById("td:TAG:16"));

      //check tbody tag and subnodes tbody
      assertTrue(IDE.OUTLINE.isItemPresentById("tbody:TAG:18"));
      assertTrue(IDE.OUTLINE.isItemPresentById("tr:TAG:19"));
      assertTrue(IDE.OUTLINE.isItemPresentById("td:TAG:20"));
      assertTrue(IDE.OUTLINE.isItemPresentById("td:TAG:21"));
      assertTrue(IDE.OUTLINE.isItemPresentById("td:TAG:22"));
      assertTrue(IDE.OUTLINE.isItemPresentById("br:TAG:25"));
      assertTrue(IDE.OUTLINE.isItemPresentById("br:TAG:26"));

      //check script tag and subnodes script
      assertTrue(IDE.OUTLINE.isItemPresentById("script:TAG:27"));
      assertTrue(IDE.OUTLINE.isItemPresentById("prefs:VARIABLE:28"));
      assertTrue(IDE.OUTLINE.isItemPresentById("displayGreeting:FUNCTION:30"));
      assertTrue(IDE.OUTLINE.isItemPresentById("today:VARIABLE:31"));
      assertTrue(IDE.OUTLINE.isItemPresentById("html:VARIABLE:33"));

      //check style tag
      assertTrue(IDE.OUTLINE.isItemPresentById("style:TAG:36"));

      //check tr and subnodes tag
      assertTrue(IDE.OUTLINE.isItemPresentById("tr:TAG:45"));
      assertTrue(IDE.OUTLINE.isItemPresentById("td:TAG:46"));
      assertTrue(IDE.OUTLINE.isItemPresentById("td:TAG:47"));

      //check tr and subnodes tag
      assertTrue(IDE.OUTLINE.isItemPresentById("tr:TAG:49"));
      assertTrue(IDE.OUTLINE.isItemPresentById("td:TAG:50"));
      assertTrue(IDE.OUTLINE.isItemPresentById("td:TAG:51"));
   }

}

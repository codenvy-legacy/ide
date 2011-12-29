/*
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
 * Created by The eXo Platform SAS.
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id:
 *
 */
public class CodeOutlineGoogleGadgetTest extends BaseTest
{
   private final static String FILE_NAME = "GoogleGadgetCodeOutline.gadget";

   private final static String PROJECT = CodeOutlineGoogleGadgetTest.class.getSimpleName();

   @BeforeClass
   public static void setUp()
   {

      String filePath = "src/test/resources/org/exoplatform/ide/operation/edit/outline/GoogleGadgetCodeOutline.xml";
      try
      {
         Map<String, Link> project = VirtualFileSystemUtils.createDefaultProject(PROJECT);
         Link link = project.get(Link.REL_CREATE_FILE);
         VirtualFileSystemUtils.createFileFromLocal(link, FILE_NAME, MimeType.GOOGLE_GADGET, filePath);
      }
      catch (IOException e)
      {
         e.printStackTrace();
      }
   }

   //IDE-173 : Google Gadget Code Outline
   @Test
   public void testCodeOutlineGoogleGadget() throws Exception
   {
      IDE.PROJECT.EXPLORER.waitOpened();
      IDE.PROJECT.OPEN.openProject(PROJECT);
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + FILE_NAME);
      IDE.LOADER.waitClosed();

      IDE.PROJECT.EXPLORER.openItem(PROJECT + "/" + FILE_NAME);
      IDE.EDITOR.waitActiveFile(PROJECT + "/" + FILE_NAME);

      IDE.TOOLBAR.runCommand(ToolbarCommands.View.SHOW_OUTLINE);
      IDE.OUTLINE.waitOpened();

      checkTreeCorrectlyCreated();
   }

   private void checkTreeCorrectlyCreated() throws Exception
   {
      //check module tag and subnode
      assertTrue(IDE.OUTLINE.isItemPresentById("Module:TAG:2"));
      assertTrue(IDE.OUTLINE.isItemPresentById("ModulePrefs:TAG:3"));

      assertTrue(IDE.OUTLINE.isItemPresentById("Content:TAG:4"));
      assertTrue(IDE.OUTLINE.isItemPresentById("CDATA:CDATA:5"));
      assertTrue(IDE.OUTLINE.isItemPresentById("html:TAG:6"));

      //check head tag and subnodes
      assertTrue(IDE.OUTLINE.isItemPresentById("head:TAG:7"));
      assertTrue(IDE.OUTLINE.isItemPresentById("meta:TAG:8"));
      assertTrue(IDE.OUTLINE.isItemPresentById("link:TAG:9"));
      assertTrue(IDE.OUTLINE.isItemPresentById("title:TAG:10"));

      //check script tag and subnodes
      assertTrue(IDE.OUTLINE.isItemPresentById("script:TAG:11"));
      assertTrue(IDE.OUTLINE.isItemPresentById("a:VARIABLE:12"));

      assertTrue(IDE.OUTLINE.isItemPresentById("style:TAG:14"));

      //check body tag and subnodes
      assertTrue(IDE.OUTLINE.isItemPresentById("body:TAG:20"));
      assertTrue(IDE.OUTLINE.isItemPresentById("table:TAG:21"));
      assertTrue(IDE.OUTLINE.isItemPresentById("thead:TAG:22"));
      assertTrue(IDE.OUTLINE.isItemPresentById("tr:TAG:23"));
      assertTrue(IDE.OUTLINE.isItemPresentById("td:TAG:23"));

      //check tbody tag and subnodes
      assertTrue(IDE.OUTLINE.isItemPresentById("tbody:TAG:25"));
      assertTrue(IDE.OUTLINE.isItemPresentById("tr:TAG:26"));
      assertTrue(IDE.OUTLINE.isItemPresentById("td:TAG:27"));
      assertTrue(IDE.OUTLINE.isItemPresentById("td:TAG:28"));
      assertTrue(IDE.OUTLINE.isItemPresentById("td:TAG:29"));
      assertTrue(IDE.OUTLINE.isItemPresentById("br:TAG:32"));
      assertTrue(IDE.OUTLINE.isItemPresentById("br:TAG:33"));
      assertTrue(IDE.OUTLINE.isItemPresentById("style:TAG:34"));
      assertTrue(IDE.OUTLINE.isItemPresentById("script:TAG:40"));
      assertTrue(IDE.OUTLINE.isItemPresentById("b:VARIABLE:41"));
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
         e.printStackTrace();
      }
   }
}
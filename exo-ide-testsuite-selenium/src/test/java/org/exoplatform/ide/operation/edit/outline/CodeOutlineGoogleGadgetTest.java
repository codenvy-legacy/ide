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
import static org.junit.Assert.assertEquals;

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
      //check first node after open
      assertEquals("Module", IDE.OUTLINE.getItemLabel(1));
      //expand first node and check

      IDE.OUTLINE.expandSelectItem(1);

      assertEquals("ModulePrefs", IDE.OUTLINE.getItemLabel(2));
      assertEquals("Content", IDE.OUTLINE.getItemLabel(3));
      //expand third node and check 
      IDE.OUTLINE.selectRow(3);
      IDE.OUTLINE.expandSelectItem(3);
      assertEquals("CDATA", IDE.OUTLINE.getItemLabel(4));

      //expand 4 node and check 
      IDE.OUTLINE.selectRow(4);
      IDE.OUTLINE.expandSelectItem(4);
      assertEquals("html", IDE.OUTLINE.getItemLabel(5));

      //expand 5 node and check
      IDE.OUTLINE.selectRow(5);
      IDE.OUTLINE.expandSelectItem(5);
      assertEquals("head", IDE.OUTLINE.getItemLabel(6));
      assertEquals("body", IDE.OUTLINE.getItemLabel(7));

      //expand 5 node and check
      IDE.OUTLINE.selectRow(6);
      IDE.OUTLINE.expandSelectItem(6);
      assertEquals("meta", IDE.OUTLINE.getItemLabel(7));
      assertEquals("link", IDE.OUTLINE.getItemLabel(8));
      assertEquals("title", IDE.OUTLINE.getItemLabel(9));
      assertEquals("script", IDE.OUTLINE.getItemLabel(10));
      assertEquals("style", IDE.OUTLINE.getItemLabel(11));
      assertEquals("body", IDE.OUTLINE.getItemLabel(12));

      //expand 10 (script) node and check
      IDE.OUTLINE.selectRow(10);
      IDE.OUTLINE.expandSelectItem(10);
      assertEquals("a", IDE.OUTLINE.getItemLabel(11));

      //expand 13 (body) node and check
      IDE.OUTLINE.selectRow(13);
      IDE.OUTLINE.expandSelectItem(13);
      assertEquals("table", IDE.OUTLINE.getItemLabel(14));
      assertEquals("br", IDE.OUTLINE.getItemLabel(15));
      assertEquals("br", IDE.OUTLINE.getItemLabel(16));
      assertEquals("style", IDE.OUTLINE.getItemLabel(17));
      assertEquals("script", IDE.OUTLINE.getItemLabel(18));

      //go to line and check item in Outline tree
      IDE.GOTOLINE.goToLine(41);
      assertEquals("b", IDE.OUTLINE.getItemLabel(19));

      //go to line and check item in Outline tree
      IDE.GOTOLINE.goToLine(41);
      assertEquals("b", IDE.OUTLINE.getItemLabel(19));

      IDE.OUTLINE.selectRow(14);
      IDE.OUTLINE.expandSelectItem(14);
      assertEquals("thead", IDE.OUTLINE.getItemLabel(15));
      assertEquals("tbody", IDE.OUTLINE.getItemLabel(16));

      IDE.OUTLINE.selectRow(15);
      assertEquals("22 : 1", IDE.STATUSBAR.getCursorPosition());

      IDE.OUTLINE.selectRow(16);
      assertEquals("25 : 1", IDE.STATUSBAR.getCursorPosition());

      IDE.GOTOLINE.goToLine(23);
      assertEquals("tr", IDE.OUTLINE.getItemLabel(16));
      assertEquals("td", IDE.OUTLINE.getItemLabel(17));

      IDE.GOTOLINE.goToLine(27);
      assertEquals("tr", IDE.OUTLINE.getItemLabel(19));
      assertEquals("td", IDE.OUTLINE.getItemLabel(20));
      assertEquals("td", IDE.OUTLINE.getItemLabel(21));
      assertEquals("td", IDE.OUTLINE.getItemLabel(22));

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
}
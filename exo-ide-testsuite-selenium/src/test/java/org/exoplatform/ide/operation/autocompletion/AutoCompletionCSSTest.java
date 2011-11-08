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
package org.exoplatform.ide.operation.autocompletion;

import static org.junit.Assert.assertTrue;

import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.MenuCommands;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.exoplatform.ide.vfs.shared.Link;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.Keys;

import java.io.IOException;
import java.util.Map;

/**
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: $
 *
 */
public class AutoCompletionCSSTest extends BaseTest
{
   
   
   private static Map<String, Link> project;

   @BeforeClass
   public static void createProject()
   {
      try
      {
         project = VirtualFileSystemUtils.createDefaultProject(AutoCompletionCSSTest.class.getSimpleName());
      }
      catch (IOException e)
      {
         e.printStackTrace();
      }
   }
   
   @AfterClass
   public static void deleteProject() throws IOException
   {
      VirtualFileSystemUtils.deleteFolder(project.get(Link.REL_DELETE));
   }

   @Test
   public void testPlainCSS() throws Exception
   {
      IDE.PROJECT.EXPLORER.waitOpened();
      IDE.PROJECT.OPEN.openProject(AutoCompletionCSSTest.class.getSimpleName());
      IDE.TOOLBAR.runCommandFromNewPopupMenu(MenuCommands.New.CSS_FILE);
      IDE.EDITOR.waitActiveFile(AutoCompletionCSSTest.class.getSimpleName() + "/Untitled file.css");
      cssTest();
   }

   @Test
   public void testGoogleGadget() throws Exception
   {
      selenium.refresh();
      IDE.PROJECT.EXPLORER.waitOpened();
      IDE.PROJECT.OPEN.openProject(AutoCompletionCSSTest.class.getSimpleName());
      IDE.TOOLBAR.runCommandFromNewPopupMenu(MenuCommands.New.GOOGLE_GADGET_FILE);
      IDE.EDITOR.waitActiveFile(AutoCompletionCSSTest.class.getSimpleName() + "/Untitled file.xml");
      IDE.EDITOR.moveCursorDown(0, 4);
      IDE.EDITOR.moveCursorRight(0, 16);
      IDE.EDITOR.typeTextIntoEditor(0, Keys.RETURN + "<style>\n\n\n</style>");
      IDE.EDITOR.moveCursorUp(0, 1);

      cssTest();
   }

   @Test
   public void testHTML() throws Exception
   {
      selenium().refresh();
      IDE.PROJECT.EXPLORER.waitOpened();
      IDE.PROJECT.OPEN.openProject(AutoCompletionCSSTest.class.getSimpleName());
      IDE.TOOLBAR.runCommandFromNewPopupMenu(MenuCommands.New.HTML_FILE);
      IDE.EDITOR.waitActiveFile(AutoCompletionCSSTest.class.getSimpleName() + "/Untitled file.html");
      IDE.EDITOR.moveCursorDown(0, 2);
      IDE.EDITOR.typeTextIntoEditor(0, Keys.END.toString() + Keys.ENTER);
      IDE.EDITOR.typeTextIntoEditor(0, "<script>\n</script>");

      IDE.EDITOR.moveCursorDown(0, 2);
      IDE.EDITOR.typeTextIntoEditor(0, Keys.END.toString() + Keys.ENTER);
      IDE.EDITOR.typeTextIntoEditor(0, "<style>\n\n</style>");
      IDE.EDITOR.moveCursorUp(0, 1);

      cssTest();
   }

   private void cssTest() throws Exception
   {
      IDE.EDITOR.typeTextIntoEditor(0, ".main{" + Keys.ENTER.toString());
      IDE.CODEASSISTANT.openForm();
      IDE.CODEASSISTANT.typeToInput("list-st");
      IDE.CODEASSISTANT.moveCursorDown(3);
      IDE.CODEASSISTANT.insertSelectedItem();
      String text = IDE.EDITOR.getTextFromCodeEditor(0);
      assertTrue(text.contains("list-style-type:"));
   }
}

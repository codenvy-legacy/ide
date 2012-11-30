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
package org.exoplatform.ide.operation.edit;

import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.ToolbarCommands;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.exoplatform.ide.vfs.shared.Link;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.Keys;

import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

/**
 * Created by The eXo Platform SAS.
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id:
 *
 */
public class UndoRedoEditingInWysiwygEditorTest extends BaseTest
{

   //IDE-122 Undo/Redo Editing in WYSIWYG editor 

   private final static String PROJECT = UndoRedoEditingInWysiwygEditorTest.class.getSimpleName();

   private final static String HTML_FILE = "EditFileInWysiwygEditor.html";

   private final static String GOOGLE_GADGET = "GoogleGadget.xml";

   @BeforeClass
   public static void setUp()
   {
      String htmlPath = "src/test/resources/org/exoplatform/ide/operation/edit/" + HTML_FILE;
      String gadgetPath = "src/test/resources/org/exoplatform/ide/miscellaneous/" + GOOGLE_GADGET;

      try
      {
         Map<String, Link> project = VirtualFileSystemUtils.createDefaultProject(PROJECT);
         Link link = project.get(Link.REL_CREATE_FILE);
         VirtualFileSystemUtils.createFileFromLocal(link, HTML_FILE, MimeType.TEXT_HTML, htmlPath);
         VirtualFileSystemUtils.createFileFromLocal(link, GOOGLE_GADGET, MimeType.GOOGLE_GADGET, gadgetPath);
      }
      catch (Exception e)
      {
      }
   }

   @After
   public void closeFile()
   {
      try
      {
         IDE.EDITOR.forcedClosureFile(1);
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
   public void undoRedoEditingInWysiwydEditorFromEditMenu() throws Exception
   {

      //step 1 open project and walidation error marks 
      IDE.PROJECT.EXPLORER.waitOpened();
      IDE.PROJECT.OPEN.openProject(PROJECT);
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + HTML_FILE);
      IDE.PROJECT.EXPLORER.openItem(PROJECT + "/" + HTML_FILE);
      IDE.EDITOR.waitActiveFile(PROJECT + "/" + HTML_FILE);

      IDE.EDITOR.clickDesignButton();
      IDE.CK_EDITOR.typeTextIntoCkEditor(1, "1,");
      //delay for emulation of the user input
      Thread.sleep(1000);
      IDE.CK_EDITOR.typeTextIntoCkEditor(1, "2,");
      //delay for emulation of the user input
      Thread.sleep(1000);
      IDE.CK_EDITOR.typeTextIntoCkEditor(1, "3");
      //delay for emulation of the user input
      Thread.sleep(1000);
      assertEquals("1," + "2," + "3", IDE.CK_EDITOR.getTextFromCKEditor(1));

      IDE.TOOLBAR.runCommand(ToolbarCommands.Editor.UNDO);
      assertEquals(IDE.CK_EDITOR.getTextFromCKEditor(1), "1");
      IDE.TOOLBAR.runCommand(ToolbarCommands.Editor.UNDO);
      assertEquals(IDE.CK_EDITOR.getTextFromCKEditor(1), "");

      IDE.TOOLBAR.runCommand(ToolbarCommands.Editor.REDO);
      assertEquals(IDE.CK_EDITOR.getTextFromCKEditor(1), "1");

      IDE.TOOLBAR.runCommand(ToolbarCommands.Editor.REDO);
      assertEquals(IDE.CK_EDITOR.getTextFromCKEditor(1), "1,2,3");

   }

   @Test
   public void undoRedoEditingFromShortKeys() throws Exception
   {

      //step 1 open project and walidation error marks 
      driver.navigate().refresh();
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + HTML_FILE);
      IDE.PROJECT.EXPLORER.openItem(PROJECT + "/" + HTML_FILE);
      IDE.EDITOR.waitActiveFile(PROJECT + "/" + HTML_FILE);

      IDE.EDITOR.clickDesignButton();

      IDE.CK_EDITOR.typeTextIntoCkEditor(1, "1,");
      //delay for emulation of the user input
      Thread.sleep(500);
      IDE.CK_EDITOR.typeTextIntoCkEditor(1, "2,");
      //delay for emulation of the user input
      Thread.sleep(500);
      IDE.CK_EDITOR.typeTextIntoCkEditor(1, "3");
      //delay for emulation of the user input
      Thread.sleep(500);

      assertEquals("1," + "2," + "3", IDE.CK_EDITOR.getTextFromCKEditor(1));

      IDE.CK_EDITOR.typeTextIntoCkEditor(1, Keys.CONTROL.toString() + "z");
      assertEquals(IDE.CK_EDITOR.getTextFromCKEditor(1), "1");
      IDE.CK_EDITOR.typeTextIntoCkEditor(1, Keys.CONTROL.toString() + "z");

      IDE.CK_EDITOR.typeTextIntoCkEditor(1, Keys.CONTROL.toString() + "z");
      assertEquals(IDE.CK_EDITOR.getTextFromCKEditor(1), "");

      IDE.CK_EDITOR.typeTextIntoCkEditor(1, Keys.CONTROL.toString() + "y");
      assertEquals(IDE.CK_EDITOR.getTextFromCKEditor(1), "1");

      IDE.CK_EDITOR.typeTextIntoCkEditor(1, Keys.CONTROL.toString() + "y");
      assertEquals(IDE.CK_EDITOR.getTextFromCKEditor(1), "1,2,3");

   }

   /**
    * @throws Exception
    */
   private void checkNoFileOpened() throws Exception
   {
      String divIndex = "0";

      //check Code Editor is not present in tab 0
      assertFalse(selenium().isElementPresent(
         "//div[@panel-id='editor'and @tab-index=" + "'" + divIndex + "'" + "]"
            + "//div[@class='CodeMirror-wrapping']/iframe"));
      //check CK editor is not present in tab 0
      assertFalse(selenium().isElementPresent(
         "//div[@panel-id='editor'and @tab-index=" + "'" + divIndex + "'" + "]"
            + "//div[@class='CodeMirror-wrapping']/iframe"));
   }

}

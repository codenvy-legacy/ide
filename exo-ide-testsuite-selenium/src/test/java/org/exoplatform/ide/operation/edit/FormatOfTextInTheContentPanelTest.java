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

import static org.junit.Assert.assertEquals;

import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.MenuCommands;
import org.exoplatform.ide.Utils;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.exoplatform.ide.vfs.shared.Link;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Map;

/**
 * Created by The eXo Platform SAS.
 * @author <a href="mailto:vitaly.parfonov@gmail.com">Vitaly Parfonov</a>
 * @version $Id: $
*/

//IDE-58:Format of text in the Content panel

public class FormatOfTextInTheContentPanelTest extends BaseTest
{
   private static String PROJECT = FormatOfTextInTheContentPanelTest.class.getSimpleName();

   private static String FORMAT_HTML_FILE_NAME = "formating.html";

   private static String NON_FORMAT_HTML_FILE_NAME = "non-formating.html";

   private static String FORMAT_CSS_FILE_NAME = "formating.css";

   private static String NON_FORMAT_CSS_FILE_NAME = "non-formating.css";

   private static String FORMAT_JS_FILE_NAME = "formating.js";

   private static String NON_FORMAT_JS_FILE_NAME = "non-formating.js";

   private static String FORMAT_GADGET_FILE_NAME = "formating.gadget";

   private static String NON_FORMAT_GADGET_FILE_NAME = "non-formating.gadget";

   private final static String PATH = "src/test/resources/org/exoplatform/ide/operation/file/formating/";

   @BeforeClass
   public static void setUp()
   {
      try
      {
         Map<String, Link> project = VirtualFileSystemUtils.createDefaultProject(PROJECT);
         Link link = project.get(Link.REL_CREATE_FILE);
         VirtualFileSystemUtils.createFileFromLocal(link, NON_FORMAT_HTML_FILE_NAME, MimeType.TEXT_HTML, PATH
            + NON_FORMAT_HTML_FILE_NAME);
         VirtualFileSystemUtils.createFileFromLocal(link, NON_FORMAT_CSS_FILE_NAME, MimeType.TEXT_CSS, PATH
            + NON_FORMAT_CSS_FILE_NAME);
         VirtualFileSystemUtils.createFileFromLocal(link, NON_FORMAT_JS_FILE_NAME, MimeType.APPLICATION_JAVASCRIPT,
            PATH + NON_FORMAT_JS_FILE_NAME);
         VirtualFileSystemUtils.createFileFromLocal(link, NON_FORMAT_GADGET_FILE_NAME, MimeType.GOOGLE_GADGET, PATH
            + NON_FORMAT_GADGET_FILE_NAME);
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
   public void testFormatingHtml() throws Exception
   {
      IDE.PROJECT.EXPLORER.waitOpened();
      IDE.PROJECT.OPEN.openProject(PROJECT);
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + NON_FORMAT_HTML_FILE_NAME);

      IDE.PROJECT.EXPLORER.openItem(PROJECT + "/" + NON_FORMAT_HTML_FILE_NAME);
      IDE.EDITOR.waitActiveFile(PROJECT + "/" + NON_FORMAT_HTML_FILE_NAME);

      IDE.MENU.runCommand(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.FORMAT);

      String postFormating = IDE.EDITOR.getTextFromCodeEditor(0);
      String formatingSource = Utils.readFileAsString(PATH + FORMAT_HTML_FILE_NAME);
      IDE.EDITOR.closeTabIgnoringChanges(1);
      assertEquals(formatingSource, postFormating);
   }

   @Test
   public void testFormatingCss() throws Exception
   {
      driver.navigate().refresh();

      IDE.PROJECT.EXPLORER.waitOpened();
      IDE.PROJECT.OPEN.openProject(PROJECT);
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + NON_FORMAT_CSS_FILE_NAME);

      IDE.PROJECT.EXPLORER.openItem(PROJECT + "/" + NON_FORMAT_CSS_FILE_NAME);
      IDE.EDITOR.waitActiveFile(PROJECT + "/" + NON_FORMAT_CSS_FILE_NAME);

      IDE.MENU.runCommand(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.FORMAT);

      String postFormating = IDE.EDITOR.getTextFromCodeEditor(0);
      String formatingSource = Utils.readFileAsString(PATH + FORMAT_CSS_FILE_NAME);
      IDE.EDITOR.closeTabIgnoringChanges(1);
      assertEquals(formatingSource, postFormating);
   }

   @Test
   public void testFormatingJS() throws Exception
   {
      driver.navigate().refresh();

      IDE.PROJECT.EXPLORER.waitOpened();
      IDE.PROJECT.OPEN.openProject(PROJECT);
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + NON_FORMAT_JS_FILE_NAME);

      IDE.PROJECT.EXPLORER.openItem(PROJECT + "/" + NON_FORMAT_JS_FILE_NAME);
      IDE.EDITOR.waitActiveFile(PROJECT + "/" + NON_FORMAT_JS_FILE_NAME);

      IDE.MENU.runCommand(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.FORMAT);

      String postFormating = IDE.EDITOR.getTextFromCodeEditor(0);
      String formatingSource = Utils.readFileAsString(PATH + FORMAT_JS_FILE_NAME);
      //IDE.EDITOR.closeUnsavedFileAndDoNotSave(0);
      IDE.EDITOR.closeTabIgnoringChanges(1);
      assertEquals(formatingSource, postFormating);

   }

   @Test
   public void testFormatingGadget() throws Exception
   {
      driver.navigate().refresh();

      IDE.PROJECT.EXPLORER.waitOpened();
      IDE.PROJECT.OPEN.openProject(PROJECT);
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + NON_FORMAT_GADGET_FILE_NAME);

      IDE.PROJECT.EXPLORER.openItem(PROJECT + "/" + NON_FORMAT_GADGET_FILE_NAME);
      IDE.EDITOR.waitActiveFile(PROJECT + "/" + NON_FORMAT_GADGET_FILE_NAME);

      IDE.MENU.runCommand(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.FORMAT);

      String postFormating = IDE.EDITOR.getTextFromCodeEditor(0);
      String formatingSource = Utils.readFileAsString(PATH + FORMAT_GADGET_FILE_NAME);
      IDE.EDITOR.closeTabIgnoringChanges(1);
      assertEquals(formatingSource, postFormating);
   }
}

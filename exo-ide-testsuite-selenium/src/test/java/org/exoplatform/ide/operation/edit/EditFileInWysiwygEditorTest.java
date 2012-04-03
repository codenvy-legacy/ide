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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.MenuCommands;
import org.exoplatform.ide.TestConstants;
import org.exoplatform.ide.ToolbarCommands;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.exoplatform.ide.vfs.shared.Link;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.Map;

/**
 * Created by The eXo Platform SAS.
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @author <a href="dnochevnov@exoplatform.com">Dmytro Nochevnov</a> 
 * @version $Id:
 *
 */
public class EditFileInWysiwygEditorTest extends BaseTest
{

   //IDE-123 Edit file in WYSIWYG editor

   private final static String FILE_NAME = "EditFileInWysiwygEditor.html";

   private final static String PROJECT = EditFileInWysiwygEditorTest.class.getSimpleName();

   private final static String URL = BASE_URL + REST_CONTEXT + "/" + WEBDAV_CONTEXT + "/" + REPO_NAME + "/" + WS_NAME
      + "/" + PROJECT + "/";

   private final static String IDE_URL = BASE_URL + "IDE/" + REST_CONTEXT + "/" + WEBDAV_CONTEXT + "/" + REPO_NAME
      + "/" + WS_NAME + "/" + PROJECT + "/";

   private final static String DEFAULT_TEXT_MOZILLA =
      "<html webdriver=\"true\">\n <head>\n  <title></title>\n </head>\n <body>\n  <br />\n </body>\n</html>";

   private final static String DEFAULT_TEXT_CHROME =
      "<html>\n <head>\n  <title></title>\n </head>\n <body>\n  <br />\n </body>\n</html>";

   private final static String WARNINNG_MASSAGE = "Table height must be a number.";

   private final static String CODE_AFTER_MODIFED_TABLE_MOZILLA = "<html webdriver=\"true\">\n" + " <head>\n"
      + "  <title></title>\n" + " </head>\n" + " <body>\n"
      + "  <table border=\"1\" cellpadding=\"1\" cellspacing=\"1\" style=\"height: 50px; width: 200px;\">\n"
      + "   <tbody>\n" + "    <tr>\n" + "     <td>\n" + "      &nbsp;</td>\n" + "     <td>\n" + "      &nbsp;</td>\n"
      + "    </tr>\n" + "    <tr>\n" + "     <td>\n" + "      &nbsp;</td>\n" + "     <td>\n" + "      &nbsp;</td>\n"
      + "    </tr>\n" + "    <tr>\n" + "     <td>\n" + "      &nbsp;</td>\n" + "     <td>\n" + "      &nbsp;</td>\n"
      + "    </tr>\n" + "    <tr>\n" + "     <td>\n" + "      &nbsp;</td>\n" + "     <td>\n" + "      &nbsp;</td>\n"
      + "    </tr>\n" + "   </tbody>\n" + "  </table>\n" + "  <br />\n" + " </body>\n" + "</html>";

   private final static String CODE_AFTER_MODIFED_TABLE = "<html>\n" + " <head>\n" + "  <title></title>\n"
      + " </head>\n" + " <body>\n"
      + "  <table border=\"1\" cellpadding=\"1\" cellspacing=\"1\" style=\"height: 50px; width: 200px;\">\n"
      + "   <tbody>\n" + "    <tr>\n" + "     <td>\n" + "      &nbsp;</td>\n" + "     <td>\n" + "      &nbsp;</td>\n"
      + "    </tr>\n" + "    <tr>\n" + "     <td>\n" + "      &nbsp;</td>\n" + "     <td>\n" + "      &nbsp;</td>\n"
      + "    </tr>\n" + "    <tr>\n" + "     <td>\n" + "      &nbsp;</td>\n" + "     <td>\n" + "      &nbsp;</td>\n"
      + "    </tr>\n" + "    <tr>\n" + "     <td>\n" + "      &nbsp;</td>\n" + "     <td>\n" + "      &nbsp;</td>\n"
      + "    </tr>\n" + "   </tbody>\n" + "  </table>\n" + "  <br />\n" + " </body>\n" + "</html>";

   @Before
   public void setUp()
   {
      String filePath = "src/test/resources/org/exoplatform/ide/operation/edit/" + FILE_NAME;
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

   @Test
   public void editFileInWysiwygEditor() throws Exception
   {

      //step one open project switch between codeeditor and ck-editor. Check kontent
      IDE.PROJECT.EXPLORER.waitOpened();
      IDE.PROJECT.OPEN.openProject(PROJECT);
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + FILE_NAME);
      IDE.LOADER.waitClosed();
      IDE.WELCOME_PAGE.close();

      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + FILE_NAME);
      IDE.PROJECT.EXPLORER.openItem(PROJECT + "/" + FILE_NAME);
      IDE.EDITOR.waitActiveFile(PROJECT + "/" + FILE_NAME);
      IDE.EDITOR.clickDesignButton();
      assertEquals(FILE_NAME, IDE.EDITOR.getTabTitle(0).replace(" *", ""));
      IDE.CK_EDITOR.waitToolsCkEditor(1);
      IDE.EDITOR.clickSourceButton();
      IDE.EDITOR.waitSwitchOnCodeEditor();
      IDE.selectMainFrame();

      if (IDE_SETTINGS.getString("selenium.browser.commad").equals("GOOGLE_CHROME"))
      {
         assertEquals(DEFAULT_TEXT_CHROME, IDE.EDITOR.getTextFromCodeEditor(0));
      }
      else
      {
         assertEquals(DEFAULT_TEXT_MOZILLA, IDE.EDITOR.getTextFromCodeEditor(0));
      }
      //step 2 return to ck-editor. Click on table icon set wrong value into height field, check warning message
      IDE.EDITOR.clickDesignButton();
      IDE.CK_EDITOR.waitToolsCkEditor(1);
      IDE.CK_EDITOR.clickOnToolCkEditor("Table");
      IDE.CK_EDITOR.waitCkEditorTableOpen();
      IDE.CK_EDITOR.typeToHeightwisiwyngtable("qwe");
      IDE.CK_EDITOR.clickOkWyswygTable();
      IDE.WARNING_DIALOG.waitOpened();
      assertEquals(IDE.WARNING_DIALOG.getWarningMessage(), WARNINNG_MASSAGE);
      IDE.WARNING_DIALOG.clickOk();
      IDE.WARNING_DIALOG.waitClosed();
      IDE.CK_EDITOR.waitCkEditorTableOpen();
      IDE.CK_EDITOR.typeToHeightwisiwyngtable("50");

      //step 3 Create default table and check
      IDE.CK_EDITOR.clickOkWyswygTable();
      IDE.CK_EDITOR.switchToCkEditorIframe();
      isDefaultTableCreated();

      //step 4 Add row in table and check
      IDE.CK_EDITOR.clickOnCellTableCkEditor(3, 1);
      IDE.CK_EDITOR.moveCursorUp();
      IDE.CK_EDITOR.callContextMenuCellTableCkEditor(3, 1);
      IDE.selectMainFrame();
      IDE.CK_EDITOR.switchToContextMenuIframe();
      IDE.CK_EDITOR.moveCursorToRowContextMenu("Row");
      IDE.selectMainFrame();
      IDE.CK_EDITOR.switchToContextSubMenuIframe();
      IDE.CK_EDITOR.clickOnContextSubMenu("Insert Row After");
      IDE.selectMainFrame();
      IDE.CK_EDITOR.switchToCkEditorIframe();
      isInsertTableCreated();

      //step 4 select codeeditor and check html code
      IDE.selectMainFrame();
      IDE.EDITOR.clickSourceButton();

      if (IDE_SETTINGS.getString("selenium.browser.commad").equals("GOOGLE_CHROME"))
      {
         assertEquals(CODE_AFTER_MODIFED_TABLE, IDE.EDITOR.getTextFromCodeEditor(0));
      }
      else
      {
         assertEquals(CODE_AFTER_MODIFED_TABLE_MOZILLA, IDE.EDITOR.getTextFromCodeEditor(0));
      }
     
      //step 5 save the file and check table in preview panel 
      IDE.TOOLBAR.runCommand(ToolbarCommands.File.SAVE);
      IDE.LOADER.waitClosed();
      IDE.TOOLBAR.waitForButtonEnabled(ToolbarCommands.Run.SHOW_PREVIEW, true);

      IDE.TOOLBAR.runCommand(ToolbarCommands.Run.SHOW_PREVIEW);
      IDE.PREVIEW.waitHtmlPreviewOpened();
      IDE.PREVIEW.selectPreviewHtmlIFrame();
      isInsertTableCreated();
   }

   private void isDefaultTableCreated()
   {
      assertTrue(IDE.CK_EDITOR.isTablePresent(1, 1));
      assertTrue(IDE.CK_EDITOR.isTablePresent(1, 2));
      assertTrue(IDE.CK_EDITOR.isTablePresent(2, 1));
      assertTrue(IDE.CK_EDITOR.isTablePresent(2, 2));
      assertTrue(IDE.CK_EDITOR.isTablePresent(3, 1));
      assertTrue(IDE.CK_EDITOR.isTablePresent(3, 2));
   }

   private void isInsertTableCreated()
   {
      assertTrue(IDE.PREVIEW.isTablePresent(1, 1));
      assertTrue(IDE.PREVIEW.isTablePresent(1, 2));
      assertTrue(IDE.PREVIEW.isTablePresent(2, 1));
      assertTrue(IDE.PREVIEW.isTablePresent(2, 2));
      assertTrue(IDE.PREVIEW.isTablePresent(3, 1));
      assertTrue(IDE.PREVIEW.isTablePresent(3, 2));
      assertTrue(IDE.PREVIEW.isTablePresent(4, 1));
      assertTrue(IDE.PREVIEW.isTablePresent(4, 2));
   }

   @After
   public void tearDown() throws Exception
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
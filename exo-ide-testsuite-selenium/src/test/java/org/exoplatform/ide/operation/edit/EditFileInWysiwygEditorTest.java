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
 * Created by The eXo Platform SAS.
 * 
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @author <a href="dnochevnov@exoplatform.com">Dmytro Nochevnov</a>
 * @version $Id:
 * 
 */
public class EditFileInWysiwygEditorTest extends BaseTest
{

   // IDE-123 Edit file in WYSIWYG editor

   private final static String FILE_NAME = "EditFileInWysiwygEditor.html";

   private final static String PROJECT = EditFileInWysiwygEditorTest.class.getSimpleName();

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

   @BeforeClass
   public static void setUp()
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

      // step one open project switch between codeeditor and ck-editor. Check
      // content
      IDE.PROJECT.EXPLORER.waitOpened();
      IDE.PROJECT.OPEN.openProject(PROJECT);
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + FILE_NAME);
      IDE.LOADER.waitClosed();
      IDE.WELCOME_PAGE.close();

      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + FILE_NAME);
      IDE.PROJECT.EXPLORER.openItem(PROJECT + "/" + FILE_NAME);
      IDE.EDITOR.waitActiveFile();
      IDE.EDITOR.clickDesignButton();
      assertEquals(FILE_NAME, IDE.EDITOR.getTabTitle(0).replace(" *", ""));
      IDE.CK_EDITOR.waitToolsCkEditor();
      IDE.selectMainFrame();

      IDE.CK_EDITOR.waitToolsCkEditor();
      IDE.CK_EDITOR.clickOnToolCkEditor("Table");
      IDE.CK_EDITOR.waitCkEditorTableOpen();
      IDE.CK_EDITOR.typeToHeightwisiwyngtable("qwe");
      IDE.CK_EDITOR.clickOkWyswygTable();
      IDE.WARNING_DIALOG.waitOpened();
      assertEquals(IDE.WARNING_DIALOG.getWarningMessage(), WARNINNG_MASSAGE);
      IDE.WARNING_DIALOG.clickOk();
      IDE.WARNING_DIALOG.waitClosed();
      IDE.CK_EDITOR.clickOnToolCkEditor("Table");
      IDE.CK_EDITOR.waitCkEditorTableOpen();
      IDE.CK_EDITOR.typeToHeightwisiwyngtable("50");

      // step 3 Create default table and check
      IDE.CK_EDITOR.clickOkWyswygTable();
      IDE.CK_EDITOR.switchToCkEditorIframe(1);
      isDefaultTableCreated();

      // step 4 Add row in table and check
      IDE.CK_EDITOR.clickOnCellTableCkEditor(3, 1);
      IDE.CK_EDITOR.moveCursorUp();
      IDE.CK_EDITOR.callContextMenuCellTableCkEditor(3, 1);
      IDE.selectMainFrame();
      IDE.CK_EDITOR.switchToContextMenuIframe();
      IDE.CK_EDITOR.waitContextMenu("Row");
      IDE.CK_EDITOR.moveCursorToRowContextMenu("Row");
      IDE.selectMainFrame();
      IDE.CK_EDITOR.switchToContextSubMenuIframe();
      IDE.CK_EDITOR.waitContextSubMenu("Insert Row After");
      IDE.CK_EDITOR.clickOnContextSubMenu("Insert Row After");
      IDE.selectMainFrame();
      IDE.CK_EDITOR.switchToCkEditorIframe(1);
      isInsertTableCreated();

      // step 4 select code editor and check html code
      IDE.selectMainFrame();
      IDE.EDITOR.clickSourceButton();
      IDE.TOOLBAR.runCommand(ToolbarCommands.File.SAVE);
      IDE.LOADER.waitClosed();

      if (IDE_SETTINGS.getString("selenium.browser.commad").equals("GOOGLE_CHROME"))
      {
         assertEquals(CODE_AFTER_MODIFED_TABLE, IDE.EDITOR.getTextFromCodeEditor());
      }
      else
      {
         assertEquals(CODE_AFTER_MODIFED_TABLE_MOZILLA, IDE.EDITOR.getTextFromCodeEditor());
      }

      // step 5 save the file and check table in preview panel
      IDE.TOOLBAR.waitForButtonEnabled(ToolbarCommands.Run.SHOW_PREVIEW);
      IDE.TOOLBAR.runCommand(ToolbarCommands.Run.SHOW_PREVIEW);
      IDE.PREVIEW.waitHtmlPreviewOpened();
      IDE.PREVIEW.selectPreviewHtmlIFrame();
      isInsertTableCreated();
      IDE.selectMainFrame();
   }

   private void isDefaultTableCreated()
   {
      IDE.CK_EDITOR.waitTablePresent(1, 1);
      IDE.CK_EDITOR.waitTablePresent(1, 2);
      IDE.CK_EDITOR.waitTablePresent(2, 1);
      IDE.CK_EDITOR.waitTablePresent(2, 2);
      IDE.CK_EDITOR.waitTablePresent(3, 1);
      IDE.CK_EDITOR.waitTablePresent(3, 2);
   }

   private void isInsertTableCreated()
   {
      IDE.PREVIEW.waitTablePresent(1, 1);
      IDE.PREVIEW.waitTablePresent(1, 2);
      IDE.PREVIEW.waitTablePresent(2, 1);
      IDE.PREVIEW.waitTablePresent(2, 2);
      IDE.PREVIEW.waitTablePresent(3, 1);
      IDE.PREVIEW.waitTablePresent(3, 2);
      IDE.PREVIEW.waitTablePresent(4, 1);
      IDE.PREVIEW.waitTablePresent(4, 2);
   }

   @AfterClass
   public static void tearDown() throws Exception
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
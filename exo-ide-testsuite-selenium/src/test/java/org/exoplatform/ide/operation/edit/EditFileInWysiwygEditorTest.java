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

import org.everrest.http.client.ModuleException;
import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.MenuCommands;
import org.exoplatform.ide.TestConstants;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

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

   private final static String FOLDER = EditFileInWysiwygEditorTest.class.getSimpleName();

   private final static String URL = BASE_URL + REST_CONTEXT + "/" + WEBDAV_CONTEXT + "/" + REPO_NAME + "/" + WS_NAME + "/" + FOLDER + "/";
   
   private final static String IDE_URL = BASE_URL +"IDE/"+ REST_CONTEXT + "/" + WEBDAV_CONTEXT + "/" + REPO_NAME + "/" + WS_NAME + "/" + FOLDER + "/";
   
   @Before
   public void setUp()
   {
      String filePath = "src/test/resources/org/exoplatform/ide/operation/edit/" + FILE_NAME;
      try
      {
         VirtualFileSystemUtils.mkcol(URL);
         VirtualFileSystemUtils.put(filePath, MimeType.TEXT_HTML, "nt:resource", URL + FILE_NAME);
      }
      catch (IOException e)
      {
         e.printStackTrace();
      }
      catch (ModuleException e)
      {
         e.printStackTrace();
      }
   }
   
   @Test
   public void editFileInWysiwygEditor() throws Exception
   {
      IDE.WORKSPACE.waitForRootItem();
      
      IDE.WORKSPACE.selectItem(WS_URL);
      IDE.MENU.runCommand(MenuCommands.File.FILE, MenuCommands.File.REFRESH);
      IDE.WORKSPACE.waitForItem(URL);
      
      //------ 2 ---------------
      IDE.WORKSPACE.clickOpenIconOfFolder(URL);
      IDE.WORKSPACE.waitForItem(URL + FILE_NAME);   
      
      IDE.WORKSPACE.doubleClickOnFile(URL + FILE_NAME);
      IDE.EDITOR.waitTabPresent(0);
      IDE.EDITOR.clickDesignButton();

      assertEquals("File should be unchanged!", FILE_NAME, IDE.EDITOR.getTabTitle(0));
      
      IDE.EDITOR.checkCkEditorOpened(0);
      
      //------ 3 ---------------
      IDE.EDITOR.clickSourceButton();
      
      IDE.EDITOR.checkCodeEditorOpened(0);

      assertEquals("File should be unchanged!", FILE_NAME, IDE.EDITOR.getTabTitle(0));
      
      final String defaultText =
         "<html>\n <head>\n  <title></title>\n </head>\n <body>\n  <br />\n </body>\n</html>";

      assertEquals(defaultText, IDE.EDITOR.getTextFromCodeEditor(0));

      //------ 4 ---------------
      IDE.EDITOR.clickDesignButton();

      IDE.EDITOR.checkCkEditorOpened(0);
      
      assertEquals("File should be unchanged!", FILE_NAME, IDE.EDITOR.getTabTitle(0));
      
      //check, that content is empty
      assertEquals("", IDE.EDITOR.getTextFromCKEditor(0));

      //------ 5 ---------------
      //click on button Table in CK editor
      selenium().clickAt("//td[@class='cke_top']//a[contains(@class, 'cke_button_table')]", "");
      Thread.sleep(TestConstants.SLEEP);

      //check Table Properties dialog window appeared
      assertTrue(selenium().isElementPresent("//div[@class='cke_dialog_body']"));
      assertEquals("Table Properties", selenium().getText("//div[@class='cke_dialog_body']/div"));

      //TODO fix problem in issue IDE-762
      //------ 6 ---------------
      //type qwe to Height field
      selenium()
         .typeKeys(
            "//table[@class='cke_dialog_contents']/tbody/tr/td/div/table/tbody/tr/td/table/tbody/tr/td[2]/div/table/tbody//tr[2]/td/table/tbody/tr/td/div/div[2]/div/input",
            "qwe");

      //click Ok button
      selenium().click("//div[@class='cke_dialog_footer']//span[text()='OK']");

      //warning dialog

      //------ 7 ---------------
      //click Ok button in warning dialog
      waitForElementPresent("exoWarningDialog");
      selenium().click("//div[@id='exoWarningDialogOkButton']");
      waitForElementNotPresent("exoWarningDialog");

      //click Cancel button in Table Properties dialog
      waitForElementPresent("//div[@class='cke_dialog_footer']//span[text()='Cancel']");
      selenium().click("//div[@class='cke_dialog_footer']//span[text()='Cancel']");
      Thread.sleep(TestConstants.SLEEP);
      //check Table Properties dialog disappeared
      assertFalse(selenium().isElementPresent("//div[@class='cke_dialog_body']"));

      //------ 8 ---------------
      //click on button Table in CK editor
      selenium().clickAt("//td[@class='cke_top']//a[contains(@class, 'cke_button_table')]", "");
      Thread.sleep(TestConstants.SLEEP);
      //check Table Properties dialog window appeared
      assertTrue(selenium().isElementPresent("//div[@class='cke_dialog_body']"));
      assertEquals("Table Properties", selenium().getText("//div[@class='cke_dialog_body']/div"));
      //click Ok button
      selenium().click("//div[@class='cke_dialog_footer']//span[text()='OK']");
      Thread.sleep(TestConstants.SLEEP);

      //check table with 2 columns and 3 rows added to ck editor
      IDE.EDITOR.selectCkEditorIframe(0);
      checkTable2x3Present();
      IDE.selectMainFrame();
      IDE.EDITOR.clickSourceButton();
      Thread.sleep(TestConstants.SLEEP_SHORT);
      
      assertEquals("File should be marked as changed!", FILE_NAME + " *", IDE.EDITOR.getTabTitle(0));
      
      IDE.MENU.runCommand(MenuCommands.File.FILE, MenuCommands.File.SAVE);

      //------ 9 ---------------
      //      runHotkeyWithinCkEditor(0, true, false, java.awt.event.KeyEvent.VK_S);
      Thread.sleep(TestConstants.SLEEP);

      assertEquals("File should be unchanged!", FILE_NAME, IDE.EDITOR.getTabTitle(0));
      IDE.MENU.runCommand(MenuCommands.Run.RUN, MenuCommands.Run.SHOW_PREVIEW);
      //      Thread.sleep(TestConstants.SLEEP);

      //check is Preview tab appeared
      assertTrue(selenium().isElementPresent("//div[@view-id=\"idePreviewHTMLView\"]"));

      //select iframe in Preview tab
      IDE.PREVIEW.selectIFrame(IDE_URL + FILE_NAME);

      checkTable2x3Present();

      //------ 10 ---------------
      IDE.selectMainFrame();
      Thread.sleep(TestConstants.SLEEP_SHORT);
      IDE.EDITOR.clickDesignButton();
      Thread.sleep(TestConstants.SLEEP_SHORT);
      assertEquals("File should be unchanged!", FILE_NAME, IDE.EDITOR.getTabTitle(0));
      
      IDE.EDITOR.selectCkEditorIframe(0);
      
      //right click on cell
      selenium()
         .contextMenuAt(
            "//table[@cellspacing='1' and @cellpadding='1' and @border='1'  and @style='width: 200px;']/tbody/tr[3]/td[1]",
            "");
      IDE.selectMainFrame();

      selenium().clickAt("//span[text()='Row']", "");
      selenium().clickAt("//span[text()='Insert Row After']", "");

      Thread.sleep(TestConstants.SLEEP);

      assertEquals("File should be marked as changed!", FILE_NAME + " *", IDE.EDITOR.getTabTitle(0));
      
      checkTable2x4Present();

      //------ 11 ---------------
      IDE.EDITOR.clickSourceButton();

      assertEquals("File should be marked as changed!", FILE_NAME + " *", IDE.EDITOR.getTabTitle(0));
      
      final String textWithTable2x4InCkEditor =
         "<html>\n"
            + " <head>\n"
            + "  <title></title>\n"
            + " </head>\n"
            + " <body>\n"
            + "  <table border=\"1\" cellpadding=\"1\" cellspacing=\"1\" style=\"width: 200px;\">\n"
            + "   <tbody>\n" + "    <tr>\n" + "     <td>\n" + "      &nbsp;</td>\n"
            + "     <td>\n" + "      &nbsp;</td>\n" + "    </tr>\n" + "    <tr>\n"
            + "     <td>\n" + "      &nbsp;</td>\n" + "     <td>\n" + "      &nbsp;</td>\n"
            + "    </tr>\n" + "    <tr>\n" + "     <td>\n" + "      &nbsp;</td>\n"
            + "     <td>\n" + "      &nbsp;</td>\n" + "    </tr>\n" + "    <tr>\n"
            + "     <td>\n" + "      &nbsp;</td>\n" + "     <td>\n" + "      &nbsp;</td>\n"
            + "    </tr>\n" + "   </tbody>\n" + "  </table>\n" + "  <br />\n" + " </body>\n" + "</html>";

      assertEquals(textWithTable2x4InCkEditor, IDE.EDITOR.getTextFromCodeEditor(0));
      //------ 12 ---------------
      IDE.NAVIGATION.deleteSelectedItems();

      //------ 13 ---------------
      //check second confirmation dialog
      checkDeleteConfirmationDialogOfModifiedText();

      //click No button in confirmation dialog
      selenium().click("exoAskDialogNoButton");
      Thread.sleep(TestConstants.SLEEP);

      //check file stays in Code editor
      IDE.EDITOR.checkCodeEditorOpened(0);
      
      assertEquals(textWithTable2x4InCkEditor, IDE.EDITOR.getTextFromCodeEditor(0));

      assertEquals("File should be marked as changed!", FILE_NAME + " *", IDE.EDITOR.getTabTitle(0));
      
      assertEquals(200, VirtualFileSystemUtils.get(URL).getStatusCode());

      IDE.EDITOR.clickDesignButton();
      IDE.EDITOR.checkCkEditorOpened(0);
      assertEquals("File should be marked as changed!", FILE_NAME + " *", IDE.EDITOR.getTabTitle(0));
      
      //------ 14 ---------------
      //reopen file with CodeMirror
      
      IDE.NAVIGATION.openFileFromNavigationTreeWithCodeEditor(URL + FILE_NAME, false);

      //reopen confirmation dialog
      assertTrue(selenium().isElementPresent("exoAskDialog"));
      assertEquals("IDE", selenium().getText("//div[@id='exoAskDialog']//div[@class='Caption']/span['info']"));
      assertTrue(selenium().isElementPresent("exoAskDialogYesButton"));
      assertTrue(selenium().isElementPresent("exoAskDialogNoButton"));

      //click Yes button
      selenium().click("exoAskDialogYesButton");
      Thread.sleep(TestConstants.SLEEP);
      
      IDE.EDITOR.checkCodeEditorOpened(0);
      assertEquals("File should be marked as changed!", FILE_NAME + " *", IDE.EDITOR.getTabTitle(0));
      
      //reopen file with CKEditor
      IDE.WORKSPACE.doubleClickOnFile(URL + FILE_NAME);
      IDE.EDITOR.clickDesignButton();

      //reopen confirmation dialog
      assertTrue(selenium().isElementPresent("exoAskDialog"));
      assertEquals("IDE", selenium().getText("//div[@id='exoAskDialog']//div[@class='Caption']/span['info']"));
      assertTrue(selenium().isElementPresent("exoAskDialogYesButton"));
      assertTrue(selenium().isElementPresent("exoAskDialogNoButton"));

      //click No button
      selenium().click("exoAskDialogNoButton");
      //TODO:::: check this test
//      IDE.OPENWITH.clickCancelButton();
      Thread.sleep(TestConstants.SLEEP);

      IDE.EDITOR.checkCodeEditorOpened(0);
      assertEquals("File should be marked as changed!", FILE_NAME + " *", IDE.EDITOR.getTabTitle(0));
      
      final String table2x3FromCodeEditor =
         "<html><head><title></title></head><body>"
            + "<tableborder=\"1\"cellpadding=\"1\"cellspacing=\"1\"style=\"width:200px;\">"
            + "<tbody><tr><td>&nbsp;</td><td>&nbsp;</td></tr><tr><td>&nbsp;</td><td>&nbsp;</td>"
            + "</tr><tr><td>&nbsp;</td><td>&nbsp;</td></tr><tr><td>&nbsp;</td>"
            + "<td>&nbsp;</td></tr></tbody></table><br/></body></html>";

      String textFromCodeEditor = IDE.EDITOR.getTextFromCodeEditor(0);

      //remove all white spaces, because code mirror 
      //can change format of text 
      textFromCodeEditor = textFromCodeEditor.replaceAll("[ \t\n]", "");

      assertEquals(table2x3FromCodeEditor, textFromCodeEditor);
   }

   /**
    * Check confirmation dialog,
    * if you want to delete file,
    * which has modified and non-saved text.
    */
   private void checkDeleteConfirmationDialogOfModifiedText()
   {
      assertTrue(selenium().isElementPresent("exoAskDialog"));
      assertTrue(selenium().isElementPresent("exoAskDialogYesButton"));
      assertTrue(selenium().isElementPresent("exoAskDialogNoButton"));
   }

   private void checkTable2x4Present()
   {
      //check table
      assertTrue(selenium()
         .isElementPresent("//table[@cellspacing='1' and @cellpadding='1' and @border='1'  and @style='width: 200px;']/tbody"));
      //check first row
      assertTrue(selenium()
         .isElementPresent("//table[@cellspacing='1' and @cellpadding='1' and @border='1'  and @style='width: 200px;']/tbody/tr[1]"));
      assertTrue(selenium()
         .isElementPresent("//table[@cellspacing='1' and @cellpadding='1' and @border='1'  and @style='width: 200px;']/tbody/tr[1]/td[1]"));
      assertTrue(selenium()
         .isElementPresent("//table[@cellspacing='1' and @cellpadding='1' and @border='1'  and @style='width: 200px;']/tbody/tr[1]/td[2]"));
      //check second row
      assertTrue(selenium()
         .isElementPresent("//table[@cellspacing='1' and @cellpadding='1' and @border='1'  and @style='width: 200px;']/tbody/tr[2]"));
      assertTrue(selenium()
         .isElementPresent("//table[@cellspacing='1' and @cellpadding='1' and @border='1'  and @style='width: 200px;']/tbody/tr[2]/td[1]"));
      assertTrue(selenium()
         .isElementPresent("//table[@cellspacing='1' and @cellpadding='1' and @border='1'  and @style='width: 200px;']/tbody/tr[2]/td[2]"));
      //check third row
      assertTrue(selenium()
         .isElementPresent("//table[@cellspacing='1' and @cellpadding='1' and @border='1'  and @style='width: 200px;']/tbody/tr[3]"));
      assertTrue(selenium()
         .isElementPresent("//table[@cellspacing='1' and @cellpadding='1' and @border='1'  and @style='width: 200px;']/tbody/tr[3]/td[1]"));
      assertTrue(selenium()
         .isElementPresent("//table[@cellspacing='1' and @cellpadding='1' and @border='1'  and @style='width: 200px;']/tbody/tr[3]/td[2]"));
      //check fourth row
      assertTrue(selenium()
         .isElementPresent("//table[@cellspacing='1' and @cellpadding='1' and @border='1'  and @style='width: 200px;']/tbody/tr[4]"));
      assertTrue(selenium()
         .isElementPresent("//table[@cellspacing='1' and @cellpadding='1' and @border='1'  and @style='width: 200px;']/tbody/tr[4]/td[1]"));
      assertTrue(selenium()
         .isElementPresent("//table[@cellspacing='1' and @cellpadding='1' and @border='1'  and @style='width: 200px;']/tbody/tr[4]/td[2]"));
   }

   private void checkTable2x3Present()
   {
      //                        check table   table  cellspacing="1"      cellpadding="1"      border="1"       style="height: 200px; width: 200px;"
      assertTrue(selenium()
         .isElementPresent("//table[@cellspacing='1' and @cellpadding='1' and @border='1'  and @style='width: 200px;']/tbody"));
      //check first row
      assertTrue(selenium()
         .isElementPresent("//table[@cellspacing='1' and @cellpadding='1' and @border='1'  and @style='width: 200px;']/tbody/tr[1]"));
      assertTrue(selenium()
         .isElementPresent("//table[@cellspacing='1' and @cellpadding='1' and @border='1'  and @style='width: 200px;']/tbody/tr[1]/td[1]"));
      assertTrue(selenium()
         .isElementPresent("//table[@cellspacing='1' and @cellpadding='1' and @border='1'  and @style='width: 200px;']/tbody/tr[1]/td[2]"));
      //check second row
      assertTrue(selenium()
         .isElementPresent("//table[@cellspacing='1' and @cellpadding='1' and @border='1'  and @style='width: 200px;']/tbody/tr[2]"));
      assertTrue(selenium()
         .isElementPresent("//table[@cellspacing='1' and @cellpadding='1' and @border='1'  and @style='width: 200px;']/tbody/tr[2]/td[1]"));
      assertTrue(selenium()
         .isElementPresent("//table[@cellspacing='1' and @cellpadding='1' and @border='1'  and @style='width: 200px;']/tbody/tr[2]/td[2]"));
      //check third row
      assertTrue(selenium()
         .isElementPresent("//table[@cellspacing='1' and @cellpadding='1' and @border='1'  and @style='width: 200px;']/tbody/tr[3]"));
      assertTrue(selenium()
         .isElementPresent("//table[@cellspacing='1' and @cellpadding='1' and @border='1'  and @style='width: 200px;']/tbody/tr[3]/td[1]"));
      assertTrue(selenium()
         .isElementPresent("//table[@cellspacing='1' and @cellpadding='1' and @border='1'  and @style='width: 200px;']/tbody/tr[3]/td[2]"));
   }

   @After
   public void tearDown() throws Exception
   {
     IDE.EDITOR.closeTabIgnoringChanges(0);
      try
      {
         VirtualFileSystemUtils.delete(URL);
      }
      catch (IOException e)
      {
         e.printStackTrace();
      }
      catch (ModuleException e)
      {
         e.printStackTrace();
      }
   }

}
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

import java.io.IOException;

import org.exoplatform.common.http.client.ModuleException;
import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.MenuCommands;
import org.exoplatform.ide.TestConstants;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.junit.AfterClass;
import org.junit.Test;

/**
 * Created by The eXo Platform SAS.
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id:
 *
 */
public class EditFileInWysiwygEditorTest extends BaseTest
{

   //IDE-123 Edit file in WYSIWYG editor

   private final static String HTML_FILE = "EditFileInWysiwygEditor.html";

   private final static String URL = BASE_URL + REST_CONTEXT + "/jcr/" + REPO_NAME + "/" + WS_NAME + "/" + HTML_FILE;

   @Test
   public void editFileInWysiwygEditor() throws Exception
   {

      final String defaultText =
         "<html>\n" + "\t<head>\n" + "\t\t<title></title>\n" + "\t</head>\n" + "\t<body>\n" + "\t\t<br />\n"
            + "\t</body>\n" + "</html>";
      
      Thread.sleep(TestConstants.SLEEP);

      //------ 1 ---------------
      createSaveAndCloseFile(MenuCommands.New.HTML_FILE, HTML_FILE, 0);
      Thread.sleep(TestConstants.SLEEP);

      //------ 2 ---------------
      openFileFromNavigationTreeWithCkEditor(HTML_FILE, false);
      checkCkEditorOpened(0);
      //TODO:
      //how to check, that cursor should be at the start of editor.

      //------ 3 ---------------
      clickSourceButton();
      checkSourceAreaActiveInCkEditor(0, true);
      assertEquals(defaultText, getTextFromSourceInCkEditor(0));

      // check all buttons are disabled except Source

      //to check, that all buttons are disabled,
      //find in cke_top td class all elements a and check, that all of them
      //contains in class attribute "cke_disabled",
      //except element, that contains "cke_button_source" (becaus it is Source button)
      assertFalse(selenium
         .isElementPresent("//td[@class='cke_top']//span[@class='cke_button']/a[not(contains(@class, 'cke_disabled')) and not(contains(@class, 'cke_button_source'))]"));

      // check Source button

      //check than Source button is enabled
      assertTrue(selenium
         .isElementPresent("//td[@class='cke_top']//span[@class='cke_button']/a[not(contains(@class, 'cke_disabled')) and contains(@class, 'cke_button_source')]"));

      //------ 4 ---------------
      clickSourceButton();
      //check all buttons are enabled
      assertFalse(selenium
         .isElementPresent("//td[@class='cke_top']//span[@class='cke_button']/a[contains(@class, 'cke_disabled')]"));
      //check, that content is empty
      assertEquals("", getTextFromCkEditor(0));

      //------ 5 ---------------
      //click on button Table in CK editor
      selenium.clickAt("//td[@class='cke_top']//a[contains(@class, 'cke_button_table')]", "");
      Thread.sleep(TestConstants.SLEEP);

      //check Table Properties dialog window appeared
      assertTrue(selenium.isElementPresent("//div[@class='cke_dialog_body']"));
      assertEquals("Table Properties", selenium.getText("//div[@class='cke_dialog_body']/div"));

      //------ 6 ---------------
      //type qwe to Height field
      selenium
         .typeKeys(
            "//table[@class='cke_dialog_contents']/tbody/tr/td/div/table/tbody/tr/td/table/tbody/tr/td[2]/div/table/tbody//tr[2]/td/table/tbody/tr/td/div/div[2]/div/input",
            "qwe");
      //click Ok button
      selenium.click("//div[@class='cke_dialog_footer']//span[text()='OK']");
      Thread.sleep(TestConstants.SLEEP);

      //warning dialog
      assertTrue(selenium.isElementPresent("scLocator=//Dialog[ID=\"isc_globalWarn\"]"));
      assertEquals("WYSIWYG Editor Error", selenium.getText("scLocator=//Dialog[ID=\"isc_globalWarn\"]/header"));
      assertEquals("Table height must be a number.", selenium
         .getText("scLocator=//Dialog[ID=\"isc_globalWarn\"]/blurb/"));
      //------ 7 ---------------
      //click Ok button in warning dialog
      selenium.click("scLocator=//Dialog[ID=\"isc_globalWarn\"]/okButton/");

      //click Cancel button in Table Properties dialog
      selenium.click("//div[@class='cke_dialog_footer']//span[text()='Cancel']");
      Thread.sleep(TestConstants.SLEEP);
      //check Table Properties dialog disappeared
      assertFalse(selenium.isElementPresent("//div[@class='cke_dialog_body']"));

      //------ 8 ---------------
      //click on button Table in CK editor
      selenium.clickAt("//td[@class='cke_top']//a[contains(@class, 'cke_button_table')]", "");
      Thread.sleep(TestConstants.SLEEP);
      //check Table Properties dialog window appeared
      assertTrue(selenium.isElementPresent("//div[@class='cke_dialog_body']"));
      assertEquals("Table Properties", selenium.getText("//div[@class='cke_dialog_body']/div"));
      //click Ok button
      selenium.click("//div[@class='cke_dialog_footer']//span[text()='OK']");
      Thread.sleep(TestConstants.SLEEP);

      //check table with 2 columns and 3 rows added to ck editor
      selectCkEditorIframe(0);
      checkTable2x3Present();
      selectMainFrame();
      clickSourceButton();
      Thread.sleep(TestConstants.SLEEP_SHORT);
      runTopMenuCommand(MenuCommands.File.FILE, MenuCommands.File.SAVE);

      //------ 9 ---------------
      //      runHotkeyWithinCkEditor(0, true, false, java.awt.event.KeyEvent.VK_S);
      Thread.sleep(TestConstants.SLEEP);

      assertEquals(HTML_FILE, getTabTitle(0));
      runTopMenuCommand(MenuCommands.Run.RUN, MenuCommands.Run.SHOW_PREVIEW);
      Thread.sleep(TestConstants.SLEEP);

      //check is Preview tab appeared
      assertTrue(selenium.isElementPresent("scLocator=//TabSet[ID=\"ideOperationFormTabSet\"]/tab[ID=Preview]/"));

      //select iframe in Preview tab
      selenium.selectFrame("//iframe[@src='" + URL + "']");

      checkTable2x3Present();

      selectMainFrame();
      Thread.sleep(TestConstants.SLEEP_SHORT);
      clickSourceButton();
      Thread.sleep(TestConstants.SLEEP_SHORT);
      //------ 10 ---------------
      selectCkEditorIframe(0);
      //right click on cell
      selenium
         .contextMenuAt(
            "//table[@cellspacing='1' and @cellpadding='1' and @border='1'  and @style='width: 200px;']/tbody/tr[3]/td[1]",
            "");
      selectMainFrame();

      selenium.clickAt("//span[text()='Row']", "");
      selenium.clickAt("//span[text()='Insert Row After']", "");

      Thread.sleep(TestConstants.SLEEP);

      checkTable2x4Present();

      //------ 11 ---------------
      clickSourceButton();

      final String textWithTable2x4InCkEditor =
         "<html>\n"
            + "\t<head>\n"
            + "\t\t<title></title>\n"
            + "\t</head>\n"
            + "\t<body>\n"
            //+"\t\t<br />\n"
            + "\t\t<table border=\"1\" cellpadding=\"1\" cellspacing=\"1\" style=\"width: 200px;\">\n"
            + "\t\t\t<tbody>\n" + "\t\t\t\t<tr>\n" + "\t\t\t\t\t<td>\n" + "\t\t\t\t\t\t&nbsp;</td>\n"
            + "\t\t\t\t\t<td>\n" + "\t\t\t\t\t\t&nbsp;</td>\n" + "\t\t\t\t</tr>\n" + "\t\t\t\t<tr>\n"
            + "\t\t\t\t\t<td>\n" + "\t\t\t\t\t\t&nbsp;</td>\n" + "\t\t\t\t\t<td>\n" + "\t\t\t\t\t\t&nbsp;</td>\n"
            + "\t\t\t\t</tr>\n" + "\t\t\t\t<tr>\n" + "\t\t\t\t\t<td>\n" + "\t\t\t\t\t\t&nbsp;</td>\n"
            + "\t\t\t\t\t<td>\n" + "\t\t\t\t\t\t&nbsp;</td>\n" + "\t\t\t\t</tr>\n" + "\t\t\t\t<tr>\n"
            + "\t\t\t\t\t<td>\n" + "\t\t\t\t\t\t&nbsp;</td>\n" + "\t\t\t\t\t<td>\n" + "\t\t\t\t\t\t&nbsp;</td>\n"
            + "\t\t\t\t</tr>\n" + "\t\t\t</tbody>\n" + "\t\t</table>\n" + "\t\t<br />\n" + "\t</body>\n" + "</html>";

      assertEquals(textWithTable2x4InCkEditor, getTextFromSourceInCkEditor(0));

      //------ 12 ---------------
      deleteSelectedItems();

      //------ 13 ---------------

      //check second confirmation dialog
      checkDeleteConfirmationDialogOfModifiedText();

      //click No button in confirmation dialog
      selenium.click("scLocator=//Dialog[ID=\"isc_globalWarn\"]/noButton/");
      Thread.sleep(TestConstants.SLEEP);

      //check file stays in CK editor
      checkSourceAreaActiveInCkEditor(0, true);
      assertEquals(textWithTable2x4InCkEditor, getTextFromSourceInCkEditor(0));

      assertEquals(200, VirtualFileSystemUtils.get(URL).getStatusCode());

      //------ 14 ---------------
      //reopen file with CodeMirror
      openFileFromNavigationTreeWithCodeEditor(HTML_FILE, false);
      //reopne confirmatioin dialog
      assertTrue(selenium.isElementPresent("scLocator=//Dialog[ID=\"isc_globalWarn\"]"));
      assertEquals("Info", selenium.getText("scLocator=//Dialog[ID=\"isc_globalWarn\"]/headerLabel/"));
      assertTrue(selenium.isElementPresent("scLocator=//Dialog[ID=\"isc_globalWarn\"]/yesButton/"));
      assertTrue(selenium.isElementPresent("scLocator=//Dialog[ID=\"isc_globalWarn\"]/noButton/"));
      //click Ok button
      selenium.click("scLocator=//Dialog[ID=\"isc_globalWarn\"]/yesButton/");
      Thread.sleep(TestConstants.SLEEP);

      checkCodeEditorOpened(0);

      final String table2x3FromCodeEditor =
         "<html><head><title></title></head><body>"
            + "<tableborder=\"1\"cellpadding=\"1\"cellspacing=\"1\"style=\"width:200px;\">"
            + "<tbody><tr><td>&nbsp;</td><td>&nbsp;</td></tr><tr><td>&nbsp;</td><td>&nbsp;</td>"
            + "</tr><tr><td>&nbsp;</td><td>&nbsp;</td></tr><tr><td>&nbsp;</td>"
            + "<td>&nbsp;</td></tr></tbody></table><br/></body></html>";

      String textFromCodeEditor = getTextFromCodeEditor(0);

      //remove all white spaces, because code mirror 
      //can change format of text 
      textFromCodeEditor = textFromCodeEditor.replaceAll("[ \t\n]", "");

      assertEquals(table2x3FromCodeEditor, textFromCodeEditor);

      //      //------ 15 ---------------
      //      deleteSelectedItems();
      //      checkDeleteConfirmationDialogOfModifiedText();
      //      
      //      //------ 16 ---------------
      //      //click Ok button
      //      selenium.click("scLocator=//Dialog[ID=\"isc_globalWarn\"]/yesButton/");
      //      Thread.sleep(TestConstants.SLEEP);
      //      
      //      assertElementNotPresentInWorkspaceTree(htmlFile);
      //      checkNoEditorOpened(0);
      //      
      //      checkFileDeletedViaWebDav(htmlFile);
   }

   /**
    * Check confirmation dialog,
    * if you want to delete file,
    * which has modified and non-saved text.
    */
   private void checkDeleteConfirmationDialogOfModifiedText()
   {
      assertTrue(selenium.isElementPresent("scLocator=//Dialog[ID=\"isc_globalWarn\"]/headerLabel/"));
      assertEquals("Delete file", selenium.getText("scLocator=//Dialog[ID=\"isc_globalWarn\"]/headerLabel/"));
      assertTrue(selenium.isElementPresent("scLocator=//Dialog[ID=\"isc_globalWarn\"]/yesButton/"));
      assertTrue(selenium.isElementPresent("scLocator=//Dialog[ID=\"isc_globalWarn\"]/noButton/"));
   }

   private void checkTable2x4Present()
   {
      //check table
      assertTrue(selenium
         .isElementPresent("//table[@cellspacing='1' and @cellpadding='1' and @border='1'  and @style='width: 200px;']/tbody"));
      //check first row
      assertTrue(selenium
         .isElementPresent("//table[@cellspacing='1' and @cellpadding='1' and @border='1'  and @style='width: 200px;']/tbody/tr[1]"));
      assertTrue(selenium
         .isElementPresent("//table[@cellspacing='1' and @cellpadding='1' and @border='1'  and @style='width: 200px;']/tbody/tr[1]/td[1]"));
      assertTrue(selenium
         .isElementPresent("//table[@cellspacing='1' and @cellpadding='1' and @border='1'  and @style='width: 200px;']/tbody/tr[1]/td[2]"));
      //check second row
      assertTrue(selenium
         .isElementPresent("//table[@cellspacing='1' and @cellpadding='1' and @border='1'  and @style='width: 200px;']/tbody/tr[2]"));
      assertTrue(selenium
         .isElementPresent("//table[@cellspacing='1' and @cellpadding='1' and @border='1'  and @style='width: 200px;']/tbody/tr[2]/td[1]"));
      assertTrue(selenium
         .isElementPresent("//table[@cellspacing='1' and @cellpadding='1' and @border='1'  and @style='width: 200px;']/tbody/tr[2]/td[2]"));
      //check third row
      assertTrue(selenium
         .isElementPresent("//table[@cellspacing='1' and @cellpadding='1' and @border='1'  and @style='width: 200px;']/tbody/tr[3]"));
      assertTrue(selenium
         .isElementPresent("//table[@cellspacing='1' and @cellpadding='1' and @border='1'  and @style='width: 200px;']/tbody/tr[3]/td[1]"));
      assertTrue(selenium
         .isElementPresent("//table[@cellspacing='1' and @cellpadding='1' and @border='1'  and @style='width: 200px;']/tbody/tr[3]/td[2]"));
      //check fourth row
      assertTrue(selenium
         .isElementPresent("//table[@cellspacing='1' and @cellpadding='1' and @border='1'  and @style='width: 200px;']/tbody/tr[4]"));
      assertTrue(selenium
         .isElementPresent("//table[@cellspacing='1' and @cellpadding='1' and @border='1'  and @style='width: 200px;']/tbody/tr[4]/td[1]"));
      assertTrue(selenium
         .isElementPresent("//table[@cellspacing='1' and @cellpadding='1' and @border='1'  and @style='width: 200px;']/tbody/tr[4]/td[2]"));
   }

   private void checkTable2x3Present()
   {
      //                        check table   table  cellspacing="1"      cellpadding="1"      border="1"       style="height: 200px; width: 200px;"
      assertTrue(selenium
         .isElementPresent("//table[@cellspacing='1' and @cellpadding='1' and @border='1'  and @style='width: 200px;']/tbody"));
      //check first row
      assertTrue(selenium
         .isElementPresent("//table[@cellspacing='1' and @cellpadding='1' and @border='1'  and @style='width: 200px;']/tbody/tr[1]"));
      assertTrue(selenium
         .isElementPresent("//table[@cellspacing='1' and @cellpadding='1' and @border='1'  and @style='width: 200px;']/tbody/tr[1]/td[1]"));
      assertTrue(selenium
         .isElementPresent("//table[@cellspacing='1' and @cellpadding='1' and @border='1'  and @style='width: 200px;']/tbody/tr[1]/td[2]"));
      //check second row
      assertTrue(selenium
         .isElementPresent("//table[@cellspacing='1' and @cellpadding='1' and @border='1'  and @style='width: 200px;']/tbody/tr[2]"));
      assertTrue(selenium
         .isElementPresent("//table[@cellspacing='1' and @cellpadding='1' and @border='1'  and @style='width: 200px;']/tbody/tr[2]/td[1]"));
      assertTrue(selenium
         .isElementPresent("//table[@cellspacing='1' and @cellpadding='1' and @border='1'  and @style='width: 200px;']/tbody/tr[2]/td[2]"));
      //check third row
      assertTrue(selenium
         .isElementPresent("//table[@cellspacing='1' and @cellpadding='1' and @border='1'  and @style='width: 200px;']/tbody/tr[3]"));
      assertTrue(selenium
         .isElementPresent("//table[@cellspacing='1' and @cellpadding='1' and @border='1'  and @style='width: 200px;']/tbody/tr[3]/td[1]"));
      assertTrue(selenium
         .isElementPresent("//table[@cellspacing='1' and @cellpadding='1' and @border='1'  and @style='width: 200px;']/tbody/tr[3]/td[2]"));
   }

   private void selectCkEditorIframe(int tabIndex)
   {
      String divIndex = String.valueOf(tabIndex + 2);
      selenium.selectFrame("//div[@class='tabSetContainer']/div/div[" + divIndex + "]//iframe");
   }

   private String getTextFromCkEditor(int tabIndex)
   {
      final String divIndex = String.valueOf(tabIndex + 2);
      //select iframe with CK editor
      selenium.selectFrame("//div[@class='tabSetContainer']/div/div[" + divIndex + "]//iframe");
      final String result = selenium.getText("//body/");
      selectMainFrame();

      return result;
   }

   /**
    * Click on Source button in CK editor.
    * 
    * @throws Exception
    */
   private void clickSourceButton() throws Exception
   {
      selenium.clickAt("//table[@class='cke_editor']//span[text()='Source']", "");
      Thread.sleep(TestConstants.SLEEP);
   }

   /**
    * Return text from source area in CK editor.
    * 
    * You must be sure, that source area in CK editor is active.
    * Otherwise exception will occurs
    * 
    * @param tabIndex index of editor tab
    * @return {@link String}
    */
   private String getTextFromSourceInCkEditor(int tabIndex)
   {
      String divIndex = String.valueOf(tabIndex + 2);
      return selenium.getValue("//div[@class='tabSetContainer']/div/div[" + divIndex
         + "]//table[@class='cke_editor']//textarea");
   }

   /**
    * Check what state of CK editor active: source or visual.
    * 
    * @param tabIndex
    * @param isSourceActive
    * @throws Exception
    */
   private void checkSourceAreaActiveInCkEditor(int tabIndex, boolean isSourceActive) throws Exception
   {
      String divIndex = String.valueOf(tabIndex + 2);

      if (isSourceActive)
      {
         assertTrue(selenium.isElementPresent("//div[@class='tabSetContainer']/div/div[" + divIndex
            + "]//table[@class='cke_editor']//textarea"));

         assertFalse(selenium.isElementPresent("//div[@class='tabSetContainer']/div/div[" + divIndex + "]//iframe"));
      }
      else
      {
         assertFalse(selenium.isElementPresent("//div[@class='tabSetContainer']/div/div[" + divIndex
            + "]//table[@class='cke_editor']//textarea"));

         assertTrue(selenium.isElementPresent("//div[@class='tabSetContainer']/div/div[" + divIndex + "]//iframe"));
      }
   }

   @AfterClass
   public static void tearDown()
   {
      try
      {
         VirtualFileSystemUtils.delete(URL);
      }
      catch (IOException e)
      {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }
      catch (ModuleException e)
      {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }
   }

}

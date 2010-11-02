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
package org.exoplatform.ide.operation.templates;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.thoughtworks.selenium.Selenium;

import org.exoplatform.common.http.client.ModuleException;
import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.MenuCommands;
import org.exoplatform.ide.TestConstants;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;

/**
 * Created by The eXo Platform SAS.
 *	
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id:   ${date} ${time}
 *
 */
public class TemplateOperationsTest extends BaseTest
{

   /**
       * 
       * 
       * @throws Exception
       */

   private final static String TEST_FOLDER = "TestTemplate";

   private final static String FILE_NAME = "HtmlTemplate.html";

   private final static String URL = BASE_URL + REST_CONTEXT + "/" + WEBDAV_CONTEXT + "/" + REPO_NAME + "/" + WS_NAME
      + "/" + TEST_FOLDER + "/";

   @BeforeClass
   public static void setUp()
   {

      try
      {
         cleanRegistry();

         String filePath = "src/test/resources/org/exoplatform/ide/operation/templates/HtmlTemplate.html";

         VirtualFileSystemUtils.mkcol(URL);
         VirtualFileSystemUtils.put(filePath, MimeType.TEXT_HTML, URL + FILE_NAME);
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
   public void testRemoveNonDefaultFileTemplates() throws Exception
   {
      // --------1----------
      Thread.sleep(TestConstants.SLEEP);

      // runCommandFromMenuNewOnToolbar("HTML File");

      openOrCloseFolder(TEST_FOLDER);
      Thread.sleep(TestConstants.REDRAW_PERIOD);
      openFileFromNavigationTreeWithCodeEditor(FILE_NAME, false);
      Thread.sleep(TestConstants.EDITOR_OPEN_PERIOD);
      // Using type command me be (type...)
      // delete all Symbols
      // ----------2-------------

      //deleteFileContent();
      //Thread.sleep(TestConstants.EDITOR_OPEN_PERIOD);
      //typeContent();
      // -------2--------

      runTopMenuCommand(MenuCommands.File.FILE, MenuCommands.File.SAVE_AS_TEMPLATE);
      Thread.sleep(TestConstants.REDRAW_PERIOD);
      TemplateUtils.checkSaveAsTemplateWindow(selenium);
      Thread.sleep(TestConstants.REDRAW_PERIOD);

      // ----------------------------
      selenium
         .type(
            "scLocator=//DynamicForm[ID=\"ideSaveAsTemplateFormDynamicForm\"]/item[name=ideSaveAsTemplateFormNameField||title=ideSaveAsTemplateFormNameField||index=3||Class=TextItem]/element",
            "test template");
      // --------3--------------
      selenium.click("scLocator=//IButton[ID=\"ideSaveAsTemplateFormSaveButton\"]");
      // --------4-------------
      // contol info dialog created
      Thread.sleep(TestConstants.EDITOR_OPEN_PERIOD);
      assertTrue(selenium.isElementPresent("scLocator=//Dialog[ID=\"isc_globalWarn\"]"));
      assertTrue(selenium.isTextPresent("Info"));
      assertTrue(selenium.isTextPresent("Template created successfully!"));
      // --------5-------------
      Thread.sleep(TestConstants.EDITOR_OPEN_PERIOD);
      selenium.click("scLocator=//Dialog[ID=\"isc_globalWarn\"]/okButton");
      Thread.sleep(TestConstants.EDITOR_OPEN_PERIOD);
      // --------6--------
      runCommandFromMenuNewOnToolbar(MenuCommands.New.FILE_FROM_TEMPLATE);
      Thread.sleep(TestConstants.PAGE_LOAD_PERIOD);
      TemplateUtils.checkCreateFileFromTemplateWindow(selenium);
      Thread.sleep(TestConstants.REDRAW_PERIOD);

//      TemplateUtils.selectFileInTemplateListForTempletaOperationTets(selenium, "test template");
      selenium.click("scLocator=//IButton[ID=\"ideCreateFileFromTemplateFormDeleteButton\"]/");
      Thread.sleep(TestConstants.PAGE_LOAD_PERIOD);

      // control Ideall delete template dialog
      assertTrue(selenium.isElementPresent("scLocator=//Dialog[ID=\"isc_globalWarn\"]/"));
      assertTrue(selenium.isElementPresent("scLocator=//Dialog[ID=\"isc_globalWarn\"]/noButton/"));
      assertTrue(selenium.isElementPresent("scLocator=//Dialog[ID=\"isc_globalWarn\"]/yesButton/"));

      selenium.click("scLocator=//Dialog[ID=\"isc_globalWarn\"]/yesButton/");

      Thread.sleep(TestConstants.EDITOR_OPEN_PERIOD);

      //      // -------10-------
      selenium.click("scLocator=//IButton[ID=\"ideCreateFileFromTemplateFormCancelButton\"]/");
      Thread.sleep(TestConstants.EDITOR_OPEN_PERIOD);
      chekDeleteTemplateForm(selenium);
      closeTab("0");
      Thread.sleep(TestConstants.REDRAW_PERIOD);
   }

   //--------------------------------------------------------------------------------------------------------------------------------------
   /**
   * 
   * 
   * @throws Exception
   */
   @Test
   public void testCreateFileFromTemplateTest() throws Exception

   {
      Thread.sleep(TestConstants.SLEEP);
      //---------1--------
      runTopMenuCommand(MenuCommands.File.FILE, MenuCommands.File.REFRESH);
      Thread.sleep(TestConstants.REDRAW_PERIOD);
      runCommandFromMenuNewOnToolbar(MenuCommands.New.FILE_FROM_TEMPLATE);
      Thread.sleep(TestConstants.REDRAW_PERIOD);
      //----------2,3------
      createFileFromTemplate(MenuCommands.New.GROOVY_TEMPLATE_FILE, "Sample of Template.");

      //--------4-------
      selectItemInWorkspaceTree(TEST_FOLDER);
      Thread.sleep(TestConstants.REDRAW_PERIOD);
      saveAsUsingToolbarButton("Groovy File.groovy");
      Thread.sleep(TestConstants.REDRAW_PERIOD);
      //---------5-------
      Thread.sleep(TestConstants.REDRAW_PERIOD);
      //openOrCloseFolder(TEST_FOLDER);
      selectItemInWorkspaceTree("Groovy File.groovy");
      Thread.sleep(TestConstants.REDRAW_PERIOD);

      // --------repeat_for_Emty_HTML---------
      Thread.sleep(TestConstants.SLEEP);
      //---------1--------
      runCommandFromMenuNewOnToolbar(MenuCommands.New.FILE_FROM_TEMPLATE);

      Thread.sleep(TestConstants.REDRAW_PERIOD);
      //----------2,3------
      selectItemInTemplateListForItsTest(selenium, "Empty HTML");
      selenium.click("scLocator=//IButton[ID=\"ideCreateFileFromTemplateFormCreateButton\"]/");
      Thread.sleep(TestConstants.PAGE_LOAD_PERIOD);
      //--------4-------
      selectItemInWorkspaceTree(TEST_FOLDER);
      Thread.sleep(TestConstants.REDRAW_PERIOD);
      saveAsUsingToolbarButton("HTMLFile.html");
      Thread.sleep(TestConstants.REDRAW_PERIOD);
      //---------5-------

      selectItemInWorkspaceTree("HTMLFile.html");
      Thread.sleep(TestConstants.REDRAW_PERIOD);

      //      // --------repeat_for_Emty_Text---------
      //      Thread.sleep(TestConstants.SLEEP);
      //---------1--------
      runCommandFromMenuNewOnToolbar(MenuCommands.New.FILE_FROM_TEMPLATE);

      Thread.sleep(TestConstants.REDRAW_PERIOD);
      //----------2,3------
      selectItemInTemplateListForItsTest(selenium, "Empty TEXT");
      selenium.click("scLocator=//IButton[ID=\"ideCreateFileFromTemplateFormCreateButton\"]/");
      Thread.sleep(TestConstants.PAGE_LOAD_PERIOD);
      //--------4-------
      selectItemInWorkspaceTree(TEST_FOLDER);
      Thread.sleep(TestConstants.REDRAW_PERIOD);
      saveAsUsingToolbarButton("TextFile.txt");
      Thread.sleep(TestConstants.REDRAW_PERIOD);
      //---------5-------

      selectItemInWorkspaceTree("TextFile.txt");
      Thread.sleep(TestConstants.REDRAW_PERIOD);
      selectItemInWorkspaceTree(TEST_FOLDER);

      selectItemInWorkspaceTree("Groovy File.groovy");
      selenium.shiftKeyDown();
      selectItemInWorkspaceTree("TextFile.txt");
      selenium.shiftKeyUp();
      Thread.sleep(TestConstants.REDRAW_PERIOD);
      deleteSelectedItems();
      Thread.sleep(TestConstants.EDITOR_OPEN_PERIOD);
   }

   //-----------------------------------------------------------------------------------------------
   /**
   * 
   * 
   * @throws Exception
   */
   @Test
   public void testSaveFileAsFromTemplate() throws Exception
   {
      {

         //-------------1------------
         Thread.sleep(TestConstants.SLEEP);
         runCommandFromMenuNewOnToolbar(MenuCommands.New.REST_SERVICE_FILE);
         Thread.sleep(TestConstants.EDITOR_OPEN_PERIOD);
         typeTextIntoEditor(0, "// test groovy file template");
         selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_ENTER);
         runTopMenuCommand(MenuCommands.File.FILE, MenuCommands.File.SAVE_AS_TEMPLATE);
         Thread.sleep(TestConstants.REDRAW_PERIOD);
         TemplateUtils.checkSaveAsTemplateWindow(selenium);
         Thread.sleep(TestConstants.REDRAW_PERIOD);
         // ----------------------------
         selenium
            .type(
               "scLocator=//DynamicForm[ID=\"ideSaveAsTemplateFormDynamicForm\"]/item[name=ideSaveAsTemplateFormNameField||title=ideSaveAsTemplateFormNameField||index=3||Class=TextItem]/element",
               "test groovy template");
         // --------2--------------
         selenium.click("scLocator=//IButton[ID=\"ideSaveAsTemplateFormSaveButton\"]");
         Thread.sleep(TestConstants.EDITOR_OPEN_PERIOD);
         assertTrue(selenium.isElementPresent("scLocator=//Dialog[ID=\"isc_globalWarn\"]"));
         assertTrue(selenium.isTextPresent("Info"));
         assertTrue(selenium.isTextPresent("Template created successfully!"));
         // --------3-------------
         Thread.sleep(TestConstants.EDITOR_OPEN_PERIOD);
         selenium.click("scLocator=//Dialog[ID=\"isc_globalWarn\"]/okButton");
         Thread.sleep(TestConstants.EDITOR_OPEN_PERIOD);

         closeUnsavedFileAndDoNotSave("0");
         Thread.sleep(TestConstants.REDRAW_PERIOD);
         runCommandFromMenuNewOnToolbar(MenuCommands.New.FILE_FROM_TEMPLATE);
         Thread.sleep(TestConstants.PAGE_LOAD_PERIOD);
         TemplateUtils.checkCreateFileFromTemplateWindow(selenium);
         Thread.sleep(TestConstants.REDRAW_PERIOD);
         selectItemInTemplateListForItsTest(selenium, "test groovy template");
         selenium.click("scLocator=//IButton[ID=\"ideCreateFileFromTemplateFormCreateButton\"]/");
         Thread.sleep(TestConstants.PAGE_LOAD_PERIOD);
         Thread.sleep(1000);
         checkIsTabPresentInEditorTabset("Untitled file.groovy", true);
         selectItemInWorkspaceTree(TEST_FOLDER);
         saveAsUsingToolbarButton("Groovy File.groovy");
         Thread.sleep(TestConstants.REDRAW_PERIOD);
         //   openOrCloseFolder(TEST_FOLDER);
         selectItemInWorkspaceTree("Groovy File.groovy");

         //         // --------1----------
         //         Thread.sleep(1000);
         //         selenium
         //            .open("http://127.0.0.1:8888/org.exoplatform.ide.IDEApplication/IDEApplication.html?gwt.codesvr=127.0.0.1:9997");
         //         Thread.sleep(15000);
         //         // --------2-------
         //         selenium.mouseDownAt("//div[@title='New']//img", "");
         //         selenium.mouseUpAt("//div[@title='New']//img", "");
         //         Thread.sleep(1000);
         //         selenium.mouseDownAt("//td[@class='exo-popupMenuTitleField']/nobr[contains(text(), \"REST Service\")]", "");
         //         // ---------3--------
         //         Thread.sleep(1000);
         //         selenium.typeKeys("//body[@class='editbox']", "// test groovY file template");
         //         Thread.sleep(1000);
         //         selenium.mouseDownAt("//td[@class='exo-menuBarItem' and @menubartitle='File']", "");
         //         selenium.mouseDownAt("//td[@class='exo-popupMenuTitleField']/nobr[contains(text(), \"Save As Template...\")]",
         //            "");
         //         // control dialog form Save file as template window
         //         assertTrue(selenium.isElementPresent("scLocator=//Window[ID=\"ideSaveAsTemplateForm\"]/header/"));
         //         assertTrue(selenium.isElementPresent("scLocator=//IButton[ID=\"ideSaveAsTemplateFormCancelButton\"]/"));
         //         assertTrue(selenium.isElementPresent("scLocator=//IButton[ID=\"ideSaveAsTemplateFormSaveButton\"]/"));
         //         assertTrue(selenium.isTextPresent("Type:"));
         //         assertTrue(selenium.isTextPresent("Name:"));
         //         assertTrue(selenium.isTextPresent("Description:"));
         //         selenium
         //            .click("scLocator=//DynamicForm[ID=\"ideSaveAsTemplateFormDynamicForm\"]/item[name=ideSaveAsTemplateFormNameField||title=ideSaveAsTemplateFormNameField||Class=TextItem]/element");
         //         selenium
         //            .type(
         //               "scLocator=//DynamicForm[ID=\"ideSaveAsTemplateFormDynamicForm\"]/item[name=ideSaveAsTemplateFormNameField||title=ideSaveAsTemplateFormNameField||Class=TextItem]/element",
         //               "test groovy template");
         //         selenium
         //            .click("scLocator=//DynamicForm[ID=\"ideSaveAsTemplateFormDynamicForm\"]/item[name=ideSaveAsTemplateFormDescriptionField||title=ideSaveAsTemplateFormDescriptionField||Class=TextAreaItem]/element");
         //         selenium
         //            .type(
         //               "scLocator=//DynamicForm[ID=\"ideSaveAsTemplateFormDynamicForm\"]/item[name=ideSaveAsTemplateFormDescriptionField||title=ideSaveAsTemplateFormDescriptionField||Class=TextAreaItem]/element",
         //               "test groovy template description");
         //         selenium.click("scLocator=//IButton[ID=\"ideSaveAsTemplateFormSaveButton\"]/");
         //         // control dialog create dialog info
         //         Thread.sleep(1000);
         //         assertTrue(selenium.isElementPresent("scLocator=//Dialog[ID=\"isc_globalWarn\"]/header/"));
         //         assertTrue(selenium.isElementPresent("scLocator=//Dialog[ID=\"isc_globalWarn\"]/okButton/"));
         //         assertTrue(selenium.isTextPresent("Template created successfully!"));
         //         selenium.click("scLocator=//Dialog[ID=\"isc_globalWarn\"]/okButton/");
         //         // ------------5----------
         //         selenium.mouseDownAt("//div[@title='New']//img", "");
         //         selenium.mouseUpAt("//div[@title='New']//img", "");
         //         Thread.sleep(1000);
         //         selenium
         //            .mouseDownAt("//td[@class='exo-popupMenuTitleField']/nobr[contains(text(), \"From Template...\")]", "");
         //         // control create "Create file dialog window"
         //         Thread.sleep(1000);
         //         assertTrue(selenium
         //            .isElementPresent("scLocator=//Window[ID=\"ideCreateFileFromTemplateForm\"]/header/member[Class=Canvas||index=0||length=3||classIndex=0||classLength=1]/"));
         //         assertTrue(selenium
         //            .isElementPresent("scLocator=//Window[ID=\"ideCreateFileFromTemplateForm\"]/header/member[Class=Canvas||index=0||length=3||classIndex=0||classLength=1]/"));
         //         assertTrue(selenium.isElementPresent("scLocator=//IButton[ID=\"ideCreateFileFromTemplateFormCancelButton\"]"));
         //         assertTrue(selenium.isTextPresent("Name"));
         //         assertTrue(selenium.isTextPresent("Description"));
         //         assertTrue(selenium.isTextPresent("test groovy template"));
         //         assertTrue(selenium.isTextPresent("test groovy template description"));
         //         assertTrue(selenium.isTextPresent("File Name"));
         //         // --------6--------
         //         // control first line in eclipse
         //         selenium
         //            .click("scLocator=//ListGrid[ID=\"ideCreateFileFromTemplateFormTemplateListGrid\"]/body/row[6]/col[fieldName=description||2]");
         //         selenium
         //            .click("scLocator=//DynamicForm[ID=\"ideCreateFileFromTemplateFormDynamicForm\"]/item[name=ideCreateFileFromTemplateFormFileNameField||Class=TextItem]/element");
         //         selenium
         //            .type(
         //               "scLocator=//DynamicForm[ID=\"ideCreateFileFromTemplateFormDynamicForm\"]/item[name=ideCreateFileFromTemplateFormFileNameField||Class=TextItem]/element",
         //               "Test Groovy File.groovy");
         //         selenium.click("scLocator=//IButton[ID=\"ideCreateFileFromTemplateFormCreateButton\"]/end");
         //         Thread.sleep(1000);
         //         // --------7-------
         //         selenium.mouseDownAt("//div[@title='Save As...']//img", "");
         //         selenium.mouseUpAt("//div[@title='Save As...']//img", "");
         //         assertTrue(selenium
         //            .isElementPresent("scLocator=//Window[ID=\"ideAskForValueDialog\"]/header/member[Class=Canvas||index=0||length=2||classIndex=0||classLength=1]/"));
         //         assertTrue(selenium.isElementPresent("scLocator=//IButton[ID=\"ideAskForValueDialogOkButton\"]/"));
         //         assertTrue(selenium.isElementPresent("scLocator=//IButton[ID=\"ideAskForValueDialogCancelButton\"]/"));
         //         assertTrue(selenium.isTextPresent("Enter new file name:"));
         //         selenium.click("scLocator=//IButton[ID=\"ideAskForValueDialogOkButton\"]/");
         //         Thread.sleep(1000);
         //         assertTrue(selenium.isTextPresent("Test Groovy File.groovy"));
         //         // ----------8---------
         //         selenium.click("scLocator=//TreeGrid[ID=\"ideNavigatorItemTreeGrid\"]/body/row[1]/col[fieldName=name||0]");
         //         selenium.mouseDownAt("//div[@title='Delete Item(s)...']//img", "");
         //         selenium.mouseUpAt("//div[@title='Delete Item(s)...']//img", "");
         //         for (int second = 0;; second++)
         //         {
         //            if (second >= 60)
         //               fail("timeout");
         //            try
         //            {
         //               if (selenium.isElementPresent("scLocator=//Window[ID=\"ideDeleteItemForm\"]/"))
         //                  break;
         //            }
         //            catch (Exception e)
         //            {
         //            }
         //            Thread.sleep(1000);
         //         }
         //
         //         assertTrue(selenium.isElementPresent("scLocator=//IButton[ID=\"ideDeleteItemFormOkButton\"]/"));
         //         assertTrue(selenium.isElementPresent("scLocator=//IButton[ID=\"ideDeleteItemFormCancelButton\"]/"));
         //         Thread.sleep(1000);
         //         selenium.click("scLocator=//Window[ID=\"ideDeleteItemForm\"]/header/");
         //         selenium.click("scLocator=//IButton[ID=\"ideDeleteItemFormOkButton\"]/");
         //         Thread.sleep(2000);
         //         selenium.click("scLocator=//TabSet[ID=\"ideEditorFormTabSet\"]/tab[index=0]/icon");
         //         // closeall
         //         selenium.click("scLocator=//TabSet[ID=\"ideEditorFormTabSet\"]/tab[index=0]/icon");
         //         Thread.sleep(3000);
         //         selenium.click("scLocator=//Dialog[ID=\"isc_globalWarn\"]/noButton/");
         //         selenium.click("scLocator=//TabSet[ID=\"ideEditorFormTabSet\"]/tab[index=0]/icon");
         //         Thread.sleep(3000);
         //         selenium.click("scLocator=//Dialog[ID=\"isc_globalWarn\"]/noButton/");
         //         selenium.click("scLocator=//TabSet[ID=\"ideEditorFormTabSet\"]/tab[index=0]/icon");

      }
   }

   public void selectItemInTemplateListForItsTest(Selenium selenium, String templateName) throws Exception
   {
      assertTrue(selenium.isElementPresent("//div[@class='windowBody']//table[@class='listTable']//nobr/span[@title='"
         + templateName + "']"));
      selenium.mouseDownAt("//div[@class='windowBody']//table[@class='listTable']//nobr/span[@title='" + templateName
         + "']", "2,2");
      selenium.mouseUpAt("//div[@class='windowBody']//table[@class='listTable']//nobr/span[@title='" + templateName
         + "']", "2,2");
      Thread.sleep(TestConstants.REDRAW_PERIOD);
   }

   //--------------------------------------------------------------
   public void chekTemplateForm(Selenium selenium)
   {
      {
         assertTrue(selenium.isElementPresent("scLocator=//Window[ID=\"ideCreateFileFromTemplateForm\"]"));
         assertEquals("Create file",
            selenium.getText("scLocator=//Window[ID=\"ideCreateFileFromTemplateForm\"]/header"));
         assertTrue(selenium
            .isElementPresent("scLocator=//DynamicForm[ID=\"ideCreateFileFromTemplateFormDynamicForm\"]/item[name=ideCreateFileFromTemplateFormFileNameField]/element"));
         assertTrue(selenium.isElementPresent("scLocator=//IButton[ID=\"ideCreateFileFromTemplateFormDeleteButton\"]/"));
         assertTrue(selenium.isElementPresent("scLocator=//IButton[ID=\"ideCreateFileFromTemplateFormCreateButton\"]/"));
         assertTrue(selenium.isElementPresent("scLocator=//IButton[ID=\"ideCreateFileFromTemplateFormCancelButton\"]/"));
         //check that Delete and Create buttons are disabled and Cancel is enabled
         assertTrue(selenium
            .isElementPresent("//div[@class='windowBody']//td[@class='buttonTitleDisabled']/table//td[text()='Delete']"));
         assertFalse(selenium
            .isElementPresent("//div[@class='windowBody']//td[@class='buttonTitleDisabled']/table//td[text()='Create']"));
         assertTrue(selenium
            .isElementPresent("//div[@class='windowBody']//td[@class='buttonTitle']/table//td[text()='Cancel']"));
         //assert templates present
         assertTrue(selenium
            .isElementPresent("//div[@class='windowBody']//table[@class='listTable']//nobr/span[@title='"
               + "Groovy REST Service" + "']"));
         assertTrue(selenium
            .isElementPresent("//div[@class='windowBody']//table[@class='listTable']//nobr/span[@title='" + "Empty XML"
               + "']"));
         assertTrue(selenium
            .isElementPresent("//div[@class='windowBody']//table[@class='listTable']//nobr/span[@title='"
               + "Empty HTML" + "']"));
         assertTrue(selenium
            .isElementPresent("//div[@class='windowBody']//table[@class='listTable']//nobr/span[@title='"
               + "Empty TEXT" + "']"));
         assertTrue(selenium
            .isElementPresent("//div[@class='windowBody']//table[@class='listTable']//nobr/span[@title='"
               + "Google Gadget" + "']"));
      }
   }

   public void chekDeleteTemplateForm(Selenium selenium)
   {
      {
         assertFalse(selenium.isElementPresent("scLocator=//Window[ID=\"ideCreateFileFromTemplateForm\"]"));
         assertFalse(selenium
            .isElementPresent("scLocator=//DynamicForm[ID=\"ideCreateFileFromTemplateFormDynamicForm\"]/item[name=ideCreateFileFromTemplateFormFileNameField]/element"));
         assertFalse(selenium
            .isElementPresent("scLocator=//IButton[ID=\"ideCreateFileFromTemplateFormDeleteButton\"]/"));
         assertFalse(selenium
            .isElementPresent("scLocator=//IButton[ID=\"ideCreateFileFromTemplateFormCreateButton\"]/"));
         assertFalse(selenium
            .isElementPresent("scLocator=//IButton[ID=\"ideCreateFileFromTemplateFormCancelButton\"]/"));
         //check that Delete and Create buttons are disabled and Cancel is enabled
         assertFalse(selenium
            .isElementPresent("//div[@class='windowBody']//td[@class='buttonTitleDisabled']/table//td[text()='Delete']"));
         assertFalse(selenium
            .isElementPresent("//div[@class='windowBody']//td[@class='buttonTitleDisabled']/table//td[text()='Create']"));
         assertFalse(selenium
            .isElementPresent("//div[@class='windowBody']//td[@class='buttonTitle']/table//td[text()='Cancel']"));
         //assert templates present
         assertFalse(selenium
            .isElementPresent("//div[@class='windowBody']//table[@class='listTable']//nobr/span[@title='"
               + "Groovy REST Service" + "']"));
         assertFalse(selenium
            .isElementPresent("//div[@class='windowBody']//table[@class='listTable']//nobr/span[@title='" + "Empty XML"
               + "']"));
         assertFalse(selenium
            .isElementPresent("//div[@class='windowBody']//table[@class='listTable']//nobr/span[@title='"
               + "Empty HTML" + "']"));
         assertFalse(selenium
            .isElementPresent("//div[@class='windowBody']//table[@class='listTable']//nobr/span[@title='"
               + "Empty TEXT" + "']"));
         assertFalse(selenium
            .isElementPresent("//div[@class='windowBody']//table[@class='listTable']//nobr/span[@title='"
               + "Google Gadget" + "']"));

      }

   }

   @AfterClass
   public static void tearDown()
   {
      try
      {
         cleanRegistry();
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

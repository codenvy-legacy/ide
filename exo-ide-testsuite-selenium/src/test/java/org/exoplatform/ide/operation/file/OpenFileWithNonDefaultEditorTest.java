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
package org.exoplatform.ide.operation.file;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.exoplatform.common.http.client.ModuleException;
import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.EnumBrowserCommand;
import org.exoplatform.ide.MenuCommands;
import org.exoplatform.ide.TestConstants;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Created by The eXo Platform SAS.
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id:
 *
 */
public class OpenFileWithNonDefaultEditorTest extends BaseTest
{
   //TODO: doesn't work on windows, because double click is used
   //IDE-109 Open file with non-default editor.
   
   private static String FOLDER_NAME =OpenFileWithNonDefaultEditorTest.class.getSimpleName() ;
   
   private final static String PATH = "src/test/resources/org/exoplatform/ide/operation/file/";
   
   private final static String URL = BASE_URL + REST_CONTEXT + "/" + WEBDAV_CONTEXT + "/" + REPO_NAME + "/" + WS_NAME + "/" + FOLDER_NAME + "/";
   
   private static String HTML_FILE_NAME = "newHtmlFile.html";

   private static String GOOGLE_GADGET_FILE_NAME = "Calculator.xml";

   private static String CUR_TIME = String.valueOf(System.currentTimeMillis());   
   
   @BeforeClass
   public static void setUp()
   {
      
      try
      {
         VirtualFileSystemUtils.mkcol(URL);
         VirtualFileSystemUtils.put(PATH + HTML_FILE_NAME, MimeType.TEXT_HTML, URL + CUR_TIME + HTML_FILE_NAME);
         VirtualFileSystemUtils.put(PATH + GOOGLE_GADGET_FILE_NAME, MimeType.GOOGLE_GADGET, URL + CUR_TIME +  GOOGLE_GADGET_FILE_NAME);
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
   
   @Ignore
   @Test
   public void testOpenFileWithNonDefaultEditor() throws Exception
   {      
      //---- 2 -------------------------
      //Select file newHtmlFile.html in the Workspace Panel and then call 
      //the "File->Open with.." topmenu command.
      Thread.sleep(TestConstants.SLEEP);
      selectItemInWorkspaceTree(WS_NAME);
      IDE.menu().runCommand(MenuCommands.File.FILE, MenuCommands.File.REFRESH);
//      Thread.sleep(TestConstants.SLEEP);
      
      openOrCloseFolder(FOLDER_NAME);
      Thread.sleep(TestConstants.SLEEP);
      callOpenWithWindow(CUR_TIME + HTML_FILE_NAME);
      //gadget displayed "Open File with" dialog window with second items in the central column: 
      //"Code Editor [Default]" and "WYSYWYG editor".
      checkOpenWithWindowCodeEditorIsDefault();
      
      //---- 3 -------------------------
      //Select "WYSYWYG editor" item and then click on "Open" button.
      selectEditorAndOpen(MenuCommands.CodeEditors.CK_EDITOR, false);
      Thread.sleep(TestConstants.SLEEP);
      //gadget opened newHtmlFile.html file in the Content Panel in the WYSYWYG editor.
      checkCkEditorOpened(0);
      Thread.sleep(TestConstants.SLEEP);
      
      //---- 4 -------------------------
      //Select file newHtmlFile.html in the Workspace Panel again, 
      //then call the "File->Open with.." topmenu command, 
      //select "Code Editor" item and click on "Open" button.
      callOpenWithWindow(CUR_TIME + HTML_FILE_NAME);
      checkOpenWithWindowCodeEditorIsDefault();
      selectEditorAndOpen(MenuCommands.CodeEditors.CODE_MIRROR, false);
      Thread.sleep(TestConstants.SLEEP);
      
      //gadget displayed confirmation dialog with message "Do you want to reopen file newHtmlFile.html 
      //in selected editor?"
      //---- 5 -------------------------
      //Click on "No" button.
      checkReopenWarningDialog(false);
      Thread.sleep(TestConstants.SLEEP);
      
      //---- 6 -------------------------
      //Select file newHtmlFile.html in the Workspace Panel again, then call the "File->Open with.." 
      //topmenu command, double click on "Code Editor" item, then click "Yes" in "Info" dialog window.
      callOpenWithWindow(CUR_TIME + HTML_FILE_NAME);
      Thread.sleep(TestConstants.SLEEP);
      checkOpenWithWindowCodeEditorIsDefault();
      selectEditorAndOpen(MenuCommands.CodeEditors.CODE_MIRROR, false);
      Thread.sleep(TestConstants.SLEEP);
      checkReopenWarningDialog(true);
      Thread.sleep(TestConstants.SLEEP);
      //gadget should reopen file newHtmlFile.html new tab in the Code Editor with appropriate highlighting.
      checkCodeEditorOpened(0);
      Thread.sleep(TestConstants.SLEEP);

      //---- 7 -------------------------
      //Close file newHtmlFile.html, select this one in the Workspace Panel again, 
      //then call the "File->Open with.." topmenu command, select "WYSYWYG editor" item, 
      //check box "Use as default editor" and click "Open" button.
      closeFileTab(0);
      callOpenWithWindow(CUR_TIME + HTML_FILE_NAME);
      checkOpenWithWindowCodeEditorIsDefault();
      selectEditorAndOpen(MenuCommands.CodeEditors.CK_EDITOR, true);
      Thread.sleep(TestConstants.SLEEP);
      //gadget opened newHtmlFile.html file in the Content Panel in the WYSYWYG editor.
      checkCkEditorOpened(0);
      Thread.sleep(TestConstants.SLEEP);
      
      //---- 8 -------------------------
      //Close file newHtmlFile.html, and open this one in Content panel. 
      //Then call the "File->New->HTML file" topmenu command.
      closeFileTab(0);

      // Doubleclick doesn't work under the Firefox in the Windows
      if (isRunTestUnderWindowsOS() && BROWSER_COMMAND.equals(EnumBrowserCommand.FIREFOX))
      {
         // restore CodeEditor as default
         callOpenWithWindow(CUR_TIME + HTML_FILE_NAME);
         selectEditorAndOpen(MenuCommands.CodeEditors.CK_EDITOR, true);
         Thread.sleep(TestConstants.SLEEP);         
       
         return;
      }
      
      doubleClickItemInNavigationTree(CUR_TIME + HTML_FILE_NAME);
      Thread.sleep(TestConstants.SLEEP);
      checkCkEditorOpened(0);
      IDE.toolbar().runCommandFromNewPopupMenu(MenuCommands.New.HTML_FILE);
      Thread.sleep(TestConstants.SLEEP);
      //file newHtmlFile.html and new HTML-file should be opened only in the WYSYWYG editor.
      checkCkEditorOpened(0);
      checkCkEditorOpened(1);
      
      //---- 9 -------------------------
      //Refresh browser with opened gadget window
      selenium.refresh();
      selenium.waitForPageToLoad("30000");
      Thread.sleep(TestConstants.PAGE_LOAD_PERIOD);
      //new file should be closed, and file newHtmlFile.html should be opened in the WYSYWYG editor.
      checkCkEditorOpened(0);
//      assertEquals(CUR_TIME + HTML_FILE_NAME, getTabTitle(0));
      
      //---- 10 -------------------------
      //Reopen file newHtmlFile.html.
      openOrCloseFolder(FOLDER_NAME);
      doubleClickItemInNavigationTree(CUR_TIME + HTML_FILE_NAME);
      Thread.sleep(TestConstants.SLEEP);
      checkCkEditorOpened(0);
      
      //step 11
      closeFileTab(0);
      callOpenWithWindow(CUR_TIME + HTML_FILE_NAME);
      checkOpenWithWindowCkEditorIsDefault();
      selectEditorAndOpen(MenuCommands.CodeEditors.CODE_MIRROR, true);
      Thread.sleep(TestConstants.SLEEP);
      checkCodeEditorOpened(0);
      
      //step 12
      closeFileTab(0);
      doubleClickItemInNavigationTree(CUR_TIME + HTML_FILE_NAME);  // TODO doesn't work under the Linux
      Thread.sleep(TestConstants.SLEEP);
      checkCodeEditorOpened(0);
      
      //step 13
      selenium.refresh();
      selenium.waitForPageToLoad(TestConstants.IDE_LOAD_PERIOD+"");
      Thread.sleep(TestConstants.PAGE_LOAD_PERIOD);
      closeFileTab(0);
      openOrCloseFolder(FOLDER_NAME);
      doubleClickItemInNavigationTree(CUR_TIME + HTML_FILE_NAME);
      Thread.sleep(TestConstants.SLEEP);
      checkCodeEditorOpened(0);
      closeFileTab(0);
      
      //step 14
      //open gadget file with WYSWYG Editor
      //step 2 for gadget file
      callOpenWithWindow(CUR_TIME + GOOGLE_GADGET_FILE_NAME);
      checkOpenWithWindowCodeEditorIsDefault();
      //step 3
      selectEditorAndOpen(MenuCommands.CodeEditors.CK_EDITOR, false);
      Thread.sleep(TestConstants.SLEEP);
      checkCkEditorOpened(0);
      Thread.sleep(TestConstants.SLEEP);
      closeFileTab(0);      
   }
   
   private void closeFileTab(int tabIndex) throws Exception
   {
      IDE.editor().closeTab(tabIndex);

      //check is warning dialog appears
      if (selenium.isElementPresent(
         "scLocator=//Dialog[ID=\"isc_globalWarn\"]/header[contains(text(), 'Close file')]"))
      {
         //click No button
         selenium.click("scLocator=//Dialog[ID=\"isc_globalWarn\"]/noButton/");
         Thread.sleep(TestConstants.SLEEP);
      }
   }
   
   private void callOpenWithWindow(String fileName) throws Exception
   {
      //select file
      selenium.click("scLocator=//TreeGrid[ID=\"ideNavigatorItemTreeGrid\"]/body/row[name=" + fileName + "]/col[1]");
      Thread.sleep(TestConstants.SLEEP_SHORT);
      
      IDE.menu().runCommand(MenuCommands.File.FILE, MenuCommands.File.OPEN_WITH);
      
//      //call Open With window
//      selenium.mouseDownAt("//td[@class='exo-menuBarItem' and @menubartitle='File']", "");
//      Thread.sleep(TestConstants.SLEEP);
//      selenium.mouseDownAt("//td[@class='exo-popupMenuTitleField']/nobr[text()='"
//         + MenuCommands.File.OPEN_WITH + "']", "");
//      Thread.sleep(TestConstants.SLEEP);
   }
   
   private void checkOpenWithWindowCodeEditorIsDefault() throws Exception
   {
      //check form
      assertTrue(selenium.isElementPresent("scLocator=//Window[ID=\"ideallOpenFileWithForm\"]"));
      assertEquals("Open File With", selenium.getText("scLocator=//Window[ID=\"ideallOpenFileWithForm\"]/header"));
      //check rows in list grid
      assertTrue(selenium.isElementPresent("scLocator=//Window[ID=\"ideallOpenFileWithForm\"]/item[0][Class=\"ListGrid\"]/body/row[0]/col[fieldName=name||0]"));
      assertTrue(selenium.isElementPresent("scLocator=//Window[ID=\"ideallOpenFileWithForm\"]/item[0][Class=\"ListGrid\"]/body/row[1]/col[fieldName=name||0]"));
      assertEquals("Code Editor [Default]", selenium.getText("//table[@class='listTable']//nobr[contains(text(), 'Code Editor')]"));
      assertEquals("WYSWYG Editor", selenium.getText("//table[@class='listTable']//nobr[contains(text(), 'WYSWYG Editor')]"));
      //check Use by default checkbox
      assertTrue(selenium.isElementPresent("scLocator=//Window[ID=\"ideallOpenFileWithForm\"]/item[1][Class=\"DynamicForm\"]/item[name=Default]/textbox"));
      assertEquals("Use as default editor", selenium.getText("scLocator=//Window[ID=\"ideallOpenFileWithForm\"]/item[1][Class=\"DynamicForm\"]/"));
      //check buttons
      assertTrue(selenium.isElementPresent("scLocator=//IButton[ID=\"ideOpenFileWithOkButton\"]"));
      assertTrue(selenium.isElementPresent("scLocator=//IButton[ID=\"ideOpenFileWithCancelButton\"]"));
      assertTrue(selenium.isElementPresent("//div[@class='windowBody']//td[@class='buttonTitleDisabled']//td[@class='buttonTitleDisabled' and text()='Open']"));
      assertTrue(selenium.isElementPresent("//div[@class='windowBody']//td[@class='buttonTitle']//td[@class='buttonTitle' and text()='Cancel']"));
   }
   
   private void checkOpenWithWindowCkEditorIsDefault() throws Exception
   {
      //check form
      assertTrue(selenium.isElementPresent("scLocator=//Window[ID=\"ideallOpenFileWithForm\"]"));
      assertEquals("Open File With", selenium.getText("scLocator=//Window[ID=\"ideallOpenFileWithForm\"]/header"));
      //check rows in list grid
      assertTrue(selenium.isElementPresent("scLocator=//Window[ID=\"ideallOpenFileWithForm\"]/item[0][Class=\"ListGrid\"]/body/row[0]/col[fieldName=name||0]"));
      assertTrue(selenium.isElementPresent("scLocator=//Window[ID=\"ideallOpenFileWithForm\"]/item[0][Class=\"ListGrid\"]/body/row[1]/col[fieldName=name||0]"));
      assertEquals("Code Editor", selenium.getText("//table[@class='listTable']//nobr[contains(text(), 'Code Editor')]"));
      assertEquals("WYSWYG Editor [Default]", selenium.getText("//table[@class='listTable']//nobr[contains(text(), 'WYSWYG Editor')]"));
      //check Use by default checkbox
      assertTrue(selenium.isElementPresent("scLocator=//Window[ID=\"ideallOpenFileWithForm\"]/item[1][Class=\"DynamicForm\"]/item[name=Default]/textbox"));
      assertEquals("Use as default editor", selenium.getText("scLocator=//Window[ID=\"ideallOpenFileWithForm\"]/item[1][Class=\"DynamicForm\"]/"));
      //check buttons
      assertTrue(selenium.isElementPresent("scLocator=//IButton[ID=\"ideOpenFileWithOkButton\"]"));
      assertTrue(selenium.isElementPresent("scLocator=//IButton[ID=\"ideOpenFileWithCancelButton\"]"));
      assertTrue(selenium.isElementPresent("//div[@class='windowBody']//td[@class='buttonTitleDisabled']//td[@class='buttonTitleDisabled' and text()='Open']"));
      assertTrue(selenium.isElementPresent("//div[@class='windowBody']//td[@class='buttonTitle']//td[@class='buttonTitle' and text()='Cancel']"));
   }
   
   private static void selectEditorAndOpen(String editor, boolean clickUseDefaultCheckbox) throws Exception
   {
      //select editor
      selenium.mouseDownAt("//nobr[contains(text(), '" + editor + "')]", "");
      //check Open button enabled
      assertTrue(selenium.isElementPresent("//div[@class='windowBody']//td[@class='buttonTitle']//td[@class='buttonTitle' and text()='Open']"));
      
      if (clickUseDefaultCheckbox)
      {
         //click on checkbox
         selenium.click("scLocator=//Window[ID=\"ideallOpenFileWithForm\"]/item[1][Class=\"DynamicForm\"]/item[name=Default]/textbox");
      }
      //click Ok button
      selenium.click("scLocator=//IButton[ID=\"ideOpenFileWithOkButton\"]");
   }
   
 /*  private void selectEditorAndOpenByDoubleClick(String editor, boolean clickOnUseDefaultCheckbox) throws Exception
   {
      if (clickOnUseDefaultCheckbox)
      {
         selenium.click("scLocator=//Window[ID=\"ideallOpenFileWithForm\"]/item[1][Class=\"DynamicForm\"]/item[name=Default]/textbox");
      }
      //open
      selenium.mouseDownAt("//nobr[contains(text(), '" + editor + "')]", "");
      selenium.doubleClick("//nobr[contains(text(), '" + editor + "')]");
   }*/
   
   private void checkReopenWarningDialog(boolean clickYes) throws Exception
   {
      assertTrue(selenium.isElementPresent("scLocator=//Dialog[ID=\"isc_globalWarn\"]/"));
      assertTrue(selenium.getText("scLocator=//Dialog[ID=\"isc_globalWarn\"]/blurb/").matches("^Do you want to reopen " + CUR_TIME + HTML_FILE_NAME+ " in selected editor[\\s\\S]$"));
      assertTrue(selenium.isElementPresent("scLocator=//Dialog[ID=\"isc_globalWarn\"]/yesButton/"));
      assertTrue(selenium.isElementPresent("scLocator=//Dialog[ID=\"isc_globalWarn\"]/noButton/"));
      if (clickYes)
      {
         selenium.click("scLocator=//Dialog[ID=\"isc_globalWarn\"]/yesButton/");
      }
      else
      {
         selenium.click("scLocator=//Dialog[ID=\"isc_globalWarn\"]/noButton/");
      }
   }
   
   private void doubleClickItemInNavigationTree(String name) throws Exception
   {
      // Enable navigation by using keyboard in the Navigation, Search and Outline Panel to improve IDE accessibility. 
      //http://jira.exoplatform.org/browse/IDE-258    

      String locator = "scLocator=//TreeGrid[ID=\"ideNavigatorItemTreeGrid\"]/body/row[name=" + name + "]/col[1]";
      selenium.click(locator);
      selenium.click(locator); 
      
      Thread.sleep(5000);
   }
   
    /**
    * Clean up cookie, registry, repository after each test of in the each class:<br>
    *   - selenium.deleteAllVisibleCookies();<br>
    *   - cleanRegistry();<br>
    *   - cleanRepository(REST_CONTEXT + "/" + WEBDAV_CONTEXT + "/" + REPO_NAME + "/" + WS_NAME + "/");<>
    * @throws IOException
    */
   @After
   public void testTearDown() throws IOException
   {
      deleteCookies();
      cleanRegistry();
      cleanRepository(REST_CONTEXT + "/" + WEBDAV_CONTEXT + "/" + REPO_NAME + "/" + WS_NAME + "/");
   }
}
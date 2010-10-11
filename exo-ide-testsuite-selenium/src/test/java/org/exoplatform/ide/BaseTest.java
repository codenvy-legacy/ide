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
package org.exoplatform.ide;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.exoplatform.common.http.client.HTTPConnection;
import org.exoplatform.common.http.client.HTTPResponse;
import org.exoplatform.common.http.client.ModuleException;
import org.exoplatform.common.http.client.ProtocolNotSuppException;
import org.exoplatform.ide.utils.AbstractTextUtil;
import org.exoplatform.ide.utils.InternetExplorerUtil;
import org.exoplatform.ide.utils.TextUtil;
import org.exoplatform.ide.utils.WebKitUtil;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;

import com.thoughtworks.selenium.DefaultSelenium;
import com.thoughtworks.selenium.Selenium;

/**
 * Created by The eXo Platform SAS.
 * 
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @author <a href="mailto:dmitry.nochevnov@exoplatform.com">Dmytro Nochevnov</a>
 * @version $Id:   ${date} ${time}
 *
 */
public abstract class BaseTest
{
   protected static Selenium selenium;

   protected static final String BASE_URL = IdeAddress.STANDALONE.getBaseUrl();
   protected static final String APPLICATION_URL = IdeAddress.STANDALONE.getApplicationUrl();   

   protected static final String REST_CONTEXT = "rest/private";   
   
   protected static final String REPO_NAME = "repository";   
   
   protected static final String ENTRY_POINT_URL = BASE_URL + REST_CONTEXT + "/jcr/" + REPO_NAME + "/";

   protected static final String WS_NAME = "dev-monit";
   
   protected static final String USER_NAME = "__anonim";
   // For portal 
//   protected static final String USER_NAME = "root";
   
   // For portal: 
   //protected static final String BASE_URL = "http://192.168.0.3:8080/";
   //protected static final String APPLICATION_URL = "http://192.168.0.3:8080/portal/public/default/ide";
   protected static final String REGISTER_IN_PORTAL = BASE_URL + "portal/private";

   protected static final EnumBrowserCommand BROWSER_COMMAND = EnumBrowserCommand.CHROME;

   /**
    * Name of window with application.
    * 
    * Used, when selenium works with several windows
    */
   protected static final String PAGE_NAME = "IDE";

   @BeforeClass
   public static void startSelenium() throws Exception
   {
      cleanDefaultWorkspace();
      selenium = new DefaultSelenium("localhost", 4444, BROWSER_COMMAND.toString(), BASE_URL);

      switch (BROWSER_COMMAND)
      {
         case GOOGLE_CHROME:
         case SAFARI:            
            new WebKitUtil(selenium);            
            break;
         
         case IE_EXPLORE_PROXY:
            new InternetExplorerUtil(selenium);
            break;
            
         default :
            new TextUtil(selenium);
      } 

      selenium.start();
      selenium.windowFocus();
      selenium.open(APPLICATION_URL);
      selenium.waitForPageToLoad("10000");
      selenium.windowMaximize();
      
      if (isRunIdeUnderPortal())
      {
         loginInPortal();
         selenium.open(APPLICATION_URL);
         selenium.waitForPageToLoad("10000");
         Thread.sleep(10000);
         // selenium.selectFrame("//div[@id='eXo-IDE-container']//iframe");
         // selenium.selectFrame("remote_iframe_0");

         // selectMainForm()
         if (selenium.isElementPresent("//div[@id='eXo-IDE-container']"))
         {
            selenium.selectFrame("//div[@id='eXo-IDE-container']//iframe");
         }
         else
         {
            selenium.selectFrame("relative=top");
         }
      }
      else
      {
         standaloneLogin();
         
      }
   }

   private static void standaloneLogin() throws InterruptedException 
   {
      Thread.sleep(TestConstants.SLEEP);
      selenium.type("//input[@name='j_username']", "root");
      selenium.type("//input[@name='j_password']", "gtn");
      selenium.click("//input[@value='Log In']");
      selenium.waitForPageToLoad("10000");
   }

   private static void loginInPortal() throws Exception
   {
      selenium.open(REGISTER_IN_PORTAL);
      Thread.sleep(TestConstants.SLEEP);
      selenium.type("//input[@name='username']", "root");
      selenium.type("//input[@name='password']", "gtn");
      selenium.click("//div[@id='UIPortalLoginFormAction']");
      selenium.waitForPageToLoad("10000");
   }

   /**
    * Performs click on toolbar button and makes pause after it.
    * @param buttonTitle toolbar button title
    */
   public void runToolbarButton(String buttonTitle) throws Exception
   {
      String locator = "//div[@title='" + buttonTitle + "']//img";
      selenium.mouseOver(locator);
      Thread.sleep(TestConstants.ANIMATION_PERIOD);

      String hoverLocator = "//div[@title='" + buttonTitle + "']//img";
      selenium.mouseDownAt(hoverLocator, "");
      selenium.mouseUpAt(hoverLocator, "");
      Thread.sleep(TestConstants.REDRAW_PERIOD);

      try
      {
         selenium.mouseOut(hoverLocator);
      }
      catch (Exception e)
      {
      }
   }

   /**
    * Close tab by it's index.
    * 
    * @param index numeration starts with 0 index
    */
   protected static void closeTab(String index) throws Exception
   {
      selenium.mouseOver("scLocator=//TabSet[ID=\"ideEditorFormTabSet\"]/tab[index=" + index + "]/icon");
      Thread.sleep(TestConstants.REDRAW_PERIOD);
      
      selenium.click("scLocator=//TabSet[ID=\"ideEditorFormTabSet\"]/tab[index=" + index + "]/icon");
      Thread.sleep(TestConstants.REDRAW_PERIOD);
   }

   /**
    * Returns the title of the tab with the pointed index.
    * 
    * @param index tab index
    * @return {@link String} tab's title
    * @throws Exception
    */
   protected String getTabTitle(int index) throws Exception
   {
      return selenium.getText("scLocator=//TabSet[ID=\"ideEditorFormTabSet\"]/tab[index=" + index + "]/title");
   }

   @AfterClass
   public static void stopSelenium()
   {
      selenium.stop();
   }

   /**
    * Types text to selected frame to body tag, which has attribute
    * class='editbox'.
    * 
    * Use keyPressNative for typing such symbols: 'y', '.'
    * 
    * Replace '\n' (Enter) symbol with keyDown and keyUp functions
    * 
    * @param text text to type
    */
   /* protected void typeText(String text) throws Exception
   {
      for (int i = 0; i < text.length(); i++)
      {
         char symbol = text.charAt(i);
         if (symbol == 'y')
         {
            selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_Y);
         }
         else if (symbol == '\n')
         {
            Thread.sleep(300);
            selenium.keyDown("//body[@class='editbox']/", "\\13");
            selenium.keyUp("//body[@class='editbox']/", "\\13");
            Thread.sleep(300);
         }
         else if (symbol == '.')
         {
            selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_PERIOD);
         }
         else
         {
            selenium.typeKeys("//body[@class='editbox']/", String.valueOf(symbol));
         }
      }
    }*/

   /**
    * Delete pointed number of lines in editor.s
    * 
    * @param count number of lines to delete
    */
   protected void deleteLinesInEditor(int count)
   {
      selenium.keyDownNative("" + java.awt.event.KeyEvent.VK_CONTROL);
      for (int i = 0; i < count; i++)
      {
         selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_D);
      }
      selenium.keyUpNative("" + java.awt.event.KeyEvent.VK_CONTROL);
   }

   /**
    * Select main frame of IDE.
    * 
    * This method is used, after typing text in editor.
    * To type text you must select editor iframe. After typing,
    * to return to them main frame, use selectMainFrame()
    * 
    */
   protected void selectMainFrame()
   {
      if (selenium.isElementPresent("//div[@id='eXo-IDE-container']"))
      {
         selenium.selectFrame("//div[@id='eXo-IDE-container']//iframe");
      }
      else
      {
         selenium.selectFrame("relative=top");
      }
   }

   /**
    * Select iframe, which contains editor from tab with index tabIndex
    * 
    * @param tabIndex begins from 0
    */
   protected void selectIFrameWithEditor(int tabIndex) throws Exception
   {
      selenium.selectFrame(getContentPanelLocator(tabIndex) + "//iframe");
      Thread.sleep(TestConstants.ANIMATION_PERIOD);
   }

   /**
    * Type text to file, opened in tab.
    * 
    * Sometimes, if you can't type text to editor,
    * try before to click on editor:
    * 
    * selenium.clickAt("//body[@class='editbox']", "5,5");
    * 
    * @param tabIndex begins from 0
    * @param text (can be used '\n' as line break)
    */
   protected void typeTextIntoEditor(int tabIndex, String text) throws Exception
   {
      selectIFrameWithEditor(tabIndex);

      if (selenium.isElementPresent(getContentPanelLocator(tabIndex) + "//table[@class='cke_editor']//td[@class='cke_contents']/iframe"))
      {
         AbstractTextUtil.getInstance().typeTextToEditor(TestConstants.CK_EDITOR_LOCATOR, text);
      }
      else
      {
         AbstractTextUtil.getInstance().typeTextToEditor(TestConstants.CODEMIRROR_EDITOR_LOCATOR, text);  
      }

      selectMainFrame();
   }

   /**
    * Get text from tab number "tabIndex" from editor
    * @param tabIndex begins from 0
    */
   protected String getTextFromCodeEditor(int tabIndex) throws Exception
   {
      selectIFrameWithEditor(tabIndex);
      String text = selenium.getText("//body[@class='editbox']");
      selectMainFrame();
      return text;
   }

   /**
    * Get selected text from browser window.
    * Note: if use editor - select frame with it.
    * 
    * @return {@link String}
    */
   protected String getSelectedText()
   {
      return selenium.getEval("if (window.getSelection) { window.getSelection();}");
   }

   /**
    * Select the item in the workspace navigation tree. 
    * 
    * @param name item name
    * @throws Exception
    */
   protected void selectItemInWorkspaceTree(String name) throws Exception
   {
      selenium.click("scLocator=//TreeGrid[ID=\"ideNavigatorItemTreeGrid\"]/body/row[name=" + name + "]/col[1]");
      Thread.sleep(TestConstants.REDRAW_PERIOD);
   }

   /**
    * Select the item in the search results tree. 
    * 
    * @param name item's name
    * @throws Exception
    */
   protected void selectItemInSearchResultsTree(String name) throws Exception
   {
      selenium.click("scLocator=//TreeGrid[ID=\"ideSearchResultItemTreeGrid\"]/body/row[name=" + name + "]/col[1]");
   }

   /**
    * Select the item in the outline tree. 
    * 
    * @param name item's name
    * @throws Exception
    */
   protected void selectItemInOutlineTree(String name) throws Exception
   {
      selenium.click("scLocator=//TreeGrid[ID=\"ideOutlineTreeGrid\"]/body/row[name=" + name + "]/col[1]");
   }   
   
   /**
    * Select the root workspace item in workspace tree.
    * 
    * @param name
    * @throws Exception
    */
   protected void selectRootOfWorkspaceTree() throws Exception
   {
      selenium.click("scLocator=//TreeGrid[ID=\"ideNavigatorItemTreeGrid\"]/body/row[0]/col[1]");
      Thread.sleep(TestConstants.REDRAW_PERIOD);
   }

   /**
    * Check navigation workspace tree contains pointed item.
    * 
    * @param name name of item in the navigation tree
    * @throws Exception
    */
   protected void assertElementPresentInWorkspaceTree(String name) throws Exception
   {
      assertTrue(selenium.isElementPresent("scLocator=//TreeGrid[ID=\"ideNavigatorItemTreeGrid\"]/body/row[name="
         + name + "]/col[0]"));
   }

   /**
    * Check item is shown in search results tree.
    * 
    * @param name
    * @throws Exception
    */
   protected void assertElementPresentSearchResultsTree(String name) throws Exception
   {
      assertTrue(selenium.isElementPresent("scLocator=//TreeGrid[ID=\"ideSearchResultItemTreeGrid\"]/body/row[name="
         + name + "]/col[0]"));
   }

   /**
    * Check item is not shown in search results tree.
    * 
    * @param name
    * @throws Exception
    */
   protected void assertElementNotPresentSearchResultsTree(String name) throws Exception
   {
      assertFalse(selenium.isElementPresent("scLocator=//TreeGrid[ID=\"ideSearchResultItemTreeGrid\"]/body/row[name="
         + name + "]/col[0]"));
   }

   /**
    * Check navigation workspace tree doesn't contain pointed item.
    * 
    * @param name name of item in the navigation tree
    * @throws Exception
    */
   protected void assertElementNotPresentInWorkspaceTree(String name) throws Exception
   {
      assertFalse(selenium.isElementPresent("scLocator=//TreeGrid[ID=\"ideNavigatorItemTreeGrid\"]/body/row[name="
         + name + "]/col[0]"));
   }


   /**
    * Check item is shown in outline tree.
    * 
    * @param name
    * @throws Exception
    */
   protected void assertElementPresentOutlineTree(String name) throws Exception
   {
      assertTrue(selenium.isElementPresent("scLocator=//TreeGrid[ID=\"ideOutlineTreeGrid\"]/body/row[name="
         + name + "]/col[0]"));
   }

   /**
    * Check item is not shown in outline tree.
    * 
    * @param name
    * @throws Exception
    */
   protected void assertElementNotPresentOutlineTree(String name) throws Exception
   {
      assertFalse(selenium.isElementPresent("scLocator=//TreeGrid[ID=\"ideOutlineTreeGrid\"]/body/row[name="
         + name + "]/col[0]"));
   }   
   
   /**
    * Get name of item in workspace tree by it's index.
    * 
    * @param index
    * @return {@link String} name
    */
   protected String getItemNameFromWorkspaceTree(int index)
   {
      return selenium.getText("scLocator=//TreeGrid[ID=\"ideNavigatorItemTreeGrid\"]/body/row[" + index + "]/col[0]");
   }
   
   /*
   * Get name of item in search tree by it's index.
   * 
   * @param index starting from 0
   * @return {@link String} name
   */
  protected String getItemNameFromSearchResultsTree(int index)
  {
     return selenium.getText("scLocator=//TreeGrid[ID=\"ideSearchResultItemTreeGrid\"]/body/row[" + index + "]/col[0]");
  }   

   /**
    * Delete selected item in navigation tree.
    * 
    * @throws Exception
    */
   protected void deleteSelectedItems() throws Exception
   {
      runToolbarButton(ToolbarCommands.File.DELETE);
      //check deletion form
      assertTrue(selenium.isElementPresent("scLocator=//Window[ID=\"ideDeleteItemForm\"]/"));
      assertEquals("Delete Item(s)", selenium.getText("scLocator=//Window[ID=\"ideDeleteItemForm\"]/headerLabel/"));
      assertTrue(selenium.isElementPresent("scLocator=//IButton[ID=\"ideDeleteItemFormOkButton\"]"));
      assertTrue(selenium.isElementPresent("scLocator=//IButton[ID=\"ideDeleteItemFormCancelButton\"]"));
      //click Ok button
      selenium.click("scLocator=//IButton[ID=\"ideDeleteItemFormOkButton\"]/");
      Thread.sleep(TestConstants.REDRAW_PERIOD);
   }

   /*  protected void typeTextTo(String locator, String text) throws Exception
   {
      selenium.click(locator);

      selenium.keyDownNative("" + java.awt.event.KeyEvent.VK_CONTROL);
      selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_A);
      selenium.keyUpNative("" + java.awt.event.KeyEvent.VK_CONTROL);
      Thread.sleep(TestConstants.ANIMATION_PERIOD);

      selenium.type(locator, "");

      for (int i = 0; i < text.length(); i++)
      {
         char symbol = text.charAt(i);
         if (symbol == 'y')
         {
            selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_Y);
         }
         else if (symbol == '.')
         {
            selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_PERIOD);
         }
         else
         {
            selenium.typeKeys(locator, String.valueOf(symbol));
         }
         //Thread.sleep(TYPE_DELAY_PERIOD);
      }

      Thread.sleep(TestConstants.REDRAW_PERIOD);
     }*/

   /**
    * Creates folder with name folderName.
    * 
    * Folder, that will be parent for folderName must be selected before.
    * 
    * Clicks on New button on toolbar, then click on Folder menu from list.
    * 
    * @param folderName folder name
    */
   protected void createFolder(String folderName) throws Exception
   {
      createFileFromToolbar(MenuCommands.New.FOLDER);

      //      runToolbarButton("New");
      //      
      //      selenium.mouseDownAt("//td[@class=\"exo-popupMenuTitleField\"]//nobr[contains(text(), \"Folder\")]", "");

      //Check creation form elements
      assertTrue(selenium.isTextPresent("Name of new folder:"));
      assertTrue(selenium.isElementPresent("scLocator=//Window[ID=\"ideCreateFolderForm\"]"));
      assertTrue(selenium.isElementPresent("scLocator=//Window[ID=\"ideCreateFolderForm\"]//input"));
      assertTrue(selenium.isElementPresent("scLocator=//IButton[ID=\"ideCreateFolderFormCreateButton\"]"));
      assertTrue(selenium.isElementPresent("scLocator=//IButton[ID=\"ideCreateFolderFormCancelButton\"]"));

      //clearFocus();
      
      String locator =
         "scLocator=//DynamicForm[ID=\"ideCreateFolderFormDynamicForm\"]/item[name=ideCreateFolderFormNameField]/element";

      //selenium.select(locator, optionLocator)

      AbstractTextUtil.getInstance().typeToInput(locator, folderName, true);
      Thread.sleep(TestConstants.ANIMATION_PERIOD);

      selenium.click("scLocator=//IButton[ID=\"ideCreateFolderFormCreateButton\"]/");

      Thread.sleep(TestConstants.REDRAW_PERIOD);
      //Check creation form is not shown
      assertFalse(selenium.isElementPresent("scLocator=//Window[ID=\"ideCreateFolderForm\"]"));
      assertElementPresentInWorkspaceTree(folderName);
   }

   /**
    * Calls Save As command by clicking Save As... icon on toolbar.
    * 
    * Checks is dialog appears, and do all elements are present in window.
    * 
    * Enters name to text field and click Ok button.
    * 
    * If name is null, will created with proposed default name.
    * 
    * @param name file name
    * @throws Exception
    */
   protected void saveAsUsingToolbarButton(String name) throws Exception
   {
      runToolbarButton("Save As...");
      checkSaveAsDialogAndSave(name);
   }

   /**
    * Call Save As command using top menu File.
    * 
    * If name is null, will created with proposed default name.
    * 
    * @param name file name to save
    * @throws Exception
    */
   protected void saveAsByTopMenu(String name) throws Exception
   {
      //open menu File
      selenium.mouseDownAt("//td[@class='exo-menuBarItem' and @menubartitle='File']", "");
      Thread.sleep(TestConstants.SLEEP);

      //check is Save As enabled
      assertTrue(selenium.isElementPresent("//td[@class='exo-popupMenuTitleField']/nobr[text()='Save As...']"));

      //click Save As
      selenium.mouseDownAt("//td[@class='exo-popupMenuTitleField']/nobr[text()='Save As...']", "");
      Thread.sleep(TestConstants.SLEEP);

      checkSaveAsDialogAndSave(name);
   }

   /**
    * Open file from navigation tree with code mirror.
    * 
    * @param fileName name of file in navigation tree
    * @param checkDefault is click on checkbox "Use by default"
    * @throws Exception
    */
   protected void openFileFromNavigationTreeWithCodeEditor(String fileName, boolean checkDefault) throws Exception
   {
      //TODO add check form
      //select file in navigation tree
      selenium.click("scLocator=//TreeGrid[ID=\"ideNavigatorItemTreeGrid\"]/body/row[name=" + fileName + "]/col[1]");
      Thread.sleep(TestConstants.ANIMATION_PERIOD);

      selenium.mouseDownAt("//td[@class='exo-menuBarItem' and @menubartitle='File']", "");
      Thread.sleep(TestConstants.ANIMATION_PERIOD);

      selenium.mouseDownAt("//td[@class='exo-popupMenuTitleField']/nobr[contains(text(), 'Open With')]", "");
      Thread.sleep(TestConstants.ANIMATION_PERIOD);

      selenium.click("scLocator=//ListGrid[ID=\"ideOpenFileWithListGrid\"]/body/row[0]/col[0]");
      Thread.sleep(TestConstants.ANIMATION_PERIOD);

      if (checkDefault)
      {
         //click on checkbox Use as default editor
         selenium
            .click("scLocator=//Window[ID=\"ideallOpenFileWithForm\"]/item[1][Class=\"DynamicForm\"]/item[name=Default]/textbox");
         Thread.sleep(TestConstants.ANIMATION_PERIOD);
      }

      selenium.click("scLocator=//IButton[ID=\"ideOpenFileWithOkButton\"]");
      //time remaining to open editor
      Thread.sleep(TestConstants.EDITOR_OPEN_PERIOD);
   }

   protected void openFileFromSearchResultsWithCodeEditor(String fileName) throws Exception
   {
      //TODO add check form
      selenium.click("scLocator=//TreeGrid[ID=\"ideSearchResultItemTreeGrid\"]/body/row[name=" + fileName + "]/col[1]");
      Thread.sleep(TestConstants.SLEEP_SHORT);
      selenium.mouseDownAt("//td[@class='exo-menuBarItem' and @menubartitle='File']", "");
      Thread.sleep(TestConstants.SLEEP);
      selenium.mouseDownAt("//td[@class='exo-popupMenuTitleField']/nobr[contains(text(), 'Open With')]", "");
      selenium.click("scLocator=//ListGrid[ID=\"ideOpenFileWithListGrid\"]/body/row[0]/col[0]");
      selenium.click("scLocator=//IButton[ID=\"ideOpenFileWithOkButton\"]");
   }

   /**
    * Open file from navigation tree with CK (WYSIWYG) editor
    * @param fileName name of file to open
    * @param checkDefault do mark checkbox Use by default
    * @throws Exception
    */
   protected void openFileFromNavigationTreeWithCkEditor(String fileName, boolean checkDefault) throws Exception
   {
      //TODO add check form
      selectItemInWorkspaceTree(fileName);
      //      selenium.click("scLocator=//TreeGrid[ID=\"ideNavigatorItemTreeGrid\"]/body/row[name=" + fileName + "]/col[1]");
      Thread.sleep(TestConstants.SLEEP_SHORT);
      selenium.mouseDownAt("//td[@class='exo-menuBarItem' and @menubartitle='File']", "");
      Thread.sleep(TestConstants.SLEEP);
      selenium.mouseDownAt("//td[@class='exo-popupMenuTitleField']/nobr[contains(text(), 'Open With')]", "");
      selenium.click("scLocator=//ListGrid[ID=\"ideOpenFileWithListGrid\"]/body/row[1]/col[0]");
      if (checkDefault)
      {
         //click on checkbox Use as default editor
         selenium
            .click("scLocator=//Window[ID=\"ideallOpenFileWithForm\"]/item[1][Class=\"DynamicForm\"]/item[name=Default]/textbox");
         Thread.sleep(TestConstants.SLEEP);
      }
      selenium.click("scLocator=//IButton[ID=\"ideOpenFileWithOkButton\"]");
      Thread.sleep(TestConstants.SLEEP);
      //time remaining to open CK editor
      Thread.sleep(TestConstants.SLEEP);
      //TODO add check that editor opened
   }

   protected void saveCurrentFile() throws Exception
   {
      runToolbarButton("Save");
   }

   /**
    * Clicks on New button on toolbar and then clicks on 
    * menuName from list
    * @param menuName
    */
   protected void createFileFromToolbar(String menuName) throws Exception
   {
      runToolbarButton("New");

      String locator = "//td[@class=\"exo-popupMenuTitleField\"]//nobr[text()='" + menuName + "']";
      selenium.mouseOver(locator);
      Thread.sleep(TestConstants.ANIMATION_PERIOD);

      String hoverLocator = "//td[@class=\"exo-popupMenuTitleFieldOver\"]//nobr[text()='" + menuName + "']";
      selenium.mouseDownAt(hoverLocator, "");
      //time to wait while gadget open new file
      Thread.sleep(TestConstants.REDRAW_PERIOD);
   }

   /**
    * Clicks on New button on toolbar and then clicks on 
    * menuName from list
    * @param menuName
    */
   protected void callNewItemFromToolbar(String itemName) throws Exception
   {
      runToolbarButton("New");

      String locator = "//td[@class=\"exo-popupMenuTitleField\"]//nobr[text()='" + itemName + "']";
      selenium.mouseOver(locator);
      Thread.sleep(TestConstants.ANIMATION_PERIOD);

      String hoverLocator = "//td[@class=\"exo-popupMenuTitleFieldOver\"]//nobr[text()='" + itemName + "']";
      selenium.mouseDownAt(hoverLocator, "");
      //time to wait while gadget open new file
      Thread.sleep(TestConstants.ANIMATION_PERIOD);
   }

   /**
    * Check is button present on toolbar and is it enabled or disabled.
    * 
    * @param name button name
    * @param enabled boolean value
    */
   protected void checkToolbarButtonState(String name, boolean enabled)
   {
      if (enabled)
      {
         assertTrue(selenium.isElementPresent("//div[@title='" + name + "']/div[@elementenabled='true']"));
      }
      else
      {
         assertTrue(selenium.isElementPresent("//div[@title='" + name + "']/div[@elementenabled='false']"));
      }
   }

   /**
    * Check is button present on toolbar
    * 
    * @param name button name (title in DOM)
    * @param isPresent is present
    */
   protected void checkToolbarButtonPresentOnLeftSide(String name, boolean isPresent)
   {
      if (isPresent)
      {
         assertFalse(selenium.isElementPresent("//div[@class='exo-toolbar16ButtonPanel_LeftHidden' and @title='" + name
            + "']//img"));
         assertTrue(selenium.isElementPresent("//div[@class='exo-toolbar16ButtonPanel_Left' and @title='" + name
            + "']//img"));
      }
      else
      {
         assertFalse(selenium.isElementPresent("//div[@class='exo-toolbar16ButtonPanel_Left' and @title='" + name
            + "']//img"));
         assertTrue(selenium.isElementPresent("//div[@class='exo-toolbar16ButtonPanel_LeftHidden' and @title='" + name
            + "']//img"));
      }
   }

   //exo-toolbar16ButtonPanel_Right

   /**
    * Check is button present on toolbar
    * 
    * @param name button name (title in DOM)
    * @param isPresent is present
    */
   protected void checkToolbarButtonPresentOnRightSide(String name, boolean isPresent)
   {
      if (isPresent)
      {
         assertFalse(selenium.isElementPresent("//div[@class='exo-toolbar16ButtonPanel_RightHidden' and @title='"
            + name + "']//img"));
         assertTrue(selenium.isElementPresent("//div[@class='exo-toolbar16ButtonPanel_Right' and @title='" + name
            + "']//img"));
      }
      else
      {
         assertFalse(selenium.isElementPresent("//div[@class='exo-toolbar16ButtonPanel_Right' and @title='" + name
            + "']//img"));
         assertTrue(selenium.isElementPresent("//div[@class='exo-toolbar16ButtonPanel_RightHidden' and @title='" + name
            + "']//img"));
      }
   }

   /**
    * Opens folder in Workspace tree (if folder is closed)
    * or closes folder (if it is opened)
    * 
    * Clicks on open sign of folder.
    * 
    * If folderName doesn't present in Workspace tree, test fails.
    * 
    * @param folderName
    */
   protected void openOrCloseFolder(String folderName) throws Exception
   {
      selenium.click("scLocator=//TreeGrid[ID=\"ideNavigatorItemTreeGrid\"]/body/row[name=" + folderName
         + "]/col[0]/open");
      Thread.sleep(TestConstants.REDRAW_PERIOD);
   }

   /**
    * 
    */
   protected void openCloseRootWorkspace()
   {
      selenium.click("scLocator=//TreeGrid[ID=\"ideNavigatorItemTreeGrid\"]/body/row[0]/col[0]/open");
   }

   /**
    * Close unsaved file withous saving it.
    * 
    * Close tab with tabIndex. Check is warning dialog appears.
    * Click No button]
    * 
    * @param tabIndex index of tab to close
    * @throws Exception
    */
   protected static void closeUnsavedFileAndDoNotSave(String tabIndex) throws Exception
   {
      closeTab(tabIndex);

      //check is warning dialog appears
      assertTrue(selenium
         .isElementPresent("scLocator=//Dialog[ID=\"isc_globalWarn\"]/header[contains(text(), 'Close file')]"));
      assertTrue(selenium.isElementPresent("scLocator=//Dialog[ID=\"isc_globalWarn\"]/noButton/"));
      assertTrue(selenium.isElementPresent("scLocator=//Dialog[ID=\"isc_globalWarn\"]/yesButton/"));

      //click No button
      selenium.click("scLocator=//Dialog[ID=\"isc_globalWarn\"]/noButton/");
      Thread.sleep(TestConstants.SLEEP);
   }

   /**
    * Check is command in top menu enabled or disabled.
    * 
    * @param topMenuName mane of menu
    * @param commandName command name
    * @param enabled boolean value
    */
   protected void checkMenuCommandState(String topMenuName, String commandName, boolean enabled) throws Exception
   {
      selenium.mouseDownAt("//td[@class='exo-menuBarItem' and @menubartitle='" + topMenuName + "']", "");
      Thread.sleep(TestConstants.REDRAW_PERIOD);
      if (enabled)
      {
         assertTrue(selenium.isElementPresent("//td[@class='exo-popupMenuTitleField']/nobr[text()='" + commandName
            + "']"));
      }
      else
      {
         assertTrue(selenium.isElementPresent("//td[@class='exo-popupMenuTitleFieldDisabled']/nobr[text()='"
            + commandName + "']"));
      }
      selenium.mouseDown("//div[@class='exo-lockLayer']/");
      Thread.sleep(TestConstants.REDRAW_PERIOD);
   }

   /**
    * Check is command in top menu visible or hidden.
    * 
    * @param topMenuName mane of menu
    * @param commandName command name
    * @param isPresent boolean value
    */
   protected void checkMenuCommandPresent(String topMenuName, String commandName, boolean isPresent) throws Exception
   {
      selenium.mouseDownAt("//td[@class='exo-menuBarItem' and @menubartitle='" + topMenuName + "']", "");
      if (isPresent)
      {
         assertTrue(selenium.isElementPresent("//td/nobr[text()='" + commandName + "']"));
      }
      else
      {
         assertFalse(selenium.isElementPresent("//td/nobr[text()='" + commandName + "']"));
      }
      selenium.mouseDown("//div[@class='exo-lockLayer']/");
      Thread.sleep(TestConstants.REDRAW_PERIOD);
   }

   /**
    * Open command from top menu.
    * 
    * @param topMenuName name of menu
    * @param commandName command name
    */
   protected void runTopMenuCommand(String topMenuName, String commandName) throws Exception
   {
//    selenium.mouseDownAt("//td[@class='exo-menuBarItem' and @menubartitle='" + topMenuName + "']", "");  // Doesn't work in the IE
      selenium.mouseDownAt("//td[@class='exo-menuBarItem' and text()='" + topMenuName + "']", "");
      Thread.sleep(TestConstants.ANIMATION_PERIOD);

      selenium.mouseDownAt("//td[@class='exo-popupMenuTitleField']/nobr[text()='" + commandName + "']", "");
      Thread.sleep(TestConstants.REDRAW_PERIOD);
   }

   /**
    * Check is file in tabIndex tab opened with CK editor.
    * 
    * @param tabIndex index of tab
    * @throws Exception
    */
   protected void checkCkEditorOpened(int tabIndex) throws Exception
   {
      String divIndex = String.valueOf(tabIndex + 2);
      assertTrue(selenium.isElementPresent("//div[@class='tabSetContainer']/div/div[" + divIndex
         + "]//table[@class='cke_editor']//td[@class='cke_contents']/iframe"));
   }

   /**
    * Check is dialog window Save as file appeared
    * and do all elements present.
    * 
    * Enter name to text field and click Ok button.
    * 
    * If name is null, will created with proposed default name.
    * 
    * @param name file name
    */
   protected void checkSaveAsDialogAndSave(String name) throws Exception
   {
      String okButtonLocator = "scLocator=//IButton[ID=\"ideAskForValueDialogOkButton\"]/";
      String cancelButtonLocator = "scLocator=//IButton[ID=\"ideAskForValueDialogCancelButton\"]/";

      assertTrue(selenium.isElementPresent("scLocator=//Window[ID=\"ideAskForValueDialog\"]"));
      assertTrue(selenium.isTextPresent("Save file as"));
      assertTrue(selenium
         .isElementPresent("scLocator=//Window[ID=\"ideAskForValueDialog\"]/item[0][Class=\"DynamicForm\"]/item[name=ideAskForValueDialogValueField||title=ideAskForValueDialogValueField||Class=TextItem]/element"));
      assertTrue(selenium.isElementPresent(okButtonLocator));
      assertTrue(selenium.isElementPresent(cancelButtonLocator));
      
      //clearFocus();

      String textItemLocator =
         "scLocator=//Window[ID=\"ideAskForValueDialog\"]/item[0][Class=\"DynamicForm\"]/item[name=ideAskForValueDialogValueField||title=ideAskForValueDialogValueField||Class=TextItem]/element";
      AbstractTextUtil.getInstance().typeToInput(textItemLocator, name, true);

      selenium.click(okButtonLocator);
      Thread.sleep(TestConstants.PAGE_LOAD_PERIOD);
   }

   /**
    * Get the URL of selected item.
    * 
    * @return {@link String} URL
    * @throws Exception
    */
   protected String getItemUrl() throws Exception
   {
      //Click get URL 
      runTopMenuCommand(MenuCommands.View.VIEW, MenuCommands.View.GET_URL);
      Thread.sleep(TestConstants.SLEEP);
      assertTrue(selenium.isElementPresent("scLocator=//Window[ID=\"ideGetItemURLForm\"]"));
      assertTrue(selenium.isElementPresent("scLocator=//IButton[ID=\"ideGetItemURLFormOkButton\"]"));
      assertTrue(selenium
         .isElementPresent("scLocator=//Window[ID=\"ideGetItemURLForm\"]/item[0][Class=\"DynamicForm\"]/item[name=ideGetItemURLFormURLField]/element"));

      String url =
         selenium
            .getValue("scLocator=//Window[ID=\"ideGetItemURLForm\"]/item[0][Class=\"DynamicForm\"]/item[name=ideGetItemURLFormURLField]/element");

      //Close form
      selenium.click("scLocator=//IButton[ID=\"ideGetItemURLFormOkButton\"]");
      Thread.sleep(TestConstants.SLEEP);
      assertFalse(selenium.isElementPresent("scLocator=//Window[ID=\"ideGetItemURLForm\"]"));
      return url;
   }

   /**
    * Select "Workspace" tab in navigation panel
    */
   protected void selectWorkspaceTab()
   {
      selenium.click("scLocator=//TabSet[ID=\"ideNavigationTabSet\"]/tab[ID=BrowserPanel]");
   }

   /**
    * Select "Search Result" tab in navigation panel
    */
   protected void selectSearchResultTab()
   {
      selenium.click("scLocator=//TabSet[ID=\"ideNavigationTabSet\"]/tab[ID=SearchResultPanel]");
   }

   /**
    * Performs search by pointed params.
    * 
    * @param checkPath path to check
    * @param text text to search
    * @param mimeType mime type to search
    * @throws Exception 
    */
   protected void performSearch(String checkPath, String text, String mimeType) throws Exception
   {
      runToolbarButton("Search...");
      Thread.sleep(TestConstants.SLEEP);

      assertTrue(selenium.isElementPresent("scLocator=//Window[ID=\"ideSearchForm\"]"));
      assertTrue(selenium.isElementPresent("scLocator=//IButton[ID=\"ideSearchFormSearchButton\"]"));
      assertTrue(selenium.isElementPresent("scLocator=//IButton[ID=\"ideSearchFormCancelButton\"]"));
      //Check form inputs
      assertEquals(
         checkPath,
         selenium
            .getValue("scLocator=//DynamicForm[ID=\"ideSearchFormDynamicForm\"]/item[name=ideSearchFormPathField]/element"));
      assertEquals(
         "",
         selenium
            .getValue("scLocator=//DynamicForm[ID=\"ideSearchFormDynamicForm\"]/item[name=ideSearchFormContentField]/element"));
      assertEquals(
         "",
         selenium
            .getValue("scLocator=//DynamicForm[ID=\"ideSearchFormDynamicForm\"]/item[name=ideSearchFormMimeTypeField]/element"));
      //Type content to input
      selenium
         .click("scLocator=//DynamicForm[ID=\"ideSearchFormDynamicForm\"]/item[name=ideSearchFormContentField]/element");
      selenium.type(
         "scLocator=//DynamicForm[ID=\"ideSearchFormDynamicForm\"]/item[name=ideSearchFormContentField]/element", text);
      //Type mime type
      selenium
         .click("scLocator=//DynamicForm[ID=\"ideSearchFormDynamicForm\"]/item[name=ideSearchFormMimeTypeField]/element");
      selenium.type(
         "scLocator=//DynamicForm[ID=\"ideSearchFormDynamicForm\"]/item[name=ideSearchFormMimeTypeField]/element",
         mimeType);
      //Click "Search" button
      selenium.click("scLocator=//IButton[ID=\"ideSearchFormSearchButton\"]");
      Thread.sleep(TestConstants.SLEEP);
   }

   /**
    * Get text shown in status bar.
    * 
    * @return {@link String} text
    */
   protected String getStatusbarText()
   {
      return selenium.getText("//table[@class='exo-statusText-table']");
   }

   /**
    * Check is file in tabIndex tab opened with Code Editor.
    * 
    * @param tabIndex
    * @throws Exception
    */
   protected void checkCodeEditorOpened(int tabIndex) throws Exception
   {
      String divIndex = String.valueOf(tabIndex + 2);
      //check Code Editor is present
      assertTrue(selenium.isElementPresent("//div[@class='tabSetContainer']/div/div[" + divIndex
         + "]//div[@class='CodeMirror-wrapping']/iframe"));
      //check CK editor is not present
      assertFalse(selenium.isElementPresent("//div[@class='tabSetContainer']/div/div[" + divIndex
         + "]//table[@class='cke_editor']//td[@class='cke_contents']/iframe"));
   }

   /**
    * Use to create new file in selected folder
    * 
    * @param menuCommand name of command from New button on toolbar
    * @param fileName name of file
    * @throws Exception
    */
   protected void createSaveAndCloseFile(String menuCommand, String fileName, int tabIndex) throws Exception
   {
      createFileFromToolbar(menuCommand);
      Thread.sleep(1000);

      saveAsUsingToolbarButton(fileName);
      Thread.sleep(TestConstants.SLEEP);

      closeTab(String.valueOf(tabIndex));
      Thread.sleep(TestConstants.SLEEP);
   }

   /**
    *  Delete all file content via Ctrl+a, Delete
    */
   protected void deleteFileContent()
   {
      selenium.keyDownNative("" + java.awt.event.KeyEvent.VK_CONTROL);

      selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_A);

      selenium.keyUpNative("" + java.awt.event.KeyEvent.VK_CONTROL);

      selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_DELETE);
   }

   /**
   * Select editor tab
   * 
   * @param tabIndex index of tab
   * @throws Exception
   */
   protected void selectEditorTab(int tabIndex) throws Exception
   {
      selenium.click("scLocator=//TabSet[ID=\"ideEditorFormTabSet\"]/tab[index=" + String.valueOf(tabIndex) + "]/");
      Thread.sleep(TestConstants.REDRAW_PERIOD);
   }

   /**
    * Run hot key within editor. 
    * 
    * This method used for running hotkeys for editor, such as ctrl+z, ctrl+a, ctrl+s and so on.
    * 
    * @param tabIndex index of tab
    * @param isCtrl is control key used
    * @param isAlt is alt key used
    * @param keyCode virtual code of key (code of key on keyboard)
    */
   protected void runHotkeyWithinEditor(int tabIndex, boolean isCtrl, boolean isAlt, int keyCode) throws Exception
   {
      selectIFrameWithEditor(tabIndex);

      if (isCtrl)
      {
         selenium.controlKeyDown();
      }
      if (isAlt)
      {
         selenium.altKeyDown();
      }

      selenium.keyDown("//", String.valueOf(keyCode));
      selenium.keyUp("//", String.valueOf(keyCode));

      if (isCtrl)
      {
         selenium.controlKeyUp();
      }
      if (isAlt)
      {
         selenium.altKeyUp();
      }

      selectMainFrame();
      Thread.sleep(TestConstants.REDRAW_PERIOD);
   }

   /**
    * Read file content.
    * 
    * @param file to read
    * @return String file content
    */
   protected String getFileContent(String filePath)
   {
      File file = new File(filePath);
      StringBuilder content = new StringBuilder();

      try
      {
         BufferedReader input = new BufferedReader(new FileReader(file));
         try
         {
            String line = null;

            while ((line = input.readLine()) != null)
            {
               content.append(line);
               content.append('\n');
            }
         }
         finally
         {
            input.close();
         }
      }
      catch (IOException e)
      {
         e.printStackTrace();
         assertTrue(false);
      }

      return content.toString();
   }

   /**
    * Call the "Run->Launch REST Service" topmenu command
    * 
    * @throws Exception
    * @throws InterruptedException
    */
   protected void launchRestService() throws Exception, InterruptedException
   {
      runToolbarButton(MenuCommands.Run.LAUNCH_REST_SERVICE);
      Thread.sleep(TestConstants.SLEEP);
      assertTrue(selenium.isElementPresent("scLocator=//Window[ID=\"ideGroovyServiceOutputPreviewForm\"]"));
   }

   /**
    * Read value from cursor position panel in status bar.
    * 
    * @return {@link String}
    */
   protected String getCursorPositionUsingStatusBar()
   {
      return selenium
         .getText("//table[@class='exo-statusBar-table']/tbody/tr/td[4]/div/table[@class='exo-statusText-table']//nobr");
   }

   /**
    * Check is node in Outline tree is selected
    * 
    * @param rowNumber number of item in treegrid starting from 0
    * @param name name of item
    * @param isSelected is node selected
    */
   protected void checkOutlineTreeNodeSelected(int rowNumber, String name, boolean isSelected)
   {
      String divIndex = String.valueOf(rowNumber + 1);
      if (isSelected)
      {
         assertTrue(selenium.isElementPresent("//div[@id='isc_H']/div[2]/div/div[4]//div["
            + "@class='tabSetContainer']//table[@class='listTable']/tbody/tr[" + divIndex
            + "]/td[@class='treeCellSelected']//nobr[text()='" + name + "']"));
      }
      else
      {
         assertTrue(selenium.isElementPresent("//div[@id='isc_H']/div[2]/div/div[4]//div["
            + "@class='tabSetContainer']//table[@class='listTable']/tbody/tr[" + divIndex
            + "]/td[@class='treeCell']//nobr[text()='" + name + "']"));
      }
   }

   /**
    * Open Upload or Open Local File form. And Upload file.
    * 
    * @param formName name of the form
    * @param filePath path to file 
    * @param mimeType mime type of the file
    * @throws InterruptedException 
    */
   protected void uploadFile(String formName, String filePath, String mimeType) throws Exception
   {
      if (!MenuCommands.File.OPEN_LOCAL_FILE.equals(formName) && !MenuCommands.File.UPLOAD.equals(formName))
      {
         Assert.fail("Form name must be - " + MenuCommands.File.OPEN_LOCAL_FILE + " or - " + MenuCommands.File.UPLOAD);
      }
      runTopMenuCommand(MenuCommands.File.FILE, formName);

      Thread.sleep(TestConstants.SLEEP);

      assertTrue(selenium.isElementPresent("scLocator=//Window[ID=\"ideUploadForm\"]/body/"));
      assertTrue(selenium
         .isElementPresent("//div[@class='stretchImgButtonDisabled' and @eventproxy='ideUploadFormUploadButton']"));
      try
      {
         File file = new File(filePath);
         selenium.type("//input[@type='file']", file.getCanonicalPath());
      }
      catch (Exception e)
      {
      }
      Thread.sleep(TestConstants.SLEEP);

      assertEquals(
         filePath.substring(filePath.lastIndexOf("/") + 1, filePath.length()),
         selenium
            .getValue("scLocator=//DynamicForm[ID=\"ideUploadFormDynamicForm\"]/item[name=ideUploadFormFilenameField]/element"));

      selenium
         .type(
            "scLocator=//Window[ID=\"ideUploadForm\"]/item[0][Class=\"DynamicForm\"]/item[name=ideUploadFormMimeTypeField]/element",
            mimeType);
      assertTrue(selenium
         .isElementPresent("//div[@class='stretchImgButton' and @eventproxy='ideUploadFormUploadButton']"));

      selenium.click("scLocator=//IButton[ID=\"ideUploadFormUploadButton\"]/");
      Thread.sleep(TestConstants.SLEEP);

      assertFalse(selenium.isElementPresent("scLocator=//Window[ID=\"ideUploadForm\"]/"));
   }

   /**
    * Create file from template.
    * 
    * @param templateName
    * @param fileName
    * @throws Exception
    */
   protected void createFileFromTemplate(String templateName, String fileName) throws Exception
   {
      createFileFromToolbar(MenuCommands.New.FROM_TEMPLATE);

      useTemplateForm(templateName, fileName);
   }

   /**
    * If you have opened create file from template form,
    * use this method to create file.
    * 
    * @param templateName
    * @param fileName
    * @throws Exception
    */
   protected void useTemplateForm(String templateName, String fileName) throws Exception
   {
      assertTrue(selenium.isElementPresent("//div[@class='windowBody']//table[@class='listTable']//nobr/span[@title='"
         + templateName + "']"));
      selenium.mouseDownAt("//div[@class='windowBody']//table[@class='listTable']//nobr/span[@title='" + templateName
         + "']", "2,2");
      selenium.mouseUpAt("//div[@class='windowBody']//table[@class='listTable']//nobr/span[@title='" + templateName
         + "']", "2,2");
      Thread.sleep(TestConstants.SLEEP_SHORT);

      if (fileName != null)
      {
         //type file name into name field
         selenium.type("scLocator=//DynamicForm[ID=\"ideCreateFileFromTemplateFormDynamicForm\"]/item["
            + "name=ideCreateFileFromTemplateFormFileNameField||title=File Name]/element", fileName);
      }

      //click Create Button
      selenium.click("scLocator=//IButton[ID=\"ideCreateFileFromTemplateFormCreateButton\"]/");
      Thread.sleep(TestConstants.SLEEP);
   }

   protected void checkIsTabPresentInEditorTabset(String tabTitle, boolean isOpened)
   {
      if (isOpened)
      {
         assertTrue(selenium.isElementPresent("//div[@id='isc_H']//div[@class='tabBar']//span[contains(text(), '"
            + tabTitle + "')]"));
      }
      else
      {
         assertFalse(selenium.isElementPresent("//div[@id='isc_H']//div[@class='tabBar']//span[contains(text(), '"
            + tabTitle + "')]"));
      }
   }
   
   protected void checkIsEditorTabSelected(String tabTitle, boolean isSelected)
   {
      if (isSelected)
      {
         assertTrue(selenium.isElementPresent("//div[@id='isc_H']//div[@class='tabBar']//td[@class='tabTitleSelected']/span[contains(text(), '" 
            + tabTitle + "')]"));
      }
      else
      {
         assertTrue(selenium.isElementPresent("//div[@id='isc_H']//div[@class='tabBar']//td[@class='tabTitle']/span[contains(text(), '" 
            + tabTitle + "')]"));
      }
    
   }
   
   /*
    * set the focus to hidden input
    */
   public void clearFocus() throws Exception
   {
      selenium
         .focus("//body/input[@class='gwt-TextBox' and contains(@style,'position: absolute; left: -100px; top: -100px;')]");
      Thread.sleep(TestConstants.REDRAW_PERIOD);
   }   

   //   @After
   //   public void tearDown()
   //   {
   ////      cleanRepository();
   ////      cleanRegistry();
   //   }
   protected static void cleanDefaultWorkspace()
   {
      cleanRepository(REST_CONTEXT + "/jcr/" + REPO_NAME + "/" + WS_NAME + "/");
   }

   protected static void cleanRepository(String repositoryUrl)
   {
      HTTPConnection connection;
      URL url;
      try
      {
         url = new URL(BASE_URL + repositoryUrl);
         connection = Utils.getConnection(url);
         HTTPResponse response = connection.PropfindAllprop(BASE_URL + repositoryUrl, 1);
         ByteArrayInputStream inputStream = new ByteArrayInputStream(response.getData());
         Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(inputStream);
         NodeList nodeList = document.getElementsByTagName("D:href");
         for (int i = 0; i < nodeList.getLength(); i++)
         {
            Node node = nodeList.item(i);
            String href = node.getFirstChild().getNodeValue();
            if (!href.equals(repositoryUrl))
            {
               connection = Utils.getConnection(url);
               response = connection.Delete(href);
            }

         }
      }
      catch (MalformedURLException e)
      {
         e.printStackTrace();
      }
      catch (ProtocolNotSuppException e)
      {
         e.printStackTrace();
      }
      catch (IOException e)
      {
         e.printStackTrace();
      }
      catch (ModuleException e)
      {
         e.printStackTrace();
      }
      catch (SAXException e)
      {
         e.printStackTrace();
      }
      catch (ParserConfigurationException e)
      {
         e.printStackTrace();
      }
      catch (FactoryConfigurationError e)
      {
         e.printStackTrace();
      }
   }

   protected static void cleanRegistry()
   {
      HTTPConnection connection;
      URL url;
      try
      {
         url = new URL(BASE_URL);
         connection = Utils.getConnection(url);
         connection.Delete(BASE_URL + "rest/private/registry/repository/exo:users/root/IDE");                                                                                          
         connection = Utils.getConnection(url);                                                                                                                                                                
         connection.Delete(BASE_URL + "rest/private/registry/repository/exo:applications/IDE");    
         
      }
      catch (MalformedURLException e)
      {
         e.printStackTrace();
      }
      catch (ProtocolNotSuppException e)
      {
         e.printStackTrace();
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

   @AfterClass
   public static void killFireFox()
   {
      try
      {
         if (System.getProperty("os.name").equals("Linux"))
         {
            Runtime.getRuntime().exec("killall firefox");
         }
      }
      catch (IOException e)
      {
         e.printStackTrace();
      }

   }

   public enum IdeAddress 
   {
      SHELL("http://127.0.0.1:8888/", "http://127.0.0.1:8888/org.exoplatform.ide.IDEShell/IDEShell.html?gwt.codesvr=127.0.0.1:9997"), 
      PORTAL("http://127.0.0.1:8080/", "http://127.0.0.1:8080/portal/private/classic/" + PAGE_NAME), 
      STANDALONE("http://127.0.0.1:8080/", "http://127.0.0.1:8080/IDE-application/IDE/IDEApplication.html");

      private String baseUrl;
      private String applicationUrl;

      IdeAddress(String baseUrl, String applicationUrl)
      {
         this.baseUrl = baseUrl;
         this.applicationUrl = applicationUrl;
      }

      public String getBaseUrl()
      {
         return this.baseUrl;
      }
      
      public String getApplicationUrl()
      {
         return this.applicationUrl;
      }
   }

   protected static boolean isRunIdeUnderPortal()
   {
      return APPLICATION_URL.equals(IdeAddress.PORTAL.getApplicationUrl());
   }     
 
   protected static boolean isRunTestUnderWindowsOS()
   {
      return selenium.getEval("/Win/.test(navigator.platform)").equals("true");
   }
   
   /**
    * Check is Outline Panel visible.
    * Note: you can't use this method, to check is Outline Panel visible,
    * when it appears in first time.
    * It is because to check, is Outline visible this method used
    * visibility attribute in style attribute of div[4].
    * But this attribute appears after that, when you hide outline.
    * 
    * @param isVisible
    */
   protected void checkOutlineVisibility(boolean isVisible)
   {
      if (isVisible)
      {
         assertFalse(selenium.isElementPresent("//div[@id='isc_H']/div[2]/div/div[4 "
            + "and contains(@style,'visibility: hidden;')]//div[@class='tabSetContainer']"));
         assertTrue(selenium.isElementPresent("//div[@id='isc_H']/div[2]/div/div[4 "
            + "and contains(@style,'visibility: inherit;')]//div[@class='tabSetContainer']"));
      }
      else
      {
         assertFalse(selenium.isElementPresent("//div[@id='isc_H']/div[2]/div/div[4 "
            + "and contains(@style,'visibility: inherit;')]//div[@class='tabSetContainer']"));
         assertTrue(selenium.isElementPresent("//div[@id='isc_H']/div[2]/div/div[4 "
            + "and contains(@style,'visibility: hidden;')]//div[@class='tabSetContainer']"));
      }
   }
   
   /**
    * 
    * @param tabIndex begins from 0
    * @return content panel locator 
    */
   protected String getContentPanelLocator(int tabIndex)
   {
      String divIndex = String.valueOf(tabIndex + 2);
      if (BROWSER_COMMAND.equals(EnumBrowserCommand.IE_EXPLORE_PROXY))
      {
         return "//div[@class='tabSetContainer']/div[" + divIndex + "]";
      }
      else
      {
         return "//div[@class='tabSetContainer']/div/div[" + divIndex + "]";         
      }
   }

   /**
    * remove all cookies which can be stored by IDE
    */
   protected static void deleteCookies()
   {     
      if (selenium.isCookiePresent("line-numbers_bool"))
      {
         selenium.deleteCookie("line-numbers_bool", "/");
      }
      if (selenium.isCookiePresent("opened-files_list"))
      {
         selenium.deleteCookie("opened-files_list", "/");
      }
      if (selenium.isCookiePresent("active-file_str"))
      {
         selenium.deleteCookie("active-file_str", "/");
      }
      if (selenium.isCookiePresent("line-numbers_bool"))
      {
         selenium.deleteCookie("line-numbers_bool", "/");
      }
      if(selenium.isCookiePresent("lock-tokens_map"))
      {
         selenium.deleteCookie("lock-tokens_map", "/");
      }
   }
}

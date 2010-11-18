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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.exoplatform.common.http.client.ModuleException;
import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.ToolbarCommands;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.exoplatform.ide.TestConstants;

/**
 * @author <a href="mailto:musienko.maxim@gmail.com">Musienko Maxim</a>
 * @version $Id: exo-jboss-codetemplates.xml 34360 2009-07-22 23:58:59Z aheritier $
 *
 */
public class OpeningSavingAndClosingTabsWithFilesTest extends BaseTest
{

   private static String FOLDER_NAME = OpeningSavingAndClosingTabsWithFilesTest.class.getSimpleName();

   private static String HTML_FILE_NAME = "newHtmlFile.html";

   private static String CSS_FILE_NAME = "newCssFile.css";

   private static String JS_FILE_NAME = "newJavaScriptFile.js";

   private static String GADGET_FILE_NAME = "newGoogleGadget.gadget";

   private static String GROOVY_FILE_NAME = "newGroovyFile.groovy";

   private static String XML_FILE_NAME = "newXMLFile.xml";

   private static String TXT_FILE_NAME = "newTextFile.txt";

   private final static String PATH = "src/test/resources/org/exoplatform/ide/operation/file/";

   private final static String STORAGE_URL = BASE_URL + REST_CONTEXT + "/" + WEBDAV_CONTEXT + "/" + REPO_NAME + "/"
      + WS_NAME + "/" + FOLDER_NAME + "/";

   @BeforeClass
   public static void setUp()
   {
      try
      {
         VirtualFileSystemUtils.mkcol(STORAGE_URL);
         VirtualFileSystemUtils.put(PATH + HTML_FILE_NAME, MimeType.TEXT_HTML, STORAGE_URL + HTML_FILE_NAME);
         VirtualFileSystemUtils.put(PATH + CSS_FILE_NAME, MimeType.TEXT_CSS, STORAGE_URL + CSS_FILE_NAME);
         VirtualFileSystemUtils.put(PATH + JS_FILE_NAME, MimeType.APPLICATION_JAVASCRIPT, STORAGE_URL + JS_FILE_NAME);
         VirtualFileSystemUtils.put(PATH + GADGET_FILE_NAME, MimeType.GOOGLE_GADGET, STORAGE_URL + GADGET_FILE_NAME);
         VirtualFileSystemUtils.put(PATH + GROOVY_FILE_NAME, MimeType.GROOVY_SERVICE, STORAGE_URL + GROOVY_FILE_NAME);
         VirtualFileSystemUtils.put(PATH + XML_FILE_NAME, MimeType.TEXT_XML, STORAGE_URL + XML_FILE_NAME);
         VirtualFileSystemUtils.put(PATH + TXT_FILE_NAME, MimeType.TEXT_PLAIN, STORAGE_URL + TXT_FILE_NAME);
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
   public static void tearDown()
   {
      try
      {
         VirtualFileSystemUtils.delete(STORAGE_URL + HTML_FILE_NAME);
         VirtualFileSystemUtils.delete(STORAGE_URL + CSS_FILE_NAME);
         VirtualFileSystemUtils.delete(STORAGE_URL + JS_FILE_NAME);
         VirtualFileSystemUtils.delete(STORAGE_URL + GADGET_FILE_NAME);
         VirtualFileSystemUtils.delete(STORAGE_URL + GROOVY_FILE_NAME);
         VirtualFileSystemUtils.delete(STORAGE_URL + XML_FILE_NAME);
         VirtualFileSystemUtils.delete(STORAGE_URL + TXT_FILE_NAME);
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
   public void testOpeningSavingAndClosingTabsWithFile() throws Exception
   {

      //------------3---------------------    
      // Refresh Workspace:
      Thread.sleep(TestConstants.SLEEP);
      selectItemInWorkspaceTree(WS_NAME);
      runToolbarButton(ToolbarCommands.File.REFRESH);
      selectItemInWorkspaceTree(FOLDER_NAME);
      runToolbarButton(ToolbarCommands.File.REFRESH);
      
      // check icons
      checkIcons();

      // reopenFiles();

      //------------------4------------------------      
      //check_hilight_code     
      openCss();
      chekHilightingInCssFile();
      Thread.sleep(TestConstants.REDRAW_PERIOD);
      closeTab("0");
      Thread.sleep(TestConstants.EDITOR_OPEN_PERIOD);

      openGooglegadget();
      checkHiligtGoofleGadget();
      closeTab("0");
      Thread.sleep(TestConstants.EDITOR_OPEN_PERIOD);

      openGroovy();
      checkHilightGroovy();
      closeTab("0");
      Thread.sleep(TestConstants.EDITOR_OPEN_PERIOD);

      openHtml();
      checkHilightHTML();
      closeTab("0");
      Thread.sleep(TestConstants.EDITOR_OPEN_PERIOD);

      openJavaScript();
      checkHilightJavaScript();
      closeTab("0");
      Thread.sleep(TestConstants.EDITOR_OPEN_PERIOD);

      openTXT();
      checkHiligtTXT();
      closeTab("0");
      Thread.sleep(TestConstants.EDITOR_OPEN_PERIOD);

      openXML();
      checkHilightXML();
      closeTab("0");
      Thread.sleep(TestConstants.EDITOR_OPEN_PERIOD);
      // ----------5--------------
      reopenFiles();
      clickTabAndCheckSaveButton();
      //------------6-------------      
      changeFiles();
      saveAndCloseFile();

      //--------------8------------
      reopenFiles();
      chekSaveInFiles();
   }

   public void chekSaveInFiles() throws InterruptedException
   {
      // check changed string in CSS file
      Thread.sleep(2000);
      String CSS =
         "Change file\n/*Some example CSS*/\n\n@import url (\"something.css\")\nbody {\n  margin 0;\n  padding 3em 6em;\n  font-family: tahoma, arial, sans-serif;\n  color #000;\n}\n  #navigation a {\n    font-weigt: bold;\n  text-decoration: none !important;\n}\n}";
      assertEquals(CSS, selenium.getText("//body[@class='editbox']"));
      selenium.click("scLocator=//TabSet[ID=\"ideEditorFormTabSet\"]/tab[index=0]/icon");
      Thread.sleep(2000);
      // check changed string in Google Gadget file
      String GG =
         "Change file\nChange file\n<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n<Module>\n  <ModulePrefs title=\"Hello World!\" />\n  <Content type=\"html\">\n    <![CDATA[ \n    <script type='text/javascript'>\n      function foo(bar, baz) {\n        alert('quux');\n        return bar + baz + 1;\n      }\n    </script>\n    <style type='text/css'>\n      div.border {\n        border: 1px solid black;\n        padding: 3px;\n      }\n      #foo code {\n        font-family: courier, monospace;\n        font-size: 80%;\n        color: #448888;\n      }\n    </style>\n    <p>Hello</p>\n    ]]></Content></Module>";
      assertEquals(GG, selenium.getText("//body[@class='editbox']"));
      selenium.click("scLocator=//TabSet[ID=\"ideEditorFormTabSet\"]/tab[index=0]/icon");
      Thread.sleep(2000);
      // check changed string in Groovy file
      String Groovy =
         "//simple groovy script\n\nimport javax.ws.rs.Path\nimport javax.ws.rs.GET\nimport javax.ws.rs.PathParam\n\n@Path (\"/\")\npublic class HelloWorld{\n@Get\n@Path (\"helloworld/{name}\")\npublic String hello(PathParam(\"name\")String name){\n  return \"Hello\"+name\n  }\n  }";
      assertEquals(Groovy, selenium.getText("//body[@class='editbox']"));
      selenium.click("scLocator=//TabSet[ID=\"ideEditorFormTabSet\"]/tab[index=0]/icon");
      Thread.sleep(2000);
      // check changed string in HTML file
      String HTML =
         "Change file\n<html>\n<head>\n  <title>HTML Example</title>\n  <script type='text/javascript'>\n    function foo(bar, baz) {\n      alert('quux');\n      return bar + baz + 1;\n    }\n  </script>\n  <style type='text/css'>\n    div.border {\n      border: 1px solid black;\n      padding: 3px;\n    }\n    #foo code {\n      font-family: courier, monospace;\n      font-size: 80%;\n      color: #448888;\n    }\n  </style>\n</head>\n<body>\n  <p>Hello</p>\n</body>\n</html>";
      assertEquals(HTML, selenium.getText("//body[@class='editbox']"));
      selenium.click("scLocator=//TabSet[ID=\"ideEditorFormTabSet\"]/tab[index=0]/icon");
      Thread.sleep(2000);
    // check changed string in JS file
      String JS = "Change file\n  //Here you see some JavaScript code. Mess around with it to get\n//acquinted with CodeMirror's features.\n\n// Press enter inside the objects and your new line will \n// intended.\n\nvar keyBindings ={\n  enter:\"newline-and-indent\",\n  tab:\"reindent-selection\",\n  ctrl_z \"undo\",\n  ctrl_y:\"redo\"\n  };\n  var regex =/foo|bar/i;\n  function example (x){\n  var y=44.4;\n  return x+y;\n  }";
      assertEquals(JS, selenium.getText("//body[@class='editbox']"));
      selenium.click("scLocator=//TabSet[ID=\"ideEditorFormTabSet\"]/tab[index=0]/icon");
      Thread.sleep(2000);
     // check changed string in TXT file
      String TXT = "text content";
      assertEquals(TXT, selenium.getText("//body[@class='editbox']"));
      selenium.click("scLocator=//TabSet[ID=\"ideEditorFormTabSet\"]/tab[index=0]/icon");
      Thread.sleep(2000);
   }

   public void reopenFiles() throws InterruptedException, Exception
   {
      // Open CSS:
      openCss();

      // Open GoogleGadged:
      openGooglegadget();

      //Open Groovy:
      openGroovy();

      //Open HTML:
      openHtml();

      //Open JavaScript:
      openJavaScript();

      //Open Txt:
      openTXT();

      //Open XML:
      openXML();
   }

   public void reopenAndChekSavedFile() throws InterruptedException
   {
      // ------8-------
      // reopenFiles
      // Open Css:
      Thread.sleep(TestConstants.EDITOR_OPEN_PERIOD);
      selenium.click("scLocator=//TreeGrid[ID=\"ideNavigatorItemTreeGrid\"]/body/row[3]/col[1]");
      selenium.mouseDownAt("//td[@class='exo-menuBarItem' and @menubartitle='File']", "");
      selenium.mouseDownAt("//td[@class='exo-popupMenuTitleField']/nobr[contains(text(), \"Open With...\")]", "");
      Thread.sleep(2000);
      assertTrue(selenium.isTextPresent("Open File With"));
      assertTrue(selenium.isTextPresent("Name"));
      assertTrue(selenium.isTextPresent("Code Editor [Default]"));
      assertTrue(selenium.isTextPresent("Use as default editor"));
      assertTrue(selenium
         .isElementPresent("scLocator=//Window[ID=\"ideallOpenFileWithForm\"]/item[0][Class=\"ListGrid\"]/body/row[0]/col[0]"));
      selenium
         .click("scLocator=//Window[ID=\"ideallOpenFileWithForm\"]/item[0][Class=\"ListGrid\"]/body/row[0]/col[0]");
      selenium.click("scLocator=//IButton[ID=\"ideOpenFileWithOkButton\"]/end");
      Thread.sleep(4000);
      assertTrue(selenium.isElementPresent("//div[@class='tabSetContainer']/div/div[2]//iframe"));
      // Open HTML:
      selenium.click("scLocator=//TreeGrid[ID=\"ideNavigatorItemTreeGrid\"]/body/row[6]/col[1]");
      selenium.mouseDownAt("//td[@class='exo-menuBarItem' and @menubartitle='File']", "");
      selenium.mouseDownAt("//td[@class='exo-popupMenuTitleField']/nobr[contains(text(), \"Open With...\")]", "");
      Thread.sleep(2000);
      assertTrue(selenium.isTextPresent("Open File With"));
      assertTrue(selenium.isTextPresent("Name"));
      assertTrue(selenium.isTextPresent("Code Editor [Default]"));
      assertTrue(selenium.isTextPresent("Use as default editor"));
      assertTrue(selenium
         .isElementPresent("scLocator=//Window[ID=\"ideallOpenFileWithForm\"]/item[0][Class=\"ListGrid\"]/body/row[0]/col[0]"));
      selenium
         .click("scLocator=//Window[ID=\"ideallOpenFileWithForm\"]/item[0][Class=\"ListGrid\"]/body/row[0]/col[0]");
      selenium.click("scLocator=//IButton[ID=\"ideOpenFileWithOkButton\"]/end");
      Thread.sleep(3000);
      assertTrue(selenium.isElementPresent("//div[@class='tabSetContainer']/div/div[4]//iframe"));
      // Open JavaScript:
      selenium.click("scLocator=//TreeGrid[ID=\"ideNavigatorItemTreeGrid\"]/body/row[7]/col[1]");
      selenium.mouseDownAt("//td[@class='exo-menuBarItem' and @menubartitle='File']", "");
      selenium.mouseDownAt("//td[@class='exo-popupMenuTitleField']/nobr[contains(text(), \"Open With...\")]", "");
      Thread.sleep(2000);
      assertTrue(selenium.isTextPresent("Open File With"));
      assertTrue(selenium.isTextPresent("Name"));
      assertTrue(selenium.isTextPresent("Code Editor [Default]"));
      assertTrue(selenium.isTextPresent("Use as default editor"));
      assertTrue(selenium
         .isElementPresent("scLocator=//Window[ID=\"ideallOpenFileWithForm\"]/item[0][Class=\"ListGrid\"]/body/row[0]/col[0]"));
      selenium
         .click("scLocator=//Window[ID=\"ideallOpenFileWithForm\"]/item[0][Class=\"ListGrid\"]/body/row[0]/col[0]");
      selenium.click("scLocator=//IButton[ID=\"ideOpenFileWithOkButton\"]/end");
      Thread.sleep(3000);
      assertTrue(selenium.isElementPresent("//div[@class='tabSetContainer']/div/div[5]//iframe"));
      // Open XML:
      selenium.click("scLocator=//TreeGrid[ID=\"ideNavigatorItemTreeGrid\"]/body/row[9]/col[1]");
      selenium.mouseDownAt("//td[@class='exo-menuBarItem' and @menubartitle='File']", "");
      selenium.mouseDownAt("//td[@class='exo-popupMenuTitleField']/nobr[contains(text(), \"Open With...\")]", "");
      Thread.sleep(2000);
      assertTrue(selenium.isTextPresent("Open File With"));
      assertTrue(selenium.isTextPresent("Name"));
      assertTrue(selenium.isTextPresent("Code Editor [Default]"));
      assertTrue(selenium.isTextPresent("Use as default editor"));
      assertTrue(selenium
         .isElementPresent("scLocator=//Window[ID=\"ideallOpenFileWithForm\"]/item[0][Class=\"ListGrid\"]/body/row[0]/col[0]"));
      selenium
         .click("scLocator=//Window[ID=\"ideallOpenFileWithForm\"]/item[0][Class=\"ListGrid\"]/body/row[0]/col[0]");
      selenium.click("scLocator=//IButton[ID=\"ideOpenFileWithOkButton\"]/end");
      Thread.sleep(3000);
      assertTrue(selenium.isElementPresent("//div[@class='tabSetContainer']/div/div[6]//iframe"));
   }

   public void saveAndCloseFile() throws InterruptedException, Exception
   {
      // Save and closeCssFile
      selenium.click("scLocator=//TabSet[ID=\"ideEditorFormTabSet\"]/tab[index=0]/");
      Thread.sleep(2000);
      saveCurrentFile();
      Thread.sleep(2000);
      selenium.click("scLocator=//TabSet[ID=\"ideEditorFormTabSet\"]/tab[index=0]/icon");
      Thread.sleep(2000);

      // Save and closeGoogleGadgetFile
      selenium.click("scLocator=//TabSet[ID=\"ideEditorFormTabSet\"]/tab[index=0]/");
      Thread.sleep(2000);
      saveCurrentFile();
      Thread.sleep(2000);
      selenium.click("scLocator=//TabSet[ID=\"ideEditorFormTabSet\"]/tab[index=0]/icon");
      Thread.sleep(2000);

      // Saveand close HTMLFile
      selenium.click("scLocator=//TabSet[ID=\"ideEditorFormTabSet\"]/tab[index=1]/");
      Thread.sleep(2000);
      saveCurrentFile();
      Thread.sleep(2000);
      selenium.click("scLocator=//TabSet[ID=\"ideEditorFormTabSet\"]/tab[index=1]/icon");
      Thread.sleep(2000);

      // Saveand closeJsFile
      selenium.click("scLocator=//TabSet[ID=\"ideEditorFormTabSet\"]/tab[index=1]/");
      Thread.sleep(2000);
      saveCurrentFile();
      Thread.sleep(2000);
      selenium.click("scLocator=//TabSet[ID=\"ideEditorFormTabSet\"]/tab[index=1]/icon");
      Thread.sleep(2000);

      // Saveand closeXMLFile
      selenium.click("scLocator=//TabSet[ID=\"ideEditorFormTabSet\"]/tab[index=2]/");
      Thread.sleep(2000);
      saveCurrentFile();
      Thread.sleep(2000);
      selenium.click("scLocator=//TabSet[ID=\"ideEditorFormTabSet\"]/tab[index=2]/icon");
      Thread.sleep(2000);

      // close GroovyFile
      selenium.click("scLocator=//TabSet[ID=\"ideEditorFormTabSet\"]/tab[index=0]/");
      Thread.sleep(500);
      selenium.click("scLocator=//TabSet[ID=\"ideEditorFormTabSet\"]/tab[index=0]/icon");
      Thread.sleep(2000);

      // close TXTFile
      selenium.click("scLocator=//TabSet[ID=\"ideEditorFormTabSet\"]/tab[index=0]/");
      Thread.sleep(500);
      selenium.click("scLocator=//TabSet[ID=\"ideEditorFormTabSet\"]/tab[index=0]/icon");
      Thread.sleep(2000);
      assertFalse(selenium.isElementPresent("//body[@class='editbox']"));
   }

   public void changeFiles() throws InterruptedException
   {
      // changeCssFile
      selenium.selectFrame("//div[@class='tabSetContainer']/div/div[2]//iframe");
      selenium.typeKeys("//body[@class='editbox']", "Change file");
      selenium.keyDown("//body[@class='editbox']", "13");
      selenium.keyUp("//body[@class='editbox']", "13");
      Thread.sleep(500);
      selenium.selectFrame("relative=top");
      assertTrue(selenium.isTextPresent(CSS_FILE_NAME + "*"));
      Thread.sleep(3000);

      // changeGoogleGadgetFile
      selenium.selectFrame("//div[@class='tabSetContainer']/div/div[3]//iframe");
      selenium.typeKeys("//body[@class='editbox']", "Change file");
      selenium.keyDown("//body[@class='editbox']", "13");
      selenium.keyUp("//body[@class='editbox']", "13");
      Thread.sleep(500);
      selenium.selectFrame("relative=top");
      Thread.sleep(3000);
     
      // changeGoogleGadgetFile
      selenium.selectFrame("//div[@class='tabSetContainer']/div/div[3]//iframe");
      selenium.typeKeys("//body[@class='editbox']", "Change file");
      selenium.keyDown("//body[@class='editbox']", "13");
      selenium.keyUp("//body[@class='editbox']", "13");
      Thread.sleep(500);
      selenium.selectFrame("relative=top");
      Thread.sleep(3000);
      assertTrue(selenium.isTextPresent(GADGET_FILE_NAME+"*"));
     
      // changeHTMLFile
      selenium.selectFrame("//div[@class='tabSetContainer']/div/div[5]//iframe");
      selenium.typeKeys("//body[@class='editbox']", "Change file");
      selenium.keyDown("//body[@class='editbox']", "13");
      selenium.keyUp("//body[@class='editbox']", "13");
      Thread.sleep(500);
      selenium.selectFrame("relative=top");
      Thread.sleep(3000);
      assertTrue(selenium.isTextPresent(HTML_FILE_NAME + "*"));
      
      // changeJavaScriptFile
      selenium.selectFrame("//div[@class='tabSetContainer']/div/div[6]//iframe");
      selenium.typeKeys("//body[@class='editbox']", "Change file");
      selenium.keyDown("//body[@class='editbox']", "13");
      selenium.keyUp("//body[@class='editbox']", "13");
      Thread.sleep(500);
      selenium.selectFrame("relative=top");
      Thread.sleep(3000);
      assertTrue(selenium.isTextPresent(JS_FILE_NAME+"*"));
     
      // changeXMLFile
      selenium.selectFrame("//div[@class='tabSetContainer']/div/div[8]//iframe");
      selenium.typeKeys("//body[@class='editbox']", "Change file");
      selenium.keyDown("//body[@class='editbox']", "13");
      selenium.keyUp("//body[@class='editbox']", "13");
      Thread.sleep(500);
      selenium.selectFrame("relative=top");
      Thread.sleep(3000);
      assertTrue(selenium.isTextPresent(XML_FILE_NAME+"*"));
      Thread.sleep(500);
   }

   public void clickTabAndCheckSaveButton() throws InterruptedException
   {
      selenium.click("scLocator=//TabSet[ID=\"ideEditorFormTabSet\"]/tab[index=0]/");
      Thread.sleep(2000);
      assertTrue(selenium.isElementPresent("//div[@title='Save']/div[@elementenabled='false']"));
      // click_tab_and_check_SaveButton _2
      selenium.click("scLocator=//TabSet[ID=\"ideEditorFormTabSet\"]/tab[index=1]/");
      Thread.sleep(2000);
      assertTrue(selenium.isElementPresent("//div[@title='Save']/div[@elementenabled='false']"));
      // click_tab_and_check_SaveButton _3
      selenium.click("scLocator=//TabSet[ID=\"ideEditorFormTabSet\"]/tab[index=2]/");
      Thread.sleep(2000);
      assertTrue(selenium.isElementPresent("//div[@title='Save']/div[@elementenabled='false']"));
      // click_tab_and_check_SaveButton _4
      selenium.click("scLocator=//TabSet[ID=\"ideEditorFormTabSet\"]/tab[index=3]/");
      Thread.sleep(2000);
      assertTrue(selenium.isElementPresent("//div[@title='Save']/div[@elementenabled='false']"));
      // click_tab_and_check_SaveButton _5
      selenium.click("scLocator=//TabSet[ID=\"ideEditorFormTabSet\"]/tab[index=4]/");
      Thread.sleep(2000);
      assertTrue(selenium.isElementPresent("//div[@title='Save']/div[@elementenabled='false']"));
      // click_tab_and_check_SaveButton _6
      selenium.click("scLocator=//TabSet[ID=\"ideEditorFormTabSet\"]/tab[index=5]/");
      Thread.sleep(2000);
      assertTrue(selenium.isElementPresent("//div[@title='Save']/div[@elementenabled='false']"));
      // click_tab_and_check_SaveButton _7
      selenium.click("scLocator=//TabSet[ID=\"ideEditorFormTabSet\"]/tab[index=6]/");
      Thread.sleep(2000);
      assertTrue(selenium.isElementPresent("//div[@title='Save']/div[@elementenabled='false']"));
   }

   public void openXML() throws InterruptedException, Exception
   {
      Thread.sleep(TestConstants.EDITOR_OPEN_PERIOD);
      selectItemInWorkspaceTree(WS_NAME);
    //  runTopMenuCommand(MenuCommands.File.FILE, MenuCommands.File.REFRESH);
      Thread.sleep(TestConstants.EDITOR_OPEN_PERIOD);
      openFileFromNavigationTreeWithCodeEditor(XML_FILE_NAME, false);
      Thread.sleep(2000);
   }

   public void openTXT() throws InterruptedException, Exception
   {
      Thread.sleep(TestConstants.EDITOR_OPEN_PERIOD);
      selectItemInWorkspaceTree(WS_NAME);
     // runTopMenuCommand(MenuCommands.File.FILE, MenuCommands.File.REFRESH);
      Thread.sleep(TestConstants.EDITOR_OPEN_PERIOD);
      openFileFromNavigationTreeWithCodeEditor(TXT_FILE_NAME, false);
      Thread.sleep(2000);
   }

   public void openJavaScript() throws InterruptedException, Exception
   {
      Thread.sleep(TestConstants.EDITOR_OPEN_PERIOD);
      selectItemInWorkspaceTree(WS_NAME);
    //  runTopMenuCommand(MenuCommands.File.FILE, MenuCommands.File.REFRESH);
      Thread.sleep(TestConstants.EDITOR_OPEN_PERIOD);
      openFileFromNavigationTreeWithCodeEditor(JS_FILE_NAME, false);
      Thread.sleep(2000);
   }

   public void openHtml() throws InterruptedException, Exception
   {
      Thread.sleep(TestConstants.EDITOR_OPEN_PERIOD);
      selectItemInWorkspaceTree(WS_NAME);
  //    runTopMenuCommand(MenuCommands.File.FILE, MenuCommands.File.REFRESH);
      Thread.sleep(TestConstants.EDITOR_OPEN_PERIOD);
      openFileFromNavigationTreeWithCodeEditor(HTML_FILE_NAME, false);
      Thread.sleep(2000);
   }

   public void openGroovy() throws InterruptedException, Exception
   {
      Thread.sleep(TestConstants.EDITOR_OPEN_PERIOD);
      selectItemInWorkspaceTree(WS_NAME);
      //runTopMenuCommand(MenuCommands.File.FILE, MenuCommands.File.REFRESH);
      openFileFromNavigationTreeWithCodeEditor(GROOVY_FILE_NAME, false);
      Thread.sleep(2000);
   }

   public void openGooglegadget() throws InterruptedException, Exception
   {
      Thread.sleep(TestConstants.EDITOR_OPEN_PERIOD);
      selectItemInWorkspaceTree(WS_NAME);
     // runTopMenuCommand(MenuCommands.File.FILE, MenuCommands.File.REFRESH);
      Thread.sleep(TestConstants.EDITOR_OPEN_PERIOD);
      openFileFromNavigationTreeWithCodeEditor(GADGET_FILE_NAME, false);
      Thread.sleep(2000);
   }

   public void openCss() throws InterruptedException, Exception
   {
      Thread.sleep(TestConstants.EDITOR_OPEN_PERIOD);
      openFileFromNavigationTreeWithCodeEditor(CSS_FILE_NAME, false);
   }

   public void checkHilightXML()
   {
     // selenium.selectFrame("relative=top");
      //selenium.selectFrame("//div[@class='tabSetContainer']/div/div[8]//iframe");
      assertTrue(selenium
         .isElementPresent("//body[@class='editbox']/span[1][@class='xml-processing' and text()=\"<?xml \"]"));
      assertTrue(selenium
         .isElementPresent("//body[@class='editbox']/span[3][@class='xml-punctuation' and text()=\"<\"]"));
      assertTrue(selenium
         .isElementPresent("//body[@class='editbox']/span[4][@class='xml-tagname' and text()=\"Module\"]"));
      assertTrue(selenium
         .isElementPresent("//body[@class='editbox']/span[5][@class='xml-punctuation' and text()=\">\"]"));
      assertTrue(selenium
         .isElementPresent("//body[@class='editbox']/span[7][@class='xml-punctuation' and text()=\"<\"]"));
      assertTrue(selenium
         .isElementPresent("//body[@class='editbox']/span[8][@class='xml-tagname' and text()=\"ModulePrefs \"]"));
      assertTrue(selenium
         .isElementPresent("//body[@class='editbox']/span[9][@class='xml-attname' and text()=\"author\"]"));
      assertTrue(selenium
         .isElementPresent("//body[@class='editbox']/span[10][@class='xml-punctuation' and text()=\"=\"]"));
      assertTrue(selenium
         .isElementPresent("//body[@class='editbox']/span[13][@class='xml-attname' and text()=\"author_email\"]"));
      assertTrue(selenium
         .isElementPresent("//body[@class='editbox']/span[14][@class='xml-punctuation' and text()=\"=\"]"));
      assertTrue(selenium
         .isElementPresent("//body[@class='editbox']/span[17][@class='xml-attname' and text()=\"title\"]"));
      assertTrue(selenium
         .isElementPresent("//body[@class='editbox']/span[37][@class='xml-attname' and text()=\"<Locale \"]"));
      assertTrue(selenium
         .isElementPresent("//body[@class='editbox']/span[44][@class='xml-punctuation' and text()=\">\"]"));
      selenium.selectFrame("relative=top");
   }

   public void checkHiligtTXT()
   {
      //selenium.selectFrame("relative=top");
     // selenium.selectFrame("//div[@class='tabSetContainer']/div/div[7]//iframe");
      assertTrue(selenium
         .isElementPresent("//body[@class='editbox']/span[1][@class='css-selector' and text()=\"text \"]"));
      assertTrue(selenium
         .isElementPresent("//body[@class='editbox']/span[2][@class='css-selector' and text()=\"content\"]"));
   }

   public void checkHilightJavaScript()
   {
      assertTrue(selenium
         .isElementPresent("//body[@class='editbox']/span[1][@class='js-comment' and text()=\"//Here you see some JavaScript code. Mess around with it to get\"]"));
      assertTrue(selenium.isElementPresent("//body[@class='editbox']/span[5][@class='js-keyword' and text()=\"var \"]"));
      assertTrue(selenium
         .isElementPresent("//body[@class='editbox']/span[6][@class='js-variable' and text()=\"keyBindings \"]"));
      assertTrue(selenium.isElementPresent("//body[@class='editbox']/span[7][@class='js-operator' and text()=\"=\"]"));
      assertTrue(selenium
         .isElementPresent("//body[@class='editbox']/span[8][@class='js-punctuation' and text()=\"{\"]"));
      assertTrue(selenium
         .isElementPresent("//body[@class='editbox']/span[10][@class='js-property' and text()=\"enter\"]"));
      assertTrue(selenium
         .isElementPresent("//body[@class='editbox']/span[11][@class='js-punctuation' and text()=\":\"]"));
      assertTrue(selenium
         .isElementPresent("//body[@class='editbox']/span[12][@class='js-string' and text()='\"newline-and-indent\"']"));
      assertTrue(selenium
         .isElementPresent("//body[@class='editbox']/span[13][@class='js-punctuation' and text()=\",\"]"));
      assertTrue(selenium
         .isElementPresent("//body[@class='editbox']/span[15][@class='js-property' and text()=\"tab\"]"));
      assertTrue(selenium
         .isElementPresent("//body[@class='editbox']/span[16][@class='js-punctuation' and text()=\":\"]"));
      assertTrue(selenium
         .isElementPresent("//body[@class='editbox']/span[17][@class='js-string' and text()='\"reindent-selection\"']"));
      assertTrue(selenium
         .isElementPresent("//body[@class='editbox']/span[18][@class='js-punctuation' and text()=\",\"]"));
      assertTrue(selenium
         .isElementPresent("//body[@class='editbox']/span[20][@class='js-property' and text()=\"ctrl_z \"]"));
      assertTrue(selenium
         .isElementPresent("//body[@class='editbox']/span[21][@class='js-string' and text()='\"undo\"']"));
      assertTrue(selenium
         .isElementPresent("//body[@class='editbox']/span[22][@class='js-punctuation' and text()=\",\"]"));
      assertTrue(selenium
         .isElementPresent("//body[@class='editbox']/span[24][@class='js-variable' and text()=\"ctrl_y\"]"));
      assertTrue(selenium
         .isElementPresent("//body[@class='editbox']/span[25][@class='js-punctuation' and text()=\":\"]"));
      assertTrue(selenium
         .isElementPresent("//body[@class='editbox']/span[26][@class='js-string' and text()='\"redo\"']"));
      assertTrue(selenium
         .isElementPresent("//body[@class='editbox']/span[28][@class='js-punctuation' and text()=\"}\"]"));
      assertTrue(selenium
         .isElementPresent("//body[@class='editbox']/span[29][@class='js-punctuation' and text()=\";\"]"));
      assertTrue(selenium
         .isElementPresent("//body[@class='editbox']/span[31][@class='js-keyword' and text()=\"var \"]"));
      assertTrue(selenium
         .isElementPresent("//body[@class='editbox']/span[32][@class='js-variable' and text()=\"regex \"]"));
      assertTrue(selenium.isElementPresent("//body[@class='editbox']/span[33][@class='js-operator' and text()=\"=\"]"));
      assertTrue(selenium
         .isElementPresent("//body[@class='editbox']/span[34][@class='js-string' and text()=\"/foo|bar/i\"]"));
      assertTrue(selenium
         .isElementPresent("//body[@class='editbox']/span[35][@class='js-punctuation' and text()=\";\"]"));
      assertTrue(selenium
         .isElementPresent("//body[@class='editbox']/span[37][@class='js-keyword' and text()=\"function \"]"));
      assertTrue(selenium
         .isElementPresent("//body[@class='editbox']/span[38][@class='js-variable' and text()=\"example \"]"));
      assertTrue(selenium
         .isElementPresent("//body[@class='editbox']/span[39][@class='js-punctuation' and text()=\"(\"]"));
      assertTrue(selenium
         .isElementPresent("//body[@class='editbox']/span[40][@class='js-variabledef' and text()=\"x\"]"));
      assertTrue(selenium
         .isElementPresent("//body[@class='editbox']/span[41][@class='js-punctuation' and text()=\")\"]"));
      assertTrue(selenium
         .isElementPresent("//body[@class='editbox']/span[42][@class='js-punctuation' and text()=\"{\"]"));
      assertTrue(selenium
         .isElementPresent("//body[@class='editbox']/span[44][@class='js-keyword' and text()=\"var \"]"));
      assertTrue(selenium
         .isElementPresent("//body[@class='editbox']/span[45][@class='js-variabledef' and text()=\"y\"]"));
      assertTrue(selenium.isElementPresent("//body[@class='editbox']/span[46][@class='js-operator' and text()=\"=\"]"));
      assertTrue(selenium.isElementPresent("//body[@class='editbox']/span[47][@class='js-atom' and text()=\"44.4\"]"));
      assertTrue(selenium
         .isElementPresent("//body[@class='editbox']/span[48][@class='js-punctuation' and text()=\";\"]"));
      assertTrue(selenium
         .isElementPresent("//body[@class='editbox']/span[50][@class='js-keyword' and text()=\"return \"]"));
      assertTrue(selenium
         .isElementPresent("//body[@class='editbox']/span[51][@class='js-localvariable' and text()=\"x\"]"));
      assertTrue(selenium.isElementPresent("//body[@class='editbox']/span[52][@class='js-operator' and text()=\"+\"]"));
      assertTrue(selenium
         .isElementPresent("//body[@class='editbox']/span[53][@class='js-localvariable' and text()=\"y\"]"));
      assertTrue(selenium
         .isElementPresent("//body[@class='editbox']/span[54][@class='js-punctuation' and text()=\";\"]"));
      assertTrue(selenium
         .isElementPresent("//body[@class='editbox']/span[56][@class='js-punctuation' and text()=\"}\"]"));
   }

   public void checkHilightHTML()
   {
//      selenium.selectFrame("//div[@class='tabSetContainer']/div/div[5]//iframe");
      assertTrue(selenium
         .isElementPresent("//body[@class='editbox']/span[1][@class='xml-punctuation' and text()=\"<\"]"));
      assertTrue(selenium
         .isElementPresent("//body[@class='editbox']/span[2][@class='xml-tagname' and text()=\"html\"]"));
      assertTrue(selenium
         .isElementPresent("//body[@class='editbox']/span[3][@class='xml-punctuation' and text()=\">\"]"));
      assertTrue(selenium
         .isElementPresent("//body[@class='editbox']/span[4][@class='xml-punctuation' and text()=\"<\"]"));
      assertTrue(selenium
         .isElementPresent("//body[@class='editbox']/span[5][@class='xml-tagname' and text()=\"head\"]"));
      assertTrue(selenium
         .isElementPresent("//body[@class='editbox']/span[6][@class='xml-punctuation' and text()=\">\"]"));
      assertTrue(selenium
         .isElementPresent("//body[@class='editbox']/span[8][@class='xml-punctuation' and text()=\"<\"]"));
      assertTrue(selenium
         .isElementPresent("//body[@class='editbox']/span[9][@class='xml-tagname' and text()=\"title\"]"));
      assertTrue(selenium
         .isElementPresent("//body[@class='editbox']/span[10][@class='xml-punctuation' and text()=\">\"]"));
      assertTrue(selenium
         .isElementPresent("//body[@class='editbox']/span[11][@class='xml-text' and text()=\"HTML Example\"]"));
      assertTrue(selenium
         .isElementPresent("//body[@class='editbox']/span[12][@class='xml-punctuation' and text()=\"</\"]"));
      assertTrue(selenium
         .isElementPresent("//body[@class='editbox']/span[13][@class='xml-tagname' and text()=\"title\"]"));
      assertTrue(selenium
         .isElementPresent("//body[@class='editbox']/span[14][@class='xml-punctuation' and text()=\">\"]"));
      // ---------------------------------------
      assertTrue(selenium
         .isElementPresent("//body[@class='editbox']/span[16][@class='xml-punctuation' and text()=\"<\"]"));
      assertTrue(selenium
         .isElementPresent("//body[@class='editbox']/span[17][@class='xml-tagname' and text()=\"script \"]"));
      assertTrue(selenium
         .isElementPresent("//body[@class='editbox']/span[18][@class='xml-attname' and text()=\"type\"]"));
      assertTrue(selenium
         .isElementPresent("//body[@class='editbox']/span[19][@class='xml-punctuation' and text()=\"=\"]"));
      assertTrue(selenium
         .isElementPresent("//body[@class='editbox']/span[20][@class='xml-attribute' and text()=\"'text/javascript'\"]"));
      assertTrue(selenium
         .isElementPresent("//body[@class='editbox']/span[23][@class='js-keyword' and text()=\"function \"]"));
      assertTrue(selenium
         .isElementPresent("//body[@class='editbox']/span[24][@class='js-variable' and text()=\"foo\"]"));
      assertTrue(selenium
         .isElementPresent("//body[@class='editbox']/span[25][@class='js-punctuation' and text()=\"(\"]"));
      assertTrue(selenium
         .isElementPresent("//body[@class='editbox']/span[26][@class='js-variabledef' and text()=\"bar\"]"));
      assertTrue(selenium
         .isElementPresent("//body[@class='editbox']/span[27][@class='js-punctuation' and text()=\", \"]"));
      assertTrue(selenium
         .isElementPresent("//body[@class='editbox']/span[28][@class='js-variabledef' and text()=\"baz\"]"));
      assertTrue(selenium
         .isElementPresent("//body[@class='editbox']/span[29][@class='js-punctuation' and text()=\") \"]"));
      assertTrue(selenium
         .isElementPresent("//body[@class='editbox']/span[30][@class='js-punctuation' and text()=\"{\"]"));
      assertTrue(selenium
         .isElementPresent("//body[@class='editbox']/span[32][@class='js-variable' and text()=\"alert\"]"));
      assertTrue(selenium
         .isElementPresent("//body[@class='editbox']/span[33][@class='js-punctuation' and text()=\"(\"]"));
      assertTrue(selenium
         .isElementPresent("//body[@class='editbox']/span[34][@class='js-string' and text()=\"'quux'\"]"));
      assertTrue(selenium
         .isElementPresent("//body[@class='editbox']/span[35][@class='js-punctuation' and text()=\")\"]"));
      assertTrue(selenium
         .isElementPresent("//body[@class='editbox']/span[36][@class='js-punctuation' and text()=\";\"]"));
      assertTrue(selenium
         .isElementPresent("//body[@class='editbox']/span[38][@class='js-keyword' and text()=\"return \"]"));
      assertTrue(selenium
         .isElementPresent("//body[@class='editbox']/span[39][@class='js-localvariable' and text()=\"bar \"]"));
      assertTrue(selenium.isElementPresent("//body[@class='editbox']/span[40][@class='js-operator' and text()=\"+ \"]"));
      assertTrue(selenium
         .isElementPresent("//body[@class='editbox']/span[41][@class='js-localvariable' and text()=\"baz \"]"));
      assertTrue(selenium.isElementPresent("//body[@class='editbox']/span[42][@class='js-operator' and text()=\"+ \"]"));
      assertTrue(selenium.isElementPresent("//body[@class='editbox']/span[43][@class='js-atom' and text()=\"1\"]"));
      assertTrue(selenium
         .isElementPresent("//body[@class='editbox']/span[44][@class='js-punctuation' and text()=\";\"]"));
      assertTrue(selenium
         .isElementPresent("//body[@class='editbox']/span[46][@class='js-punctuation' and text()=\"}\"]"));
      // --------------------------------------------
      assertTrue(selenium
         .isElementPresent("//body[@class='editbox']/span[48][@class='xml-punctuation' and text()=\"</\"]"));
      assertTrue(selenium
         .isElementPresent("//body[@class='editbox']/span[49][@class='xml-tagname' and text()=\"script\"]"));
      assertTrue(selenium
         .isElementPresent("//body[@class='editbox']/span[50][@class='xml-punctuation' and text()=\">\"]"));
      assertTrue(selenium
         .isElementPresent("//body[@class='editbox']/span[52][@class='xml-punctuation' and text()=\"<\"]"));
      assertTrue(selenium
         .isElementPresent("//body[@class='editbox']/span[53][@class='xml-tagname' and text()=\"style \"]"));
      assertTrue(selenium
         .isElementPresent("//body[@class='editbox']/span[54][@class='xml-attname' and text()=\"type\"]"));
      assertTrue(selenium
         .isElementPresent("//body[@class='editbox']/span[55][@class='xml-punctuation' and text()=\"=\"]"));
      assertTrue(selenium
         .isElementPresent("//body[@class='editbox']/span[56][@class='xml-attribute' and text()=\"'text/css'\"]"));
      assertTrue(selenium
         .isElementPresent("//body[@class='editbox']/span[59][@class='css-selector' and text()=\"div\"]"));
      assertTrue(selenium
         .isElementPresent("//body[@class='editbox']/span[60][@class='css-select-op' and text()=\".\"]"));
      assertTrue(selenium
         .isElementPresent("//body[@class='editbox']/span[61][@class='css-selector' and text()=\"border \"]"));
      assertTrue(selenium
         .isElementPresent("//body[@class='editbox']/span[62][@class='css-punctuation' and text()=\"{\"]"));
      assertTrue(selenium
         .isElementPresent("//body[@class='editbox']/span[64][@class='css-identifier' and text()=\"border\"]"));
      assertTrue(selenium
         .isElementPresent("//body[@class='editbox']/span[65][@class='css-punctuation' and text()=\": \"]"));
      assertTrue(selenium.isElementPresent("//body[@class='editbox']/span[66][@class='css-unit' and text()=\"1px \"]"));
      assertTrue(selenium
         .isElementPresent("//body[@class='editbox']/span[67][@class='css-value' and text()=\"solid \"]"));
      assertTrue(selenium
         .isElementPresent("//body[@class='editbox']/span[68][@class='css-value' and text()=\"black\"]"));
      assertTrue(selenium
         .isElementPresent("//body[@class='editbox']/span[69][@class='css-punctuation' and text()=\";\"]"));
      assertTrue(selenium
         .isElementPresent("//body[@class='editbox']/span[71][@class='css-identifier' and text()=\"padding\"]"));
      assertTrue(selenium
         .isElementPresent("//body[@class='editbox']/span[72][@class='css-punctuation' and text()=\": \"]"));
      assertTrue(selenium.isElementPresent("//body[@class='editbox']/span[73][@class='css-unit' and text()=\"3px\"]"));
      assertTrue(selenium
         .isElementPresent("//body[@class='editbox']/span[74][@class='css-punctuation' and text()=\";\"]"));
      assertTrue(selenium
         .isElementPresent("//body[@class='editbox']/span[76][@class='css-punctuation' and text()=\"}\"]"));
      assertTrue(selenium
         .isElementPresent("//body[@class='editbox']/span[78][@class='css-selector' and text()=\"#foo \"]"));
      assertTrue(selenium
         .isElementPresent("//body[@class='editbox']/span[79][@class='css-selector' and text()=\"code \"]"));
      assertTrue(selenium
         .isElementPresent("//body[@class='editbox']/span[80][@class='css-punctuation' and text()=\"{\"]"));
      assertTrue(selenium
         .isElementPresent("//body[@class='editbox']/span[82][@class='css-identifier' and text()=\"font-family\"]"));
      assertTrue(selenium
         .isElementPresent("//body[@class='editbox']/span[83][@class='css-punctuation' and text()=\": \"]"));
      assertTrue(selenium
         .isElementPresent("//body[@class='editbox']/span[84][@class='css-value' and text()=\"courier\"]"));
      assertTrue(selenium
         .isElementPresent("//body[@class='editbox']/span[85][@class='css-select-op' and text()=\", \"]"));
      assertTrue(selenium
         .isElementPresent("//body[@class='editbox']/span[86][@class='css-value' and text()=\"monospace\"]"));
      assertTrue(selenium
         .isElementPresent("//body[@class='editbox']/span[87][@class='css-punctuation' and text()=\";\"]"));
      assertTrue(selenium
         .isElementPresent("//body[@class='editbox']/span[89][@class='css-identifier' and text()=\"font-size\"]"));
      assertTrue(selenium
         .isElementPresent("//body[@class='editbox']/span[90][@class='css-punctuation' and text()=\": \"]"));
      assertTrue(selenium.isElementPresent("//body[@class='editbox']/span[91][@class='css-unit' and text()=\"80%\"]"));
      assertTrue(selenium
         .isElementPresent("//body[@class='editbox']/span[94][@class='css-identifier' and text()=\"color\"]"));
      assertTrue(selenium
         .isElementPresent("//body[@class='editbox']/span[96][@class='css-colorcode' and text()=\"#448888\"]"));
      assertTrue(selenium
         .isElementPresent("//body[@class='editbox']/span[97][@class='css-punctuation' and text()=\";\"]"));
      assertTrue(selenium
         .isElementPresent("//body[@class='editbox']/span[99][@class='css-punctuation' and text()=\"}\"]"));
      assertTrue(selenium
         .isElementPresent("//body[@class='editbox']/span[102][@class='xml-tagname' and text()=\"style\"]"));
      assertTrue(selenium
         .isElementPresent("//body[@class='editbox']/span[103][@class='xml-punctuation' and text()=\">\"]"));
      assertTrue(selenium
         .isElementPresent("//body[@class='editbox']/span[104][@class='xml-punctuation' and text()=\"</\"]"));
      assertTrue(selenium
         .isElementPresent("//body[@class='editbox']/span[105][@class='xml-tagname' and text()=\"head\"]"));
      assertTrue(selenium
         .isElementPresent("//body[@class='editbox']/span[106][@class='xml-punctuation' and text()=\">\"]"));
      assertTrue(selenium
         .isElementPresent("//body[@class='editbox']/span[107][@class='xml-punctuation' and text()=\"<\"]"));
      assertTrue(selenium
         .isElementPresent("//body[@class='editbox']/span[108][@class='xml-tagname' and text()=\"body\"]"));
      assertTrue(selenium
         .isElementPresent("//body[@class='editbox']/span[109][@class='xml-punctuation' and text()=\">\"]"));
      assertTrue(selenium
         .isElementPresent("//body[@class='editbox']/span[111][@class='xml-punctuation' and text()=\"<\"]"));
      assertTrue(selenium.isElementPresent("//body[@class='editbox']/span[112][@class='xml-tagname' and text()=\"p\"]"));
      assertTrue(selenium
         .isElementPresent("//body[@class='editbox']/span[113][@class='xml-punctuation' and text()=\">\"]"));
      assertTrue(selenium
         .isElementPresent("//body[@class='editbox']/span[114][@class='xml-text' and text()=\"Hello\"]"));
      assertTrue(selenium
         .isElementPresent("//body[@class='editbox']/span[115][@class='xml-punctuation' and text()=\"</\"]"));
      assertTrue(selenium.isElementPresent("//body[@class='editbox']/span[116][@class='xml-tagname' and text()=\"p\"]"));
      assertTrue(selenium
         .isElementPresent("//body[@class='editbox']/span[117][@class='xml-punctuation' and text()=\">\"]"));
      assertTrue(selenium
         .isElementPresent("//body[@class='editbox']/span[118][@class='xml-punctuation' and text()=\"</\"]"));
      assertTrue(selenium
         .isElementPresent("//body[@class='editbox']/span[119][@class='xml-tagname' and text()=\"body\"]"));
      assertTrue(selenium
         .isElementPresent("//body[@class='editbox']/span[120][@class='xml-punctuation' and text()=\">\"]"));
      assertTrue(selenium
         .isElementPresent("//body[@class='editbox']/span[121][@class='xml-punctuation' and text()=\"</\"]"));
      assertTrue(selenium
         .isElementPresent("//body[@class='editbox']/span[122][@class='xml-tagname' and text()=\"html\"]"));
      assertTrue(selenium
         .isElementPresent("//body[@class='editbox']/span[120][@class='xml-punctuation' and text()=\">\"]"));
      selenium.selectFrame("relative=top");
   }

   public void checkHilightGroovy()
   {
     // selenium.selectFrame("//div[@class='tabSetContainer']/div/div[4]//iframe");
      assertTrue(selenium
         .isElementPresent("//body[@class='editbox']/span[1][@class='groovyComment' and text()=\"//simple groovy script\"]"));
      assertTrue(selenium
         .isElementPresent("//body[@class='editbox']/span[2][@class='javaKeyword' and text()=\"import \"]"));
      assertTrue(selenium
         .isElementPresent("//body[@class='editbox']/span[3][@class='groovyVariable' and text()=\"javax\"]"));
      assertTrue(selenium
         .isElementPresent("//body[@class='editbox']/span[4][@class='groovyPunctuation' and text()=\".\"]"));
      assertTrue(selenium
         .isElementPresent("//body[@class='editbox']/span[5][@class='groovyVariable' and text()=\"ws\"]"));
      assertTrue(selenium
         .isElementPresent("//body[@class='editbox']/span[6][@class='groovyPunctuation' and text()=\".\"]"));
      assertTrue(selenium
         .isElementPresent("//body[@class='editbox']/span[7][@class='groovyVariable' and text()=\"rs\"]"));
      assertTrue(selenium
         .isElementPresent("//body[@class='editbox']/span[8][@class='groovyPunctuation' and text()=\".\"]"));
      assertTrue(selenium
         .isElementPresent("//body[@class='editbox']/span[9][@class='groovyVariable' and text()=\"Path\"]"));
      assertTrue(selenium
         .isElementPresent("//body[@class='editbox']/span[10][@class='javaKeyword' and text()=\"import \"]"));
      assertTrue(selenium
         .isElementPresent("//body[@class='editbox']/span[11][@class='groovyVariable' and text()=\"javax\"]"));
      assertTrue(selenium
         .isElementPresent("//body[@class='editbox']/span[12][@class='groovyPunctuation' and text()=\".\"]"));
      assertTrue(selenium
         .isElementPresent("//body[@class='editbox']/span[13][@class='groovyVariable' and text()=\"ws\"]"));
      assertTrue(selenium
         .isElementPresent("//body[@class='editbox']/span[14][@class='groovyPunctuation' and text()=\".\"]"));
      assertTrue(selenium
         .isElementPresent("//body[@class='editbox']/span[15][@class='groovyVariable' and text()=\"rs\"]"));
      assertTrue(selenium
         .isElementPresent("//body[@class='editbox']/span[16][@class='groovyPunctuation' and text()=\".\"]"));
      assertTrue(selenium
         .isElementPresent("//body[@class='editbox']/span[17][@class='groovyVariable' and text()=\"GET\"]"));
      assertTrue(selenium
         .isElementPresent("//body[@class='editbox']/span[18][@class='javaKeyword' and text()=\"import \"]"));
      assertTrue(selenium
         .isElementPresent("//body[@class='editbox']/span[19][@class='groovyVariable' and text()=\"javax\"]"));
      assertTrue(selenium
         .isElementPresent("//body[@class='editbox']/span[20][@class='groovyPunctuation' and text()=\".\"]"));
      assertTrue(selenium
         .isElementPresent("//body[@class='editbox']/span[21][@class='groovyVariable' and text()=\"ws\"]"));
      assertTrue(selenium
         .isElementPresent("//body[@class='editbox']/span[22][@class='groovyPunctuation' and text()=\".\"]"));
      assertTrue(selenium
         .isElementPresent("//body[@class='editbox']/span[23][@class='groovyVariable' and text()=\"rs\"]"));
      assertTrue(selenium
         .isElementPresent("//body[@class='editbox']/span[24][@class='groovyPunctuation' and text()=\".\"]"));
      assertTrue(selenium
         .isElementPresent("//body[@class='editbox']/span[25][@class='groovyVariable' and text()=\"PathParam\"]"));
      assertTrue(selenium
         .isElementPresent("//body[@class='editbox']/span[26][@class='javaAnnotation' and text()=\"@Path \"]"));
      assertTrue(selenium
         .isElementPresent("//body[@class='editbox']/span[27][@class='groovyPunctuation' and text()=\"(\"]"));
      assertTrue(selenium
         .isElementPresent("//body[@class='editbox']/span[32][@class='javaModifier' and text()=\"public \"]"));
      assertTrue(selenium
         .isElementPresent("//body[@class='editbox']/span[33][@class='javaType' and text()=\"class \"]"));
      assertTrue(selenium
         .isElementPresent("//body[@class='editbox']/span[34][@class='groovyVariable' and text()=\"HelloWorld\"]"));
      assertTrue(selenium
         .isElementPresent("//body[@class='editbox']/span[36][@class='javaAnnotation' and text()=\"@Get\"]"));
      assertTrue(selenium
         .isElementPresent("//body[@class='editbox']/span[37][@class='javaAnnotation' and text()=\"@Path \"]"));
      assertTrue(selenium.isElementPresent("//body[@class='editbox']/span[40][@class='groovyString' and text()=\"h\"]"));
      assertTrue(selenium.isElementPresent("//body[@class='editbox']/span[41][@class='groovyString' and text()=\"e\"]"));
      assertTrue(selenium.isElementPresent("//body[@class='editbox']/span[42][@class='groovyString' and text()=\"l\"]"));
      assertTrue(selenium.isElementPresent("//body[@class='editbox']/span[43][@class='groovyString' and text()=\"l\"]"));
      assertTrue(selenium.isElementPresent("//body[@class='editbox']/span[44][@class='groovyString' and text()=\"o\"]"));
      assertTrue(selenium.isElementPresent("//body[@class='editbox']/span[45][@class='groovyString' and text()=\"w\"]"));
      assertTrue(selenium.isElementPresent("//body[@class='editbox']/span[46][@class='groovyString' and text()=\"o\"]"));
      assertTrue(selenium.isElementPresent("//body[@class='editbox']/span[47][@class='groovyString' and text()=\"r\"]"));
      assertTrue(selenium.isElementPresent("//body[@class='editbox']/span[48][@class='groovyString' and text()=\"l\"]"));
      assertTrue(selenium.isElementPresent("//body[@class='editbox']/span[49][@class='groovyString' and text()=\"d\"]"));
      assertTrue(selenium.isElementPresent("//body[@class='editbox']/span[50][@class='groovyString' and text()=\"/\"]"));
      assertTrue(selenium.isElementPresent("//body[@class='editbox']/span[51][@class='groovyString' and text()=\"{\"]"));
      assertTrue(selenium.isElementPresent("//body[@class='editbox']/span[52][@class='groovyString' and text()=\"n\"]"));
      assertTrue(selenium.isElementPresent("//body[@class='editbox']/span[53][@class='groovyString' and text()=\"a\"]"));
      assertTrue(selenium.isElementPresent("//body[@class='editbox']/span[54][@class='groovyString' and text()=\"m\"]"));
      assertTrue(selenium.isElementPresent("//body[@class='editbox']/span[55][@class='groovyString' and text()=\"e\"]"));
      assertTrue(selenium.isElementPresent("//body[@class='editbox']/span[56][@class='groovyString' and text()=\"}\"]"));
      assertTrue(selenium
         .isElementPresent("//body[@class='editbox']/span[58][@class='groovyPunctuation' and text()=\")\"]"));
      assertTrue(selenium
         .isElementPresent("//body[@class='editbox']/span[59][@class='javaModifier' and text()=\"public \"]"));
      assertTrue(selenium
         .isElementPresent("//body[@class='editbox']/span[60][@class='groovyVariable' and text()=\"String \"]"));
      assertTrue(selenium
         .isElementPresent("//body[@class='editbox']/span[61][@class='groovyVariable' and text()=\"hello\"]"));
      assertTrue(selenium
         .isElementPresent("//body[@class='editbox']/span[62][@class='groovyPunctuation' and text()=\"(\"]"));
      assertTrue(selenium
         .isElementPresent("//body[@class='editbox']/span[63][@class='groovyVariable' and text()=\"PathParam\"]"));
      assertTrue(selenium
         .isElementPresent("//body[@class='editbox']/span[64][@class='groovyPunctuation' and text()=\"(\"]"));
      assertTrue(selenium.isElementPresent("//body[@class='editbox']/span[66][@class='groovyString' and text()=\"n\"]"));
      assertTrue(selenium.isElementPresent("//body[@class='editbox']/span[67][@class='groovyString' and text()=\"a\"]"));
      assertTrue(selenium.isElementPresent("//body[@class='editbox']/span[68][@class='groovyString' and text()=\"m\"]"));
      assertTrue(selenium.isElementPresent("//body[@class='editbox']/span[69][@class='groovyString' and text()=\"e\"]"));
      assertTrue(selenium
         .isElementPresent("//body[@class='editbox']/span[71][@class='groovyPunctuation' and text()=\")\"]"));
      assertTrue(selenium
         .isElementPresent("//body[@class='editbox']/span[72][@class='groovyVariable' and text()=\"String \"]"));
      assertTrue(selenium
         .isElementPresent("//body[@class='editbox']/span[73][@class='groovyVariable' and text()=\"name\"]"));
      assertTrue(selenium
         .isElementPresent("//body[@class='editbox']/span[74][@class='groovyPunctuation' and text()=\")\"]"));
      assertTrue(selenium
         .isElementPresent("//body[@class='editbox']/span[75][@class='groovyPunctuation' and text()=\"{\"]"));
      assertTrue(selenium
         .isElementPresent("//body[@class='editbox']/span[77][@class='javaKeyword' and text()=\"return \"]"));
      assertTrue(selenium.isElementPresent("//body[@class='editbox']/span[79][@class='groovyString' and text()=\"H\"]"));
      assertTrue(selenium.isElementPresent("//body[@class='editbox']/span[80][@class='groovyString' and text()=\"e\"]"));
      assertTrue(selenium.isElementPresent("//body[@class='editbox']/span[81][@class='groovyString' and text()=\"l\"]"));
      assertTrue(selenium.isElementPresent("//body[@class='editbox']/span[82][@class='groovyString' and text()=\"l\"]"));
      assertTrue(selenium.isElementPresent("//body[@class='editbox']/span[83][@class='groovyString' and text()=\"o\"]"));
      assertTrue(selenium
         .isElementPresent("//body[@class='editbox']/span[85][@class='groovyOperator' and text()=\"+\"]"));
      assertTrue(selenium
         .isElementPresent("//body[@class='editbox']/span[86][@class='groovyVariable' and text()=\"name\"]"));
      assertTrue(selenium
         .isElementPresent("//body[@class='editbox']/span[88][@class='groovyPunctuation' and text()=\"}\"]"));
      assertTrue(selenium
         .isElementPresent("//body[@class='editbox']/span[90][@class='groovyPunctuation' and text()=\"}\"]"));
      selenium.selectFrame("relative=top");
   }

   public void checkHiligtGoofleGadget()
   {
      //selenium.selectFrame("//div[@class='tabSetContainer']/div/div[3]//iframe");
      // string 1
    
      try
      {
         selectIFrameWithEditor(0);
      }
      catch (Exception e)
      {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }
      
      
      assertTrue(selenium
         .isElementPresent("//body[@class='editbox']/span[1][@class='xml-processing' and text()=\"<?xml \"]"));
      assertTrue(selenium
         .isElementPresent("//body[@class='editbox']/span[3][@class='xml-punctuation' and text()=\"<\"]"));
      assertTrue(selenium
         .isElementPresent("//body[@class='editbox']/span[4][@class='xml-tagname' and text()=\"Module\"]"));
      assertTrue(selenium
         .isElementPresent("//body[@class='editbox']/span[5][@class='xml-punctuation' and text()=\">\"]"));
      assertTrue(selenium
         .isElementPresent("//body[@class='editbox']/span[7][@class='xml-punctuation' and text()=\"<\"]"));
      assertTrue(selenium
         .isElementPresent("//body[@class='editbox']/span[8][@class='xml-tagname' and text()=\"ModulePrefs \"]"));
      assertTrue(selenium
         .isElementPresent("//body[@class='editbox']/span[9][@class='xml-attname' and text()=\"title\"]"));
      assertTrue(selenium
         .isElementPresent("//body[@class='editbox']/span[10][@class='xml-punctuation' and text()=\"=\"]"));
      assertTrue(selenium
         .isElementPresent("//body[@class='editbox']/span[11][@class='xml-attribute' and text()='\"Hello World!\" ']"));
      assertTrue(selenium
         .isElementPresent("//body[@class='editbox']/span[14][@class='xml-punctuation' and text()=\"<\"]"));
      assertTrue(selenium
         .isElementPresent("//body[@class='editbox']/span[15][@class='xml-tagname' and text()=\"Content \"]"));
      assertTrue(selenium
         .isElementPresent("//body[@class='editbox']/span[16][@class='xml-attname' and text()=\"type\"]"));
      assertTrue(selenium
         .isElementPresent("//body[@class='editbox']/span[17][@class='xml-punctuation' and text()=\"=\"]"));
      assertTrue(selenium
         .isElementPresent("//body[@class='editbox']/span[18][@class='xml-attribute' and text()='\"html\"']"));
      assertTrue(selenium
         .isElementPresent("//body[@class='editbox']/span[21][@class='xml-text' and text()=\"<![CDATA[ \"]"));
      assertTrue(selenium
         .isElementPresent("//body[@class='editbox']/span[23][@class='xml-punctuation' and text()=\"<\"]"));
      assertTrue(selenium
         .isElementPresent("//body[@class='editbox']/span[24][@class='xml-tagname' and text()=\"script \"]"));
      assertTrue(selenium
         .isElementPresent("//body[@class='editbox']/span[25][@class='xml-attname' and text()=\"type\"]"));
      assertTrue(selenium
         .isElementPresent("//body[@class='editbox']/span[26][@class='xml-punctuation' and text()=\"=\"]"));
      assertTrue(selenium
         .isElementPresent("//body[@class='editbox']/span[27][@class='xml-attribute' and text()=\"'text/javascript'\"]"));
      assertTrue(selenium
         .isElementPresent("//body[@class='editbox']/span[30][@class='js-keyword' and text()=\"function \"]"));
      assertTrue(selenium
         .isElementPresent("//body[@class='editbox']/span[31][@class='js-variable' and text()=\"foo\"]"));
      assertTrue(selenium
         .isElementPresent("//body[@class='editbox']/span[32][@class='js-punctuation' and text()=\"(\"]"));
      assertTrue(selenium
         .isElementPresent("//body[@class='editbox']/span[33][@class='js-variabledef' and text()=\"bar\"]"));
      assertTrue(selenium
         .isElementPresent("//body[@class='editbox']/span[34][@class='js-punctuation' and text()=\", \"]"));
      assertTrue(selenium
         .isElementPresent("//body[@class='editbox']/span[35][@class='js-variabledef' and text()=\"baz\"]"));
      assertTrue(selenium
         .isElementPresent("//body[@class='editbox']/span[36][@class='js-punctuation' and text()=\") \"]"));
      assertTrue(selenium
         .isElementPresent("//body[@class='editbox']/span[37][@class='js-punctuation' and text()=\"{\"]"));
      assertTrue(selenium
         .isElementPresent("//body[@class='editbox']/span[39][@class='js-variable' and text()=\"alert\"]"));
      assertTrue(selenium
         .isElementPresent("//body[@class='editbox']/span[40][@class='js-punctuation' and text()=\"(\"]"));
      assertTrue(selenium
         .isElementPresent("//body[@class='editbox']/span[41][@class='js-string' and text()=\"'quux'\"]"));
      assertTrue(selenium
         .isElementPresent("//body[@class='editbox']/span[42][@class='js-punctuation' and text()=\")\"]"));
      assertTrue(selenium
         .isElementPresent("//body[@class='editbox']/span[43][@class='js-punctuation' and text()=\";\"]"));
      assertTrue(selenium
         .isElementPresent("//body[@class='editbox']/span[45][@class='js-keyword' and text()=\"return \"]"));
      assertTrue(selenium
         .isElementPresent("//body[@class='editbox']/span[46][@class='js-localvariable' and text()=\"bar \"]"));
      assertTrue(selenium.isElementPresent("//body[@class='editbox']/span[47][@class='js-operator' and text()=\"+ \"]"));
      assertTrue(selenium
         .isElementPresent("//body[@class='editbox']/span[48][@class='js-localvariable' and text()=\"baz \"]"));
      assertTrue(selenium.isElementPresent("//body[@class='editbox']/span[49][@class='js-operator' and text()=\"+ \"]"));
      assertTrue(selenium.isElementPresent("//body[@class='editbox']/span[50][@class='js-atom' and text()=\"1\"]"));
      assertTrue(selenium
         .isElementPresent("//body[@class='editbox']/span[51][@class='js-punctuation' and text()=\";\"]"));
      assertTrue(selenium
         .isElementPresent("//body[@class='editbox']/span[53][@class='js-punctuation' and text()=\"}\"]"));
      assertTrue(selenium
         .isElementPresent("//body[@class='editbox']/span[55][@class='xml-punctuation' and text()=\"</\"]"));
      assertTrue(selenium
         .isElementPresent("//body[@class='editbox']/span[56][@class='xml-tagname' and text()=\"script\"]"));
      assertTrue(selenium
         .isElementPresent("//body[@class='editbox']/span[57][@class='xml-punctuation' and text()=\">\"]"));
      assertTrue(selenium
         .isElementPresent("//body[@class='editbox']/span[59][@class='xml-punctuation' and text()=\"<\"]"));
      assertTrue(selenium
         .isElementPresent("//body[@class='editbox']/span[60][@class='xml-tagname' and text()=\"style \"]"));
      assertTrue(selenium
         .isElementPresent("//body[@class='editbox']/span[61][@class='xml-attname' and text()=\"type\"]"));
      assertTrue(selenium
         .isElementPresent("//body[@class='editbox']/span[62][@class='xml-punctuation' and text()=\"=\"]"));
      assertTrue(selenium
         .isElementPresent("//body[@class='editbox']/span[63][@class='xml-attribute' and text()=\"'text/css'\"]"));
      assertTrue(selenium
         .isElementPresent("//body[@class='editbox']/span[66][@class='css-selector' and text()=\"div\"]"));
      assertTrue(selenium
         .isElementPresent("//body[@class='editbox']/span[67][@class='css-select-op' and text()=\".\"]"));
      assertTrue(selenium
         .isElementPresent("//body[@class='editbox']/span[68][@class='css-selector' and text()=\"border \"]"));
      assertTrue(selenium
         .isElementPresent("//body[@class='editbox']/span[69][@class='css-punctuation' and text()=\"{\"]"));
      assertTrue(selenium
         .isElementPresent("//body[@class='editbox']/span[71][@class='css-identifier' and text()=\"border\"]"));
      assertTrue(selenium
         .isElementPresent("//body[@class='editbox']/span[72][@class='css-punctuation' and text()=\": \"]"));
      assertTrue(selenium.isElementPresent("//body[@class='editbox']/span[73][@class='css-unit' and text()=\"1px \"]"));
      assertTrue(selenium
         .isElementPresent("//body[@class='editbox']/span[74][@class='css-value' and text()=\"solid \"]"));
      assertTrue(selenium
         .isElementPresent("//body[@class='editbox']/span[75][@class='css-value' and text()=\"black\"]"));
      assertTrue(selenium
         .isElementPresent("//body[@class='editbox']/span[76][@class='css-punctuation' and text()=\";\"]"));
      assertTrue(selenium
         .isElementPresent("//body[@class='editbox']/span[78][@class='css-identifier' and text()=\"padding\"]"));
      assertTrue(selenium
         .isElementPresent("//body[@class='editbox']/span[79][@class='css-punctuation' and text()=\": \"]"));
      assertTrue(selenium.isElementPresent("//body[@class='editbox']/span[80][@class='css-unit' and text()=\"3px\"]"));
      assertTrue(selenium
         .isElementPresent("//body[@class='editbox']/span[81][@class='css-punctuation' and text()=\";\"]"));
      assertTrue(selenium
         .isElementPresent("//body[@class='editbox']/span[83][@class='css-punctuation' and text()=\"}\"]"));
      assertTrue(selenium
         .isElementPresent("//body[@class='editbox']/span[85][@class='css-selector' and text()=\"#foo \"]"));
      assertTrue(selenium
         .isElementPresent("//body[@class='editbox']/span[86][@class='css-selector' and text()=\"code \"]"));
      assertTrue(selenium
         .isElementPresent("//body[@class='editbox']/span[87][@class='css-punctuation' and text()=\"{\"]"));
      assertTrue(selenium
         .isElementPresent("//body[@class='editbox']/span[89][@class='css-identifier' and text()=\"font-family\"]"));
      assertTrue(selenium
         .isElementPresent("//body[@class='editbox']/span[90][@class='css-punctuation' and text()=\": \"]"));
      assertTrue(selenium
         .isElementPresent("//body[@class='editbox']/span[91][@class='css-value' and text()=\"courier\"]"));
      assertTrue(selenium
         .isElementPresent("//body[@class='editbox']/span[92][@class='css-select-op' and text()=\", \"]"));
      assertTrue(selenium
         .isElementPresent("//body[@class='editbox']/span[93][@class='css-value' and text()=\"monospace\"]"));
      assertTrue(selenium
         .isElementPresent("//body[@class='editbox']/span[94][@class='css-punctuation' and text()=\";\"]"));
      assertTrue(selenium
         .isElementPresent("//body[@class='editbox']/span[96][@class='css-identifier' and text()=\"font-size\"]"));
      assertTrue(selenium
         .isElementPresent("//body[@class='editbox']/span[97][@class='css-punctuation' and text()=\": \"]"));
      assertTrue(selenium.isElementPresent("//body[@class='editbox']/span[98][@class='css-unit' and text()=\"80%\"]"));
      assertTrue(selenium
         .isElementPresent("//body[@class='editbox']/span[101][@class='css-identifier' and text()=\"color\"]"));
      assertTrue(selenium
         .isElementPresent("//body[@class='editbox']/span[103][@class='css-colorcode' and text()=\"#448888\"]"));
      assertTrue(selenium
         .isElementPresent("//body[@class='editbox']/span[104][@class='css-punctuation' and text()=\";\"]"));
      assertTrue(selenium
         .isElementPresent("//body[@class='editbox']/span[106][@class='css-punctuation' and text()=\"}\"]"));
      assertTrue(selenium
         .isElementPresent("//body[@class='editbox']/span[109][@class='xml-tagname' and text()=\"style\"]"));
      assertTrue(selenium
         .isElementPresent("//body[@class='editbox']/span[112][@class='xml-punctuation' and text()=\"<\"]"));
      assertTrue(selenium.isElementPresent("//body[@class='editbox']/span[113][@class='xml-tagname' and text()=\"p\"]"));
      assertTrue(selenium
         .isElementPresent("//body[@class='editbox']/span[114][@class='xml-punctuation' and text()=\">\"]"));
      assertTrue(selenium
         .isElementPresent("//body[@class='editbox']/span[115][@class='xml-text' and text()=\"Hello\"]"));
      assertTrue(selenium
         .isElementPresent("//body[@class='editbox']/span[116][@class='xml-punctuation' and text()=\"</\"]"));
      assertTrue(selenium.isElementPresent("//body[@class='editbox']/span[117][@class='xml-tagname' and text()=\"p\"]"));
      assertTrue(selenium
         .isElementPresent("//body[@class='editbox']/span[118][@class='xml-punctuation' and text()=\">\"]"));
      assertTrue(selenium.isElementPresent("//body[@class='editbox']/span[120][@class='xml-text' and text()=\"]]>\"]"));
      assertTrue(selenium
         .isElementPresent("//body[@class='editbox']/span[121][@class='xml-punctuation' and text()=\"</\"]"));
      assertTrue(selenium
         .isElementPresent("//body[@class='editbox']/span[122][@class='xml-tagname' and text()=\"Content\"]"));
      assertTrue(selenium
         .isElementPresent("//body[@class='editbox']/span[123][@class='xml-punctuation' and text()=\">\"]"));
      assertTrue(selenium
         .isElementPresent("//body[@class='editbox']/span[124][@class='xml-punctuation' and text()=\"</\"]"));
      assertTrue(selenium
         .isElementPresent("//body[@class='editbox']/span[125][@class='xml-tagname' and text()=\"Module\"]"));
      assertTrue(selenium
         .isElementPresent("//body[@class='editbox']/span[126][@class='xml-punctuation' and text()=\">\"]"));
      selenium.selectFrame("relative=top");
   }

   public void chekHilightingInCssFile()
   {
      try
      {
         selectIFrameWithEditor(0);
      }
      catch (Exception e)
      {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }
      // hilight in string 1
      assertTrue(selenium
         .isElementPresent("//body[@class='editbox']/span[1][@class='css-comment' and text()=\"/*Some example CSS*/\"]"));
      // hilight in string 3
      assertTrue(selenium.isElementPresent("//body[@class='editbox']/span[2][@class='css-at' and text()=\"@import \"]"));
      assertTrue(selenium
         .isElementPresent("//body[@class='editbox']/span[3][@class='css-identifier' and text()=\"url \"]"));
      // hilight in string 4
      assertTrue(selenium
         .isElementPresent("//body[@class='editbox']/span[7][@class='css-identifier' and text()=\"body \"]"));
      assertTrue(selenium
         .isElementPresent("//body[@class='editbox']/span[8][@class='css-punctuation' and text()=\"{\"]"));
      // hilight in string 5
      assertTrue(selenium
         .isElementPresent("//body[@class='editbox']/span[10][@class='css-identifier' and text()=\"margin \"]"));
      assertTrue(selenium.isElementPresent("//body[@class='editbox']/span[11][@class='css-unit' and text()=\"0\"]"));
      assertTrue(selenium
         .isElementPresent("//body[@class='editbox']/span[12][@class='css-punctuation' and text()=\";\"]"));
      // hilight in string 6
      assertTrue(selenium
         .isElementPresent("//body[@class='editbox']/span[14][@class='css-identifier' and text()=\"padding \"]"));
      assertTrue(selenium.isElementPresent("//body[@class='editbox']/span[15][@class='css-unit' and text()=\"3em \"]"));
      assertTrue(selenium.isElementPresent("//body[@class='editbox']/span[16][@class='css-unit' and text()=\"6em\"]"));
      assertTrue(selenium
         .isElementPresent("//body[@class='editbox']/span[17][@class='css-punctuation' and text()=\";\"]"));
      // hilight in string 7
      assertTrue(selenium
         .isElementPresent("//body[@class='editbox']/span[19][@class='css-identifier' and text()=\"font-family\"]"));
      assertTrue(selenium
         .isElementPresent("//body[@class='editbox']/span[20][@class='css-punctuation' and text()=\": \"]"));
      assertTrue(selenium
         .isElementPresent("//body[@class='editbox']/span[21][@class='css-value' and text()=\"tahoma\"]"));
      assertTrue(selenium
         .isElementPresent("//body[@class='editbox']/span[22][@class='css-select-op' and text()=\", \"]"));
      assertTrue(selenium
         .isElementPresent("//body[@class='editbox']/span[23][@class='css-value' and text()=\"arial\"]"));
      assertTrue(selenium
         .isElementPresent("//body[@class='editbox']/span[24][@class='css-select-op' and text()=\", \"]"));
      assertTrue(selenium
         .isElementPresent("//body[@class='editbox']/span[25][@class='css-value' and text()=\"sans-serif\"]"));
      assertTrue(selenium
         .isElementPresent("//body[@class='editbox']/span[26][@class='css-punctuation' and text()=\";\" ]"));
      // string 8
      assertTrue(selenium
         .isElementPresent("//body[@class='editbox']/span[28][@class='css-identifier' and text()=\"color \"]"));
      assertTrue(selenium
         .isElementPresent("//body[@class='editbox']/span[29][@class='css-colorcode' and text()=\"#000\"]"));
      assertTrue(selenium
         .isElementPresent("//body[@class='editbox']/span[30][@class='css-punctuation' and text()=\";\" ]"));
      // string 9
      assertTrue(selenium
         .isElementPresent("//body[@class='editbox']/span[31][@class='css-punctuation' and text()=\"}\" ]"));
      // string 10
      assertTrue(selenium
         .isElementPresent("//body[@class='editbox']/span[33][@class='css-selector' and text()=\"#navigation \"]"));
      assertTrue(selenium
         .isElementPresent("//body[@class='editbox']/span[34][@class='css-selector' and text()=\"a \"]"));
      assertTrue(selenium
         .isElementPresent("//body[@class='editbox']/span[35][@class='css-punctuation' and text()=\"{\" ]"));
      assertTrue(selenium
         .isElementPresent("//body[@class='editbox']/span[37][@class='css-identifier' and text()=\"font-weigt\"]"));
      assertTrue(selenium
         .isElementPresent("//body[@class='editbox']/span[38][@class='css-punctuation' and text()=\": \"]"));
      assertTrue(selenium.isElementPresent("//body[@class='editbox']/span[39][@class='css-value' and text()=\"bold\"]"));
      assertTrue(selenium
         .isElementPresent("//body[@class='editbox']/span[42][@class='css-identifier' and text()=\"text-decoration\"]"));
      assertTrue(selenium
         .isElementPresent("//body[@class='editbox']/span[43][@class='css-punctuation' and text()=\": \"]"));
      assertTrue(selenium
         .isElementPresent("//body[@class='editbox']/span[44][@class='css-value' and text()=\"none \"]"));
      assertTrue(selenium
         .isElementPresent("//body[@class='editbox']/span[45][@class='css-important' and text()=\"!important\"]"));
      selenium.selectFrame("relative=top");
   }

   public void checkIcons() throws InterruptedException, Exception
   {
      Thread.sleep(TestConstants.EDITOR_OPEN_PERIOD);
      selectItemInWorkspaceTree(WS_NAME);
      Thread.sleep(TestConstants.EDITOR_OPEN_PERIOD);
      assertTrue(selenium.isElementPresent("//table[@class='listTable']/tbody/tr[3]//img[contains(@src, 'css.png')]"));
      assertTrue(selenium.isTextPresent(CSS_FILE_NAME));
      assertTrue(selenium
         .isElementPresent("//table[@class='listTable']/tbody/tr[4]//img[contains(@src, 'gadget.png')]"));
      assertTrue(selenium.isTextPresent(GADGET_FILE_NAME));
      assertTrue(selenium
         .isElementPresent("//table[@class='listTable']/tbody/tr[5]//img[contains(@src, 'rest.png')]"));
      assertTrue(selenium.isTextPresent(GROOVY_FILE_NAME));
      assertTrue(selenium.isElementPresent("//table[@class='listTable']/tbody/tr[6]//img[contains(@src, 'html.png')]"));
      assertTrue(selenium.isTextPresent(HTML_FILE_NAME));
      assertTrue(selenium
         .isElementPresent("//table[@class='listTable']/tbody/tr[7]//img[contains(@src, 'javascript.gif')]"));
      assertTrue(selenium.isTextPresent(JS_FILE_NAME));
      assertTrue(selenium.isElementPresent("//table[@class='listTable']/tbody/tr[8]//img[contains(@src, 'txt.png')]"));
      assertTrue(selenium.isTextPresent(TXT_FILE_NAME));
      assertTrue(selenium.isElementPresent("//table[@class='listTable']/tbody/tr[9]//img[contains(@src, 'xml.png')]"));
      assertTrue(selenium.isTextPresent(XML_FILE_NAME));
   }

}
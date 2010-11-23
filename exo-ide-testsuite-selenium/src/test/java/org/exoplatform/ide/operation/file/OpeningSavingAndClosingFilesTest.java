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
import org.exoplatform.ide.CloseFileUtils;
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
 * 
 *
 */
public class OpeningSavingAndClosingFilesTest extends BaseTest
{

   private static String FOLDER_NAME = OpeningSavingAndClosingFilesTest.class.getSimpleName();

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

      // Refresh Workspace:
      Thread.sleep(TestConstants.SLEEP);
      selectItemInWorkspaceTree(WS_NAME);
      runToolbarButton(ToolbarCommands.File.REFRESH);
      selectItemInWorkspaceTree(FOLDER_NAME);
      runToolbarButton(ToolbarCommands.File.REFRESH);

      // ----------5--------------
      reopenFiles();
     // clickTabAndCheckSaveButton();

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
      Thread.sleep(TestConstants.SLEEP);
      String CSS =
         "Change file\n/*Some example CSS*/\n\n@import url (\"something.css\")\nbody {\n  margin 0;\n  padding 3em 6em;\n  font-family: tahoma, arial, sans-serif;\n  color #000;\n}\n#navigation a {\n    font-weigt: bold;\n  text-decoration: none !important;\n}\n}";
      assertEquals(CSS, selenium.getText("//body[@class='editbox']"));
      selenium.click("scLocator=//TabSet[ID=\"ideEditorFormTabSet\"]/tab[index=0]/icon");
      Thread.sleep(TestConstants.SLEEP);
      // check changed string in Google Gadget file
      String GG =
         "Change file\nChange file\n<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n<Module>\n  <ModulePrefs title=\"Hello World!\" />\n  <Content type=\"html\">\n    <![CDATA[ \n    <script type='text/javascript'>\n      function foo(bar, baz) {\n        alert('quux');\n        return bar + baz + 1;\n      }\n    </script>\n    <style type='text/css'>\n      div.border {\n        border: 1px solid black;\n        padding: 3px;\n      }\n      #foo code {\n        font-family: courier, monospace;\n        font-size: 80%;\n        color: #448888;\n      }\n    </style>\n    <p>Hello</p>\n    ]]></Content></Module>";
      assertEquals(GG, selenium.getText("//body[@class='editbox']"));
      selenium.click("scLocator=//TabSet[ID=\"ideEditorFormTabSet\"]/tab[index=0]/icon");
      Thread.sleep(TestConstants.SLEEP);
      // check changed string in Groovy file
      String Groovy =
         "//simple groovy script\n\nimport javax.ws.rs.Path\nimport javax.ws.rs.GET\nimport javax.ws.rs.PathParam\n\n@Path (\"/\")\npublic class HelloWorld{\n@Get\n@Path (\"helloworld/{name}\")\npublic String hello(PathParam(\"name\")String name){\n  return \"Hello\"+name\n  }\n  }";
      assertEquals(Groovy, selenium.getText("//body[@class='editbox']"));
      selenium.click("scLocator=//TabSet[ID=\"ideEditorFormTabSet\"]/tab[index=0]/icon");
      Thread.sleep(TestConstants.SLEEP);
      // check changed string in HTML file
      String HTML =
         "Change file\n<html>\n<head>\n  <title>HTML Example</title>\n  <script type='text/javascript'>\n    function foo(bar, baz) {\n      alert('quux');\n      return bar + baz + 1;\n    }\n  </script>\n  <style type='text/css'>\n    div.border {\n      border: 1px solid black;\n      padding: 3px;\n    }\n    #foo code {\n      font-family: courier, monospace;\n      font-size: 80%;\n      color: #448888;\n    }\n  </style>\n</head>\n<body>\n  <p>Hello</p>\n</body>\n</html>";
      assertEquals(HTML, selenium.getText("//body[@class='editbox']"));
      selenium.click("scLocator=//TabSet[ID=\"ideEditorFormTabSet\"]/tab[index=0]/icon");
      Thread.sleep(TestConstants.SLEEP);
      // check changed string in JS file
      String JS =
         "Change file\n  //Here you see some JavaScript code. Mess around with it to get\n//acquinted with CodeMirror's features.\n\n// Press enter inside the objects and your new line will \n// intended.\n\nvar keyBindings ={\n  enter:\"newline-and-indent\",\n  tab:\"reindent-selection\",\n  ctrl_z \"undo\",\n  ctrl_y:\"redo\"\n  };\n  var regex =/foo|bar/i;\n  function example (x){\n  var y=44.4;\n  return x+y;\n  }";
      assertEquals(JS, selenium.getText("//body[@class='editbox']"));
      selenium.click("scLocator=//TabSet[ID=\"ideEditorFormTabSet\"]/tab[index=0]/icon");
      Thread.sleep(TestConstants.SLEEP);
      // check changed string in TXT file
      String TXT = "text content";
      assertEquals(TXT, selenium.getText("//body[@class='editbox']"));
      selenium.click("scLocator=//TabSet[ID=\"ideEditorFormTabSet\"]/tab[index=0]/icon");
      Thread.sleep(TestConstants.SLEEP);
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
      Thread.sleep(TestConstants.SLEEP);
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
      Thread.sleep(TestConstants.SLEEP);
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
      Thread.sleep(TestConstants.SLEEP);
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
      Thread.sleep(TestConstants.SLEEP);
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
      Thread.sleep(TestConstants.SLEEP);
      saveCurrentFile();
      Thread.sleep(TestConstants.SLEEP);
      selenium.click("scLocator=//TabSet[ID=\"ideEditorFormTabSet\"]/tab[index=0]/icon");
      Thread.sleep(TestConstants.SLEEP);

      // Save and closeGoogleGadgetFile
      selenium.click("scLocator=//TabSet[ID=\"ideEditorFormTabSet\"]/tab[index=0]/");
      Thread.sleep(TestConstants.SLEEP);
      saveCurrentFile();
      Thread.sleep(TestConstants.SLEEP);
      selenium.click("scLocator=//TabSet[ID=\"ideEditorFormTabSet\"]/tab[index=0]/icon");
      Thread.sleep(TestConstants.SLEEP);

      // Save and close HTMLFile
      selenium.click("scLocator=//TabSet[ID=\"ideEditorFormTabSet\"]/tab[index=1]/");
      Thread.sleep(TestConstants.SLEEP);
      saveCurrentFile();
      Thread.sleep(TestConstants.SLEEP);
      selenium.click("scLocator=//TabSet[ID=\"ideEditorFormTabSet\"]/tab[index=1]/icon");
      Thread.sleep(TestConstants.SLEEP);

      // Save and closeJsFile
      selenium.click("scLocator=//TabSet[ID=\"ideEditorFormTabSet\"]/tab[index=1]/");
      Thread.sleep(TestConstants.SLEEP);
      saveCurrentFile();
      Thread.sleep(TestConstants.SLEEP);
      selenium.click("scLocator=//TabSet[ID=\"ideEditorFormTabSet\"]/tab[index=1]/icon");
      Thread.sleep(TestConstants.SLEEP);

      // Save and closeXMLFile
      selenium.click("scLocator=//TabSet[ID=\"ideEditorFormTabSet\"]/tab[index=2]/");
      Thread.sleep(TestConstants.SLEEP);
      saveCurrentFile();
      Thread.sleep(TestConstants.SLEEP);
      selenium.click("scLocator=//TabSet[ID=\"ideEditorFormTabSet\"]/tab[index=2]/icon");
      Thread.sleep(TestConstants.SLEEP);

      // close GroovyFile
      selenium.click("scLocator=//TabSet[ID=\"ideEditorFormTabSet\"]/tab[index=0]/");
      Thread.sleep(500);
      selenium.click("scLocator=//TabSet[ID=\"ideEditorFormTabSet\"]/tab[index=0]/icon");
      Thread.sleep(TestConstants.SLEEP);
      //**********TODO**********
      checkSaveDialog();
      
      // close TXTFile
      selenium.click("scLocator=//TabSet[ID=\"ideEditorFormTabSet\"]/tab[index=0]/");
      Thread.sleep(500);
      selenium.click("scLocator=//TabSet[ID=\"ideEditorFormTabSet\"]/tab[index=0]/icon");
      Thread.sleep(TestConstants.SLEEP);
      //*****TODO************
      checkSaveDialog();
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
      assertTrue(selenium.isTextPresent(GADGET_FILE_NAME + "*"));

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
      assertTrue(selenium.isTextPresent(JS_FILE_NAME + "*"));

      // changeXMLFile
      selenium.selectFrame("//div[@class='tabSetContainer']/div/div[8]//iframe");
      selenium.typeKeys("//body[@class='editbox']", "Change file");
      selenium.keyDown("//body[@class='editbox']", "13");
      selenium.keyUp("//body[@class='editbox']", "13");
      Thread.sleep(500);
      selenium.selectFrame("relative=top");
      Thread.sleep(3000);
      assertTrue(selenium.isTextPresent(XML_FILE_NAME + "*"));
      Thread.sleep(500);
   }

   public void clickTabAndCheckSaveButton() throws InterruptedException
   {
      selenium.click("scLocator=//TabSet[ID=\"ideEditorFormTabSet\"]/tab[index=0]/");
      Thread.sleep(TestConstants.SLEEP);
      assertTrue(selenium.isElementPresent("//div[@title='Save']/div[@elementenabled='false']"));
      // click_tab_and_check_SaveButton _2
      selenium.click("scLocator=//TabSet[ID=\"ideEditorFormTabSet\"]/tab[index=1]/");
      Thread.sleep(TestConstants.SLEEP);
      assertTrue(selenium.isElementPresent("//div[@title='Save']/div[@elementenabled='false']"));
      // click_tab_and_check_SaveButton _3
      selenium.click("scLocator=//TabSet[ID=\"ideEditorFormTabSet\"]/tab[index=2]/");
      Thread.sleep(TestConstants.SLEEP);
      assertTrue(selenium.isElementPresent("//div[@title='Save']/div[@elementenabled='false']"));
      // click_tab_and_check_SaveButton _4
      selenium.click("scLocator=//TabSet[ID=\"ideEditorFormTabSet\"]/tab[index=3]/");
      Thread.sleep(TestConstants.SLEEP);
      assertTrue(selenium.isElementPresent("//div[@title='Save']/div[@elementenabled='false']"));
      // click_tab_and_check_SaveButton _5
      selenium.click("scLocator=//TabSet[ID=\"ideEditorFormTabSet\"]/tab[index=4]/");
      Thread.sleep(TestConstants.SLEEP);
      assertTrue(selenium.isElementPresent("//div[@title='Save']/div[@elementenabled='false']"));
      // click_tab_and_check_SaveButton _6
      selenium.click("scLocator=//TabSet[ID=\"ideEditorFormTabSet\"]/tab[index=5]/");
      Thread.sleep(TestConstants.SLEEP);
      assertTrue(selenium.isElementPresent("//div[@title='Save']/div[@elementenabled='false']"));
      // click_tab_and_check_SaveButton _7
      selenium.click("scLocator=//TabSet[ID=\"ideEditorFormTabSet\"]/tab[index=6]/");
      Thread.sleep(TestConstants.SLEEP);
      assertTrue(selenium.isElementPresent("//div[@title='Save']/div[@elementenabled='false']"));
   }

   public void openXML() throws InterruptedException, Exception
   {
      Thread.sleep(TestConstants.EDITOR_OPEN_PERIOD);
      selectItemInWorkspaceTree(WS_NAME);
      //  runTopMenuCommand(MenuCommands.File.FILE, MenuCommands.File.REFRESH);
      Thread.sleep(TestConstants.EDITOR_OPEN_PERIOD);
      openFileFromNavigationTreeWithCodeEditor(XML_FILE_NAME, false);
      Thread.sleep(TestConstants.SLEEP);
   }

   public void openTXT() throws InterruptedException, Exception
   {
      Thread.sleep(TestConstants.EDITOR_OPEN_PERIOD);
      selectItemInWorkspaceTree(WS_NAME);
      // runTopMenuCommand(MenuCommands.File.FILE, MenuCommands.File.REFRESH);
      Thread.sleep(TestConstants.EDITOR_OPEN_PERIOD);
      openFileFromNavigationTreeWithCodeEditor(TXT_FILE_NAME, false);
      Thread.sleep(TestConstants.SLEEP);
   }

   public void openJavaScript() throws InterruptedException, Exception
   {
      Thread.sleep(TestConstants.EDITOR_OPEN_PERIOD);
      selectItemInWorkspaceTree(WS_NAME);
      //  runTopMenuCommand(MenuCommands.File.FILE, MenuCommands.File.REFRESH);
      Thread.sleep(TestConstants.EDITOR_OPEN_PERIOD);
      openFileFromNavigationTreeWithCodeEditor(JS_FILE_NAME, false);
      Thread.sleep(TestConstants.SLEEP);
   }

   public void openHtml() throws InterruptedException, Exception
   {
      Thread.sleep(TestConstants.EDITOR_OPEN_PERIOD);
      selectItemInWorkspaceTree(WS_NAME);
      //    runTopMenuCommand(MenuCommands.File.FILE, MenuCommands.File.REFRESH);
      Thread.sleep(TestConstants.EDITOR_OPEN_PERIOD);
      openFileFromNavigationTreeWithCodeEditor(HTML_FILE_NAME, false);
      Thread.sleep(3000);
   }

   public void openGroovy() throws InterruptedException, Exception
   {
      Thread.sleep(TestConstants.EDITOR_OPEN_PERIOD);
      selectItemInWorkspaceTree(WS_NAME);
      //runTopMenuCommand(MenuCommands.File.FILE, MenuCommands.File.REFRESH);
      openFileFromNavigationTreeWithCodeEditor(GROOVY_FILE_NAME, false);
      Thread.sleep(TestConstants.SLEEP);
   }

   public void openGooglegadget() throws InterruptedException, Exception
   {
      Thread.sleep(TestConstants.EDITOR_OPEN_PERIOD);
      selectItemInWorkspaceTree(WS_NAME);
      // runTopMenuCommand(MenuCommands.File.FILE, MenuCommands.File.REFRESH);
      Thread.sleep(TestConstants.EDITOR_OPEN_PERIOD);
      openFileFromNavigationTreeWithCodeEditor(GADGET_FILE_NAME, false);
      Thread.sleep(TestConstants.SLEEP);
   }

   public void openCss() throws InterruptedException, Exception
   {
      Thread.sleep(TestConstants.EDITOR_OPEN_PERIOD);
      openFileFromNavigationTreeWithCodeEditor(CSS_FILE_NAME, false);
   }

   //****************TODO fix  Task IDE-445
   public void checkSaveDialog() throws InterruptedException
   {
      if (selenium.isElementPresent("scLocator=//Dialog[ID=\"isc_globalWarn\"]/header/member/"))
      {
         selenium.click("scLocator=//Dialog[ID=\"isc_globalWarn\"]/noButton/");
         Thread.sleep(TestConstants.EDITOR_OPEN_PERIOD);
      }
      else
      {
         Thread.sleep(TestConstants.TYPE_DELAY_PERIOD);
      }
   }

}
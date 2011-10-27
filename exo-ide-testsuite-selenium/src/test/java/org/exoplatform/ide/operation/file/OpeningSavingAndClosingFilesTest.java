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

import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.TestConstants;
import org.exoplatform.ide.ToolbarCommands;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;

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
   }

   @Test
   public void testOpeningSavingAndClosingTabsWithFile() throws Exception
   {
      // Refresh Workspace:
      IDE.WORKSPACE.waitForItem(WS_URL + FOLDER_NAME + "/");

      IDE.WORKSPACE.doubleClickOnFolder(WS_URL + FOLDER_NAME + "/");

      //      IDE.WORKSPACE.selectItem(WS_URL);
      //      
      //      IDE.TOOLBAR.runCommand(ToolbarCommands.File.REFRESH);
      //      
      //      IDE.WORKSPACE.selectItem(WS_URL + FOLDER_NAME + "/");
      //      
      //      IDE.TOOLBAR.runCommand(ToolbarCommands.File.REFRESH);

      // ----------5--------------
      reopenFiles();
      // clickTabAndCheckSaveButton();

      //------------6-------------      
      changeFiles();
      saveAndCloseFile();

      //--------------8------------
      reopenFiles();
      clickTabAndCheckSaveButton();

      chekSaveInFiles();
   }

   public void chekSaveInFiles() throws Exception
   {
      // check changed string in CSS file
      IDE.EDITOR.selectTab(0);
      String CSS =
         "Change file\n/*Some example CSS*/\n\n@import url (\"something.css\")\nbody {\n  margin 0;\n  padding 3em 6em;\n  font-family: tahoma, arial, sans-serif;\n  color #000;\n}\n  #navigation a {\n    font-weigt: bold;\n  text-decoration: none !important;\n}\n}";

      assertEquals(CSS, IDE.EDITOR.getTextFromCodeEditor(0));

      // check changed string in Google Gadget file
      IDE.EDITOR.selectTab(1);
      String GG =
         "Change file\n<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n<Module>\n  <ModulePrefs title=\"Hello World!\" />\n  <Content type=\"html\">\n    <![CDATA[ \n    <script type='text/javascript'>\n      function foo(bar, baz) {\n        alert('quux');\n        return bar + baz + 1;\n      }\n    </script>\n    <style type='text/css'>\n      div.border {\n        border: 1px solid black;\n        padding: 3px;\n      }\n      #foo code {\n        font-family: courier, monospace;\n        font-size: 80%;\n        color: #448888;\n      }\n    </style>\n    <p>Hello</p>\n    ]]></Content></Module>";
      assertEquals(GG, IDE.EDITOR.getTextFromCodeEditor(1));
      //      assertEquals(GG, selenium().getText("//body[@class='editbox']"));
      //      selenium().click("scLocator=//TabSet[ID=\"ideEditorFormTabSet\"]/tab[index=0]/icon");
      //      Thread.sleep(TestConstants.SLEEP);

      //      // check changed string in Groovy file
      IDE.EDITOR.selectTab(2);
      String Groovy =
         "//simple groovy script\n\nimport javax.ws.rs.Path\nimport javax.ws.rs.GET\nimport javax.ws.rs.PathParam\n\n@Path (\"/\")\npublic class HelloWorld{\n@Get\n@Path (\"helloworld/{name}\")\npublic String hello(PathParam(\"name\")String name){\n  return \"Hello\"+name\n  }\n  }";
      assertEquals(Groovy, IDE.EDITOR.getTextFromCodeEditor(2));

      //      assertEquals(Groovy, selenium().getText("//body[@class='editbox']"));
      //      selenium().click("scLocator=//TabSet[ID=\"ideEditorFormTabSet\"]/tab[index=0]/icon");
      //      Thread.sleep(TestConstants.SLEEP);

      // check changed string in HTML file
      IDE.EDITOR.selectTab(3);

      String HTML =
         "Change file\n<html>\n<head>\n  <title>HTML Example</title>\n  <script type='text/javascript'>\n    function foo(bar, baz) {\n      alert('quux');\n      return bar + baz + 1;\n    }\n  </script>\n  <style type='text/css'>\n    div.border {\n      border: 1px solid black;\n      padding: 3px;\n    }\n    #foo code {\n      font-family: courier, monospace;\n      font-size: 80%;\n      color: #448888;\n    }\n  </style>\n</head>\n<body>\n  <p>Hello</p>\n</body>\n</html>";
      assertEquals(HTML, IDE.EDITOR.getTextFromCodeEditor(3));
      //      assertEquals(HTML, selenium().getText("//body[@class='editbox']"));
      //      selenium().click("scLocator=//TabSet[ID=\"ideEditorFormTabSet\"]/tab[index=0]/icon");
      //      Thread.sleep(TestConstants.SLEEP);

      // check changed string in JS file
      IDE.EDITOR.selectTab(4);

      String JS =
         "Change file\n  //Here you see some JavaScript code. Mess around with it to get\n//acquinted with CodeMirror's features.\n\n// Press enter inside the objects and your new line will \n// intended.\n\nvar keyBindings ={\n  enter:\"newline-and-indent\",\n  tab:\"reindent-selection\",\n  ctrl_z \"undo\",\n  ctrl_y:\"redo\"\n  };\n  var regex =/foo|bar/i;\n  function example (x){\n  var y=44.4;\n  return x+y;\n  }";
      assertEquals(JS, IDE.EDITOR.getTextFromCodeEditor(4));
      //      assertEquals(JS, selenium().getText("//body[@class='editbox']"));
      //      selenium().click("scLocator=//TabSet[ID=\"ideEditorFormTabSet\"]/tab[index=0]/icon");
      //      Thread.sleep(TestConstants.SLEEP);

      // check changed string in TXT file
      IDE.EDITOR.selectTab(5);
      String TXT = "text content";
      assertEquals(TXT, IDE.EDITOR.getTextFromCodeEditor(5));
      //      assertEquals(TXT, selenium().getText("//body[@class='editbox']"));
      //      selenium().click("scLocator=//TabSet[ID=\"ideEditorFormTabSet\"]/tab[index=0]/icon");
      //      Thread.sleep(TestConstants.SLEEP);
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

   public void saveAndCloseFile() throws InterruptedException, Exception
   {
      // Save and closeCssFile
      IDE.EDITOR.selectTab(0);
      saveCurrentFile();
      IDE.EDITOR.closeFile(0);

      // Save and closeGoogleGadgetFile
      IDE.EDITOR.selectTab(0);
      saveCurrentFile();
      IDE.EDITOR.closeFile(0);

      // Save and close HTMLFile
      IDE.EDITOR.selectTab(1);
      saveCurrentFile();
      IDE.EDITOR.closeFile(1);

      // Save and closeJsFile
      IDE.EDITOR.selectTab(1);
      saveCurrentFile();
      IDE.EDITOR.closeFile(1);

      //
      //      // Save and closeXMLFile
      IDE.EDITOR.selectTab(2);
      saveCurrentFile();
      IDE.EDITOR.closeFile(2);

      //      // close GroovyFile
      IDE.EDITOR.selectTab(0);
      IDE.EDITOR.closeFile(0);
      //      Thread.sleep(500);
      //      selenium().click("scLocator=//TabSet[ID=\"ideEditorFormTabSet\"]/tab[index=0]/icon");
      //      Thread.sleep(TestConstants.SLEEP);
      //      //**********TODO**********
      //      checkSaveDialog();
      //
      //      // close TXTFile
      IDE.EDITOR.selectTab(0);
      IDE.EDITOR.closeFile(0);
      //      selenium().click("scLocator=//TabSet[ID=\"ideEditorFormTabSet\"]/tab[index=0]/icon");
      //      Thread.sleep(TestConstants.SLEEP);
      //      //*****TODO************
      //      checkSaveDialog();
      //      assertFalse(selenium().isElementPresent("//body[@class='editbox']"));

   }

   public void changeFiles() throws Exception
   {
      // changeCssFile
      IDE.EDITOR.selectTab(0);
      IDE.EDITOR.typeTextIntoEditor(0, "Change file");
      IDE.EDITOR.pressEnter();
      assertEquals(IDE.EDITOR.getTabTitle(0), CSS_FILE_NAME + " *");

      // changeGoogleGadgetFile
      IDE.EDITOR.selectTab(1);
      IDE.EDITOR.typeTextIntoEditor(1, "Change file");
      //Don't work IDE.EDITOR.pressEnter();
      IDE.EDITOR.runHotkeyWithinEditor(1, false, false, 13);
      assertEquals(IDE.EDITOR.getTabTitle(1), GADGET_FILE_NAME + " *");

      // changeHTMLFile
      IDE.EDITOR.selectTab(3);
      IDE.EDITOR.typeTextIntoEditor(3, "Change file");
      IDE.EDITOR.runHotkeyWithinEditor(3, false, false, 13);
      assertEquals(IDE.EDITOR.getTabTitle(3), HTML_FILE_NAME + " *");

      // changeJavaScriptFile
      IDE.EDITOR.selectTab(4);
      IDE.EDITOR.typeTextIntoEditor(4, "Change file");
      IDE.EDITOR.runHotkeyWithinEditor(4, false, false, 13);
      assertEquals(IDE.EDITOR.getTabTitle(4), JS_FILE_NAME + " *");

      // changeXMLFile
      IDE.EDITOR.selectTab(6);
      IDE.EDITOR.typeTextIntoEditor(6, "Change file");
      IDE.EDITOR.runHotkeyWithinEditor(6, false, false, 13);
      assertEquals(IDE.EDITOR.getTabTitle(6), XML_FILE_NAME + " *");

   }

   protected void clickTabAndCheckSaveButton() throws Exception
   {
      IDE.EDITOR.selectTab(0);
      IDE.TOOLBAR.assertButtonEnabled(ToolbarCommands.File.SAVE, false);

      IDE.EDITOR.selectTab(1);
      IDE.TOOLBAR.assertButtonEnabled(ToolbarCommands.File.SAVE, false);

      IDE.EDITOR.selectTab(2);
      IDE.TOOLBAR.assertButtonEnabled(ToolbarCommands.File.SAVE, false);

      IDE.EDITOR.selectTab(3);
      IDE.TOOLBAR.assertButtonEnabled(ToolbarCommands.File.SAVE, false);

      IDE.EDITOR.selectTab(4);
      IDE.TOOLBAR.assertButtonEnabled(ToolbarCommands.File.SAVE, false);

      IDE.EDITOR.selectTab(5);
      IDE.TOOLBAR.assertButtonEnabled(ToolbarCommands.File.SAVE, false);

      IDE.EDITOR.selectTab(6);
      IDE.TOOLBAR.assertButtonEnabled(ToolbarCommands.File.SAVE, false);
   }

   public void openXML() throws InterruptedException, Exception
   {
      IDE.NAVIGATION.openFileFromNavigationTreeWithCodeEditor(STORAGE_URL + XML_FILE_NAME, false);
      IDE.EDITOR.waitTabPresent(6);
   }

   public void openTXT() throws InterruptedException, Exception
   {

      IDE.NAVIGATION.openFileFromNavigationTreeWithCodeEditor(STORAGE_URL + TXT_FILE_NAME, false);
      IDE.EDITOR.waitTabPresent(5);
   }

   public void openJavaScript() throws InterruptedException, Exception
   {
      IDE.NAVIGATION.openFileFromNavigationTreeWithCodeEditor(STORAGE_URL + JS_FILE_NAME, false);
      IDE.EDITOR.waitTabPresent(4);
   }

   public void openHtml() throws InterruptedException, Exception
   {
      IDE.NAVIGATION.openFileFromNavigationTreeWithCodeEditor(STORAGE_URL + HTML_FILE_NAME, false);
      IDE.EDITOR.waitTabPresent(3);
   }

   public void openGroovy() throws InterruptedException, Exception
   {

      IDE.NAVIGATION.openFileFromNavigationTreeWithCodeEditor(STORAGE_URL + GROOVY_FILE_NAME, false);
      IDE.EDITOR.waitTabPresent(2);
   }

   public void openGooglegadget() throws InterruptedException, Exception
   {

      IDE.NAVIGATION.openFileFromNavigationTreeWithCodeEditor(STORAGE_URL + GADGET_FILE_NAME, false);
      IDE.EDITOR.waitTabPresent(1);
   }

   public void openCss() throws InterruptedException, Exception
   {
      IDE.NAVIGATION.openFileFromNavigationTreeWithCodeEditor(STORAGE_URL + CSS_FILE_NAME, false);
      IDE.EDITOR.waitTabPresent(0);
      //Thread.sleep(TestConstants.EDITOR_OPEN_PERIOD);

   }

   //****************TODO fix  Task IDE-445
   public void checkSaveDialog() throws InterruptedException
   {
      if (selenium().isElementPresent("scLocator=//Dialog[ID=\"isc_globalWarn\"]/header/member/"))
      {
         selenium().click("scLocator=//Dialog[ID=\"isc_globalWarn\"]/noButton/");
         Thread.sleep(TestConstants.EDITOR_OPEN_PERIOD);
      }
      else
      {
         Thread.sleep(TestConstants.TYPE_DELAY_PERIOD);
      }
   }

}
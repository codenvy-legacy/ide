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

import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.MenuCommands;
import org.exoplatform.ide.ToolbarCommands;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.exoplatform.ide.vfs.shared.Link;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.WebDriver;

import java.io.IOException;
import java.util.Map;

/**
 * @author <a href="mailto:musienko.maxim@gmail.com">Musienko Maxim</a>
 */
public class OpeningSavingAndClosingFilesTest extends BaseTest
{

   private static String PROJECT = OpeningSavingAndClosingFilesTest.class.getSimpleName();

   private static String HTML_FILE_NAME = "newHtmlFile.html";

   private static String CSS_FILE_NAME = "newCssFile.css";

   private static String JS_FILE_NAME = "newJavaScriptFile.js";

   private static String GADGET_FILE_NAME = "newGoogleGadget.gadget";

   private static String GROOVY_FILE_NAME = "newGroovyFile.groovy";

   private static String XML_FILE_NAME = "newXMLFile.xml";

   private static String TXT_FILE_NAME = "newTextFile.txt";

   private static String DEFAULT_CONTENT_CSS_FILE =
      "/*Some example CSS*/\n\n@import url (\"something.css\")\nbody {\n  margin 0;\n  padding 3em 6em;\n  font-family: tahoma, arial, sans-serif;\n  color #000;\n}\n  #navigation a {\n    font-weigt: bold;\n  text-decoration: none !important;\n}\n}";

   private static String DEFAULT_CONTENT_HTML_FILE =
      "<html>\n<head>\n  <title>HTML Example</title>\n  <script type='text/javascript'>\n    function foo(bar, baz) {\n      alert('quux');\n      return bar + baz + 1;\n    }\n  </script>\n  <style type='text/css'>\n    div.border {\n      border: 1px solid black;\n      padding: 3px;\n    }\n    #foo code {\n      font-family: courier, monospace;\n      font-size: 80%;\n      color: #448888;\n    }\n  </style>\n</head>\n<body>\n  <p>Hello</p>\n</body>\n</html>";

   private static String DEFAULT_CONTENT_GADGET_FILE =
      "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n<Module>\n  <ModulePrefs title=\"Hello World!\" />\n  <Content type=\"html\">\n    <![CDATA[\n    <script type='text/javascript'>\n      function foo(bar, baz) {\n        alert('quux');\n        return bar + baz + 1;\n      }\n    </script>\n    <style type='text/css'>\n      div.border {\n        border: 1px solid black;\n        padding: 3px;\n      }\n      #foo code {\n        font-family: courier, monospace;\n        font-size: 80%;\n        color: #448888;\n      }\n    </style>\n    <p>Hello</p>\n    ]]></Content></Module>";

   private static String DEFAULT_CONTENT_JS_FILE =
      "//Here you see some JavaScript code. Mess around with it to get\n//acquinted with CodeMirror's features.\n\n// Press enter inside the objects and your new line will\n// intended.\n\nvar keyBindings ={\n  enter:\"newline-and-indent\",\n  tab:\"reindent-selection\",\n  ctrl_z \"undo\",\n  ctrl_y:\"redo\"\n  };\n  var regex =/foo|bar/i;\n  function example (x){\n  var y=44.4;\n  return x+y;\n  }";

   private static String DEFAULT_CONTENT_GROOVY_FILE =
      "//simple groovy script\n\nimport javax.ws.rs.Path\nimport javax.ws.rs.GET\nimport javax.ws.rs.PathParam\n\n@Path (\"/\")\npublic class HelloWorld{\n@Get\n@Path (\"helloworld/{name}\")\npublic String hello(PathParam(\"name\")String name){\n  return \"Hello\"+name\n  }\n  }";

   private static String DEFAULT_CONTENT_XML_FILE =
      "<?xml version='1.0' encoding='UTF-8'?>\n<Module>\n  <UserPref>name=\"last_location\" datatype=\"hidden\"</UserPref>\n</Module>";

   private static String DEFAULT_CONTENT_TXT_FILE = "text content";

   private final static String PATH = "src/test/resources/org/exoplatform/ide/operation/file/";

   @BeforeClass
   public static void setUp()
   {
      try
      {
         Map<String, Link> project = VirtualFileSystemUtils.createDefaultProject(PROJECT);
         Link link = project.get(Link.REL_CREATE_FILE);
         VirtualFileSystemUtils.createFileFromLocal(link, HTML_FILE_NAME, MimeType.TEXT_HTML, PATH + HTML_FILE_NAME);
         VirtualFileSystemUtils.createFileFromLocal(link, CSS_FILE_NAME, MimeType.TEXT_CSS, PATH + CSS_FILE_NAME);
         VirtualFileSystemUtils.createFileFromLocal(link, JS_FILE_NAME, MimeType.APPLICATION_JAVASCRIPT, PATH
            + JS_FILE_NAME);
         VirtualFileSystemUtils.createFileFromLocal(link, GADGET_FILE_NAME, MimeType.GOOGLE_GADGET, PATH
            + GADGET_FILE_NAME);
         VirtualFileSystemUtils.createFileFromLocal(link, GROOVY_FILE_NAME, MimeType.GROOVY_SERVICE, PATH
            + GROOVY_FILE_NAME);
         VirtualFileSystemUtils.createFileFromLocal(link, XML_FILE_NAME, MimeType.TEXT_XML, PATH + XML_FILE_NAME);
         VirtualFileSystemUtils.createFileFromLocal(link, TXT_FILE_NAME, MimeType.TEXT_PLAIN, PATH + TXT_FILE_NAME);
      }
      catch (IOException e)
      {
      }
   }

   @AfterClass
   public static void tearDown()
   {
      try
      {
         VirtualFileSystemUtils.delete(WS_URL + PROJECT);
      }
      catch (IOException e)
      {
      }
   }

   @Test
   public void testOpeningSavingAndClosingTabsWithFile() throws Exception
   {
      IDE.PROJECT.EXPLORER.waitOpened();
      IDE.PROJECT.OPEN.openProject(PROJECT);
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT);

      //step 1 check all files is present, open all files, checks default content.
      checkAllFilesPresent();

      //close welcome tab for easy indexing of tabs and iframes in testcase
      IDE.EDITOR.clickCloseEditorButton(0);
      IDE.EDITOR.waitTabNotPresent(0);
      checkDefaultContentInOpenFiles();

      //step 2 change file (add string "change file" into CSS, gadget, html and js file)
      IDE.EDITOR.selectTab("newCssFile.css");
      IDE.EDITOR.typeTextIntoEditor(0, "Change file\n");
      IDE.MENU.runCommand(MenuCommands.File.FILE, MenuCommands.File.SAVE);
      IDE.LOADER.waitClosed();
      assertFalse(IDE.EDITOR.isFileContentChanged(0));

      IDE.EDITOR.selectTab("newHtmlFile.html");
      IDE.EDITOR.typeTextIntoEditor(1, "Change file\n");
      IDE.MENU.runCommand(MenuCommands.File.FILE, MenuCommands.File.SAVE);
      IDE.LOADER.waitClosed();
      assertFalse(IDE.EDITOR.isFileContentChanged(1));

      IDE.EDITOR.selectTab("newJavaScriptFile.js");
      IDE.EDITOR.typeTextIntoEditor(2, "Change file\n");
      IDE.MENU.runCommand(MenuCommands.File.FILE, MenuCommands.File.SAVE);
      IDE.LOADER.waitClosed();
      assertFalse(IDE.EDITOR.isFileContentChanged(2));

      IDE.EDITOR.selectTab("newGroovyFile.groovy");
      IDE.EDITOR.typeTextIntoEditor(4, "Change file\n");
      IDE.MENU.runCommand(MenuCommands.File.FILE, MenuCommands.File.SAVE);
      IDE.LOADER.waitClosed();
      assertFalse(IDE.EDITOR.isFileContentChanged(4));

      //step 3 close all files
      IDE.EDITOR.clickCloseEditorButton(CSS_FILE_NAME);
      IDE.LOADER.waitClosed();
      IDE.EDITOR.waitTabNotPresent(CSS_FILE_NAME);

      IDE.EDITOR.clickCloseEditorButton(HTML_FILE_NAME);
      IDE.LOADER.waitClosed();
      IDE.EDITOR.waitTabNotPresent(HTML_FILE_NAME);

      IDE.EDITOR.clickCloseEditorButton(GADGET_FILE_NAME);
      IDE.LOADER.waitClosed();
      IDE.EDITOR.waitTabNotPresent(GADGET_FILE_NAME);

      IDE.EDITOR.clickCloseEditorButton(GROOVY_FILE_NAME);
      IDE.LOADER.waitClosed();
      IDE.EDITOR.waitTabNotPresent(GROOVY_FILE_NAME);

      IDE.EDITOR.clickCloseEditorButton(JS_FILE_NAME);
      IDE.LOADER.waitClosed();
      IDE.EDITOR.waitTabNotPresent(JS_FILE_NAME);

      IDE.EDITOR.clickCloseEditorButton(TXT_FILE_NAME);
      IDE.LOADER.waitClosed();
      IDE.EDITOR.waitTabNotPresent(TXT_FILE_NAME);

      IDE.EDITOR.clickCloseEditorButton(XML_FILE_NAME);
      IDE.LOADER.waitClosed();
      IDE.EDITOR.waitTabNotPresent(XML_FILE_NAME);

      //step 4 reopen all files check changed and not changed file. Check save button state
      driver.navigate().refresh();
      checkAllFilesPresent();
      IDE.EDITOR.clickCloseEditorButton(0);
      IDE.EDITOR.waitTabNotPresent(0);
      checkStatusReopenedFiles();
   }

   /**
    * method is check all status (change and not change) after 
    * previous steps
    * @throws Exception
    */
   private void checkStatusReopenedFiles() throws Exception
   {
      IDE.PROJECT.EXPLORER.openItem(PROJECT + "/" + CSS_FILE_NAME);
      IDE.LOADER.waitClosed();
      IDE.EDITOR.waitActiveFile(PROJECT + "/" + CSS_FILE_NAME);
      assertFalse(IDE.TOOLBAR.isButtonEnabled("Save"));
      IDE.EDITOR.waitActiveFile(PROJECT + "/" + CSS_FILE_NAME);
      assertEquals("Change file\n" + DEFAULT_CONTENT_CSS_FILE, IDE.EDITOR.getTextFromCodeEditor(0));

      IDE.PROJECT.EXPLORER.openItem(PROJECT + "/" + HTML_FILE_NAME);
      IDE.LOADER.waitClosed();
      IDE.EDITOR.waitActiveFile(PROJECT + "/" + HTML_FILE_NAME);
      assertFalse(IDE.TOOLBAR.isButtonEnabled("Save"));
      IDE.EDITOR.selectTab(1);
      IDE.EDITOR.waitActiveFile(PROJECT + "/" + HTML_FILE_NAME);
      assertEquals("Change file\n" + DEFAULT_CONTENT_HTML_FILE, IDE.EDITOR.getTextFromCodeEditor(1));

      IDE.PROJECT.EXPLORER.openItem(PROJECT + "/" + JS_FILE_NAME);
      IDE.LOADER.waitClosed();
      IDE.EDITOR.waitActiveFile(PROJECT + "/" + JS_FILE_NAME);
      assertFalse(IDE.TOOLBAR.isButtonEnabled("Save"));
      IDE.EDITOR.selectTab(2);
      assertEquals("Change file\n  " + DEFAULT_CONTENT_JS_FILE, IDE.EDITOR.getTextFromCodeEditor(2));

      IDE.PROJECT.EXPLORER.openItem(PROJECT + "/" + GADGET_FILE_NAME);
      IDE.LOADER.waitClosed();
      IDE.EDITOR.waitActiveFile(PROJECT + "/" + GADGET_FILE_NAME);
      assertFalse(IDE.TOOLBAR.isButtonEnabled("Save"));
      IDE.EDITOR.selectTab(3);
      assertEquals(DEFAULT_CONTENT_GADGET_FILE, IDE.EDITOR.getTextFromCodeEditor(3));

      IDE.PROJECT.EXPLORER.openItem(PROJECT + "/" + GROOVY_FILE_NAME);
      IDE.LOADER.waitClosed();
      IDE.EDITOR.waitActiveFile(PROJECT + "/" + GROOVY_FILE_NAME);
      assertFalse(IDE.TOOLBAR.isButtonEnabled("Save"));
      IDE.EDITOR.selectTab(4);
      assertEquals("Change file\n" + DEFAULT_CONTENT_GROOVY_FILE, IDE.EDITOR.getTextFromCodeEditor(4));

      IDE.PROJECT.EXPLORER.openItem(PROJECT + "/" + XML_FILE_NAME);
      IDE.LOADER.waitClosed();
      IDE.EDITOR.waitActiveFile(PROJECT + "/" + XML_FILE_NAME);
      assertFalse(IDE.TOOLBAR.isButtonEnabled("Save"));
      IDE.EDITOR.selectTab(5);
      assertEquals(DEFAULT_CONTENT_XML_FILE, IDE.EDITOR.getTextFromCodeEditor(5));

      IDE.PROJECT.EXPLORER.openItem(PROJECT + "/" + TXT_FILE_NAME);
      IDE.LOADER.waitClosed();
      IDE.EDITOR.waitActiveFile(PROJECT + "/" + TXT_FILE_NAME);
      assertFalse(IDE.TOOLBAR.isButtonEnabled("Save"));
      IDE.EDITOR.selectTab(6);
      assertEquals(DEFAULT_CONTENT_TXT_FILE, IDE.EDITOR.getTextFromCodeEditor(6));
   }

   /**
    * @throws Exception
    */
   private void checkDefaultContentInOpenFiles() throws Exception
   {
      IDE.PROJECT.EXPLORER.openItem(PROJECT + "/" + CSS_FILE_NAME);
      IDE.LOADER.waitClosed();
      IDE.EDITOR.waitActiveFile(PROJECT + "/" + CSS_FILE_NAME);
      assertEquals(DEFAULT_CONTENT_CSS_FILE, IDE.EDITOR.getTextFromCodeEditor(0));

      IDE.PROJECT.EXPLORER.openItem(PROJECT + "/" + HTML_FILE_NAME);
      IDE.LOADER.waitClosed();
      IDE.EDITOR.waitActiveFile(PROJECT + "/" + HTML_FILE_NAME);
      IDE.EDITOR.selectTab(1);
      IDE.EDITOR.waitActiveFile(PROJECT + "/" + HTML_FILE_NAME);
      assertEquals(DEFAULT_CONTENT_HTML_FILE, IDE.EDITOR.getTextFromCodeEditor(1));

      IDE.PROJECT.EXPLORER.openItem(PROJECT + "/" + JS_FILE_NAME);
      IDE.LOADER.waitClosed();
      IDE.EDITOR.waitActiveFile(PROJECT + "/" + JS_FILE_NAME);
      IDE.EDITOR.selectTab(2);
      IDE.EDITOR.waitActiveFile(PROJECT + "/" + JS_FILE_NAME);
      assertEquals(DEFAULT_CONTENT_JS_FILE, IDE.EDITOR.getTextFromCodeEditor(2));

      IDE.PROJECT.EXPLORER.openItem(PROJECT + "/" + GADGET_FILE_NAME);
      IDE.LOADER.waitClosed();
      IDE.EDITOR.waitActiveFile(PROJECT + "/" + GADGET_FILE_NAME);
      IDE.EDITOR.selectTab(3);
      IDE.EDITOR.waitActiveFile(PROJECT + "/" + GADGET_FILE_NAME);
      assertEquals(DEFAULT_CONTENT_GADGET_FILE, IDE.EDITOR.getTextFromCodeEditor(3));

      IDE.PROJECT.EXPLORER.openItem(PROJECT + "/" + GROOVY_FILE_NAME);
      IDE.LOADER.waitClosed();
      IDE.EDITOR.waitActiveFile(PROJECT + "/" + GROOVY_FILE_NAME);
      IDE.EDITOR.selectTab(4);
      IDE.EDITOR.waitActiveFile(PROJECT + "/" + GROOVY_FILE_NAME);
      assertEquals(DEFAULT_CONTENT_GROOVY_FILE, IDE.EDITOR.getTextFromCodeEditor(4));

      IDE.PROJECT.EXPLORER.openItem(PROJECT + "/" + XML_FILE_NAME);
      IDE.LOADER.waitClosed();
      IDE.EDITOR.waitActiveFile(PROJECT + "/" + XML_FILE_NAME);
      IDE.EDITOR.selectTab(5);
      IDE.EDITOR.waitActiveFile(PROJECT + "/" + XML_FILE_NAME);
      assertEquals(DEFAULT_CONTENT_XML_FILE, IDE.EDITOR.getTextFromCodeEditor(5));

      IDE.PROJECT.EXPLORER.openItem(PROJECT + "/" + TXT_FILE_NAME);
      IDE.LOADER.waitClosed();
      IDE.EDITOR.waitActiveFile(PROJECT + "/" + TXT_FILE_NAME);
      IDE.EDITOR.selectTab(6);
      IDE.EDITOR.waitActiveFile(PROJECT + "/" + TXT_FILE_NAME);
      assertEquals(DEFAULT_CONTENT_TXT_FILE, IDE.EDITOR.getTextFromCodeEditor(6));
   }

   /**
    * @throws Exception
    */
   private void checkAllFilesPresent() throws Exception
   {
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + CSS_FILE_NAME);
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + HTML_FILE_NAME);
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + JS_FILE_NAME);
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + GROOVY_FILE_NAME);
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + GADGET_FILE_NAME);
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + XML_FILE_NAME);
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + TXT_FILE_NAME);
   }

}
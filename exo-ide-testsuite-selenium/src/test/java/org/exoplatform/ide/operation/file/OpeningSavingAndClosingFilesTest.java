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
import org.exoplatform.ide.ToolbarCommands;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.exoplatform.ide.vfs.shared.Link;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

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
         e.printStackTrace();
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
         e.printStackTrace();
      }
   }

   @Test
   public void testOpeningSavingAndClosingTabsWithFile() throws Exception
   {
      IDE.PROJECT.EXPLORER.waitOpened();
      IDE.PROJECT.OPEN.openProject(PROJECT);
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT);
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + CSS_FILE_NAME);

      reopenFiles();
      changeFiles();
      saveAndCloseFile();
      reopenFiles();
      clickTabAndCheckSaveButton();
      checkSaveInFiles();
   }

   /**
    * Check changes were saved in file.
    * 
    * @throws Exception
    */
   public void checkSaveInFiles() throws Exception
   {
      // check changed string in CSS file
      String CSS =
         "Change file\n/*Some example CSS*/\n\n@import url (\"something.css\")\nbody {\n  margin 0;\n  padding 3em 6em;\n  font-family: tahoma, arial, sans-serif;\n  color #000;\n}\n  #navigation a {\n    font-weigt: bold;\n  text-decoration: none !important;\n}\n}";
      IDE.EDITOR.selectTab(CSS_FILE_NAME);
      assertEquals(CSS, IDE.EDITOR.getTextFromCodeEditor(7));

      // check changed string in Google Gadget file
      String GG =
         "Change file\n<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n<Module>\n  <ModulePrefs title=\"Hello World!\" />\n  <Content type=\"html\">\n    <![CDATA[\n    <script type='text/javascript'>\n      function foo(bar, baz) {\n        alert('quux');\n        return bar + baz + 1;\n      }\n    </script>\n    <style type='text/css'>\n      div.border {\n        border: 1px solid black;\n        padding: 3px;\n      }\n      #foo code {\n        font-family: courier, monospace;\n        font-size: 80%;\n        color: #448888;\n      }\n    </style>\n    <p>Hello</p>\n    ]]></Content></Module>";
      IDE.EDITOR.selectTab(GADGET_FILE_NAME);
      assertEquals(GG, IDE.EDITOR.getTextFromCodeEditor(8));

      // check changed string in Groovy file
      String Groovy =
         "//simple groovy script\n\nimport javax.ws.rs.Path\nimport javax.ws.rs.GET\nimport javax.ws.rs.PathParam\n\n@Path (\"/\")\npublic class HelloWorld{\n@Get\n@Path (\"helloworld/{name}\")\npublic String hello(PathParam(\"name\")String name){\n  return \"Hello\"+name\n  }\n  }";
      IDE.EDITOR.selectTab(GROOVY_FILE_NAME);
      assertEquals(Groovy, IDE.EDITOR.getTextFromCodeEditor(9));

      // check changed string in HTML file
      String HTML =
         "Change file\n<html>\n<head>\n  <title>HTML Example</title>\n  <script type='text/javascript'>\n    function foo(bar, baz) {\n      alert('quux');\n      return bar + baz + 1;\n    }\n  </script>\n  <style type='text/css'>\n    div.border {\n      border: 1px solid black;\n      padding: 3px;\n    }\n    #foo code {\n      font-family: courier, monospace;\n      font-size: 80%;\n      color: #448888;\n    }\n  </style>\n</head>\n<body>\n  <p>Hello</p>\n</body>\n</html>";
      IDE.EDITOR.selectTab(HTML_FILE_NAME);
      assertEquals(HTML, IDE.EDITOR.getTextFromCodeEditor(10));

      // check changed string in JS file
      String JS =
         "var a=5;\n//Here you see some JavaScript code. Mess around with it to get\n//acquinted with CodeMirror's features.\n\n// Press enter inside the objects and your new line will\n// intended.\n\nvar keyBindings ={\n  enter:\"newline-and-indent\",\n  tab:\"reindent-selection\",\n  ctrl_z \"undo\",\n  ctrl_y:\"redo\"\n  };\n  var regex =/foo|bar/i;\n  function example (x){\n  var y=44.4;\n  return x+y;\n  }";
      IDE.EDITOR.selectTab(JS_FILE_NAME);
      assertEquals(JS, IDE.EDITOR.getTextFromCodeEditor(11));

      // check changed string in TXT file
      String TXT = "text content";
      IDE.EDITOR.selectTab(TXT_FILE_NAME);
      assertEquals(TXT, IDE.EDITOR.getTextFromCodeEditor(12));
   }

   /**
    * Reopened files.
    * 
    * @throws InterruptedException
    * @throws Exception
    */
   public void reopenFiles() throws InterruptedException, Exception
   {
      openCss();
      openGooglegadget();
      openGroovy();
      openHtml();
      openJavaScript();
      openTXT();
      openXML();
   }

   /**
    * Save file and close it.
    * 
    * @throws InterruptedException
    * @throws Exception
    */
   public void saveAndCloseFile() throws InterruptedException, Exception
   {
      // Save and closeCssFile
      IDE.EDITOR.selectTab(CSS_FILE_NAME);
      IDE.TOOLBAR.runCommand(ToolbarCommands.File.SAVE);
      IDE.EDITOR.waitNoContentModificationMark(CSS_FILE_NAME);
      IDE.EDITOR.closeFile(CSS_FILE_NAME);

      // Save and closeGoogleGadgetFile
      IDE.EDITOR.selectTab(GADGET_FILE_NAME);
      IDE.TOOLBAR.runCommand(ToolbarCommands.File.SAVE);
      IDE.EDITOR.waitNoContentModificationMark(GADGET_FILE_NAME);
      IDE.EDITOR.closeFile(GADGET_FILE_NAME);

      // Save and close HTMLFile
      IDE.EDITOR.selectTab(HTML_FILE_NAME);
      IDE.TOOLBAR.runCommand(ToolbarCommands.File.SAVE);
      IDE.EDITOR.waitNoContentModificationMark(HTML_FILE_NAME);
      IDE.EDITOR.closeFile(HTML_FILE_NAME);

      // Save and closeJsFile
      IDE.EDITOR.selectTab(JS_FILE_NAME);
      IDE.TOOLBAR.runCommand(ToolbarCommands.File.SAVE);
      IDE.EDITOR.waitNoContentModificationMark(JS_FILE_NAME);
      IDE.EDITOR.closeFile(JS_FILE_NAME);

      //
      //      // Save and closeXMLFile
      IDE.EDITOR.selectTab(XML_FILE_NAME);
      IDE.TOOLBAR.runCommand(ToolbarCommands.File.SAVE);
      IDE.EDITOR.waitNoContentModificationMark(XML_FILE_NAME);
      IDE.EDITOR.closeFile(XML_FILE_NAME);

      //      // close GroovyFile
      IDE.EDITOR.closeFile(GROOVY_FILE_NAME);
      IDE.EDITOR.closeFile(TXT_FILE_NAME);
   }

   /**
    * Change files' content.
    * 
    * @throws Exception
    */
   public void changeFiles() throws Exception
   {
      // changeCssFile
      assertFalse(IDE.EDITOR.isFileContentChanged(CSS_FILE_NAME));
      IDE.EDITOR.selectTab(CSS_FILE_NAME);
      IDE.EDITOR.typeTextIntoEditor(0, "Change file\n");
      IDE.EDITOR.waitFileContentModificationMark(CSS_FILE_NAME);

      // changeGoogleGadgetFile
      assertFalse(IDE.EDITOR.isFileContentChanged(GADGET_FILE_NAME));
      IDE.EDITOR.selectTab(GADGET_FILE_NAME);
      IDE.EDITOR.typeTextIntoEditor(1, "Change file\n");
      IDE.EDITOR.waitFileContentModificationMark(GADGET_FILE_NAME);

      // changeHTMLFile
      assertFalse(IDE.EDITOR.isFileContentChanged(HTML_FILE_NAME));
      IDE.EDITOR.selectTab(HTML_FILE_NAME);
      IDE.EDITOR.typeTextIntoEditor(3, "Change file\n");
      IDE.EDITOR.waitFileContentModificationMark(HTML_FILE_NAME);

      // changeJavaScriptFile
      assertFalse(IDE.EDITOR.isFileContentChanged(JS_FILE_NAME));
      IDE.EDITOR.selectTab(JS_FILE_NAME);
      IDE.EDITOR.typeTextIntoEditor(4, "var a=5;\n");
      IDE.EDITOR.waitFileContentModificationMark(JS_FILE_NAME);

      // changeXMLFile
      assertFalse(IDE.EDITOR.isFileContentChanged(XML_FILE_NAME));
      IDE.EDITOR.selectTab(XML_FILE_NAME);
      IDE.EDITOR.typeTextIntoEditor(6, "Change file\n");
      IDE.EDITOR.waitFileContentModificationMark(XML_FILE_NAME);
   }

   protected void clickTabAndCheckSaveButton() throws Exception
   {
      IDE.EDITOR.selectTab(CSS_FILE_NAME);
      assertFalse(IDE.TOOLBAR.isButtonEnabled(ToolbarCommands.File.SAVE));

      IDE.EDITOR.selectTab(HTML_FILE_NAME);
      assertFalse(IDE.TOOLBAR.isButtonEnabled(ToolbarCommands.File.SAVE));

      IDE.EDITOR.selectTab(GADGET_FILE_NAME);
      assertFalse(IDE.TOOLBAR.isButtonEnabled(ToolbarCommands.File.SAVE));

      IDE.EDITOR.selectTab(GROOVY_FILE_NAME);
      assertFalse(IDE.TOOLBAR.isButtonEnabled(ToolbarCommands.File.SAVE));

      IDE.EDITOR.selectTab(JS_FILE_NAME);
      assertFalse(IDE.TOOLBAR.isButtonEnabled(ToolbarCommands.File.SAVE));

      IDE.EDITOR.selectTab(TXT_FILE_NAME);
      assertFalse(IDE.TOOLBAR.isButtonEnabled(ToolbarCommands.File.SAVE));

      IDE.EDITOR.selectTab(XML_FILE_NAME);
      assertFalse(IDE.TOOLBAR.isButtonEnabled(ToolbarCommands.File.SAVE));
   }

   public void openXML() throws InterruptedException, Exception
   {
      IDE.PROJECT.EXPLORER.openItem(PROJECT + "/" + XML_FILE_NAME);
      IDE.EDITOR.waitActiveFile(PROJECT + "/" + XML_FILE_NAME);
   }

   public void openTXT() throws InterruptedException, Exception
   {
      IDE.PROJECT.EXPLORER.openItem(PROJECT + "/" + TXT_FILE_NAME);
      IDE.EDITOR.waitActiveFile(PROJECT + "/" + TXT_FILE_NAME);
   }

   public void openJavaScript() throws InterruptedException, Exception
   {
      IDE.PROJECT.EXPLORER.openItem(PROJECT + "/" + JS_FILE_NAME);
      IDE.EDITOR.waitActiveFile(PROJECT + "/" + JS_FILE_NAME);
   }

   public void openHtml() throws InterruptedException, Exception
   {
      IDE.PROJECT.EXPLORER.openItem(PROJECT + "/" + HTML_FILE_NAME);
      IDE.EDITOR.waitActiveFile(PROJECT + "/" + HTML_FILE_NAME);
   }

   public void openGroovy() throws InterruptedException, Exception
   {
      IDE.PROJECT.EXPLORER.openItem(PROJECT + "/" + GROOVY_FILE_NAME);
      IDE.EDITOR.waitActiveFile(PROJECT + "/" + GROOVY_FILE_NAME);
   }

   public void openGooglegadget() throws InterruptedException, Exception
   {
      IDE.PROJECT.EXPLORER.openItem(PROJECT + "/" + GADGET_FILE_NAME);
      IDE.EDITOR.waitActiveFile(PROJECT + "/" + GADGET_FILE_NAME);
   }

   public void openCss() throws InterruptedException, Exception
   {
      IDE.PROJECT.EXPLORER.openItem(PROJECT + "/" + CSS_FILE_NAME);
      IDE.EDITOR.waitActiveFile(PROJECT + "/" + CSS_FILE_NAME);
   }
}
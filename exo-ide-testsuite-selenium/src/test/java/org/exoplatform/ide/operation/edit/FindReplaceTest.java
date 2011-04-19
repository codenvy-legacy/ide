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
import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.MenuCommands;
import org.exoplatform.ide.TestConstants;
import org.exoplatform.ide.ToolbarCommands;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: 2010
 * 
 */
public class FindReplaceTest extends BaseTest
{
   private final String NOT_FOUND = "String not found.";

   private final static String FILE_CONTENT_FOR_FIND =
      "IDEall gadget operates with repository folders and files, which are displayed in Workspace Panel,\n"
         + " which - represents repository folders and files in the tree view, and Content Panels,\n"
         + " which consists of two horizontal panels:\n"
         + "Top panel represents files in several file tabs with file content;\n"
         + "Bottom panel could contain several tabs with table of file properties, REST Service and Google Gadget output messages, \n"
         + "Html or Gadget files preview. To view this tabs you should use special buttons at the right part of Toolbar or View or Run top menu commands.\n"
         + "These panels are divided by Horizontal Resize Bar. \n"
         + "Also Workspace Panel and Content Panel are divided by Vertical Resize Bar.\n"
         + "You can maximize or minimize one of the \n"
         + "file tabs or action tabs from bottom part of Content Panel by clicking on special button at the top right corner of panel.\n"
         + "Basic operations include browsing, creating, editing, coping, renaming, uploading, downloading, moving, deleting the files and folders";

   private final static String FILE_CONTENT_FOR_REPLACE =
      "IDEall gadget operates with repository folders and files, \n"
         + "which are displayed in Workspace Panel, which - represents repository folders and files in the tree view,\n"
         + " and Content Panels, which consists of two horizontal panels:\n"
         + " These panels are divided by Horizontal Resize Bar. \n"
         + "Also Workspace Panel and Content Panel are divided by Vertical Resize Bar.\n"
         + "You can maximize or minimize one of the file tabs or action tabs from bottom part of\n"
         + " Content Panel by clicking on special button at the top right corner of panel. \n"
         + "Basic operations include browsing, creating, editing, coping, renaming, uploading, \n"
         + "downloading, moving, deleting the files and folders.";

   private static final String FILE_NAME_TXT = "findReplace.txt";

   private static final String FILE_NAME = "findReplace";

   private static final String FILE_NAME_GROOVY_1 = "findReplace1.groovy";
   
   private static final String FILE_NAME_GROOVY_2 = "findReplace2.groovy";

   private static final String FILE_NAME_HTML = "findReplace.html";
   
   private final static String TEST_FOLDER = FindReplaceTest.class.getSimpleName();

   private final static String GROOVY_FILE_CONTENT =
      "// simple groovy script\n" + "import javax.ws.rs.Path\n" + "import javax.ws.rs.GET\n"
         + "import javax.ws.rs.PathParam\n" + "@Path(\"/\")\n" + "public class HelloWorld {\n" + "@GET\n"
         + "@Path(\"helloworld/{name}\")\n" + "public String hello(@PathParam(\"name\") String name) {\n"
         + "return \"Hello \" + name\n" + "}\n" + "}";

   private final static String HTML_FILE_CONTENT =
      "<html>\n" + "<head>\n" + "<title></title>\n" + "</head>\n" + "<body>\n" + "</body>\n" + "</html>";

   private final String wordToFind1 = "gadget";

   private final String wordToFind2 = "apple";

   private final String wordToFind3 = "panel";

   private final String wordToReplace = "form";

   private final static String URL = BASE_URL + REST_CONTEXT + "/" + WEBDAV_CONTEXT + "/" + REPO_NAME + "/" + WS_NAME + "/";

   
   @BeforeClass
   public static void setUp()
   {
      try
      {
         VirtualFileSystemUtils.mkcol(URL + TEST_FOLDER);
         VirtualFileSystemUtils.put(FILE_CONTENT_FOR_FIND.getBytes(), MimeType.TEXT_PLAIN, URL + TEST_FOLDER + "/" + FILE_NAME_TXT);
         VirtualFileSystemUtils.put(FILE_CONTENT_FOR_REPLACE.getBytes(), MimeType.TEXT_PLAIN, URL + TEST_FOLDER + "/" + FILE_NAME);
         VirtualFileSystemUtils.put(GROOVY_FILE_CONTENT.getBytes(), MimeType.APPLICATION_GROOVY, URL + TEST_FOLDER + "/" + FILE_NAME_GROOVY_1);
         VirtualFileSystemUtils.put(GROOVY_FILE_CONTENT.getBytes(), MimeType.APPLICATION_GROOVY, URL + TEST_FOLDER + "/" + FILE_NAME_GROOVY_2);
         VirtualFileSystemUtils.put(HTML_FILE_CONTENT.getBytes(), MimeType.TEXT_HTML, URL + TEST_FOLDER + "/" + FILE_NAME_HTML);
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

   /**
    * IDE-164:Find text in file.
    * 
    * @throws Exception
    */
   @Test
   public void testFindTextInFile() throws Exception
   {
      selenium.refresh();
      selenium.waitForPageToLoad(String.valueOf(TestConstants.IDE_LOAD_PERIOD));
      Thread.sleep(TestConstants.SLEEP);
      IDE.navigator().selectItem(WS_URL);
      IDE.menu().runCommand(MenuCommands.File.FILE, MenuCommands.File.REFRESH);
//      Thread.sleep(TestConstants.SLEEP);
      
      IDE.navigator().clickOpenIconOfFolder(WS_URL + TEST_FOLDER + "/");
      Thread.sleep(TestConstants.REDRAW_PERIOD);
      
      IDE.navigator().openFileFromNavigationTreeWithCodeEditor(FILE_NAME_TXT, false);
      Thread.sleep(TestConstants.SLEEP);
      IDE.toolbar().assertButtonEnabled(ToolbarCommands.Editor.FIND_REPLACE, true);
      IDE.toolbar().runCommand(ToolbarCommands.Editor.FIND_REPLACE);
      Thread.sleep(TestConstants.SLEEP);
      checkFindReplaceFormAppeared();
      IDE.toolbar().assertButtonEnabled(ToolbarCommands.Editor.FIND_REPLACE, false);

      // Type what to find
      selenium
         .typeKeys(
            "scLocator=//DynamicForm[ID=\"ideFindReplaceTextFormDynamicForm\"]/item[name=ideFindReplaceTextFormFindField]/element",
            wordToFind1);
      checkFindFieldNotEmptyState();

      // Step 5. Put cursor at the start of the document and click "Find"
      // button.
      selenium
         .fireEvent(
            "scLocator=//DynamicForm[ID=\"ideFindReplaceTextFormDynamicForm\"]/item[name=ideFindReplaceTextFormFindField]/element",
            "blur");
      selenium.fireEvent("//", "focus");
      Thread.sleep(TestConstants.SLEEP);

      // Make system mouse click on editor space
      IDE.editor().clickOnEditor();

      selenium.click("scLocator=//IButton[ID=\"ideFindReplaceTextFormFindButton\"]");
      Thread.sleep(TestConstants.SLEEP);
      selectIFrameWithEditor(0);
      assertEquals(wordToFind1, getSelectedText());
      // Check buttons enabled
      selectMainFrame();
      checkTextFoundState();

      // Step 6
      selenium.click("scLocator=//IButton[ID=\"ideFindReplaceTextFormFindButton\"]");
      selectIFrameWithEditor(0);
      Thread.sleep(TestConstants.SLEEP);
      assertEquals("Gadget", getSelectedText());
      // Check buttons enabled
      selectMainFrame();
      checkTextFoundState();

      // Step 7
      selenium.click("scLocator=//IButton[ID=\"ideFindReplaceTextFormFindButton\"]");
      Thread.sleep(TestConstants.SLEEP);
      selectIFrameWithEditor(0);
      assertEquals("Gadget", getSelectedText());
      // Check buttons enabled
      selectMainFrame();
      checkTextFoundState();

      // Step 8
      selenium.click("scLocator=//IButton[ID=\"ideFindReplaceTextFormFindButton\"]");
      Thread.sleep(TestConstants.SLEEP);
      selectIFrameWithEditor(0);
      assertEquals("Gadget", getSelectedText());
      selectMainFrame();
      // Check buttons enabled
      checkTextNotFoundState();

      // Step 9
      selenium
         .click("scLocator=//DynamicForm[ID=\"ideFindReplaceTextFormDynamicForm\"]/item[name=ideFindReplaceTextFormCaseSensitiveField]/textbox");
      IDE.editor().clickOnEditor();

      selenium.click("scLocator=//IButton[ID=\"ideFindReplaceTextFormFindButton\"]");
      Thread.sleep(TestConstants.SLEEP);
      selectIFrameWithEditor(0);
      assertEquals(wordToFind1, getSelectedText());
      // Check buttons enabled
      selectMainFrame();
      checkTextFoundState();

      // Step 10
      selenium.click("scLocator=//IButton[ID=\"ideFindReplaceTextFormFindButton\"]");
      Thread.sleep(TestConstants.SLEEP);
      selectIFrameWithEditor(0);
      assertEquals(wordToFind1, getSelectedText());
      selectMainFrame();
      // Check buttons enabled
      checkTextNotFoundState();

      // Step 11
      selenium
         .click("scLocator=//DynamicForm[ID=\"ideFindReplaceTextFormDynamicForm\"]/item[name=ideFindReplaceTextFormFindField]/element");
      selenium
         .focus("scLocator=//DynamicForm[ID=\"ideFindReplaceTextFormDynamicForm\"]/item[name=ideFindReplaceTextFormFindField]/element");

      selenium.keyDownNative("" + java.awt.event.KeyEvent.VK_CONTROL);
      selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_A);
      selenium.keyUpNative("" + java.awt.event.KeyEvent.VK_CONTROL);
      selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_BACK_SPACE);
      Thread.sleep(TestConstants.SLEEP_SHORT);
      checkFindFieldEmptyState();

      // Step 12
      selenium
         .type(
            "scLocator=//DynamicForm[ID=\"ideFindReplaceTextFormDynamicForm\"]/item[name=ideFindReplaceTextFormFindField]/element",
            "");
      selenium
         .typeKeys(
            "scLocator=//DynamicForm[ID=\"ideFindReplaceTextFormDynamicForm\"]/item[name=ideFindReplaceTextFormFindField]/element",
            wordToFind2);
      checkFindFieldNotEmptyState();
      selenium.click("scLocator=//IButton[ID=\"ideFindReplaceTextFormFindButton\"]");
      checkTextNotFoundState();

      // Step 13 Close "Find/Replace" dialog window.
      selenium.click("scLocator=//Window[ID=\"ideFindReplaceForm\"]/closeButton");
      Thread.sleep(TestConstants.SLEEP);
      assertFalse(selenium.isElementPresent("scLocator=//Window[ID=\"ideFindReplaceForm\"]"));
      IDE.toolbar().assertButtonEnabled(ToolbarCommands.Editor.FIND_REPLACE, true);
      IDE.editor().closeTab(0);
   }

   /**
    * IDE-165:Replace text in file
    * 
    * @throws Exception
    */
   @Test
   public void testReplaceTextInFile() throws Exception
   {
      selenium.refresh();
      selenium.waitForPageToLoad(String.valueOf(TestConstants.IDE_LOAD_PERIOD));
      Thread.sleep(TestConstants.SLEEP);
      IDE.navigator().selectItem(WS_URL);
      IDE.menu().runCommand(MenuCommands.File.FILE, MenuCommands.File.REFRESH);
//      Thread.sleep(TestConstants.SLEEP);
      
      IDE.navigator().clickOpenIconOfFolder(WS_URL + TEST_FOLDER + "/");
      Thread.sleep(TestConstants.REDRAW_PERIOD);
      IDE.navigator().openFileFromNavigationTreeWithCodeEditor(FILE_NAME, false);
      Thread.sleep(TestConstants.SLEEP_SHORT);
      // Step 3 Click "Find/Replace" button on toolbar
      IDE.toolbar().assertButtonEnabled(ToolbarCommands.Editor.FIND_REPLACE, true);
      IDE.toolbar().runCommand(ToolbarCommands.Editor.FIND_REPLACE);
      Thread.sleep(TestConstants.SLEEP);
      checkFindReplaceFormAppeared();
      IDE.toolbar().assertButtonEnabled(ToolbarCommands.Editor.FIND_REPLACE, false);

      // Step 4
      // Print "panel" to find field
      selenium
         .typeKeys(
            "scLocator=//DynamicForm[ID=\"ideFindReplaceTextFormDynamicForm\"]/item[name=ideFindReplaceTextFormFindField]/element",
            wordToFind3);
      checkFindFieldNotEmptyState();
      // Check "Case sensitive"
      selenium
         .click("scLocator=//DynamicForm[ID=\"ideFindReplaceTextFormDynamicForm\"]/item[name=ideFindReplaceTextFormCaseSensitiveField]/textbox");

      // Put cursor at the start of file and click "Find" button.
      IDE.editor().clickOnEditor();
      selenium.click("scLocator=//IButton[ID=\"ideFindReplaceTextFormFindButton\"]");
      Thread.sleep(TestConstants.SLEEP);
      selectIFrameWithEditor(0);
      assertEquals(wordToFind3, getSelectedText());
      // Check buttons enabled
      selectMainFrame();
      checkTextFoundState();

      // Step 5 Print "form" to replace field and click replace.
      selenium
         .click("scLocator=//DynamicForm[ID=\"ideFindReplaceTextFormDynamicForm\"]/item[name=ideFindReplaceTextFormReplaceField]/element");
      selenium
         .typeKeys(
            "scLocator=//DynamicForm[ID=\"ideFindReplaceTextFormDynamicForm\"]/item[name=ideFindReplaceTextFormReplaceField]/element",
            wordToReplace);
      selenium.click("scLocator=//IButton[ID=\"ideFindReplaceTextFormReplaceButton\"]");
      Thread.sleep(TestConstants.SLEEP);

      assertFalse(isButtonEnabled("Replace"));
      assertFalse(isButtonEnabled("Replace/Find"));
      // TODO check replaced

      // Step 6 Click find button
      selenium.click("scLocator=//IButton[ID=\"ideFindReplaceTextFormFindButton\"]");
      Thread.sleep(TestConstants.SLEEP);
      selectIFrameWithEditor(0);
      assertEquals(wordToFind3, getSelectedText());
      // Check buttons enabled
      selectMainFrame();
      checkTextFoundState();

      // Step 7 Clear replace field and click replace button
      selenium
         .click("scLocator=//DynamicForm[ID=\"ideFindReplaceTextFormDynamicForm\"]/item[name=ideFindReplaceTextFormReplaceField]/element");
      selenium
         .focus("scLocator=//DynamicForm[ID=\"ideFindReplaceTextFormDynamicForm\"]/item[name=ideFindReplaceTextFormReplaceField]/element");

      selenium.keyDownNative("" + java.awt.event.KeyEvent.VK_CONTROL);
      selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_A);
      selenium.keyUpNative("" + java.awt.event.KeyEvent.VK_CONTROL);
      selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_BACK_SPACE);

      selenium.click("scLocator=//IButton[ID=\"ideFindReplaceTextFormReplaceButton\"]");
      Thread.sleep(TestConstants.SLEEP);

      assertFalse(isButtonEnabled("Replace"));
      assertFalse(isButtonEnabled("Replace/Find"));

      // Step 8
      // Make "Case sensitive" not checked
      selenium
         .click("scLocator=//DynamicForm[ID=\"ideFindReplaceTextFormDynamicForm\"]/item[name=ideFindReplaceTextFormCaseSensitiveField]/textbox");
      // Text in find field is "panel", print "box" in replace field.
      selenium
         .click("scLocator=//DynamicForm[ID=\"ideFindReplaceTextFormDynamicForm\"]/item[name=ideFindReplaceTextFormReplaceField]/element");
      selenium
         .typeKeys(
            "scLocator=//DynamicForm[ID=\"ideFindReplaceTextFormDynamicForm\"]/item[name=ideFindReplaceTextFormReplaceField]/element",
            "box");

      selenium
         .click("scLocator=//DynamicForm[ID=\"ideFindReplaceTextFormDynamicForm\"]/item[name=ideFindReplaceTextFormFindField]/element");
      selenium
         .type(
            "scLocator=//DynamicForm[ID=\"ideFindReplaceTextFormDynamicForm\"]/item[name=ideFindReplaceTextFormFindField]/element",
            "");
      selenium
         .typeKeys(
            "scLocator=//DynamicForm[ID=\"ideFindReplaceTextFormDynamicForm\"]/item[name=ideFindReplaceTextFormFindField]/element",
            "panel");

      // Put cursor at the start of file and click "Find" button.
      IDE.editor().clickOnEditor();
      // Step 9 Click find button
      selenium.click("scLocator=//IButton[ID=\"ideFindReplaceTextFormFindButton\"]");
      Thread.sleep(TestConstants.SLEEP);
      selectIFrameWithEditor(0);
      assertEquals("Panel", getSelectedText());
      // Check buttons enabled
      selectMainFrame();
      checkTextFoundState();

      // Step 10 Click "Replace/Find" button
      selenium.click("scLocator=//IButton[ID=\"ideFindReplaceTextFormReplaceFindButton\"]");
      selectIFrameWithEditor(0);
      assertEquals("Panel", getSelectedText());
      selectMainFrame();
      checkTextFoundState();
      // Step 11 Click "Replace/Find" again
      selenium.click("scLocator=//IButton[ID=\"ideFindReplaceTextFormReplaceFindButton\"]");
      selectIFrameWithEditor(0);
      assertEquals("Panel", getSelectedText());
      selectMainFrame();
      checkTextFoundState();

      // Step 12 Put cursor position at the start of document and make
      // "Case sensitive" not checked, text in find field is "panel", make
      // replace field empty.
      selenium
         .click("scLocator=//DynamicForm[ID=\"ideFindReplaceTextFormDynamicForm\"]/item[name=ideFindReplaceTextFormReplaceField]/element");
      selenium
         .focus("scLocator=//DynamicForm[ID=\"ideFindReplaceTextFormDynamicForm\"]/item[name=ideFindReplaceTextFormReplaceField]/element");

      selenium.keyDownNative("" + java.awt.event.KeyEvent.VK_CONTROL);
      selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_A);
      selenium.keyUpNative("" + java.awt.event.KeyEvent.VK_CONTROL);
      selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_BACK_SPACE);
      // Put cursor at the start of file and click "Find" button.
      IDE.editor().clickOnEditor();
      // Step 13 Click "Replace All" button.
      selenium.click("scLocator=//IButton[ID=\"ideFindReplaceTextFormReplaceAllButton\"]");
      Thread.sleep(TestConstants.SLEEP);

      // Step 14
      IDE.editor().clickOnEditor();
      selenium.click("scLocator=//IButton[ID=\"ideFindReplaceTextFormFindButton\"]");
      Thread.sleep(TestConstants.SLEEP);
      // Check buttons enabled
      checkTextNotFoundState();

      // Step 15 Close "Find/Replace" dialog window.
      selenium.click("scLocator=//Window[ID=\"ideFindReplaceForm\"]/closeButton");
      Thread.sleep(TestConstants.SLEEP);
      
      assertFalse(selenium.isElementPresent("scLocator=//Window[ID=\"ideFindReplaceForm\"]"));
      
      IDE.toolbar().assertButtonEnabled(ToolbarCommands.Editor.FIND_REPLACE, true);
      
      IDE.editor().closeUnsavedFileAndDoNotSave(0);
   }

   /**
    * IDE-166:Find/Replace in few documents
    * @throws Exception 
    */
   @Test
   public void testfindReplaceInFewFiles() throws Exception
   {
      selenium.refresh();
      selenium.waitForPageToLoad(String.valueOf(TestConstants.IDE_LOAD_PERIOD));
      Thread.sleep(TestConstants.SLEEP);
      IDE.navigator().selectItem(WS_URL);
      IDE.menu().runCommand(MenuCommands.File.FILE, MenuCommands.File.REFRESH);
//      Thread.sleep(TestConstants.SLEEP);
      
      IDE.navigator().clickOpenIconOfFolder(WS_URL + TEST_FOLDER + "/");
      Thread.sleep(TestConstants.REDRAW_PERIOD);
      IDE.navigator().openFileFromNavigationTreeWithCodeEditor(FILE_NAME_HTML, false);
      Thread.sleep(TestConstants.EDITOR_OPEN_PERIOD);
      IDE.navigator().openFileFromNavigationTreeWithCodeEditor(FILE_NAME_GROOVY_1, false);
      Thread.sleep(TestConstants.EDITOR_OPEN_PERIOD);
      IDE.navigator().openFileFromNavigationTreeWithCodeEditor(FILE_NAME_GROOVY_2, false);
      Thread.sleep(TestConstants.EDITOR_OPEN_PERIOD);

      //Create first rest file IDE.editor().with content
      IDE.editor().selectTab(0);
      IDE.toolbar().assertButtonEnabled(ToolbarCommands.Editor.FIND_REPLACE, true);
      Thread.sleep(TestConstants.SLEEP_SHORT);
      IDE.toolbar().runCommand(ToolbarCommands.Editor.FIND_REPLACE);
      Thread.sleep(TestConstants.SLEEP);
      checkFindReplaceFormAppeared();
      IDE.toolbar().assertButtonEnabled(ToolbarCommands.Editor.FIND_REPLACE, false);

      //Step 4 Print "html" word in "Find" field, put cursot at start of document and click "Find"
      selenium
         .typeKeys(
            "scLocator=//DynamicForm[ID=\"ideFindReplaceTextFormDynamicForm\"]/item[name=ideFindReplaceTextFormFindField]/element",
            "html");
      checkFindFieldNotEmptyState();
      // Make system mouse click on editor space
      IDE.editor().clickOnEditor();

      selenium.click("scLocator=//IButton[ID=\"ideFindReplaceTextFormFindButton\"]");
      Thread.sleep(TestConstants.SLEEP);
      selectIFrameWithEditor(0);
      Thread.sleep(TestConstants.EDITOR_OPEN_PERIOD);
      assertEquals("html", getSelectedText());
      // Check buttons enabled
      selectMainFrame();
      checkTextFoundState();

      //Step 5 Go to "rest2.groovy" file.
      IDE.editor().selectTab(2);
      //Step 6 Click "Find" button.
      selenium.click("scLocator=//IButton[ID=\"ideFindReplaceTextFormFindButton\"]");
      Thread.sleep(TestConstants.SLEEP);
      selectIFrameWithEditor(2);
      Thread.sleep(TestConstants.EDITOR_OPEN_PERIOD);
      assertEquals("", getSelectedText());
      // Check buttons enabled
      selectMainFrame();
      checkTextNotFoundState();

      //Step 7 Go to "rest.groovy" file.
      IDE.editor().selectTab(1);
      //Step 8 
      //Print "import" in find field
      selenium
         .click("scLocator=//DynamicForm[ID=\"ideFindReplaceTextFormDynamicForm\"]/item[name=ideFindReplaceTextFormFindField]/element");
      selenium
         .type(
            "scLocator=//DynamicForm[ID=\"ideFindReplaceTextFormDynamicForm\"]/item[name=ideFindReplaceTextFormFindField]/element",
            "");
      selenium
         .typeKeys(
            "scLocator=//DynamicForm[ID=\"ideFindReplaceTextFormDynamicForm\"]/item[name=ideFindReplaceTextFormFindField]/element",
            "import");
      //Print "define" in replace field, 
      selenium
         .click("scLocator=//DynamicForm[ID=\"ideFindReplaceTextFormDynamicForm\"]/item[name=ideFindReplaceTextFormReplaceField]/element");
      selenium
         .typeKeys(
            "scLocator=//DynamicForm[ID=\"ideFindReplaceTextFormDynamicForm\"]/item[name=ideFindReplaceTextFormReplaceField]/element",
            "define");
      //Put cursor at start and click on "Find" button
      IDE.editor().clickOnEditor();
      selenium.click("scLocator=//IButton[ID=\"ideFindReplaceTextFormFindButton\"]");
      Thread.sleep(TestConstants.SLEEP);
      selectIFrameWithEditor(1);
      Thread.sleep(TestConstants.EDITOR_OPEN_PERIOD);
      assertEquals("import", getSelectedText());
      // Check buttons enabled
      selectMainFrame();
      checkTextFoundState();

      //Step 9 Go to "rest2.groovy" file.
      IDE.editor().selectTab(2);
      //Step 10 
      //Print "java" into the find field
      selenium
         .click("scLocator=//DynamicForm[ID=\"ideFindReplaceTextFormDynamicForm\"]/item[name=ideFindReplaceTextFormFindField]/element");
      selenium
         .type(
            "scLocator=//DynamicForm[ID=\"ideFindReplaceTextFormDynamicForm\"]/item[name=ideFindReplaceTextFormFindField]/element",
            "");
      selenium
         .typeKeys(
            "scLocator=//DynamicForm[ID=\"ideFindReplaceTextFormDynamicForm\"]/item[name=ideFindReplaceTextFormFindField]/element",
            "java");

      //Put cursor at start and click on "Find" button
      IDE.editor().clickOnEditor();
      selenium.click("scLocator=//IButton[ID=\"ideFindReplaceTextFormFindButton\"]");
      Thread.sleep(TestConstants.SLEEP);
      selectIFrameWithEditor(2);
      Thread.sleep(TestConstants.EDITOR_OPEN_PERIOD);
      assertEquals("java", getSelectedText());
      // Check buttons enabled
      selectMainFrame();
      checkTextFoundState();

      //Step 11 Click "Replace" button
      selenium.click("scLocator=//IButton[ID=\"ideFindReplaceTextFormReplaceButton\"]");
      //TODO check replaced text
      assertFalse(isButtonEnabled("Replace"));
      assertFalse(isButtonEnabled("Replace/Find"));

      //Step 12 Go to "test.html" file
      IDE.editor().selectTab(0);
      //Step 13 Click "Replace/Find"
      selenium.click("scLocator=//IButton[ID=\"ideFindReplaceTextFormReplaceFindButton\"]");
      //TODO check replaced
      Thread.sleep(TestConstants.SLEEP);
      selectIFrameWithEditor(0);
      Thread.sleep(TestConstants.EDITOR_OPEN_PERIOD);
      assertEquals("html", getSelectedText());
      // Check buttons enabled
      selectMainFrame();
      checkTextFoundState();
      //Step 14 Click "Replace/Find" again
      selenium.click("scLocator=//IButton[ID=\"ideFindReplaceTextFormReplaceFindButton\"]");
      //TODO Check replaced
      Thread.sleep(TestConstants.SLEEP);
      // Check buttons enabled
      selectMainFrame();
      checkTextNotFoundState();

      //Step 15 Go to "rest.groovy" file
      IDE.editor().selectTab(1);
      //Step 16 Click "Replace All" button
      selenium.click("scLocator=//IButton[ID=\"ideFindReplaceTextFormReplaceAllButton\"]");
      //TODO check replaced
      // Step 17 Close "Find/Replace" dialog window.
      selenium.click("scLocator=//Window[ID=\"ideFindReplaceForm\"]/closeButton");
      Thread.sleep(TestConstants.SLEEP);
      assertFalse(selenium.isElementPresent("scLocator=//Window[ID=\"ideFindReplaceForm\"]"));
      IDE.toolbar().assertButtonEnabled(ToolbarCommands.Editor.FIND_REPLACE, true);
   }

   /**
    * Check buttons when text is found.
    */
   private void checkTextFoundState()
   {
      assertTrue(isButtonEnabled("Find"));
      assertTrue(isButtonEnabled("Replace All"));
      assertTrue(isButtonEnabled("Replace"));
      assertTrue(isButtonEnabled("Replace/Find"));
      assertTrue(isButtonEnabled("Cancel"));
      assertEquals("", getFindResultText());
   }

   /**
    * Check buttons when text is not found.
    */
   private void checkTextNotFoundState()
   {
      assertTrue(isButtonEnabled("Find"));
      assertTrue(isButtonEnabled("Replace All"));
      assertFalse(isButtonEnabled("Replace"));
      assertFalse(isButtonEnabled("Replace/Find"));
      assertTrue(isButtonEnabled("Cancel"));
      assertEquals(NOT_FOUND, getFindResultText());
   }

   /**
    * Check the buttons state when "Find" field is empty.
    */
   private void checkFindFieldEmptyState()
   {
      assertFalse(isButtonEnabled("Find"));
      assertFalse(isButtonEnabled("Replace All"));
      assertFalse(isButtonEnabled("Replace"));
      assertFalse(isButtonEnabled("Replace/Find"));
      assertTrue(isButtonEnabled("Cancel"));
   }

   /**
    * Check the buttons state when "Find" field is not empty.
    */
   private void checkFindFieldNotEmptyState()
   {
      assertTrue(isButtonEnabled("Find"));
      assertTrue(isButtonEnabled("Replace All"));
      assertFalse(isButtonEnabled("Replace"));
      assertFalse(isButtonEnabled("Replace/Find"));
      assertTrue(isButtonEnabled("Cancel"));
   }

   private void checkFindReplaceFormAppeared()
   {
      assertTrue(selenium.isElementPresent("scLocator=//Window[ID=\"ideFindReplaceForm\"]"));
      assertTrue(selenium.isElementPresent("scLocator=//IButton[ID=\"ideFindReplaceTextFormFindButton\"]"));
      assertTrue(selenium.isElementPresent("scLocator=//IButton[ID=\"ideFindReplaceTextFormReplaceFindButton\"]"));
      assertTrue(selenium.isElementPresent("scLocator=//IButton[ID=\"ideFindReplaceTextFormReplaceAllButton\"]"));
      assertTrue(selenium.isElementPresent("scLocator=//IButton[ID=\"ideFindReplaceTextFormReplaceButton\"]"));
      assertTrue(selenium.isElementPresent("scLocator=//IButton[ID=\"ideFindReplaceTextFormCancelButton\"]"));
      assertTrue(selenium
         .isElementPresent("scLocator=//DynamicForm[ID=\"ideFindReplaceTextFormDynamicForm\"]/item[name=ideFindReplaceTextFormFindField]/element"));
      assertTrue(selenium
         .isElementPresent("scLocator=//DynamicForm[ID=\"ideFindReplaceTextFormDynamicForm\"]/item[name=ideFindReplaceTextFormReplaceField]/element"));
      assertTrue(selenium
         .isElementPresent("scLocator=//DynamicForm[ID=\"ideFindReplaceTextFormDynamicForm\"]/item[name=ideFindReplaceTextFormCaseSensitiveField]/textbox"));
      // Check buttons state
      assertFalse(isButtonEnabled("Find"));
      assertFalse(isButtonEnabled("Replace/Find"));
      assertFalse(isButtonEnabled("Replace"));
      assertFalse(isButtonEnabled("Replace All"));
      assertTrue(isButtonEnabled("Cancel"));
   }

   private boolean isButtonEnabled(String title)
   {
      if (selenium.isElementPresent("//div[@class='windowBody']//td[@class='buttonTitle' and text()='" + title + "']"))
      {
         return true;
      }
      else if (selenium.isElementPresent("//div[@class='windowBody']//td[@class='buttonTitleOver' and text()='" + title
         + "']"))
      {
         return true;
      }
      else if (selenium.isElementPresent("//div[@class='windowBody']//td[@class='buttonTitleDisabled' and text()='"
         + title + "']"))
      {
         return false;
      }
      return false;
   }

   /**
    * Get the find result's text.
    * 
    * @return {@link String} find result text
    */
   private String getFindResultText()
   {
      return selenium.getText("scLocator=//Label[ID=\"ideFindReplaceTextFormFindResult\"]");
   }
  
   @AfterClass
   public static void tearDown()
   {
      try
      {
         VirtualFileSystemUtils.delete(URL + TEST_FOLDER);
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

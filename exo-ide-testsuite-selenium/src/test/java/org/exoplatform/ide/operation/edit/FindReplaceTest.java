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

   private final static String GROOVY_FILE_CONTENT = "// simple groovy script\n" + "import javax.ws.rs.Path\n"
      + "import javax.ws.rs.GET\n" + "import javax.ws.rs.PathParam\n" + "@Path(\"/\")\n"
      + "public class HelloWorld {\n" + "@GET\n" + "@Path(\"helloworld/{name}\")\n"
      + "public String hello(@PathParam(\"name\") String name) {\n" + "return \"Hello \" + name\n" + "}\n" + "}";

   private final static String HTML_FILE_CONTENT = "<html>\n" + "<head>\n" + "<title></title>\n" + "</head>\n"
      + "<body>\n" + "</body>\n" + "</html>";

   private final String wordToFind1 = "gadget";

   private final String wordToFind2 = "apple";

   private final String wordToFind3 = "panel";

   private final String wordToReplace = "form";

   private final static String URL = BASE_URL + REST_CONTEXT + "/" + WEBDAV_CONTEXT + "/" + REPO_NAME + "/" + WS_NAME
      + "/";

   @BeforeClass
   public static void setUp()
   {
      try
      {
         VirtualFileSystemUtils.mkcol(URL + TEST_FOLDER);
         VirtualFileSystemUtils.put(FILE_CONTENT_FOR_FIND.getBytes(), MimeType.TEXT_PLAIN, URL + TEST_FOLDER + "/"
            + FILE_NAME_TXT);
         VirtualFileSystemUtils.put(FILE_CONTENT_FOR_REPLACE.getBytes(), MimeType.TEXT_PLAIN, URL + TEST_FOLDER + "/"
            + FILE_NAME);
         VirtualFileSystemUtils.put(GROOVY_FILE_CONTENT.getBytes(), MimeType.APPLICATION_GROOVY, URL + TEST_FOLDER
            + "/" + FILE_NAME_GROOVY_1);
         VirtualFileSystemUtils.put(GROOVY_FILE_CONTENT.getBytes(), MimeType.APPLICATION_GROOVY, URL + TEST_FOLDER
            + "/" + FILE_NAME_GROOVY_2);
         VirtualFileSystemUtils.put(HTML_FILE_CONTENT.getBytes(), MimeType.TEXT_HTML, URL + TEST_FOLDER + "/"
            + FILE_NAME_HTML);
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
      IDE.WORKSPACE.waitForRootItem();
      IDE.WORKSPACE.selectRootItem();
      IDE.MENU.runCommand(MenuCommands.File.FILE, MenuCommands.File.REFRESH);
      IDE.WORKSPACE.waitForItem(WS_URL + TEST_FOLDER + "/");
      IDE.WORKSPACE.clickOpenIconOfFolder(WS_URL + TEST_FOLDER + "/");

      //Open file
      IDE.WORKSPACE.waitForItem(WS_URL + TEST_FOLDER + "/" + FILE_NAME_TXT);
      IDE.NAVIGATION.openFileFromNavigationTreeWithCodeEditor(WS_URL + TEST_FOLDER + "/" + FILE_NAME_TXT, false);
      IDE.EDITOR.waitTabPresent(0);
      IDE.TOOLBAR.assertButtonEnabled(ToolbarCommands.Editor.FIND_REPLACE, true);
      IDE.TOOLBAR.runCommand(ToolbarCommands.Editor.FIND_REPLACE);

      IDE.FINDREPLACE.waitForFindReplaceViewOpened();
      IDE.FINDREPLACE.checkFindReplaceFormAppeared();
      IDE.TOOLBAR.assertButtonEnabled(ToolbarCommands.Editor.FIND_REPLACE, false);

      // Type what to find
      IDE.FINDREPLACE.typeInFindField(wordToFind1);
      IDE.FINDREPLACE.checkFindFieldNotEmptyState();

      // Step 5. Put cursor at the start of the document and click "Find"
      // button.
      selenium.fireEvent("ideFindReplaceTextFormFindField", "blur");
      selenium.fireEvent("//", "focus");

      // Make system mouse click on editor space
      IDE.EDITOR.clickOnEditor();
      IDE.FINDREPLACE.clickFindButton();
      IDE.EDITOR.selectIFrameWithEditor(0);
      assertEquals(wordToFind1, getSelectedText());
      // Check buttons enabled
      IDE.selectMainFrame();

      IDE.FINDREPLACE.checkStateWhenTextFound();

      // Step 6
      IDE.FINDREPLACE.clickFindButton();
      IDE.EDITOR.selectIFrameWithEditor(0);
      assertEquals("Gadget", getSelectedText());
      // Check buttons enabled
      IDE.selectMainFrame();
      IDE.FINDREPLACE.checkStateWhenTextFound();

      // Step 7
      IDE.FINDREPLACE.clickFindButton();
      IDE.EDITOR.selectIFrameWithEditor(0);
      assertEquals("Gadget", getSelectedText());
      // Check buttons enabled
      IDE.selectMainFrame();
      IDE.FINDREPLACE.checkStateWhenTextFound();

      // Step 8
      IDE.FINDREPLACE.clickFindButton();
      IDE.EDITOR.selectIFrameWithEditor(0);
      assertEquals("Gadget", getSelectedText());
      IDE.selectMainFrame();

      // Check buttons enabled
      IDE.FINDREPLACE.checkStateWhenTextNotFound();

      // Step 9
      IDE.FINDREPLACE.clickCaseSensitiveField();
      IDE.EDITOR.clickOnEditor();

      IDE.FINDREPLACE.clickFindButton();

      IDE.EDITOR.selectIFrameWithEditor(0);
      assertEquals(wordToFind1, getSelectedText());
      // Check buttons enabled
      IDE.selectMainFrame();
      IDE.FINDREPLACE.checkStateWhenTextFound();

      // Step 10
      IDE.FINDREPLACE.clickFindButton();
      IDE.EDITOR.selectIFrameWithEditor(0);
      assertEquals(wordToFind1, getSelectedText());
      IDE.selectMainFrame();
      // Check buttons enabled
      IDE.FINDREPLACE.checkStateWhenTextNotFound();

      // Step 11
      IDE.FINDREPLACE.typeInFindField("");
      IDE.FINDREPLACE.checkFindFieldEmptyState();

      // Step 12
      IDE.FINDREPLACE.typeInFindField(wordToFind2);
      IDE.FINDREPLACE.checkFindFieldNotEmptyState();
      IDE.FINDREPLACE.clickFindButton();
      IDE.FINDREPLACE.checkStateWhenTextNotFound();

      // Step 13 Close "Find/Replace" dialog window.
      IDE.FINDREPLACE.clickCancelButton();
      IDE.FINDREPLACE.waitForFindReplaceViewClosed();
      IDE.TOOLBAR.assertButtonEnabled(ToolbarCommands.Editor.FIND_REPLACE, true);
      IDE.EDITOR.closeFile(0);
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
      IDE.WORKSPACE.waitForRootItem();
      IDE.WORKSPACE.selectItem(WS_URL);
      IDE.MENU.runCommand(MenuCommands.File.FILE, MenuCommands.File.REFRESH);
      IDE.WORKSPACE.waitForItem(WS_URL + TEST_FOLDER + "/");

      IDE.WORKSPACE.clickOpenIconOfFolder(WS_URL + TEST_FOLDER + "/");
      IDE.WORKSPACE.waitForItem(WS_URL + TEST_FOLDER + "/" + FILE_NAME);
      IDE.NAVIGATION.openFileFromNavigationTreeWithCodeEditor(WS_URL + TEST_FOLDER + "/" + FILE_NAME, false);
      IDE.EDITOR.waitTabPresent(0);
      // Step 3 Click "Find/Replace" button on toolbar
      IDE.TOOLBAR.assertButtonEnabled(ToolbarCommands.Editor.FIND_REPLACE, true);
      IDE.TOOLBAR.runCommand(ToolbarCommands.Editor.FIND_REPLACE);

      IDE.FINDREPLACE.waitForFindReplaceViewOpened();
      IDE.FINDREPLACE.checkFindReplaceFormAppeared();
      IDE.TOOLBAR.assertButtonEnabled(ToolbarCommands.Editor.FIND_REPLACE, false);

      // Step 4
      // Print "panel" to find field
      IDE.FINDREPLACE.typeInFindField(wordToFind3);
      IDE.FINDREPLACE.checkFindFieldNotEmptyState();
      // Check "Case sensitive"
      IDE.FINDREPLACE.clickCaseSensitiveField();
      // Put cursor at the start of file and click "Find" button.
      IDE.EDITOR.clickOnEditor();
      IDE.FINDREPLACE.clickFindButton();
      IDE.EDITOR.selectIFrameWithEditor(0);
      assertEquals(wordToFind3, getSelectedText());
      // Check buttons enabled
      IDE.selectMainFrame();
      IDE.FINDREPLACE.checkStateWhenTextFound();

      // Step 5 Print "form" to replace field and click replace.
      IDE.FINDREPLACE.typeInReplaceField(wordToReplace);
      IDE.FINDREPLACE.clickReplaceButton();

      assertFalse(IDE.FINDREPLACE.isReplaceButtonEnabled());
      assertFalse(IDE.FINDREPLACE.isReplaceFindButtonEnabled());
      String content = IDE.EDITOR.getTextFromCodeEditor(0);
      System.out.println("FindReplaceTest.testReplaceTextInFile()" + content);
      assertTrue(content.contains(wordToReplace));

      // Step 6 Click find button
      IDE.FINDREPLACE.clickFindButton();
      IDE.EDITOR.selectIFrameWithEditor(0);
      assertEquals(wordToFind3, getSelectedText());
      // Check buttons enabled
      IDE.selectMainFrame();
      IDE.FINDREPLACE.checkStateWhenTextFound();

      // Step 7 Clear replace field and click replace button
      IDE.FINDREPLACE.typeInReplaceField("");
      IDE.FINDREPLACE.clickReplaceButton();
      assertFalse(IDE.FINDREPLACE.isReplaceButtonEnabled());
      assertFalse(IDE.FINDREPLACE.isReplaceFindButtonEnabled());

      // Step 8
      // Make "Case sensitive" not checked
      IDE.FINDREPLACE.clickCaseSensitiveField();
      // Text in find field is "panel", print "box" in replace field.
      IDE.FINDREPLACE.typeInReplaceField("box");

      IDE.FINDREPLACE.typeInFindField("panel");

      // Put cursor at the start of file and click "Find" button.
      IDE.EDITOR.clickOnEditor();
      // Step 9 Click find button
      IDE.FINDREPLACE.clickFindButton();
      IDE.EDITOR.selectIFrameWithEditor(0);
      assertEquals("Panel", getSelectedText());
      // Check buttons enabled
      IDE.selectMainFrame();
      IDE.FINDREPLACE.checkStateWhenTextFound();

      // Step 10 Click "Replace/Find" button
      IDE.FINDREPLACE.clickReplaceFindButton();
      IDE.EDITOR.selectIFrameWithEditor(0);
      assertEquals("Panel", getSelectedText());
      IDE.selectMainFrame();
      IDE.FINDREPLACE.checkStateWhenTextFound();
      // Step 11 Click "Replace/Find" again
      IDE.FINDREPLACE.clickReplaceFindButton();
      IDE.EDITOR.selectIFrameWithEditor(0);
      assertEquals("Panel", getSelectedText());
      IDE.selectMainFrame();
      IDE.FINDREPLACE.checkStateWhenTextFound();

      // Step 12 Put cursor position at the start of document and make
      // "Case sensitive" not checked, text in find field is "panel", make
      // replace field empty.
      IDE.FINDREPLACE.typeInReplaceField("");

      // Put cursor at the start of file and click "Find" button.
      IDE.EDITOR.clickOnEditor();
      // Step 13 Click "Replace All" button.
      IDE.FINDREPLACE.clickReplaceAllButton();

      // Step 14
      IDE.EDITOR.clickOnEditor();
      IDE.FINDREPLACE.clickFindButton();
      // Check buttons enabled
      IDE.FINDREPLACE.checkStateWhenTextNotFound();

      // Step 15 Close "Find/Replace" dialog window.
      IDE.FINDREPLACE.clickCancelButton();
      IDE.FINDREPLACE.waitForFindReplaceViewClosed();

      IDE.TOOLBAR.assertButtonEnabled(ToolbarCommands.Editor.FIND_REPLACE, true);

      IDE.EDITOR.closeTabIgnoringChanges(0);
   }

   /**
    * IDE-166:Find/Replace in few documents
    * @throws Exception 
    */
   @Test
   public void testfindReplaceInFewFiles() throws Exception
   {
      selenium.refresh();
      IDE.WORKSPACE.waitForRootItem();
      IDE.WORKSPACE.selectItem(WS_URL);
      IDE.MENU.runCommand(MenuCommands.File.FILE, MenuCommands.File.REFRESH);

      IDE.WORKSPACE.waitForItem(WS_URL + TEST_FOLDER + "/");
      IDE.WORKSPACE.clickOpenIconOfFolder(WS_URL + TEST_FOLDER + "/");

      IDE.WORKSPACE.waitForItem(WS_URL + TEST_FOLDER + "/" + FILE_NAME_HTML);
      IDE.NAVIGATION.openFileFromNavigationTreeWithCodeEditor(WS_URL + TEST_FOLDER + "/" + FILE_NAME_HTML, false);
      IDE.EDITOR.waitTabPresent(0);
      IDE.NAVIGATION.openFileFromNavigationTreeWithCodeEditor(WS_URL + TEST_FOLDER + "/" + FILE_NAME_GROOVY_1, false);
      IDE.EDITOR.waitTabPresent(1);
      IDE.NAVIGATION.openFileFromNavigationTreeWithCodeEditor(WS_URL + TEST_FOLDER + "/" + FILE_NAME_GROOVY_2, false);
      IDE.EDITOR.waitTabPresent(2);

      //Create first rest fileIDE.EDITOR.with content
      IDE.EDITOR.selectTab(0);
      IDE.TOOLBAR.assertButtonEnabled(ToolbarCommands.Editor.FIND_REPLACE, true);
      IDE.TOOLBAR.runCommand(ToolbarCommands.Editor.FIND_REPLACE);
      IDE.FINDREPLACE.waitForFindReplaceViewOpened();
      IDE.FINDREPLACE.checkFindReplaceFormAppeared();

      IDE.TOOLBAR.assertButtonEnabled(ToolbarCommands.Editor.FIND_REPLACE, false);

      //Step 4 Print "html" word in "Find" field, put cursot at start of document and click "Find"
      IDE.FINDREPLACE.typeInFindField("html");
      IDE.FINDREPLACE.checkFindFieldNotEmptyState();
      // Make system mouse click on editor space
      IDE.EDITOR.clickOnEditor();

      IDE.FINDREPLACE.clickFindButton();
      IDE.EDITOR.selectIFrameWithEditor(0);
      assertEquals("html", getSelectedText());
      // Check buttons enabled
      IDE.selectMainFrame();
      IDE.FINDREPLACE.checkStateWhenTextFound();

      //Step 5 Go to "rest2.groovy" file.
      IDE.EDITOR.selectTab(2);
      //Step 6 Click "Find" button.
      IDE.FINDREPLACE.clickFindButton();
      IDE.EDITOR.selectIFrameWithEditor(2);
      assertEquals("", getSelectedText());
      // Check buttons enabled
      IDE.selectMainFrame();
      IDE.FINDREPLACE.checkStateWhenTextNotFound();

      //Step 7 Go to "rest.groovy" file.
      IDE.EDITOR.selectTab(1);
      //Step 8 
      //Print "import" in find field
      IDE.FINDREPLACE.typeInFindField("import");
      //Print "define" in replace field, 
      IDE.FINDREPLACE.typeInReplaceField("define");
      //Put cursor at start and click on "Find" button
      IDE.EDITOR.clickOnEditor();
      IDE.FINDREPLACE.clickFindButton();
      IDE.EDITOR.selectIFrameWithEditor(1);
      assertEquals("import", getSelectedText());
      // Check buttons enabled
      IDE.selectMainFrame();
      IDE.FINDREPLACE.checkStateWhenTextFound();

      //Step 9 Go to "rest2.groovy" file.
      IDE.EDITOR.selectTab(2);
      //Step 10 
      //Print "java" into the find field
      IDE.FINDREPLACE.typeInFindField("java");

      //Put cursor at start and click on "Find" button
      IDE.EDITOR.clickOnEditor();
      IDE.FINDREPLACE.clickFindButton();
      IDE.EDITOR.selectIFrameWithEditor(2);
      assertEquals("java", getSelectedText());
      // Check buttons enabled
      IDE.selectMainFrame();
      IDE.FINDREPLACE.checkStateWhenTextFound();

      //Step 11 Click "Replace" button
      IDE.FINDREPLACE.clickReplaceButton();
      //TODO check replaced text
      assertFalse(IDE.FINDREPLACE.isReplaceButtonEnabled());
      assertFalse(IDE.FINDREPLACE.isReplaceFindButtonEnabled());

      //Step 12 Go to "test.html" file
      IDE.EDITOR.selectTab(0);
      //Step 13 Click "Replace/Find"
      IDE.FINDREPLACE.clickReplaceFindButton();
      //TODO check replaced
      IDE.EDITOR.selectIFrameWithEditor(0);
      assertEquals("html", getSelectedText());
      // Check buttons enabled
      IDE.selectMainFrame();
      IDE.FINDREPLACE.checkStateWhenTextFound();
      //Step 14 Click "Replace/Find" again
      IDE.FINDREPLACE.clickReplaceFindButton();
      //TODO Check replaced
      // Check buttons enabled
      IDE.selectMainFrame();
      IDE.FINDREPLACE.checkStateWhenTextFound();

      //Step 15 Go to "rest.groovy" file
      IDE.EDITOR.selectTab(1);
      //Step 16 Click "Replace All" button
      IDE.FINDREPLACE.clickReplaceAllButton();
      //TODO check replaced
      // Step 17 Close "Find/Replace" dialog window.
      IDE.FINDREPLACE.clickCancelButton();
      IDE.FINDREPLACE.waitForFindReplaceViewClosed();
      IDE.TOOLBAR.assertButtonEnabled(ToolbarCommands.Editor.FIND_REPLACE, true);
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

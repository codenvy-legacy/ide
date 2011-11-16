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

import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.ToolbarCommands;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.exoplatform.ide.vfs.shared.Link;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.Keys;

import java.io.IOException;
import java.util.Map;

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

   private final static String PROJECT = FindReplaceTest.class.getSimpleName();

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

   @BeforeClass
   public static void setUp()
   {
      try
      {
         Map<String, Link> project = VirtualFileSystemUtils.createDefaultProject(PROJECT);
         Link link = project.get(Link.REL_CREATE_FILE);
         VirtualFileSystemUtils.createFile(link, FILE_NAME_TXT, MimeType.TEXT_PLAIN, FILE_CONTENT_FOR_FIND);
         VirtualFileSystemUtils.createFile(link, FILE_NAME, MimeType.TEXT_PLAIN, FILE_CONTENT_FOR_REPLACE);
         VirtualFileSystemUtils.createFile(link, FILE_NAME_GROOVY_1, MimeType.APPLICATION_GROOVY, GROOVY_FILE_CONTENT);
         VirtualFileSystemUtils.createFile(link, FILE_NAME_GROOVY_2, MimeType.APPLICATION_GROOVY, GROOVY_FILE_CONTENT);
         VirtualFileSystemUtils.createFile(link, FILE_NAME_HTML, MimeType.TEXT_HTML, HTML_FILE_CONTENT);
      }
      catch (IOException e)
      {
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
      IDE.PROJECT.EXPLORER.waitOpened();
      IDE.PROJECT.OPEN.openProject(PROJECT);
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + FILE_NAME_TXT);

      //Open file
      IDE.PROJECT.EXPLORER.openItem(PROJECT + "/" + FILE_NAME_TXT);
      IDE.EDITOR.waitActiveFile(PROJECT + "/" + FILE_NAME_TXT);
      assertTrue(IDE.TOOLBAR.isButtonEnabled(ToolbarCommands.Editor.FIND_REPLACE));

      //Open Find/Replace view:
      IDE.TOOLBAR.runCommand(ToolbarCommands.Editor.FIND_REPLACE);
      IDE.FINDREPLACE.waitOpened();
      IDE.FINDREPLACE.checkFindReplaceFormAppeared();
      assertFalse(IDE.TOOLBAR.isButtonEnabled(ToolbarCommands.Editor.FIND_REPLACE));

      // Type what to find
      IDE.FINDREPLACE.typeInFindField(wordToFind1);
      IDE.FINDREPLACE.checkFindFieldNotEmptyState();

      // Step 5. Put cursor at the start of the document and click "Find"
      // button.
      IDE.EDITOR.typeTextIntoEditor(0, Keys.HOME.toString());
      IDE.FINDREPLACE.clickFindButton();
      assertEquals(wordToFind1, IDE.EDITOR.getSelectedText(0));

      // Check buttons enabled
      IDE.FINDREPLACE.checkStateWhenTextFound();

      // Step 6
      IDE.FINDREPLACE.clickFindButton();
      assertEquals("Gadget", IDE.EDITOR.getSelectedText(0));

      // Check buttons enabled
      IDE.FINDREPLACE.checkStateWhenTextFound();

      // Step 7
      IDE.FINDREPLACE.clickFindButton();
      assertEquals("Gadget", IDE.EDITOR.getSelectedText(0));

      // Check buttons enabled
      IDE.FINDREPLACE.checkStateWhenTextFound();

      // Step 8
      IDE.FINDREPLACE.clickFindButton();
      assertEquals("Gadget", IDE.EDITOR.getSelectedText(0));

      // Check buttons enabled
      IDE.FINDREPLACE.checkStateWhenTextNotFound();

      // Step 9
      IDE.FINDREPLACE.clickCaseSensitiveField();
      IDE.EDITOR.typeTextIntoEditor(0, Keys.HOME.toString() + Keys.PAGE_UP);

      IDE.FINDREPLACE.clickFindButton();

      assertEquals(wordToFind1, IDE.EDITOR.getSelectedText(0));
      // Check buttons enabled
      IDE.FINDREPLACE.checkStateWhenTextFound();

      // Step 10
      IDE.FINDREPLACE.clickFindButton();
      assertEquals(wordToFind1, IDE.EDITOR.getSelectedText(0));
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
      IDE.FINDREPLACE.waitClosed();
      assertTrue(IDE.TOOLBAR.isButtonEnabled(ToolbarCommands.Editor.FIND_REPLACE));
      IDE.EDITOR.closeFile(1);
   }

   @Test
   public void testReplaceTextInFile() throws Exception
   {
      driver.navigate().refresh();
      IDE.PROJECT.EXPLORER.waitOpened();
      IDE.PROJECT.OPEN.openProject(PROJECT);
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + FILE_NAME);

      //Open file
      IDE.PROJECT.EXPLORER.openItem(PROJECT + "/" + FILE_NAME);
      IDE.EDITOR.waitActiveFile(PROJECT + "/" + FILE_NAME);
      assertTrue(IDE.TOOLBAR.isButtonEnabled(ToolbarCommands.Editor.FIND_REPLACE));

      // Step 3 Click "Find/Replace" button on toolbar
      IDE.TOOLBAR.runCommand(ToolbarCommands.Editor.FIND_REPLACE);

      IDE.FINDREPLACE.waitOpened();
      IDE.FINDREPLACE.checkFindReplaceFormAppeared();
      assertFalse(IDE.TOOLBAR.isButtonEnabled(ToolbarCommands.Editor.FIND_REPLACE));

      // Step 4
      // Print "panel" to find field
      IDE.FINDREPLACE.typeInFindField(wordToFind3);
      IDE.FINDREPLACE.checkFindFieldNotEmptyState();

      // Check "Case sensitive"
      IDE.FINDREPLACE.clickCaseSensitiveField();

      // Put cursor at the start of file and click "Find" button.
      IDE.EDITOR.typeTextIntoEditor(0, Keys.HOME.toString() + Keys.PAGE_UP);
      IDE.FINDREPLACE.clickFindButton();
      assertEquals(wordToFind3, IDE.EDITOR.getSelectedText(0));
      // Check buttons enabled
      IDE.FINDREPLACE.checkStateWhenTextFound();

      // Step 5 Print "form" to replace field and click replace.
      IDE.FINDREPLACE.typeInReplaceField(wordToReplace);
      IDE.FINDREPLACE.clickReplaceButton();

      assertFalse(IDE.FINDREPLACE.isReplaceButtonEnabled());
      assertFalse(IDE.FINDREPLACE.isReplaceFindButtonEnabled());
      String content = IDE.EDITOR.getTextFromCodeEditor(0);
      assertTrue(content.contains(wordToReplace));

      // Step 6 Click find button
      IDE.FINDREPLACE.clickFindButton();
      assertEquals(wordToFind3, IDE.EDITOR.getSelectedText(0));
      // Check buttons enabled
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
      IDE.EDITOR.typeTextIntoEditor(0, Keys.HOME.toString() + Keys.PAGE_UP);

      // Step 9 Click find button
      IDE.FINDREPLACE.clickFindButton();
      assertEquals("Panel", IDE.EDITOR.getSelectedText(0));

      // Check buttons enabled
      IDE.FINDREPLACE.checkStateWhenTextFound();

      // Step 10 Click "Replace/Find" button
      IDE.FINDREPLACE.clickReplaceFindButton();
      assertEquals("Panel", IDE.EDITOR.getSelectedText(0));
      IDE.FINDREPLACE.checkStateWhenTextFound();

      // Step 11 Click "Replace/Find" again
      IDE.FINDREPLACE.clickReplaceFindButton();
      assertEquals("Panel", IDE.EDITOR.getSelectedText(0));
      IDE.FINDREPLACE.checkStateWhenTextFound();

      // Step 12 Put cursor position at the start of document and make
      // "Case sensitive" not checked, text in find field is "panel", make
      // replace field empty.
      IDE.FINDREPLACE.typeInReplaceField("");

      // Put cursor at the start of file and click "Find" button.
      IDE.EDITOR.typeTextIntoEditor(0, Keys.HOME.toString() + Keys.PAGE_UP);

      // Step 13 Click "Replace All" button.
      IDE.FINDREPLACE.clickReplaceAllButton();

      // Step 14
      IDE.EDITOR.typeTextIntoEditor(0, Keys.HOME.toString() + Keys.PAGE_UP);
      IDE.FINDREPLACE.clickFindButton();
      // Check buttons enabled
      IDE.FINDREPLACE.checkStateWhenTextNotFound();

      // Step 15 Close "Find/Replace" dialog window.
      IDE.FINDREPLACE.clickCancelButton();
      IDE.FINDREPLACE.waitClosed();

      assertTrue(IDE.TOOLBAR.isButtonEnabled(ToolbarCommands.Editor.FIND_REPLACE));

      IDE.EDITOR.closeTabIgnoringChanges(1);
   }

   @Test
   public void testfindReplaceInFewFiles() throws Exception
   {
      driver.navigate().refresh();
      IDE.PROJECT.EXPLORER.waitOpened();
      IDE.PROJECT.OPEN.openProject(PROJECT);
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + FILE_NAME_HTML);

      //Open files
      IDE.PROJECT.EXPLORER.openItem(PROJECT + "/" + FILE_NAME_HTML);
      IDE.EDITOR.waitActiveFile(PROJECT + "/" + FILE_NAME_HTML);

      IDE.PROJECT.EXPLORER.openItem(PROJECT + "/" + FILE_NAME_GROOVY_1);
      IDE.EDITOR.waitActiveFile(PROJECT + "/" + FILE_NAME_GROOVY_1);

      IDE.PROJECT.EXPLORER.openItem(PROJECT + "/" + FILE_NAME_GROOVY_2);
      IDE.EDITOR.waitActiveFile(PROJECT + "/" + FILE_NAME_GROOVY_2);

      IDE.EDITOR.selectTab(1);
      assertTrue(IDE.TOOLBAR.isButtonEnabled(ToolbarCommands.Editor.FIND_REPLACE));

      IDE.TOOLBAR.runCommand(ToolbarCommands.Editor.FIND_REPLACE);
      IDE.FINDREPLACE.waitOpened();
      IDE.FINDREPLACE.checkFindReplaceFormAppeared();
      assertFalse(IDE.TOOLBAR.isButtonEnabled(ToolbarCommands.Editor.FIND_REPLACE));

      //Step 4 Print "html" word in "Find" field, put cursot at start of document and click "Find"
      IDE.FINDREPLACE.typeInFindField("html");
      IDE.FINDREPLACE.checkFindFieldNotEmptyState();
      // Make system mouse click on editor space
      IDE.EDITOR.typeTextIntoEditor(0, Keys.HOME.toString() + Keys.PAGE_UP);

      IDE.FINDREPLACE.clickFindButton();
      assertEquals("html", IDE.EDITOR.getSelectedText(0));
      // Check buttons enabled
      IDE.FINDREPLACE.checkStateWhenTextFound();

      //Step 5 Go to "rest2.groovy" file.
      IDE.EDITOR.selectTab(3);
      //Step 6 Click "Find" button.
      IDE.FINDREPLACE.clickFindButton();
      assertEquals("", IDE.EDITOR.getSelectedText(1));
      // Check buttons enabled
      IDE.FINDREPLACE.checkStateWhenTextNotFound();

      //Step 7 Go to "rest.groovy" file.
      IDE.EDITOR.selectTab(2);
      //Step 8 
      //Print "import" in find field
      IDE.FINDREPLACE.typeInFindField("import");
      //Print "define" in replace field, 
      IDE.FINDREPLACE.typeInReplaceField("define");
      //Put cursor at start and click on "Find" button
      IDE.EDITOR.typeTextIntoEditor(1, Keys.HOME.toString() + Keys.PAGE_UP);
      IDE.FINDREPLACE.clickFindButton();
      assertEquals("import", IDE.EDITOR.getSelectedText(1));
      // Check buttons enabled
      IDE.FINDREPLACE.checkStateWhenTextFound();

      //Step 9 Go to "rest2.groovy" file.
      IDE.EDITOR.selectTab(3);
      //Step 10 
      //Print "java" into the find field
      IDE.FINDREPLACE.typeInFindField("java");

      //Put cursor at start and click on "Find" button
      IDE.EDITOR.typeTextIntoEditor(1, Keys.HOME.toString() + Keys.PAGE_UP);
      IDE.FINDREPLACE.clickFindButton();
      assertEquals("java", IDE.EDITOR.getSelectedText(1));
      // Check buttons enabled
      IDE.FINDREPLACE.checkStateWhenTextFound();

      //Step 11 Click "Replace" button
      IDE.FINDREPLACE.clickReplaceButton();
      //TODO check replaced text
      assertFalse(IDE.FINDREPLACE.isReplaceButtonEnabled());
      assertFalse(IDE.FINDREPLACE.isReplaceFindButtonEnabled());

      //Step 12 Go to "test.html" file
      IDE.EDITOR.selectTab(1);
      //Step 13 Click "Replace/Find"
      IDE.FINDREPLACE.clickReplaceFindButton();
      //TODO check replaced
      assertEquals("html", IDE.EDITOR.getSelectedText(0));
      // Check buttons enabled
      IDE.FINDREPLACE.checkStateWhenTextFound();
      //Step 14 Click "Replace/Find" again
      IDE.FINDREPLACE.clickReplaceFindButton();
      //TODO Check replaced
      // Check buttons enabled
      IDE.FINDREPLACE.checkStateWhenTextFound();

      //Step 15 Go to "rest.groovy" file
      IDE.EDITOR.selectTab(2);
      //Step 16 Click "Replace All" button
      IDE.FINDREPLACE.clickReplaceAllButton();
      //TODO check replaced
      // Step 17 Close "Find/Replace" dialog window.
      IDE.FINDREPLACE.clickCancelButton();
      IDE.FINDREPLACE.waitClosed();
      assertTrue(IDE.TOOLBAR.isButtonEnabled(ToolbarCommands.Editor.FIND_REPLACE));
   }

   @AfterClass
   public static void afterTest()
   {
      try
      {
         VirtualFileSystemUtils.delete(WS_URL + PROJECT);
      }
      catch (IOException e)
      {
      }
   }
}

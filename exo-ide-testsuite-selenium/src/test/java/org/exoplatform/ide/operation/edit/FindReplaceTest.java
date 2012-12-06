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
import org.junit.After;
import org.junit.Before;
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

   @Before
   public void beforeTest()
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

   @Test
   public void testfindReplaceInFewFiles() throws Exception
   {
      driver.navigate().refresh();
      IDE.LOADER.waitClosed();
      IDE.PROJECT.EXPLORER.waitOpened();
      IDE.PROJECT.OPEN.openProject(PROJECT);
      IDE.LOADER.waitClosed();
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + FILE_NAME_HTML);

      // Open files
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
      assertFalse(IDE.FINDREPLACE.isReplaceButtonEnabled());
      assertFalse(IDE.FINDREPLACE.isReplaceFindButtonEnabled());
      assertFalse(IDE.FINDREPLACE.isFindButtonEnabled());
      assertFalse(IDE.FINDREPLACE.isReplaceAllButtonEnabled());
      assertFalse(IDE.TOOLBAR.isButtonEnabled(ToolbarCommands.Editor.FIND_REPLACE));

      // Step 4 Print "html" word in "Find" field, put cursot at start of document and click "Find"
      IDE.FINDREPLACE.typeInFindField("html");
      assertTrue(IDE.FINDREPLACE.isFindFieldNotEmptyState());
      // Make system mouse click on editor space
      IDE.EDITOR.typeTextIntoEditor(0, Keys.HOME.toString() + Keys.PAGE_UP);

      IDE.FINDREPLACE.clickFindButton();
      assertEquals("html", IDE.EDITOR.getSelectedText(0));
      // Check buttons enabled
      assertTrue(IDE.FINDREPLACE.isStateWhenTextFound());
   }

   @After
   public void afterTest()
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

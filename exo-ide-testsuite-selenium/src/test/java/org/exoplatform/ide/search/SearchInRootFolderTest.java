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
package org.exoplatform.ide.search;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.MenuCommands;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Created by The eXo Platform SAS.
 * 
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: ${date} ${time}
 * 
 */
public class SearchInRootFolderTest extends BaseTest
{
   private static final String PROJECT = SearchInRootFolderTest.class.getSimpleName();

   private static final String folder1Name = "Users";

   private static final String folder2Name = "Test";

   private final String restFileName = "Example.groovy";

   private final String restFileMimeType = MimeType.TEXT_PLAIN;

   private final String copyofRestFileName = "Copy Of Example.groovy";

   private final String restFileContent = "// simple groovy script\n" + "import javax.ws.rs.Path\n"
      + "import javax.ws.rs.GET\n" + "import javax.ws.rs.PathParam\n" + "@Path(\"/\")\n"
      + "public class HelloWorld {\n" + "@GET\n" + "@Path(\"helloworld/{name}\")\n"
      + "public String hello(@PathParam(\"name\") String name) {\n" + "return \"Hello \" + name\n" + "}\n" + "}\n";

   @BeforeClass
   public static void setUp()
   {
      try
      {
         VirtualFileSystemUtils.createDefaultProject(PROJECT);
      }
      catch (Exception e)
      {
      }
   }

   /**
    * IDE-31:Searching file from root folder test.
    * 
    * @throws Exception
    */
   @Test
   public void testSearchInRootFolder() throws Exception
   {
      IDE.PROJECT.EXPLORER.waitOpened();
      IDE.LOADER.waitClosed();
      IDE.PROJECT.OPEN.openProject(PROJECT);
      IDE.LOADER.waitClosed();
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT);

      IDE.FOLDER.createFolder(folder1Name);
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + folder1Name);

      // Create and save
      IDE.TOOLBAR.runCommandFromNewPopupMenu(MenuCommands.New.TEXT_FILE);
      IDE.EDITOR.waitActiveFile();
      IDE.EDITOR.deleteFileContent();
      IDE.EDITOR.typeTextIntoEditor(restFileContent);
      IDE.EDITOR.saveAs(1, restFileName);
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + folder1Name + "/" + restFileName);

      // Create second folder
      IDE.PROJECT.EXPLORER.selectItem(PROJECT);
      IDE.FOLDER.createFolder(folder2Name);
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + folder2Name);
      // Save in second folder first time
      IDE.EDITOR.saveAs(1, copyofRestFileName);
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + folder2Name + "/" + copyofRestFileName);

      // Save in second folder second time
      IDE.EDITOR.saveAs(1, restFileName);
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + folder2Name + "/" + restFileName);

      IDE.PROJECT.EXPLORER.selectItem(PROJECT);

      IDE.SEARCH.performSearch("/" + PROJECT, "Hello", "text/html");
      IDE.SEARCH_RESULT.waitOpened();
      IDE.SEARCH_RESULT.waitForItem(PROJECT);

      assertEquals(1, IDE.SEARCH_RESULT.getResultCount());
      IDE.SEARCH_RESULT.close();
      IDE.SEARCH_RESULT.waitClosed();

      IDE.SEARCH.performSearch("/" + PROJECT, "Hello", restFileMimeType);
      IDE.SEARCH_RESULT.waitOpened();
      IDE.SEARCH_RESULT.waitForItem(PROJECT);

      assertEquals(4, IDE.SEARCH_RESULT.getResultCount());

      IDE.SEARCH_RESULT.waitItemPresent(PROJECT + "/" + folder1Name + "/" + restFileName);
      IDE.SEARCH_RESULT.waitItemPresent(PROJECT + "/" + folder2Name + "/" + restFileName);
      IDE.SEARCH_RESULT.waitItemPresent(PROJECT + "/" + folder2Name + "/" + copyofRestFileName);
   }

   @AfterClass
   public static void tearDown() throws IOException
   {
      VirtualFileSystemUtils.delete(WS_URL + PROJECT);
   }

}

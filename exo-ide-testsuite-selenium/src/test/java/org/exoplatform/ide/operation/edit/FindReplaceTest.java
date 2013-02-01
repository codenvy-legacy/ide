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

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Map;

import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.ToolbarCommands;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.exoplatform.ide.vfs.shared.Link;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: 2010
 * 
 */
public class FindReplaceTest extends BaseTest
{
   private final static String FILE_CONTENT_FOR_FIND = "html";

   private final static String FILE_CONTENT_FOR_REPLACE = "testtag";

   private static final String FILE_NAME_HTML = "findReplace.html";

   private final static String PROJECT = FindReplaceTest.class.getSimpleName();

   private final static String HTML_FILE_CONTENT = "<html>\n" + "<head>\n" + "<title></title>\n" + "</head>\n"
      + "<body>\n" + "</body>\n" + "</html>";

   @Before
   public void beforeTest()
   {
      try
      {
         Map<String, Link> project = VirtualFileSystemUtils.createDefaultProject(PROJECT);
         Link link = project.get(Link.REL_CREATE_FILE);
         VirtualFileSystemUtils.createFile(link, FILE_NAME_HTML, MimeType.TEXT_HTML, HTML_FILE_CONTENT);
      }
      catch (IOException e)
      {
      }
   }

   @Test
   public void testFindReplace() throws Exception
   {
      IDE.LOADER.waitClosed();
      IDE.PROJECT.EXPLORER.waitOpened();
      IDE.PROJECT.OPEN.openProject(PROJECT);
      IDE.LOADER.waitClosed();
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + FILE_NAME_HTML);

      // Open file
      IDE.PROJECT.EXPLORER.openItem(PROJECT + "/" + FILE_NAME_HTML);
      IDE.EDITOR.waitActiveFile();

      IDE.TOOLBAR.waitForButtonEnabled(ToolbarCommands.Editor.FIND_REPLACE);
      IDE.TOOLBAR.runCommand(ToolbarCommands.Editor.FIND_REPLACE);

      IDE.FINDREPLACE.waitOpened();
      IDE.FINDREPLACE.waitReplaceButtonDisabled();
      IDE.FINDREPLACE.waitReplaceFindButtonDisabled();
      IDE.FINDREPLACE.waitFindButtonDisabled();
      IDE.FINDREPLACE.waitReplaceAllButtonDisabled();
      IDE.TOOLBAR.waitForButtonDisabled(ToolbarCommands.Editor.FIND_REPLACE);
      // Print "html" word in "Find" field and click "Find"
      IDE.FINDREPLACE.typeInFindField(FILE_CONTENT_FOR_FIND);

      IDE.FINDREPLACE.waitFindButtonEnabled();
      IDE.FINDREPLACE.waitReplaceAllButtonEnabled();
      IDE.FINDREPLACE.waitReplaceButtonDisabled();
      IDE.FINDREPLACE.waitReplaceFindButtonDisabled();

      IDE.FINDREPLACE.clickFindButton();
      // Check buttons enabled
      IDE.FINDREPLACE.waitFindButtonEnabled();
      IDE.FINDREPLACE.waitReplaceAllButtonEnabled();
      IDE.FINDREPLACE.waitReplaceButtonEnabled();
      IDE.FINDREPLACE.waitReplaceFindButtonEnabled();
      IDE.FINDREPLACE.waitFindResultEmpty();

      IDE.FINDREPLACE.typeInReplaceField(FILE_CONTENT_FOR_REPLACE);
      IDE.FINDREPLACE.clickReplaceAllButton();
      //check that buttons are disabled after clicking on replace button.

      IDE.FINDREPLACE.waitFindButtonEnabled();
      IDE.FINDREPLACE.waitReplaceAllButtonEnabled();
      IDE.FINDREPLACE.waitReplaceButtonDisabled();
      IDE.FINDREPLACE.waitReplaceFindButtonDisabled();

      // check that all tags was replaced
      IDE.FINDREPLACE.clickFindButton();
      IDE.FINDREPLACE.waitFindResultNotFound();

      // check that file content was changed.
      IDE.EDITOR.selectTab(FILE_NAME_HTML);
      assertTrue(IDE.EDITOR.getTextFromCodeEditor().contains(FILE_CONTENT_FOR_REPLACE));

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

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
package org.exoplatform.ide.operation.upload;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.MenuCommands;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.exoplatform.ide.vfs.shared.Link;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;
import java.util.Map;

/**
 * Test for "Open file by path" dialog.
 * 
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: $
 * 
 */
public class OpenFileByPathTest extends BaseTest
{
   private final static String PROJECT = OpenFileByPathTest.class.getSimpleName();

   private static final String FILE_NAME = OpenFileByPathTest.class.getSimpleName() + ".grs";

   @BeforeClass
   public static void setUp()
   {
      final String filePath = "src/test/resources/org/exoplatform/ide/operation/upload/open-file-by-path.grs";
      try
      {
         Map<String, Link> project = VirtualFileSystemUtils.createDefaultProject(PROJECT);
         Link link = project.get(Link.REL_CREATE_FILE);
         VirtualFileSystemUtils.createFileFromLocal(link, FILE_NAME, MimeType.GROOVY_SERVICE, filePath);
      }
      catch (IOException e)
      {
      }
   }

   @Test
   @Ignore 
   //When issue is ready https://jira.exoplatform.org/browse/IDE-1489
   public void testOpenFileByPath() throws Exception
   {
      IDE.PROJECT.EXPLORER.waitOpened();
      IDE.LOADER.waitClosed();
      IDE.PROJECT.OPEN.openProject(PROJECT);
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + FILE_NAME);

      IDE.PROJECT.EXPLORER.selectItem(PROJECT + "/" + FILE_NAME);

      String fileUrl = IDE.GET_URL.getURL();

      // Call Open File By Path form
      IDE.MENU.runCommand(MenuCommands.File.FILE, MenuCommands.File.OPEN_FILE_BY_PATH);
      IDE.OPEN_FILE_BY_PATH.waitOpened();
      assertFalse(IDE.OPEN_FILE_BY_PATH.isOpenButtonEnabled());
      assertTrue(IDE.OPEN_FILE_BY_PATH.isCancelButtonEnabled());

      // trying to type file path
      IDE.OPEN_FILE_BY_PATH.setFilePath("h");
      assertTrue(IDE.OPEN_FILE_BY_PATH.isOpenButtonEnabled());

      // empty file path field
      IDE.OPEN_FILE_BY_PATH.setFilePath("");
      assertFalse(IDE.OPEN_FILE_BY_PATH.isOpenButtonEnabled());

      // close form by clicking "Cancel" button
      IDE.OPEN_FILE_BY_PATH.clickCancelButton();
      IDE.OPEN_FILE_BY_PATH.waitClosed();

      // trying to open file by wrong path
      IDE.MENU.runCommand(MenuCommands.File.FILE, MenuCommands.File.OPEN_FILE_BY_PATH);
      IDE.OPEN_FILE_BY_PATH.waitOpened();
      IDE.OPEN_FILE_BY_PATH.setFilePath("h");
      IDE.OPEN_FILE_BY_PATH.clickOpenButton();
      IDE.OPEN_FILE_BY_PATH.waitClosed();

      IDE.WARNING_DIALOG.waitOpened();
      IDE.WARNING_DIALOG.clickOk();

      // trying to open file by correct url and using "Open" key
      IDE.MENU.runCommand(MenuCommands.File.FILE, MenuCommands.File.OPEN_FILE_BY_PATH);
      IDE.OPEN_FILE_BY_PATH.waitOpened();
      assertFalse(IDE.OPEN_FILE_BY_PATH.isOpenButtonEnabled());
      IDE.OPEN_FILE_BY_PATH.setFilePath(fileUrl);
      assertTrue(IDE.OPEN_FILE_BY_PATH.isOpenButtonEnabled());
      IDE.OPEN_FILE_BY_PATH.clickOpenButton();
      IDE.OPEN_FILE_BY_PATH.waitClosed();

      IDE.EDITOR.waitActiveFile(PROJECT + "/" + FILE_NAME);

      IDE.EDITOR.closeFile(FILE_NAME);
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
}

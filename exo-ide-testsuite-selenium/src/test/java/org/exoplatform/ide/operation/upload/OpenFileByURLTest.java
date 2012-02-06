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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.MenuCommands;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.exoplatform.ide.vfs.shared.Link;
import org.junit.AfterClass;
import org.junit.BeforeClass;
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
public class OpenFileByURLTest extends BaseTest
{
   private static final String FILE_NAME = OpenFileByURLTest.class.getSimpleName() + ".grs";

   private String fileUrl;

   private final static String PROJECT = OpenFileByURLTest.class.getSimpleName();

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
   public void testOpenFileByURL() throws Exception
   {
      IDE.PROJECT.EXPLORER.waitOpened();
      IDE.LOADER.waitClosed();
      IDE.PROJECT.OPEN.openProject(PROJECT);
      IDE.LOADER.waitClosed();
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + FILE_NAME);

      IDE.PROJECT.EXPLORER.selectItem(PROJECT + "/" + FILE_NAME);
      fileUrl = IDE.GET_URL.getURL();

      IDE.MENU.runCommand(MenuCommands.File.FILE, MenuCommands.File.OPEN_FILE_BY_URL);
      IDE.OPEN_FILE_BY_URL.waitOpened();

      assertFalse(IDE.OPEN_FILE_BY_URL.isOpenButtonEnabled());
      assertTrue(IDE.OPEN_FILE_BY_URL.isCancelButtonEnabled());

      // trying to type file path
      IDE.OPEN_FILE_BY_URL.setURL("h");
      assertFalse(IDE.OPEN_FILE_BY_URL.isOpenButtonEnabled());

      IDE.OPEN_FILE_BY_URL.setURL("http://");
      assertTrue(IDE.OPEN_FILE_BY_URL.isOpenButtonEnabled());

      // empty file path field
      IDE.OPEN_FILE_BY_URL.setURL("");
      assertFalse(IDE.OPEN_FILE_BY_URL.isOpenButtonEnabled());

      // close form by clicking "Cancel" button
      IDE.OPEN_FILE_BY_URL.clickCancelButton();
      IDE.OPEN_FILE_BY_URL.waitClosed();

      // trying to open file by wrong url
      IDE.MENU.runCommand(MenuCommands.File.FILE, MenuCommands.File.OPEN_FILE_BY_URL);
      IDE.OPEN_FILE_BY_URL.setURL("http://123");
      IDE.OPEN_FILE_BY_URL.clickOpenButton();

      IDE.WARNING_DIALOG.waitOpened();
      assertEquals("File 123 could not be opened.", IDE.WARNING_DIALOG.getWarningMessage());
      IDE.WARNING_DIALOG.clickOk();
      IDE.WARNING_DIALOG.waitClosed();

      assertTrue(IDE.OPEN_FILE_BY_URL.isOpened());

      IDE.OPEN_FILE_BY_URL.setURL(fileUrl);
      IDE.OPEN_FILE_BY_URL.clickOpenButton();
      IDE.OPEN_FILE_BY_URL.waitClosed();

      IDE.EDITOR.waitTabPresent(1);
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

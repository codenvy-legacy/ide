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

import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author <a href="mailto:musienko.maxim@gmail.com">Musienko Maxim</a>
 * @author <a href="mailto:oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: Dec 1, 2010 $
 *
 */

public class RenameClosedFileTest extends BaseTest
{

   private static final String PROJECT = RenameClosedFileTest.class.getSimpleName();

   private static final String ORIG_FILE_NAME = "fileforrename.txt";

   private static final String RENAMED_FILE_NAME = "RenamedTestFile.groovy";

   private static final String FILE_CONTENT = "file for rename";

   private static final String PATH = "src/test/resources/org/exoplatform/ide/operation/file/" + ORIG_FILE_NAME;

   @Before
   public void setUp()
   {
      try
      {
         VirtualFileSystemUtils.createDefaultProject(PROJECT);
         VirtualFileSystemUtils.put(PATH, MimeType.TEXT_PLAIN, WS_URL + PROJECT + "/" + ORIG_FILE_NAME);
      }
      catch (Exception e)
      {
      }
   }

   @After
   public void tearDown()
   {
      try
      {
         VirtualFileSystemUtils.delete(WS_URL + PROJECT);
      }
      catch (Exception e)
      {
      }
   }

   //IDE-121 Rename Closed File
   @Test
   public void testRenameClosedFile() throws Exception
   {
      IDE.PROJECT.EXPLORER.waitOpened();
      IDE.PROJECT.OPEN.openProject(PROJECT);

      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + ORIG_FILE_NAME);
      IDE.PROJECT.EXPLORER.selectItem(PROJECT + "/" + ORIG_FILE_NAME);

      IDE.RENAME.rename(RENAMED_FILE_NAME);

      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + RENAMED_FILE_NAME);
      Assert.assertTrue(IDE.PROJECT.EXPLORER.isItemPresent(PROJECT + "/" + RENAMED_FILE_NAME));
      Assert.assertFalse(IDE.PROJECT.EXPLORER.isItemPresent(PROJECT + "/" + ORIG_FILE_NAME));

      assertEquals(404, VirtualFileSystemUtils.get(WS_URL + PROJECT + "/" + ORIG_FILE_NAME).getStatusCode());
      assertEquals(200, VirtualFileSystemUtils.get(WS_URL + PROJECT + "/" + RENAMED_FILE_NAME).getStatusCode());
   }

   @Test
   public void testChangeMimeType() throws Exception
   {
      selenium().refresh();
      IDE.PROJECT.EXPLORER.waitOpened();
      IDE.PROJECT.OPEN.openProject(PROJECT);

      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + ORIG_FILE_NAME);
      IDE.PROJECT.EXPLORER.selectItem(PROJECT + "/" + ORIG_FILE_NAME);

      IDE.RENAME.rename(null, MimeType.TEXT_XML);

      IDE.PROJECT.EXPLORER.openItem(PROJECT + "/" + ORIG_FILE_NAME);
      IDE.EDITOR.waitActiveFile(PROJECT + "/" + ORIG_FILE_NAME);
      String textFromEditor = IDE.EDITOR.getTextFromCodeEditor(0);
      assertEquals(FILE_CONTENT, textFromEditor);

      IDE.PROPERTIES.openProperties();
      assertEquals(MimeType.TEXT_XML, IDE.PROPERTIES.getContentType());
      IDE.PROPERTIES.closeProperties();
      IDE.EDITOR.closeFile(1);
   }

   @Test
   public void testRenameAndChangeMimeType() throws Exception
   {
      selenium().refresh();
      IDE.PROJECT.EXPLORER.waitOpened();
      IDE.PROJECT.OPEN.openProject(PROJECT);

      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + ORIG_FILE_NAME);
      IDE.PROJECT.EXPLORER.selectItem(PROJECT + "/" + ORIG_FILE_NAME);

      IDE.RENAME.rename(RENAMED_FILE_NAME, MimeType.TEXT_XML);

      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + RENAMED_FILE_NAME);
      Assert.assertTrue(IDE.PROJECT.EXPLORER.isItemPresent(PROJECT + "/" + RENAMED_FILE_NAME));
      Assert.assertFalse(IDE.PROJECT.EXPLORER.isItemPresent(PROJECT + "/" + ORIG_FILE_NAME));

      IDE.PROJECT.EXPLORER.openItem(PROJECT + "/" + RENAMED_FILE_NAME);
      IDE.EDITOR.waitActiveFile(PROJECT + "/" + RENAMED_FILE_NAME);
      String textFromEditor = IDE.EDITOR.getTextFromCodeEditor(0);
      assertEquals(FILE_CONTENT, textFromEditor);

      IDE.PROPERTIES.openProperties();
      assertEquals(MimeType.TEXT_XML, IDE.PROPERTIES.getContentType());
      IDE.PROPERTIES.closeProperties();
      IDE.EDITOR.closeFile(1);
   }
}

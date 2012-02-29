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

import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.MenuCommands;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: Oct 28, 2010 $
 * 
 */
public class UploadMimeTypeAutoCompletionTest extends BaseTest
{
   private static final String PROJECT = UploadMimeTypeAutoCompletionTest.class.getSimpleName();

   private static final String FILE_PATH = "src/test/resources/org/exoplatform/ide/operation/file/upload/Example.html";

   @BeforeClass
   public static void setUp()
   {
      try
      {
         VirtualFileSystemUtils.createDefaultProject(PROJECT);
      }
      catch (IOException e)
      {
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
      }
   }

   @Test
   public void testMimeTypeAutoCompletion() throws Exception
   {
      IDE.PROJECT.EXPLORER.waitOpened();
      IDE.LOADER.waitClosed();
      IDE.PROJECT.OPEN.openProject(PROJECT);
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT);

      IDE.MENU.runCommand(MenuCommands.File.FILE, MenuCommands.File.UPLOAD_FILE);
      IDE.UPLOAD.waitOpened();
      try
      {
         File file = new File(FILE_PATH);
         IDE.UPLOAD.setUploadFilePath(file.getCanonicalPath());
      }
      catch (Exception e)
      {
      }
      IDE.UPLOAD.typeToMimeTypeField("application/");
      assertTrue(IDE.UPLOAD.isMimeTypeContainsProposes("application/javascript", "application/java"));
      assertFalse(IDE.UPLOAD.isMimeTypeContainsProposes("text/javascript"));

      IDE.UPLOAD.typeToMimeTypeField("text/");
      assertTrue(IDE.UPLOAD.isMimeTypeContainsProposes("text/javascript", "text/html", "text/css", "text/plain",
         "text/xml"));
      assertFalse(IDE.UPLOAD.isMimeTypeContainsProposes("application/javascript"));

      String mimeTypeToSelect = "text/html";
      IDE.UPLOAD.selectMimeTypeByName(mimeTypeToSelect);

      assertEquals(mimeTypeToSelect, IDE.UPLOAD.getMimeTypeValue());
   }

}

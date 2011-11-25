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
import static org.junit.Assert.assertTrue;

import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.MenuCommands;
import org.junit.Test;

import java.io.File;

/**
 * Created by The eXo Platform SAS .
 *
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: Oct 28, 2010 $
 *
 */
public class UploadMimeTypeAutoCompletionTest extends BaseTest
{
   private static final String FILE_PATH = "src/test/resources/org/exoplatform/ide/operation/file/upload/Example.html";

   @Test
   public void testMimeTypeAutoCompletion() throws Exception
   {
      IDE.WORKSPACE.waitForRootItem();

      IDE.MENU.runCommand(MenuCommands.File.FILE, MenuCommands.File.UPLOAD_FILE);
      IDE.UPLOAD.waitOpened();
      try
      {
         File file = new File(FILE_PATH);
         IDE.UPLOAD.setUploadFilePath(file.getCanonicalPath());
      }
      catch (Exception e)
      {
         e.printStackTrace();
      }

      IDE.UPLOAD.setMimeType("text/");
      assertTrue(IDE.UPLOAD.isMimeTypeContainsProposes("text/any", "text/html", "text/css", "text/plain", "text/xml"));
      String mimeTypeToSelect = "text/richtext";
      IDE.UPLOAD.selectMimeTypeByName(mimeTypeToSelect);

      assertEquals(mimeTypeToSelect, IDE.UPLOAD.getMimeTypeValue());
   }

}

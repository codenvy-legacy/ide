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
import org.exoplatform.ide.TestConstants;
import org.exoplatform.ide.ToolbarCommands;
import org.junit.Test;

import java.io.File;

/**
 * Test for "Open local file" form.
 * 
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: $
 *
 */
public class OpenLocalFileTest extends BaseTest
{
   private static final String FILE_PATH = "src/test/resources/org/exoplatform/ide/operation/file/upload/test";
   
   @Test
   public void testOpenFileWithoutExtention() throws Exception
   {
      IDE.WORKSPACE.waitForRootItem();
      IDE.TOOLBAR.waitForButtonEnabled(ToolbarCommands.File.REFRESH, true);
      IDE.WORKSPACE.selectItem(WS_URL);

      //call Open Local File form
      IDE.MENU.runCommand(MenuCommands.File.FILE, MenuCommands.File.OPEN_LOCAL_FILE);
      IDE.UPLOAD.waitOpened();
      assertFalse(IDE.UPLOAD.isUploadButtonEnabled());
      
      //select file from local driver without file extention
      try
      {
         File file = new File(FILE_PATH);
         IDE.UPLOAD.setUploadFilePath(file.getCanonicalPath());
      }
      catch (Exception e)
      {
      }
      
      Thread.sleep(TestConstants.SLEEP);
      
      String fileName = FILE_PATH.substring(FILE_PATH.lastIndexOf("/") + 1, FILE_PATH.length());
      assertEquals(fileName, IDE.UPLOAD.getFilePathValue());
      
      assertEquals("", IDE.UPLOAD.getMimeTypeValue());
      assertFalse(IDE.UPLOAD.isUploadButtonEnabled());
      
      IDE.UPLOAD.setMimeType(MimeType.TEXT_HTML);
      Thread.sleep(TestConstants.ANIMATION_PERIOD);
      
      assertTrue(IDE.UPLOAD.isUploadButtonEnabled());
      
      IDE.UPLOAD.clickUploadButton();
      IDE.UPLOAD.waitClosed();
      
      IDE.EDITOR.waitTabPresent(0);

   }
}

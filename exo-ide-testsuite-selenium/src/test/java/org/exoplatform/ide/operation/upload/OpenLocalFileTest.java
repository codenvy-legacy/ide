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

import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.MenuCommands;
import org.exoplatform.ide.TestConstants;
import org.exoplatform.ide.ToolbarCommands;
import org.exoplatform.ide.core.Upload;
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
      waitForRootElement();
      IDE.TOOLBAR.waitForButtonEnabled(ToolbarCommands.File.REFRESH, true, TestConstants.WAIT_PERIOD * 10);
      IDE.WORKSPACE.selectItem(WS_URL);

      //call Open Local File form
      IDE.MENU.runCommand(MenuCommands.File.FILE, MenuCommands.File.OPEN_LOCAL_FILE);
      IDE.UPLOAD.waitUploadViewOpened();
      IDE.UPLOAD.checkButtonState(Upload.UPLOAD_BUTTON_ID, false);
      
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
      IDE.UPLOAD.checkButtonState(Upload.UPLOAD_BUTTON_ID, false);

      IDE.UPLOAD.typeToMimeTypeField(MimeType.TEXT_HTML);
      Thread.sleep(TestConstants.ANIMATION_PERIOD);
      
      IDE.UPLOAD.checkButtonState(Upload.UPLOAD_BUTTON_ID, true);
      
      IDE.UPLOAD.clickUploadButton();

      IDE.UPLOAD.waitUploadViewClosed();
      
      IDE.UPLOAD.checkIsOpened(false);
      IDE.EDITOR.waitTabPresent(0);

   }
}

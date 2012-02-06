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

import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.MenuCommands;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

/**
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: $
 * 
 */
public class UploadingGroovyFileTest extends BaseTest
{
   private static final String PROJECT = UploadingGroovyFileTest.class.getSimpleName();

   private static String GROOVY_NAME = "Example.groovy";

   private static final String FILE_PATH =
      "src/test/resources/org/exoplatform/ide/operation/file/upload/Example.groovy";

   @Before
   public void beforeTest()
   {
      try
      {
         VirtualFileSystemUtils.createDefaultProject(PROJECT);
      }
      catch (IOException e)
      {
      }
   }

   @Test
   public void testUploadingGroovy() throws Exception
   {
      IDE.PROJECT.EXPLORER.waitOpened();
      IDE.LOADER.waitClosed();
      IDE.PROJECT.OPEN.openProject(PROJECT);
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT);
      IDE.LOADER.waitClosed();

      IDE.UPLOAD.open(MenuCommands.File.UPLOAD_FILE, FILE_PATH, MimeType.GROOVY_SERVICE);

      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + GROOVY_NAME);
      IDE.PROJECT.EXPLORER.openItem(PROJECT + "/" + GROOVY_NAME);
      IDE.EDITOR.waitActiveFile(PROJECT + "/" + GROOVY_NAME);

      String text = IDE.EDITOR.getTextFromCodeEditor(0);

      assertTrue(text.length() > 0);

      String fileContent = getFileContent(FILE_PATH);

      assertEquals(fileContent.split("\n").length, text.split("\n").length);

      IDE.PROPERTIES.openProperties();
      assertEquals(MimeType.GROOVY_SERVICE, IDE.PROPERTIES.getContentType());
   }

   // IDE-322 Issue
   @Test
   public void testAllMimeTypesArePresent() throws Exception
   {
      driver.navigate().refresh();
      IDE.PROJECT.EXPLORER.waitOpened();
      IDE.LOADER.waitClosed();
      IDE.PROJECT.OPEN.openProject(PROJECT);
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT);
      IDE.LOADER.waitClosed();
      
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
      assertEquals(FILE_PATH.substring(FILE_PATH.lastIndexOf("/") + 1, FILE_PATH.length()),
         IDE.UPLOAD.getFilePathValue());

      assertTrue(IDE.UPLOAD.isMimeTypeContainsProposes("application/x-groovy",
         "application/x-jaxrs+groovy", "application/x-groovy+html", "application/x-chromattic+groovy"));

      // close form
      IDE.UPLOAD.clickCancelButton();
      IDE.UPLOAD.waitClosed();
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

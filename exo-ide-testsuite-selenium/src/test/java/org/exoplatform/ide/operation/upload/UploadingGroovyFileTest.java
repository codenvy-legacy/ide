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
import org.junit.AfterClass;
import org.junit.BeforeClass;
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
   private static final String FOLDER_NAME = UploadingGroovyFileTest.class.getSimpleName();

   private static String GROOVY_NAME = "Example.groovy";

   private static final String URL = BASE_URL + REST_CONTEXT + "/" + WEBDAV_CONTEXT + "/" + REPO_NAME + "/" + WS_NAME
      + "/" + FOLDER_NAME;

   private static final String FILE_PATH =
      "src/test/resources/org/exoplatform/ide/operation/file/upload/Example.groovy";

   @BeforeClass
   public static void setUp()
   {
      try
      {
         VirtualFileSystemUtils.mkcol(URL);
      }
      catch (IOException e)
      {
         e.printStackTrace();
      }
   }

   @Test
   public void testUploadingGroovy() throws Exception
   {
      IDE.WORKSPACE.waitForRootItem();

      IDE.UPLOAD.open(MenuCommands.File.UPLOAD_FILE, FILE_PATH, MimeType.GROOVY_SERVICE);

      IDE.NAVIGATION.openFileFromNavigationTreeWithCodeEditor(WS_URL + GROOVY_NAME, false);
      IDE.EDITOR.waitTabPresent(0);

      IDE.EDITOR.checkCodeEditorOpened(0);
      String text = IDE.EDITOR.getTextFromCodeEditor(0);

      assertTrue(text.length() > 0);

      String fileContent = getFileContent(FILE_PATH);

      assertEquals(fileContent.split("\n").length, text.split("\n").length);

      IDE.MENU.runCommand(MenuCommands.View.VIEW, MenuCommands.View.SHOW_PROPERTIES);
      IDE.PROPERTIES.waitOpened();
      assertEquals(MimeType.GROOVY_SERVICE, IDE.PROPERTIES.getContentType());
   }

   //IDE-322 Issue
   @Test
   public void testAllMimeTypesArePresent() throws Exception
   {
      refresh();

      //----- 1 --------------
      //open upload form
      IDE.MENU.runCommand(MenuCommands.File.FILE, MenuCommands.File.UPLOAD_FILE);
      IDE.UPLOAD.waitOpened();

      //----- 2 --------------
      //type path to file on local system to upload
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

      //----- 2 --------------
      //click to open mime types list
      //check, all mime types for groovy extention are present
      assertTrue(IDE.UPLOAD.isMimeTypeContainsProposes("script/groovy", "application/x-groovy",
         "application/x-jaxrs+groovy", "application/x-groovy+html", "application/x-chromattic+groovy"));

      //close form
      IDE.UPLOAD.clickCancelButton();
      IDE.UPLOAD.waitClosed();
   }

   @AfterClass
   public static void tearDown()
   {
      try
      {
         VirtualFileSystemUtils.delete(URL);
      }
      catch (IOException e)
      {
         e.printStackTrace();
      }
   }

}

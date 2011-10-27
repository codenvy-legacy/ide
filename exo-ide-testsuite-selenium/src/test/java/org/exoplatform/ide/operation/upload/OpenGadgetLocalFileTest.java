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
import org.exoplatform.ide.TestConstants;
import org.exoplatform.ide.ToolbarCommands;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;

/**
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: $
 *
 */
public class OpenGadgetLocalFileTest extends BaseTest
{

   private static String FOLDER_NAME = OpenGadgetLocalFileTest.class.getSimpleName();

   private static String GADGET_NAME = "gadget.xml";

   private final static String URL = BASE_URL + REST_CONTEXT + "/" + WEBDAV_CONTEXT + "/" + REPO_NAME + "/" + WS_NAME
      + "/" + FOLDER_NAME + "/";

   private static final String FILE_PATH = "src/test/resources/org/exoplatform/ide/operation/file/upload/gadget.xml";

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
   public void testOpenGadget() throws Exception
   {

      IDE.WORKSPACE.waitForRootItem();
      IDE.TOOLBAR.runCommand(ToolbarCommands.File.REFRESH);
      IDE.WORKSPACE.waitForItem(URL);

      IDE.WORKSPACE.selectItem(URL);

      IDE.UPLOAD.open(MenuCommands.File.OPEN_LOCAL_FILE, FILE_PATH, MimeType.GOOGLE_GADGET);

      IDE.EDITOR.waitTabPresent(0);

      IDE.EDITOR.checkCodeEditorOpened(0);

      String text = IDE.EDITOR.getTextFromCodeEditor(0);

      assertTrue(text.length() > 0);

      String fileContent = getFileContent(FILE_PATH);

      assertEquals(fileContent.split("\n").length, text.split("\n").length);

      saveAsByTopMenu(GADGET_NAME);
      Thread.sleep(TestConstants.SLEEP);

      IDE.PROPERTIES.openProperties();

      assertEquals("exo:googleGadget", IDE.PROPERTIES.getContentNodeType());
      assertEquals(MimeType.GOOGLE_GADGET, IDE.PROPERTIES.getContentType());
      IDE.EDITOR.closeFile(0);
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

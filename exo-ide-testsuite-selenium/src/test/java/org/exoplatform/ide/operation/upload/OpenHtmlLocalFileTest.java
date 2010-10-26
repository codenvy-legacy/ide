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

import java.io.IOException;

import org.exoplatform.common.http.client.ModuleException;
import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.MenuCommands;
import org.exoplatform.ide.TestConstants;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.junit.AfterClass;
import org.junit.Test;

/**
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: $
 *
 */
public class OpenHtmlLocalFileTest extends BaseTest
{

   private static String HTML_NAME = "file.groovy";
   
   private static String FOLDER_NAME = "Test";
   
   private final static String URL = BASE_URL + REST_CONTEXT + "/" + WEBDAV_CONTEXT + "/" + REPO_NAME + "/" + WS_NAME + "/" + FOLDER_NAME;
   
   @Test
   public void testOpenHtml() throws Exception
   {
      Thread.sleep(TestConstants.SLEEP);
      createFolder(FOLDER_NAME);

      String filePath = "src/test/resources/org/exoplatform/ide/operation/file/upload/Example.html";
      uploadFile(MenuCommands.File.OPEN_LOCAL_FILE, filePath, MimeType.TEXT_HTML);
      Thread.sleep(TestConstants.SLEEP);

      checkCodeEditorOpened(0);

      String text = selenium.getText("//body[@class='editbox']");

      assertTrue(text.length() > 0);

      String fileContent = getFileContent(filePath);

      assertEquals(fileContent.split("\n").length, text.split("\n").length);

      saveAsByTopMenu(HTML_NAME);

      Thread.sleep(TestConstants.SLEEP);

      runTopMenuCommand(MenuCommands.View.VIEW, MenuCommands.View.SHOW_PROPERTIES);

      assertEquals(
         "nt:resource",
         selenium
            .getText("scLocator=//DynamicForm[ID=\"ideDynamicPropertiesForm\"]/item[name=idePropertiesTextContentNodeType||title=%3Cb%3EContent%20Node%20Type%3C%24fs%24b%3E||index=2||Class=StaticTextItem]/textbox"));
      assertEquals(
         MimeType.TEXT_HTML,
         selenium
            .getText("scLocator=//DynamicForm[ID=\"ideDynamicPropertiesForm\"]/item[name=idePropertiesTextContentType||title=%3Cb%3EContent%20Type%3C%24fs%24b%3E||index=3||Class=StaticTextItem]/textbox"));

      selectItemInWorkspaceTree(FOLDER_NAME);
      deleteSelectedItems();

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
      catch (ModuleException e)
      {
         e.printStackTrace();
      }
   }
   
}

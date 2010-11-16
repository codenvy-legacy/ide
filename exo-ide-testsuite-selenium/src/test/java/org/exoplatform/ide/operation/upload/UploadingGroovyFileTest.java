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

import org.exoplatform.common.http.client.ModuleException;
import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.MenuCommands;
import org.exoplatform.ide.TestConstants;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.junit.AfterClass;
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
   private static String GROOVY_NAME = "Приклад.groovy";
   
   private static String FOLDER = UploadingGroovyFileTest.class.getSimpleName();
   
   private final static String URL = BASE_URL + REST_CONTEXT + "/" + WEBDAV_CONTEXT + "/" + REPO_NAME + "/" + WS_NAME + "/" + GROOVY_NAME;

   @Test
   public void testUploadingGroovy() throws Exception
   {
      Thread.sleep(TestConstants.SLEEP);
      String filePath = "src/test/resources/org/exoplatform/ide/operation/file/upload/Приклад.groovy";
      
      
      //TODO*********change****change add folder for locked file
      createFolder(FOLDER);
      //******************
      
      uploadFile(MenuCommands.File.UPLOAD, filePath, MimeType.GROOVY_SERVICE);
      Thread.sleep(TestConstants.SLEEP);

      openFileFromNavigationTreeWithCodeEditor(GROOVY_NAME, false);
      Thread.sleep(TestConstants.SLEEP);
      
      checkCodeEditorOpened(0);
      String text = selenium.getText("//body[@class='editbox']");

      assertTrue(text.length() > 0);

      String fileContent = getFileContent(filePath);

      assertEquals(fileContent.split("\n").length, text.split("\n").length);

      runTopMenuCommand(MenuCommands.View.VIEW, MenuCommands.View.SHOW_PROPERTIES);

      assertEquals(
         "exo:groovyResourceContainer",
         selenium
            .getText("scLocator=//DynamicForm[ID=\"ideDynamicPropertiesForm\"]/item[name=idePropertiesTextContentNodeType||title=%3Cb%3EContent%20Node%20Type%3C%24fs%24b%3E||index=2||Class=StaticTextItem]/textbox"));
      assertEquals(
         MimeType.GROOVY_SERVICE,
         selenium
            .getText("scLocator=//DynamicForm[ID=\"ideDynamicPropertiesForm\"]/item[name=idePropertiesTextContentType||title=%3Cb%3EContent%20Type%3C%24fs%24b%3E||index=3||Class=StaticTextItem]/textbox"));

      selectItemInWorkspaceTree(GROOVY_NAME);
      deleteSelectedItems();
   }
   
   //IDE-322 Issue
   @Test
   public void testAllMimeTypesArePresent() throws Exception
   {
      Thread.sleep(TestConstants.SLEEP);
      String filePath = "src/test/resources/org/exoplatform/ide/operation/file/upload/Приклад.groovy";
      
      String formName = MenuCommands.File.UPLOAD;
      
      //----- 1 --------------
      //open upload form
      runTopMenuCommand(MenuCommands.File.FILE, formName);
      Thread.sleep(TestConstants.REDRAW_PERIOD);

      assertTrue(selenium.isElementPresent("scLocator=//Window[ID=\"ideUploadForm\"]/body/"));
      assertTrue(selenium.isElementPresent("//div[@class='stretchImgButtonDisabled' and @eventproxy='ideUploadFormUploadButton']"));

      //----- 2 --------------
      //type path to file on local system to upload
      try
      {
         File file = new File(filePath);
         selenium.type("//input[@type='file']", file.getCanonicalPath());
      }
      catch (Exception e)
      {
      }
      Thread.sleep(TestConstants.SLEEP);

      assertEquals(filePath.substring(filePath.lastIndexOf("/") + 1, filePath.length()),
         selenium.getValue(
            "scLocator=//DynamicForm[ID=\"ideUploadFormDynamicForm\"]/item[name=ideUploadFormFilenameField]/element"));
      
      //----- 2 --------------
      //click to open mime types list
      selenium.click("scLocator=//Window[ID=\"ideUploadForm\"]/item[0][Class=\"DynamicForm\"]"
         + "/item[name=ideUploadFormMimeTypeField]/[icon='picker']");
      Thread.sleep(TestConstants.REDRAW_PERIOD);
      //check, all mime types for groovy extention are present
      assertTrue(selenium.isElementPresent("//nobr[text()='script/groovy']"));
      assertTrue(selenium.isElementPresent("//nobr[text()='application/x-groovy']"));
      assertTrue(selenium.isElementPresent("//nobr[text()='application/x-jaxrs+groovy']"));
      assertTrue(selenium.isElementPresent("//nobr[text()='application/x-groovy+html']"));
      assertTrue(selenium.isElementPresent("//nobr[text()='application/x-chromattic+groovy']"));
      
      //close form
      selenium.click("scLocator=//Window[ID=\"ideUploadForm\"]/closeButton/");
      Thread.sleep(TestConstants.REDRAW_PERIOD);

      assertFalse(selenium.isElementPresent("scLocator=//Window[ID=\"ideUploadForm\"]/"));
      Thread.sleep(TestConstants.SLEEP);
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
